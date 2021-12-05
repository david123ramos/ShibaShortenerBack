package com.shibashortener.repositories;

import com.shibashortener.models.Stats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatsUrlDbRepository extends MongoRepository<Stats, String> {
}
