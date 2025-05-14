package com.example.myapplication;

public class Usuario {
  public String nombre;
  public String password;
  public String email;

  public int id;
  public String path_avatar;

  public Usuario(int id, String nombre, String password) {
    this.id=id;
    this.nombre = nombre;
    this.password = password;
  }
  public Usuario(int id, String nombre, String password, String path_avatar) {
    this.id=id;
    this.nombre = nombre;
    this.password = password;
    this.path_avatar=path_avatar;
  }
  public Usuario(String nombre,int id, String email, String path_avatar) {
    this.id=id;
    this.nombre = nombre;
    this.email = email;
    this.path_avatar=path_avatar;
  }

  public int getId() {
    return id;
  }
  public String getNombre() {
    return nombre;
  }

  public String getPassword() {
    return password;
  }

  public String getPath_avatar() {return path_avatar; }

  public String getEmail() { return email;}
}
