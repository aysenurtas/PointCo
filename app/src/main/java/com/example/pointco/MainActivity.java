package com.example.pointco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView navExplore, navFavorites, navProfile;
    TextView heartFavorite, heartCaribou, heartStarbucks;
    TextView reviewSoulmate, reviewCaribou, reviewStarbucks;
    TextView txtSeeAll;

    EditText editSearch;
    View categoryCoffee, categoryDessert, categoryStudy;

    Button btnDiscover;

    SharedPreferences prefs;
    Set<String> favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("PointCoPrefs", MODE_PRIVATE);
        favorites = new HashSet<>(prefs.getStringSet("favoriteCafes", new HashSet<>()));

        navExplore = findViewById(R.id.navExplore);
        navFavorites = findViewById(R.id.navFavorites);
        navProfile = findViewById(R.id.navProfile);

        btnDiscover = findViewById(R.id.btnDiscover);
        txtSeeAll = findViewById(R.id.txtSeeAll);
        editSearch = findViewById(R.id.editSearch);

        categoryCoffee = findViewById(R.id.categoryCoffee);
        categoryDessert = findViewById(R.id.categoryDessert);
        categoryStudy = findViewById(R.id.categoryStudy);

        heartFavorite = findViewById(R.id.heartFavorite);
        heartCaribou = findViewById(R.id.heartCaribou);
        heartStarbucks = findViewById(R.id.heartStarbucks);

        reviewSoulmate = findViewById(R.id.reviewSoulmate);
        reviewCaribou = findViewById(R.id.reviewCaribou);
        reviewStarbucks = findViewById(R.id.reviewStarbucks);

        updateHeartTexts();

        navExplore.setOnClickListener(v -> openExplore("Tüm Kafeler"));
        btnDiscover.setOnClickListener(v -> openExplore("Yakındaki Kafeler"));
        txtSeeAll.setOnClickListener(v -> openExplore("Tüm Kafeler"));

        categoryCoffee.setOnClickListener(v -> openExplore("Kahve"));
        categoryDessert.setOnClickListener(v -> openExplore("Tatlı"));
        categoryStudy.setOnClickListener(v -> openExplore("Çalışma"));

        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = editSearch.getText().toString().trim();

                if (query.isEmpty()) {
                    Toast.makeText(this, "Lütfen kafe adı yaz", Toast.LENGTH_SHORT).show();
                } else {
                    openExplore("Arama: " + query);
                }

                return true;
            }
            return false;
        });

        navFavorites.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class))
        );

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class))
        );

        heartFavorite.setOnClickListener(v ->
                toggleFavorite("Soulmate Coffee", heartFavorite)
        );

        heartCaribou.setOnClickListener(v ->
                toggleFavorite("Caribou Coffee", heartCaribou)
        );

        heartStarbucks.setOnClickListener(v ->
                toggleFavorite("Starbucks", heartStarbucks)
        );

        reviewSoulmate.setOnClickListener(v ->
                openReviews("Soulmate Coffee")
        );

        reviewCaribou.setOnClickListener(v ->
                openReviews("Caribou Coffee")
        );

        reviewStarbucks.setOnClickListener(v ->
                openReviews("Starbucks")
        );
    }

    private void openExplore(String filterName) {
        Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
        intent.putExtra("filterName", filterName);
        startActivity(intent);
    }

    private void toggleFavorite(String cafeName, TextView heartView) {
        if (favorites.contains(cafeName)) {
            favorites.remove(cafeName);
            heartView.setText("♡");
            Toast.makeText(this, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(cafeName);
            heartView.setText("♥");
            Toast.makeText(this, "Favorilere eklendi 🧡", Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("favoriteCafes", favorites).apply();
    }

    private void updateHeartTexts() {
        heartFavorite.setText(favorites.contains("Soulmate Coffee") ? "♥" : "♡");
        heartCaribou.setText(favorites.contains("Caribou Coffee") ? "♥" : "♡");
        heartStarbucks.setText(favorites.contains("Starbucks") ? "♥" : "♡");
    }

    private void openReviews(String cafeName) {
        Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
        intent.putExtra("cafeName", cafeName);
        startActivity(intent);
    }
}