/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.db;

import com.dieterholvoet.scoutsapp.gui.Splash;
import com.dieterholvoet.scoutsapp.model.LeidingTak;
import com.dieterholvoet.scoutsapp.model.Lid;
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

public class LeidingDAO {
    
    public static final int FETCH_NOTHING = 0;
    public static final int FETCH_ALL = 1;
    
    public static ArrayList<Lid> getLeiding(int status) {
        return getLeiding(status, -1);
    }
        
    public static ArrayList<Lid> getLeiding(int status, int totalSize) {
        ArrayList<Lid> resultaat = new ArrayList<>();
        boolean doProgress = totalSize != -1;
        int size = 0;
        
        try {
            ResultSet sizeSet = Database.voerSqlUitEnHaalResultaatOp("SELECT COUNT(*) AS count FROM leiding");
            ResultSet ledenSet = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding");
            
            if(sizeSet != null) {
                if (sizeSet.isBeforeFirst() ) {
                    sizeSet.first();
                    size = sizeSet.getInt("count");
                }
            }
            
            if (ledenSet != null) {
                int index = 0;
                while (ledenSet.next()) {
                    Lid lid = converteerHuidigeRijNaarLeidingObject(ledenSet);
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
    
    public static ArrayList<LeidingTak> getCurrentLeidingByTakID(int ID) {
        ArrayList<LeidingTak> resultaat = new ArrayList<>();

        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding_takken WHERE takID = ? AND end_date IS NULL", new Object[] { ID });
            if (mijnResultset != null) {
                while (mijnResultset.next()) {
                    LeidingTak l = converteerHuidigeRijNaarLeidingTakObject(mijnResultset);
                    resultaat.add(l);
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static ArrayList<LeidingTak> getPastLeidingByTakID(int ID) {
        ArrayList<LeidingTak> resultaat = new ArrayList<>();

        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding_takken WHERE takID = ? AND end_date IS NOT NULL", new Object[] { ID });
            if (mijnResultset != null) {
                while (mijnResultset.next()) {
                    LeidingTak l = converteerHuidigeRijNaarLeidingTakObject(mijnResultset);
                    resultaat.add(l);
                }
            }
            
        } catch (SQLException ex) {
                ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static int voegLeidingTakRelatieToe(LeidingTak lt) {
        int id = 0;
        try {
            if(lt.getId() == 0) {
                Object[] values = new Object[] {lt.getLeiding().getLidnr(), lt.getTakID(), lt.getStartDate(), lt.getEndDate()};
                String insertString = "INSERT INTO leiding_takken (leidingID, takID, start_date, end_date) VALUES (?, ?, ?, ?)";
                id = Database.voerSqlUitEnHaalIDOp(insertString, values);
                
            } else {
                System.err.println("Deze LeidingTak heeft al een primary key met zich mee!");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return id;
    }
    
    public static int updateLeidingTakRelatie(LeidingTak lt) {
        int aantalAangepasteRijen = 0;
        try {
            if(lt.getId() == 0) {
                System.err.println("Deze LeidingTak bestaat nog niet in de database.");
                
            } else {
                Object[] values = new Object[] {lt.getLeiding().getLidnr(), lt.getTakID(), lt.getStartDate(), lt.getEndDate(), lt.getId()};
                String insertString = "UPDATE leiding_takken SET leidingID = ?, takID = ?, start_date = ?, end_date = ? WHERE leiding_takken_id = ?";
                aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp(insertString, values);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }
    
    public static int verwijderLeidingTakRelatie(int id) {
        int aantalAangepasteRijen = 0;
        try {
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp("DELETE FROM leiding_takken WHERE leiding_takken_id = ?", new Object[] { id });
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }

    public static Lid getLeidingByLidnr(long lidnr, int status) {
        Lid resultaat = null;
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding WHERE lidnr = ?", new Object[] { lidnr });
            if (mijnResultset != null) {
                while (mijnResultset.next()) {
                    resultaat = converteerHuidigeRijNaarLeidingObject(mijnResultset);
                    if(status == FETCH_ALL) {
                        resultaat.setContactpersonen(ContactPersoonDAO.getContactPersonenByLidnr(resultaat));
                    }
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static boolean leidingExists(long lidnr) {
        return LeidingDAO.getLeidingByLidnr(lidnr, FETCH_NOTHING) != null;
    }

    public static int voegLeidingToe(Lid nieuweLeiding) {
        int aantalAangepasteRijen = 0;
        try {
            Field[] fields = nieuweLeiding.getClass().getDeclaredFields();
            Object[] values = new Object[fields.length - 1];
            String insertString = "INSERT INTO leiding (";
            
            int index = 0;
            for (Field field : fields) {
                String fieldName = field.getName();
                if(!fieldName.equals("contactpersonen")) {
                    String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = nieuweLeiding.getClass().getMethod(methodName, new Class[] {});
                    Object fieldValue = method.invoke(nieuweLeiding);

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

    public static int updateLeiding(Lid nieuweLeiding) {
        int aantalAangepasteRijen = 0;
        try {
            Field[] fields = nieuweLeiding.getClass().getDeclaredFields();
            Object[] values = new Object[fields.length - 1];
            String insertString = "UPDATE leiding SET ";
            
            int index = 0;
            for (Field field : fields) {
                String fieldName = field.getName();
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = nieuweLeiding.getClass().getMethod(methodName, new Class[] {});
                Object fieldValue = method.invoke(nieuweLeiding);

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

    public static int verwijderLeiding(long lidnr) {
        int aantalAangepasteRijen = 0;
        try {
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp("DELETE FROM leiding WHERE lidnr = ?", new Object[] { lidnr });
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }
    
    private static Lid converteerHuidigeRijNaarLeidingObject(ResultSet mijnResultset) throws SQLException {
        return new Lid(
                mijnResultset.getBigDecimal("lidnr").longValue(),
                mijnResultset.getString("voornaam"),
                mijnResultset.getString("achternaam"),
                mijnResultset.getString("geslacht"),
                1, // mijnResultset.getInt("takID"),
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
    
    private static LeidingTak converteerHuidigeRijNaarLeidingTakObject(ResultSet mijnResultset) throws SQLException {
        return new LeidingTak (
                mijnResultset.getInt("leiding_takken_id"),
                mijnResultset.getInt("takID"),
                mijnResultset.getDate("start_date"),
                mijnResultset.getDate("end_date"),
                new Lid(mijnResultset.getBigDecimal("leidingID").longValue())
        );
    }
}
