/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import javax.swing.JLabel;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.workflow.gui.wizard.FinalStep;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.awt.Insets;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;
/**
 *
 */
public class ImportWizardSummaryGui implements FinalStep.WizardSummaryGui {
    private LabelledItemPanel guiPanel = new LabelledItemPanel(new Insets(4, 4, 4, 4));
    private JTextField filePath = createField("filePath");
    private JTextField fileType = createField("fileType");


    public ImportWizardSummaryGui(GuiContext guiContext) {
        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);

        JLabel fileLabel = new JLabel("Fichier :");
        translationNotifier.addInternationalizableComponent(fileLabel, "ImportWizardSummaryGui.fileLabel");
        guiPanel.addItem(fileLabel, filePath);

        JLabel typeLabel = new JLabel("Type :");
        translationNotifier.addInternationalizableComponent(typeLabel, "ImportWizardSummaryGui.typeLabel");
        guiPanel.addItem(typeLabel, fileType);
    }


    public JComponent getGui() {
        return guiPanel;
    }


    public void display(Map requestState) {
        String fileParam = (String)requestState.get(WizardConstants.IMPORT_FILE_PATH);
        String fileTypeParam = (String)requestState.get(WizardConstants.IMPORT_TYPE);

        filePath.setText(fileParam);
        fileType.setText(fileTypeParam);
    }


    private static JTextField createField(String name) {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setName(name);
        return field;
    }
}
