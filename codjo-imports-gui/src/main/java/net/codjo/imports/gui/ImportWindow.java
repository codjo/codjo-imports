package net.codjo.imports.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.codjo.gui.toolkit.swing.CheckBoxRenderer;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.i18n.InternationalizableRequestTable;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.mad.gui.structure.StructureCache;
import net.codjo.mad.gui.structure.StructureRenderer;

public class ImportWindow extends JInternalFrame implements InternationalizableContainer {
    static final String QUARANTINES_STRUCTURE = "StructureReader";
    private BorderLayout borderLayout1 = new BorderLayout();
    private BorderLayout borderLayout2 = new BorderLayout();
    private BorderLayout borderLayout3 = new BorderLayout();
    private RequestTable sectionsTable = new RequestTable();
    private RequestToolBar contentsToolBar = new RequestToolBar();
    private StructureRenderer destinationFieldRenderer = new StructureRenderer();
    private JPanel filesPanel = new JPanel();
    private RequestTable filesTable = new RequestTable();
    private RequestToolBar filesToolBar = new RequestToolBar();
    private JScrollPane scrollPane = new JScrollPane();
    private JScrollPane scrollPane1 = new JScrollPane();
    private JPanel sectionsPanel = new JPanel();
    private StructureReader reader;


    public ImportWindow(GuiContext ctxt) throws Exception {
        reader = ((StructureCache)ctxt.getProperty(StructureCache.STRUCTURE_CACHE)).getStructureReader();
        jbInit();

        initSectionsTable();
        initFilesTable();
        initToolBars(ctxt);

        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);
        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(this, "ImportWindow.title");
        translationNotifier.addInternationalizableComponent(filesPanel, "ImportWindow.filesPanel.title");
        translationNotifier.addInternationalizableComponent(sectionsPanel, "ImportWindow.sectionsPanel.title");
        translationNotifier.addInternationalizableComponent(new InternationalizableRequestTable(filesTable.getPreference(),
                                                                                                filesTable));
        translationNotifier.addInternationalizableComponent(new InternationalizableRequestTable(sectionsTable.getPreference(),
                                                                                                sectionsTable));
    }


    private void jbInit() {
        TitledBorder titledBorder1 = new TitledBorder(
              BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134)), "Fichiers");
        TitledBorder titledBorder2 = new TitledBorder(
              BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134)), "Sections");
        this.getContentPane().setLayout(borderLayout3);
        filesPanel.setLayout(borderLayout1);
        this.setClosable(true);
        this.setResizable(true);
        this.setIconifiable(true);
        this.setMaximizable(true);
        this.setTitle("Fichiers / Sections");
        this.setPreferredSize(new Dimension(500, 500));
        sectionsPanel.setLayout(borderLayout2);
        filesPanel.setBorder(titledBorder1);
        filesPanel.setMinimumSize(new Dimension(100, 100));
        filesPanel.setPreferredSize(new Dimension(300, 300));
        sectionsPanel.setBorder(titledBorder2);
        sectionsPanel.setMinimumSize(new Dimension(100, 100));
        sectionsPanel.setPreferredSize(new Dimension(300, 300));
        filesPanel.add(scrollPane, BorderLayout.CENTER);
        filesPanel.add(filesToolBar, BorderLayout.SOUTH);
        sectionsPanel.add(scrollPane1, BorderLayout.CENTER);
        sectionsPanel.add(contentsToolBar, BorderLayout.SOUTH);
        scrollPane1.getViewport().add(sectionsTable, null);
        scrollPane.getViewport().add(filesTable, null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, filesPanel, sectionsPanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(200);

        this.getContentPane().add(splitPane, BorderLayout.CENTER);
    }


    private void initSectionsTable() {
        Preference preference = PreferenceFactory.getPreference("FieldImportWindow");
        sectionsTable.setPreference(preference);
        sectionsTable.getModel().addTableModelListener(new TableListener());
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.setCellRenderer("dbDestinationFieldName", destinationFieldRenderer);
        if (-1 < preference.getColumnIndex("removeLeftZeros")) {
            sectionsTable.setCellRenderer("removeLeftZeros", new CheckBoxRenderer());
        }
    }


    private void initFilesTable() throws RequestException {
        filesTable.setPreference(PreferenceFactory.getPreference("ImportSettingsWindow"));
        filesTable.load();
        filesTable.sizeColumnsToFit(2);
        filesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filesTable.setCellRenderer("destTable", new StructureRenderer(reader.getQuarantineTables()));
    }


    private void initToolBars(GuiContext context) {
        LocalGuiContext localCtxt = new LocalGuiContext(context);
        localCtxt.putProperty(QUARANTINES_STRUCTURE, reader.getQuarantineTables());
        initToolbar(filesToolBar, localCtxt, filesTable);
        initToolbar(contentsToolBar, localCtxt, sectionsTable);

        contentsToolBar.setFather(filesTable.getDataSource(), new JoinKeys("importSettingsId"));
    }


    static void initToolbar(RequestToolBar toolBar, GuiContext context, RequestTable table) {
        toolBar.setHasExcelButton(true);
        toolBar.init(context, table);
    }


    private class TableListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (filesTable.getSelectedRow() == -1) {
                return;
            }
            String destTable = filesTable.getFirstSelectedDataRow().getFieldValue("destTable");
            TableStructure tableStructure = reader.getTableBySqlName(destTable);
            destinationFieldRenderer.setStructures(tableStructure.getFieldsBySqlKey());
        }
    }
}
