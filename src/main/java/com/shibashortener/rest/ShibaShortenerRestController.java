package com.shibashortener.rest;


import com.shibashortener.models.LongUrlBean;
import com.shibashortener.models.ShibUrl;
import com.shibashortener.models.embedded.Insight;
import com.shibashortener.services.AnalyticsService;
import com.shibashortener.services.CacheService;
import com.shibashortener.services.KeyGenService;
import com.shibashortener.services.ShibUrlDbService;
import com.shibashortener.utils.URLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@RestController
public class ShibaShortenerRestController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ShibUrlDbService shibUrlDbService;

    @Autowired
    private KeyGenService keyGenService;

    @Autowired
    private AnalyticsService analyticsService;

    private static final int EXPIRATION_TIME_LIMIT = 6; //Months


    @GetMapping(path = "/itworks")
    public String itWorks(@RequestHeader("User-Agent") String user){
        return user;
    }


    @GetMapping(path = "/{shortUrl}")
    public void recieveLongString(@PathVariable String shortUrl,
                                    @RequestHeader("User-Agent") String userAgent,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        final String  baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //server url

        if(!URLUtils.isShibShortenedUrl(shortUrl)){
            response.setHeader("Location", baseURL+"/shib/not-found");
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        }

        //TODO: remove boilerplate
        if(cacheService.verifyKeyInCache(shortUrl)) {

            ShibUrl obj = cacheService.getShibUrlFromCache(shortUrl);

            analyticsService.analize(request, userAgent, obj, false);

            redirectTo(obj.getLongUrl(), response);

        }else {

            ShibUrl urlStoredInDb = shibUrlDbService.read(shortUrl);

            if(urlStoredInDb != null){
                redirectTo(urlStoredInDb.getLongUrl(), response);
                cacheService.putUrlInCache(urlStoredInDb.getId(), urlStoredInDb); //update cache
                analyticsService.analize(request, userAgent, urlStoredInDb, false);

            }else  {

                    response.setHeader("Location", baseURL+"/shib/not-found");
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

            }
        }
    }

    @PostMapping(path = "/shortening")
    @CrossOrigin
    public ResponseEntity<String> shortening(@RequestBody LongUrlBean longURL,
                             HttpServletRequest request,
                             @RequestHeader("User-Agent") String userAgent,
                             HttpServletResponse response) {

        final String  baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //server url
        String exptime = LocalDate.now().plusMonths(EXPIRATION_TIME_LIMIT).toString();
        String sanitizedUrl = null;

        if(URLUtils.isValid(longURL.getLongUrl())){
            sanitizedUrl = URLUtils.sanitizeURL(longURL.getLongUrl());
            sanitizedUrl = URLUtils.checkProtocol(sanitizedUrl);
        }else {
            return ResponseEntity.badRequest().build();
        }

        String result = generateKey(sanitizedUrl);
        ShibUrl shibUrl = new ShibUrl(result, baseURL+"/"+result, sanitizedUrl, exptime);

        analyticsService.analize(request,userAgent ,shibUrl, true);

        boolean hasInsertedInCache = cacheService.putUrlInCache(result, shibUrl);
        String dbId = shibUrlDbService.create(shibUrl);

        if(dbId != null && hasInsertedInCache) {
            return ResponseEntity.ok(baseURL+"/"+result);
        }

        response.setHeader("Location", baseURL+"/internal-error");
        response.setStatus(HttpServletResponse.SC_OK);

        return null;
    }


    @GetMapping(path = "/analytics/{shortUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<Insight> getShortUrlStats(@PathVariable String shortUrl,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response){
        if(!URLUtils.isShibShortenedUrl(shortUrl)){
            return ResponseEntity.badRequest().build();
        }

        Insight i =  analyticsService.getInsight(shortUrl);

        if(i != null) {
            return ResponseEntity.ok(i);
        }

        return ResponseEntity.notFound().build();
    }


    /**
     * Auxiliary method to redirect user for site with long URL.
     * @param url
     * @param response
     */
    private void redirectTo(String url, HttpServletResponse response) {
        response.setHeader("Location", url);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }


    /**
     * This is an auxiliary method to generate a key recursively
     * if one with same value already did generated
     *
     * @param longURL URL provided by user
     * @return
     */
    private String generateKey(String longURL){

        try {
            String key = keyGenService.encode(longURL);

            if(cacheService.verifyKeyInCache(key) || shibUrlDbService.read(key) != null) {
                return  generateKey(longURL);
            }

            return  key;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return  null;
    }

}
