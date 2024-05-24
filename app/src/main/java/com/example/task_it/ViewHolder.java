package com.example.task_it;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;

public class ViewHolder extends RecyclerView.ViewHolder {


    ImageView imageView;
    public TextView TitreView;
    public TextView DescriptionView;
    public TextView DateView;

    public ViewHolder(@NonNull View itemView,TaskInterface taskinterface) {
        super(itemView);
         DateView = itemView.findViewById(R.id.date);
        TitreView = itemView.findViewById(R.id.titre);
        DescriptionView = itemView.findViewById(R.id.description);
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if( taskinterface != null) {
int pos = getAdapterPosition();
if(pos != RecyclerView.NO_POSITION){
    taskinterface.onItemClick(pos);
}

                }
            }

        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
          if( taskinterface != null) {
              int pos = getAdapterPosition();
              if(pos != RecyclerView.NO_POSITION){
                  taskinterface.onItemLongClick(pos);
              }}
    return true;
                                            }
                                        }

        );
    }
}