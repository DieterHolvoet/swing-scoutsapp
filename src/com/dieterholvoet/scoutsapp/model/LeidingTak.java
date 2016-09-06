/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

import java.sql.Date;

/**
 *
 * @author Dieter
 */
public class LeidingTak {
    private int _id;
    private int takID;
    private java.sql.Date startDate;
    private java.sql.Date endDate;
    private Lid leiding;

    public LeidingTak() {
    }

    public LeidingTak(int _id, int takID, Date startDate, Date endDate, Lid leiding) {
        this._id = _id;
        this.takID = takID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leiding = leiding;
    }

    public LeidingTak(int takID, Date startDate, Date endDate, Lid leiding) {
        this.takID = takID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leiding = leiding;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public Lid getLeiding() {
        return leiding;
    }

    public void setLeiding(Lid leiding) {
        this.leiding = leiding;
    }

    public int getTakID() {
        return takID;
    }

    public void setTakID(int takID) {
        this.takID = takID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        if(this.leiding != null) {
            return this.leiding.toString() + " (" + startDate.toString() + (endDate == null ? ")" : " tot " + endDate + ")");
            
        } else {
            return "Onbekende naam (" + startDate.toString() + (endDate == null ? ")" : " tot " + endDate + ")");
        }
    }
}
