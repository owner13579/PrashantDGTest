package com.prashant.prashantdgtest;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Prashant Gadekar on 10,November,2019
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private JSONArray mDataset;
    ActionChangePostCount actionChangePostCount;

    public void refresh(JSONArray postList) {

        mDataset = new JSONArray();
        mDataset = postList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_post_title;
        public TextView tv_created_at;
        private SwitchCompat swich_is_selected;
        public MyViewHolder(View row) {
            super(row);
            tv_post_title = row.findViewById(R.id.tv_post_title);
            tv_created_at = row.findViewById(R.id.tv_created_at);
            swich_is_selected = row.findViewById(R.id.swich_is_selected);
        }
    }

    public PostAdapter(JSONArray myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View row = inflater.inflate(R.layout.row_post_list, parent, false);


        MyViewHolder vh = new MyViewHolder(row);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            holder.tv_post_title.setText(mDataset.getJSONObject(position).getString("title"));
            holder.tv_created_at.setText("Created On: "+ mDataset.getJSONObject(position).getString("created_at"));


            holder.swich_is_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    try {
                        if(isChecked){
                            mDataset.getJSONObject(position).put("IS_SELECTED", true);
                        }else{
                            mDataset.getJSONObject(position).put("IS_SELECTED", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    actionChangePostCount.getIsSelected(isChecked, position);


                }
            });


            if(mDataset.getJSONObject(position).getBoolean("IS_SELECTED")){
                holder.swich_is_selected.setChecked(true);
            }else{
                holder.swich_is_selected.setChecked(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }


    public void  setSelectedListener(ActionChangePostCount actionChangePostCount){
        this.actionChangePostCount = actionChangePostCount;
    }




}