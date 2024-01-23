package com.backbase.moviesdigger.mappers;

import com.backbase.moviesdigger.client.spec.model.MovieWinnerResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper to map movie from entity object to openapi movie winners response body list
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieWinnerResponseBodyItemMapper {

    List<MovieWinnerResponseBodyItem> toMovieWinnerResponseBodyItemList(
            List<Movie> movieList);

    @Mapping(target = "year", source = "releaseYear")
    MovieWinnerResponseBodyItem toMovieWinnerResponseBodyItemModel(
            Movie movie);
}
