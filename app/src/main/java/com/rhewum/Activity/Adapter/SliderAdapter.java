package com.rhewum.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rhewum.Activity.model.SliderItem;
import com.rhewum.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {
    private final List<SliderItem> sliderItems;
    private final Context context;
    private int currentPosition = 0;

    public SliderAdapter(Context context, List<SliderItem> sliderItems) {
        this.context = context;
        this.sliderItems = sliderItems;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new SliderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        // Ensure we don't go out of bounds
        if (position >= 0 && position < sliderItems.size()) {
            SliderItem sliderItem = sliderItems.get(position);
            viewHolder.textView.setText(sliderItem.getTitle());
            Glide.with(context)
                    .load(sliderItem.getImageUrl())
                    .fitCenter()
                    .into(viewHolder.imageView);
        }
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
        notifyDataSetChanged();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;
        TextView textView;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
