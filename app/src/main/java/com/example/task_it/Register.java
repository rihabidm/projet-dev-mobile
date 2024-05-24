package com.example.task_it;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, nom, prenom;
    Button buttonReg;
    private FirebaseAuth auth;
    TextView textView;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        nom = findViewById(R.id.Nom);
        prenom = findViewById(R.id.Prenom);
        buttonReg = findViewById(R.id.buttonSignup);
        textView = findViewById(R.id.buttonLogin);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email, password;
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                final String nomValue = nom.getText().toString().trim();
                final String prenomValue = prenom.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nomValue) || TextUtils.isEmpty(prenomValue)) {
                    Toast.makeText(Register.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authentification de l'utilisateur
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //stockage
                                    // Create a new user with a first, middle, and last name
                                    String nomValue = String.valueOf(nom.getText());
                                    String prenomValue = String.valueOf(prenom.getText());


// Create a new user with a first, middle, and last name
                                    Map<String, Object> userdoc = new HashMap<>();
                                    userdoc.put("nom", nomValue);
                                    userdoc.put("prenom", prenomValue);
                                    userdoc.put("email", email);
                                    userdoc.put("password", password);

                                    // Enregistrer les données utilisateur dans Firestore
                                        db.collection("user").document(email)
                                                .set(userdoc)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Document utilisateur ajouté avec succès à Firestore !");
                                                        Toast.makeText(Register.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                                                        // Rediriger l'utilisateur vers l'activité de gestion des tâches
                                                        startActivity(new Intent(Register.this, alltasks.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Erreur lors de l'ajout du document utilisateur à Firestore", e);
                                                        Toast.makeText(Register.this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }
                        );
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Vérifier si l'utilisateur est déjà connecté
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), alltasks.class));
            finish();
        }
    }
}
