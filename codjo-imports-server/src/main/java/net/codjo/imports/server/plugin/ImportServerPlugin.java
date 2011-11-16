/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.DFService;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.imports.plugin.filter.kernel.DefaultImportFunctionHolder;
import net.codjo.imports.server.audit.ImportStringifier;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.sql.server.JdbcServiceUtil;
import net.codjo.sql.server.plugin.JdbcServerPlugin;
import net.codjo.workflow.server.api.JobAgent;
import net.codjo.workflow.server.api.JobAgent.MODE;
import static net.codjo.workflow.server.api.JobAgent.MODE.DELEGATE;
import net.codjo.workflow.server.api.ResourcesManagerAgent;
import net.codjo.workflow.server.api.ResourcesManagerAgent.AgentFactory;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;

public final class ImportServerPlugin implements ServerPlugin {
    private final ImportServerPluginConfiguration importConfiguration;
    public static final String IMPORT_JOB_TYPE = ImportJobRequest.IMPORT_JOB_TYPE;


    public ImportServerPlugin(JdbcServerPlugin jdbc, WorkflowServerPlugin workflowServerPlugin) {
        this.importConfiguration = new DefaultImportServerPluginConfiguration();
        importConfiguration.addFunctionHolder(new DefaultImportFunctionHolder());
        workflowServerPlugin.getConfiguration().registerJobBuilder(
              new ImportJobRequestHandler(jdbc.getOperations(), new DefaultImportJobDao()));
        new ImportStringifier().install(workflowServerPlugin);
    }


    public void initContainer(ContainerConfiguration configuration) {
    }


    public void start(AgentContainer agentContainer)
          throws Exception {

        agentContainer.acceptNewAgent("import-drh-agent",
                                      new ResourcesManagerAgent(
                                            new ImportAgentFactory(),
                                            DFService.createAgentDescription(IMPORT_JOB_TYPE))).start();
        agentContainer.acceptNewAgent("import-job-agent", createImportAgent(MODE.NOT_DELEGATE)).start();
    }


    private ImportJobAgent createImportAgent(MODE mode) {
        ImportJobAgent agent =
              new ImportJobAgent(new DefaultImporterFactory(new JdbcServiceUtil(), importConfiguration),
                                 mode);
        return agent;
    }


    public void stop() {
    }


    public ImportServerPluginConfiguration getConfiguration() {
        return importConfiguration;
    }


    private class ImportAgentFactory implements AgentFactory {
        public JobAgent create() throws Exception {
            return createImportAgent(DELEGATE);
        }
    }
}
