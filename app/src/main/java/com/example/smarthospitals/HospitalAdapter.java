package com.example.smarthospitals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ImageViewHolder> {

    private final Context hospitalsContext;
    private final List<Hospitals> hospitalsUploads;

    private OnItemClickListener clickListener;

    public HospitalAdapter(Context hospitals_context, List<Hospitals> hospitals_uploads){
        hospitalsContext = hospitals_context;
        hospitalsUploads = hospitals_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(hospitalsContext).inflate(R.layout.image_hospital,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Hospitals uploadCurrent = hospitalsUploads.get(position);

        holder.tVShowHospName.setText(uploadCurrent.getHosp_Name());
    }

    @Override
    public int getItemCount() {
        return hospitalsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVShowHospName;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVShowHospName = itemView.findViewById(R.id.tvShowHospName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
