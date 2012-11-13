/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.gui;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.imports.gui.ImportSettingsDetailWindow;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
/**
 * Paramétrage d'un import en incluant le filtre.
 */
public class ImportSettingsFilterDetailWindow extends ImportSettingsDetailWindow {

    private JLabel filterExpressionLabel;


    public ImportSettingsFilterDetailWindow(DetailDataSource dataSource)
          throws RequestException {
        super(dataSource);
    }


    @Override
    public void addInternationalizableComponents(TranslationNotifier notifier) {
        super.addInternationalizableComponents(notifier);
        this.translationNotifier
              .addInternationalizableComponent(filterExpressionLabel,
                                               "ImportSettingsFilterDetailWindow.filterExpressionLabel");
    }


    @Override
    protected void buildFields() {
        super.buildFields();
        filterExpressionLabel = new JLabel();
        addField("filterExpression", filterExpressionLabel, new JTextArea(10, 20));
    }
}
