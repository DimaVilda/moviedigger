### Future improvements/maintenance
- Split application in 2 different services: 'authservice' for user auth with user apis, 'moviesdigger' service with 2 modules:
'moviesdigger' for retrieving data from local store, 'moviessyncer' - to sync with OMDB or any other movie providers.
- Use docker compose to create images from this 2 services
- Find application's bottleneck by providing a few performance tests for endpoints that potentially trigger OMDB service apis.
Use python + locust library for it cause it fast and provides its UI client to check graphics and setup test init load.
- Use async process to retrieve data from OMDB or request a GET movies batch endpoint from them
- If moviesdigger service will be bigger - split it into 2 different services
- Add alerts and metrics