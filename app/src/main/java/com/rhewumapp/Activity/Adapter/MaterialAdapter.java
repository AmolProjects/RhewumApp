package com.rhewumapp.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rhewumapp.R;
import com.rhewumapp.Utils.Material;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {

    private Context context;
    private List<Material> materialList;

    public MaterialAdapter(Context context, List<Material> materialList) {
        this.context = context;
        this.materialList = materialList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.capacity_info_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Material material = materialList.get(position);
        holder.txtMaterialVal.setText(material.getName());
        holder.txtKgM3Val.setText(material.getDensityKgM3());
        holder.txtLbFt3Val.setText(material.getDensityLbFt3());
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaterialVal, txtKgM3Val, txtLbFt3Val;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaterialVal = itemView.findViewById(R.id.txtMaterialVal);
            txtKgM3Val = itemView.findViewById(R.id.txtKgM3Val);
            txtLbFt3Val = itemView.findViewById(R.id.txtLbFt3Val);
        }
    }
}

