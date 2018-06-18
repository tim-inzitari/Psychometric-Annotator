# Psychometric-Annotator

Toolset for generating pixel by pixel ground truth data for manuscript images.


Requires Docker and Docker-Compose


For first time use, use `initialize.sh` to initialize the docker containers and the database.
Note that `initialize.sh` also destroys all previously collected data, so please be careful
Afterwards, simply go to localhost:8080 in any web browser (it was mostly tested in firefox)

To start it again after initializing it run `startup.sh` or `docker-compose up`

To shut it down run `shutdown.sh` or `docker-compose down --remove-orphans`
