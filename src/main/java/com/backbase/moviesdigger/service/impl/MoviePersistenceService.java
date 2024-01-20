package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviePersistenceService {

    private final MovieJpaRepository movieJpaRepository;

    public List<Movie> getMoviesByName(String movieName) {
       return movieJpaRepository.findMoviesByNameContaining(movieName);
    }
}
