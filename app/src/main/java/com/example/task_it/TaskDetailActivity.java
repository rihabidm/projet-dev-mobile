package com.example.task_it;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.Timestamp;

public class TaskDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView titleTextView;
    TextView descriptionTextView;
    TextView dateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ///MENU///
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ////UPDATE task//
        Button buttonSave = findViewById(R.id.button_save_task);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), alltasks.class);
                startActivity(intent);
            }
        });
        // Récupérer les données de la tâche à afficher depuis l'intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        ////
        Timestamp date = (Timestamp) getIntent().getSerializableExtra("dueDate");

        // Afficher les données dans les vues appropriées
         titleTextView = findViewById(R.id.task_title);
         descriptionTextView = findViewById(R.id.task_description);
         dateTextView = findViewById(R.id.task_date);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText((CharSequence) date);
    }

    private void UpdateTask() {
        // Récupérer les nouvelles valeurs saisies par l'utilisateur
    String newTitle = titleTextView.getText().toString();
    String newDesc = descriptionTextView.getText().toString();
    String newDate = dateTextView.getText().toString();

    // Récupérer l'utilisateur connecté
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
        String userEmail = currentUser.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert userEmail != null;

        // Récupérer l'ID de la tâche à partir de l'Intent
        String taskId = getIntent().getStringExtra("TASK_ID");

        // Référence au document de l'utilisateur
        DocumentReference userDocRef = db.collection("users").document(userEmail);

        // Référence à la tâche spécifique dans la sous-collection "tasks"
            assert taskId != null;
            DocumentReference taskDocRef = userDocRef.collection("tasks").document(taskId);

        // Mettre à jour les champs de la tâche avec les nouvelles valeurs
        taskDocRef.update("title", newTitle, "description", newDesc, "date", newDate)
                .addOnSuccessListener(aVoid -> {
                    // Mise à jour réussie
                    Toast.makeText(TaskDetailActivity.this, "Tâche mise à jour avec succès", Toast.LENGTH_SHORT).show();
                    // Redirection vers l'activité AllTasks ou une autre activité
                    Intent intent = new Intent(TaskDetailActivity.this, alltasks.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Erreur lors de la mise à jour
                    Toast.makeText(TaskDetailActivity.this, "Erreur lors de la mise à jour de la tâche", Toast.LENGTH_SHORT).show();
                });
    }
}
    //MENU toolbar//

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }

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

}
