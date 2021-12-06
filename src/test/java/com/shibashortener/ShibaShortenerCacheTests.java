package com.shibashortener;

import com.shibashortener.models.ShibUrl;
import com.shibashortener.services.CacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ShibaShortenerCacheTests {

    private static final int EXPIRATION_TIME_LIMIT = 6; //Months

    @Autowired
    private CacheService cacheService;

    @Test
    void testKeyIsNotInCache(){
        assertThat(cacheService.verifyKeyInCache("AMONGIASD")).isFalse();
    }

    @Test
    void  testKeyIsInCache(){
        String key = "amsQ1Ld";
        String shortened = "shib.sr/"+key;
        String longUrl = "https://mylooooooooonnnggggwebsite.com/Final/TestCase.html";
        String expiration = LocalDate.now() .plusMonths(EXPIRATION_TIME_LIMIT).toString();

        ShibUrl s = new ShibUrl(key, shortened, longUrl, expiration);
        cacheService.putUrlInCache(key, s);
        assertThat(cacheService.getShibUrlFromCache(key)).isNotNull();
    }
}
