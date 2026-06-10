package com.example.pointco;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewsActivity extends AppCompatActivity {

    TextView btnBack, txtCafeName, txtReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        btnBack = findViewById(R.id.btnBack);
        txtCafeName = findViewById(R.id.txtCafeName);
        txtReviews = findViewById(R.id.txtReviews);

        btnBack.setOnClickListener(v -> finish());

        String cafeName = getIntent().getStringExtra("cafeName");

        if (cafeName == null) {
            cafeName = "Kafe";
        }

        txtCafeName.setText(cafeName);

        txtReviews.setText(
                "⭐ Çok sıcak ve samimi bir ortam.\n\n" +
                        "☕ Kahveleri lezzetli, özellikle soğuk kahveleri güzel.\n\n" +
                        "💻 Çalışmak için sakin ve rahat bir yer.\n\n" +
                        "📍 Konumu ulaşım açısından kolay.\n\n" +
                        "🧡 Genel olarak tekrar gidilebilecek bir mekan."
        );
    }
}