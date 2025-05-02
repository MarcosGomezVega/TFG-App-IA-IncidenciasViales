package com.example.myapplication;

public class Incident {

  private String tipo;
  private int id;

  public Incident(String tipo, int id) {
    this.tipo = tipo;
    this.id = id;
  }

  public String getTipo() { return tipo; }
  public int getId(){return id;}

}
