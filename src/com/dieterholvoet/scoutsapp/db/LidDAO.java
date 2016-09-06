/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.db;

import com.dieterholvoet.scoutsapp.gui.Splash;
import com.dieterholvoet.scoutsapp.gui.WerkstukGUI;
import com.dieterholvoet.scoutsapp.model.Lid;
import java.awt.Frame;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public class LidDAO {
    public static final int FETCH_NOTHING = 0;
    public static final int FETCH_ALL = 1;
        
    public static ArrayList<Lid> getLeden(int status) {
        return getLeden(status, -1);
    }
    
    public static ArrayList<Lid> getLeden(int status, int totalSize) {
        ArrayList<Lid> resultaat = new ArrayList<>();
        boolean doProgress = totalSize != -1;
        int size = 0;
        
        try {
            ResultSet sizeSet = Database.voerSqlUitEnHaalResultaatOp("SELECT COUNT(*) AS count FROM leden");
            ResultSet ledenSet = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leden");
            
            if(sizeSet != null) {
                if (sizeSet.isBeforeFirst() ) {
                    sizeSet.first();
                    size = sizeSet.getInt("count");
                }
            }
            
            if (ledenSet != null) {
                int index = 0;
                while (ledenSet.next()) {
                    Lid lid = converteerHuidigeRijNaarObject(ledenSet);
                    if(status == FETCH_ALL) {
                        lid.setContactpersonen(ContactPersoonDAO.getContactPersonenByLidnr(lid));
                    }
                    if(doProgress) {
                        Splash.updateProgress(new Integer[] {index, size, totalSize});
                    }
                    resultaat.add(lid);
                    index++;
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static ArrayList<Lid> getLedenByTakID(int takID, int status) {
        ArrayList<Lid> resultaat = new ArrayList<>();
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leden WHERE takID = ?", new Object[] { takID });
            if (mijnResultset != null) {
                while (mijnResultset.next()) {
                    Lid lid = converteerHuidigeRijNaarObject(mijnResultset);
                    if(status == FETCH_ALL) {
                        lid.setContactpersonen(ContactPersoonDAO.getContactPersonenByLidnr(lid));
                    }
                    resultaat.add(lid);
                }
            }
                
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }

    public static Lid getLidByLidnr(long lidnr, int status) {
        Lid resultaat = null;
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leden WHERE lidnr = ?", new Object[] { lidnr });
            if (mijnResultset.isBeforeFirst() ) {
                mijnResultset.first();
                resultaat = converteerHuidigeRijNaarObject(mijnResultset);
                if(status == FETCH_ALL) {
                    resultaat.setContactpersonen(ContactPersoonDAO.getContactPersonenByLidnr(resultaat));
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static boolean lidExists(long lidnr) {
        return LidDAO.getLidByLidnr(lidnr, FETCH_NOTHING) != null;
    }

    public static int voegLidToe(Lid nieuweLid) {
        int aantalAangepasteRijen = 0;
        try {
            Field[] fields = nieuweLid.getClass().getDeclaredFields();
            Object[] values = new Object[fields.length - 1];
            String insertString = "INSERT INTO leden (";
            
            int index = 0;
            for (Field field : fields) {
                String fieldName = field.getName();
                if(!fieldName.equals("contactpersonen")) {
                    String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = nieuweLid.getClass().getMethod(methodName, new Class[] {});
                    Object fieldValue = method.invoke(nieuweLid);

                    insertString += fieldName + ", ";
                    values[index] = fieldValue;
                }
                index++;
            }
            insertString = insertString.substring(0, insertString.length()-2) + ") VALUES (";
            
            for (int i = 0; i < values.length; i++) {
                insertString += "?,";
            }
            
            insertString =  insertString.substring(0, insertString.length()-1) + ")";
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp(insertString, values);
            
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }

    public static int updateLid(Lid nieuweLid) {
        int aantalAangepasteRijen = 0;
        try {
            Field[] fields = nieuweLid.getClass().getDeclaredFields();
            Object[] values = new Object[fields.length - 1];
            String insertString = "UPDATE leden SET ";
            
            int index = 0;
            for(Field field : fields) {
                String fieldName = field.getName();
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = nieuweLid.getClass().getMethod(methodName, new Class[] {});
                Object fieldValue = method.invoke(nieuweLid);
                
                if(fieldName.equals("lidnr")) {
                    values[fields.length - 2] = fieldValue;
                            
                } else if(!fieldName.equals("contactpersonen")) {
                    insertString += fieldName + " = ?, ";
                    values[index] = fieldValue;
                    index++;
                }
            }

            insertString = insertString.substring(0, insertString.length() - 2) + " WHERE lidnr = ?";
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp(insertString, values);
            
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException ex){
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }

    public static int verwijderLid(long lidnr) {
        int aantalAangepasteRijen = 0;
        try {
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp("DELETE FROM leden WHERE lidnr = ?", new Object[] { lidnr });
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }
    
    private static Lid converteerHuidigeRijNaarObject(ResultSet mijnResultset) throws SQLException {
        return new Lid(
            mijnResultset.getBigDecimal("lidnr").longValue(),
            mijnResultset.getString("voornaam"),
            mijnResultset.getString("achternaam"),
            mijnResultset.getString("geslacht"),
            mijnResultset.getInt("takID"),
            mijnResultset.getDate("geboortedatum"),
            mijnResultset.getString("telefoon"),
            mijnResultset.getString("gsm"),
            mijnResultset.getString("email"),
            mijnResultset.getString("adres"),
            mijnResultset.getBoolean("heeftBetaald"),
            mijnResultset.getBoolean("neemtWindroos"),
            mijnResultset.getString("totem"),
            mijnResultset.getString("adjectief"),
            mijnResultset.getString("aandachtspunten")
        );
    }
}
