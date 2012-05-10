/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import net.codjo.gui.toolkit.wizard.Step;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.imports.gui.ImportsGuiContext;
import net.codjo.imports.gui.plugin.ImportGuiPlugin;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.test.common.LogString;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import net.codjo.workflow.gui.wizard.WizardConstants;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowInterceptor;
/**
 * Classe de test de {@link net.codjo.imports.gui.wizard.ImportSelectionStep}.
 */
public class ImportSelectionStepTest extends UISpecTestCase {
    private MadServerFixture madServerFixture = new MadServerFixture();
    private DirectoryFixture directoryFixture =
          DirectoryFixture.newTemporaryDirectoryFixture("ImportSelectionStepTest");
    private CompositeFixture fixture = new CompositeFixture(madServerFixture, directoryFixture);
    private LogString log = new LogString();
    private Panel panel;
    private ImportSelectionStep step;
    private MutableGuiContext guiContext;


    public void test_stepIsFulfilled() throws Exception {
        assertEquals("Selection du type d'import :", InternationalizationUtil.translate(step.getName(), guiContext));

        mockStart(step);

        ComboBox typeCombo = panel.getComboBox();
        assertEquals("importTypeComboBox", typeCombo.getName());
        assertTrue(typeCombo.contentEquals(new String[]{"to_import_1", "to_import_2"}));

        assertFalse(step.isFulfilled());

        typeCombo.select("to_import_1");
        assertFalse(step.isFulfilled());

        File expectedPath = createFile("to_import.txt");
        selectFile(expectedPath);

        assertTrue(step.isFulfilled());
        assertStepStates("to_import_1", expectedPath.getAbsolutePath());
    }


    public void test_event() throws Exception {
        mockStart(step);

        ComboBox typeCombo = panel.getComboBox();

        step.addPropertyChangeListener(Step.FULFILLED_PROPERTY,
                                       new PropertyChangeListener() {
                                           public void propertyChange(PropertyChangeEvent evt) {
                                               log.call("propertyChange", evt.getNewValue());
                                           }
                                       });

        typeCombo.select("to_import_1");

        File expectedPath = createFile("to_import.txt");
        selectFile(expectedPath);

        log.assertContent("propertyChange(true)");
    }


    public void test_overrideImportSelector() throws Exception {
        guiContext.putProperty(ImportGuiPlugin.IMPORTS_INBOX_PARAMETER, "c:/inbox");
        step = new ImportSelectionStep(guiContext, new ImportSelectorMock(log));
        step.start(null);
        log.assertContent("selectImportItems([importSettingsId, fileType])");
    }


    public void test_fileNameTooLong() throws Exception {
        assertBadFile("thisFileIsTooLongToBeImported.txt",
                      "Le nom du fichier est trop long ! (22 caractère(s) au maximum)");
    }


    public void test_fileNameTooLongEnglish() throws Exception {
        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.setLanguage(Language.EN);

        assertBadFile("thisFileIsTooLongToBeImported.txt",
                      "File Name Too Long ! (22 character(s) max)");
    }


    public void test_fileMoreThanOneExtension() throws Exception {
        assertBadFile("to.import.txt",
                      "Le nom du fichier doit contenir une seule extension !");
    }


    public void test_fileExtensionTooLong() throws Exception {
        assertBadFile("to.extension_tooo_long",
                      "L'extension du fichier ne doit pas depasser 19 caractères !");
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.getProperties().put("user.name", "toto");
        DefaultImportSelector importSelector =
              new DefaultImportSelector(madServerFixture.getOperations(), "selectAllImportSettings");
        guiContext = new ImportsGuiContext();
        guiContext.putProperty(ImportGuiPlugin.IMPORTS_INBOX_PARAMETER, "inbox");
        step = new ImportSelectionStep(guiContext, importSelector);
        panel = new Panel(step);
        fixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
        super.tearDown();
    }


    private void mockStart(ImportSelectionStep importSelectionStep) {
        madServerFixture.mockServerResult(new String[]{"importSettingsId", "fileType"},
                                          new String[][]{
                                                {"to_import_1", "to_import_1"},
                                                {"to_import_2", "to_import_2"}
                                          });

        madServerFixture.assertRequestedHandlers(new String[0]);

        importSelectionStep.start(null);

        madServerFixture.assertRequestedHandlers(new String[]{"selectAllImportSettings"});
    }


    private void assertBadFile(String fileName, String error) throws IOException {
        mockStart(step);

        ComboBox typeCombo = panel.getComboBox();
        TextBox errorLabel = panel.getTextBox("errorLabel");

        typeCombo.select("to_import_1");

        File temp = createFile(fileName);

        selectFile(temp);

        assertTrue(!step.isFulfilled());
        assertTrue(error.equals(errorLabel.getText()));
        assertStepStates("to_import_1", temp.getPath());
    }


    private void assertStepStates(String expectedFileType, String expectedFilePath) {
        String filePath = (String)step.getState().get(WizardConstants.IMPORT_FILE_PATH);
        assertEquals(expectedFilePath, filePath);

        final String fileType = (String)step.getState().get(WizardConstants.IMPORT_TYPE);
        assertEquals(expectedFileType, fileType);
    }


    private void selectFile(File fileToBeImported) {
        WindowInterceptor.init(panel.getButton("selectFileButton").triggerClick())
              .process(FileChooserHandler.init().assertIsOpenDialog().select(fileToBeImported))
              .run();
    }


    private File createFile(String fileName) throws IOException {
        File expectedPath = new File(directoryFixture, fileName);
        FileUtil.saveContent(expectedPath, "content");
        return expectedPath;
    }
}
