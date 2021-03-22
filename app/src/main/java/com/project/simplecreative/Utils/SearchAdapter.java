package com.project.simplecreative.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.project.simplecreative.Model.UserModel;
import com.project.simplecreative.R;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

//    private ArrayList<String> photo;
//    private ArrayList<String> displayName;
//    private ArrayList<String> caption;

    private List<UserModel> user;
    private List<UserModel> userFilter;
    private Context context;
    private OnItemClickListener onItemClickListener;

//    public SearchAdapter(Context context, ArrayList<String> photo, ArrayList<String> displayName, ArrayList<String> caption) {
//        this.photo = photo;
//        this.displayName = displayName;
//        this.caption = caption;
//        this.context = context;
//    }


    public SearchAdapter(Context context, List<UserModel> user) {
        this.context = context;
        this.user = user;
        this.userFilter = user;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        ViewHolder v = new ViewHolder(view, onItemClickListener);

        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        UserModel userModel = userFilter.get(position);
        Glide.with(context)
                .asBitmap()
                .load(userModel.getProfile_photo())
                .into(holder.circleImageView);

        holder.name.setText(userModel.getName());
        holder.desc.setText(userModel.getDescription());
    }

    @Override
    public int getItemCount() {
        return userFilter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name, desc;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            circleImageView = itemView.findViewById(R.id.listProfileImage);
            name = itemView.findViewById(R.id.listDisplayName);
            desc = itemView.findViewById(R.id.listStatus);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    userFilter = user;
                }else {
                    List<UserModel> filteredList = new ArrayList<>();
                    for (UserModel userModel : user){
                        if (userModel.getName().toLowerCase().contains(charString.toLowerCase()) || userModel.getDisplay_name().toLowerCase().contains(charSequence)){
                            filteredList.add(userModel);
                        }
                    }
                    userFilter = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userFilter = (ArrayList<UserModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
