package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<ForecastItem> forecastItems;

    public List<ForecastItem> getForecastItems() {
        return forecastItems;
    }

    public static class ForecastItem {
        @SerializedName("dt_txt")
        private String dateText;

        @SerializedName("main")
        private MainData mainData;

        @SerializedName("weather")
        private List<WeatherData> weather;

        public String getDateText() {
            return dateText;
        }

        public MainData getMainData() {
            return mainData;
        }

        public List<WeatherData> getWeather() {
            return weather;
        }
    }

    public static class MainData {
        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }
    }

    public static class WeatherData {
        @SerializedName("icon")
        private String icon;

        public String getIcon() {
            return icon;
        }
    }
}
