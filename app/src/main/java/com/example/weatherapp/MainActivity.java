package com.example.weatherapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.api.WeatherApiService;
import com.example.weatherapp.api.WeatherClient;
import com.example.weatherapp.model.CurrentWeatherResponse;
import com.example.weatherapp.model.ForecastResponse;
import com.example.weatherapp.ui.ForecastAdapter;
import com.example.weatherapp.ui.ForecastDayUiModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final WeatherApiService weatherApiService = WeatherClient.create();

    private TextInputEditText cityEditText;
    private TextInputLayout cityInputLayout;
    private TextView cityNameTextView;
    private ImageView weatherIconImageView;
    private TextView tempTextView;
    private TextView feelsLikeTextView;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        cityInputLayout = findViewById(R.id.cityInputLayout);
        cityNameTextView = findViewById(R.id.cityNameTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        tempTextView = findViewById(R.id.tempTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);

        RecyclerView forecastRecyclerView = findViewById(R.id.forecastRecyclerView);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        forecastAdapter = new ForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);

        cityInputLayout.setEndIconOnClickListener(v -> searchWeather());
        cityEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWeather();
                return true;
            }
            return false;
        });

        if (BuildConfig.WEATHER_API_KEY.isEmpty()) {
            Toast.makeText(this, getString(R.string.weather_api_key_message), Toast.LENGTH_LONG).show();
            return;
        }

        cityEditText.setText("Moscow");
        searchWeather();
    }

    private void searchWeather() {
        String city = String.valueOf(cityEditText.getText()).trim();
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, getString(R.string.empty_city_message), Toast.LENGTH_SHORT).show();
            return;
        }

        loadCurrentWeather(city);
        loadForecast(city);
    }

    private void loadCurrentWeather(String city) {
        weatherApiService.getCurrentWeather(city, BuildConfig.WEATHER_API_KEY, "metric", "ru")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<CurrentWeatherResponse> call,
                                           @NonNull Response<CurrentWeatherResponse> response) {
                        CurrentWeatherResponse body = response.body();
                        if (!response.isSuccessful() || body == null || body.getMainData() == null) {
                            showWeatherError();
                            return;
                        }

                        cityNameTextView.setText(body.getCityName());
                        int temp = (int) Math.round(body.getMainData().getTemperature());
                        int feels = (int) Math.round(body.getMainData().getFeelsLike());

                        tempTextView.setText(getString(R.string.temp_format, String.valueOf(temp)));
                        feelsLikeTextView.setText(getString(R.string.feels_like_format, String.valueOf(feels)));

                        if (body.getWeather() != null && !body.getWeather().isEmpty()) {
                            String iconCode = body.getWeather().get(0).getIcon();
                            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                            Glide.with(MainActivity.this)
                                    .load(iconUrl)
                                    .into(weatherIconImageView);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CurrentWeatherResponse> call, @NonNull Throwable throwable) {
                        showWeatherError();
                    }
                });
    }

    private void loadForecast(String city) {
        weatherApiService.getForecast(city, BuildConfig.WEATHER_API_KEY, "metric", "ru")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ForecastResponse> call,
                                           @NonNull Response<ForecastResponse> response) {
                        ForecastResponse body = response.body();
                        if (!response.isSuccessful() || body == null || body.getForecastItems() == null) {
                            showWeatherError();
                            return;
                        }

                        forecastAdapter.submitList(mapToFiveDays(body.getForecastItems()));
                    }

                    @Override
                    public void onFailure(@NonNull Call<ForecastResponse> call, @NonNull Throwable throwable) {
                        showWeatherError();
                    }
                });
    }

    private List<ForecastDayUiModel> mapToFiveDays(List<ForecastResponse.ForecastItem> items) {
        Map<LocalDate, ForecastAccumulator> byDay = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (ForecastResponse.ForecastItem item : items) {
            if (item.getDateText() == null || item.getMainData() == null) {
                continue;
            }

            LocalDate date = LocalDate.parse(item.getDateText(), formatter);
            ForecastAccumulator accumulator = byDay.get(date);
            if (accumulator == null) {
                accumulator = new ForecastAccumulator();
                byDay.put(date, accumulator);
            }

            accumulator.minTemp = Math.min(accumulator.minTemp, item.getMainData().getTempMin());
            accumulator.maxTemp = Math.max(accumulator.maxTemp, item.getMainData().getTempMax());
            if (accumulator.iconCode == null && item.getWeather() != null && !item.getWeather().isEmpty()) {
                accumulator.iconCode = item.getWeather().get(0).getIcon();
            }
        }

        List<ForecastDayUiModel> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Map.Entry<LocalDate, ForecastAccumulator> entry : byDay.entrySet()) {
            if (!entry.getKey().isAfter(today)) {
                continue;
            }

            ForecastAccumulator acc = entry.getValue();
            String dayName = entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ru"));
            result.add(new ForecastDayUiModel(
                    dayName,
                    (int) Math.round(acc.minTemp),
                    (int) Math.round(acc.maxTemp),
                    acc.iconCode == null ? "01d" : acc.iconCode
            ));

            if (result.size() == 5) {
                break;
            }
        }

        return result;
    }

    private void showWeatherError() {
        Toast.makeText(this, getString(R.string.weather_error_message), Toast.LENGTH_SHORT).show();
    }

    private static class ForecastAccumulator {
        private double minTemp = Double.MAX_VALUE;
        private double maxTemp = -Double.MAX_VALUE;
        private String iconCode;
    }
}
