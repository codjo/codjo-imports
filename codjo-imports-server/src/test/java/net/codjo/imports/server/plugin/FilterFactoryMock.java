package net.codjo.imports.server.plugin;
import net.codjo.imports.common.FilterFactory;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.ImportFilter;
import java.sql.Connection;
/**
 *
 */
class FilterFactoryMock implements FilterFactory {
    private ImportFilter importFilterMock;


    FilterFactoryMock(ImportFilter importFilter) {
        importFilterMock = importFilter;
    }


    public ImportFilter createFilter(Connection connection, String fileType, String sourceSystem)
          throws ImportFailureException {
        return importFilterMock;
    }
}
