package com.backbase.moviesdigger.auth.service.domain;

import com.backbase.moviesdigger.auth.service.domain.enums.UserStatesEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "user_information")
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    @Column(name = "state", nullable = false, length = 36)
    private UserStatesEnum state;

    @OneToOne(mappedBy = "userInformation", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;
}
