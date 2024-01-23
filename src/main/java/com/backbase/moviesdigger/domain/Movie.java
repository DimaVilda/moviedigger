package com.backbase.moviesdigger.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "movie")
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_winner", nullable = false)
    private Integer isWinner;

    @EqualsAndHashCode.Include
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(name = "office_box_value", precision = 13, scale = 3)
    private BigDecimal officeBoxValue;

    @Column(name = "avg_rating", precision = 5, scale = 2)
    private BigDecimal avgRating;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rating> ratingList = new ArrayList<>();
}
