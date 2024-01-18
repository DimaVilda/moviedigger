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
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_winner", nullable = false)
    private Integer isWinner;

    @Column(name = "office_box_value", precision = 10, scale = 3)
    private BigDecimal officeBoxValue;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rating> ratingList = new ArrayList<>();
}
