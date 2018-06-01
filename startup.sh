#!/bin/bash
cd Psychometric-Annotator
mvn install
cd ..
docker-compose up -d
sleep 1
docker exec -it psychometricannotator_db_1 sh -c 'mysql --password=$MYSQL_ROOT_PASSWORD documents < /test/initScript.sql'
