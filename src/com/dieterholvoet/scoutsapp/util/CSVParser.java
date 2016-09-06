/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.util;

import com.dieterholvoet.scoutsapp.db.ContactPersoonDAO;
import com.dieterholvoet.scoutsapp.db.LeidingDAO;
import com.dieterholvoet.scoutsapp.db.LidDAO;
import com.dieterholvoet.scoutsapp.db.TakDAO;
import com.dieterholvoet.scoutsapp.gui.WerkstukGUI;
import com.dieterholvoet.scoutsapp.model.ColumnHolder;
import com.dieterholvoet.scoutsapp.model.ContactPersoon;
import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.model.Tak;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Dieter
 */
public class CSVParser {
    private String selectedFile;
    private final FileNameExtensionFilter filter;
    private final JFileChooser chooser;
    private final WerkstukGUI parent;
    private final ColumnHolder c;
    public Thread readFileThread;
    private int count = 0;
    private int progress = 0;

    public CSVParser(WerkstukGUI WerkstukGUI) {
        this.parent = WerkstukGUI;
        this.c = new ColumnHolder();
        
        this.filter = new FileNameExtensionFilter("CSV Files", "csv");
        this.chooser = new JFileChooser();
        this.chooser.setFileFilter(this.filter);
    }
    
    private void parseColumnsFromString(String uitgelezenRegel) {
        String[] parts = uitgelezenRegel.split(";");        
        
        for(int i = 0; i < parts.length; i++) {
            // Fix for string comparison problems due to leading BOM character
            if((int)parts[i].charAt(0) == 65279) {
                parts[i] = parts[i].substring(1);
            }
            
            switch(parts[i]) {
                case "Voornaam":
                    c.setVoornaamIndex(i);
                    break;
                    
                case "Achternaam":
                    c.setAchternaamIndex(i);
                    break;
                    
                case "Geslacht":
                    c.setGeslachtIndex(i);
                    break;
                    
                case "Takken":
                    c.setTakIDIndex(i);
                    break;
                    
                case "Geboortedatum":
                    c.setGeboortedatumIndex(i);
                    break;
                
                case "Telefoon":
                    c.setTelefoonIndex(i);
                    break;
                    
                case "Gsm":
                    c.setGsmIndex(i);
                    break;
                    
                case "E-mail":
                    c.setEmailIndex(i);
                    break;
                    
                case "Adres":
                    c.setAdresIndex(i);
                    break;
                    
                case "Lidnummer":
                    c.setLidnrIndex(i);
                    break;
                    
                case "Betaald":
                    c.setHeeftBetaaldIndex(i);
                    break;
                    
                case "Windroos":
                    c.setNeemtWindroosIndex(i);
                    break;
                    
                case "Adjectief":
                    c.setAdjectiefIndex(i);
                    break;
                    
                case "Adjectief en totem":
                    c.setAdjectiefTotemIndex(i);
                    break;
                    
                case "Naam eerste contact":
                    c.setContact1NaamIndex(i);
                    break;
                    
                case "Gsm eerste contact":
                    c.setContact1GsmIndex(i);
                    break;
                    
                case "E-mail eerste contact":
                    c.setContact1EmailIndex(i);
                    break;
                    
                case "Naam tweede contact":
                    c.setContact2NaamIndex(i);
                    break;
                    
                case "Gsm tweede contact":
                    c.setContact2GsmIndex(i);
                    break;
                    
                case "E-mail tweede contact":
                    c.setContact2EmailIndex(i);
                    break;
                    
                case "Aandachtspunten":
                    c.setAandachtspuntenIndex(i);
                    break;
                    
                default:
                    Logger.getLogger(WerkstukGUI.class.getName()).log(Level.WARNING, "Unknown column name: {0}", parts[i]);
                    break;
            }
        }
    }
    
