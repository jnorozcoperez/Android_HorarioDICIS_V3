package com.example.android_horariodicis_v3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


class Template {
    private int version = 0;
    private String name = "";
    private LinkedList<String> tables = new LinkedList<>();
    private Map<String, ArrayList<ArrayList<String>>> columns = new HashMap<>();

    Template(String namex, int versionX) {
        this.name = namex;
        this.version = versionX;
    }

    void AddTables(String tablesSeparateByComma) {
        this.tables.clear();
        String[] tab = tablesSeparateByComma.split(",");
        for (String aux : tab) {
            this.tables.add(Nap.Text.RemoveFirstSpace(aux));
        }
    }

    boolean AddColumn(String tableX, String column, String propertie) {
        ArrayList<ArrayList<String>> aux = this.columns.get(tableX);
        ArrayList<String> tempAux = new ArrayList<>();
        tempAux.add(Nap.Text.RemoveFirstSpace(column));
        tempAux.add(propertie);
        if (aux != null) {
            aux.add(tempAux);
            this.columns.put(tableX, aux);
        }
        return true;
    }

    void AddColumns(String tableX, String columnsSeparateByComma) {
        String[] cols = columnsSeparateByComma.split(",");
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        for (String aux : cols) {
            ArrayList<String> tempAux = new ArrayList<>();
            tempAux.add(Nap.Text.RemoveFirstSpace(aux));
            if (aux.contentEquals("ID")) tempAux.add("INTEGER PRIMARY KEY AUTOINCREMENT");
            else tempAux.add("TEXT");
            temp.add(tempAux);
        }
        this.columns.put(tableX, temp);
    }

    String GetName() {
        return this.name;
    }

    int GetVersion() {
        return this.version;
    }

    LinkedList<String> GetTables() {
        return tables;
    }

    String GetCmdColumns(String table) {
        StringBuilder output = new StringBuilder();
        ArrayList<ArrayList<String>> cols = this.columns.get(table);
        for (int i = 0; i < (cols != null ? cols.size() : 0); i++) {
            ArrayList<String> aux = cols.get(i);
            if (i < cols.size() - 1)
                output.append(aux.get(0)).append(" ").append(aux.get(1)).append(", ");
            else output.append(aux.get(0)).append(" ").append(aux.get(1)).append(" ) ");
        }
        return output.toString();
    }

    void SetVersion(int versionX) {
        this.version = versionX;
    }
}

class Horario extends SQLiteOpenHelper {
    //Creación de la base de datos
    private Template dbDICIS = new Template("horarioDICIS.db", 1);
    SQLiteDatabase database;
    private String[] carreras = new String[]{"Artes Digitales", "Comunicaciones y Electrónica", "Eléctrica",
            "Gestión Empresarial", "Mecánica", "Mecatrónica", "Sistemas Computacionales"};
    Horario(Context context) {
        super(context, "horarioDICIS.db", null, 1);
        database = getWritableDatabase();
        dbDICIS.AddTables("SISTEMAS, ARTES, MECANICA, MECATRONICA, ELECTRONICA, ELECTRICA, GESTION, INGLES");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd = "";
        LinkedList<String> tab = dbDICIS.GetTables();
        if(tab.size() == 0) dbDICIS.AddTables("SISTEMAS, ARTES, MECANICA, MECATRONICA, ELECTRONICA, ELECTRICA, GESTION, INGLES");
        dbDICIS.AddTables("SISTEMAS, ARTES, MECANICA, MECATRONICA, ELECTRONICA, ELECTRICA, GESTION, INGLES");
        dbDICIS.AddColumns("SISTEMAS", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("ARTES", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("MECANICA", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("MECATRONICA", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("ELECTRONICA", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("ELECTRICA", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("GESTION", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        dbDICIS.AddColumns("INGLES", "ID, AreaDeLaUda, Clave, UnidadDeAprendizaje, HorasSem, Requisitos, Grupo, Lun, Mar, Mie, Jue, Vie, Sab, Aula, Profesor, Presidente, Vocal");
        for(String table : dbDICIS.GetTables()){
            cmd = "CREATE TABLE " + table + " ( ";
            cmd += dbDICIS.GetCmdColumns(table);
            db.execSQL(cmd);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String table : dbDICIS.GetTables()) {
            db.execSQL("DELETE FROM " + table);
        }
        dbDICIS.SetVersion(newVersion);
        //onCreate(db);
    }

    void addItems(Document doc, String carrera) {
        NodeList nodeList = doc.getDocumentElement().getElementsByTagName("item");
        String[] reference = new String[]{"AreaDeLaUda", "UnidadDeAprendizaje", "HorasSem", "Clave",
                "Requisitos", "Grupo", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Aula", "Profesor", "Presidente", "Vocal"};
        //Check carrera
        carrera = CheckCarrera(carrera);
        if(carrera.isEmpty()) return;
        //Insert data into database
        ArrayList<String> listCMD = new ArrayList<>();
        for(int i = 0; i<nodeList.getLength(); i++) {
            StringBuilder cmd = new StringBuilder("INSERT INTO '" + carrera + "' (");
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                String[][] contenido = new String[16][2];
                for(int j = 0; j<element.getChildNodes().getLength(); j++) {
                    Node nde = element.getChildNodes().item(j);
                    for (String aReference : reference) {
                        if (nde.getNodeName().equals(aReference)) {
                            contenido[j][0] = nde.getNodeName();
                            if (!nde.hasChildNodes()) contenido[j][1] = "";
                            else contenido[j][1] = nde.getFirstChild().getTextContent();
                            break;
                        }
                    }
                }
                for(int j = 0; j<16; j++) {
                    cmd.append("'").append(contenido[j][0]);
                    if(j < 15) cmd.append("', ");
                    else cmd.append("') ");
                }
                cmd.append("VALUES (");
                for(int j = 0; j<16; j++) {
                    cmd.append("'").append(contenido[j][1]).append("'");
                    if(j < 15) cmd.append(", ");
                    else cmd.append(");");
                }
                listCMD.add(cmd.toString());
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i = 0; i<listCMD.size(); i++) {
            db.execSQL(listCMD.get(i));
        }
    }

    android.database.Cursor viewData(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cmd = "SELECT * FROM " + table;
        return db.rawQuery(cmd, null);
    }

    android.database.Cursor runCMD(String cmd) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(cmd, null);
    }

    String CheckCarrera(String carrera) {
        //Check carrera
        if(carrera.equals(carreras[0])) return "ARTES";
        else if(carrera.equals(carreras[1])) return "ELECTRONICA";
        else if(carrera.equals(carreras[2])) return "ELECTRICA";
        else if(carrera.equals(carreras[3])) return "GESTION";
        else if(carrera.equals(carreras[4])) return "MECANICA";
        else if(carrera.equals(carreras[5])) return "MECATRONICA";
        else if(carrera.equals(carreras[6])) return "SISTEMAS";
        return carrera;
    }
}
