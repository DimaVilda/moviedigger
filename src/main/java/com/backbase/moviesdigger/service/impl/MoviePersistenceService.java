package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviePersistenceService {

    private final MovieJpaRepository movieJpaRepository;

    public List<Movie> getMoviesByName(String movieName) {
        log.debug("Trying to retrieve movies by name {} from local store", movieName);

        return movieJpaRepository.findMoviesByName(movieName);
    }

    public Movie getMovieByRatingId(String ratingId) {
        log.debug("Trying to retrieve movie by rating {} from local store", ratingId);

        return movieJpaRepository.findByRatingId(ratingId)
                .orElseThrow(() -> {
                    log.warn("Movie was not found by provided rating {}", ratingId);

                    return new NotFoundException("Rating " + ratingId + " was not found");
                });
    }

    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieJpaRepository.save(movie);
    }

    @Transactional
    public void saveAllMovies(List<Movie> moviesList) {
        movieJpaRepository.saveAll(moviesList);
    }

    public Movie getMovieById(String movieId) {
        return movieJpaRepository.findById(movieId)
                .orElseThrow(() -> {
                    log.warn("Provided movie {} in request was not found", movieId);

                    return new NotFoundException("Movie " + movieId + " was not found");
                });
    }

    public List<Movie> getTopRatedMoviesByUserRating(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "avgRating");
        return movieJpaRepository.findAll(pageable).getContent();
    }
}
