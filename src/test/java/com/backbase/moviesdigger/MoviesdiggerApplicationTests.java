package com.backbase.moviesdigger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MoviesdiggerApplication.class)
@ActiveProfiles("integration")
class MoviesdiggerApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldCreateApplicationContext() {
        Assertions.assertNotNull(applicationContext);
    }
}
