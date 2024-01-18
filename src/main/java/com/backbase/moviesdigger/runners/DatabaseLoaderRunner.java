package com.backbase.moviesdigger.runners;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseLoaderRunner implements CommandLineRunner {

    private final MovieJpaRepository movieRepository;

    private static final String CSV_FILE_PATH = "src/main/resources/presetup/academy_awards.csv";

    @Override
    public void run(String... args) throws Exception {
        log.debug("Movie table initialisation by csv file data");
        if (movieRepository.isMovieTableEmpty()) {
            log.debug("Movie table contains data, cancel initialisation");
            return;
        }
        initMovieTableByCSV();
        log.debug("Movie table initialise successfully");
    }

    private void initMovieTableByCSV() {
        List<Movie> moviesToSave = new ArrayList<>();

        try (Reader in = new FileReader(CSV_FILE_PATH)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreSurroundingSpaces(true)
                    .build();

            try (CSVParser parser = new CSVParser(in, format)) {
                for (CSVRecord record : parser) {
                    if ("Best Picture".equals(record.get("Category"))) {
                        Movie movie = new Movie();
                        movie.setName(record.get("Nominee"));
                        movie.setIsWinner("YES".equals(record.get("Won?")) ? 1 : 0);
                        moviesToSave.add(movie);
                    }
                }
                movieRepository.saveAll(moviesToSave);
            }
        } catch (Exception e) {
            log.warn("Movie table initialization failed, reason is {}", e.getMessage());
        }
    }
}
