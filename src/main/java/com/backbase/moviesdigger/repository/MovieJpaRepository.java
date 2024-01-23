package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieJpaRepository extends JpaRepository<Movie, String> {

    @Query("select " +
            "case when count(m) > 0 then true else false end " +
            "from movie m")
    boolean isMovieTableEmpty();

    @Query("select m from movie m " +
            "where lower(m.name) like lower(concat('%', :movieName, '%') ) ")
    List<Movie> findMoviesByName(@Param("movieName") String movieName);

    @Query("select m from movie m " +
            "join m.ratingList r " +
            "where r.id = :ratingId")
    Optional<Movie> findByRatingId(@Param("ratingId") String ratingId);
}
