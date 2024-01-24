### Moviesdigger (Backbase technical task)
This application acts as an OMDB service integrator and use its apis to provide certain information regarding movies.
Consists of 3 main services: 
- UserService - provides user's authentication process with keycloak tool located in resources/presetup folder;
- MoviesDiggerService - receives user's requests after successful authentication and provide movie info only from local storage;
- MovieProviderService - collaborates with movie providers (in current app version it's OMDB) by using their apis and 
retrieve movie's missing data from local storage and update local storage in case new data retrieved from movie provider.
Application uses OpenID client endpoints to secure application and check JWT access-tokens public keys in endpoint's headers.
Keycloak + OpenID client solutions were chosen because they provide ready-made tools for user authentication and used in Backbase.
The only reason why Keycloak runs locally and not from docker compose it's a non-working docker image for the M2 generation macbooks.

###  Tech stack
    Java 17
    Keycloak desktop
    H2 database
    Liquibase
    Testcontainers
    Jupiter
    