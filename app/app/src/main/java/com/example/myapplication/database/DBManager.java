package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
  private DBConexion dbConexion;
  private SQLiteDatabase db;

  public DBManager(Context context) {
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
    ContentValues valores = new ContentValues();
    valores.put("nombre", nombre);
    valores.put("email", email);
    valores.put("password", password);

    long result = db.insert("usuarios", null, valores);
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
}
