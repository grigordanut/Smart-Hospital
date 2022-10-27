package com.example.smarthospital;

import static android.icu.text.DateFormat.NONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ImageViewHolder> {

    //declare variables
    private Context medRecContext;
    private List<MedicalRecords> medRecList;
    private OnItemClickListener clickListener;

    public MedicalRecordAdapter(Context recContext, List<MedicalRecords>medRecUpload){
        medRecContext = recContext;
        medRecList = medRecUpload;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(medRecContext).inflate(R.layout.image_medical_record, parent, false);
        return new ImageViewHolder(v);
    }

    //set the item layout view
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        MedicalRecords uploadCurrent = medRecList.get(position);
        holder.tVShowPatName.setText(uploadCurrent.getRecordPat_Name());
        holder.tVShowPatGender.setText(uploadCurrent.getMedRecord_Gender());
        holder.tVShowPatDateBirth.setText(uploadCurrent.getMedRecord_DateBirth());
        holder.tVShowPatPPSNo.setText(uploadCurrent.getMedRecord_PPS());
        holder.tVShowPatAddress.setText(uploadCurrent.getMedRecord_Address());
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return medRecList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public TextView tVShowPatName;
        public TextView tVShowPatGender;
        public TextView tVShowPatDateBirth;
        public TextView tVShowPatPPSNo;
        public TextView tVShowPatAddress;

        @SuppressLint("CutPasteId")
        public ImageViewHolder(View itemView) {
            super(itemView);
            tVShowPatName = itemView.findViewById(R.id.tvShowPatName);
            tVShowPatGender = itemView.findViewById(R.id.tvShowPatGender);
            tVShowPatDateBirth = itemView.findViewById(R.id.tvShowPatDateBirth);
            tVShowPatPPSNo = itemView.findViewById(R.id.tvShowPatPPSNo);
            tVShowPatAddress = itemView.findViewById(R.id.tvShowPatAddress);


            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }

        //create onItem click menu
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doUpdate  = menu.add(NONE, 1, 1, "Update");
            MenuItem doDelete  = menu.add(NONE, 2, 2, "Delete");

            doUpdate.setOnMenuItemClickListener(this);
            doDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListener.onUpdateClick(position);
                            return true;

                        case 2:
                            clickListener.onDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onUpdateClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
