package net.codjo.imports.gui;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.imports.common.FieldType;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.mad.gui.structure.StructureCombo;
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

public class FieldImportDetailWindow extends JInternalFrame {
    private LabelledItemPanel mainPanel = new LabelledItemPanel();
    private DetailDataSource dataSource;


    public FieldImportDetailWindow(DetailDataSource dataSource, Row selectedFileRow) throws RequestException {
        super("Description d'une colonne", true, false, true, true);
        this.dataSource = dataSource;
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
        this.getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);

        TableStructure currentStructure = getCurrentTableStructure(selectedFileRow);

        NumberField positionField = buildIntegerField();
        addField("position", "Position", positionField);
        addField("length", "Longueur du champ", buildIntegerField());
        StructureCombo combo = new StructureCombo(currentStructure.getFieldsBySqlKey());
        addField("dbDestinationFieldName", "Champ destination", combo);
        JComboBox comboBox = new JComboBox(new Object[]{
              FieldType.STRING_FIELD,
              FieldType.NUMERIC_FIELD,
              FieldType.DATE_FIELD,
              FieldType.CLASS_FIELD,
              FieldType.BOOLEAN_FIELD
        });
        comboBox.setRenderer(new FieldTypeRenderer());
        addField("destinationFieldType", "Type du champ destination", comboBox);
        addField("removeLeftZeros", "Suppression des zéros à gauche", new JCheckBox());
        addField("expression", "Expression", new JTextArea(3, 20));

        dataSource.declare("importSettingsId", new NumberField());
        dataSource.setFieldValue("importSettingsId", selectedFileRow.getFieldValue("importSettingsId"));
        dataSource.load();
        buttonPanelLogic.setMainDataSource(dataSource);

        boolean updateMode = dataSource.getLoadFactory() != null;
        positionField.setEnabled(!updateMode);
    }


    private TableStructure getCurrentTableStructure(Row selectedFileRow) {
        GuiContext context = dataSource.getGuiContext();
        Map quarantineTables = (HashMap)context.getProperty(ImportWindow.QUARANTINES_STRUCTURE);
        return (TableStructure)quarantineTables.get(selectedFileRow.getFieldValue("destTable"));
    }


    protected void addField(String fieldName, String label, JComponent comp) {
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
                        setText("Booléen");
                        break;
                    case FieldType.CLASS_FIELD:
                        setText("Classe");
                        break;
                    case FieldType.DATE_FIELD:
                        setText("Date");
                        break;
                    case FieldType.NUMERIC_FIELD:
                        setText("Numérique");
                        break;
                    case FieldType.STRING_FIELD:
                        setText("Chaîne de caractères");
                        break;
                    default:
                        setText("<NON_RECONNU>");
                }
            }
            return this;
        }
    }
}
