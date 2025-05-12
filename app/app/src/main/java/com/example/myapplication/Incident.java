package com.example.myapplication;

public class Incident {
  private String tipo;

  private String status;
  private int id;


  public Incident(String tipo, int id, String status) {
    this.tipo = tipo;
    this.id = id;
    this.status=status;
  }

  public String getTipo() { return tipo; }
  public int getId(){return id;}
  public String getStatus(){return status;}

}
