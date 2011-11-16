/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestTemplate;
import net.codjo.workflow.gui.wizard.RequestTemplateFactory;
import net.codjo.workflow.gui.wizard.WizardConstants;
import java.io.File;
import java.util.Map;
/**
 *
 */
public class ImportTemplateRequestFactory implements RequestTemplateFactory {
    public JobRequestTemplate createTemplate(final Map wizardState) {
        JobRequestTemplate matchType = JobRequestTemplate.matchType(ImportJobRequest.IMPORT_JOB_TYPE);
        JobRequestTemplate matchInitiator =
              JobRequestTemplate.matchInitiator(System.getProperty("user.name"));
        JobRequestTemplate matchRequest =
              JobRequestTemplate.matchCustom(new ImportMatchExpression(wizardState));

        return JobRequestTemplate.and(matchType,
                                      JobRequestTemplate.and(matchInitiator, matchRequest));
    }


    private class ImportMatchExpression implements JobRequestTemplate.MatchExpression {
        private String selectionFileName;


        ImportMatchExpression(Map wizardState) {
            String selectionFileType =
                  (String)wizardState.get(WizardConstants.IMPORT_TYPE);
            File sourceFile =
                  new File((String)wizardState.get(WizardConstants.IMPORT_FILE_PATH));
            selectionFileName =
                  ImportFileUtil.determineDestinationFileName(sourceFile, selectionFileType)
                        .toLowerCase();
        }


        public boolean match(JobRequest jobRequest) {
            ImportJobRequest request = new ImportJobRequest(jobRequest);
            String requestFileName = request.getFile().getName().toLowerCase();
            return requestFileName.startsWith(selectionFileName);
        }
    }
}
