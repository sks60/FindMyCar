package com.example.sandysaju.findmycar;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView infoPage = findViewById(R.id.infoText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            infoPage.setText(Html.fromHtml(getString(R.string.infoHTML), Html.FROM_HTML_MODE_COMPACT));
        } else {
            infoPage.setText(Html.fromHtml(getString(R.string.infoHTML)));
        }

        Button backButton = findViewById(R.id.infoBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MapsActivity.class);
                back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(back);
                onBackPressed();
            }
        });
    }


}
