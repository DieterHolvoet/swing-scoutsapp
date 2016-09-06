/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Dieter
 */
public class Bijeenkomst {
    private int bijeenkomstID;
    private String typeBijeenkomst;
    private int takID;
    private Date startDatum;
    private Time startTijd;
    private Date eindDatum;
    private Time eindTijd;

    public Bijeenkomst() {
    }

    public int getBijeenkomstID() {
        return bijeenkomstID;
    }

    public void setBijeenkomstID(int bijeenkomstID) {
        this.bijeenkomstID = bijeenkomstID;
    }

    public String getTypeBijeenkomst() {
        return typeBijeenkomst;
    }

    public void setTypeBijeenkomst(String typeBijeenkomst) {
        this.typeBijeenkomst = typeBijeenkomst;
    }

    public int getTakID() {
        return takID;
    }

    public void setTakID(int takID) {
        this.takID = takID;
    }

    public Date getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    public Time getStartTijd() {
        return startTijd;
    }

    public void setStartTijd(Time startTijd) {
        this.startTijd = startTijd;
    }

    public Date getEindDatum() {
        return eindDatum;
    }

    public void setEindDatum(Date eindDatum) {
        this.eindDatum = eindDatum;
    }

    public Time getEindTijd() {
        return eindTijd;
    }

    public void setEindTijd(Time eindTijd) {
        this.eindTijd = eindTijd;
    }
}
