#!/bin/bash
docker-compose down
docker rm -f $(docker ps -a -q)
docker volume rm $(docker volume ls -q)
gradle clean bootJar
docker-compose build
docker-compose up