package com.example.task_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    private TextView nomTextView;
    private TextView prenomTextView;
    private TextView emailTextView;
    private TextView passwordTextView;
    CircleImageView profileImageView;
    private static final int REQUEST_IMAGE_PICK = 100;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        ////UPDATE PROFILE//
        Button buttonSave = findViewById(R.id.button_save_profil);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          UpdateProfil();
            }
                                      });
        //// modifier la photo de profil////
        TextView editPhotoTextView = findViewById(R.id.editphoto);
        editPhotoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir la galerie pour sélectionner une nouvelle photo
                 Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_IMAGE_PICK);
                // Démarrer l'activité des avatars
            }
        });

        ///MENU///
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
            // Initialisation des vues
        nomTextView = findViewById(R.id.Nom);
        emailTextView = findViewById(R.id.Email);
        prenomTextView = findViewById(R.id.Prenom);
        passwordTextView = findViewById(R.id.Password);
         profileImageView = findViewById(R.id.profile_image);
           // Récupérer l'utilisateur connecté
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                    String email = document.getString("email");
                                    String password = document.getString("password");
                                    // Afficher les données dans la vue activity_profil
                                    nomTextView.setText(nom);
                                    emailTextView.setText(email);
                                    prenomTextView.setText(prenom);
                                    passwordTextView.setText(password);



                }

}}
                    });

        }
    }

    //end of OnCreate///

    ///PHOTO DE PROFIL////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Afficher l'image dans le CircleImageView
            afficherImage(selectedImageUri);
            updateProfilePhotoInFirestore(selectedImageUri);
        }
    }

    private void updateProfilePhotoInFirestore(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert userEmail != null;
            DocumentReference userDocRef = db.collection("users").document(userEmail);

            // Mettre à jour le champ de la photo de profil dans Firestore avec l'URI de l'image sélectionnée
            userDocRef.update("profile_photo", imageUri.toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Succès de la mise à jour
                            Toast.makeText(ProfilActivity.this, "Photo de profil mise à jour avec succès", Toast.LENGTH_SHORT).show();
                            // Redirection vers l'activité Profile
                            Intent intent = new Intent(ProfilActivity.this, ProfilActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Échec de la mise à jour
                            Toast.makeText(ProfilActivity.this, "Erreur lors de la mise à jour de la photo de profil", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void afficherImage(Uri imageUri) {
        Picasso.get().load(imageUri).into(profileImageView);
    }
    ////UPDATE PROFILE//
    private void UpdateProfil() {
        // Récupérer les nouvelles valeurs saisies par l'utilisateur
        String newNom = nomTextView.getText().toString();
        String newPrenom = prenomTextView.getText().toString();
        String newEmail = emailTextView.getText().toString();
        String newPassword = passwordTextView.getText().toString();

        // Récupérer l'utilisateur connecté
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert userEmail != null;
            DocumentReference userDocRef = db.collection("user").document(userEmail);

            // Mettre à jour les champs du document Firestore avec les nouvelles valeurs
            userDocRef.update("nom", newNom);
            userDocRef.update("prenom", newPrenom);
            userDocRef.update("email", newEmail);
            userDocRef.update("password", newPassword)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Mise à jour réussie
                            Toast.makeText(ProfilActivity.this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                            // Redirection vers l'activité AllTasks
                            Intent intent = new Intent(ProfilActivity.this, alltasks.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Erreur lors de la mise à jour
                            Toast.makeText(ProfilActivity.this, "Profil mis à jour avec succés", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    ////MENU////

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

}