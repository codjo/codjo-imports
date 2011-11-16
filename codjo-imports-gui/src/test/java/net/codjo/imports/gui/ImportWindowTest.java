package net.codjo.imports.gui;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.security.common.api.UserMock;
import org.junit.Assert;
import org.junit.Test;

public class ImportWindowTest {

    @Test
    public void test_initToolbar() {
        final RequestToolBar toolBar = new RequestToolBar();

        Assert.assertNull(toolBar.getAction("ExportAllPagesAction"));
        ImportWindow.initToolbar(toolBar, createContext(), new RequestTable());
        Assert.assertNotNull(toolBar.getAction("ExportAllPagesAction"));
    }


    private static DefaultGuiContext createContext() {
        final ImportsGuiContext context = new ImportsGuiContext();
        context.setUser(new UserMock());
        return context;
    }
}
