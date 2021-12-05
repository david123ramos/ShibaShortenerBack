package com.shibashortener.rest;


import com.shibashortener.models.LongUrlBean;
import com.shibashortener.models.ShibUrl;
import com.shibashortener.models.embedded.Insight;
import com.shibashortener.service.AnalyticsService;
import com.shibashortener.service.CacheService;
import com.shibashortener.service.KeyGenService;
import com.shibashortener.service.ShibUrlDbService;
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

        //TODO chain of responsability: shortUrl->sanitization->validation


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
    public String shortening(@RequestBody LongUrlBean longURL,
                             HttpServletRequest request,
                             @RequestHeader("User-Agent") String userAgent,
                             HttpServletResponse response) {

        final String  baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //server url
        String exptime = LocalDate.now().plusMonths(EXPIRATION_TIME_LIMIT).toString();

        //TODO: chain of responsability: shortUrl->sanitization->validation

        String result = generateKey(longURL.getLongUrl());
        ShibUrl shibUrl = new ShibUrl(result, baseURL+"/"+result, longURL.getLongUrl(), exptime);

        analyticsService.analize(request,userAgent ,shibUrl, true);

        boolean hasInsertedInCache = cacheService.putUrlInCache(result, shibUrl);
        String dbId = shibUrlDbService.create(shibUrl);

        if(dbId != null && hasInsertedInCache) {
            return baseURL+"/"+result;
        }

        response.setHeader("Location", baseURL+"/internal-error");
        response.setStatus(HttpServletResponse.SC_OK);

        return null;
    }


    @GetMapping(path = "/analytics/{shortUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Insight> getShortUrlStats(@PathVariable String shortUrl,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response){

        Insight i =  analyticsService.getInsight(shortUrl);

        if(i != null) {
            return ResponseEntity.ok(i);
        }

        return ResponseEntity.notFound().build();
    }



    private void redirectTo(String url, HttpServletResponse response) {
        response.setHeader("Location", "http://"+url);
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
