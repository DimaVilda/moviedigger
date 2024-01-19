package com.backbase.moviesdigger.service.iml;

import com.backbase.moviesdigger.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviePersistenceService {

    private final MovieJpaRepository movieJpaRepository;
}
