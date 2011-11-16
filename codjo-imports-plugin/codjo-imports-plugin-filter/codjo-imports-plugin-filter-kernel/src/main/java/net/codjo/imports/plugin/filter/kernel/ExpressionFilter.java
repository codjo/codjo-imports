/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import net.codjo.expression.Expression;
import net.codjo.expression.ExpressionException;
import net.codjo.expression.FunctionHolder;
import net.codjo.imports.common.ImportFilter;
import java.sql.Types;
/**
 * Filtre d'import utilisant une expression booléenne permettant d'exclure certaines lignes du fichier avant
 * importation.
 *
 * @see Expression
 */
class ExpressionFilter implements ImportFilter {
    private Expression expression;
    private int lineCount;
    private Expression.Variable[] variables;


    ExpressionFilter(String expressionFilter) {
        try {
            lineCount = 0;
            variables = new Expression.Variable[2];
            variables[0] = new Expression.Variable("Valeur", Types.VARCHAR);
            variables[1] = new Expression.Variable("NumLigne", Types.INTEGER);
            expression =
                  new Expression(expressionFilter, variables,
                                 new FunctionHolder[]{new FilterFunctions()}, Types.BIT,
                                 new String[]{"Valeur_nulle"});
        }
        catch (Throwable ex) {
            throw new FilterException("Erreur à la construction de l'expression.", ex);
        }
    }


    /**
     * Indique si la ligne passée en paramètre doit être filtrée (cad elle n'est pas importée).
     *
     * @param line La ligne à tester
     *
     * @return <code>true</code> si la ligne est filtrée.
     *
     * @throws FilterException s'il y a une erreur dans l'expression.
     */
    public boolean filteredLine(String line) {
        try {
            lineCount++;
            variables[0].setValue(line);
            variables[1].setValue(lineCount);
            return (Boolean)expression.compute(variables);
        }
        catch (ExpressionException e) {
            throw new FilterException("Erreur dans l'expression.", e);
        }
    }
}
