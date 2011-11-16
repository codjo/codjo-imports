/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import java.util.StringTokenizer;
/**
 * Classe permettant d'importer un booleen.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.6 $
 */
class BooleanTranslator implements Translator {
    private int destSqlType;

    BooleanTranslator() {
        this.destSqlType = java.sql.Types.BIT;
    }


    BooleanTranslator(int destSqlType) {
        this.destSqlType = destSqlType;
    }

    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.BIT.
     */
    public int getSQLType() {
        return destSqlType;
    }


    /**
     * Traduction du champ en objet Boolean.
     * 
     * <p>
     * Conversion du booleen d'entrée (VRAI/FAUX) en booleen de sortie.
     * </p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @exception BadFormatException Description of Exception
     */
    public Object translate(String field) throws BadFormatException {
        if (field == null) {
            return Boolean.FALSE;
        }

        if ("".equalsIgnoreCase(field)) {
            return Boolean.FALSE;
        }

        String yesString = "OUI;O;VRAI;YES;TRUE;1";
        String noString = "NON;N;FAUX;NO;FALSE;0";

        StringTokenizer yesTokenizer = new StringTokenizer(yesString, ";");
        StringTokenizer noTokenizer = new StringTokenizer(noString, ";");

        while (yesTokenizer.hasMoreTokens()) {
            if (yesTokenizer.nextToken().equalsIgnoreCase(field)) {
                return Boolean.TRUE;
            }
        }

        while (noTokenizer.hasMoreTokens()) {
            if (noTokenizer.nextToken().equalsIgnoreCase(field)) {
                return Boolean.FALSE;
            }
        }

        throw new BadFormatException(this, field + " n'est pas un booleen");
    }
}
