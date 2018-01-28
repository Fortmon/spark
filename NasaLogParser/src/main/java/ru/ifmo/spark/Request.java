package ru.ifmo.spark;

import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Request implements Serializable {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");
    private static final int HOST_INDEX = 0;
    private static final int DATE_INDEX = 3;
    private static final int METHOD_INDEX = 5;
    private static final int PATH_TO_FILE_INDEX = 6;
    private static final int PROTOCOL_INDEX = 7;
    private static final int REPLY_CODE_INDEX = 8;
    private static final int REPLY_BYTES_INDEX = 9;

    private String host;
    private Date dateTime;
    private String method;
    private String pathToFile;
    private String protocol;
    private int replyCode;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int replyCode) {
        this.replyCode = replyCode;
    }

    public int getBytesInReply() {
        return bytesInReply;
    }

    public void setBytesInReply(int bytesInReply) {
        this.bytesInReply = bytesInReply;
    }

    private int bytesInReply;

    public static Request parseRequest(String s) {
        try {
            s = s.replaceAll("[\",\\[,\\]]", "").trim().replaceAll("\\s{2,}", " ");
            String[] parts = s.split(" ");

            String host = parts[HOST_INDEX];
            LocalDateTime dateTime = LocalDateTime.parse(parts[DATE_INDEX], DATE_TIME_FORMATTER);
            String method = parts[METHOD_INDEX];
            String pathToFile = parts[PATH_TO_FILE_INDEX];
            String protocol = parts[PROTOCOL_INDEX];
            int replyCode = Integer.parseInt(parts[REPLY_CODE_INDEX]);
            int bytesInReply = 0;
            if (!parts[REPLY_BYTES_INDEX].equals("-")) {
                bytesInReply = Integer.parseInt(parts[REPLY_BYTES_INDEX]);
            }
            return new Request(host, new Date(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), method, pathToFile, protocol, replyCode, bytesInReply);
        } catch (Exception ex) {
            System.out.println("Error parsing string: " + s + " Message: " + ex.getMessage());

        }

        return null;
    }

    public static Function<String, Request> parseRequest = s -> Request.parseRequest(s);

    public Request() {

    }

    public Request(String host, Date dateTime, String method, String pathToFile, String protocol, int replyCode, int bytesInReply) {
        this.host = host;
        this.dateTime = dateTime;
        this.method = method;
        this.pathToFile = pathToFile;
        this.protocol = protocol;
        this.replyCode = replyCode;
        this.bytesInReply = bytesInReply;
    }
}