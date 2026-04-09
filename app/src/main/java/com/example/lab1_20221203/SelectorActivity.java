package com.example.lab1_20221203;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selector);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        ((TextView) findViewById(R.id.nombre_display)).setText(nombre);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_stats) {
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra("nombre", getIntent().getStringExtra("nombre"));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void jugarFacil(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("dificultad", "facil");
        intent.putExtra("nombre", getIntent().getStringExtra("nombre"));
        startActivity(intent);
    }

    public void jugarDificil(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("dificultad", "dificil");
        intent.putExtra("nombre", getIntent().getStringExtra("nombre"));
        startActivity(intent);
    }

    public void jugarAleatorio(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("dificultad", "aleatorio");
        intent.putExtra("nombre", getIntent().getStringExtra("nombre"));
        startActivity(intent);
    }

}