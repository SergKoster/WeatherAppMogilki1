package com.example.weatherapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private final List<ForecastDayUiModel> items = new ArrayList<>();

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastDayUiModel item = items.get(position);
        holder.dayTextView.setText(item.getDay());
        String tempText = holder.itemView.getContext().getString(
                R.string.day_temp_format,
                String.format(Locale.getDefault(), "%d", item.getMaxTemp()),
                String.format(Locale.getDefault(), "%d", item.getMinTemp())
        );
        holder.tempTextView.setText(tempText);

        String iconUrl = "https://openweathermap.org/img/wn/" + item.getIconCode() + "@2x.png";
        Glide.with(holder.iconImageView)
                .load(iconUrl)
                .into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submitList(List<ForecastDayUiModel> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        private final TextView dayTextView;
        private final ImageView iconImageView;
        private final TextView tempTextView;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            iconImageView = itemView.findViewById(R.id.forecastIconImageView);
            tempTextView = itemView.findViewById(R.id.dayTempTextView);
        }
    }
}
