package com.example.task_it;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface TaskInterface {
    //outside OnCreate
    //MENU//
    boolean onCreateOptionsMenu(Menu menu);


    boolean onOptionsItemSelected(@NonNull MenuItem item);

    void onItemClick(int position);
    void onItemLongClick(int position);
}
