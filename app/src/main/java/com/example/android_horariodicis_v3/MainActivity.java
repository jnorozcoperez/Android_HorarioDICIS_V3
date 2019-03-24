package com.example.android_horariodicis_v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btHorario;
    Button btPersonalizar;
    String carrera;
    Horario dbHandler = null;
    Nap.MailService mailService = null;

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
                Intent intent = new Intent(getBaseContext(), PersonalizarActivity.class);
                startActivity(intent);
            }
        });
        //_______________ Forzar posición vertical en pantalla principal
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            if(!Nap.FileX.Exists(getFilesDir() + "/SCHDATA")) {
                //_______________ Activity para seleccionar la carrera del estudiante
                Intent carreraIntent =  new Intent(this, CarreraChooseActivity.class);
                startActivityForResult(carreraIntent, 1);
                dbHandler = new Horario(this);
            }
            else {
                //_______________ Leer archivo que identifica carrera
                carrera = Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt");
                //_______________ Mostrar ventana de carga
                Intent loadingIntent = new Intent(getBaseContext(), LoadingActivity.class);
                loadingIntent.putExtra("CARRERA", carrera);
                startActivityForResult(loadingIntent, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DescargarHorario extends AsyncTask<Void, Void, Void> {
        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //_______________ Leer correo electrónico de una carrera en específica
                mailService = new Nap.MailService(dbHandler.CheckCarrera(carrera));
                String xml = mailService.CreateXMLDoc();
                if(dbHandler != null && xml != null) {
                    Nap.FileX.Save(xml, getFilesDir() + "/SCHDATA/xmlDICIS.xml");
                    dbHandler.addItems(mailService.GetDocument(), carrera);
                }
            } catch (Exception e) {
                Toast errorToast = Toast.makeText(getApplicationContext(), "Error!, no tiene conexión a internet, el horario mostrado puede no ser el mas actualizado, le sugerimos conectarse a internet", Toast.LENGTH_LONG);
                errorToast.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //_______________ Crear folder para almacenar archivos permanentes
                File folder = new File(getFilesDir() + "/SCHDATA");
                if (folder.mkdir()) {
                    String result = data.getStringExtra("result");
                    try {
                        File file = new File(getFilesDir() + "/SCHDATA", "Carrera.txt");
                        if (file.createNewFile()) {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(result.getBytes());
                            fos.flush();
                            fos.close();
                        }
                        carrera = result;
                        new DescargarHorario().execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        finish();
                    }
                }
            } else {
                finish();
            }
        }
        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_CANCELED) {
                Toast errorToast = Toast.makeText(getApplicationContext(), "Error!, no tiene conexión a internet, el horario mostrado puede no ser el mas actualizado, le sugerimos conectarse a internet", Toast.LENGTH_LONG);
                errorToast.show();
            }
        }
    }
}
