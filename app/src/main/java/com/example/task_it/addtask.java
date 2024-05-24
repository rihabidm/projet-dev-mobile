package com.example.task_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addtask extends AppCompatActivity {

    private static final String TAG = "addtask";

    private EditText titreEditText;
    private EditText descriptionEditText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        ///MENU///
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ////////
        titreEditText = findViewById(R.id.Titre);
        descriptionEditText = findViewById(R.id.Description);
        datePicker = findViewById(R.id.date);
        timePicker = findViewById(R.id.time);
        Button okButton = findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskToFirestore();
            }
        });
    }
    //end of Oncreate//

//MENU//

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

        return super.onOptionsItemSelected(item);
    }
    private void addTaskToFirestore() {
        String titre = titreEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();

        int hourOfDay = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
        Date dueDate = calendar.getTime();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Référence au document de l'utilisateur connecté dans la collection "user"
            assert userEmail != null;
            DocumentReference userDocRef = db.collection("user").document(userEmail);

            // Créer une nouvelle tâche
            Map<String, Object> task = new HashMap<>();
            task.put("title", titre);
            task.put("description", description);
            task.put("dueDate", new Timestamp(dueDate));

            // Ajouter la nouvelle tâche à la sous-collection "tasks" du document utilisateur
            userDocRef.collection("tasks").add(task)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Tâche ajoutée avec l'ID : " + documentReference.getId());
                            // Redirection vers l'activité AllTasks
                            Intent intent = new Intent(addtask.this, alltasks.class);
                            startActivity(intent);
                            finish(); // Ferme l'activité une fois la tâche ajoutée avec succès
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Erreur lors de l'ajout de la tâche", e);
                        }
                    });
        }
    }
}
