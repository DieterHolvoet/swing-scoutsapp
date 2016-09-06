/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public class Lid {
    private long lidnr;
    private String voornaam;
    private String achternaam;
    private String geslacht;
    private int takID;
    private Date geboortedatum;
    private String telefoon;
    private String gsm;
    private String email;
    private String adres;
    private boolean heeftBetaald;
    private boolean neemtWindroos;
    private String totem;
    private String adjectief;
    private String aandachtspunten;
    private ArrayList<ContactPersoon> contactpersonen;

    public Lid() {
        this.contactpersonen = new ArrayList<>();
    }

    public Lid(long lidnr) {
        this.contactpersonen = new ArrayList<>();
        this.lidnr = lidnr;
    }

    public Lid(String voornaam, String achternaam) {
        this.contactpersonen = new ArrayList<>();
        this.voornaam = voornaam;
        this.achternaam = achternaam;
    }

    public Lid(long lidnr, String voornaam, String achternaam, String geslacht, int takID, Date geboortedatum, String telefoon, String gsm, String email, String adres, boolean heeftBetaald, boolean neemtWindroos, String totem, String adjectief, String aandachtspunten) {
        this.contactpersonen = new ArrayList<>();
        this.lidnr = lidnr;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.geslacht = geslacht;
        this.takID = takID;
        this.geboortedatum = geboortedatum;
        this.telefoon = telefoon;
        this.gsm = gsm;
        this.email = email;
        this.adres = adres;
        this.heeftBetaald = heeftBetaald;
        this.neemtWindroos = neemtWindroos;
        this.totem = totem;
        this.adjectief = adjectief;
        this.aandachtspunten = aandachtspunten;
    }

    public ArrayList<ContactPersoon> getContactpersonen() {
        return contactpersonen;
    }

    public void setContactpersonen(ArrayList<ContactPersoon> contactpersonen) {
        this.contactpersonen = contactpersonen;
    }

    public long getLidnr() {
        return lidnr;
    }

    public void setLidnr(long lidnr) {
        this.lidnr = lidnr;
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

    public String getGeslacht() {
        return geslacht;
    }

    public void setGeslacht(String geslacht) {
        this.geslacht = geslacht;
    }

    public int getTakID() {
        return takID;
    }

    public void setTakID(int takID) {
        this.takID = takID;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public String getTelefoon() {
        return telefoon;
    }

    public void setTelefoon(String telefoon) {
        this.telefoon = telefoon;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public boolean getHeeftBetaald() {
        return heeftBetaald;
    }

    public void setHeeftBetaald(boolean heeftBetaald) {
        this.heeftBetaald = heeftBetaald;
    }

    public boolean getNeemtWindroos() {
        return neemtWindroos;
    }

    public void setNeemtWindroos(boolean neemtWindroos) {
        this.neemtWindroos = neemtWindroos;
    }

    public String getTotem() {
        return totem;
    }

    public void setTotem(String totem) {
        this.totem = totem;
    }

    public String getAdjectief() {
        return adjectief;
    }

    public void setAdjectief(String adjectief) {
        this.adjectief = adjectief;
    }

    public String getAandachtspunten() {
        return aandachtspunten;
    }

    public void setAandachtspunten(String aandachtspunten) {
        this.aandachtspunten = aandachtspunten;
    }
    
    @Override
    public String toString() {
        return this.voornaam + " " + this.achternaam;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Lid) {
            Lid lid = (Lid) obj;
            if(this.lidnr != 0) {
                return this.lidnr == lid.getLidnr();
                
            } else if(this.voornaam != null & this.achternaam != null) {
                return this.voornaam.equals(lid.getVoornaam()) && this.achternaam.equals(lid.getAchternaam());
            
            } else {
                return false;
            }
            
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.takID;
        return hash;
    }
    
    public boolean isLeiding() {
        return this.takID == 1;
    }
}
