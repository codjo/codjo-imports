package net.codjo.imports.gui;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.gui.TranslationNotifier;

public class ImportsGuiContext extends DefaultGuiContext {
    public ImportsGuiContext() {
        MadGuiContext context = new MadGuiContext();

        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(context);
        TranslationNotifier translationNotifier
              = InternationalizationUtil.retrieveTranslationNotifier(context);

        translationManager.addBundle("net.codjo.imports.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.imports.gui.i18n", Language.EN);

        putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, translationManager);
        putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, translationNotifier);
    }
}
