package com.project.simplecreative.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.R;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
//    private ArrayList<String> imgUrls;
//    private ArrayList<String> caption;
//    private ArrayList<String> title;
    private ArrayList<PhotoModel> photoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<PhotoModel> photoList) {
//        this.imgUrls = imgUrls;
//        this.title = title;
//        this.caption = caption;
        this.context = context;
        this.photoList = photoList;
    }

    public RecyclerViewAdapter() {
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview, parent, false);

        ViewHolder v = new ViewHolder(view, onItemClickListener);

        return v;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PhotoModel photoModel = photoList.get(position);
        Glide.with(context)
                .asBitmap()
                .load(photoModel.getPath())
                .into(holder.imageView);

        holder.desc.setText(photoModel.getCaption());
        holder.title.setText(photoModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView desc, title;

        public ViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imageView = itemView.findViewById(R.id.photoView);
            desc = itemView.findViewById(R.id.photoDescription);
            title = itemView.findViewById(R.id.photoTitle);
        }
    }
}
