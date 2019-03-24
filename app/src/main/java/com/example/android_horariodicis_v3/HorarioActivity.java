package com.example.android_horariodicis_v3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HorarioActivity extends AppCompatActivity {
    static final int REQUEST_CREATEPDF = 112;
    static final String filePDFName = "HorarioDICIS";
    Button btBack;
    Button btPDF;
    String carrera;
    String html;
    Horario db;

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private boolean hasPermission(Context context, String... permissions) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for(String permission : permissions) {
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Nap.ListView.Cursos_item> GetArrayItems() {
        ArrayList<Nap.ListView.Cursos_item> lvItems = new ArrayList<>();
        String carrera = db.CheckCarrera(getIntent().getStringExtra("CARRERA"));
        Cursor cursor = db.viewData(carrera);
        //_________________ Leer el horario
        if(cursor.getCount() != 0) {
            //_______________ Recorrer los datos con el objeto cursor
            while(cursor.moveToNext()) {
                //_______________ Agregar un curso al ArrayList diseñado para contener el horario
                lvItems.add(new Nap.ListView.Cursos_item(false, Nap.Cursor.GetID(cursor, "Id"), Nap.Cursor.GetElement(cursor, "HorasSem"), Nap.Cursor.GetElement(cursor, "UnidadDeAprendizaje"),
                        Nap.Cursor.GetElement(cursor, "AreaDeLaUda"), Nap.Cursor.GetElement(cursor, "Clave"),
                        Nap.Cursor.GetElement(cursor, "Lun"), Nap.Cursor.GetElement(cursor, "Mar"), Nap.Cursor.GetElement(cursor, "Mie"),
                        Nap.Cursor.GetElement(cursor, "Jue"), Nap.Cursor.GetElement(cursor, "Vie"), Nap.Cursor.GetElement(cursor, "Sab"),
                        Nap.Cursor.GetElement(cursor, "Grupo"), Nap.Cursor.GetElement(cursor, "Aula"), Nap.Cursor.GetElement(cursor, "Profesor")));
            }
        }
        return lvItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        //_______________ Crear base de datos
        db = new Horario(this);
        //_______________ Obtener carrera
        carrera = getIntent().getStringExtra("CARRERA");
        //_______________ Crear listView
        ListView lvitemX = findViewById(R.id.listViewCursos);
        //_______________ Crear objeto listView personalizada para los cursos
        Nap.ListView.Cursos lvCursoX = new Nap.ListView.Cursos(this, GetArrayItems());
        //_______________ Vaciar todos los datos de los cursos a la listView
        lvitemX.setAdapter(lvCursoX);
        //_______________ Botón para regresar
        btBack = findViewById(R.id.btHBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //_______________ Botón para generar PDF
        btPDF = findViewById(R.id.btPDF);
        btPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String xslt = convertStreamToString(getResources().openRawResource(R.raw.xsltdicisv));
                try {
                    Nap.FileX.Save(xslt, getFilesDir() + "/SCHDATA/xsltDICIS.xslt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                html = Nap.XML.ToHTML(new File(getFilesDir() + "/SCHDATA/xmlDICIS.xml"), new File(getFilesDir() + "/SCHDATA/xsltDICIS.xslt"));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermission(HorarioActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(HorarioActivity.this, PERMISSIONS, REQUEST_CREATEPDF);
                    }
                    else if(Nap.PDF.Create.FromHTML(Nap.FileX.RenameIfExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", 0), html)) {
                        Nap.Notification.Show_ClickFile(getApplicationContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", "HorarioDICIS", "Archivo descargado");
                    }
                }
                else {
                    if(Nap.PDF.Create.FromHTML(Nap.FileX.RenameIfExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", 0), html)) {
                        Nap.Notification.Show_ClickFile(getApplicationContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", "HorarioDICIS", "Archivo descargado");
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CREATEPDF: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Nap.PDF.Create.FromHTML(Nap.FileX.RenameIfExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", 0), html)) {
                        Nap.Notification.Show_ClickFile(getApplicationContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", "HorarioDICIS", "Archivo descargado");
                    }
                } else {
                    Toast.makeText(HorarioActivity.this, getResources().getString(R.string.error_permissionWrite), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
