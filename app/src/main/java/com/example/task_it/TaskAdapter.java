package com.example.task_it;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class TaskAdapter extends RecyclerView.Adapter<ViewHolder> {
private final TaskInterface taskinterface ;
    List<Item> taskList;
    Context context;

    // Constructeur prenant la liste des tâches à afficher
    public TaskAdapter(Context context, List<Item> taskList, TaskInterface taskinterface) {
        this.taskList = taskList;
        this.context = context;
        this.taskinterface = taskinterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_task, parent, false);
        return new ViewHolder(view, taskinterface);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        //Item task = taskList.get(position);
        holder.TitreView.setText(taskList.get(position).getTaskName());
        holder.DescriptionView.setText(taskList.get(position).getTaskDescription());
        // Formater la date et l'afficher dans le TextView
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(taskList.get(position).getTaskDate());
        holder.DateView.setText(formattedDate);
            }





    @Override
    public int getItemCount() {
        return taskList.size();
    }
    public void deleteItem(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }
    // Méthode pour obtenir le DocumentSnapshot correspondant à une position donnée



}