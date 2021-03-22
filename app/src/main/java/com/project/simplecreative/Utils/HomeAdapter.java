package com.project.simplecreative.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.simplecreative.Model.PhotoModel;
import com.project.simplecreative.R;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> implements Filterable {

    private Context context;
//    private ArrayList<String> img;
//    private ArrayList<String> title;
//    private ArrayList<String> caption;
    private List<PhotoModel> photoList;
    private List<PhotoModel> photoListFilter;
    private OnItemClickListener onItemClickListener;


    public HomeAdapter(Context context, List<PhotoModel> photoList) {
        this.context = context;
//        this.img = img;
//        this.title = title;
//        this.caption = caption;
        this.photoList = photoList;
        this.photoListFilter = photoList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    photoListFilter = photoList;
                }else {
                    List<PhotoModel> filteredList = new ArrayList<>();
                    for (PhotoModel photoModel : photoList){
                        if (photoModel.getTitle().toLowerCase().contains(charString.toLowerCase()) || photoModel.getTags().toLowerCase().contains(charSequence)){
                            filteredList.add(photoModel);
                        }
                    }
                    photoListFilter = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = photoListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                photoListFilter = (ArrayList<PhotoModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.cardview,parent,false);

        MyViewHolder v = new MyViewHolder(view, onItemClickListener);

        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PhotoModel photoModel = photoListFilter.get(position);
        Glide.with(context)
                .asBitmap()
                .load(photoModel.getPath())
                .into(holder.imageView);

        holder.title.setText(photoModel.getTitle());
        holder.caption.setText(photoModel.getCaption());

    }

    @Override
    public int getItemCount() {
        return photoListFilter.size();
    }

    @SuppressWarnings("Duplicates")
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, caption;
        ImageView imageView;

        public MyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            title = itemView.findViewById(R.id.postTitle);
            caption = itemView.findViewById(R.id.postCaption);
            imageView = itemView.findViewById(R.id.postImage);
//            time = itemView.findViewById(R.id.postTime);
        }
    }
}
