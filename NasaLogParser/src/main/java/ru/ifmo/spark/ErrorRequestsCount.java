package ru.ifmo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple3;

import java.text.SimpleDateFormat;

import static org.apache.spark.api.java.JavaSparkContext.toSparkContext;
import static org.apache.spark.sql.functions.count;
import static org.apache.spark.sql.functions.window;


public class ErrorRequestsCount {
    private static final SimpleDateFormat SIMPLE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage java -jar NasaLogParser.jar ErrorRequestsCount [hostname] [input file name] [output file name]");
            return;
        }

        String inputFileName = "hdfs://" + args[0] + ":9000" + args[1];
        String outputFileName = "hdfs://" + args[0] + ":9000" + args[2];

        SparkConf sparkConf = new SparkConf().setAppName("NasaLogParser");

        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        SparkSession sparkSession = SparkSession.builder().
                sparkContext(toSparkContext(javaSparkContext))
                .getOrCreate();

        JavaRDD<Request> requests = javaSparkContext
                .textFile(inputFileName)
                .map(Request.parseRequest)
                .filter(x -> x != null && x.getReplyCode() >= 400 && x.getReplyCode() < 600);


        Dataset<Row> errorRequestsCount = sparkSession.createDataFrame(requests, Request.class);

        errorRequestsCount
                .groupBy(window(errorRequestsCount.col("dateTime"), "1 week", "1 day"))
                .agg(count("replyCode").as("count"))
                .select("window.start", "window.end", "count")
                .orderBy("start")
                .javaRDD()
                .map(r -> new Tuple3<>(
                        SIMPLE_FORMATTER.format(r.getTimestamp(0)),
                        SIMPLE_FORMATTER.format(r.getTimestamp(1)),
                        r.getLong(2)))
                .coalesce(1)
                .saveAsTextFile(outputFileName);

    }
}
