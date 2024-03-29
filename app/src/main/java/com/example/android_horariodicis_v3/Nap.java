package com.example.android_horariodicis_v3;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

class Nap
{
    Nap(){ }

    static class Share {
        static Intent File(Context context, String filename) {
            File file = new File(filename);
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String type = mimeTypeMap.getMimeTypeFromExtension("pdf");
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            }
            else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setDataAndType(uri, type);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(intent, context.getResources().getString(R.string.share_with));
            if(chooser == null) return null;
            return chooser;
        }
    }

    static class Notification {
        static Intent Show_ClickFile(Context context, String filename, String titleN, String messageN) {
            File file = new File(filename);
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String type = mimeTypeMap.getMimeTypeFromExtension("pdf");
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            }
            else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //Intent chooser = Intent.createChooser(intent, "Choose an app to open with:");
            Intent chooser = Intent.createChooser(intent, context.getResources().getString(R.string.open_with));
            if(chooser == null) return null;
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, chooser, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(titleN)
                    .setContentText(messageN)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setChannelId(BuildConfig.APPLICATION_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(BuildConfig.APPLICATION_ID, "PDFbutton", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(null, null);
                channel.enableVibration(true);
                if(notificationManager!=null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
            assert notificationManager != null;
            notificationManager.notify(2, builder.build());
            //context.startActivity(chooser);
            return chooser;
        }
    }

    static class XML {
        static boolean Generate(android.database.Cursor cursor, String path) {
            try {
                if(cursor.getCount() == 0) return false;
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                //root
                Document document = documentBuilder.newDocument();
                Element rootElement = document.createElement("list");
                document.appendChild(rootElement);
                while(cursor.moveToNext()) {
                    //item
                    Element item = document.createElement("item");
                    rootElement.appendChild(item);
                    //___________Elements
                    Element AreaDeLaUda = document.createElement("AreaDeLaUda");
                    AreaDeLaUda.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "AreaDeLaUda")));
                    item.appendChild(AreaDeLaUda);
                    //
                    Element Clave = document.createElement("Clave");
                    Clave.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Clave")));
                    item.appendChild(Clave);
                    //
                    Element UnidadDeAprendizaje = document.createElement("UnidadDeAprendizaje");
                    UnidadDeAprendizaje.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "UnidadDeAprendizaje")));
                    item.appendChild(UnidadDeAprendizaje);
                    //
                    Element HorasSem = document.createElement("HorasSem");
                    HorasSem.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "HorasSem")));
                    item.appendChild(HorasSem);
                    //
                    Element Requisitos = document.createElement("Requisitos");
                    Requisitos.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Requisitos")));
                    item.appendChild(Requisitos);
                    //
                    Element Grupo = document.createElement("Grupo");
                    Grupo.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Grupo")));
                    item.appendChild(Grupo);
                    //
                    Element Lun = document.createElement("Lun");
                    Lun.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Lun")));
                    item.appendChild(Lun);
                    //
                    Element Mar = document.createElement("Mar");
                    Mar.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Mar")));
                    item.appendChild(Mar);
                    //
                    Element Mie = document.createElement("Mie");
                    Mie.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Mie")));
                    item.appendChild(Mie);
                    //
                    Element Jue = document.createElement("Jue");
                    Jue.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Jue")));
                    item.appendChild(Jue);
                    //
                    Element Vie = document.createElement("Vie");
                    Vie.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Vie")));
                    item.appendChild(Vie);
                    //
                    Element Sab = document.createElement("Sab");
                    Sab.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Sab")));
                    item.appendChild(Sab);
                    //
                    Element Aula = document.createElement("Aula");
                    Aula.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Aula")));
                    item.appendChild(Aula);
                    //
                    Element Profesor = document.createElement("Profesor");
                    Profesor.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Profesor")));
                    item.appendChild(Profesor);
                    //
                    Element Presidente = document.createElement("Presidente");
                    Presidente.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Presidente")));
                    item.appendChild(Presidente);
                    //
                    Element Vocal = document.createElement("Vocal");
                    Vocal.appendChild(document.createTextNode(Nap.Cursor.GetElement(cursor, "Vocal")));
                    item.appendChild(Vocal);
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(writer));
                String xml = writer.getBuffer().toString().replaceAll("[\n\r]", "");
                Nap.FileX.Save(xml, path);
                return true;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        static String ToHTML(File XML, File XSLT) {
            StringWriter res = new StringWriter();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(XML);
                StreamSource styleSource = new StreamSource(XSLT);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer(styleSource);
                DOMSource source = new DOMSource(document);
                //StreamResult result = new StreamResult(path);
                transformer.transform(source, new StreamResult(res));
                return res.toString();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    static class PDF {
        static class Create {
            static boolean FromHTML(String filename, String html) {
                try {
                    InputStream htmlX = new ByteArrayInputStream(html.getBytes());
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A3.rotate());
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
                    document.open();
                    XMLWorkerHelper workerHelper = XMLWorkerHelper.getInstance();
                    workerHelper.parseXHtml(writer, document, htmlX, Charset.forName("UTF-8"));
                    document.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } catch (DocumentException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
    }

    static class Carrera {
        static String Check(String input) {
            String[] carreras = new String[]{"Artes Digitales", "Comunicaciones y Electrónica", "Eléctrica",
                    "Gestión Empresarial", "Mecánica", "Mecatrónica", "Sistemas Computacionales"};
            if(input.equals(carreras[0])) return "ARTES";
            else if(input.equals(carreras[1])) return "ELECTRONICA";
            else if(input.equals(carreras[2])) return "ELECTRICA";
            else if(input.equals(carreras[3])) return "GESTION";
            else if(input.equals(carreras[4])) return "MECANICA";
            else if(input.equals(carreras[5])) return "MECATRONICA";
            else if(input.equals(carreras[6])) return "SISTEMAS";
            return input;
        }
    }

    static class MailService {
        private String xmlHorario;
        private Document doc = null;

        MailService(String label, String path) throws Exception {
            xmlHorario = "";
            URLName url = new URLName("imaps", "imap.gmail.com", 993, label, "sch.dicis@gmail.com", "10071994JnOp_Chicken");
            Properties props;
            try {
                props = System.getProperties();
            } catch (SecurityException excep) {
                props = new Properties();
            }
            Session session = Session.getInstance(props);
            Store store = session.getStore(url);
            store.connect();
            Folder folder = store.getFolder(label);
            try {
                folder.open(Folder.READ_ONLY);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_WRITE);
            }
            for(int i = folder.getMessageCount(); i>=0; i--) {
                String xmlX = "";
                Message msg = folder.getMessage(i);
                Multipart multipart = (Multipart) msg.getContent();
                for(int j = 0; j<multipart.getCount(); j++) {
                    BodyPart bodyPart = multipart.getBodyPart(j);
                    Part part = multipart.getBodyPart(j);
                    if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && StringUtils.isBlank(bodyPart.getFileName())) {
                        continue; // dealing with attachments only
                    }
                    String filename = bodyPart.getFileName();
                    if(filename.contains("xml")) {
                        xmlX = IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
                        if (xmlX.contains("<?xml")) {
                            xmlHorario = xmlX;
                            if (!CreateXMLDoc()) xmlHorario = "";
                        }
                    }
                    else if(filename.contains("pdf")) {
                        File fileToSave = new File(path, filename);
                        fileToSave.delete();
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
                        mimeBodyPart.saveFile(fileToSave);
                        i = 0;
                    }
                }
            }
            folder.close(false);
            store.close();
        }


        boolean CreateXMLDoc() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(new InputSource(new StringReader(xmlHorario)));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (SAXException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        String GetXML() {
            return xmlHorario;
        }

        Document GetDocument() {
            return doc;
        }
    }

    static class Cursor {

        static String GetElement(android.database.Cursor cursor, String columnName) {
            cursor.getString(cursor.getColumnIndex(columnName));
            for(int i = 0; i<cursor.getColumnCount(); i++) {
                if (columnName.equals(cursor.getColumnName(i))) return cursor.getString(i);
            }
            return "";
        }

        static int GetID(android.database.Cursor cursor, String columnName) {
            int id = -1;
            for(int i = 0; i<cursor.getColumnCount(); i++) {
                if (columnName.equals(cursor.getColumnName(i))) return Integer.parseInt(cursor.getString(i));
            }
            return id;
        }
    }

    static class Text {
        static String RemoveFirstSpace(String input){
            if (input.isEmpty()) return "";
            if (input.charAt(0) == ' ') {
                if (input.length() <= 1) {
                    return "";
                }
                if (input.charAt(0) != ' ') return input;
                StringBuilder aux = new StringBuilder();
                for (int i = 1; i < input.length(); i++) {
                    aux.append(input.charAt(i));
                }
                return aux.toString();
            }
            return input;
        }
    }

    static class ListView {

        static class Cursos_item implements Parcelable {
            boolean isSelected;
            private int id;
            private String horasSem;
            private String unidadAprendizaje;
            private String areaUDA;
            private String clave;
            private String lun;
            private String mar;
            private String mie;
            private String jue;
            private String vie;
            private String sab;
            private String grupo;
            private String aula;
            private String responsable;

            Cursos_item(boolean isSelected, int id, String horasSem, String unidadAprendizaje, String areaUDA, String clave, String lun, String mar, String mie, String jue, String vie, String sab,
                        String grupo, String aula, String responsable){
                this.isSelected = isSelected;
                this.id = id;
                this.horasSem = horasSem;
                this.unidadAprendizaje = unidadAprendizaje;
                this.areaUDA = areaUDA;
                this.clave = clave;
                this.lun = lun;
                this.mar = mar;
                this.mie = mie;
                this.jue = jue;
                this.vie = vie;
                this.sab = sab;
                this.aula = aula;
                this.grupo = grupo;
                this.responsable = responsable;
            }

            Cursos_item(Parcel in) {
                isSelected = in.readByte() != 0;
                id = in.readInt();
                horasSem = in.readString();
                unidadAprendizaje = in.readString();
                areaUDA = in.readString();
                clave = in.readString();
                lun = in.readString();
                mar = in.readString();
                mie = in.readString();
                jue = in.readString();
                vie = in.readString();
                sab = in.readString();
                grupo = in.readString();
                aula = in.readString();
                responsable = in.readString();
            }

            public static final Creator<Cursos_item> CREATOR = new Creator<Cursos_item>() {
                @Override
                public Cursos_item createFromParcel(Parcel in) {
                    return new Cursos_item(in);
                }

                @Override
                public Cursos_item[] newArray(int size) {
                    return new Cursos_item[size];
                }
            };

            boolean isSelected() { return isSelected; }
            int GetID() { return id; }
            String GetHorasSem() { return horasSem; }
            String GetUnidadAprendizaje() { return unidadAprendizaje; }
            String GetAreaUDA() { return areaUDA; }
            String GetClave() { return clave; }
            String GetLun() { return lun; }
            String GetMar() { return mar; }
            String GetMie() { return mie; }
            String GetJue() { return jue; }
            String GetVie() { return vie; }
            String GetSab() { return sab; }
            String GetGrupo() { return grupo; }
            String GetAula() { return aula; }
            String GetResponsable() { return responsable; }
            void setSelected(boolean isSelected) { this.isSelected = isSelected; }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeByte((byte) (isSelected ? 1 : 0));
                dest.writeInt(id);
                dest.writeString(horasSem);
                dest.writeString(unidadAprendizaje);
                dest.writeString(areaUDA);
                dest.writeString(clave);
                dest.writeString(lun);
                dest.writeString(mar);
                dest.writeString(mie);
                dest.writeString(jue);
                dest.writeString(vie);
                dest.writeString(sab);
                dest.writeString(grupo);
                dest.writeString(aula);
                dest.writeString(responsable);
            }
        }

        static class AddCursos extends BaseAdapter {
            private Context context;
            private ArrayList<Cursos_item> lvItems;
            private ArrayList<Integer> idList;
            TextView empty;

            AddCursos(Context context, ArrayList<Cursos_item> lvItems, ArrayList<Integer> idList, TextView empty)  {
                this.context = context;
                this.lvItems = lvItems;
                this.idList = idList;
                this.empty = empty;
            }

            @Override
            public int getCount() { return lvItems.size(); }

            @Override
            public Object getItem(int position) {
                return lvItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return lvItems.get(position).id;
            }

            @SuppressLint({"ViewHolder", "InflateParams"})
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Cursos_item itemX = (Cursos_item)getItem(position);
                convertView = LayoutInflater.from(context).inflate(R.layout.addlistview_item, null);

                TextView unidadAprendizaje = convertView.findViewById(R.id.atbxUnidadAprendizaje);
                TextView areaUDA = convertView.findViewById(R.id.atbxAreaUDA);
                TextView clave = convertView.findViewById(R.id.atbxClave);
                TextView grupo = convertView.findViewById(R.id.atbxGrupo);
                TextView profesor = convertView.findViewById(R.id.atbxProfesor);

                unidadAprendizaje.setText(itemX.GetUnidadAprendizaje());
                areaUDA.setText(itemX.GetAreaUDA());
                clave.setText(itemX.GetClave());
                grupo.setText(itemX.GetGrupo());
                profesor.setText(itemX.GetResponsable());

                Button btDelete = convertView.findViewById(R.id.abtDelete);
                btDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        lvItems.remove(position);
                        idList.remove(position);
                        if(lvItems.isEmpty()) {
                            if(empty != null) {
                                empty.setVisibility(View.VISIBLE);
                                empty.setEnabled(true);
                            }
                        }
                        updateRecords(lvItems, idList);
                    }
                });
                return convertView;
            }

            void updateRecords(ArrayList<Cursos_item> lvItems, ArrayList<Integer> idList) {
                this.lvItems = lvItems;
                this.idList = idList;
                notifyDataSetChanged();
            }
        }

        static class SearchCursos extends BaseAdapter {
            private Context context;
            private ArrayList<Cursos_item> lvItems;

            SearchCursos(Context context, ArrayList<Cursos_item> lvItems) {
                this.context = context;
                this.lvItems = lvItems;
            }

            @Override
            public int getCount() { return lvItems.size(); }

            @Override
            public Object getItem(int position) {
                return lvItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return lvItems.get(position).id;
            }

            @SuppressLint({"ViewHolder", "InflateParams"})
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Cursos_item itemX = (Cursos_item)getItem(position);
                convertView = LayoutInflater.from(context).inflate(R.layout.searchlistview_item, null);

                if(itemX.isSelected()) {
                    ImageView imgCheck = convertView.findViewById(R.id.imgCheck);
                    imgCheck.setImageResource(R.drawable.checked);
                    convertView.setBackgroundColor(Color.argb(255, 220, 220, 220));

                }
                else {
                    ImageView imgCheck = convertView.findViewById(R.id.imgCheck);
                    imgCheck.setImageResource(R.drawable.check);
                    convertView.setBackgroundColor(Color.argb(255, 255, 255, 255));
                }

                TextView unidadAprendizaje = convertView.findViewById(R.id.stbxUnidadAprendizaje);
                TextView areaUDA = convertView.findViewById(R.id.stbxAreaUDA);
                TextView clave = convertView.findViewById(R.id.stbxClave);
                TextView grupo = convertView.findViewById(R.id.stbxGrupo);
                TextView profesor = convertView.findViewById(R.id.stbxProfesor);

                unidadAprendizaje.setText(itemX.GetUnidadAprendizaje());
                areaUDA.setText(itemX.GetAreaUDA());
                clave.setText(itemX.GetClave());
                grupo.setText(itemX.GetGrupo());
                profesor.setText(itemX.GetResponsable());
                return convertView;
            }

            void updateRecords(ArrayList<Cursos_item> lvItems) {
                this.lvItems = lvItems;
                notifyDataSetChanged();
            }
        }

        static class Cursos extends BaseAdapter {
            private Context context;
            private ArrayList<Cursos_item> lvItems;

            Cursos(Context context, ArrayList<Cursos_item> lvItems) {
                this.context = context;
                this.lvItems = lvItems;
            }

            @Override
            public int getCount() {
                return lvItems.size();
            }

            @Override
            public Object getItem(int position) {
                return lvItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint({"ViewHolder", "InflateParams"})
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Cursos_item itemX = (Cursos_item)getItem(position);
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
                TextView horaSem = convertView.findViewById(R.id.tbxHoraSem);
                TextView unidadAprendizaje = convertView.findViewById(R.id.tbxUnidadAprendizaje);
                TextView areaUDA = convertView.findViewById(R.id.tbxAreaUDA);
                TextView clave = convertView.findViewById(R.id.tbxClave);
                TextView lun = convertView.findViewById(R.id.tbxLun);
                TextView mar = convertView.findViewById(R.id.tbxMar);
                TextView mie = convertView.findViewById(R.id.tbxMie);
                TextView jue = convertView.findViewById(R.id.tbxJue);
                TextView vie = convertView.findViewById(R.id.tbxVie);
                TextView sab = convertView.findViewById(R.id.tbxSab);
                TextView aula = convertView.findViewById(R.id.tbxAula);
                TextView profesor = convertView.findViewById(R.id.tbxProfesor);

                horaSem.setText(itemX.GetHorasSem());
                unidadAprendizaje.setText(itemX.GetUnidadAprendizaje());
                areaUDA.setText(itemX.GetAreaUDA());
                clave.setText(itemX.GetClave());
                lun.setText(itemX.GetLun());
                mar.setText(itemX.GetMar());
                mie.setText(itemX.GetMie());
                jue.setText(itemX.GetJue());
                vie.setText(itemX.GetVie());
                sab.setText(itemX.GetSab());
                aula.setText(itemX.GetAula());
                profesor.setText(itemX.GetResponsable());
                return convertView;
            }
        }
    }

    static class FileX {

        static boolean Copy(File input, File destination) {
            try (InputStream in = new FileInputStream(input)) {
                try(OutputStream out = new FileOutputStream(destination)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        static String GetBaseFileName(String fullfilename) {
            String filename = "";
            int i = fullfilename.lastIndexOf(".");
            int p = Math.max(fullfilename.lastIndexOf("/"), fullfilename.lastIndexOf("\\"));
            filename = fullfilename.substring(p + 1, i);
            return filename;
        }

        static String GetFileName(String fullfilename) {
            String filename = "";
            int i = fullfilename.lastIndexOf(".");
            filename = fullfilename.substring(0, i);
            return  filename;
        }

        static String GetFileExtension(String filename) {
            String extension = "";
            int i = filename.lastIndexOf(".");
            int p = Math.max(filename.lastIndexOf("/"), filename.lastIndexOf("\\"));
            if (i > p) {
                extension = filename.substring(i + 1);
            }
            return extension;
        }

        static String RenameIfExist(String fullfilename, int num) {
            String extension = GetFileExtension(fullfilename);

            if (num == 0) {
                File myFile = new File(fullfilename);
                if (!myFile.exists()) {
                    return fullfilename;
                } else return RenameIfExist(fullfilename, ++num);
            } else {
                String finalName = GetFileName(fullfilename) + "(" + num + ")." + extension;
                File myFile = new File(finalName);
                if (!myFile.exists()) return finalName;
                else return RenameIfExist(fullfilename, ++num);
            }
        }

        static void Save(String data, String path) throws IOException {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            out.write(data);
            out.close();
        }

        static boolean Exists(String path) {
            File folder = new File(path);
            return folder.exists();
        }

        static String Read(String path, String nameFile) {
            String output;
            File reader = new File(path, nameFile);
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(reader));
                while((output = br.readLine()) != null) {
                    text.append(output);
                }
                output = text.toString();
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return output;
        }
    }
}