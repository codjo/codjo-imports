/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 * Affiche les IHM de paramétrages des imports.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.7 $
 */
public class ImportAction extends AbstractGuiAction {
    private CleanUpListener cleanUpListener = new CleanUpListener();
    private JInternalFrame frame;


    public ImportAction(GuiContext ctxt) {
        super(ctxt, "Imports", "Paramétrage des fichiers à importer", null, "ParametrageImport");
    }


    public void actionPerformed(ActionEvent evt) {
        if (frame == null) {
            displayNewWindow();
        }
        else {
            try {
                frame.setSelected(true);
            }
            catch (PropertyVetoException ex) {
                ; // Pas grave
            }
        }
    }


    private void displayNewWindow() {
        try {
            frame = new ImportWindow(getGuiContext());
            frame.addInternalFrameListener(cleanUpListener);
            getDesktopPane().add(frame);
            frame.pack();
            frame.setVisible(true);
            frame.setSelected(true);
        }
        catch (Exception ex) {
            ErrorDialog.show(getDesktopPane(), "Impossible d'afficher la liste", ex);
        }
    }


    private class CleanUpListener extends InternalFrameAdapter {
        @Override
        public void internalFrameActivated(InternalFrameEvent evt) {
        }


        @Override
        public void internalFrameClosed(InternalFrameEvent evt) {
            evt.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent evt) {
            evt.getInternalFrame().removeInternalFrameListener(this);
            frame = null;
        }
    }
}
