/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.gui.toolkit.path.FilePathField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.workflow.gui.wizard.AbstractSelectionStep;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 */
public class ImportSelectionStep extends AbstractSelectionStep {
    public static final String SELECTION_FILE = "import.file";
    public static final String SELECTION_TYPE = "import.type";
    private RequestComboBox importTypeCombo;
    private FilePathField importFilePath;
    private JLabel errorLabel;
    private ImportSelector selector;
    private String importsInbox;


    public ImportSelectionStep(String importsInbox, ImportSelector importSelector) {
        super(null, "Selection du type d'import :");
        this.importsInbox = importsInbox;
        selector = importSelector;
    }


    @Override
    public void initGui() {
        errorLabel.setForeground(Color.RED);
        importTypeCombo.setModelFieldName("fileType");
        try {
            Result result = selector.selectImportItems(new String[]{"importSettingsId", "fileType"});
            importTypeCombo.getDataSource().setLoadResult(result);
        }
        catch (RequestException ex) {
            ErrorDialog.show(this, "Impossible de charger la liste des fichier d'import.", ex);
        }

        importTypeCombo.getDataSource().addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                                                  new PropertyChangeListener() {
                                                                      public void propertyChange(
                                                                            PropertyChangeEvent evt) {
                                                                          checkFulfilled();
                                                                      }
                                                                  });

        importFilePath.getFileNameField().getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent evt) {
                checkFulfilled();
            }


            public void insertUpdate(DocumentEvent evt) {
                checkFulfilled();
            }


            public void removeUpdate(DocumentEvent evt) {
                checkFulfilled();
            }
        });
    }


    private void checkFulfilled() {
        if (importTypeCombo.getSelectedIndex() != -1 && importFilePath.getFile() != null) {
            setFulfilled(checkFileName());
            setValue(WizardConstants.IMPORT_TYPE, importTypeCombo.getSelectedValue("fileType"));
            setValue(WizardConstants.IMPORT_FILE_PATH, importFilePath.getFile().getAbsolutePath());
            setValue(WizardConstants.IMPORT_INBOX, importsInbox);
        }
        else {
            errorLabel.setText("");
            setFulfilled(false);
            setValue(WizardConstants.IMPORT_TYPE, null);
            setValue(WizardConstants.IMPORT_FILE_PATH, null);
        }
    }


    private boolean checkFileName() {
        String filePrefix =
              System.getProperty("user.name") + "_" + importTypeCombo.getSelectedValue("fileType") + "_";

        String finalFileName = filePrefix + importFilePath.getFile().getName();

        int maxFileNameLength = 39 - (filePrefix.length());

        if (finalFileName.length() > 39) {
            errorLabel.setText("Le nom du fichier est trop long ! (" + maxFileNameLength
                               + " caractère(s) au maximum)");
        }
        else if (finalFileName.indexOf(".") != finalFileName.lastIndexOf(".")) {
            errorLabel.setText("Le nom du fichier doit contenir une seule extension !");
        }
        else if (finalFileName.indexOf(".") > 0
                 && (finalFileName.length() - finalFileName.lastIndexOf(".") > 19)) {
            errorLabel.setText("L'extension du fichier ne doit pas depasser 19 caractères !");
        }
        else if (!ImportFileUtil.checkFileName(finalFileName)) {
            errorLabel.setText("Le nom du fichier contient des caractères non autorisés !");
        }
        else {
            errorLabel.setText("");
            return true;
        }
        return false;
    }


    @Override
    public void buildLayout() {
        importTypeCombo = new RequestComboBox();
        importTypeCombo.setName("importTypeComboBox");
        importTypeCombo.setModelFieldName("fileType");
        importTypeCombo.setRendererFieldName("fileType");

        importFilePath = new FilePathField();
        importFilePath.setName("importFilePath");

        errorLabel = new JLabel();
        errorLabel.setName("errorLabel");

        setLayout(new GridBagLayout());

        Insets insets = new Insets(4, 4, 4, 4);

        add(new JLabel("Type de fichier :"),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL, insets, 0, 0));

        add(importTypeCombo,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

        add(new JLabel("Fichier à importer :"),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

        add(importFilePath,
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

        add(errorLabel,
            new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
    }
}
