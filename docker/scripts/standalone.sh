#!/bin/bash

start-master.sh
start-slaves.sh

spark-submit --class ru.ifmo.spark.ServerErrorList NasaLogParser.jar master /logs /task1
spark-submit --class ru.ifmo.spark.RequestCountByDate NasaLogParser.jar master /logs /task2
spark-submit --class ru.ifmo.spark.ErrorRequestsCount NasaLogParser.jar master /logs /task3
