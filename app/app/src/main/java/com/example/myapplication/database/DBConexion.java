package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConexion extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "App-IA-IncidenciasViales.db";
  private static final int DATABASE_VERSION = 1;

  public DBConexion(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_TABLE_USUARIOS = "CREATE TABLE usuarios (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
      "nombre TEXT NOT NULL," +
      "email TEXT NOT NULL UNIQUE," +
      "password TEXT NOT NULL," +
      "lastLogin TEXT NOT NULL" +
      ");";

    String CREATE_TABLE_INCIDENT = "CREATE TABLE incidencias (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
      "usuario_id INTEGER NOT NULL," +
      "tipo_incidencia TEXT NOT NULL," +
      "localizacion TEXT NOT NULL," +
      "foto TEXT NOT NULL," +
      "fecha TEXT NOT NULL," +
      "status TEXT NOT NULL," +
      "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
      ");";

     db.execSQL(CREATE_TABLE_USUARIOS);
    db.execSQL(CREATE_TABLE_INCIDENT);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS usuarios");
    db.execSQL("DROP TABLE IF EXISTS incidencias");
    onCreate(db);
  }
}
