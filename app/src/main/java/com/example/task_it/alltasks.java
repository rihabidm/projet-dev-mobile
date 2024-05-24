package com.example.task_it;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class alltasks extends AppCompatActivity implements TaskInterface {
    //Button buttonLogout;
    List<Item> taskList;
    RecyclerView recyclerView;
    SearchView searchview;
    //ImageView menu;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alltasks);
        ///MENU///
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //RECHERCHE
        searchview = findViewById(R.id.search_view);
        searchview.clearFocus();
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
        ///////DELETE////////

        // Attacher l'ItemTouchHelper au RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /////// LOGOUT///////
       /* buttonLogout = findViewById(R.id.btnLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déconnexion de l'utilisateur actuel
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });*/

        // Redirection vers l'activité AddTask
        FloatingActionButton addTaskButton = findViewById(R.id.addtask);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirection vers l'activité AddTask
                Intent intent = new Intent(getApplicationContext(), addtask.class);
                startActivity(intent);
            }
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Référence au document de l'utilisateur connecté dans la collection "users"
            assert userEmail != null;
            DocumentReference userDocRef = db.collection("user").document(userEmail);

            userDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Récupération du nom et prénom de l'utilisateur connecté
                                    String nom = document.getString("nom");
                                    String prenom = document.getString("prenom");

                                    // Mise à jour de la Toolbar avec le nom et prénom de l'utilisateur
                                    toolbar = findViewById(R.id.toolbar);
                                    toolbar.setTitle("Hello " + nom + " " + prenom);
                                }
                            }
                        }
                    });

            // Récupérez les tâches associées à l'utilisateur connecté depuis Firestore
            recyclerView = findViewById(R.id.recycler_view);
            taskList = new ArrayList<>();
            userDocRef.collection("tasks")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Récupérez les données de chaque document et créez un objet Task
                                String title = document.getString("title");
                                String description = document.getString("description");
                                Timestamp dueDateTimestamp = document.getTimestamp("dueDate");
                                // Convertir le Timestamp en Date si nécessaire
                                assert dueDateTimestamp != null;
                                Date dueDate = dueDateTimestamp.toDate();


                                Item taskItem;
                                taskItem = new Item(title, description, dueDate);
                                taskList.add(taskItem);

                            }
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(new TaskAdapter(getApplicationContext(), taskList, this));

                    });



        }

    }
