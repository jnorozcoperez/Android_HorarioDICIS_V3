package com.example.android_horariodicis_v3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonalizarActivity extends AppCompatActivity {
    ArrayList<Nap.ListView.Cursos_item> lvItems = new ArrayList<>();
    ArrayList<Integer> idList = null;
    Button fbtAdd;
    Button btBack;
    Horario db;

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
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of item position
        outState.putParcelableArrayList("ITEM", lvItems);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //_______________ Restaurar lvItems
        lvItems = savedInstanceState.getParcelableArrayList("ITEM");
        //_______________ TextView
        TextView tbxEmpty = findViewById(R.id.tbxEmpty);
        //_______________ Crear listView
        final ListView lvAux = findViewById(R.id.listViewAdd);
        //_______________ Crear objeto listView personalizada para los cursos
        final Nap.ListView.AddCursos lvCursoX = new Nap.ListView.AddCursos(this, lvItems, tbxEmpty);
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
                idList = data.getIntegerArrayListExtra("RESULT");
                if(idList == null) return;
                String carrera = Nap.Carrera.Check(Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt"));
                Cursor cursor = db.viewData(carrera);
                //_______________ Buscar los elementos seleccionados
                if(cursor.getCount() != 0) {
                    //_______________ Recorrer los datos con el objeto cursor
                    while(cursor.moveToNext()) {
                        int finalID = Nap.Cursor.GetID(cursor, "ID");
                        if(idList.contains(finalID)) {
                            //_______________ Agregar un curso al ArrayList diseñado para contener el horario
                            lvItems.add(new Nap.ListView.Cursos_item(false, Nap.Cursor.GetID(cursor, "ID"), Nap.Cursor.GetElement(cursor, "HorasSem"), Nap.Cursor.GetElement(cursor, "UnidadDeAprendizaje"),
                                    Nap.Cursor.GetElement(cursor, "AreaDeLaUda"), Nap.Cursor.GetElement(cursor, "Clave"),
                                    Nap.Cursor.GetElement(cursor, "Lun"), Nap.Cursor.GetElement(cursor, "Mar"), Nap.Cursor.GetElement(cursor, "Mie"),
                                    Nap.Cursor.GetElement(cursor, "Jue"), Nap.Cursor.GetElement(cursor, "Vie"), Nap.Cursor.GetElement(cursor, "Sab"),
                                    Nap.Cursor.GetElement(cursor, "Grupo"), Nap.Cursor.GetElement(cursor, "Aula"), Nap.Cursor.GetElement(cursor, "Profesor")));
                        }
                    }
                    //_______________ Crear objeto listView personalizada para los cursos
                    Nap.ListView.AddCursos lvCursoX = new Nap.ListView.AddCursos(this, lvItems, tbxEmpty);
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
