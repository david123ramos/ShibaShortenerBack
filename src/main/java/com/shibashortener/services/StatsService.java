package com.shibashortener.services;


import com.shibashortener.models.embedded.DailyStats;
import com.shibashortener.models.Stats;
import com.shibashortener.models.embedded.Visitor;
import com.shibashortener.repositories.StatsUrlDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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


    public void addClick(Stats stats, String date, Visitor newVisitor){


        if(stats != null) {

            DateTimeFormatter f =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateWithNoTime = LocalDateTime.parse(date).format(f);

            DailyStats dailyStats = stats.getDailyStats()
                    .stream()
                    .filter(e -> LocalDateTime.parse(e.getDate()).format(f).equals(dateWithNoTime))
                    .findFirst().orElse(null);

            if(dailyStats != null) {
                dailyStats.incrementVisits();
            }else{
                dailyStats = new DailyStats(date);
                stats.addDailyReport(dailyStats);
            }
            dailyStats.addVisitor(newVisitor);

            statsUrlDbRepository.save(stats);
        }

    }





}
