/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.ContainerConfigurationMock;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.sql.server.plugin.JdbcServerPluginMock;
import net.codjo.test.common.LogString;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ImportServerPlugin}.
 */
public class ImportServerPluginTest extends TestCase {
    private ImportServerPlugin plugin;
    private LogString log = new LogString();
    private AgentContainerFixture fixture = new AgentContainerFixture();


    public void test_other() throws Exception {
        plugin.initContainer(new ContainerConfigurationMock(log));
        plugin.stop();
        log.assertContent("");
    }


    public void test_start() throws Exception {
        plugin.start(fixture.getContainer());

        fixture.assertNumberOfAgentWithService(2, ImportServerPlugin.IMPORT_JOB_TYPE);
        fixture.assertAgentWithService(new String[]{"import-drh-agent", "import-job-agent"}
              , ImportServerPlugin.IMPORT_JOB_TYPE);
    }


    public void test_filterFactory() throws Exception {
        assertNotNull(plugin.getConfiguration());
        assertNull(plugin.getConfiguration().getFilterFactory());

        FilterFactoryMock filterFactory = new FilterFactoryMock(null);
        plugin.getConfiguration().setFilterFactory(filterFactory);
        assertSame(filterFactory, plugin.getConfiguration().getFilterFactory());
    }


    @Override
    protected void setUp() throws Exception {
        plugin = new ImportServerPlugin(new JdbcServerPluginMock(), new WorkflowServerPlugin());
        fixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }
}
