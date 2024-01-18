package com.backbase.moviesdigger.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

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
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "name")
    private String name;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rating> ratingList = new ArrayList<>();
}
