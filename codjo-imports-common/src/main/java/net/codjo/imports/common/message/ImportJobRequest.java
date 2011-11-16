/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.message;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.message.JobRequestWrapper;
import java.io.File;
/**
 *
 */
public class ImportJobRequest extends JobRequestWrapper {
    public static final String IMPORT_JOB_TYPE = "import";
    public static final String FILE_NAME = "import.fileName";
    public static final String FILE_TYPE = "import.fileType";
    public static final String SOURCE_FOLDER = "import.source.folder";


    public ImportJobRequest(JobRequest request) {
        super(IMPORT_JOB_TYPE, request);
    }


    public ImportJobRequest() {
        this(new JobRequest());
    }


    public void setFile(File file) {
        setArgument(FILE_NAME, file.getName());
        setArgument(SOURCE_FOLDER, toNormalizedPath(file));
    }


    public File getFile() {
        return new File(getFolder(), getArgument(FILE_NAME));
    }


    private File getFolder() {
        return new File(getArgument(SOURCE_FOLDER));
    }


    public String getFileType() {
        return getArgument(FILE_TYPE);
    }


    private String toNormalizedPath(File file) {
        String directory = file.getParent();

        if(!directory.endsWith(File.separator)) {
            directory = directory.concat(File.separator);
        }

        return directory.replace("\\", "/");
    }
}
