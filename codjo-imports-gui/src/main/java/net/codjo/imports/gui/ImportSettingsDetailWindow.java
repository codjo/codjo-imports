/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.structure.StructureCombo;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Paramétrage d'un fichier en import.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.7 $
 */
public class ImportSettingsDetailWindow extends JInternalFrame {
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private LabelledItemPanel mainPanel = new LabelledItemPanel();
    private DetailDataSource dataSource;


    public ImportSettingsDetailWindow(DetailDataSource dataSource) throws RequestException {
        super("Fichier d'import", true, false, true, true);
        this.dataSource = dataSource;
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);

        buildFields();

        dataSource.load();
        buttonPanelLogic.setMainDataSource(dataSource);
    }


    protected void buildFields() {
        Map quarantineTables =
              (HashMap)dataSource.getGuiContext().getProperty(ImportWindow.QUARANTINES_STRUCTURE);

        addField("destTable", "Table destination", new StructureCombo(quarantineTables));

        TextField fileType = new TextField();
        fileType.setMaxTextLength(30);
        addField("fileType", "Type de fichier", fileType);

        TextField sourceSystem = new TextField();
        sourceSystem.setMaxTextLength(30);
        addField("sourceSystem", "Système source", sourceSystem);

        addField("recordLength", "Longueur d'un enregistrement", buildIntegerField());
        addField("fixedLength", "Longueur fixe", new JCheckBox());

        TextField fieldSeparator = new TextField();
        fieldSeparator.setMaxTextLength(2);
        addField("fieldSeparator", "Séparateur de champ", fieldSeparator);

        addField("headerLine", "Ligne d'entête", new JCheckBox());
        addField("comment", "Commentaires", new JTextArea(3, 20));
    }


    private NumberField buildIntegerField() {
        NumberField nb = new NumberField();
        nb.setMaximumFractionDigits(0);
        return nb;
    }


    protected void addField(String fieldName, String label, JComponent comp) {
        if (comp instanceof JTextArea) {
            ((JTextArea)comp).setLineWrap(true);
            ((JTextArea)comp).setWrapStyleWord(true);
            mainPanel.addItem(label, new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        }
        else {
            mainPanel.addItem(label, comp);
        }
        dataSource.declare(fieldName, comp);
    }
}
