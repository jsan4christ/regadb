/*
 * SelectPanel.java
 *
 * Created on September 5, 2003, 1:39 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui;

import java.awt.GridBagConstraints;
import java.util.*;
import javax.swing.*;

import com.pharmadm.custom.rega.queryeditor.ComposedSelection;
import com.pharmadm.custom.rega.queryeditor.OutputSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.queryeditor.gui.QueryEditorTree;

/**
 *
 * @author  kristof
 */
public class SelectPanel extends javax.swing.JPanel {
    
    private QueryEditorTree controller;
    
    /** Creates new form SelectPanel */
    public SelectPanel(QueryEditorTree controller) {
        this.controller = controller;
        initComponents();
        initSelectListComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.GridBagLayout());

    }//GEN-END:initComponents
    
    private void initSelectListComponents() {
        makeSelectListComponents();
        controller.addSelectionListChangeListener(new SelectionListChangeListener() {
            public void listChanged() {
                // maybe not the most efficient (or elegant) implementation
                reInitSelectListComponents();
                // both revalidate and repaint are required to properly update the GUI
                revalidate();
                repaint();
            }
        });
    }
    
    
    private void reInitSelectListComponents() {
        this.removeAll();
        makeSelectListComponents();
    }
    
    private void makeSelectListComponents() {
        Iterator<Selection> outputs = controller.getQuery().getSelectList().getSelections().iterator();
        while (outputs.hasNext()) {
            Selection selection = outputs.next();
            OutputVariable ovar = (OutputVariable)selection.getObject();
            if (selection instanceof ComposedSelection) {
                java.awt.GridBagConstraints gridBagConstraints = new GridBagConstraints();
                //gridBagConstraints.gridheight = 10;
                //gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.insets = new java.awt.Insets(5,5,5,5);
                this.add(new TableFieldSelectorPanel((TableSelection)selection), gridBagConstraints);
            } else {
                java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.insets = new java.awt.Insets(5,5,5,5);
                this.add(new SimpleOutputSelectorPanel((OutputSelection)selection), gridBagConstraints);
            }
        }
        JLabel pushLabel = new JLabel();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1;
        this.add(pushLabel, gridBagConstraints);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
