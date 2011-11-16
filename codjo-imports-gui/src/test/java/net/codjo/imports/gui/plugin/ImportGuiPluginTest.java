/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.plugin;
import net.codjo.imports.gui.wizard.ImportSelectorMock;
import net.codjo.mad.client.plugin.MadConnectionPluginMock;
import net.codjo.mad.gui.base.GuiConfigurationMock;
import net.codjo.mad.gui.plugin.MadGuiPlugin;
import net.codjo.mad.gui.plugin.MadGuiPluginMock;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.workflow.gui.plugin.WorkflowGuiPlugin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class ImportGuiPluginTest {
    private static final String PREFERENCE_CONFIG =
          "<?xml version='1.0' encoding='ISO-8859-1'?>                                             "
          + "<preferenceList xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'                 "
          + "                xsi:noNamespaceSchemaLocation='preference.xsd'>                       "
          + "    <preference id='CountryList'                                                      "
          + "                detailWindowClassName='" + ImportGuiPluginTest.class.getName() + "'>  "
          + "        <selectAll>selectAllCountry</selectAll>                                       "
          + "        <selectByPk>selectCountryById</selectByPk>                                    "
          + "        <update>updateCountry</update>                                                "
          + "        <delete>deleteCountry</delete>                                                "
          + "        <insert>newCountry</insert>                                                   "
          + "        <requetor>allCountry</requetor>                                               "
          + "        <column fieldName='countryCode' label='Code' preferredSize='20'/>             "
          + "        <column fieldName='countryName' label='Libellé' preferredSize='450'/>         "
          + "    </preference>                                                                     "
          + "</preferenceList>";
    private static final String PREFERENCE_CONFIG_OVERRIDEN =
          "<?xml version='1.0' encoding='ISO-8859-1'?>                                             "
          + "<preferenceList xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'                 "
          + "                xsi:noNamespaceSchemaLocation='preference.xsd'>                       "
          + "    <preference id='CountryList'                                                      "
          + "                detailWindowClassName='" + ImportGuiPluginTest.class.getName() + "'>  "
          + "        <selectAll>selectAllCountry</selectAll>                                       "
          + "        <selectByPk>selectCountryById</selectByPk>                                    "
          + "        <update>updateCountry</update>                                                "
          + "        <delete>deleteCountry</delete>                                                "
          + "        <insert>newCountry</insert>                                                   "
          + "        <requetor>allCountry</requetor>                                               "
          + "        <column fieldName='countryCode' label='Code' preferredSize='20'/>             "
          + "        <column fieldName='countryName' label='Libellé' preferredSize='450'/>         "
          + "    </preference>                                                                     "
          + "    <preference detailWindowClassName='net.codjo.imports.gui.FieldImportDetailWindow'   "
          + "                id='FieldImportWindow'>                                               "
          + "        <selectByPk>selectFieldImportSettingsById</selectByPk>                        "
          + "        <selectAll>selectFieldImportSettingsByFile</selectAll>                        "
          + "        <insert>newFieldImportSettings</insert>                                       "
          + "        <update>updateFieldImportSettings</update>                                    "
          + "        <delete>deleteFieldImportSettings</delete>                                    "
          + "        <column fieldName='position' label='Position' maxSize='100' minSize='50'/>    "
          + "    </preference>                                                                     "
          + "    <preference detailWindowClassName='net.codjo.imports.gui.ImportSettingsDetailWindow'"
          + "                id='ImportSettingsWindow'>                                            "
          + "        <selectByPk>selectImportSettingsById</selectByPk>                             "
          + "        <selectAll>selectAllImportSettings</selectAll>                                "
          + "        <insert>newImportSettings</insert>                                            "
          + "        <update>updateImportSettings</update>                                         "
          + "        <delete>deleteImportSettings</delete>                                         "
          + "        <column fieldName='destTable' label='Table de destination'/>                  "
          + "    </preference>                                                                     "
          + "</preferenceList>";
    private ImportGuiPlugin plugin;


    @Test
    public void test_loadImportPreference() throws Exception {
        ImportGuiPlugin importGuiPlugin = createImportPlugin(createMadGuiPlugin(PREFERENCE_CONFIG));

        importGuiPlugin.loadImportPreferences();
        Preference preference = PreferenceFactory.getPreference("ImportSettingsWindow");
        assertNotNull(preference);
        assertEquals(3, preference.getColumns().size());
        preference = PreferenceFactory.getPreference("FieldImportWindow");
        assertNotNull(preference);
        assertEquals(2, preference.getColumns().size());

        preference = PreferenceFactory.getPreference("CountryList");
        assertNotNull(preference);
        assertEquals(2, preference.getColumns().size());
    }


    @Test
    public void test_loadImportPreferenceOverriden() throws Exception {
        ImportGuiPlugin importGuiPlugin = createImportPlugin(createMadGuiPlugin(PREFERENCE_CONFIG_OVERRIDEN));

        importGuiPlugin.loadImportPreferences();
        Preference preference = PreferenceFactory.getPreference("ImportSettingsWindow");
        assertNotNull(preference);
        assertEquals(1, preference.getColumns().size());
        preference = PreferenceFactory.getPreference("FieldImportWindow");
        assertNotNull(preference);
        assertEquals(1, preference.getColumns().size());

        preference = PreferenceFactory.getPreference("CountryList");
        assertNotNull(preference);
        assertEquals(2, preference.getColumns().size());
    }


    @Test
    public void test_getConfiguration() throws Exception {
        assertNotNull(plugin.getConfiguration());
    }


    @Test
    public void test_getConfiguration_setWizardImportSelector() throws Exception {
        ImportGuiPlugin.ImportGuiPluginConfiguration configuration = plugin.getConfiguration();
        assertNotNull(configuration.getWizardImportSelector());

        ImportSelectorMock selector = new ImportSelectorMock();
        configuration.setWizardImportSelector(selector);
        assertEquals(selector, configuration.getWizardImportSelector());
    }


    @Before
    public void setUp() {
        plugin = createImportPlugin(new MadGuiPluginMock());
    }


    private static ImportGuiPlugin createImportPlugin(MadGuiPlugin madGuiPlugin) {
        return new ImportGuiPlugin(madGuiPlugin, new MadConnectionPluginMock(), new WorkflowGuiPlugin());
    }


    private static MadGuiPlugin createMadGuiPlugin(String preferenceConfig) throws Exception {
        final MadGuiPlugin madGuiPlugin = new MadGuiPluginMock(preferenceConfig);
        madGuiPlugin.initGui(new GuiConfigurationMock());
        return madGuiPlugin;
    }
}
