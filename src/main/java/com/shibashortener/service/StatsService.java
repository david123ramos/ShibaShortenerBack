package com.shibashortener.service;


import com.shibashortener.models.ShibUrl;
import com.shibashortener.models.embedded.DailyStats;
import com.shibashortener.models.embedded.Stats;
import com.shibashortener.repositories.ShibUrlDbRepository;
import com.shibashortener.repositories.StatsUrlDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatsService {

    @Autowired
    private StatsUrlDbRepository statsUrlDbRepository;

    public String create(Stats stats) {
        return statsUrlDbRepository.save(stats).getId();
    }

    public Stats read(String shibUrlId) { return statsUrlDbRepository.findById(shibUrlId).orElse(null); }

    public void delete(String shibUrlId){
        statsUrlDbRepository.deleteById(shibUrlId);
    }


    public void addClick(Stats stats, String date){


        if(stats != null) {

            DailyStats dailyStats = stats.getDailyStats()
                    .stream()
                    .filter(e -> e.getDate().equals(date))
                    .findFirst().orElse(null);

            if(dailyStats != null) {
                dailyStats.incrementVisits();
            }else{
                dailyStats = new DailyStats(date);
                stats.addDailyReport(dailyStats);
            }

            statsUrlDbRepository.save(stats);

        }

    }





}
