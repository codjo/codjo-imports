/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.DFService;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.message.ImportJobAuditArgument;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.message.JobException;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocolParticipant;
import net.codjo.workflow.server.api.JobAgent;
import java.io.File;
/**
 *
 */
class ImportJobAgent extends JobAgent {
    public static final String JOB_REQUEST_TYPE = ImportServerPlugin.IMPORT_JOB_TYPE;


    ImportJobAgent(ImporterFactory importerFactory) {
        this(importerFactory, MODE.NOT_DELEGATE);
    }


    ImportJobAgent(ImporterFactory importerFactory, MODE mode) {
        super(new ImportParticipant(importerFactory),
              new DFService.AgentDescription(
                    new DFService.ServiceDescription(JOB_REQUEST_TYPE, "import-service")), mode);
    }


    private static class ImportParticipant extends JobProtocolParticipant {
        private final ImporterFactory importerFactory;
        private String destinationTable;
        private String fileName;


        ImportParticipant(ImporterFactory importerFactory) {
            this.importerFactory = importerFactory;
        }


        @Override
        protected void executeJob(JobRequest request)
              throws JobException {
            importerFactory.init(getAgent(), getRequestMessage());
            ImportJobRequest importRequest = new ImportJobRequest(request);
            File file = importRequest.getFile();

            try {
                Importer importer = importerFactory.createImporter(file);
                destinationTable = importer.getDestinationTable();
                fileName = importer.getFileName();
                importer.execute();
            }
            catch (ImportFailureException exception) {
                throw new JobException(exception.getLocalizedMessage(), exception);
            }
            catch (Exception exception) {
                throw new JobException("Error lors de l'import du fichier '"
                                       + file.getPath() + "' :" + exception.getLocalizedMessage(), exception);
            }
        }


        @Override
        protected void handlePOST(JobRequest request, JobException failure) {
            JobAudit audit = new JobAudit(JobAudit.Type.POST);
            if (failure != null) {
                audit.setError(new JobAudit.Anomaly(failure.getMessage(), failure));
            }
            else {
                Arguments arguments = new Arguments(ImportJobAuditArgument.FILLED_TABLE, destinationTable);
                arguments.put(ImportJobRequest.FILE_NAME, fileName);
                audit.setArguments(arguments);
            }
            sendAudit(audit);
        }
    }
}
