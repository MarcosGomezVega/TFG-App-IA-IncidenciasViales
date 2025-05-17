package com.example.myapplication;

import com.google.firebase.firestore.PropertyName;

public class Incident {
  private String uid;
  private String tipoIncidencia;
  private String localizacion;
  private String foto;
  private String fecha;
  private String status;
  private String usuarioId;

  public Incident() {

  }

  public Incident(String uid, String tipoIncidencia, String localizacion, String foto, String fecha, String status, String usuarioId) {
    this.uid = uid;
    this.tipoIncidencia = tipoIncidencia;
    this.localizacion = localizacion;
    this.foto = foto;
    this.fecha = fecha;
    this.status = status;
    this.usuarioId = usuarioId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @PropertyName("tipo_incidencia")
  public String getTipoIncidencia() {
    return tipoIncidencia;
  }

  @PropertyName("tipo_incidencia")
  public void setTipoIncidencia(String tipoIncidencia) {
    this.tipoIncidencia = tipoIncidencia;
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

  public String getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(String usuarioId) {
    this.usuarioId = usuarioId;
  }
}
