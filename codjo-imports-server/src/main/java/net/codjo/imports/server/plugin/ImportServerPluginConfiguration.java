package net.codjo.imports.server.plugin;
import java.util.List;
import net.codjo.expression.FunctionHolder;
import net.codjo.imports.common.FilterFactory;
import net.codjo.imports.common.Processor;
/**
 *
 */
public interface ImportServerPluginConfiguration {
    void setFilterFactory(FilterFactory filterFactory);


    FilterFactory getFilterFactory();


    boolean isFixedReadLineFor(String sourceSystem);


    /**
     * Positionne l'attribut qui indique si la lecture d'une ligne (pour le type fichier a longueur fixe, ayant pour
     * systeme source sourceSystem) doit etre faites de maniere fixe ou en tenant compte du retour chariot.
     *
     * @param sourceSystem    Le système source des imports concerné.
     * @param isFixedReadLine La nouvelle valeur de fixedReadLine
     */
    void setFixedReadLineFor(String sourceSystem, boolean isFixedReadLine);


    /**
     * @Deprecated Use <code>setProcessorFor<code>
     */
    @Deprecated
    void setPreProcessorFor(String sourceSystem, Processor processor);


    void setProcessorFor(String sourceSystem, Processor processor);


    Processor getProcessorFor(String sourceSystem);


    void addFunctionHolder(FunctionHolder functionHolder);


    List<FunctionHolder> getFunctionHolders();


    void setTruncateFileName(boolean isTruncateFileName);


    boolean isTruncateFileName();
}
