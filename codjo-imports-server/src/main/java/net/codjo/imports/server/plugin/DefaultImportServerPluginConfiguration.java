package net.codjo.imports.server.plugin;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.codjo.expression.FunctionHolder;
import net.codjo.imports.common.FilterFactory;
import net.codjo.imports.common.Processor;
/**
 *
 */
class DefaultImportServerPluginConfiguration implements ImportServerPluginConfiguration {
    private boolean truncateFileName = true;
    private FilterFactory filterFactory;
    private Map<String, Boolean> fixedReadLineFor = new HashMap<String, Boolean>();
    private Map<String, Processor> processorFor = new HashMap<String, Processor>();

    private List<FunctionHolder> functionHolderList = new LinkedList<FunctionHolder>();


    public void setFilterFactory(FilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }


    public FilterFactory getFilterFactory() {
        return filterFactory;
    }


    public void addFunctionHolder(FunctionHolder functionHolder) {
        functionHolderList.add(functionHolder);
    }


    public List<FunctionHolder> getFunctionHolders() {
        return functionHolderList;
    }


    public void setTruncateFileName(boolean isTruncateFileName) {
        truncateFileName = isTruncateFileName;
    }


    public boolean isTruncateFileName() {
        return truncateFileName;
    }


    public boolean isFixedReadLineFor(String sourceSystem) {
        return !fixedReadLineFor.containsKey(sourceSystem) || fixedReadLineFor.get(sourceSystem);
    }


    public void setFixedReadLineFor(String sourceSystem, boolean isFixedReadLine) {
        fixedReadLineFor.put(sourceSystem, (isFixedReadLine ? Boolean.TRUE : Boolean.FALSE));
    }


    public void setPreProcessorFor(String sourceSystem, Processor processor) {
        processorFor.put(sourceSystem, processor);
    }


    public void setProcessorFor(String sourceSystem, Processor processor) {
        processorFor.put(sourceSystem, processor);
    }


    public Processor getProcessorFor(String sourceSystem) {
        return processorFor.get(sourceSystem);
    }
}