    private Lid parseLidFromString(String uitgelezenRegel) {
        String[] parts = uitgelezenRegel.split(";");
        Lid l = new Lid();
        ContactPersoon c1 = new ContactPersoon();
        ContactPersoon c2 = new ContactPersoon();
        
        for(int i = 0; i < parts.length; i++) {
            if(i == c.getVoornaamIndex()) {
                l.setVoornaam(capitalize(parts[c.getVoornaamIndex()]));
            }
            if(i == c.getAchternaamIndex()) {
                l.setAchternaam(WordUtils.capitalizeFully(parts[c.getAchternaamIndex()]));
            }
            if(i == c.getAdjectiefIndex()) {
                l.setAdjectief(WordUtils.capitalizeFully(parts[c.getAdjectiefIndex()]));
            }
            if(i == c.getAdjectiefTotemIndex()) {
                String[] parts2 = parts[c.getAdjectiefTotemIndex()].split(" ");
                switch(parts2.length) {
                    case 2:
                        l.setAdjectief(capitalize(parts2[1]));
                    
                    case 1:
                        l.setTotem(capitalize(parts2[0]));
                        break;
                }
                
            }
            if(i == c.getAdresIndex()) {
                l.setAdres(WordUtils.capitalizeFully(parts[c.getAdresIndex()]));
            }
            if(i == c.getEmailIndex()) {
                l.setEmail(parts[c.getEmailIndex()].toLowerCase());
            }
            if(i == c.getGeboortedatumIndex()) {
                l.setGeboortedatum(parseDate(parts[c.getGeboortedatumIndex()]));
            }
            if(i == c.getGeslachtIndex()) {
                l.setGeslacht(parts[c.getGeslachtIndex()]);
            }
            if(i == c.getGsmIndex()) {
                l.setGsm(parts[c.getGsmIndex()]);
            }
            if(i == c.getHeeftBetaaldIndex()) {
                l.setHeeftBetaald(parseBoolean(parts[c.getHeeftBetaaldIndex()]));
            }
            if(i == c.getLidnrIndex()) {
                l.setLidnr(Long.parseLong(parts[c.getLidnrIndex()]));
            }
            if(i == c.getNeemtWindroosIndex()) {
                l.setNeemtWindroos(parseBoolean(parts[c.getNeemtWindroosIndex()]));
            }
            if(i == c.getTakIDIndex()) {
                int takID = parseTakNaamFromString(capitalize(parts[c.getTakIDIndex()]));
                if(takID == -1) {
                    Logger.getLogger(WerkstukGUI.class.getName()).log(Level.WARNING, "Error while parsing taknaam");
                } else {
                    l.setTakID(takID);
                }
            }
            if(i == c.getTelefoonIndex()) {
                l.setTelefoon(parts[c.getTelefoonIndex()]);
            }
            if(i == c.getTotemIndex()) {
                l.setTotem(capitalize(parts[c.getTotemIndex()]));
            }
            if(i == c.getVoornaamIndex()) {
                l.setVoornaam(WordUtils.capitalizeFully(parts[c.getVoornaamIndex()]));
            }
        }
        return l;
    }
    
    private ArrayList<ContactPersoon> parseContactPersonenFromString(String uitgelezenRegel) {
        String[] parts = uitgelezenRegel.split(";");
        ArrayList<ContactPersoon> list = new ArrayList<>();
        ContactPersoon c1 = new ContactPersoon();
        ContactPersoon c2 = new ContactPersoon();
        
        for(int i = 0; i < parts.length; i++) {
            if(i == c.getContact1EmailIndex()) {
                if(!parts[c.getContact1EmailIndex()].equals("")) {
                    c1.setEmail(parts[c.getContact1EmailIndex()].toLowerCase());
                }
            }
            if(i == c.getContact1GsmIndex()) {
                if(!parts[c.getContact1GsmIndex()].equals("")) {
                    c1.setTelefoon(parts[c.getContact1GsmIndex()]);
                }
            }
            if(i == c.getContact1NaamIndex()) {
                if(!parts[c.getContact1NaamIndex()].equals("")) {
                    String[] parsedName = parseName(parts[c.getContact1NaamIndex()]);
                    c1.setVoornaam(WordUtils.capitalizeFully(parsedName[0]));
                    c1.setAchternaam(WordUtils.capitalizeFully(parsedName[1]));
                }
            }
            if(i == c.getContact2EmailIndex()) {
                if(!parts[c.getContact2EmailIndex()].equals("")) {
                    c2.setEmail(parts[c.getContact2EmailIndex()].toLowerCase());
                }
            }
            if(i == c.getContact2GsmIndex()) {
                if(!parts[c.getContact2GsmIndex()].equals("")) {
                    c2.setEmail(parts[c.getContact2GsmIndex()]);
                }
            }
            if(i == c.getContact2NaamIndex()) {
                if(!"".equals(parts[c.getContact2NaamIndex()])) {
                    String[] parsedName = parseName(parts[c.getContact2NaamIndex()]);
                    c2.setVoornaam(WordUtils.capitalizeFully(parsedName[0]));
                    c2.setAchternaam(WordUtils.capitalizeFully(parsedName[1]));
                }
            }
        }
        
        if(!c1.isEmpty()) {
            list.add(c1);
        }
        
        if(!c2.isEmpty()) {
            list.add(c2);
        }
        
        return list;
    }
    
    private String capitalize(String s) {
        if(s != null) {
            if(s.length() > 0) {
                return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
            }
        }
        return "";
    }
    
    private boolean parseBoolean(String s) {
        switch(s) {
            case "[ ]":
                return false;
                
            case "[X]":
                return true;
                
            default:
                Logger.getLogger(WerkstukGUI.class.getName()).log(Level.WARNING, "Invalid boolean in input string: {0}", s);
                return false;
        }
    }
    
    private String[] parseName(String s) {
        String[] c1Naam = s.trim().split(" ");
        if(c1Naam.length == 1) {
            return new String[]{
                c1Naam[0],      // Voornaam
                ""              // Achternaam
            };
            
        } else if(c1Naam.length > 1) {
            return new String[]{
                c1Naam[0],                               // Voornaam
                s.substring(c1Naam[0].length()).trim()   // Achternaam
            };
            
        } else {
            return new String[] { s, "" };
        }
    }
    
