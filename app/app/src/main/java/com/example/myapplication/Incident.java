package com.example.myapplication;

import com.google.firebase.firestore.PropertyName;

public class Incident {
  private String uid;
  private String tipo_incidencia;
  private String localizacion;
  private String foto;
  private String fecha;
  private String status;
  private String usuario_id;

  public Incident() {

  }

  public Incident(String uid, String tipo_incidencia, String localizacion, String foto, String fecha, String status, String usuario_id) {
    this.uid = uid;
    this.tipo_incidencia = tipo_incidencia;
    this.localizacion = localizacion;
    this.foto = foto;
    this.fecha = fecha;
    this.status = status;
    this.usuario_id = usuario_id;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @PropertyName("tipo_incidencia")
  public String getTipoIncidencia() {
    return tipo_incidencia;
  }

  @PropertyName("tipo_incidencia")
  public void setTipoIncidencia(String tipo_incidencia) {
    this.tipo_incidencia = tipo_incidencia;
  }

  public String getLocalizacion() {
    return localizacion;
  }

  public void setLocalizacion(String localizacion) {
    this.localizacion = localizacion;
  }

  public String getFoto() {
    return foto;
  }

  public void setFoto(String foto) {
    this.foto = foto;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUsuario_id() {
    return usuario_id;
  }

  public void setUsuario_id(String usuario_id) {
    this.usuario_id = usuario_id;
  }
}
