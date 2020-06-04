package com.example.stromful;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ForcastAdapter extends RecyclerView.Adapter<ForcastAdapter.ForcastAdapterViewHolder> {

    private String[] mWeatherdata;
    private final ForcastAdapterOnclickListner mOnclickListnrer;

    //setter for weather data

    public void setWeatherData(String[] weatherData) {

        this.mWeatherdata = weatherData;
        notifyDataSetChanged();
    }

    public interface ForcastAdapterOnclickListner {
        void onClick(String weatherfordy);
    }

    public ForcastAdapter(ForcastAdapterOnclickListner listner) {
        this.mOnclickListnrer = listner;
    }


    @NonNull
    @Override
    public ForcastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forcast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ForcastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForcastAdapterViewHolder holder, int position) {

        String weatherForThisDay = mWeatherdata[position];
        holder.weathertextview.setText(weatherForThisDay);

    }

    @Override
    public int getItemCount() {
        if (mWeatherdata == null) {
            return 0;
        }else {
            return mWeatherdata.length;
        }
    }

    public class ForcastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView weathertextview;

        public ForcastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            weathertextview = itemView.findViewById(R.id.iv_tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();
            String weatherforday = mWeatherdata[adapterposition];
            mOnclickListnrer.onClick(weatherforday);

        }
    }
}
