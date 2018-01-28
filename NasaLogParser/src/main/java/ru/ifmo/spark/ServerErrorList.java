package ru.ifmo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class ServerErrorList {
    public static void main(String[] args) {
        if (args.length < 3 ){
            System.out.println("usage java -jar NasaLogParser.jar ServerErrorList [hostname] [input file name] [output file name]");
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

        JavaPairRDD<String, Integer> serverErrorList = requests
                .filter(x -> x.getReplyCode() >= 500 && x.getReplyCode() < 600)
                .mapToPair(x -> new Tuple2<>(x.getHost() + " " + x.getReplyCode(), 1));

        serverErrorList
                .reduceByKey((i1,i2) -> i1+i2)
                .coalesce(1)
                .saveAsTextFile(outputFileName);
    }
}
