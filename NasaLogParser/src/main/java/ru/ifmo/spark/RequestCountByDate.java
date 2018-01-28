package ru.ifmo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.text.SimpleDateFormat;

public class RequestCountByDate {
    private static final SimpleDateFormat SIMPLE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        if (args.length < 3 ){
            System.out.println("usage java -jar NasaLogParser.jar RequestCountByDate [hostname] [input file name] [output file name]");
            return;
        }

        String inputFileName = "hdfs://" + args[0] + ":9000" + args[1];
        String outputFileName = "hdfs://" + args[0] + ":9000" + args[2];

        SparkConf sparkConf = new SparkConf().setAppName("NasaLogParser");

        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<Request> requests = javaSparkContext
                .textFile(inputFileName)
                .map(Request.parseRequest)
                .filter(x -> x != null);

        JavaPairRDD<String, Integer> requestCountByDate = requests
                .mapToPair(x -> new Tuple2<>(SIMPLE_FORMATTER.format(x.getDateTime()) + " " + x.getMethod() + " "  + x.getReplyCode(), 1));

        requestCountByDate
                .reduceByKey((i1,i2) -> i1+i2)
                .filter((x) -> x._2 > 10)
                .sortByKey()
                .coalesce(1)
                .saveAsTextFile(outputFileName);
    }
}
