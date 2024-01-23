DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS user_information;
DROP TABLE IF EXISTS rating;

CREATE TABLE user_information
(
    id                     VARCHAR(255) NOT NULL,
    name                   VARCHAR(255) NOT NULL,

    CONSTRAINT pk_user_information PRIMARY KEY (id),
    CONSTRAINT unique_name UNIQUE (name)
);


CREATE TABLE movie
(
    id               VARCHAR(255)  NOT NULL,
    name             VARCHAR(255)  NOT NULL,
    is_winner        INTEGER       NOT NULL,
    movie_year       VARCHAR(255)  NOT NULL,
    office_box_value DECIMAL(13,3),
    avg_rating       DECIMAL(5,2),

    CONSTRAINT pk_movie PRIMARY KEY (id)
);

CREATE TABLE rating
(
    id                  VARCHAR(255) NOT NULL,
    user_information_id VARCHAR(255) NOT NULL,
    movie_id            VARCHAR(255) NOT NULL,
    rating_value        INTEGER,

    CONSTRAINT pk_rating PRIMARY KEY (id),
    CONSTRAINT fk_rating_user_information FOREIGN KEY (user_information_id) REFERENCES user_information (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_rating_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    UNIQUE (user_information_id, movie_id) -- so same user cannot rate twice
);