//////////////////////outside OnCreate////////////////////////////
//MENU toolbar//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        /////////LOGOUT////
        if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id == R.id.profil){
            Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //CLICK SUR UN ITEM DE RECYCLER VIEW ET REDIRIGER VERS LA PAGE DE DETAILS DUN SEUL TASK
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent (alltasks.this ,TaskDetailActivity.class);
        intent.putExtra("title", taskList.get(position).getTaskName());
        intent.putExtra("description", taskList.get(position).getTaskDescription());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(getApplicationContext(), taskList, this));
        startActivity(intent);
    }
    //// LongClick sur un item du recyclerview pour afficher un menu d'options/////
    @Override
    public void onItemLongClick(int position) {
        // Afficher un menu d'options (PopupMenu) pour l'élément à la position donnée
        PopupMenu popupMenu = new PopupMenu(this, recyclerView.getChildAt(position));
        popupMenu.inflate(R.menu.item_options_menu); // Définir le menu d'options
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                /////////DELETE////
                if(id == R.id.delete_option){
                    TaskAdapter taskadapter = new TaskAdapter(getApplicationContext(), taskList, alltasks.this);
                    // Supprimer l'élément de la liste RecyclerView
                    taskadapter.deleteItem(position);
                    return true;}
                /////UPDATE//////
                if(id == R.id.update_option){


                    return true;}
                ////DETAILS///
                if(id == R.id.details_option){
                    Intent intent = new Intent (alltasks.this ,TaskDetailActivity.class);
                    intent.putExtra("title", taskList.get(position).getTaskName());
                    intent.putExtra("description", taskList.get(position).getTaskDescription());
                    recyclerView.setLayoutManager(new LinearLayoutManager((alltasks.this)));
                    recyclerView.setAdapter(new TaskAdapter(getApplicationContext(), taskList, alltasks.this));
                    startActivity(intent);
                    return true;}

                return false;
            }});
        popupMenu.show();

    }

    //////RECHERCHE TASK/////////
    private void filterList(String text) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : taskList){
            if(item.getTaskName().toLowerCase().contains(text.toLowerCase()) || item.getTaskDescription().toLowerCase().contains(text.toLowerCase()) )
            {filteredList.add(item);}
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No Date found", Toast.LENGTH_SHORT).show();
        }
        else {
            TaskAdapter filteredAdapter = new TaskAdapter(getApplicationContext(), filteredList, new TaskInterface() {
                @Override
                public boolean onCreateOptionsMenu(Menu menu) {
                    MenuInflater menuInflater = new MenuInflater(alltasks.this);
                    menuInflater.inflate(R.menu.toolbar_menu,menu);
                    return true;
                }

                @Override
                public boolean onOptionsItemSelected(@NonNull MenuItem item) {

                    int id = item.getItemId();
                    /////////LOGOUT////
                    if(id == R.id.logout){
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    if(id == R.id.profil){
                        Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
                        startActivity(intent);
                        return true;
                    }

                    return alltasks.super.onOptionsItemSelected(item);
                }

                ////// dans la recherche de task /////
                @Override
                public void onItemClick(int position) {
                    // Gérer le clic sur un élément dans la liste filtrée
                    Intent intent = new Intent(alltasks.this, TaskDetailActivity.class);
                    intent.putExtra("title", filteredList.get(position).getTaskName());
                    intent.putExtra("description", filteredList.get(position).getTaskDescription());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(int position) {
                    // Afficher un menu d'options (PopupMenu) pour l'élément à la position donnée
                    PopupMenu popupMenu = new PopupMenu(alltasks.this, recyclerView.getChildAt(position));
                    popupMenu.inflate(R.menu.item_options_menu); // Définir le menu d'options
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            /////////DELETE////
                            if(id == R.id.delete_option){
                                TaskAdapter taskadapter = new TaskAdapter(getApplicationContext(), taskList, alltasks.this);
                                // Supprimer l'élément de la liste RecyclerView
                                taskadapter.deleteItem(position);
                                return true;}
                            /////UPDATE//////
                            if(id == R.id.update_option){


                                return true;}
                            ////DETAILS///
                            if(id == R.id.details_option){
                                Intent intent = new Intent (alltasks.this ,TaskDetailActivity.class);
                                intent.putExtra("title", taskList.get(position).getTaskName());
                                intent.putExtra("description", taskList.get(position).getTaskDescription());
                                recyclerView.setAdapter(new TaskAdapter(getApplicationContext(), taskList, alltasks.this));
                                startActivity(intent);
                                return true;}

                            return false;
                        }});
                    popupMenu.show();

                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(filteredAdapter);
        }

    }

    /////DELETE TASK by swipping/////
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                TaskAdapter taskadapter = new TaskAdapter(getApplicationContext(), taskList, alltasks.this);
                // Supprimer l'élément de la liste RecyclerView
                taskadapter.deleteItem(position);
                ///....mettre à jour la vue après suppression du recycler view...////
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(new TaskAdapter(getApplicationContext(), taskList, alltasks.this));
                // Supprimer l'élément correspondant de Firestore
                // on utilise une liste de DocumentSnapshot
                List<DocumentSnapshot> taskListDoc = new ArrayList<>();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Référence au document de l'utilisateur connecté dans la collection "users"
                    assert userEmail != null;
                    DocumentReference userDocRef = db.collection("user").document(userEmail);
                    userDocRef.collection("tasks")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        taskListDoc.add(document);
                                    }

                                    if (position >= 0 && position < taskList.size()) {

                                        // Renvoyer le DocumentSnapshot correspondant à la position donnée
                                        DocumentSnapshot snapshot = taskListDoc.get(position);

                                        if (snapshot != null) {
                                            snapshot.getReference().delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Élément supprimé avec succès dans Firestore
                                                            Log.d(TAG, "DocumentSnapshot successfully deleted from Firestore!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Erreur lors de la suppression de l'élément dans Firestore
                                                            Log.w(TAG, "Error deleting document from Firestore", e);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }}};
}
////////END out of OnCreate////////