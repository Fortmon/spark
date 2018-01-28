#!/bin/bash
cd NasaLogParser/
mvn clean install
mv target/NasaLogParser-1.0-jar-with-dependencies.jar ../docker/code/NasaLogParser.jar
