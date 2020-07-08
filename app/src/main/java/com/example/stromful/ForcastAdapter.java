package com.example.stromful;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stromful.Utilities.StromfulDateUtils;
import com.example.stromful.Utilities.StromfulWeatherUtils;

public class ForcastAdapter extends RecyclerView.Adapter<ForcastAdapter.ForcastAdapterViewHolder> {
    // variables
    private final ForcastAdapterOnclickListner mOnclickListnrer;
    private Cursor mCursor;
    private final Context mContext;

    //setter for weather data


// -------------------------rv on click listener-------------------
    public interface ForcastAdapterOnclickListner {
        void onClick(long date);
    }

    public ForcastAdapter(Context context,ForcastAdapterOnclickListner listner) {
        this.mContext = context;
        this.mOnclickListnrer = listner;
    }

// ================RV Functions========================================
    @NonNull
    @Override
    public ForcastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forcast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        view.setFocusable(true);
        return new ForcastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForcastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        //=====================Weather Data Setting========================
        // getting all columns values
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = StromfulDateUtils.getFriendlyDateString(mContext,dateInMillis,false);
        // condition
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = StromfulWeatherUtils.getStringForWeatherCondition(mContext,weatherId);
        // temperature
        double highTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        double lowTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        // format degree celsius
        String highAndLowTemperature = StromfulWeatherUtils.formatHighLow(mContext,highTemp,lowTemp);
        String weatherSummary = dateString + "-" +description + "-" +highAndLowTemperature;
        holder.weathertextview.setText(weatherSummary);

    }
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }
    // =========================Cursor Function===========================
        // swap cursor for new weather data
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
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
            mCursor.moveToPosition(adapterposition);
            long dateTimeMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mOnclickListnrer.onClick(dateTimeMillis);

        }
    }
}
