package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieJpaRepository extends JpaRepository<Movie, String> {

    @Query("select case when count(m) > 0 then true else false end from movie m")
    boolean isMovieTableEmpty();
}
