package com.shibashortener;

import com.shibashortener.models.Stats;
import com.shibashortener.models.embedded.Insight;
import com.shibashortener.models.embedded.Visitor;
import com.shibashortener.services.AnalyticsService;
import com.shibashortener.services.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

@SpringBootTest
public class ShibaShotenerStatsServiceTests {
    @Autowired
    private StatsService statsService;

    @Autowired
    private AnalyticsService analyticsService;


    @Test
    void createStatsTest(){

        String urlId = "MAzP02f";
        String browser = "Chrome.1 Test";
        String os = "Android";
        String createdAt = LocalDateTime.now().toString();

        Stats stats = new Stats(urlId, browser, os, createdAt);

        String responseId = statsService.create(stats);
        assertThat(responseId).isNotNull().isEqualTo(urlId);
    }


    @Test
    void readStatsTest(){
        String urlId = "TSlmcv1";
        String browser = "IE.1 Test";
        String os = "IOS";
        String createdAt = LocalDateTime.now().toString();
        Stats stats = new Stats(urlId, browser, os, createdAt);

        String responseId = statsService.create(stats);

        Stats statsFromDb = statsService.read(responseId);

        assertThat(statsFromDb).isNotNull().extracting("id").isEqualTo(responseId);
    }

    @Test
    void updateStatsTest(){

        //TODO: migrate to mockito

        String urlId = "TSl18dm";
        String browser = "Mozilla.1 Test";
        String os = "Windows";
        String createdAt = LocalDateTime.now().toString();
        Stats stats = new Stats(urlId, browser, os, createdAt);

        String responseId = statsService.create(stats);

        Stats statsFromDb = statsService.read(responseId);

        statsFromDb.initCounter(createdAt);

        String visitorIp = " 10.0.0.119";
        String visitorBrowser = "Chrome";
        String visitorOs = "Windows";
        String datetime = createdAt;

        statsService.addClick(stats, createdAt, new Visitor(visitorIp, visitorBrowser, visitorOs, datetime));


        Stats updateStatsFromDb = statsService.read(responseId);

        assertThat(updateStatsFromDb.getDailyStats().size()).isGreaterThan(0);
        assertThat(updateStatsFromDb.getDailyStats().get(0).getVisitors().get(0).getVisitorIp())
                .isNotNull()
                .isEqualTo(visitorIp);
    }


    @Test
    void insightsTest( ){


        String urlId = "TSl18dm";
        String browser = "Mozilla.1 Test";
        String os = "Windows";
        String createdAt = LocalDateTime.now().toString();
        Stats stats = new Stats(urlId, browser, os, createdAt);

        String responseId = statsService.create(stats);

        Stats statsFromDb = statsService.read(responseId);

        statsFromDb.initCounter(createdAt);

        String visitorBrowser = "Chrome";
        String visitorOs = "Windows";
        String datetime = createdAt;

        Visitor[] visitors = new Visitor[]  {
                new Visitor("10.0.0.119", visitorBrowser, visitorOs, datetime),
                new Visitor("10.0.0.117", visitorBrowser, visitorOs, datetime),
                new Visitor("10.0.0.116", "IE", visitorOs, datetime),
                new Visitor("10.0.0.115", visitorBrowser, visitorOs, datetime),
                new Visitor("10.0.0.114", visitorBrowser, "IOS", datetime)
        };

        for(Visitor v: visitors) {
            statsService.addClick(stats, createdAt, v);
        }

        Insight insight = analyticsService.getInsight(urlId);
        Stats updateStatsFromDb = statsService.read(responseId);

        assertThat(insight).isNotNull()
                .extracting("id")
                .as("id should be equal to %s", urlId)
                .isEqualTo(urlId);

        assertThat(insight)
                .extracting("totalClicks")
                .as("totalClicks should be equal to %s", visitors.length -1)
                .isEqualTo(visitors.length -1);

        assertThat(insight)
                .extracting("existingDays")
                .as("existignDays should be equal to %s", updateStatsFromDb.getDailyStats().size())
                .isEqualTo(updateStatsFromDb.getDailyStats().size());
    }




}
