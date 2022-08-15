#!/bin/bash
docker-compose down --remove-orphans

read -p "WARNING: Initialization will erase all data previously collected, are you sure you want to do this [y/n]: " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
  docker pull tsinzitari/psyanno:latest
  docker pull tsinzitari/imagehandler:latest
  docker pull mysql/mysql-server:5.7.22
  docker-compose up -d --force-recreate --renew-anon-volumes 
  sleep 1
  docker exec -it psychometric-annotator_db_1 sh -c 'mysql --password=$MYSQL_ROOT_PASSWORD documents < /test/initScript.sql'
  docker exec -it psychometric-annotator_imagehandler_1 sh -c '. ~/miniconda2/bin/activate image_handler && python /app/document_processor.py'
  docker exec -it psychometric-annotator_imagehandler_1 sh -c 'rm -rf /input/*'
  docker exec -it psychometric-annotator_imagehandler_1 sh -c 'rm -rf /output/*'
fi
