/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.plugin;
import java.io.File;
import javax.swing.Action;
import javax.swing.ImageIcon;
import net.codjo.gui.toolkit.wizard.Wizard;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.imports.gui.ImportAction;
import net.codjo.imports.gui.wizard.DefaultImportSelector;
import net.codjo.imports.gui.wizard.ImportSelectionStep;
import net.codjo.imports.gui.wizard.ImportSelector;
import net.codjo.imports.gui.wizard.ImportTemplateRequestFactory;
import net.codjo.imports.gui.wizard.ImportVtomCaller;
import net.codjo.imports.gui.wizard.ImportWizardSummaryGui;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.plugin.MadConnectionPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.AbstractInternationalizableGuiPlugin;
import net.codjo.mad.gui.plugin.MadGuiPlugin;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.workflow.common.message.JobRequestTemplate;
import net.codjo.workflow.gui.plugin.WorkflowGuiPlugin;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.DefaultJobGui;
import net.codjo.workflow.gui.wizard.DefaultRequestTemplateFactory;
import net.codjo.workflow.gui.wizard.FinalStep;
import net.codjo.workflow.gui.wizard.WizardAction;
import net.codjo.workflow.gui.wizard.WizardBuilder;
import org.xml.sax.InputSource;
/**
 * Plugin permettant d'enregistrer les préférences des familles d'import.
 */
public final class ImportGuiPlugin extends AbstractInternationalizableGuiPlugin {
    public static final String IMPORT_VTOM_PARAMETER = "imports.vtom";
    public static final String IMPORTS_INBOX_PARAMETER = "imports.inbox";
    private static final String FIELD_IMPORT_PREFERENCE_ID = "FieldImportWindow";
    private static final String IMPORT_SETTINGS_PREFERENCE_ID = "ImportSettingsWindow";
    private static final String IMPORT_PREFERENCE_FILE_NAME = "importPreference.xml";
    private static final String WIZARD_IMAGE = "wizard.import.gif";
    private static final String WIZARD_DESCRIPTION = "Assistant d'import à la demande";
    private static final String WIZARD_ACTION = "ImportWizard";
    private static final String IMPORT_ACTION = "ImportAction";
    private static final String WIZARD_ICON = "/net/codjo/imports/gui/wizard/imports.gif";
    private final MadGuiPlugin madGuiPlugin;
    private final ImportGuiPluginConfiguration configuration;
    private GuiContext guiContext;


    public ImportGuiPlugin(MadGuiPlugin madGuiPlugin,
                           MadConnectionPlugin madConnectionPlugin,
                           WorkflowGuiPlugin workflowGuiPlugin) {
        this.madGuiPlugin = madGuiPlugin;

        workflowGuiPlugin.getConfiguration()
              .setTaskManagerJobIcon(ImportJobRequest.IMPORT_JOB_TYPE,
                                     new ImageIcon(getClass().getResource("/images/job.import.png")));

        configuration = new ImportGuiPluginConfiguration(madConnectionPlugin.getOperations());
    }


    @Override
    protected void registerLanguageBundles(TranslationManager translationManager) {
        translationManager.addBundle("net.codjo.imports.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.imports.gui.i18n", Language.EN);
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        super.initGui(guiConfiguration);

        guiContext = guiConfiguration.getGuiContext();

        registerActions(guiConfiguration);

        loadImportPreferences();
    }


    public ImportGuiPluginConfiguration getConfiguration() {
        return configuration;
    }


    void registerActions(GuiConfiguration guiConfiguration) {
        WizardAction wizardAction = createWizardAction(guiConfiguration);
        guiConfiguration.registerAction(this, WIZARD_ACTION, wizardAction);

        Action importAction = new ImportAction(guiContext);
        guiConfiguration.registerAction(this, IMPORT_ACTION, importAction);
    }


    void loadImportPreferences() {
        if (PreferenceFactory.containsPreferenceId(FIELD_IMPORT_PREFERENCE_ID)
            && PreferenceFactory.containsPreferenceId(IMPORT_SETTINGS_PREFERENCE_ID)) {
            return;
        }
        InputSource inputSource =
              new InputSource(getClass().getResourceAsStream(IMPORT_PREFERENCE_FILE_NAME));
        madGuiPlugin.getConfiguration().addPreferenceMapping(inputSource);
    }


    private WizardAction createWizardAction(GuiConfiguration guiConfiguration) {
        String file = (String)guiConfiguration.getGuiContext().getProperty(IMPORT_VTOM_PARAMETER);
        return new WizardAction(guiConfiguration.getGuiContext(),
                                "net.codjo.imports.gui.plugin.ImportGuiPlugin#ImportWizard",
                                WIZARD_DESCRIPTION,
                                new ImportWizardBuilder(new File(file)),
                                WIZARD_ACTION,
                                WIZARD_ICON,
                                new ImageIcon(getClass().getResource(WIZARD_IMAGE)));
    }


    public class ImportGuiPluginConfiguration {
        private ImportSelector wizardImportSelector;
        private ImportsWizardCustomizer importsWizardCustomizer = new EmptyImportsWizardCustomizer();


        ImportGuiPluginConfiguration(MadConnectionOperations madConnectionOperations) {
            wizardImportSelector = new DefaultImportSelector(madConnectionOperations,
                                                             "selectAllImportSettings");
        }


        public void setWizardImportSelector(ImportSelector wizardImportSelector) {
            this.wizardImportSelector = wizardImportSelector;
        }


        public ImportSelector getWizardImportSelector() {
            return wizardImportSelector;
        }


        public ImportsWizardCustomizer getImportsWizardCustomizer() {
            return importsWizardCustomizer;
        }


        public void setImportsWizardCustomizer(ImportsWizardCustomizer importsWizardCustomizer) {
            this.importsWizardCustomizer = importsWizardCustomizer;
        }
    }

    private class ImportWizardBuilder implements WizardBuilder {
        private final File file;


        ImportWizardBuilder(File file) {
            this.file = file;
        }


        public Wizard createWizard() {
            FinalStep.JobGuiData importStep =
                  new FinalStep.JobGuiData(new DefaultJobGui(guiContext, "ImportGuiPlugin.import"),
                                           new ImportTemplateRequestFactory());
            FinalStep.JobGuiData controlStep =
                  new FinalStep.JobGuiData(new DefaultJobGui(guiContext, "ImportGuiPlugin.control"),
                                           new DefaultRequestTemplateFactory(
                                                 JobRequestTemplate.matchType("control")));
            FinalStep finalStep =
                  new FinalStep("ImportGuiPlugin.finalStep.title",
                                new ImportVtomCaller(new CommandFile(file)),
                                new ImportWizardSummaryGui(guiContext),
                                new DefaultJobGui(guiContext, "ImportGuiPlugin.VTOMProcess"),
                                new FinalStep.JobGuiData[]{importStep, controlStep});

            Wizard wizard = new Wizard();
            wizard.addStep(new ImportSelectionStep(guiContext,
                                                   getConfiguration().getWizardImportSelector()));
            configuration.importsWizardCustomizer.customizeWizard(wizard);
            wizard.setFinalStep(finalStep);

            return wizard;
        }
    }
}
