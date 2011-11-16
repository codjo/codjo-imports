/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.batch.plugin;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.plugin.batch.BatchCore;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPlugin;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPluginTestCase;
import static net.codjo.workflow.common.util.WorkflowSystem.workFlowSystem;
import org.junit.Test;
/**
 * Classe de test de {@link net.codjo.imports.batch.plugin.ImportBatchPlugin}.
 */
public class ImportBatchPluginTest extends AbstractWorkflowBatchPluginTestCase {

    @Test
    public void test_execute_jobs_workflow() throws Exception {
        story.record().mock(workFlowSystem())
              .schedule("import -> nextJob")
              .then()
              .simulateJob(getExpectedLogContentAfterExecute())
              .forUser(USER_ID)
              .then()
              .simulateJob("job<nextJob>()");

        story.record().addAction(new AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                CommandLineArguments arguments = buildCommandLineArguments();

                plugin.start(story.getAgentContainerFixture().getContainer());
                plugin.execute(USER_ID, arguments);
            }
        });

        story.execute();
    }


    @Override
    protected String getExpectedLogContentAfterExecute() {
        return "job<import>(import.fileName=fileName, import.source.folder=directory/)";
    }


    @Override
    protected CommandLineArguments buildCommandLineArguments() {
        return new CommandLineArguments(new String[]{
              "-" + BatchCore.BATCH_ARGUMENT, "directory\\fileName",
              "-" + BatchCore.BATCH_TYPE, "Decisiv"
        });
    }


    @Override
    protected String getExpectedType() {
        return ImportJobRequest.IMPORT_JOB_TYPE;
    }


    @Override
    protected AbstractWorkflowBatchPlugin createPlugin() {
        return new ImportBatchPlugin();
    }
}
