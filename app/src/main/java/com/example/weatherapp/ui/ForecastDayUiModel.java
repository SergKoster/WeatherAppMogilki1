package com.example.weatherapp.ui;

public class ForecastDayUiModel {
    private final String day;
    private final int minTemp;
    private final int maxTemp;
    private final String iconCode;

    public ForecastDayUiModel(String day, int minTemp, int maxTemp, String iconCode) {
        this.day = day;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.iconCode = iconCode;
    }

    public String getDay() {
        return day;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public String getIconCode() {
        return iconCode;
    }
}
