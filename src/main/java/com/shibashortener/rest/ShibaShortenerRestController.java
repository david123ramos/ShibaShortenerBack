package com.shibashortener.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ShibaShortenerRestController {

    @GetMapping(path = "/itworks")
    public String itWorks(){
        return "It works!";
    }




}
