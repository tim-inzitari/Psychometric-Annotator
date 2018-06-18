#!/bin/bash


read -p "WARNING: Initialization will erase all data previously collected." -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
  docker-compose up -d
  sleep 1
  docker exec -it psychometricannotator_db_1 sh -c 'mysql --password=$MYSQL_ROOT_PASSWORD documents < /test/initScript.sql'
  docker exec -it psychometricannotator_imagehandler_1 sh -c -rm /input/*
  docker exec -it psychometricannotator_imagehandler_1 sh -c -rm /output/*
fi
