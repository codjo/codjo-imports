package net.codjo.imports.server.audit;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.server.plugin.StringifierImpl;
/**
 *
 */
public class ImportStringifier extends StringifierImpl {

    public ImportStringifier() {
        super(ImportJobRequest.IMPORT_JOB_TYPE);
    }


    public String toString(JobRequest jobRequest) {
        return new ImportJobRequest(jobRequest).getFile().getName();
    }
}
