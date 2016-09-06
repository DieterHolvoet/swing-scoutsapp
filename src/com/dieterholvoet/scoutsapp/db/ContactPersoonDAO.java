/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.db;

import com.dieterholvoet.scoutsapp.model.ContactPersoon;
import com.dieterholvoet.scoutsapp.model.Lid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public class ContactPersoonDAO {
    public static ArrayList<ContactPersoon> getContactPersonenByLidnr(Lid lid) {
        ArrayList<ContactPersoon> resultaat = new ArrayList<>();
        long lidnr = lid.getLidnr();
        
        try {
            ResultSet mijnResultset;
            if(lid.isLeiding()) {
                mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding_contactpersonen WHERE lidnr = ?", new Object[] { lidnr });
                
            } else {
                mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leden_contactpersonen WHERE lidnr = ?", new Object[] { lidnr });
            }
            
            if (mijnResultset != null) {
                while (mijnResultset.next()) {
                    int contactID = mijnResultset.getInt("contactID");
                    ResultSet contact = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM contactpersonen WHERE contactID = ?", new Object[] { contactID });
                    
                    if (contact.isBeforeFirst() ) {
                        contact.first();
                        resultaat.add(converteerHuidigeRijNaarObject(contact));
                    }
                }
            }
                
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static int voegContactPersoonToe(ContactPersoon c, Lid lid) {
        int relatieID = 0;
        
        try {
            Object[] values = new Object[] {c.getVoornaam(), c.getAchternaam(), c.getTelefoon(), c.getEmail()};
            String insertString = "INSERT INTO contactpersonen (voornaam, achternaam, telefoon, email) VALUES (?, ?, ?, ?)";
            c.setId(Database.voerSqlUitEnHaalIDOp(insertString, values));
            
            if(lid.getLidnr() != 0) {
                relatieID = voegContactPersoonRelatieToe(c, lid);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return c.getId();
    }
    
    public static int voegContactPersoonToe(ContactPersoon c) {
        return voegContactPersoonToe(c, new Lid());
    }
    
    public static int voegContactPersoonRelatieToe(ContactPersoon contact, Lid lid) {
        long lidnr = lid.getLidnr();
        int relatieID = 0;
        
        try {
            if(lidnr != 0 && contact.getId() != 0) {
                if(lid.isLeiding()) {
                    relatieID = Database.voerSqlUitEnHaalIDOp("INSERT INTO leiding_contactpersonen (lidnr, contactID) VALUES (?, ?)", new Object[] {lidnr, contact.getId()});
                
                } else {
                    relatieID = Database.voerSqlUitEnHaalIDOp("INSERT INTO leden_contactpersonen (lidnr, contactID) VALUES (?, ?)", new Object[] {lidnr, contact.getId()});
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return relatieID;
    }
    
    public static boolean contactPersoonExists(ContactPersoon c) {
        return getContactPersoonByName(c.getVoornaam(), c.getAchternaam()) != null;
    }
    
    public static ContactPersoon getContactPersoonByName(String voornaam, String achternaam) {
        ContactPersoon resultaat = null;
        try {
            Object[] values = new Object[] {voornaam, achternaam};
            String insertString = "SELECT * FROM contactpersonen WHERE voornaam = ? AND achternaam = ?";
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp(insertString, values);
            
            if (mijnResultset != null) {
                if(mijnResultset.isBeforeFirst()) {
                    mijnResultset.first();
                    resultaat = converteerHuidigeRijNaarObject(mijnResultset);
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static int updateContactPersoon(ContactPersoon c) {
        int aantalAangepasteRijen = 0;
        try {
            Object[] values = new Object[] {c.getVoornaam(), c.getAchternaam(), c.getTelefoon(), c.getEmail()};
            String insertString = "UPDATE contactpersonen SET voornaam = ?, achternaam = ?, telefoon = ?, email = ?";
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp(insertString, values);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }
    
    private static ContactPersoon converteerHuidigeRijNaarObject(ResultSet mijnResultset) throws SQLException {
        return new ContactPersoon (
            mijnResultset.getInt("contactID"),
            mijnResultset.getString("voornaam"),
            mijnResultset.getString("achternaam"),
            mijnResultset.getString("telefoon"),
            mijnResultset.getString("email")
        );
    }
}
