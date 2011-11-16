package net.codjo.imports.gui.wizard;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.SelectRequest;
/**
 *
 */
public class DefaultImportSelector implements ImportSelector {
    private MadConnectionOperations madConnection;
    private String selectHandlerId;


    public DefaultImportSelector(MadConnectionOperations madConnection, String handlerId) {
        this.madConnection = madConnection;
        selectHandlerId = handlerId;
    }


    public Result selectImportItems(String[] columns) throws RequestException {
        SelectRequest select = new SelectRequest();
        select.setPage(1, 1000);
        select.setId(selectHandlerId);
        select.setAttributes(columns);
        return madConnection.sendRequest(select);
    }
}
