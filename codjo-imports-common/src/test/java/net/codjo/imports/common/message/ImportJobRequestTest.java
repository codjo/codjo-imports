/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.message;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
import net.codjo.workflow.common.message.JobRequestWrapperTestCase;
import java.io.File;
/**
 * Classe de test de {@link ImportJobRequest}.
 */
public class ImportJobRequestTest extends JobRequestWrapperTestCase {
    public void test_createFromJobRequest() throws Exception {
        Arguments arguments = new Arguments();
        arguments.put(ImportJobRequest.SOURCE_FOLDER, "destinationFolder");
        arguments.put(ImportJobRequest.FILE_NAME, "temp.txt");

        ImportJobRequest importRequest = new ImportJobRequest(new JobRequest("", arguments));

        assertEquals("destinationFolder" + File.separator + "temp.txt", importRequest.getFile().getPath());
        assertEquals(arguments, importRequest.toRequest().getArguments());
    }


    public void test_setters() throws Exception {
        Arguments arguments = new Arguments();
        arguments.put(ImportJobRequest.SOURCE_FOLDER, "/destinationFolder/");
        arguments.put(ImportJobRequest.FILE_NAME, "temp.txt");

        ImportJobRequest importRequest = new ImportJobRequest();
        importRequest.setFile(new File("/destinationFolder/temp.txt"));

        JobRequest jobRequest = importRequest.toRequest();
        assertEquals(arguments.encode(), jobRequest.getArguments().encode());
    }


    @Override
    protected String getJobRequestType() {
        return ImportJobRequest.IMPORT_JOB_TYPE;
    }


    @Override
    protected JobRequestWrapper createWrapper(JobRequest jobRequest) {
        return new ImportJobRequest(jobRequest);
    }
}
