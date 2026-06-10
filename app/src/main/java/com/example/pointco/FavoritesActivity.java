package com.example.pointco;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    TextView btnBack, txtFavoriteCafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        btnBack = findViewById(R.id.btnBack);
        txtFavoriteCafe = findViewById(R.id.txtFavoriteCafe);

        btnBack.setOnClickListener(v -> finish());

        SharedPreferences prefs =
                getSharedPreferences(
                        "PointCoPrefs",
                        MODE_PRIVATE
                );

        Set<String> favorites =
                prefs.getStringSet(
                        "favoriteCafes",
                        null
                );

        if (favorites == null || favorites.isEmpty()) {

            txtFavoriteCafe.setText(
                    "🧡 Henüz favori yok"
            );

        } else {

            StringBuilder builder =
                    new StringBuilder();

            for (String cafe : favorites) {
                builder.append("🧡 ")
                        .append(cafe)
                        .append("\n\n");
            }

            txtFavoriteCafe.setText(
                    builder.toString()
            );
        }
    }
}