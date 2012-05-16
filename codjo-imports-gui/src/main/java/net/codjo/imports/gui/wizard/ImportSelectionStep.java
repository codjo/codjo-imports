/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.codjo.gui.toolkit.fileChooser.FileChooserUtils;
import net.codjo.gui.toolkit.path.FilePathField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.imports.gui.plugin.ImportGuiPlugin;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.workflow.gui.wizard.AbstractSelectionStep;
import net.codjo.workflow.gui.wizard.WizardConstants;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.translate;
/**
 *
 */
public class ImportSelectionStep extends AbstractSelectionStep implements InternationalizableContainer {
    public static final String SELECTION_FILE = "import.file";
    public static final String SELECTION_TYPE = "import.type";
    private RequestComboBox importTypeCombo;
    private FilePathField importFilePath;
    private JLabel errorLabel;
    private ImportSelector selector;
    private String importsInbox;
    private GuiContext guiContext;
    private JLabel importTypeComboLabel;
    private JLabel importFilePathLabel;


    public ImportSelectionStep(GuiContext guiContext, ImportSelector importSelector) {
        super(null, "ImportSelectionStep.title");
        this.guiContext = guiContext;
        this.importsInbox = (String)guiContext.getProperty(ImportGuiPlugin.IMPORTS_INBOX_PARAMETER);
        selector = importSelector;

        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(this, "ImportSelectionStep.name");
        translationNotifier.addInternationalizableComponent(importTypeComboLabel,
                                                            "ImportSelectionStep.importTypeComboLabel");
        translationNotifier.addInternationalizableComponent(importFilePathLabel,
                                                            "ImportSelectionStep.importFilePathLabel");
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
            ErrorDialog.show(this, translate("ImportSelectionStep.initGui.loadErrorMessage", guiContext), ex);
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

        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        importFilePath.setTranslationBackpack(InternationalizationUtil.retrieveTranslationManager(guiContext),
                                              notifier);

        importFilePath.setWithAccessories(false);
        FileChooserUtils.setUILanguage(notifier.getLanguage().getLocale());
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
            errorLabel.setText(
                  translate("ImportSelectionStep.errorLabel.fileNameTooLong.begin", guiContext) + maxFileNameLength
                  + " " + translate("ImportSelectionStep.errorLabel.fileNameTooLong.end", guiContext));
        }
        else if (finalFileName.indexOf(".") != finalFileName.lastIndexOf(".")) {
            errorLabel.setText(translate("ImportSelectionStep.errorLabel.onlyOneExtension", guiContext));
        }
        else if (finalFileName.indexOf(".") > 0
                 && (finalFileName.length() - finalFileName.lastIndexOf(".") > 19)) {
            errorLabel.setText(translate("ImportSelectionStep.errorLabel.extensionNameTooLong", guiContext));
        }
        else if (!ImportFileUtil.checkFileName(finalFileName)) {
            errorLabel.setText(translate("ImportSelectionStep.errorLabel.nonAuthorizedCharacters", guiContext));
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

        importTypeComboLabel = new JLabel();
        add(importTypeComboLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL, insets, 0, 0));

        add(importTypeCombo,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

        importFilePathLabel = new JLabel();
        add(importFilePathLabel,
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
