package com.shibashortener.repositories;

import com.shibashortener.models.ShibUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShibUrlDbRepository extends MongoRepository<ShibUrl, String> {
    List<ShibUrl> findAllByExpirationTimeGreaterThan(String now);
}
