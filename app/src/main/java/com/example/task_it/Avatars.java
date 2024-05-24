package com.example.task_it;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.hdodenhof.circleimageview.CircleImageView;

public class Avatars extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatars);
        CircleImageView avatar1 = findViewById(R.id.avatar1);
        CircleImageView avatar2 = findViewById(R.id.avatar2);
        CircleImageView avatar3 = findViewById(R.id.avatar3);
        ////Valider l'avatar///
        Button btnValider = findViewById(R.id.btn_valider);

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajouter la photo sélectionnée au Firestore
                // Utilisez ici la méthode ou la fonction que vous avez pour ajouter une photo au Firestore

                // Rediriger l'utilisateur vers l'activité Profile
                Intent intent = new Intent(Avatars.this, ProfilActivity.class);
                startActivity(intent);
                finish(); // Facultatif : terminer cette activité pour empêcher l'utilisateur de revenir en arrière
            }
        });

        // Écouteurs de clic pour chaque avatar
        avatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedAvatarUri = "URI_de_votre_avatar_1";
                returnSelectedAvatarUri(selectedAvatarUri);
            }
        });

        avatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedAvatarUri = "URI_de_votre_avatar_2";
                returnSelectedAvatarUri(selectedAvatarUri);
            }
        });
        avatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedAvatarUri = "URI_de_votre_avatar_3";
                returnSelectedAvatarUri(selectedAvatarUri);
            }
        });
    }
    private void returnSelectedAvatarUri(String selectedAvatarUri) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_avatar_uri", selectedAvatarUri);
        setResult(Activity.RESULT_OK, resultIntent);
        finish(); // Terminer cette activité et revenir à l'activité principale
    }
}