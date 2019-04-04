package com.example.android_horariodicis_v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PersonalizarActivity extends AppCompatActivity {
    static final int REQUEST_CREATEPDF_CUSTOM = 113;
    static final String filePDFName = "HorarioDICIS_PERSONAL";
    ArrayList<Nap.ListView.Cursos_item> lvItems = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();
    String carrera;
    Button fbtAdd;
    Button btBack;
    Button btPDF;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { actionBar.hide(); }
        //_______________ Crear base de datos
        db = new Horario(this);
        //_______________ Botón para regresar
        btBack = findViewById(R.id.btPBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //_______________ Botón para agregar
        fbtAdd = findViewById(R.id.fbtAdd);
        fbtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        //_______________ Botón para generar PDF
        btPDF = findViewById(R.id.btGeneratePDF);
        btPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idList.size() == 0) {
                    Toast errorToast = Toast.makeText(PersonalizarActivity.this, getResources().getString(R.string.error_no_item), Toast.LENGTH_LONG);
                    errorToast.show();
                    return;
                }
                carrera = Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt");
                carrera = Nap.Carrera.Check(carrera);
                StringBuilder cmd = new StringBuilder("SELECT * FROM " + carrera + " WHERE ID IN (");
                for(int i = 0; i<idList.size(); i++) {
                    if(i < idList.size() - 1) cmd.append(String.valueOf(idList.get(i))).append(", ");
                    else cmd.append(String.valueOf(idList.get(i))).append(")");
                }
                Cursor cursor = db.runCMD(cmd.toString());
                if(Nap.XML.Generate(cursor, getFilesDir() + "/SCHDATA/xmlCUSTOM.xml")) {
                    String xslt = convertStreamToString(getResources().openRawResource(R.raw.xsltdicisv));
                    try {
                        Nap.FileX.Save(xslt, getFilesDir() + "/SCHDATA/xsltDICIS.xslt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    html = Nap.XML.ToHTML(new File(getFilesDir() + "/SCHDATA/xmlCUSTOM.xml"), new File(getFilesDir() + "/SCHDATA/xsltDICIS.xslt"));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!hasPermission(PersonalizarActivity.this, PERMISSIONS)) {
                            ActivityCompat.requestPermissions(PersonalizarActivity.this, PERMISSIONS, REQUEST_CREATEPDF_CUSTOM);
                        }
                        else {
                            new PersonalizarActivity.CreatePDF().execute();
                        }
                    }
                    else {
                        new PersonalizarActivity.CreatePDF().execute();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CREATEPDF_CUSTOM: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new PersonalizarActivity.CreatePDF().execute();
                } else {
                    Toast errorToast = Toast.makeText(PersonalizarActivity.this, getResources().getString(R.string.error_permissionWrite), Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CreatePDF extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String filename = Nap.FileX.RenameIfExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filePDFName + "_" + Nap.Carrera.Check(carrera) + ".pdf", 0);
            if(Nap.PDF.Create.FromHTML(filename, html)) {
                Intent chooser = Nap.Notification.Show_ClickFile(getApplicationContext(), filename, getResources().getString(R.string.app_name), Nap.FileX.GetBaseFileName(filename));
                startActivity(chooser);
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of item position
        outState.putParcelableArrayList("ITEM", lvItems);
        outState.putIntegerArrayList("ID", idList);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //_______________ Restaurar lvItems
        lvItems = savedInstanceState.getParcelableArrayList("ITEM");
        idList = savedInstanceState.getIntegerArrayList("ID");
        //_______________ TextView
        TextView tbxEmpty = findViewById(R.id.tbxEmpty);
        //_______________ Crear listView
        final ListView lvAux = findViewById(R.id.listViewAdd);
        //_______________ Crear objeto listView personalizada para los cursos
        final Nap.ListView.AddCursos lvCursoX = new Nap.ListView.AddCursos(this, lvItems, idList, tbxEmpty);
        lvAux.setAdapter(lvCursoX);
        if(!lvItems.isEmpty()) {
            tbxEmpty.setVisibility(View.GONE);
            tbxEmpty.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if(requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                //_______________ TextView
                TextView tbxEmpty = findViewById(R.id.tbxEmpty);
                ArrayList<Integer> idListX = data.getIntegerArrayListExtra("RESULT");
                if(idListX == null) return;
                int slist = idListX.size();
                if(slist>0) {
                    for(int i = 0; i<slist; i++) {
                        if(this.idList.contains(idListX.get(i))) {
                            idListX.remove(i);
                            i--;
                            slist--;
                        }
                    }
                    this.idList.addAll(idListX);
                } else return;
                if(idListX.size() == 0){
                    Toast errorToast = Toast.makeText(PersonalizarActivity.this, getResources().getString(R.string.error_repeat_item), Toast.LENGTH_LONG);
                    errorToast.show();
                }
                String carrera = Nap.Carrera.Check(Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt"));
                Cursor cursor = db.viewData(carrera);
                //_______________ Buscar los elementos seleccionados
                if(cursor.getCount() != 0) {
                    //_______________ Recorrer los datos con el objeto cursor
                    while(cursor.moveToNext()) {
                        int finalID = Nap.Cursor.GetID(cursor, "ID");
                        if(idListX.contains(finalID)) {
                            //_______________ Agregar un curso al ArrayList diseñado para contener el horario
                            lvItems.add(new Nap.ListView.Cursos_item(false, Nap.Cursor.GetID(cursor, "ID"), Nap.Cursor.GetElement(cursor, "HorasSem"), Nap.Cursor.GetElement(cursor, "UnidadDeAprendizaje"),
                                    Nap.Cursor.GetElement(cursor, "AreaDeLaUda"), Nap.Cursor.GetElement(cursor, "Clave"),
                                    Nap.Cursor.GetElement(cursor, "Lun"), Nap.Cursor.GetElement(cursor, "Mar"), Nap.Cursor.GetElement(cursor, "Mie"),
                                    Nap.Cursor.GetElement(cursor, "Jue"), Nap.Cursor.GetElement(cursor, "Vie"), Nap.Cursor.GetElement(cursor, "Sab"),
                                    Nap.Cursor.GetElement(cursor, "Grupo"), Nap.Cursor.GetElement(cursor, "Aula"), Nap.Cursor.GetElement(cursor, "Profesor")));
                        }
                    }
                    //_______________ Crear objeto listView personalizada para los cursos
                    Nap.ListView.AddCursos lvCursoX = new Nap.ListView.AddCursos(this, lvItems, idList, tbxEmpty);
                    //_______________ Vaciar todos los datos de los cursos a la listView
                    ListView lvAux = findViewById(R.id.listViewAdd);
                    lvAux.setAdapter(lvCursoX);
                    tbxEmpty.setVisibility(View.GONE);
                    tbxEmpty.setEnabled(false);
                }
            }
        }
    }
}
