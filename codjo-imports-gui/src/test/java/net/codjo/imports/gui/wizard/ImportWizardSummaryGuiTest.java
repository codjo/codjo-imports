/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.imports.gui.ImportsGuiContext;
import net.codjo.workflow.gui.wizard.WizardUtil;
import java.util.Map;
import org.uispec4j.Panel;
import org.uispec4j.UISpecTestCase;
/**
 *
 */
public class ImportWizardSummaryGuiTest extends UISpecTestCase {
    public void test_displayStart() throws Exception {
        Map displayStart =
              WizardUtil.createImportState("to_import.txt", "Decisiv", "inbox");

        ImportWizardSummaryGui summaryPanel = new ImportWizardSummaryGui(new ImportsGuiContext());

        summaryPanel.display(displayStart);

        Panel mainPanel = new Panel(summaryPanel.getGui());

        assertTrue(mainPanel.getInputTextBox("filePath").textEquals("to_import.txt"));
        assertTrue(mainPanel.getInputTextBox("fileType").textEquals("Decisiv"));
    }
}
