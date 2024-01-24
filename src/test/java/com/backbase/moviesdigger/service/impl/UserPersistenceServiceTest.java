package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPersistenceServiceTest {
    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private UserPersistenceService userPersistenceService;

    public static final String testUsername = "user";

    @Test
    void shouldThrowNotFoundWhenFindByUserName() {
        when(userJpaRepository.findByNameIs(testUsername)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userPersistenceService.findByUserName(testUsername));
        assertThat(exception.getMessage(), is("A user " + testUsername + " was not created."));
    }

    @Test
    void shouldThrowConflictExceptionWhenSaveUserWithSameName() {
       when(userJpaRepository.save(any())).thenThrow(new DataIntegrityViolationException("Exception"));

        ConflictException exception = assertThrows(ConflictException.class, () ->
                userPersistenceService.saveUser(testUsername));
        assertThat(exception.getMessage(), is("A user with the name " + testUsername + " already exists." +
                "User name must be unique."));
    }
}
