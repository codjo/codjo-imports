/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.structure.StructureCombo;
/**
 * Paramétrage d'un fichier en import.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.7 $
 */
public class ImportSettingsDetailWindow extends JInternalFrame implements InternationalizableContainer {
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private LabelledItemPanel mainPanel = new LabelledItemPanel();
    private DetailDataSource dataSource;
    protected TranslationNotifier translationNotifier;
    private JLabel destinationTableLabel = new JLabel();
    private JLabel fileTypeLabel = new JLabel();
    private JLabel sourceSystemLabel = new JLabel();
    private JLabel recordLengthLabel = new JLabel();
    private JLabel fixedLengthLabel = new JLabel();
    private JLabel fieldSeparatorLabel = new JLabel();
    private JLabel headerLineLabel = new JLabel();
    private JLabel commentLabel = new JLabel();


    public ImportSettingsDetailWindow(DetailDataSource dataSource) throws RequestException {
        super("Fichier d'import", true, false, true, true);
        this.dataSource = dataSource;
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);

        buildFields();

        dataSource.load();
        buttonPanelLogic.setMainDataSource(dataSource);

        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(dataSource.getGuiContext());
        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        this.translationNotifier.addInternationalizableComponent(this, "ImportSettingsDetailWindow.title");
        this.translationNotifier
              .addInternationalizableComponent(destinationTableLabel,
                                               "ImportSettingsDetailWindow.destinationTableLabel");
        this.translationNotifier
              .addInternationalizableComponent(fileTypeLabel, "ImportSettingsDetailWindow.fileTypeLabel");
        this.translationNotifier
              .addInternationalizableComponent(sourceSystemLabel, "ImportSettingsDetailWindow.sourceSystemLabel");
        this.translationNotifier
              .addInternationalizableComponent(recordLengthLabel, "ImportSettingsDetailWindow.recordLengthLabel");
        this.translationNotifier
              .addInternationalizableComponent(fixedLengthLabel, "ImportSettingsDetailWindow.fixedLengthLabel");
        this.translationNotifier
              .addInternationalizableComponent(fieldSeparatorLabel, "ImportSettingsDetailWindow.fieldSeparatorLabel");
        this.translationNotifier
              .addInternationalizableComponent(headerLineLabel, "ImportSettingsDetailWindow.headerLineLabel");
        this.translationNotifier
              .addInternationalizableComponent(commentLabel, "ImportSettingsDetailWindow.commentLabel");
    }


    protected void buildFields() {
        Map quarantineTables =
              (HashMap)dataSource.getGuiContext().getProperty(ImportWindow.QUARANTINES_STRUCTURE);

        addField("destTable", destinationTableLabel, new StructureCombo(quarantineTables));

        TextField fileType = new TextField();
        fileType.setMaxTextLength(30);
        addField("fileType", fileTypeLabel, fileType);

        TextField sourceSystem = new TextField();
        sourceSystem.setMaxTextLength(30);
        addField("sourceSystem", sourceSystemLabel, sourceSystem);

        addField("recordLength", recordLengthLabel, buildIntegerField());
        addField("fixedLength", fixedLengthLabel, new JCheckBox());

        TextField fieldSeparator = new TextField();
        fieldSeparator.setMaxTextLength(2);
        addField("fieldSeparator", fieldSeparatorLabel, fieldSeparator);

        addField("headerLine", headerLineLabel, new JCheckBox());

        addField("comment", commentLabel, new JTextArea(3, 20));
    }


    private NumberField buildIntegerField() {
        NumberField nb = new NumberField();
        nb.setMaximumFractionDigits(0);
        return nb;
    }


    protected void addField(String fieldName, String label, JComponent comp) {
        addField(fieldName, new JLabel(label), comp);
    }


    protected void addField(String fieldName, JLabel label, JComponent comp) {
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
