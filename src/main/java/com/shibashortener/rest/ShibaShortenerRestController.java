package com.shibashortener.rest;


import com.shibashortener.models.LongUrlBean;
import com.shibashortener.models.ShibUrl;
import com.shibashortener.service.CacheService;
import com.shibashortener.service.KeyGenService;
import com.shibashortener.service.ShibUrlDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class ShibaShortenerRestController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ShibUrlDbService shibUrlDbService;

    @Autowired
    private KeyGenService keyGenService;

    private static final int EXPIRATION_TIME_LIMIT = 6; //Months
    final String  baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); //server url

    @GetMapping(path = "/itworks")
    public String itWorks(@RequestHeader("User-Agent") String user){
        return user;
    }


    @GetMapping(path = "/recieve/{shortUrl}")
    public void recieveLongString(@PathVariable String shortUrl,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        //TODO chain of responsability: shortUrl->sanitization->validation

        if(cacheService.verifyKeyInCache(shortUrl)) {

            ShibUrl obj = cacheService.getShibUrlFromCache(shortUrl);

            //TODO: run analytics service over object

            redirectTo(obj.getLongUrl(), response);


        }else {

            ShibUrl url = shibUrlDbService.read(shortUrl);

            if(url != null){
                redirectTo(url.getLongUrl(), response);

                cacheService.putUrlInCache(url.getId(), url); //update cache

            }else  {

                    response.setHeader("Location", baseURL+"/not-found");
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

            }

        }
    }

    @PostMapping(path = "/shortening")
    public String shortening(@RequestBody LongUrlBean longURL,
                             HttpServletRequest request,
                             HttpServletResponse response) {


        String exptime = LocalDate.now().plusMonths(EXPIRATION_TIME_LIMIT).toString();


        //TODO: chain of responsability: shortUrl->sanitization->validation

        String result = generateKey(longURL.getLongUrl());
        ShibUrl shibUrl = new ShibUrl(result, baseURL+"/"+result, longURL.getLongUrl(), exptime);
        //TODO: Run analytics

        boolean hasInsertedInCache = cacheService.putUrlInCache(result, shibUrl);
        String dbId = shibUrlDbService.create(shibUrl);

        if(dbId != null && hasInsertedInCache) {
            return baseURL+"/"+result;
        }

        response.setHeader("Location", baseURL+"/internal-error");
        response.setStatus(HttpServletResponse.SC_OK);

        return null;
    }




    private void redirectTo(String url, HttpServletResponse response) {
        response.setHeader("Location", url);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }


    private String generateKey(String longURL){

        try {
            String key = keyGenService.encode(longURL);

            if(!cacheService.verifyKeyInCache(key) || shibUrlDbService.read(key) == null) {
                return key;
            }

            return  generateKey(longURL);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return  null;
    }




}
