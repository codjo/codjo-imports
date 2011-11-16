/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import net.codjo.expression.FunctionHolder;
import java.util.List;
/**
 * Classe contenant les fonctions disponibles dans les expressions de filtre.
 */
public class FilterFunctions implements FunctionHolder {
    public String getName() {
        return "utils";
    }


    public List getAllFunctions() {
        return null;
    }


    public String extractField(String line, String separator, int indexColumns) {
        int currentIndex = 1;
        String result = null;

        if (line != null) {
            result = line;

            while (currentIndex != indexColumns) {
                result = result.substring(result.indexOf(separator) + separator.length());
                currentIndex++;
            }

            int end = result.indexOf(separator);
            if (end != -1) {
                result = result.substring(0, end);
            }
        }

        return result;
    }
}
