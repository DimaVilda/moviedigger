package com.backbase.moviesdigger.mappers;

import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.domain.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper to map movie from entity object to openapi movie rating response body
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieRatingResponseBodyMapper {

    @Mapping(target = "rating", source = "avgRating")
    MovieRatingResponseBody toMovieResponseBodyItemModel(
            Movie movie);
}
