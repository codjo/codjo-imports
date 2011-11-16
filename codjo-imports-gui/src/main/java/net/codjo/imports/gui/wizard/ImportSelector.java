package net.codjo.imports.gui.wizard;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
/**
 * Selectionne la liste des imports affichés dans le wizard d'import.
 *
 * @see ImportSelectionStep
 * @see net.codjo.imports.gui.plugin.ImportGuiPlugin.ImportGuiPluginConfiguration#setWizardImportSelector(ImportSelector)
 */
public interface ImportSelector {
    Result selectImportItems(String[] columns) throws RequestException;
}
