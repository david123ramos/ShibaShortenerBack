package com.shibashortener.services;


import com.shibashortener.models.ShibUrl;
import com.shibashortener.models.Stats;
import com.shibashortener.models.embedded.Insight;
import com.shibashortener.models.embedded.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

   @Autowired
   private ShibUrlDbService shibUrlDbService;

   @Autowired
   private CacheService cacheService;

   @Autowired
   private StatsService statsService;


    /**
     *
     * @param request
     * @param url ShibUrl from cache, database or new instance
     * @param newUrl flag used to avoid unecessary query in database
     */
   @Async
    public void analize(HttpServletRequest request, String userAgent,  ShibUrl url, boolean newUrl) {
        String userBrowser = getBrowser(userAgent);
        String userOs = getOs(userAgent);
        String date = LocalDateTime.now().toString();
        String ip = request.getRemoteAddr();

        Stats stats = null;

        if(!newUrl) {
            stats = statsService.read(url.getId());
        }

        if(stats != null) {
            Visitor newVisitor = new Visitor(ip, userBrowser, userOs, date);
            statsService.addClick(stats, date, newVisitor);
        }else{
            Stats newStats = new Stats(url.getId(), userBrowser, userOs, date);
            newStats.initCounter(date);

            //Todo: Add kafka messaging

            statsService.create(newStats);
        }

    }

    public Insight getInsight(String shibUrl) {

       Stats shibUrlStats = statsService.read(shibUrl);

       if(shibUrlStats != null) {

           int existingDays = Period.between(LocalDateTime.now().toLocalDate(), LocalDateTime.parse(shibUrlStats.getCreatedAt()).toLocalDate()).getDays();
           int totalClicks = getTotalClicksFromStats(shibUrlStats);
           double clicksPerDay = getClicksPerDayFromStas(shibUrlStats);
           List<Visitor> visitors = getJoinnedListOfVisitors(shibUrlStats);
           Map<String, Integer> visitorsByOs = groupVisitorsByOs(visitors);

           long dayVisitors = getDayVisitors(visitors);

           long nightVisitors = getNightVisitors(visitors);

           String visitorsDayPeriod = dayVisitors > nightVisitors ? "Day" : "Night";

           Insight insight = new Insight(
                   shibUrl,
                   shibUrlStats.getCreatedAt(),
                   existingDays,
                   totalClicks,
                   clicksPerDay,
                   visitorsByOs,
                   visitorsDayPeriod
                   );

           return  insight;

       }

       return null;
    }

    private int getTotalClicksFromStats(Stats s) {
        return s.getDailyStats()
                .stream()
                .map(dailyStats -> dailyStats.getVisits())
                .mapToInt(i -> i.intValue())
                .sum();
    }

    private double getClicksPerDayFromStas(Stats s) {
       return s.getDailyStats()
                .stream()
                .map(dailyStats -> dailyStats.getVisits())
                .mapToInt(i -> i.intValue())
                .average()
                .getAsDouble();
    }

    private List<Visitor> getJoinnedListOfVisitors(Stats s){
       return s.getDailyStats()
               .stream()
               .map(el -> el.getVisitors())
               .flatMap(List::stream) //join all visitors from dailyStats
               .collect(Collectors.toList());
    }

    private  Map<String, Integer> groupVisitorsByOs(List<Visitor> visitors) {
       return visitors.stream()
               .collect(Collectors.groupingBy(v -> v.getVisitorOs())).entrySet() //grouping all visitor by OS
               .stream()
               .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().size()));
    }

    private long getDayVisitors(List<Visitor> visitors){
        LocalTime initMorning = LocalTime.parse("05:00");
        LocalTime finalAfternoon = LocalTime.parse("17:59");
       return  visitors
               .stream()
               .map(e -> LocalDateTime.parse(e.getDatetime()).toLocalTime() )
               .filter(d -> d.isAfter(initMorning) && d.isBefore(finalAfternoon))
               .count() ;
    }

    private long getNightVisitors(List<Visitor> visitors) {
        LocalTime initMorning = LocalTime.parse("05:00");
        LocalTime finalAfternoon = LocalTime.parse("17:59");
        return visitors
                .stream()
                .map(e -> LocalDateTime.parse(e.getDatetime()).toLocalTime())
                .filter(d -> d.isBefore(initMorning) && d.isAfter(finalAfternoon))
                .count() ;
    }

    private String getBrowser(String userAgentHeader){

        String  userAgent       =   userAgentHeader;
        String  user            =   userAgent.toLowerCase();
        String browser = "";

        if (user.contains("msie")) {
            String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];

        } else if (user.contains("safari") && user.contains("version")) {
            browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];

        } else if ( user.contains("opr") || user.contains("opera")) {
            if(user.contains("opera"))
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");

        } else if (user.contains("chrome")) {
            browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) )  {
            browser = "Netscape-?";

        } else if (user.contains("firefox")) {
            browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv")) {
            browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else {
            browser = "UnKnown, More-Info: "+userAgent;
        }

        return browser;
    }

    private String getOs(String userAgentHeader) {

        String  userAgent = userAgentHeader;
        String os = "";

        if (userAgent.toLowerCase().indexOf("windows") >= 0 ) {
            os = "Windows";
        } else if(userAgent.toLowerCase().indexOf("mac") >= 0) {
            os = "Mac";
        } else if(userAgent.toLowerCase().indexOf("x11") >= 0) {
            os = "Unix";
        } else if(userAgent.toLowerCase().indexOf("android") >= 0) {
            os = "Android";
        } else if(userAgent.toLowerCase().indexOf("iphone") >= 0) {
            os = "IPhone";
        }else{
            os = "UnKnown, More-Info: "+userAgent;
        }

        return  os;
    }


}
