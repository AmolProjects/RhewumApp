package com.rhewum.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    private static final int MAX_ITEMS = 5;  // Set the maximum number of items to display

    public SliderAdapter(Context context, ArrayList<SubArrayNews> sliderItems) {
        this.context = context;
//        this.sliderItems = sliderItems;
        this.sliderItems = new ArrayList<>(sliderItems.subList(0, Math.min(sliderItems.size(), MAX_ITEMS)));
        // Log.e("Item size","Size"+sl)
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliders_items, parent, false);
        return new SliderAdapterViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
//        // Ensure we don't go out of bounds
//        if (position >= 0 && position < sliderItems.size()) {
//            ArrayList<SubArrayNews> sliderItem = sliderItems;
//            viewHolder.txtNewsTitle.setText(sliderItem.get(position).title);
//            viewHolder.txtHeader.setText(sliderItem.get(position).headline);
//            viewHolder.txtDescription.setText(sliderItem.get(position).meta_description);
//            Glide.with(context)
//                    .load(sliderItem.get(position).teaser_image)
//                    .fitCenter()
//                    .into(viewHolder.imageView);
//
//            // Set click listener to open the webpage URL
//            viewHolder.itemView.setOnClickListener(v -> {
//                String baseUrl = "https://www.rhewum.com"; // Assuming this is the base URL
//                String fullUrl = baseUrl + sliderItem.getUrl();
//
//                // Open the URL in a browser
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(fullUrl));
//                context.startActivity(intent);
//            });
//        }
//    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        // Ensure we don't go out of bounds
        if (position >= 0 && position < sliderItems.size()) {
            // Get the individual item at the current position
            SubArrayNews sliderItem = sliderItems.get(position);

            // Set the text fields with data from the current item
            viewHolder.txtNewsTitle.setText(sliderItem.getTitle());
            viewHolder.txtHeader.setText(sliderItem.getHeadline());
            viewHolder.txtDescription.setText(sliderItem.getMeta_description());

            // Load the image using Glide
            Glide.with(context)
                    .load(sliderItem.getTeaser_image())  // Assuming getTeaser_image() returns the image URL
                    .fitCenter()
                    .into(viewHolder.imageView);

            // Set click listener to open the webpage URL
            viewHolder.itemView.setOnClickListener(v -> {
                String baseUrl = "https://www.rhewum.com"; // Assuming this is the base URL
                String fullUrl = baseUrl + sliderItem.getUrl(); // Get URL for the current item

                // Open the URL in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(fullUrl));
                context.startActivity(intent);
            });
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