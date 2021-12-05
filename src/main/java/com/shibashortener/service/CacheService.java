package com.shibashortener.service;

import com.shibashortener.models.ShibUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean verifyKeyInCache(String key){
        return redisTemplate.hasKey(key);
    }

    public boolean putUrlInCache(String key, ShibUrl value) {
        return redisTemplate.opsForHash().putIfAbsent(key, key, value);
    }

    public ShibUrl getShibLongUrl(String key) {
         return (ShibUrl) redisTemplate.opsForHash().get(key, key);
    }

}
