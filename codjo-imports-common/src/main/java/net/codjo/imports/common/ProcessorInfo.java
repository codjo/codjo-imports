/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
/**
 * Information relative au traitement pre-import.
 *
 * @version $Revision: 1.4 $
 */
public class ProcessorInfo {
    private final String className;
    private final String argument;
    private Processor processor;


    ProcessorInfo(String className)
          throws InvocationTargetException, InstantiationException,
                 IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        this.className = className;
        this.argument = null;
        this.processor = newProcessor();
    }


    /**
     * Constructeur.
     *
     * @param className classe du traitement.
     * @param argument  Argument a fournir au constructeur du processeur.
     */
    public ProcessorInfo(String className, String argument)
          throws InvocationTargetException, InstantiationException,
                 IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        this.className = className;
        this.argument = argument;
        this.processor = newProcessor();
    }


    public Processor newProcessor()
          throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
                 IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName(className);
        if (argument != null) {
            Constructor constructor = clazz.getConstructor(String.class);
            return (Processor)constructor.newInstance(argument);
        }
        else {
            return (Processor)clazz.newInstance();
        }
    }


    public Processor getProcessor() {
        return processor;
    }
}
