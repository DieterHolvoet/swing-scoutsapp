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
public class Huurovereenkomst {
    private int verhuurID;
    private String groepsnaam;
    private String tak;
    private ContactPersoon vertegenwoordiger;
    private int huurprijs;
    private int waarborg;
    private Date startDatum;
    private Time aankomstTijd;
    private Date eindDatum;
    private Time vertrekTijd;

    public Huurovereenkomst() {
    }

    public int getVerhuurID() {
        return verhuurID;
    }

    public void setVerhuurID(int verhuurID) {
        this.verhuurID = verhuurID;
    }

    public String getGroepsnaam() {
        return groepsnaam;
    }

    public void setGroepsnaam(String groepsnaam) {
        this.groepsnaam = groepsnaam;
    }

    public String getTak() {
        return tak;
    }

    public void setTak(String tak) {
        this.tak = tak;
    }

    public ContactPersoon getVertegenwoordiger() {
        return vertegenwoordiger;
    }

    public void setVertegenwoordiger(ContactPersoon vertegenwoordiger) {
        this.vertegenwoordiger = vertegenwoordiger;
    }

    public int getHuurprijs() {
        return huurprijs;
    }

    public void setHuurprijs(int huurprijs) {
        this.huurprijs = huurprijs;
    }

    public int getWaarborg() {
        return waarborg;
    }

    public void setWaarborg(int waarborg) {
        this.waarborg = waarborg;
    }

    public Date getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    public Time getAankomstTijd() {
        return aankomstTijd;
    }

    public void setAankomstTijd(Time aankomstTijd) {
        this.aankomstTijd = aankomstTijd;
    }

    public Date getEindDatum() {
        return eindDatum;
    }

    public void setEindDatum(Date eindDatum) {
        this.eindDatum = eindDatum;
    }

    public Time getVertrekTijd() {
        return vertrekTijd;
    }

    public void setVertrekTijd(Time vertrekTijd) {
        this.vertrekTijd = vertrekTijd;
    }
}
