#!/bin/bash
cd Psychometric-Annotator
docker build -t psyanno .
cd ..
cd ImageHandler
docker build -t imagehandler .
cd ..

docker-compose up -d
sleep 1
docker exec -it psychometricannotator_db_1 sh -c 'mysql --password=$MYSQL_ROOT_PASSWORD documents < /test/initScript.sql'
