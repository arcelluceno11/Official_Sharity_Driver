package com.capstone.sharity.driver.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.model.Tasks;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter .ViewHolder>{

    //Create an Interface
    public interface OnItemClickListener {
        void onItemCLick(Tasks tasks);
    }

    Context context;
    ArrayList<Tasks> tasksArrayList;
    OnItemClickListener onItemClickListener;

    public TasksRecyclerViewAdapter(Context context, ArrayList<Tasks> tasksArrayList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.tasksArrayList = tasksArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    public TasksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_tasks, parent, false);

        return new TasksRecyclerViewAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull TasksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(tasksArrayList.get(position), onItemClickListener);
    }

    public int getItemCount() {
        return tasksArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStatus, textViewType, textViewTime, textViewName, textviewAddress;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewStatus= itemView.findViewById(R.id.textViewStatus);
            textViewType= itemView.findViewById(R.id.textViewType);
            textViewTime= itemView.findViewById(R.id.textViewTime);
            textViewName= itemView.findViewById(R.id.textViewName);
            textviewAddress= itemView.findViewById(R.id.textViewAddress);
            btnView = itemView.findViewById(R.id.btnView);

        }

        //Binding
        public void bind(final Tasks tasks, final OnItemClickListener onItemClickListener){

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemCLick(tasks);
                }
            });


        }
    }

}
