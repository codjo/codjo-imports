/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import net.codjo.imports.common.translator.Translator;
import net.codjo.imports.common.translator.TranslatorFactory;
import java.util.StringTokenizer;
/**
 * Classe de base pour les filtes d'import.
 *
 * <p> Cette classe permet d'extraire d'une String un champs Et de le transformer dans la syntaxe SQL Et
 * contient le champs cible. </p>
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.18 $
 */
public class FieldImport implements Field {
    private String dbDestFieldName;
    private boolean fixedLength = true;
    private int length = 0;
    private int position = 0;
    private boolean removeLeftZeros = false;
    private String separator = "\t";
    private Translator translator;


    /**
     * Constructeur pour des FieldImport non <code>Persistent</code>.
     *
     * @param dbName Nom du champ DB de destination.
     */
    protected FieldImport(String dbName) {
        dbDestFieldName = dbName;
    }


    /**
     * Constructeur pour des FieldImport <code>Persistent</code>.
     *
     * @param dbName           Nom du champ DB de destination.
     * @param fieldType        type de fichier a importer
     * @param decimalSeparator seprateur utilise dans le fichier a importer
     * @param inputDateFormat  format des dates utilise dans le fichier a importer
     */
    protected FieldImport(String dbName, char fieldType, String decimalSeparator,
                          String inputDateFormat) {
        this(dbName, fieldType, fieldType, decimalSeparator, inputDateFormat);
    }


    protected FieldImport(String dbName, char sourceFieldType, char destFieldType,
                          String decimalSeparator, String inputDateFormat) {
        dbDestFieldName = dbName;
        translator =
              TranslatorFactory.newTranslator(sourceFieldType, destFieldType,
                                              decimalSeparator, inputDateFormat);
    }


    /**
     * Extrait le champs de "line" et le convertit en un Object utilisable dans une requete SQL.
     *
     * @param line - Une ligne du fichier a importer
     *
     * @return Le champs convertit.
     *
     * @throws FieldNotFoundException Si la ligne est trop courte.
     * @throws BadFormatException     Si le format est incorrecte.
     */
    public Object convertFieldToSQL(String line)
          throws FieldNotFoundException, BadFormatException {
        return translateField(extractField(line));
    }


    /**
     * Conversion du du champ en objet d'entrée en un Object utilisable dans une requete SQL.
     *
     * @param field Champ à traduire.
     *
     * @return Le champ dans un Object du bon type.
     *
     * @throws BadFormatException Description of Exception
     */
    public Object translateField(String field) throws BadFormatException {
        try {
            return translator.translate(field);
        }
        catch (net.codjo.imports.common.translator.BadFormatException e) {
            throw new BadFormatException(this, e);
        }
    }


    /**
     * Extraction du champs de la ligne texte.
     *
     * @param line Ligne de la table
     *
     * @return Retourne le champs extrait.
     *
     * @throws FieldNotFoundException Si la ligne est trop courte.
     */
    public String extractField(String line) throws FieldNotFoundException {
        String sourceField = null;

        if (fixedLength) {
            try {
                sourceField =
                      line.substring(getPosition() - 1, getPosition() + getLength() - 1);
            }
            catch (RuntimeException ex) {
                throw new FieldNotFoundException(ex.getMessage() + " ["
                                                 + getDBDestFieldName() + "]", ex);
            }
        }
        else {
            String sep = getSeparator();
            StringTokenizer st = new StringTokenizer(line, sep, true);
            boolean previousWasSep = false;
            int idx = 1;
            while ((st.hasMoreTokens()) && (idx <= getPosition() * 2 - 1)) {
                sourceField = st.nextToken();
                if (sep.equals(sourceField)) {
                    if (previousWasSep) {
                        idx++;
                    }
                    else if (idx == 1) {
                        idx++;
                    }
                    previousWasSep = true;
                    sourceField = "";
                }
                else {
                    previousWasSep = false;
                }
                idx++;
            }
            if (sep != null && sep.equals(sourceField)) {
                sourceField = "";
            }
            if (idx < (getPosition() * 2 - 1)) {
                throw new FieldNotFoundException("Pas assez de champ dans le fichier ["
                                                 + getDBDestFieldName() + "] idx=" + idx + " getPosition "
                                                 + getPosition());
            }
        }

        if (sourceField == null) {
            throw new FieldNotFoundException("Impossible de trouver le champs source");
        }
        StringBuilder field = new StringBuilder(sourceField.trim());

        if (getRemoveLeftZeros()) {
            while (field.length() > 1 && field.charAt(0) == '0') {
                field.deleteCharAt(0);
            }
        }

        return field.toString();
    }


    public String getDBDestFieldName() {
        return dbDestFieldName;
    }


    public boolean getFixedLength() {
        return fixedLength;
    }


    public int getLength() {
        return length;
    }


    public int getPosition() {
        return position;
    }


    public boolean getRemoveLeftZeros() {
        return removeLeftZeros;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.BIT.
     */
    public int getSQLType() {
        return translator.getSQLType();
    }


    public String getSeparator() {
        return separator;
    }


    public void setFixedLength(boolean fixedLength) {
        this.fixedLength = fixedLength;
    }


    public void setLength(int length) {
        this.length = length;
    }


    public void setPosition(int position) {
        this.position = position;
    }


    public void setRemoveLeftZeros(boolean removeLeftZeros) {
        this.removeLeftZeros = removeLeftZeros;
    }


    public void setSeparator(String separator) {
        this.separator = separator;
    }


    public String toString() {
        return "(" + dbDestFieldName + "," + position + ")";
    }
}
