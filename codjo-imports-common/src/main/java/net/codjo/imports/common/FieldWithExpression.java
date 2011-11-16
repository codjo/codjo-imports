/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import net.codjo.expression.Expression;
import net.codjo.expression.ExpressionException;
import net.codjo.expression.FunctionHolder;
import java.sql.Types;
/**
 * Classe permettant de remplir un champs à partir d'une expression avec eventuellement
 * un champ importé à partir du fichier.
 */
public class FieldWithExpression implements Field {
    private Expression expression;
    private Field field = null;
    private String dbDestFieldName;
    private int sourceSqlType;

    public FieldWithExpression(String dbName, char fieldType, Field field,
        String expressionString, FunctionHolder... functionHolder)
            throws ImportExpressionException {
        this(dbName, fieldType, fieldType, field, expressionString, functionHolder);
    }


    public FieldWithExpression(String dbName, char sourceFieldType, char destFieldType,
        Field field, String expressionString, FunctionHolder... functionHolder)
            throws ImportExpressionException {
        dbDestFieldName = dbName;

        int destSqlType = FieldFactory.convertFieldTypeToSqlType(destFieldType);

        if (sourceFieldType != '\u0000') {
            sourceSqlType = FieldFactory.convertFieldTypeToSqlType(sourceFieldType);
        }
        else {
            sourceSqlType = destSqlType;
        }

        try {
            Expression.Variable[] variableArray = createVariableArray();
            expression =
                new Expression(expressionString, variableArray,
                    functionHolder, destSqlType,
                    new String[] {"Valeur_nulle"});
        }
        catch (RuntimeException e) {
            throw new ImportExpressionException("Colonne '" + dbName
                + "' expression invalide : " + expressionString, e);
        }

        if (field != null) {
            this.field = field;
        }
    }

    public Object convertFieldToSQL(String ligneFichier)
            throws ImportFailureException {
        return convertFieldToSQL(ligneFichier, -1);
    }


    public Object convertFieldToSQL(String ligneFichier, int currentLine)
            throws ImportFailureException {
        if (field != null) {
            try {
                Expression.Variable[] variables;
                variables = createVariableArray();
                variables[0].setValue(field.convertFieldToSQL(ligneFichier));
                variables[1].setValue(ligneFichier);
                variables[2].setValue(new Integer(currentLine));
                return expression.compute(variables);
            }
            catch (ExpressionException e) {
                throw new ImportExpressionException(e);
            }
        }

        try {
            Expression.Variable[] variables;
            variables = createVariableArray();
            variables[0].setValue(null);
            variables[1].setValue(ligneFichier);
            variables[2].setValue(new Integer(currentLine));
            return expression.compute(variables);
        }
        catch (ExpressionException e) {
            throw new ImportExpressionException(e);
        }
    }


    private Expression.Variable[] createVariableArray() {
        Expression.Variable[] variables;
        variables = new Expression.Variable[3];
        variables[0] = new Expression.Variable("Valeur", sourceSqlType);
        variables[1] = new Expression.Variable("Ligne", Types.VARCHAR);
        variables[2] = new Expression.Variable("NumLigne", Types.INTEGER);
        return variables;
    }


    public String getDBDestFieldName() {
        return dbDestFieldName;
    }


    public int getSQLType() {
        return expression.getOutputSqlType().intValue();
    }
}
