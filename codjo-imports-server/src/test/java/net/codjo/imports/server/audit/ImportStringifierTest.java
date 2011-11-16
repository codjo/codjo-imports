package net.codjo.imports.server.audit;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class ImportStringifierTest {
    private ImportStringifier stringifier = new ImportStringifier();


    @Test
    public void test_toString() throws Exception {
        Arguments arguments = new Arguments("import.fileName", "toto.txt");
        arguments.put("import.fileType", "text");
        arguments.put("import.source.folder", "source-folder");

        assertEquals("toto.txt", stringifier.toString(new JobRequest("import", arguments)));
    }
}
