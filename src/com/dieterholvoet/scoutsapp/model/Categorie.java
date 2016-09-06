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
class Categorie {
    private int categorieID;
    private int parentID;
    private String naam;

    public Categorie() {
    }

    public Categorie(int categorieID, int parentID, String naam) {
        this.categorieID = categorieID;
        this.parentID = parentID;
        this.naam = naam;
    }

    public int getCategorieID() {
        return categorieID;
    }

    public void setCategorieID(int categorieID) {
        this.categorieID = categorieID;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }
}
