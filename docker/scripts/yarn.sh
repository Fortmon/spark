#!/bin/bash

start-yarn.sh

spark-submit --class ru.ifmo.spark.ServerErrorList \
             --master yarn \
             --deploy-mode client \
             NasaLogParser.jar master /logs /task1

spark-submit --class ru.ifmo.spark.RequestCountByDate \
             --master yarn \
             --deploy-mode client \
             NasaLogParser.jar master /logs /task2

spark-submit --class ru.ifmo.spark.ErrorRequestsCount \
             --master yarn \
             --deploy-mode client \
             NasaLogParser.jar master /logs /task3
