package com.capstone.sharity.driver.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.capstone.sharity.driver.R;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.teliver.sdk.models.Task;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter .ViewHolder>{

    //Create an Interface
    public interface OnItemClickListener {
        void onItemCLick(Task task);
    }

    Context context;
    List<Task> tasksList;
    OnItemClickListener onItemClickListener;

    public TasksRecyclerViewAdapter(Context context, List<Task> tasksList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.tasksList = tasksList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setTasksList(List<Task> tasksList) {
        this.tasksList = tasksList;
    }

    @NonNull
    public TasksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_tasks, parent, false);

        return new TasksRecyclerViewAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull TasksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(tasksList.get(position), onItemClickListener);
    }

    public int getItemCount() {
        return tasksList != null ? tasksList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStatus, textViewID, textViewType, textViewTime, textViewName, textviewAddress;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewStatus= itemView.findViewById(R.id.textViewStatus);
            textViewID= itemView.findViewById(R.id.textViewID);
            textViewType= itemView.findViewById(R.id.textViewType);
            textViewTime= itemView.findViewById(R.id.textViewTime);
            textViewName= itemView.findViewById(R.id.textViewName);
            textviewAddress= itemView.findViewById(R.id.textViewAddress);
            btnView = itemView.findViewById(R.id.btnView);
        }

        //Binding
        public void bind(final Task task, final OnItemClickListener onItemClickListener){
            textViewID.setText(task.getOrderId());
            textViewStatus.setText(task.getStatus());
            textViewType.setText(Objects.equals(task.getType(), "1") ? "P" : "D");
            textViewTime.setText(task.getUpdatedAt());
            textViewName.setText(task.getPickUp().getCustomer().getName());
            textviewAddress.setText(task.getPickUp().getAddress());
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemCLick(task);
                }
            });
        }
    }

}
