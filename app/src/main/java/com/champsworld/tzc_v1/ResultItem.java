package com.champsworld.tzc_v1;

public class ResultItem {
    private final String location;
    private final String convertedTime;
    private final String timezone;
    private final String date;

    public ResultItem(String location, String convertedTime, String timezone, String date) {
        this.location = location;
        this.convertedTime = convertedTime;
        this.timezone = timezone;
        this.date = date;
    }

    public ResultItem(String location, String timezone) {
        this(location, "", timezone, "");
    }

    public String getLocation() {
        return location;
    }

    public String getConvertedTime() {
        return convertedTime;
    }

    public String getTimezone() {
        return timezone;
    }
    public String getDate() {
        return date;
    }
}
