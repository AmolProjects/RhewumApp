package com.rhewum.Activity.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rhewum.Activity.Pojo.SubArrayNews;
import com.rhewum.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {
    private final ArrayList<SubArrayNews> sliderItems;
    private final Context context;
    private int currentPosition = 0;

    public SliderAdapter(Context context, ArrayList<SubArrayNews> sliderItems) {
        this.context = context;
        this.sliderItems = sliderItems;
       // Log.e("Item size","Size"+sl)
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliders_items, parent, false);
        return new SliderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        // Ensure we don't go out of bounds
        if (position >= 0) {
            ArrayList<SubArrayNews> sliderItem = sliderItems;
            viewHolder.txtNewsTitle.setText(sliderItem.get(position).title);
            viewHolder.txtHeader.setText(sliderItem.get(position).headline);
            viewHolder.txtDescription.setText(sliderItem.get(position).meta_description);
            Glide.with(context)
                    .load(sliderItem.get(position).teaser_image)
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
        TextView txtNewsTitle,txtHeader,txtDescription;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_imageView);
            txtNewsTitle = itemView.findViewById(R.id.txtNewsTitle);
            txtHeader = itemView.findViewById(R.id.txtHeader);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
