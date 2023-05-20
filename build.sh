#!/bin/bash

gradle clean bootJar
docker run cf83b596cc88

docker-compose down --remove-orphans --volumes
docker rm -f $(docker ps | grep -v "mcr.microsoft.com/mssql/server:2022-latest")
docker-compose build
docker-compose up
