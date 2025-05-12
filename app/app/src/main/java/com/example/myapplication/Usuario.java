package com.example.myapplication;

public class Usuario {
  public String nombre;
  public String password;
  public int id;

  public Usuario(int id, String nombre, String password) {
    this.id=id;
    this.nombre = nombre;
    this.password = password;
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

}
