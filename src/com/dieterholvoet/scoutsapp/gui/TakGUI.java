/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.gui;

import com.dieterholvoet.scoutsapp.db.LeidingDAO;
import com.dieterholvoet.scoutsapp.model.LeidingTak;
import com.dieterholvoet.scoutsapp.util.Styles;
import com.dieterholvoet.scoutsapp.util.HelperFunctions;
import com.dieterholvoet.scoutsapp.swingmodel.LeidingComboBoxModel;
import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.model.Tak;
import com.dieterholvoet.scoutsapp.swingmodel.ToggleListSelectionModel;
import com.dieterholvoet.scoutsapp.swingrenderer.LeidingTakListRenderer;
import com.michaelbaranov.microba.calendar.DatePicker;
import com.sun.glass.events.KeyEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Dieter
 */
public class TakGUI extends javax.swing.JDialog {
    private final ArrayList<Lid> leiding;
    private ArrayList<LeidingTak> huidigeLeiding;
    private ArrayList<LeidingTak> vroegereLeiding;
    private WerkstukGUI parent;
    private final Tak tak;
    private String title;
    
    public TakGUI(WerkstukGUI parent) {
        super(parent, "Tak toevoegen", ModalityType.MODELESS);
        this.tak = new Tak();
        this.leiding = new ArrayList<>();
        init();
    }
    
    public TakGUI(WerkstukGUI parent, ArrayList<Lid> leiding, String title) {
        super(parent, title, ModalityType.MODELESS);
        this.title = title;
        this.tak = new Tak();
        this.leiding = leiding;
        this.huidigeLeiding = new ArrayList<>();
        this.vroegereLeiding = new ArrayList<>();
        this.parent = parent;
        init();
    }

    public TakGUI(WerkstukGUI parent, ArrayList<Lid> leiding, Tak tak, String title) {
        super(parent, title, ModalityType.MODELESS);
        this.title = title;
        this.tak = tak;
        this.leiding = leiding;
        this.huidigeLeiding = this.tak.getHuidigeLeiding();
        this.vroegereLeiding = this.tak.getVroegereLeiding();
        this.parent = parent;
        init();
    }
    
