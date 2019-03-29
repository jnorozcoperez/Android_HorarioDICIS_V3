package com.example.android_horariodicis_v3;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Objects;

public class CarreraChooseActivity extends AppCompatActivity {
    Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera_choose);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dropdown = findViewById(R.id.ddCarrera);
        String[] items = new String[]{"Artes Digitales", "Comunicaciones y Electrónica", "Eléctrica", "Gestión Empresarial", "Mecánica", "Mecatrónica", "Sistemas Computacionales"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        Button next = findViewById(R.id.btNext);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("CARRERA", dropdown.getSelectedItem().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

        });
    }
}
