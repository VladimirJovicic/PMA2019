package com.example.donesiklon.model;

import java.util.Date;

public class VisitHistory {
    private String userId;
    private String restorauntId;
    private Date date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestorauntId() {
        return restorauntId;
    }

    public void setRestorauntId(String restorauntId) {
        this.restorauntId = restorauntId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
