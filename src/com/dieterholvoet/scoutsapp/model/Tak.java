/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public class Tak {
    private int takID;
    private String naam;
    private ArrayList<LeidingTak> huidigeLeiding;
    private ArrayList<LeidingTak> vroegereLeiding;

    public Tak() {
        this.takID = 0;
        this.naam = "";
        this.huidigeLeiding = new ArrayList<>();
        this.vroegereLeiding = new ArrayList<>();
    }

    public Tak(int takID) {
        this.takID = takID;
    }

    public Tak(int takID, String naam) {
        this.takID = takID;
        this.naam = naam;
        this.huidigeLeiding = new ArrayList<>();
        this.vroegereLeiding = new ArrayList<>();
    }

    public ArrayList<LeidingTak> getHuidigeLeiding() {
        return huidigeLeiding;
    }

    public void setHuidigeLeiding(ArrayList<LeidingTak> huidigeLeiding) {
        this.huidigeLeiding = huidigeLeiding;
    }

    public ArrayList<LeidingTak> getVroegereLeiding() {
        return vroegereLeiding;
    }

    public void setVroegereLeiding(ArrayList<LeidingTak> vroegereLeiding) {
        this.vroegereLeiding = vroegereLeiding;
    }

    public Tak(String naam) {
        this.naam = naam;
    }

    public int getTakID() {
        return takID;
    }

    public void setTakID(int takID) {
        this.takID = takID;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    @Override
    public String toString() {
        return naam;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Tak) {
            return this.takID == ((Tak) obj).getTakID();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.takID;
        return hash;
    }
}
