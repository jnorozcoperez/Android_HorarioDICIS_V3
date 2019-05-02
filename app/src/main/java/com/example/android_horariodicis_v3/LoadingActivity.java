package com.example.android_horariodicis_v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Objects;

public class LoadingActivity extends AppCompatActivity {
    boolean firstTime = false;
    String carrera;
    Horario dbHandler = null;
    Nap.MailService mailService = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        firstTime = getIntent().getBooleanExtra("FIRST", false);
        //_______________ Forzar pantalla completa
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_loading);
        //_______________ Recibir variable de otra activity
        carrera = Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt");
        //_______________ Crear base de datos
        dbHandler = new Horario(this);
        //_______________ Leer correo electrónico
        new DescargarHorario().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DescargarHorario extends AsyncTask<Void, Void, Void> {
        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //_______________ Leer correo electrónico de una carrera en específica
                mailService = new Nap.MailService(Nap.Carrera.Check(carrera), getFilesDir() + "/SCHDATA");
                String xml = mailService.GetXML();
                if (dbHandler != null && xml != null && !xml.equals("")) {
                    Nap.FileX.Save(xml, getFilesDir() + "/SCHDATA/xmlDICIS.xml");
                    if(!firstTime) dbHandler.onUpgrade(dbHandler.getWritableDatabase(), 1, 2);
                    dbHandler.addItems(mailService.GetDocument(), carrera);
                    setResult(Activity.RESULT_OK);
                }
            } catch (Exception e) {
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
