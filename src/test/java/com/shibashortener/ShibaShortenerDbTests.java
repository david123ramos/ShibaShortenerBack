package com.shibashortener;


import com.shibashortener.interfaces.ShibUrlDbRepository;
import com.shibashortener.models.ShibUrl;
import com.shibashortener.service.ShibUrlDbService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class ShibaShortenerDbTests {

    @Autowired
    private ShibUrlDbService shibUrlDbService;

    private static final int EXPIRATION_TIME_LIMIT = 6;

    String key = "zDlK01K";
    String shortened = "shib.sr/"+key;
    String longUrl = "https://mylooooooooonnnggggwebsite.com/Final/TestCase.html";
    String expiration = LocalDate.now() .plusMonths(EXPIRATION_TIME_LIMIT).toString();

    ShibUrl s = new ShibUrl(key, shortened, longUrl, expiration);




    @Test
    void createTest() {
        String id = shibUrlDbService.create(s);
        assertThat(id).isNotNull().isEqualTo(key);
    }

    @Test
    void readTest(){
        s.setId("sAlKmd0");
        String id = shibUrlDbService.create(s);
        ShibUrl response = shibUrlDbService.read(id);
        assertThat(response).isNotNull();
    }

    @Test
    void readAllTest(){
        List<ShibUrl> urls = shibUrlDbService.findAll();
        assertThat(urls).hasSize(2);
    }

}
