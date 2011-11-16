/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.gui;
import net.codjo.imports.gui.ImportSettingsDetailWindow;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import javax.swing.JTextArea;
/**
 * Paramétrage d'un import en incluant le filtre.
 */
public class ImportSettingsFilterDetailWindow extends ImportSettingsDetailWindow {
    public ImportSettingsFilterDetailWindow(DetailDataSource dataSource)
          throws RequestException {
        super(dataSource);
    }


    @Override
    protected void buildFields() {
        super.buildFields();
        addField("filterExpression", "Expression de filtre", new JTextArea(10, 20));
    }
}
