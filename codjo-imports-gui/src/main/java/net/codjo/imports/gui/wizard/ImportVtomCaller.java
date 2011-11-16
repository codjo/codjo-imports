/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.VtomCaller;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.io.File;
import java.io.IOException;
import java.util.Map;
/**
 *
 */
public class ImportVtomCaller implements VtomCaller {
    private static final int MINUTE = 60 * 1000;
    private final CommandFile commandFile;


    public ImportVtomCaller(CommandFile commandFile) {
        this.commandFile = commandFile;
        this.commandFile.setTimeout(60 * MINUTE);
    }


    public void call(Map wizardState) throws CommandFile.ExecuteException {
        String fileToImport = (String)wizardState.get(WizardConstants.IMPORT_FILE_PATH);
        String importType = (String)wizardState.get(WizardConstants.IMPORT_TYPE);
        String inbox = (String)wizardState.get(WizardConstants.IMPORT_INBOX);

        File file;
        try {
            file = ImportFileUtil.copyFileToInbox(new File(fileToImport), importType, inbox);
        }
        catch (IOException exception) {
            throw new CommandFile.ExecuteException(exception, "Erreur lors de la copie dans l'inbox", -1);
        }

        commandFile.execute(new String[]{System.getProperty("user.name"), file.getName()});
    }
}