    private void init() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = (JPanel) this.getContentPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "escape");
        content.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Frame frame : Frame.getFrames()) {
                    if(frame.getClass().equals(TakGUI.class)) {
                        frame.dispose();
                    }
                }
            }
        });

        initComponents();
        setStyles();
        loadCombo();
        loadLists();
        setInitValues();
    }
    
    public void loadCombo() {
        LeidingComboBoxModel model = new LeidingComboBoxModel(this.leiding);
        cmbLeiding.setModel(model);
        cmbLeiding.setSelectedIndex(0);
    }
    
    public void loadLists() {
        DefaultListModel<LeidingTak> modelHuidigeLeiding = new DefaultListModel<>();
        modelHuidigeLeiding.clear();
        for(LeidingTak l : huidigeLeiding) {
            modelHuidigeLeiding.addElement(l);
        }
        
        lstHuidigeLeiding.setModel(modelHuidigeLeiding);
        lstHuidigeLeiding.setCellRenderer(new LeidingTakListRenderer());
        lstHuidigeLeiding.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstHuidigeLeiding.setSelectionModel(new ToggleListSelectionModel());
        
        DefaultListModel<LeidingTak> modelVroegereLeiding = new DefaultListModel<>();
        modelVroegereLeiding.clear();
        for(LeidingTak l : vroegereLeiding) {
            modelVroegereLeiding.addElement(l);
        }
        
        lstVroegereLeiding.setModel(modelVroegereLeiding);
        lstVroegereLeiding.setCellRenderer(new LeidingTakListRenderer());
        lstVroegereLeiding.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstVroegereLeiding.setSelectionModel(new ToggleListSelectionModel());
    }
    
    private boolean checkInput() {
        for(Component component : HelperFunctions.getAllComponents(this)) {
            if(component instanceof JTextField) {
                JTextField text = (JTextField) component;
                String name = HelperFunctions.getComponentVariableName(component);
                
                switch(name) {
                    case "txtNaam":
                        if(text.getText().equals("")) {
                            return false;
                        }
                }
            }
        }
        
        return true;
    }
    
    private boolean checkLeidingTakInput() {
        switch(cmbLeiding.getSelectedIndex()) {
            case 0:
            case -1:
                return false;
                
            default:
                return true;
        }
    }
    
    private void saveInput() {
        tak.setNaam(txtNaam.getText());
        tak.setVroegereLeiding(vroegereLeiding);
        tak.setHuidigeLeiding(huidigeLeiding);
    }
    
    private LeidingTak saveLeidingTak() {
        LeidingTak l = new LeidingTak();
        l.setStartDate(new java.sql.Date(dateStartDatum.getDate().getTime()));
        l.setLeiding(((LeidingComboBoxModel)cmbLeiding.getModel()).getSelectedLid());
        l.setTakID(this.tak.getTakID());
        
        if(chkEindDatum.isSelected()) {
            l.setEndDate(new java.sql.Date(dateEindDatum.getDate().getTime()));
            
        }  else {
            l.setEndDate(null);
        }
        
        return l;
    }
    
    private void setInitValues() {
        lblHeader.setText(title);
        txtNaam.setText(tak.getNaam() == null ? "" : tak.getNaam());
    }
    
    private void setStyles() {        
        for (Component component : HelperFunctions.getAllComponents(this)) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                Dimension size = new Dimension(120, 40);
                
                switch(HelperFunctions.getComponentVariableName(label)) {
                    
                    // HEADER LABEL
                    case "lblHeader":
                        label.setFont(Styles.FjallaOne.getREGULAR(Styles.H1));
                        label.setHorizontalTextPosition(JLabel.CENTER);
                        label.setVerticalTextPosition(JLabel.BOTTOM);
                        label.setForeground(Styles.BIJNAZWART);
                        label.setBorder(new EmptyBorder(0, 0, 30, 0));
                        break;
                        
                    // REGULAR LABELS
                    case "lblNaam":
                    case "lblLeiding":
                    case "lblHuidigeLeiding":
                    case "lblVroegereLeiding":
                        label.setFont(Styles.OpenSans.getLIGHT(Styles.LABEL));
                        label.setMinimumSize(size);
                        label.setMaximumSize(size);
                        label.setPreferredSize(size);
                        break;
   
                    default:
                        break;
                }

            } else if(component instanceof JTextField) {
                JTextField text = (JTextField) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(text)) {
                    case "txtNaam":
                        text.setMinimumSize(size);
                        text.setMaximumSize(size);
                        text.setPreferredSize(size);
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JCheckBox) {
                JCheckBox check = (JCheckBox) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(check)) {
                    case "chkStartDatum":
                    case "chkEindDatum":
                        check.setFont(Styles.OpenSans.getLIGHT(Styles.LABEL));
                        check.setMinimumSize(size);
                        check.setMaximumSize(size);
                        check.setPreferredSize(size);
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JButton) {
                JButton button = (JButton) component;
                Dimension size = new Dimension(230, 40);
                
                switch(HelperFunctions.getComponentVariableName(button)) {
                    case "btnSubmit":
                    case "btnAdd":
                    case "btnDelete":
                        button.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        button.setText(button.getText().toUpperCase());
                        button.setMinimumSize(size);
                        button.setMaximumSize(size);
                        button.setPreferredSize(size);
                        break;

                    default:
                        break;
                }

            } else if(component instanceof DatePicker) {
                DatePicker date = (DatePicker) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(date)) {
                    case "dateStartDatum":
                    case "dateEindDatum":
                        date.setMinimumSize(size);
                        date.setMaximumSize(size);
                        date.setPreferredSize(size);

                    default:
                        break;
                }

            } else if(component instanceof JScrollPane) {
                JScrollPane list = (JScrollPane) component;
                Dimension size = new Dimension(200, 200);
                
                switch(HelperFunctions.getComponentVariableName(list)) {
                    case "scrlHuidigeLeiding":
                    case "scrlVroegereLeiding":
                        list.setMinimumSize(size);
                        list.setPreferredSize(size);

                    default:
                        break;
                }
                
            } else if(component instanceof JList) {
                JList list = (JList) component;
                
                switch(HelperFunctions.getComponentVariableName(list)) {
                    case "lstVroegereLeiding":
                    case "lstHuidigeLeiding":
                        list.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        list.setFixedCellHeight(30);

                    default:
                        break;
                }
                
            } else if(component instanceof JComboBox) {
                JComboBox combo = (JComboBox) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(combo)) {
                    case "cmbLeiding":
                        combo.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        combo.setMinimumSize(size);
                        combo.setMaximumSize(size);
                        combo.setPreferredSize(size);

                    default:
                        break;
                }
            } 
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btngrpGeslacht = new javax.swing.ButtonGroup();
        lblHeader = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        pnlTakInfo = new javax.swing.JPanel();
        lblNaam = new javax.swing.JLabel();
        txtNaam = new javax.swing.JTextField();
        pnlNewLeidingTak = new javax.swing.JPanel();
        lblLeiding = new javax.swing.JLabel();
        cmbLeiding = new javax.swing.JComboBox();
        chkStartDatum = new javax.swing.JCheckBox();
        chkEindDatum = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        dateStartDatum = new com.michaelbaranov.microba.calendar.DatePicker();
        dateEindDatum = new com.michaelbaranov.microba.calendar.DatePicker();
        pnlHuidigeLeiding = new javax.swing.JPanel();
        lblHuidigeLeiding = new javax.swing.JLabel();
        scrlHuidigeLeiding = new javax.swing.JScrollPane();
        lstHuidigeLeiding = new javax.swing.JList();
        pnlVroegereLeiding = new javax.swing.JPanel();
        lblVroegereLeiding = new javax.swing.JLabel();
        scrlVroegereLeiding = new javax.swing.JScrollPane();
        lstVroegereLeiding = new javax.swing.JList();
        btnSubmit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(214, 217, 223));
        setMinimumSize(new java.awt.Dimension(1000, 720));
        setName("TakFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 720));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("Tak toevoegen");
        lblHeader.setName("lblHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(lblHeader, gridBagConstraints);

        jPanel5.setPreferredSize(new java.awt.Dimension(3000, 500));
        jPanel5.setLayout(new java.awt.GridLayout(2, 2));

        pnlTakInfo.setLayout(new java.awt.GridBagLayout());

        lblNaam.setText("Naam");
        lblNaam.setName("lblNaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        pnlTakInfo.add(lblNaam, gridBagConstraints);

        txtNaam.setText("jTextField1");
        txtNaam.setName("txtNaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        pnlTakInfo.add(txtNaam, gridBagConstraints);

        jPanel5.add(pnlTakInfo);

        pnlNewLeidingTak.setLayout(new java.awt.GridBagLayout());

        lblLeiding.setText("Leiding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlNewLeidingTak.add(lblLeiding, gridBagConstraints);

        cmbLeiding.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlNewLeidingTak.add(cmbLeiding, gridBagConstraints);

        chkStartDatum.setSelected(true);
        chkStartDatum.setText("Startdatum");
        chkStartDatum.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlNewLeidingTak.add(chkStartDatum, gridBagConstraints);

        chkEindDatum.setText("Einddatum");
        chkEindDatum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEindDatumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlNewLeidingTak.add(chkEindDatum, gridBagConstraints);

        btnAdd.setText("Toevoegen");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnlButtons.add(btnAdd);

        btnDelete.setText("Verwijderen");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlButtons.add(btnDelete);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        pnlNewLeidingTak.add(pnlButtons, gridBagConstraints);

        dateStartDatum.setKeepTime(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlNewLeidingTak.add(dateStartDatum, gridBagConstraints);

        dateEindDatum.setEnabled(false);
        dateEindDatum.setKeepTime(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlNewLeidingTak.add(dateEindDatum, gridBagConstraints);

        jPanel5.add(pnlNewLeidingTak);

        pnlHuidigeLeiding.setLayout(new java.awt.GridBagLayout());

        lblHuidigeLeiding.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHuidigeLeiding.setText("Huidige leiding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlHuidigeLeiding.add(lblHuidigeLeiding, gridBagConstraints);

        lstHuidigeLeiding.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstHuidigeLeiding.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstHuidigeLeidingValueChanged(evt);
            }
        });
        scrlHuidigeLeiding.setViewportView(lstHuidigeLeiding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlHuidigeLeiding.add(scrlHuidigeLeiding, gridBagConstraints);

        jPanel5.add(pnlHuidigeLeiding);

        pnlVroegereLeiding.setLayout(new java.awt.GridBagLayout());

        lblVroegereLeiding.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVroegereLeiding.setText("Vroegere leiding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVroegereLeiding.add(lblVroegereLeiding, gridBagConstraints);

        lstVroegereLeiding.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstVroegereLeiding.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstVroegereLeidingValueChanged(evt);
            }
        });
        scrlVroegereLeiding.setViewportView(lstVroegereLeiding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlVroegereLeiding.add(scrlVroegereLeiding, gridBagConstraints);

        jPanel5.add(pnlVroegereLeiding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jPanel5, gridBagConstraints);

        btnSubmit.setText("Opslaan");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        getContentPane().add(btnSubmit, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if(checkLeidingTakInput()) {
            LeidingTak l = saveLeidingTak();
            l.setId(LeidingDAO.voegLeidingTakRelatieToe(l));
            
            if(l.getEndDate() == null) {
                huidigeLeiding.add(l);
                
            } else {
                vroegereLeiding.add(l);
            }
            
            loadLists();
            
        } else {
            JOptionPane.showMessageDialog(this, "Niet alle velden zijn ingevuld.", "Fout bij het opslaan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void lstHuidigeLeidingValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstHuidigeLeidingValueChanged

    }//GEN-LAST:event_lstHuidigeLeidingValueChanged

    private void lstVroegereLeidingValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstVroegereLeidingValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_lstVroegereLeidingValueChanged

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if(checkInput()) {
            saveInput();
            this.dispose();
            parent.NieuweTakCallback(vroegereLeiding, huidigeLeiding);
            
        } else {
            JOptionPane.showMessageDialog(this, "Niet alle velden zijn ingevuld.", "Fout bij het opslaan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(lstHuidigeLeiding.getModel().getSize() > 0) {
            List<LeidingTak> hl = lstHuidigeLeiding.getSelectedValuesList();
            for(LeidingTak l : hl) {
                huidigeLeiding.remove(l);
                LeidingDAO.verwijderLeidingTakRelatie(l.getId());
            }
        }
        
        if(lstVroegereLeiding.getModel().getSize() > 0) {
            List<LeidingTak> vl = lstVroegereLeiding.getSelectedValuesList();
            for(LeidingTak l : vl) {
                vroegereLeiding.remove(l);
                LeidingDAO.verwijderLeidingTakRelatie(l.getId());
            }
        }
        
        loadLists();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void chkEindDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEindDatumActionPerformed
        dateEindDatum.setEnabled(chkEindDatum.isSelected());
    }//GEN-LAST:event_chkEindDatumActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TakGUI dialog = new TakGUI((WerkstukGUI) new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSubmit;
    private javax.swing.ButtonGroup btngrpGeslacht;
    private javax.swing.JCheckBox chkEindDatum;
    private javax.swing.JCheckBox chkStartDatum;
    private javax.swing.JComboBox cmbLeiding;
    private com.michaelbaranov.microba.calendar.DatePicker dateEindDatum;
    private com.michaelbaranov.microba.calendar.DatePicker dateStartDatum;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblHuidigeLeiding;
    private javax.swing.JLabel lblLeiding;
    private javax.swing.JLabel lblNaam;
    private javax.swing.JLabel lblVroegereLeiding;
    private javax.swing.JList lstHuidigeLeiding;
    private javax.swing.JList lstVroegereLeiding;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlHuidigeLeiding;
    private javax.swing.JPanel pnlNewLeidingTak;
    private javax.swing.JPanel pnlTakInfo;
    private javax.swing.JPanel pnlVroegereLeiding;
    private javax.swing.JScrollPane scrlHuidigeLeiding;
    private javax.swing.JScrollPane scrlVroegereLeiding;
    private javax.swing.JTextField txtNaam;
    // End of variables declaration//GEN-END:variables
}
