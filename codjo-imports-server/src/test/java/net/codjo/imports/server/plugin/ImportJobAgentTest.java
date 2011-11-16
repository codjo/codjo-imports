/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Aid;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.test.Story;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.UnknownImportException;
import net.codjo.imports.common.message.ImportJobAuditArgument;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobAudit;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.protocol.JobProtocol;
import net.codjo.workflow.server.api.JobAgent.MODE;
import java.io.File;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ImportJobAgent}.
 */
public class ImportJobAgentTest extends TestCase {
    private static final String IMPORT_AID = "import-job-agent";
    private static final String UNKNOWN_FILE_PATH = "unknown_file.txt";
    private ImportJobAgent importAgent;
    private Story story = new Story();
    private LogString log = new LogString();
    private ImporterFactoryMock importFactory;


    public void test_imports() throws Exception {
        story.record().startAgent(IMPORT_AID, importAgent);

        story.record().startTester("tester")
              .sendMessage(createImportRequest("oggi", "input", "myFile.txt"))
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.PRE))
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.POST))
              .assertReceivedMessage(hasAuditContent(ImportJobAuditArgument.FILLED_TABLE, "MY_QUARANTINE"))
              .assertReceivedMessage(hasAuditContent(ImportJobRequest.FILE_NAME, "to_import.txt"));

        story.execute();

        log.assertContent("importFactory.init(agent:import-job-agent, message:REQUEST)"
                          + ", importFactory.createImporter(input\\myFile.txt)"
                          + ", importer.execute()");
    }


    public void test_import_unknownFile() throws Exception {
        ImporterMock importerMock = new ImporterMock();

        importFactory.mockCreateImporter(importerMock);
        importerMock.mockExecuteFailure(new ImportFailureException("error during import"));

        story.record().startAgent(IMPORT_AID, importAgent);

        story.record().startTester("tester")
              .sendMessage(createImportRequest("oggi", "conf", "myFile.txt"))
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.PRE))
              .assertReceivedMessage(hasNoAuditError())
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.POST))
              .assertReceivedMessage(hasAuditError(true, "error during import"));

        story.execute();
    }


    public void test_import_error() throws Exception {
        importFactory.mockCreateImporterFailure(new UnknownImportException(
              new File(UNKNOWN_FILE_PATH)));

        story.record().startAgent(IMPORT_AID, importAgent);

        story.record().startTester("tester")
              .sendMessage(createImportRequest("oggi", ".", UNKNOWN_FILE_PATH))
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.PRE))
              .assertReceivedMessage(hasNoAuditError())
              .then()
              .receiveMessage(hasAuditType(JobAudit.Type.POST))
              .assertReceivedMessage(hasAuditError(true,
                                                   "Aucun paramétrage trouvé pour le fichier : "
                                                   + UNKNOWN_FILE_PATH));

        story.execute();
    }


    public void test_agentDescription() throws Exception {
        story.record().startAgent(IMPORT_AID, importAgent);

        story.record().assertAgentWithService(new String[]{IMPORT_AID}, ImportJobAgent.JOB_REQUEST_TYPE);
    }


    @Override
    protected void setUp() throws Exception {
        story.doSetUp();
        importFactory = new ImporterFactoryMock(new LogString("importFactory", log));
        importAgent = new ImportJobAgent(importFactory, MODE.NOT_DELEGATE);
    }


    @Override
    protected void tearDown() throws Exception {
        story.doTearDown();
    }


    private AclMessage createImportRequest(String initiator, String path,
                                           String fileToBeImported) {
        AclMessage requestMessage = new AclMessage(AclMessage.Performative.REQUEST);
        requestMessage.setConversationId("conversation-id");
        requestMessage.setProtocol(JobProtocol.ID);
        requestMessage.addReceiver(new Aid(IMPORT_AID));

        JobRequest request = new JobRequest("imports");
        request.setId(requestMessage.getConversationId());
        request.setInitiatorLogin(initiator);
        Arguments arguments = new Arguments(ImportJobRequest.FILE_NAME, fileToBeImported);
        arguments.put(ImportJobRequest.SOURCE_FOLDER, path);
        request.setArguments(arguments);

        requestMessage.setContentObject(request);
        return requestMessage;
    }


    // --------- COPIED from BroadcastJobAgentTest
    private MessageTemplate hasNoAuditError() {
        return hasAuditError(false, "");
    }


    private MessageTemplate hasAuditError(final boolean hasError, final String message) {
        return MessageTemplate.matchWith(new MessageTemplate.MatchExpression() {
            private String actual = "";


            public boolean match(AclMessage aclMessage) {
                JobAudit jobAudit = (JobAudit)aclMessage.getContentObject();
                if (jobAudit.hasError() && hasError) {
                    actual = jobAudit.getErrorMessage();
                    return message.equals(actual);
                }
                return jobAudit.hasError() == hasError;
            }


            @Override
            public String toString() {
                return "hasAuditError(" + hasError + ", \nexpected:" + message
                       + "\nactual  :" + actual + ")";
            }
        });
    }


    private MessageTemplate hasAuditContent(final String key, final String value) {
        return MessageTemplate.matchWith(new MessageTemplate.MatchExpression() {
            private String actual = "";


            public boolean match(AclMessage aclMessage) {
                JobAudit jobAudit = (JobAudit)aclMessage.getContentObject();
                actual = jobAudit.getArguments().get(key);
                if (actual != null) {
                    return actual.equals(value);
                }
                return (value == null);
            }


            @Override
            public String toString() {
                return "hasAuditContent(" + key + ", \nexpected:" + value
                       + "\nactual  :" + actual + ")";
            }
        });
    }


    private MessageTemplate hasAuditType(JobAudit.Type type) {
        return MessageTemplate.and(MessageTemplate.matchPerformative(AclMessage.Performative.INFORM),
                                   matchType(type));
    }


    private MessageTemplate matchType(final JobAudit.Type auditType) {
        return MessageTemplate.matchWith(new MessageTemplate.MatchExpression() {
            public boolean match(AclMessage aclMessage) {
                JobAudit jobAudit = (JobAudit)aclMessage.getContentObject();
                return auditType == jobAudit.getType();
            }
        });
    }
}
