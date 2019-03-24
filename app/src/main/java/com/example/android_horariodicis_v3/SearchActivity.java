package com.example.android_horariodicis_v3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<Nap.ListView.Cursos_item> lvItems;
    Button btCancel;
    Button btAgregar;
    Horario db;

    private ArrayList<Nap.ListView.Cursos_item> GetArrayItems() {
        lvItems = new ArrayList<>();
        String carrera = Nap.Carrera.Check(Nap.FileX.Read(getFilesDir() + "/SCHDATA", "Carrera.txt"));
        Cursor cursor = db.viewData(carrera);
        //_________________ Leer el horario
        if(cursor.getCount() != 0) {
            //_______________ Recorrer los datos con el objeto cursor
            while(cursor.moveToNext()) {
                //_______________ Agregar un curso al ArrayList diseñado para contener el horario
                lvItems.add(new Nap.ListView.Cursos_item(false, Nap.Cursor.GetID(cursor, "ID"), Nap.Cursor.GetElement(cursor, "HorasSem"), Nap.Cursor.GetElement(cursor, "UnidadDeAprendizaje"),
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
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        //_______________ Crear base de datos
        db = new Horario(this);
        //_______________ Crear listView
        final ListView lvitemX = findViewById(R.id.listViewSearchCursos);
        //_______________ Crear objeto listView personalizada para los cursos
        final Nap.ListView.SearchCursos lvCursoX = new Nap.ListView.SearchCursos(this, GetArrayItems());
        //_______________ Vaciar todos los datos de los cursos a la listView
        lvitemX.setAdapter(lvCursoX);
        //_______________ Activar el click en la listView
        lvitemX.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nap.ListView.Cursos_item cursos_item = lvItems.get(position);
                if(cursos_item.isSelected()) cursos_item.setSelected(false);
                else cursos_item.setSelected(true);
                lvItems.set(position, cursos_item);
                lvCursoX.updateRecords(lvItems);
            }
        });
        //_______________ Botón para cancelar
        btCancel = findViewById(R.id.btBack);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //_______________ Botón para agregar
        btAgregar = findViewById(R.id.btAddItem);
        btAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> idList = new ArrayList<>();
                Intent returnIntent = new Intent();
                int sizeX = lvItems.size();
                for(int i = 0; i<sizeX; i++) {
                    if(lvItems.get(i).isSelected()) {
                        idList.add(lvItems.get(i).GetID());
                    }
                }
                returnIntent.putIntegerArrayListExtra("RESULT", idList);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
