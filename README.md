# Psychometric-Annotator

Toolset for generating pixel by pixel ground truth data for manuscript images.

Requires Docker and Docker-Compose

For first time use, use `startup.sh` to initialize the docker containers and the database.
Afterwards, simply go to localhost:8080 in any web browser (it was mostly tested in firefox)
To shut it down run `docker-compose down --remove-orphans`
To start it again after initializing it `run docker-compose up`
