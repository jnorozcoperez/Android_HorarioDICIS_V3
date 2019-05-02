package com.example.android_horariodicis_v3;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btHorario;
    Button btRefresh;
    Button btPersonalizar;
    Button btChooseCarrera;
    String carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        //_______________ Botón para mostrar el horario
        btHorario = findViewById(R.id.btHorario);
        btHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                Intent intent = new Intent(getBaseContext(), HorarioActivity.class);
                intent.putExtra("CARRERA", carrera);
                startActivity(intent);
            }
        });
        //_______________ Botón para personalizar horario
        btPersonalizar = findViewById(R.id.btPersonalizar);
        btPersonalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                Intent intent = new Intent(getBaseContext(), PersonalizarActivity.class);
                startActivity(intent);
            }
        });
        //_______________ Botón para actualizar horario
        btRefresh = findViewById(R.id.btRefresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                //_______________ Mostrar ventana de carga
                Intent loadingIntent = new Intent(getBaseContext(), LoadingActivity.class);
                loadingIntent.putExtra("FIRST", false);
                startActivityForResult(loadingIntent, 2);
            }
        });
        //_______________ Botón para escoger carrera
        btChooseCarrera = findViewById(R.id.btChooseCarrera);
        btChooseCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                Intent intent = new Intent(getBaseContext(), CarreraChooseActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //_______________ Forzar posición vertical en pantalla principal
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            if(!Nap.FileX.Exists(getFilesDir() + "/SCHDATA")) {
                //_______________ Activity para seleccionar la carrera del estudiante
                Intent carreraIntent =  new Intent(this, CarreraChooseActivity.class);
                startActivityForResult(carreraIntent, 1);
            }
            else {
                //_______________ Leer archivo que identifica carrera
                carrera = Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt");
                //_______________ Mostrar ventana de carga
                //Intent loadingIntent = new Intent(getBaseContext(), LoadingActivity.class);
                //loadingIntent.putExtra("FIRST", false);
                //startActivityForResult(loadingIntent, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File folder = new File(getFilesDir() + "/SCHDATA");
        boolean isFirst = !folder.exists();
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //_______________ Crear folder para almacenar archivos permanentes
                isFirst = folder.mkdir();
                carrera = data.getStringExtra("CARRERA");
                try {
                    Nap.FileX.Save(carrera, getFilesDir() + "/SCHDATA/Carrera.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }
                Intent loadingIntent = new Intent(getBaseContext(), LoadingActivity.class);
                loadingIntent.putExtra("FIRST", isFirst);
                startActivityForResult(loadingIntent, 2);
            } else if(!isFirst) {
                finish();
            }
        }
        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_CANCELED) {
                if(isFirst) {
                    if (folder.isDirectory()) {
                        String[] children = folder.list();
                        for (String aChildren : children) new File(folder, aChildren).delete();
                    }
                    folder.delete();
                }
                Toast errorToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG);
                errorToast.show();
            }
        }
    }
}