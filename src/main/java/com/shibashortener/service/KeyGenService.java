package com.shibashortener.service;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class KeyGenService {

    private static final String encodeSet = "0123456789abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWXYZ"; //base62
    private static final int URL_LENGTH = 7; // 62 ^ 7 possibilities
    private static final Random randomGenerator = new Random();
    private static  MessageDigest cryptoGenerator;


    /**
     * return shorted url given a long link
     * @param {String} originalUrl
     * @return
     */
    public String encode(String originalUrl) throws NoSuchAlgorithmException {

       String salt = genSalt();
       cryptoGenerator = MessageDigest.getInstance("MD5");
       cryptoGenerator.update((originalUrl+salt).getBytes());

       byte[] hashedUrl = cryptoGenerator.digest();
       String generatedHex = DigestUtils.md5DigestAsHex(hashedUrl);
       String result = Base64.getEncoder().encodeToString(generatedHex.getBytes());

        return result.substring(0, URL_LENGTH);
    }


    /**
     * Returns a random salt string used to minimize hash collisions
     * @return String
     */
    public String genSalt(){
        return randomGenerator
                .ints(URL_LENGTH, 0, encodeSet.length() -1)
                .mapToObj(i -> String.valueOf(encodeSet.charAt(i))).collect(Collectors.joining(""));
    }


}
