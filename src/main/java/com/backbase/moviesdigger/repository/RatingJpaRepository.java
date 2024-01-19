package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingJpaRepository extends JpaRepository<Rating, String> {
}
