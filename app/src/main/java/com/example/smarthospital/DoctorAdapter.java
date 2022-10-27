package com.example.smarthospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ImageViewHolder> {

    private final Context doctorsContext;
    private final List<Doctors> doctorsUploads;

    private OnItemClickListener clickListener;

    public DoctorAdapter(Context doctors_context, List<Doctors> doctors_uploads){
        doctorsContext = doctors_context;
        doctorsUploads = doctors_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(doctorsContext).inflate(R.layout.image_doctor,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Doctors uploadCurrent = doctorsUploads.get(position);

        holder.tVShowDocFirstName.setText(uploadCurrent.getDocFirst_Name());
        holder.tVShowDocLastName.setText(uploadCurrent.getDocLast_Name());
        holder.tVShowDocPhoneNumber.setText(uploadCurrent.getDocPhone_Number());
    }

    @Override
    public int getItemCount() {
        return doctorsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVShowDocFirstName;
        public TextView tVShowDocLastName;
        public TextView tVShowDocPhoneNumber;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVShowDocFirstName = itemView.findViewById(R.id.tvShowDocFirstName);
            tVShowDocLastName = itemView.findViewById(R.id.tvShowDocLastName);
            tVShowDocPhoneNumber = itemView.findViewById(R.id.tvShowDocPhoneNumber);

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
