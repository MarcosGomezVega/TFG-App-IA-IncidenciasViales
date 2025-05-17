package com.example.myapplication;

public class Usuario {
  private String nombre;
  private String password;
  private String email;

  private int id;
  private String pathAvatar;

  public Usuario(int id, String nombre, String password) {
    this.id=id;
    this.nombre = nombre;
    this.password = password;
  }
  public Usuario(int id, String nombre, String password, String pathAvatar) {
    this.id=id;
    this.nombre = nombre;
    this.password = password;
    this.pathAvatar=pathAvatar;
  }
  public Usuario(String nombre,int id, String email, String pathAvatar) {
    this.id=id;
    this.nombre = nombre;
    this.email = email;
    this.pathAvatar=pathAvatar;
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

  public String getPathAvatar() {return pathAvatar; }

  public String getEmail() { return email;}
}
