package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeatherResponse {
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private MainData mainData;

    @SerializedName("weather")
    private List<WeatherData> weather;

    public String getCityName() {
        return cityName;
    }

    public MainData getMainData() {
        return mainData;
    }

    public List<WeatherData> getWeather() {
        return weather;
    }

    public static class MainData {
        @SerializedName("temp")
        private double temperature;

        @SerializedName("feels_like")
        private double feelsLike;

        public double getTemperature() {
            return temperature;
        }

        public double getFeelsLike() {
            return feelsLike;
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
