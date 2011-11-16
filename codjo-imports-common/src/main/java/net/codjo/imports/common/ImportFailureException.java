/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Classe de base de toutes les exceptions propres a la libraire Imports.
 */
public class ImportFailureException extends Exception {
    private Exception causedByException;
    private java.io.File inputFile;
    private int taskStep = -1;
    private String line;


    public ImportFailureException(String msg) {
        super(msg);
    }


    public ImportFailureException(String msg, Exception cause) {
        super(msg + " : " + cause.getLocalizedMessage());
        setCausedByException(cause);
    }


    public ImportFailureException(Exception cause, File inputFile, int taskStep, String line) {
        super(computeMessage(cause, inputFile, taskStep, line));
        setCausedByException(cause);
        setInputFile(inputFile);
        setTaskStep(taskStep);
    }


    public ImportFailureException(Exception cause, File inpuFile) {
        super("Echec de l'import : " + cause.getLocalizedMessage());
        setCausedByException(cause);
        setInputFile(inpuFile);
    }


    public ImportFailureException(Exception cause) {
        super("Echec de l'import : " + cause.getLocalizedMessage());
        setCausedByException(cause);
    }


    public Exception getCausedByException() {
        return causedByException;
    }


    public java.io.File getInputFile() {
        return inputFile;
    }


    public int getTaskStep() {
        return taskStep;
    }


    public String getLine() {
        return line;
    }


    @Override
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getCausedByException() != null) {
            writer.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(writer);
        }
    }


    @Override
    public void printStackTrace() {
        printStackTrace(new PrintWriter(System.out));
    }


    @Override
    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (getCausedByException() != null) {
            stream.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(stream);
        }
    }


    protected void setCausedByException(Exception causedByException) {
        this.causedByException = causedByException;
    }


    protected void setInputFile(java.io.File inputFile) {
        this.inputFile = inputFile;
    }


    protected void setTaskStep(int taskStep) {
        this.taskStep = taskStep;
    }


    protected void setLine(String line) {
        this.line = line;
    }


    private static String computeMessage(Exception cause, File inputFile, int taskStep, String line) {
        StringBuilder buffer = new StringBuilder("Echec de l'import");
        if (inputFile != null) {
            buffer.append(" du fichier '").append(inputFile.getName()).append("'");
        }
        if (taskStep != -1) {
            buffer.append(", at ").append(taskStep);
        }
        if (line != null) {
            buffer.append(", '").append(line).append("'");
        }
        if (cause != null) {
            buffer.append(", ").append(cause.getLocalizedMessage());
        }
        return buffer.toString();
    }
}
