/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.batch.plugin;
import net.codjo.imports.common.message.ImportJobRequest;
import net.codjo.plugin.batch.BatchCore;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.workflow.common.batch.AbstractWorkflowBatchPlugin;
import net.codjo.workflow.common.message.JobRequestWrapper;
import java.io.File;
/**
 * Plugin import pour les batchs.
 */
public final class ImportBatchPlugin extends AbstractWorkflowBatchPlugin {

    @Override
    public String getType() {
        return ImportJobRequest.IMPORT_JOB_TYPE;
    }


    @Override
    protected JobRequestWrapper createRequest(CommandLineArguments arguments) {
        ImportJobRequest importRequest = new ImportJobRequest();
        File file = arguments.getFileArgument(BatchCore.BATCH_ARGUMENT);
        importRequest.setFile(file);

        return importRequest;
    }
}
