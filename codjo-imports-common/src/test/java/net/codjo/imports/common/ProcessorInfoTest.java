/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.sql.Connection;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ProcessorInfo}.
 */
public class ProcessorInfoTest extends TestCase {
    public void test_build() throws Exception {
        ProcessorInfo processorInfo = new ProcessorInfo(FakeProcessor.class.getName());

        Processor processor = processorInfo.getProcessor();

        assertEquals(FakeProcessor.class, processor.getClass());
        assertEquals("Aucun argument n'est fournit", "constructeur sans argument",
                     ((FakeProcessor)processor).argument);
    }


    public void test_build_arg() throws Exception {
        ProcessorInfo processorInfo = new ProcessorInfo(FakeProcessor.class.getName(), "argument");

        Processor processor = processorInfo.newProcessor();

        assertEquals(FakeProcessor.class, processor.getClass());
        assertEquals("Aucun argument n'est fournit", "argument", ((FakeProcessor)processor).argument);
    }


    public static class FakeProcessor implements Processor {
        final String argument;


        public FakeProcessor() {
            this.argument = "constructeur sans argument";
        }


        public FakeProcessor(String argument) {
            this.argument = argument;
        }


        public void preProceed(Connection connection, String quarantineTableName, File file) {
        }


        public void postProceed(Connection con, String quarantineTableName, File file,
                                ImportFailureException ex) {
        }
    }
}
