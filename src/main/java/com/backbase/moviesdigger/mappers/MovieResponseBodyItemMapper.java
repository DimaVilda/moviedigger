package com.backbase.moviesdigger.mappers;

import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper to map movie from entity object to openapi movie response body item
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieResponseBodyItemMapper {

    List<MovieResponseBodyItem> toMovieResponseBodyItemList(
            List<Movie> movieList);

    @Mapping(target = "boxOffice", source = "officeBoxValue")
    @Mapping(target = "rating", source = "avgRating")
    @Mapping(target = "year", source = "releaseYear")
    MovieResponseBodyItem toMovieResponseBodyItemModel(
            Movie movie);
}
