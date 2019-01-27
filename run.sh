#!/bin/bash

# run tests, compile and build the jar
./mvnw clean package

# build restshorturl image
docker-compose build

# start mysql and restshorturl containers
docker-compose up
