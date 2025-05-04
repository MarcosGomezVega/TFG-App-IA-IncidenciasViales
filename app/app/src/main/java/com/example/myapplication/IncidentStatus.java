package com.example.myapplication;

public class IncidentStatus {

  int id;
  String incident_type;
  String localitation;
  String photo;
  String date;
  String status;

  public IncidentStatus() {
  }
  public IncidentStatus(int id, String incident_type, String localitation, String photo, String date, String status) {
    this.id = id;
    this.incident_type = incident_type;
    this.localitation = localitation;
    this.photo = photo;
    this.date = date;
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public String getIncident_type() {
    return incident_type;
  }

  public String getLocalitation() {
    return localitation;
  }

  public String getPhoto() {
    return photo;
  }

  public String getDate() {
    return date;
  }

  public String getStatus() {
    return status;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setIncident_type(String incident_type) {
    this.incident_type = incident_type;
  }

  public void setLocalitation(String localitation) {
    this.localitation = localitation;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
