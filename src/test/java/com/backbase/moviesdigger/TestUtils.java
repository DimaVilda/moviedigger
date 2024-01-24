package com.backbase.moviesdigger;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;

import java.math.BigDecimal;
import java.util.List;

public class TestUtils {

    public static String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJIaDh0N3pndWd4azNuMEJQS3MxLUthWUVaQlpWRGhVTU1YWHJVZ25Wb3VjIn0.eyJleHAiOjE3MDYwMzc2NzQsImlhdCI6MTcwNjAzNzM3NCwianRpIjoiY2RhMThjZWUtMWYyNS00MDNlLTgwYWMtOWE3ZDZmOGEwNWZkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9tb3ZpZXNkaWdnZXIiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiN2MzY2Y4MGEtYjNhZS00NmVhLWIxMGItZmM5MmY2NzA2MjBhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibW92aWVzZGlnZ2VyLWNsaWVudC1pZCIsInNlc3Npb25fc3RhdGUiOiJhZmEyMTE1ZC02NDM1LTQxZGEtYWExYS05Mjg4M2M4Njc1ZDgiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbW92aWVzZGlnZ2VyIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXJSb2xlIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibW92aWVzZGlnZ2VyLWNsaWVudC1pZCI6eyJyb2xlcyI6WyJjbGllbnRfdXNlciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYWZhMjExNWQtNjQzNS00MWRhLWFhMWEtOTI4ODNjODY3NWQ4IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJkaW1hIn0.d-KQDUxQHvSRedmoCm-uqDHyXui1vxr3R_i1vIUvudB-Ef4rmUdeZeDwHcaLzE3k3PJnqzHIZdRFnzRHb1TM_pfcXejuh3y6He-gJFC_ogatzJyiG89PIUSYzUFrVoqWMtE0P7I7dDc821H78Pw6ncOgTJT-uukjVXEURZq9um_pGoKiyLsrdeyG_aAWyo_NK1YkrSO1r3cOjJS7RsHHwY1pkA3QYv5BecY0aLM2ls1_rQrhbvXmomgWRtkYAMvYS9zY2xZdogd0aWjWhhx9O3wQt0DAnx8v8BR-N_lcP1tU75XKmn_FRsV8Eqj0Fwl54I3J_wzmZa2u0eiY95y9jg";
    public static Movie createMovieFixture(String name, int isWinner, int releaseYear, BigDecimal avgRating, BigDecimal boxOfficeValue) {
        Movie movie = new Movie();
        movie.setName(name);
        movie.setIsWinner(isWinner);
        movie.setReleaseYear(releaseYear);
        movie.setAvgRating(avgRating);
        movie.setOfficeBoxValue(boxOfficeValue);
        return movie;
    }

    public static Rating createRatingFixture(Integer ratingValue, User user, Movie movie) {
        Rating rating = new Rating();
        rating.setRatingValue(ratingValue);
        rating.setUser(user);
        rating.setMovie(movie);
        return rating;
    }

    public static User createUserFixture(String name, List<Rating> ratingList) {
        User user = new User();
        user.setName(name);
        user.setRatingList(ratingList);
        return user;
    }
}
