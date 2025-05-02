package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class DBManager {
  private DBConexion dbConexion;
  private SQLiteDatabase db;
  private Context context;

  public DBManager(Context context) {
    this.context = context;
    dbConexion = new DBConexion(context);
  }

  public void open() {
    db = dbConexion.getWritableDatabase();
  }
  public void close() {
    dbConexion.close();
  }

  public boolean insertarUsuario(String nombre, String email, String password) {
    open();
    ContentValues values = new ContentValues();
    values.put("nombre", nombre);
    values.put("email", email);
    values.put("password", password);

    long result = db.insert("usuarios", null, values);
    close();
    return result != -1;
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
    open();
    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE email IS NOT NULL AND email != '' AND password IS NOT NULL AND password != ''", null);
    boolean hayDatos = cursor.getCount() > 0;
    cursor.close();
    return hayDatos;
  }

  public int obtenerIdUsuario(String email) {
    open();
    Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE email = ?", new String[]{email});
    int userId = -1;
    if (cursor.moveToFirst()) {
      userId = cursor.getInt(0);
    }
    cursor.close();
    close();
    return userId;
  }

  public boolean insertarIncidencia(int usuarioId, String tipoIncidencia, String localizacion, Bitmap foto, String fecha) {
    open();

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    foto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
    byte[] fotoBytes = byteArrayOutputStream.toByteArray();

    ContentValues values = new ContentValues();
    values.put("usuario_id", usuarioId);
    values.put("tipo_incidencia", tipoIncidencia);
    values.put("localizacion", localizacion);
    values.put("foto", fotoBytes);
    values.put("fecha", fecha);

    long result = db.insert("incidencias", null, values);
    close();
    return result != -1;
  }
}
