# Psychometric-Annotator

Toolset for generating pixel by pixel ground truth data for manuscript images.


Requires Docker and Docker-Compose


For first time use, use `initialize.sh` to initialize the docker containers and the database.
Note that `initialize.sh` also destroys all previously collected data, so please be careful
Afterwards, simply go to localhost:8080 in any web browser (it was mostly tested in firefox)

To start it again after initializing it run `startup.sh` or `docker-compose up`

To shut it down run `shutdown.sh` or `docker-compose down --remove-orphans`

After the software has started, you can access the web interface in your browser at `locahost:8080`

In order to add a document simply save your image as a .jpg file, and title it using its CITE2 URN. Then place it in the "ImageArchive" folder. The software will automatically process the image, add it to the database, and make it available in the web interface.

This software is based off of the tool for working with images cited via CITE2 URNs which is ©2017 by Christopher Blackwell. This software and Christopher Blackwell's ICT2 are available for use, modification, and distribution under the terms of the GPL 3.0 license.
