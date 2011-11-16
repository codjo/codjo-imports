package net.codjo.imports.gui.wizard;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.test.common.LogString;
import java.util.Arrays;
/**
 * Classe mock de {@link ImportSelector}.
 */
public class ImportSelectorMock implements ImportSelector {
    private final LogString log;
    private Result resultMock = new Result();


    public ImportSelectorMock() {
        this(new LogString());
    }


    public ImportSelectorMock(LogString log) {
        this.log = log;
    }


    public Result selectImportItems(String[] columns) throws RequestException {
        log.call("selectImportItems", Arrays.asList(columns));
        return resultMock;
    }


    public void mockSelectImportItems(Result result) {
        this.resultMock = result;
    }
}
