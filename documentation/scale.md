### How to scale it up
- Break it into microservices or modules
- Find application's bottleneck by providing a few performance tests for endpoints that potentially trigger OMDB service apis.
Use python + locust library for it cause it fast and provides its UI client to check graphics and setup test init load.
- Use async process to retrieve data from OMDB. You can check some ideas in diagrams/drawio/Scaling.drawio or in diagrams/images/ScalingAsync.png 
they are same just different format. Use queue, some job that will be triggered using cron and will poll requests from queue and process them.
- Move solution to Cloud infrastructure, ie Google Cloud Kubernetes Engine or VM instance, where you can predefine loadbalancer and adjust a boot disk
memory and CPU capacity to run and scale application. Of course speak about budget with managers.
- Use canary or Alpha release with limited amount of users to check how it works and define some
new issues and bugs.