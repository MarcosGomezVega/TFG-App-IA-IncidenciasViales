package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.myapplication.Incident;
import com.example.myapplication.IncidentStatus;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBManager {
  private DBConexion dbConexion;
  private SQLiteDatabase db;
  private Context context;
  private Cursor cursor;
  public DBManager(Context context) {
    this.context = context;
    dbConexion = new DBConexion(context);
  }
  public void open() {
    db = dbConexion.getWritableDatabase();
    Log.d("AdapterStatusssss", "Base de datos abierta: " + db.getPath());
  }

  public void close() {
    dbConexion.close();
  }

  public boolean insertarUsuario(String nombre, String email, String password, String date) {
    open();
    ContentValues values = new ContentValues();
    values.put("nombre", nombre);
    values.put("email", email);
    values.put("password", password);
    values.put("lastLogin", date);

    long result = db.insert("usuarios", null, values);
    close();
    return result != -1;
  }
  public boolean insertarIncidencia(int usuarioId, String tipoIncidencia, String localizacion, String fotoRuta, String fecha, String status) {
    open();

    ContentValues values = new ContentValues();
    values.put("usuario_id", usuarioId);
    values.put("tipo_incidencia", tipoIncidencia);
    values.put("localizacion", localizacion);
    values.put("foto", fotoRuta);
    values.put("fecha", fecha);
    values.put("status", status);

    long newId = db.insert("incidencias", null, values);
    if (newId == -1) {
      close();
      return false;
    }
    close();
    return true;

  }
  public boolean validarUsuario(String email, String password) {
    open();

    String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
    Cursor cursor = db.rawQuery(query, new String[]{email, password});

    boolean existe = cursor.getCount() > 0;
    cursor.close();
    close();

    return existe;
  }
  public boolean hayUsuariosRegistrados() {
    open();  // Abre la base de datos
    Cursor cursor = null;
    boolean hayDatos = false;
    try {
      cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE email IS NOT NULL AND email != '' AND password IS NOT NULL AND password != ''", null);
      if (cursor != null && cursor.moveToFirst()) {
        hayDatos = cursor.getInt(0) > 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
      close();
    }
    return hayDatos;
  }
  public int obtenerIdUltimoUsuario() {
    open();
    Cursor cursor = db.rawQuery("SELECT id FROM usuarios ORDER BY datetime(lastLogin) DESC LIMIT 1", null);
    int userId = -1;
    if (cursor.moveToFirst()) {
      userId = cursor.getInt(0);
    }
    cursor.close();
    close();
    return userId;
  }
  public List<Incident> obtenerIncidencias() {
    List<Incident> lista = new ArrayList<>();
    open();
    Cursor cursor = db.rawQuery("SELECT  id, tipo_incidencia FROM incidencias", null);
    if (cursor.moveToFirst()) {
      do {
        String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_incidencia"));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        lista.add(new Incident(tipo, id));
      } while (cursor.moveToNext());
    }
    cursor.close();
    close();
    return lista;


  }
  public IncidentStatus obtenerIncidencias(int id) {
    open();
    IncidentStatus result = null;

    String query = "SELECT id,tipo_incidencia,localizacion,foto,fecha,status " +
      "FROM incidencias " +
      "WHERE id = ? ";
    try {
      Log.d("AdapterStatusssss", "ID: " + id);
      cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

      Log.d("AdapterStatusssss", "Cursor count: " + cursor.getCount());
      boolean tieneDatos = cursor.moveToFirst();
      Log.d("AdapterStatusssss", "tieneDatos: " + tieneDatos);

      if (tieneDatos) {
        result = new IncidentStatus();
        String[] columnNames = cursor.getColumnNames();
        result.setId(cursor.getInt(0));
        result.setIncident_type(cursor.getString(1));
        result.setLocalitation(cursor.getString(2));
        result.setPhoto(cursor.getString(3));
        result.setDate(cursor.getString(4));
        result.setStatus(cursor.getString(5));
      }
    }catch (Exception e) {
      Log.e("AdapterStatusssss", "Error al acceder a los datos: " + e.getMessage(), e);
    }
    cursor.close();
    close();
    return result;

  }

  public void actualizarFechaLogin(int usuarioId) {
    open();
    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

    ContentValues values = new ContentValues();
    values.put("lastLogin", date);

    db.update("usuarios", values, "id = ?", new String[]{String.valueOf(usuarioId)});
    close();
  }
}
