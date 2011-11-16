/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.workflow.common.message.JobRequestTemplate;
import net.codjo.workflow.gui.wizard.WizardUtil;
import java.io.File;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ImportTemplateRequestFactory}.
 */
public class ImportTemplateRequestFactoryTest extends TestCase {
    public void test_createTemplate() throws Exception {
        ImportTemplateRequestFactory factory = new ImportTemplateRequestFactory();
        JobRequestTemplate template =
              factory.createTemplate(WizardUtil.createImportState("CRA052005.txt",
                                                                  "Decisiv", "C:/inbox"));

        ImportJobRequest importRequest = new ImportJobRequest();
        importRequest.setInitiatorLogin(System.getProperty("user.name"));

        importRequest.setFile(new File("/AGFAM/CRA052005.txt"));
        assertFalse(template.match(importRequest.toRequest()));

        importRequest.setFile(new File("/AGFAM/", System.getProperty("user.name")
                                                  + "_DECISIV_cra052005.txt_20060405_163042"));
        assertTrue(template.match(importRequest.toRequest()));
    }
}
