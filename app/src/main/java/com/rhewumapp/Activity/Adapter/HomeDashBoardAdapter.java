package com.rhewumapp.Activity.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rhewumapp.Activity.data.ItemData;
import com.rhewumapp.R;
import java.util.List;


public class HomeDashBoardAdapter extends RecyclerView.Adapter<HomeDashBoardAdapter.ViewHolder> {
    private static List<ItemData> dataList;
    private final LayoutInflater mInflater;
    public static ItemClickListener mClickListener;

    // data is passed into the constructor
   public HomeDashBoardAdapter(Context context, List<ItemData> dataList) {
        this.mInflater = LayoutInflater.from(context);
       HomeDashBoardAdapter.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views here
        ItemData itemData = dataList.get(position);
        holder.text_Title.setText(itemData.getText());
        holder.ad_icon.setImageResource(itemData.getImageResId());

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ad_icon;
        public TextView text_Title;



        public ViewHolder(View itemView) {
            super(itemView);
            ad_icon = itemView.findViewById(R.id.ad_icon);
            text_Title = itemView.findViewById(R.id.text_Title);
            itemView.setOnClickListener(this);
        }
        // convenience method for getting data at click position
        ItemData getItem(int id) {
            return dataList.get(id);
        }

        public static void setClickListener(ItemClickListener itemClickListener){
            mClickListener = itemClickListener;
        }
        // allows clicks events to be caught
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }
    public interface ItemClickListener {
         void onItemClick(View view, int position);
    }
}
