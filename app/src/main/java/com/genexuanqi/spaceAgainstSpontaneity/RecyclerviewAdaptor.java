package com.genexuanqi.spaceAgainstSpontaneity;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerviewAdaptor extends RecyclerView.Adapter<RecyclerviewAdaptor.ViewHolder> implements View.OnLongClickListener{

    // ======================== START OF VARIABLES ====================================== //

    ArrayList<String> textEntry;
    Context context;

    // ======================== END OF VARIABLES ====================================== //

    // ======================== START OF CONSTRUCTOR ====================================== //

    public RecyclerviewAdaptor(Context context, ArrayList<String> textEntry) {
        this.textEntry = textEntry;
        this.context = context;
    }

    // ======================== END OF CONSTRUCTOR ====================================== //

    // ======================== START OF FUNCTIONS ====================================== //

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(textEntry.get(position));
        holder.constraintLayout.setTag(position);
        holder.constraintLayout.setOnLongClickListener(this);
        holder.constraintLayout.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return textEntry.size();
    }

    ArrayList<String> getList() {
        return textEntry;
    }

    void updateList(ArrayList<String> list) {
        this.textEntry = list;
    }

    @Override
    public boolean onLongClick(View v) { // triggered when the recyclerview is being long-clicked
        if ((MainActivity.isVoter && MainActivity.canVote) || (!MainActivity.isVoter && MainActivity.canAnswer)) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // prevent crash on older devices
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    // ======================== END OF FUNCTIONS ====================================== //

    // ======================== START OF HELPER CLASS ====================================== //

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.conslayout);
            textView = itemView.findViewById(R.id.text);
        }
    }

    // ======================== END OF HELPER CLASS ====================================== //
}
