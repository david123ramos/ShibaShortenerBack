package com.shibashortener;

import com.shibashortener.rest.ShibaShortenerRestController;
import com.shibashortener.service.KeyGenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class ShibashortnnerApplicationTests {

    private final static int URL_LENGTH = 7;

    @Autowired
    private ShibaShortenerRestController shibaShortenerRestController;

    @Autowired
    private KeyGenService keyGenService;


    @LocalServerPort
    int port;

    @Test
    void contextLoads() {
        assertThat(shibaShortenerRestController).isNotNull();
    }

//    @Test
//    void itWorks(){
//        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
//                String.class)).contains("Hello, World");
//    }


    @Test
    void testSaltLength() {
        String salt = keyGenService.genSalt();
        System.out.println(salt);
        assertThat(salt).hasSize(URL_LENGTH);
    }

    @Test
    void testShotenedUrlLength() throws NoSuchAlgorithmException {
        String result = keyGenService.encode("https://mylooooongurl.com/test/api/among/us");
        System.out.println(result);
        assertThat(result).hasSize(URL_LENGTH);
    }

}
