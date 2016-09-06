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
public class ContactPersoon {
    private int id;
    private String voornaam;
    private String achternaam;
    private String telefoon;
    private String email;

    public ContactPersoon(int id, String voornaam, String achternaam, String telefoon, String email) {
        this.id = id;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.telefoon = telefoon;
        this.email = email;
    }    

    public ContactPersoon(String voornaam, String achternaam, String telefoon, String email) {
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.telefoon = telefoon;
        this.email = email;
    }

    public ContactPersoon() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public String getTelefoon() {
        return telefoon;
    }

    public void setTelefoon(String telefoon) {
        this.telefoon = telefoon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isEmpty() {
        return (this.voornaam == null || this.voornaam.equals("")) && 
               (this.achternaam == null || this.achternaam.equals("")) && 
               (this.telefoon == null || this.telefoon.equals("")) && 
               (this.email == null || this.email.equals(""));
    }
}
