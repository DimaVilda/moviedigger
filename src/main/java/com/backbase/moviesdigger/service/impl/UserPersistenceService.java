package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.domain.User;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPersistenceService {

    private final UserJpaRepository userJpaRepository;

    public void saveUser(String userName) {
        log.debug("Trying to save a new user {}: ", userName);
        try {
            User user = new User();
            user.setName(userName);
            userJpaRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("A try to create a new user with same ");
            throw new ConflictException("A user with the name " + userName + " already exists." +
                    "User name must be unique.");
        }
    }

    private User findByUserName(String userName) {
        log.debug("Trying to get user by name {}: ", userName);

        return userJpaRepository
                .findByNameIs(userName)
                .orElseThrow(() -> {
                    log.error("User {} does not exist", userName);
                    return new NotFoundException("A user " + userName + " was not created.");
                });
    }

    public boolean isUserCreated(String userName) {
        return userJpaRepository.existsByNameIs(userName);
    }
    public void deleteUser(String userName) {
        User user = findByUserName(userName);
        userJpaRepository.delete(user);

        log.debug("User {} removed successfully", userName);
    }
}
