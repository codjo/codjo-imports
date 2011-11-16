/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ImportBehavior}.
 */
public class ImportBehaviorTest extends TestCase {
    //Portefeuille PIMS (fixe): IMPORT_SETTINGS_ID=14
    ImportBehavior importBehaviorF;

    //Complement strategique (variable): IMPORT_SETTINGS_ID=11
    ImportBehavior importBehaviorV;


    public void test_addFieldImport_NullPointer() {
        //Tests pour fichiers à longueur fixe
        try {
            importBehaviorF.addField(null);
            fail("Le field Import est incorrecte (=null)");
        }
        catch (IllegalArgumentException ef) {
            ;
        }

        //Tests pour fichiers à longueur variable
        try {
            importBehaviorV.addField(null);
            fail("Le field Import est incorrecte (=null)");
        }
        catch (IllegalArgumentException ev) {
            ;
        }
    }


    /**
     * The JUnit setup method
     */
    @Override
    protected void setUp() {
        //Tests pour fichiers à longueur fixe
        importBehaviorF =
              new ImportBehavior("Portefeuille PIMS", "PIMS_SRC", 573,
                                 "Livraison prod faite le 26/01/2001", true, false, null);
        FieldImport fieldA =
              new FieldImport("BOOK_KEEPING_PLAN", FieldType.STRING_FIELD, null, null);
        fieldA.setPosition(1);
        fieldA.setLength(4);
        fieldA.setFixedLength(true);
        fieldA.setSeparator(null);
        importBehaviorF.addField(fieldA);

        FieldImport fieldB =
              new FieldImport("PORTFOLIO_TYPE", FieldType.STRING_FIELD, null, null);
        fieldB.setPosition(6);
        fieldB.setLength(5);
        fieldA.setFixedLength(true);
        fieldA.setSeparator(null);
        importBehaviorF.addField(fieldB);

        //Tests pour fichiers à longueur variable
        importBehaviorV =
              new ImportBehavior("Titres strategiques", "SRC", 0, null, false, true, null);
        FieldImport fieldC =
              new FieldImport("PORTFOLIO", FieldType.STRING_FIELD, null, null);
        fieldC.setPosition(2);
        fieldC.setLength(0);
        fieldC.setFixedLength(false);
        fieldC.setSeparator("\t");
        importBehaviorV.addField(fieldC);

        FieldImport fieldD =
              new FieldImport("SECURITY_CODE", FieldType.STRING_FIELD, null, null);
        fieldD.setPosition(3);
        fieldD.setLength(0);
        fieldD.setFixedLength(false);
        fieldD.setSeparator("\t");
        importBehaviorV.addField(fieldD);
    }
}
