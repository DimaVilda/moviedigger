package com.backbase.moviesdigger.config.it;

import com.backbase.moviesdigger.repository.MovieJpaRepository;
import com.backbase.moviesdigger.repository.RatingJpaRepository;
import com.backbase.moviesdigger.repository.UserJpaRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

public abstract class BaseIntegrationTestConfig {

    @Autowired
    protected RatingJpaRepository ratingJpaRepository;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected MovieJpaRepository movieJpaRepository;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .waitingFor(Wait.forListeningPort());

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }
}
