package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingJpaRepository extends JpaRepository<Rating, String> {

    List<Rating> findRatingsByMovieId(String movieId);

    @Query("select case when count(r) > 0 then true else false end from rating r where r.user.name = :userName")
    boolean findRatingByUserName(@Param("userName") String userName);
}
