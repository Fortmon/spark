#!/bin/bash

master_name=$(docker ps --filter "name=master" --format "{{.Names}}")
docker exec -it $master_name /usr/local/app/scripts/init.sh
docker cp code/NasaLogParser.jar $master_name:/usr/local/app/scripts/
docker exec -it $master_name /usr/local/app/scripts/standalone.sh
docker exec -it $master_name /usr/local/app/scripts/get_results.sh

