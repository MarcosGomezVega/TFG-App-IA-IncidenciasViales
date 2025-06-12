package com.example.myapplication;

import com.google.firebase.firestore.PropertyName;

public class Incident {
  private String uid;
  private String incidentType;
  private String localitation;
  private String photo;
  private String date;
  private String status;
  private String userID;

  public Incident() {}

  public Incident(String uid, String incidentType, String localitation, String photo, String date, String status, String userID) {
    this.uid = uid;
    this.incidentType = incidentType;
    this.localitation = localitation;
    this.photo = photo;
    this.date = date;
    this.status = status;
    this.userID = userID;
  }

  public String getUid() {
    return uid;
  }

  @PropertyName("incident_type")
  public String getIncidentType() {
    return incidentType;
  }

  @PropertyName("incident_type")
  public void setIncidentType(String incidentType) {
    this.incidentType = incidentType;
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

  @PropertyName("user_id")
  public String getUserId() {
    return userID;
  }

  @PropertyName("user_id")
  public void setUserId(String userID) {
    this.userID = userID;
  }
}
