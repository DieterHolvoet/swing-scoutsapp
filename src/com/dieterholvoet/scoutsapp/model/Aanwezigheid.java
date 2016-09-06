/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

/**
 *
 * @author Dieter
 */
public class Aanwezigheid {
    private int aanwezigheidID;
    private int lidnr;
    private int bijeenkomstID;
    private boolean isAanwezig;
    private boolean heeftOpVoorhandVerwittigd;
    private boolean isTeLaat;

    public Aanwezigheid() {
    }

    public int getAanwezigheidID() {
        return aanwezigheidID;
    }

    public void setAanwezigheidID(int aanwezigheidID) {
        this.aanwezigheidID = aanwezigheidID;
    }

    public int getLidnr() {
        return lidnr;
    }

    public void setLidnr(int lidnr) {
        this.lidnr = lidnr;
    }

    public int getBijeenkomstID() {
        return bijeenkomstID;
    }

    public void setBijeenkomstID(int bijeenkomstID) {
        this.bijeenkomstID = bijeenkomstID;
    }

    public boolean isIsAanwezig() {
        return isAanwezig;
    }

    public void setIsAanwezig(boolean isAanwezig) {
        this.isAanwezig = isAanwezig;
    }

    public boolean isHeeftOpVoorhandVerwittigd() {
        return heeftOpVoorhandVerwittigd;
    }

    public void setHeeftOpVoorhandVerwittigd(boolean heeftOpVoorhandVerwittigd) {
        this.heeftOpVoorhandVerwittigd = heeftOpVoorhandVerwittigd;
    }

    public boolean isIsTeLaat() {
        return isTeLaat;
    }

    public void setIsTeLaat(boolean isTeLaat) {
        this.isTeLaat = isTeLaat;
    }
}
