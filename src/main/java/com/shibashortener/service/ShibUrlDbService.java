package com.shibashortener.service;

import com.shibashortener.repositories.ShibUrlDbRepository;
import com.shibashortener.models.ShibUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShibUrlDbService {

    @Autowired
    private ShibUrlDbRepository shibUrlRepository;

    public String create(ShibUrl shibUrl) {
        return shibUrlRepository.save(shibUrl).getId();
    }

    public ShibUrl read(String shibUrlId) {
        return shibUrlRepository.findById(shibUrlId).orElse(null);
    }

    public void delete(String shibUrlId){
        shibUrlRepository.deleteById(shibUrlId);
    }

    /**
     * find all shiburl that expiration time not reached yet.
     * @return
     */
    public List<ShibUrl> findAll(){
        String now = LocalDate.now().toString();
        return shibUrlRepository.findAllByExpirationTimeGreaterThan(now);
    }




}
