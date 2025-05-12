package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.Incident;
import com.example.myapplication.IncidentStatus;
import com.example.myapplication.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DBManager handles all database operations for the application, including user management
 * and incident recording.
 */
public class DBManager {
  private DBConexion dbConexion;
  private SQLiteDatabase db;
  private Context context;
  private Cursor cursor;

  /**
   * Constructor for DBManager.
   *
   * @param context The application context.
   */
  public DBManager(Context context) {
    this.context = context;
    dbConexion = new DBConexion(context);
  }

  /**
   * Opens a writable connection to the database.
   */
  public void open() {
    db = dbConexion.getWritableDatabase();
  }

  /**
   * Closes the database connection.
   */
  public void close() {
    dbConexion.close();
  }

  /**
   * Inserts a new user into the database.
   *
   * @param nombre   User name.
   * @param email    User email.
   * @param password User password.
   * @param date     Last login date.
   * @return true if the insertion was successful, false otherwise.
   */
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

  /**
   * Inserts a new incident report into the database.
   *
   * @param usuarioId      ID of the user reporting the incident.
   * @param tipoIncidencia Type of the incident.
   * @param localizacion   Location of the incident.
   * @param fotoRuta       Path to the incident photo.
   * @param fecha          Date of the incident.
   * @param status         Status of the incident.
   * @return true if the insertion was successful, false otherwise.
   */
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

  /**
   * Validates whether a user with the given email and password exists.
   *
   * @param email    User email.
   * @param password User password.
   * @return true if user exists, false otherwise.
   */
  public boolean validarUsuario(String email, String password) {
    open();

    String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
    Cursor cursor = db.rawQuery(query, new String[]{email, password});

    boolean existe = cursor.getCount() > 0;
    cursor.close();
    close();

    return existe;
  }

  /**
   * Checks if a user ID exists in the database.
   *
   * @param userId The user ID to check.
   * @return true if the user exists, false otherwise.
   */
  public boolean userExistsInDatabase(int userId) {
    open();

    String query = "SELECT COUNT(*) FROM usuarios WHERE id = ?";
    cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
    boolean exists = false;

    if (cursor.moveToFirst()) {
      int count = cursor.getInt(0);
      exists = count > 0;
    }
    cursor.close();
    close();
    return exists;
  }

  /**
   * Retrieves the ID of the most recently logged-in user.
   *
   * @return The user ID, or -1 if not found.
   */
  public int obtenerIdUltimoUsuario() {
    open();
    cursor = db.rawQuery("SELECT id FROM usuarios ORDER BY datetime(lastLogin) DESC LIMIT 1", null);
    int userId = -1;
    if (cursor.moveToFirst()) {
      userId = cursor.getInt(0);
    }
    cursor.close();
    close();
    return userId;
  }

  /**
   * Retrieves a user ID based on the provided email.
   *
   * @param correo The user's email.
   * @return The user ID, or -1 if not found.
   */
  public int obtnerIdPorCorreo(String correo) {
    open();
    int userID = -1;
    String query = "SELECT id FROM usuarios WHERE email = ?";
    cursor = db.rawQuery(query, new String[]{correo});
    if (cursor.moveToFirst()) {
      userID = cursor.getInt(0);
    }
    cursor.close();
    close();
    return userID;
  }

  /**
   * Retrieves a list of incidents for a given user ID.
   *
   * @param user_id The ID of the user.
   * @return List of Incident objects.
   */
  public List<Incident> obtenerListaIncidencias(int user_id) {
    List<Incident> lista = new ArrayList<>();
    open();
    String query = "SELECT  id, tipo_incidencia, status FROM incidencias WHERE usuario_id =? ";
    cursor = db.rawQuery(query, new String[]{String.valueOf(user_id)});
    if (cursor.moveToFirst()) {
      do {
        String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_incidencia"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        lista.add(new Incident(tipo, id, status));
      } while (cursor.moveToNext());
    }
    cursor.close();
    close();
    return lista;

  }

  /**
   * Retrieves the details of a specific incident by its ID.
   *
   * @param id The incident ID.
   * @return An IncidentStatus object, or null if not found.
   */
  public IncidentStatus obtenerIncidencias(int id) {
    open();
    IncidentStatus result = null;

    String query = "SELECT id,tipo_incidencia,localizacion,foto,fecha,status " +
      "FROM incidencias " +
      "WHERE id = ? ";
    try {
      cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

      boolean tieneDatos = cursor.moveToFirst();

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
    } catch (Exception e) {
    }
    cursor.close();
    close();
    return result;

  }

  /**
   * Retrieves a Usuario object by email.
   *
   * @param email The user's email.
   * @return A Usuario object or null if not found.
   */
  public Usuario obtenerUsuarioPorCorreo(String email) {
    open();
    Usuario usuario = null;

    String query = "SELECT id,nombre, password FROM usuarios WHERE email = ?";
    Cursor cursor = db.rawQuery(query, new String[]{email});

    if (cursor.moveToFirst()) {
      int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
      String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
      String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
      usuario = new Usuario(id, nombre, password);
    }

    cursor.close();
    close();
    return usuario;
  }

  /**
   * Updates the last login date of a user.
   *
   * @param usuarioId The ID of the user.
   */
  public void actualizarFechaLogin(int usuarioId) {
    open();

    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

    ContentValues values = new ContentValues();
    values.put("lastLogin", date);

    db.update("usuarios", values, "id = ?", new String[]{String.valueOf(usuarioId)});

    close();
  }

  /**
   * Updates the email address of a user.
   *
   * @param correoActual Current email.
   * @param nuevoCorreo  New email to set.
   * @return true if the update was successful, false otherwise.
   */
  public boolean actualizarCorreo(String correoActual, String nuevoCorreo) {
    open();
    ContentValues values = new ContentValues();
    values.put("email", nuevoCorreo);

    int rows = db.update("usuarios", values, "email = ?", new String[]{correoActual});
    close();
    return rows > 0;
  }

  /**
   * Updates the password for a user identified by email.
   *
   * @param email       User email.
   * @param newPassword New password to set.
   * @return true if the update was successful, false otherwise.
   */
  public boolean actualizarPassword(String email, String newPassword) {
    open();
    ContentValues values = new ContentValues();
    values.put("password", newPassword);

    int filasAfectadas = db.update("usuarios", values, "email = ?", new String[]{email});
    close();
    return filasAfectadas > 0;
  }

  public boolean deleteUser(int user_id){
    open();
    int rowsDeleted = db.delete("usuarios", "id = ?", new String[]{String.valueOf(user_id)});
    close();
    return rowsDeleted>0;
  }
}
