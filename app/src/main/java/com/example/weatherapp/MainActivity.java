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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String METRIC_UNITS = "metric";
    private static final String LANG_RU = "ru";

    private final WeatherApiService weatherApiService = WeatherClient.create();

    private TextInputEditText cityEditText;
    private TextInputLayout cityInputLayout;
    private TextView cityNameTextView;
    private ImageView weatherIconImageView;
    private TextView tempTextView;
    private TextView feelsLikeTextView;
    private ForecastAdapter forecastAdapter;

    private String apiKey;

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
        apiKey = getString(R.string.weather_api_key).trim();

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

        if (TextUtils.isEmpty(apiKey) || "put_your_openweathermap_api_key_here".equals(apiKey)) {
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
        weatherApiService.getCurrentWeather(city, apiKey, METRIC_UNITS, LANG_RU)
                .enqueue(new Callback<CurrentWeatherResponse>() {
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
        weatherApiService.getForecast(city, apiKey, METRIC_UNITS, LANG_RU)
                .enqueue(new Callback<ForecastResponse>() {
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
        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat dayKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat dayLabelFormat = new SimpleDateFormat("EE", new Locale("ru"));

        String todayKey = dayKeyFormat.format(new Date());
        Map<String, ForecastAccumulator> byDay = new LinkedHashMap<>();

        for (ForecastResponse.ForecastItem item : items) {
            if (item.getDateText() == null || item.getMainData() == null) {
                continue;
            }

            Date parsedDate;
            try {
                parsedDate = fullFormat.parse(item.getDateText());
            } catch (ParseException ignored) {
                continue;
            }
            if (parsedDate == null) {
                continue;
            }

            String dayKey = dayKeyFormat.format(parsedDate);
            if (dayKey.compareTo(todayKey) <= 0) {
                continue;
            }

            ForecastAccumulator accumulator = byDay.get(dayKey);
            if (accumulator == null) {
                accumulator = new ForecastAccumulator();
                accumulator.date = parsedDate;
                byDay.put(dayKey, accumulator);
            }

            accumulator.minTemp = Math.min(accumulator.minTemp, item.getMainData().getTempMin());
            accumulator.maxTemp = Math.max(accumulator.maxTemp, item.getMainData().getTempMax());
            if (accumulator.iconCode == null && item.getWeather() != null && !item.getWeather().isEmpty()) {
                accumulator.iconCode = item.getWeather().get(0).getIcon();
            }
        }

        List<ForecastDayUiModel> result = new ArrayList<>();
        for (ForecastAccumulator acc : byDay.values()) {
            String dayName = dayLabelFormat.format(acc.date);
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
        private Date date;
    }
}
