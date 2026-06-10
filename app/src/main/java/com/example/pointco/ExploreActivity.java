package com.example.pointco;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ExploreActivity extends AppCompatActivity {

    TextView btnBack, txtExploreTitle, txtExploreSub, txtApiStatus;
    Button btnLocation;
    LinearLayout cafeList;
    FusedLocationProviderClient locationClient;

    double userLat = 0;
    double userLon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        btnBack = findViewById(R.id.btnBack);
        txtExploreTitle = findViewById(R.id.txtExploreTitle);
        txtExploreSub = findViewById(R.id.txtExploreSub);
        txtApiStatus = findViewById(R.id.txtApiStatus);
        btnLocation = findViewById(R.id.btnLocation);
        cafeList = findViewById(R.id.cafeList);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        btnBack.setOnClickListener(v -> finish());

        cafeList.removeAllViews();

        btnLocation.setOnClickListener(v -> {
            cafeList.removeAllViews();
            getUserLocationAndFindCafes();
        });
    }

    private void getUserLocationAndFindCafes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100
            );
            return;
        }

        txtApiStatus.setText("Konum algılanıyor...");

        locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLon = location.getLongitude();

                txtApiStatus.setText("Konumun başarıyla alındı.");
                txtExploreSub.setText("Konumuna yakın kafeler listeleniyor");

                cafeList.removeAllViews();

                addCafeCard(
                        "📍 Benim Konumum",
                        "Şu an bulunduğun konumdan kafeler listeleniyor.",
                        "Konum: " + userLat + ", " + userLon
                );

                fetchCafesFromOverpass(userLat, userLon);

            } else {
                txtApiStatus.setText("Konum alınamadı. Emülatörde Location ayarla.");
                showDemoCafes();
            }
        }).addOnFailureListener(e -> {
            txtApiStatus.setText("Konum alınırken hata oluştu.");
            showDemoCafes();
        });
    }

    private void fetchCafesFromOverpass(double lat, double lon) {
        new Thread(() -> {
            try {
                String query =
                        "[out:json][timeout:25];" +
                                "(" +
                                "node[\"amenity\"=\"cafe\"](around:3000," + lat + "," + lon + ");" +
                                "way[\"amenity\"=\"cafe\"](around:3000," + lat + "," + lon + ");" +
                                "node[\"amenity\"=\"restaurant\"](around:3000," + lat + "," + lon + ");" +
                                "way[\"amenity\"=\"restaurant\"](around:3000," + lat + "," + lon + ");" +
                                ");" +
                                "out center tags 20;";

                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlText = "https://overpass-api.de/api/interpreter?data=" + encodedQuery;

                URL url = new URL(urlText);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray elements = jsonObject.getJSONArray("elements");

                runOnUiThread(() -> {
                    if (elements.length() == 0) {
                        txtApiStatus.setText("Yakında kayıtlı kafe bulunamadı. Demo öneriler gösteriliyor.");
                        showDemoCafes();
                        return;
                    }

                    txtApiStatus.setText("Konumuna göre gerçek mekanlar listelendi.");

                    for (int i = 0; i < elements.length(); i++) {
                        try {
                            JSONObject item = elements.getJSONObject(i);
                            JSONObject tags = item.optJSONObject("tags");

                            if (tags == null) continue;

                            String name = tags.optString("name", "İsimsiz Mekan");
                            String amenity = tags.optString("amenity", "cafe");

                            String type;
                            if (amenity.equals("restaurant")) {
                                type = "Restoran / kafe tarzı mekan";
                            } else {
                                type = "Kafe";
                            }

                            double cafeLat;
                            double cafeLon;

                            if (item.has("lat") && item.has("lon")) {
                                cafeLat = item.optDouble("lat");
                                cafeLon = item.optDouble("lon");
                            } else if (item.has("center")) {
                                JSONObject center = item.optJSONObject("center");
                                cafeLat = center.optDouble("lat");
                                cafeLon = center.optDouble("lon");
                            } else {
                                cafeLat = lat;
                                cafeLon = lon;
                            }

                            float[] results = new float[1];

                            Location.distanceBetween(
                                    lat,
                                    lon,
                                    cafeLat,
                                    cafeLon,
                                    results
                            );

                            float distanceKm = results[0] / 1000f;

                            String distanceText = String.format(
                                    "📍 %.1f km uzaklıkta • %s",
                                    distanceKm,
                                    type
                            );

                            addCafeCard(
                                    name,
                                    "Gerçek konum verisine göre yakınındaki mekan.",
                                    distanceText
                            );

                        } catch (Exception ignored) {
                        }
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    txtApiStatus.setText("API bağlantısı başarısız oldu. Demo öneriler gösteriliyor.");
                    showDemoCafes();
                });
            }
        }).start();
    }

    private void showDemoCafes() {
        cafeList.removeAllViews();

        addCafeCard(
                "📍 Benim Konumum",
                "Konum alınamadığı için demo öneriler gösteriliyor.",
                "Konum bilgisi yok"
        );

        addCafeCard("Soulmate Coffee", "Sıcak ortam, kahve ve çalışma için uygun.", "⭐ 4.8 • 📍 0.3 km");
        addCafeCard("Caribou Coffee", "Sessiz ortam ve kahve molası için ideal.", "⭐ 4.7 • 📍 0.5 km");
        addCafeCard("Starbucks", "Popüler kahve zinciri, hızlı mola için uygun.", "⭐ 4.6 • 📍 0.7 km");
    }

    private void addCafeCard(String name, String description, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 22, 28, 22);
        card.setBackgroundResource(R.drawable.card_bg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 22);
        card.setLayoutParams(cardParams);

        TextView txtName = new TextView(this);
        txtName.setText(name);
        txtName.setTextSize(18);
        txtName.setTextColor(getResources().getColor(R.color.black));
        txtName.setTypeface(null, Typeface.BOLD);

        TextView txtDetail = new TextView(this);
        txtDetail.setText(detail);
        txtDetail.setTextSize(14);
        txtDetail.setTextColor(getResources().getColor(R.color.gray));
        txtDetail.setPadding(0, 10, 0, 0);

        TextView txtDesc = new TextView(this);
        txtDesc.setText(description);
        txtDesc.setTextSize(14);
        txtDesc.setTextColor(getResources().getColor(R.color.gray));
        txtDesc.setPadding(0, 8, 0, 0);

        card.addView(txtName);
        card.addView(txtDetail);
        card.addView(txtDesc);

        cafeList.addView(card);
    }
}