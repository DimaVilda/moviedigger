DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS user_information;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS rating;

CREATE TYPE user_state AS ENUM ('LOGGED_IN', 'LOGGED_OUT');
CREATE TABLE user_information
(
    id                     VARCHAR(255) NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    state                  user_state NOT NULL,

    CONSTRAINT pk_user_information PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id                  VARCHAR(255) NOT NULL,
    user_information_id VARCHAR(255) NOT NULL,
    token_value         VARCHAR(2048) NOT NULL,
    creation_time       TIMESTAMP NOT NULL,
    expiration_time     INTEGER NOT NULL,

    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT fk_refresh_token_user_information FOREIGN KEY (user_information_id) REFERENCES user_information (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE movie
(
    id               VARCHAR(255)  NOT NULL,
    name             VARCHAR(255)  NOT NULL,
    is_winner        TINYINT       NOT NULL,
    office_box_value DECIMAL(10,3) NOT NULL,

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
        ON DELETE NO ACTION
);