package net.codjo.imports.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.imports.common.FieldType;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.structure.StructureCombo;

public class FieldImportDetailWindow extends JInternalFrame implements InternationalizableContainer {
    private LabelledItemPanel mainPanel = new LabelledItemPanel();
    private DetailDataSource dataSource;
    private TranslationNotifier translationNotifier;
    private JLabel positionLabel = new JLabel();
    private JLabel lengthLabel = new JLabel();
    private JLabel destinationFieldNameLabel = new JLabel();
    private JLabel destinationFieldTypeLabel = new JLabel();
    private JLabel removeLeftZerosLabel = new JLabel();
    private JLabel expressionLabel = new JLabel();


    public FieldImportDetailWindow(DetailDataSource dataSource, Row selectedFileRow) throws RequestException {
        super("Description d'une colonne", true, false, true, true);
        this.dataSource = dataSource;
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);

        TableStructure currentStructure = getCurrentTableStructure(selectedFileRow);

        NumberField positionField = buildIntegerField();
        addField("position", positionLabel, positionField);
        addField("length", lengthLabel, buildIntegerField());
        StructureCombo combo = new StructureCombo(currentStructure.getFieldsBySqlKey());
        addField("dbDestinationFieldName", destinationFieldNameLabel, combo);
        JComboBox comboBox = new JComboBox(new Object[]{
              FieldType.STRING_FIELD,
              FieldType.NUMERIC_FIELD,
              FieldType.DATE_FIELD,
              FieldType.CLASS_FIELD,
              FieldType.BOOLEAN_FIELD
        });
        comboBox.setRenderer(new FieldTypeRenderer());
        addField("destinationFieldType", destinationFieldTypeLabel, comboBox);
        addField("removeLeftZeros", removeLeftZerosLabel, new JCheckBox());
        addField("expression", expressionLabel, new JTextArea(3, 20));

        dataSource.declare("importSettingsId", new NumberField());
        dataSource.setFieldValue("importSettingsId", selectedFileRow.getFieldValue("importSettingsId"));
        dataSource.load();
        buttonPanelLogic.setMainDataSource(dataSource);

        boolean updateMode = dataSource.getLoadFactory() != null;
        positionField.setEnabled(!updateMode);

        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(dataSource.getGuiContext());
        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        this.translationNotifier.addInternationalizableComponent(this, "FieldImportDetailWindow.title");
        this.translationNotifier
              .addInternationalizableComponent(positionLabel, "FieldImportDetailWindow.positionLabel");
        this.translationNotifier.addInternationalizableComponent(lengthLabel, "FieldImportDetailWindow.lengthLabel");
        this.translationNotifier.addInternationalizableComponent(destinationFieldNameLabel,
                                                                 "FieldImportDetailWindow.destinationFieldNameLabel");
        this.translationNotifier.addInternationalizableComponent(destinationFieldTypeLabel,
                                                                 "FieldImportDetailWindow.destinationFieldTypeLabel");
        this.translationNotifier.addInternationalizableComponent(removeLeftZerosLabel,
                                                                 "FieldImportDetailWindow.removeLeftZerosLabel");
        this.translationNotifier.addInternationalizableComponent(expressionLabel,
                                                                 "FieldImportDetailWindow.expressionLabel");
    }


    private TableStructure getCurrentTableStructure(Row selectedFileRow) {
        GuiContext context = dataSource.getGuiContext();
        Map quarantineTables = (HashMap)context.getProperty(ImportWindow.QUARANTINES_STRUCTURE);
        return (TableStructure)quarantineTables.get(selectedFileRow.getFieldValue("destTable"));
    }


    protected void addField(String fieldName, JLabel label, JComponent comp) {
        if (comp instanceof JTextArea) {
            JTextArea textArea = (JTextArea)comp;
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            mainPanel.addItem(label, new JScrollPane(textArea,
                                                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        }
        else {
            mainPanel.addItem(label, comp);
        }
        dataSource.declare(fieldName, comp);
    }


    private NumberField buildIntegerField() {
        NumberField positionField = new NumberField();
        positionField.setMaximumFractionDigits(0);
        return positionField;
    }


    private class FieldTypeRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            setText("");
            if (null != value) {
                char charValue = (Character)value;
                switch (charValue) {
                    case FieldType.BOOLEAN_FIELD:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.boolean",
                                                                   dataSource.getGuiContext()));
                        break;
                    case FieldType.CLASS_FIELD:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.class",
                                                                   dataSource.getGuiContext()));
                        break;
                    case FieldType.DATE_FIELD:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.date",
                                                                   dataSource.getGuiContext()));
                        break;
                    case FieldType.NUMERIC_FIELD:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.numeric",
                                                                   dataSource.getGuiContext()));
                        break;
                    case FieldType.STRING_FIELD:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.string",
                                                                   dataSource.getGuiContext()));
                        break;
                    default:
                        setText(InternationalizationUtil.translate("FieldImportDetailWindow#FieldTypeRenderer.unknown",
                                                                   dataSource.getGuiContext()));
                }
            }
            return this;
        }
    }
}
