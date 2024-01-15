package com.backbase.moviesdigger.auth.service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Entity(name = "refresh_token")
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "token_value")
    private String tokenValue;

    @EqualsAndHashCode.Include
    @Column(name = "expiration_time")
    private Integer expirationTime;

    @EqualsAndHashCode.Include
    @Column(name = "creation_time")
    private Instant creationTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_information_id")
    private User userInformation;

}
