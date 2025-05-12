package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBConexion is a helper class to manage database creation and version management.
 * It defines the schema for the application, including tables for users and incidents.
 */
public class DBConexion extends SQLiteOpenHelper {

  /**
   * Name of the SQLite database file.
   */
  private static final String DATABASE_NAME = "App-IA-IncidenciasViales.db";

  /**
   * Version number of the database schema. Increment this to trigger onUpgrade.
   */
  private static final int DATABASE_VERSION = 1;

  /**
   * Constructor for DBConexion.
   *
   * @param context The application context.
   */
  public DBConexion(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * Called when the database is created for the first time.
   * This method creates the 'usuarios' and 'incidencias' tables.
   *
   * @param db The SQLite database.
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    // SQL command to create the 'usuarios' table
    String CREATE_TABLE_USUARIOS = "CREATE TABLE usuarios (" +
      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
      "nombre TEXT NOT NULL," +
      "email TEXT NOT NULL UNIQUE," +
      "password TEXT NOT NULL," +
      "lastLogin TEXT NOT NULL" +
      ");";

    // SQL command to create the 'incidencias' table
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

    // Execute the SQL statements
    db.execSQL(CREATE_TABLE_USUARIOS);
    db.execSQL(CREATE_TABLE_INCIDENT);
  }

  /**
   * Called when the database needs to be upgraded.
   * Drops the existing tables and recreates them.
   *
   * @param db         The SQLite database.
   * @param oldVersion The old database version.
   * @param newVersion The new database version.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS usuarios");
    db.execSQL("DROP TABLE IF EXISTS incidencias");
    onCreate(db);
  }
}