    private java.sql.Date parseDate(String s) {
        // Source: http://stackoverflow.com/q/10413350
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = sdf.parse(s);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            return sqlDate;

        } catch (ParseException ex) {
            Logger.getLogger(WerkstukGUI.class.getName()).log(Level.WARNING, "Error parsing birth date.", ex);
            return null;
        }
    }
    
    private int parseTakNaamFromString(String naam) {
        switch(TakDAO.getTakkenCount()) {
            case -1:
                System.err.println("Error fetching amount of takken.");
                return -1;
                
            case 0:
                TakDAO.voegTakToe(new Tak("Leiding"));
                
            default:
                break;
        }
        
        switch(naam) {
            case "Jong gidsen":
                naam = "Jonggidsen";
                break;

            case "Jong verkenners":
                naam = "Jongverkenners";
                break;

            case "Akabe-leden":
                naam = "Akabe";
                break;
        }
        
        Tak tak = TakDAO.getTakByName(naam, TakDAO.FETCH_NOTHING);
        if(tak == null) {
            int takID = TakDAO.voegTakToe(new Tak(naam));
            if(takID == 0) {
                return -1;

            } else {
                return takID;
            }
            
        } else {
            return tak.getTakID();
        }
    }
    
    public boolean hasSelectedFile() {
        if(selectedFile != null) {
            return (!selectedFile.equals(""));
        }
        return false;
    }
    
    public void chooseFile() {
        int returnVal = chooser.showOpenDialog(this.parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile().getPath();
                        
        } else if(returnVal != JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(this.parent, "Invalid file!");
        }
    }
    
    private void progressUpdate(String text) {
        parent.CSVParserProgressUpdate(text);
    }
    
    public void readFile() {
        this.readFileThread = new Thread() {
            
            @Override
            public void run() {
                if(selectedFile != null && !"".equals(selectedFile)) {
                    File file = new File(selectedFile);
                    BufferedReader reader = null;
                    BufferedReader sizeReader = null;
                    try {
                        reader = Files.newBufferedReader(file.toPath());
                        sizeReader = Files.newBufferedReader(file.toPath());
                        
                        while (sizeReader.readLine() != null) count++;
                        sizeReader.close();
                        
                        String uitgelezenRegel = reader.readLine();
                        parseColumnsFromString(uitgelezenRegel);
                        uitgelezenRegel = reader.readLine();
                        
                        while (uitgelezenRegel != null) {
                            uitgelezenRegel = uitgelezenRegel.replaceAll("\"", "");
                            Lid nieuwLid = parseLidFromString(uitgelezenRegel);
                            ArrayList<ContactPersoon> cp = parseContactPersonenFromString(uitgelezenRegel); 
                           
                            if(nieuwLid.isLeiding()) {
                                // is Leiding
                                if(LeidingDAO.leidingExists(nieuwLid.getLidnr())) {
                                    progressUpdate("Bestaande leiding bijwerken: " + nieuwLid.toString());
                                    LeidingDAO.updateLeiding(nieuwLid);

                                } else {
                                    progressUpdate("Nieuwe leiding toevoegen: " + nieuwLid.toString());
                                    LeidingDAO.voegLeidingToe(nieuwLid);
                                }
                                
                            } else {
                                // is Lid
                                if(LidDAO.lidExists(nieuwLid.getLidnr())) {
                                    progressUpdate("Bestaand lid bijwerken: " + nieuwLid.toString());
                                    LidDAO.updateLid(nieuwLid);

                                } else {
                                    progressUpdate("Nieuw lid toevoegen: " + nieuwLid.toString());
                                    LidDAO.voegLidToe(nieuwLid);
                                }
                            }
                            
                            for(ContactPersoon contact : cp) {
                                ContactPersoon test = ContactPersoonDAO.getContactPersoonByName(contact.getVoornaam(), contact.getAchternaam());
                                if(test == null) {
                                    ContactPersoonDAO.voegContactPersoonToe(contact, nieuwLid);
                                    
                                } else {
                                    ContactPersoonDAO.voegContactPersoonRelatieToe(test, nieuwLid);
                                }
                            }
                            
                            uitgelezenRegel = reader.readLine();
                            progress++;
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(WerkstukGUI.class.getName()).log(Level.SEVERE, "Error while reading file.", ex);
                        JOptionPane.showMessageDialog(parent, "Error while reading file.");
                        
                    } finally {
                        try {
                            if(reader != null) {
                                reader.close();
                            }
                        
                        } catch (IOException ex) {
                            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        progressUpdate("Uitlezen voltooid.");
                        parent.CSVParserCallback();
                    }

                } else {
                    Logger.getLogger(WerkstukGUI.class.getName()).log(Level.SEVERE, "Choose a file first!");
                }
            }  
        };
        
        this.readFileThread.start();
    }
    
    public float getProgress() {
        return (float) progress / (float) count * 100;
    }
}
