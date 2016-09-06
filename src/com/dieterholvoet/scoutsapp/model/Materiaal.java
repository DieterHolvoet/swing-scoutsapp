/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

/*
 * @author Dieter
 */

public class Materiaal {
    private int id;
    private String naam;
    private int hoeveelheid;

    public Materiaal() {
    }

    public Materiaal(int id, String naam, int hoeveelheid) {
        this.id = id;
        this.naam = naam;
        this.hoeveelheid = hoeveelheid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public int getHoeveelheid() {
        return hoeveelheid;
    }

    public void setHoeveelheid(int hoeveelheid) {
        this.hoeveelheid = hoeveelheid;
    }
    
}