### How to test

### 1. To test it, you need to run it, so check how_to_run.md section first

### 2. To test application you first need to create a user and login in keycloak
- use POST http://localhost:8081/client-api/v1/users to create a user by providing username and password creds
- then use POST http://localhost:8081/client-api/v1/users/login to login same user using same userName and password creds
from POST create endpoint. You receive 'accessToken' and its expiration in seconds. And 'refreshToken' nd it's expiration in seconds.
When 'refreshToken' expires, you have to login again. You don't have to login when your 'access_token' is expired.
When you login, you are a user and you have a userRole and rights. Admin is already created in store and keycloak.
- When your 'access_token' expired, you can simply generate a new one using POST http://localhost:8081/client-api/v1/users/token
and providing you current (could be expired already) 'access_token' and 'refresh_token'. You provide current 'access_token' so
application can invoke it in case it's active so user could not have a few sessions in same time.
- When you created a user and logged it in, you can interact with MoviesDigger application using your 'access_token' value in
Bearer token Authorization.
- Note! Some endpoints accessible only for user with adminRole role:
- DELETE http://localhost:8081/client-api/v1/users/dima - user can't delete himself or another user from database, only admin can.
- DELETE http://localhost:8081/client-api/v1/movies/rating/{ratingId} - user can't delete his rating otherwise he can scam.
To became admin, you need to login as admin using same POST http://localhost:8081/client-api/v1/users/login api but providing admin creds:
Username - admin, password - admin. You can find also this info in 'application.yml' in 'keycloak' section.
You don't have to CREATE and admin, because admin was created in db when application started.
- To end user's session (aka logout), use GET http://localhost:8081/client-api/v1/users/login/endsession


### All apis you can find in src/main/java/com/backbase/moviesdigger/openapi folder 
To check openapi in browser you have to compile an application:

  ```bash
  mvn clean compile
  ```

Then navigate to created folder target/contractUI
Here you will find clientapi and user folders with index.html HTML pages for all browser types with spec representation.