package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.MovieWinnerResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.TopRatedMovieResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.mappers.MovieResponseBodyItemMapper;
import com.backbase.moviesdigger.mappers.MovieWinnerResponseBodyItemMapper;
import com.backbase.moviesdigger.mappers.TopRatedMovieResponseBodyItemMapper;
import com.backbase.moviesdigger.service.MovieProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final MoviePersistenceService moviePersistenceService;
    private final MovieProviderService movieProviderService;

    private final MovieResponseBodyItemMapper movieResponseBodyItemMapper;
    private final TopRatedMovieResponseBodyItemMapper topRatedMovieResponseBodyItemMapper;
    private final MovieWinnerResponseBodyItemMapper movieWinnerResponseBodyItemMapper;

    public List<MovieWinnerResponseBodyItem> getWinner(String movieName) {
        List<Movie> moviesListFromDb = moviePersistenceService.getMoviesByName(movieName);
        if (!moviesListFromDb.isEmpty()) {
            return movieWinnerResponseBodyItemMapper.toMovieWinnerResponseBodyItemList(
                    moviesListFromDb.stream()
                            .filter(movie -> movie.getIsWinner() == 1)
                            .toList()
            );
        } else {
            Movie movieFromOMDB = findMovieInOMDBbyName(movieName);
            return movieFromOMDB.getIsWinner() == 1 ? Collections.singletonList(
                    movieWinnerResponseBodyItemMapper.toMovieWinnerResponseBodyItemModel(movieFromOMDB)
            ) : Collections.emptyList();
        }
    }

    public List<MovieResponseBodyItem> getMovies(String movieName) {
        List<Movie> moviesListFromDb = moviePersistenceService.getMoviesByName(movieName);
        if (moviesListFromDb.isEmpty()) {
            return Collections.singletonList(
                    movieResponseBodyItemMapper.toMovieResponseBodyItemModel(findMovieInOMDBbyName(movieName))
            );
        }
        List<Movie> updatedMovies = updateMoviesWithBoxOfficeValue(moviesListFromDb);
        return movieResponseBodyItemMapper.toMovieResponseBodyItemList(
                updatedMovies.isEmpty() ? moviesListFromDb : updatedMovies
        );
    }

    public List<TopRatedMovieResponseBodyItem> getTopRatedMovies(Integer page,
                                                                 Integer pageSize,
                                                                 Sort.Direction sortDirection) {
        List<Movie> paginatedMovies = updateMoviesWithBoxOfficeValue(
                moviePersistenceService.getTopRatedMoviesByUserRating(page - 1, pageSize)
        ).stream()
                .sorted(
                        sortDirection.isDescending()
                                ? Comparator.comparing(Movie::getOfficeBoxValue, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                                : Comparator.comparing(Movie::getOfficeBoxValue, Comparator.nullsLast(BigDecimal::compareTo))
                )
                .toList();

        return topRatedMovieResponseBodyItemMapper.toTopRatedMovieResponseBodyItemList(paginatedMovies);
    }

    private Movie findMovieInOMDBbyName(String movieName) { // here the OMDB getMovie contract gives only one movie in response
        log.debug("Movie {} was not found in a local store so let's try to get it from OMDB service", movieName);

        Movie movieFromOMDB = movieProviderService.getMovieByTitle(movieName);
        return moviePersistenceService.saveMovie(movieFromOMDB);
    }

    private List<Movie> updateMoviesWithBoxOfficeValue(List<Movie> moviesListFromDb) {
        log.debug("Some of movies from local store might " +
                "not have boxOffice value, let's check it and update them from OMDB service");

        List<Movie> updatedMovies = moviesListFromDb.stream()
                .filter(movie -> Objects.isNull(movie.getOfficeBoxValue()))
                .peek(movie -> {
                    BigDecimal boxOfficeValue = movieProviderService.getMovieFieldByTitle(
                            movie.getName(),
                            "BoxOffice",
                            BigDecimal.class);
                    if (boxOfficeValue != null) {
                        movie.setOfficeBoxValue(boxOfficeValue);
                    }
                })
                .toList();

        if (!updatedMovies.isEmpty()) {
            moviePersistenceService.saveAllMovies(updatedMovies);
        }

        return moviesListFromDb;
    }
}
