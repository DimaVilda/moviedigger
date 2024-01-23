package com.backbase.moviesdigger.mappers;

import com.backbase.moviesdigger.client.spec.model.TopRatedMovieResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper to map movie from entity object to openapi top-rated movies response body list
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TopRatedMovieResponseBodyItemMapper {

    List<TopRatedMovieResponseBodyItem> toTopRatedMovieResponseBodyItemList(
            List<Movie> movieList);

    @Mapping(target = "boxOffice", source = "officeBoxValue")
    @Mapping(target = "rating", source = "avgRating")
    @Mapping(target = "year", source = "releaseYear")
    TopRatedMovieResponseBodyItem toTopRatedMovieResponseBodyItemModel(
            Movie movie);
}
