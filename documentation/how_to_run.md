### How to run

### 1. Run keycloak locally using terminal or using scripts below:
Note! Without keycloak you cannot work correct with application and run some IT tests.

  ```bash Linux/mac
  cd src/main/resources/presetup/keycloak-23.0.4
  bin/kc.sh start-dev
  ```

  ```bash Windows
  cd src/main/resources/presetup/keycloak-23.0.4/bin
  bin\kc.bat start-dev
  ```
Now you can go to keycloak UI by http://localhost:8080, click on Administration Console and use admin/admin 
username and password creds to access keycloak master realm. You can find also this information in application.yml 'keycloak' section.

### 2. Build back-end using terminal or a script below:
Note! It takes a few minutes to build and test it, so relax and wait just a lil it.

  ```bash
  mvn clean verify
  ```
Noice! Run application using 'MoviesdiggerApplication'file.
Application runs on http://localhost:8081, H2 db - on http://localhost:8081/h2 (password is 'digger'). You can also
find this information in application.yml 'spring.h2' section

### 3. Sorry, it's not all:(
To fully use application that work with OMDB service and send requests to it, you have to set up your own api-key to access OMDB resources:

- Make a request to get your own api-key value from OMDB site here https://www.omdbapi.com/apikey.aspx
- When you get it in your mail, replace it in application.yml in 'moviesdigger.movie-providers.omdb.api-key' section



### All apis you can find in src/main/java/com/backbase/moviesdigger/openapi folder
To check openapi in browser you have to compile an application:

  ```bash
  mvn clean compile
  ```

Then navigate to created folder target/contractUI
Here you will find clientapi and user folders with index.html HTML pages for all browser types with spec representation.