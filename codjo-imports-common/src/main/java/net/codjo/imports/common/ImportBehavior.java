/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Comportement d'import.
 *
 * <p> Cette classe definit le comportement d'import d'une opération. </p>
 *
 * @author $Author: crego $
 * @version $Revision: 1.13 $
 */
public class ImportBehavior {
    private String commentry;
    private int currentOfTask = 0;
    private String destTable;
    private List<Field> fieldImportList = new ArrayList<Field>();
    private String fileType;
    private ImportFilter filter;
    private boolean fixedLength;
    private boolean fixedReadLine = true;
    private boolean headerLine;
    private int lengthOfTask = 0;
    private int recordLength;
    private Processor processor;
    private String sourceSystem;
    private String currentLine;


    /**
     * Constructeur pour les tests.
     *
     * <p> Les arguments correspondent aux champs de la table PM_IMPORT_SETTINGS </p>
     *
     * @param fileType     -
     * @param sourceSystem -
     * @param recordLength -
     * @param commentry    -
     * @param fixedLength  -
     * @param headerLine   -
     * @param destTable    Description of Parameter
     */
    public ImportBehavior(String fileType, String sourceSystem, int recordLength,
                          String commentry, boolean fixedLength, boolean headerLine, String destTable) {
        this.destTable = destTable;
        this.fileType = fileType;
        this.sourceSystem = sourceSystem;
        this.recordLength = recordLength;
        this.commentry = commentry;
        this.fixedLength = fixedLength;
        this.headerLine = headerLine;
    }


    /**
     * Incremente le compteur courant de la tache.
     */
    private void incrementCurrentOfTask() {
        currentOfTask++;
    }


    /**
     * Determine la longueur de l'opération à effectuer (ex : Nb de lignes du fichier à importer). La methode
     * met a jour l'attribut lengthOfTask.
     *
     * @throws IOException erreur de lecture
     */
    public void determineLengthOfTask(File inputFile) throws IOException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(inputFile));
        while (lnr.readLine() != null) {
            ; // je determine la derniere ligne !
        }
        setLengthOfTask(lnr.getLineNumber());
    }


    /**
     * Retourne le commentaire.
     *
     * @return le commentaire.
     */
    public String getCommentry() {
        return commentry;
    }


    /**
     * Retourne le compteur courant de la tache.
     *
     * @return The CurrentOfTask value
     */
    public int getCurrentOfTask() {
        return currentOfTask;
    }


    public String getDestTable() {
        return destTable;
    }


    /**
     * Gets the fieldImportList attribute of the ImportBehavior object
     *
     * @return The fieldImportList value
     */
    public List<Field> getFieldImportList() {
        return fieldImportList;
    }


    /**
     * Gets the FileType attribute of the ImportBehavior object
     *
     * @return The FileType value
     */
    public String getFileType() {
        return fileType;
    }


    /**
     * Retourne La taille totale de la tache. Cette valeur est initialise par la methode
     * determineLengthOfTask.
     *
     * @return The LengthOfTask value
     *
     * @see #determineLengthOfTask
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }


    /**
     * Retourne la taille d'une ligne importe.
     *
     * @return La taille.
     */
    public int getRecordLength() {
        return recordLength;
    }


    /**
     * Retourne l'attribut de localisation du fichier
     *
     * @return la localisation
     */
    public boolean isFixedLength() {
        return fixedLength;
    }


    /**
     * Retourne l'attribut fixedReadLine de ImportBehavior
     *
     * @return La valeur de fixedReadLine
     *
     * @see #setFixedReadLine(boolean)
     */
    public boolean isFixedReadLine() {
        return fixedReadLine;
    }


    /**
     * Gets the HeaderLine attribute of the ImportBehavior object
     *
     * @return The HeaderLine value
     */
    public boolean isHeaderLine() {
        return headerLine;
    }


    /**
     * Lance l'import.
     *
     * <p> Lecture du fichier d'entree ligne par ligne. </p>
     *
     * @throws ImportFailureException   probleme lors de l'import
     * @throws InterruptedException     interruption par l'utilisateur
     * @throws IllegalArgumentException Con == null
     */
    public void proceed(File inputFile, Connection con) throws ImportFailureException, InterruptedException {
        if (con == null) {
            throw new IllegalArgumentException();
        }

        try {
            doImport(con, inputFile, getDestTable());
        }
        catch (Exception ex) {
            ImportFailureException failureException = new ImportFailureException(ex, inputFile,
                                                                                 getCurrentOfTask(),
                                                                                 currentLine);
            if (processor != null) {
                try {
                    processor.postProceed(con, getDestTable(), inputFile, failureException);
                }
                catch (SQLException ex1) {
                    throw new ImportFailureException(ex1, inputFile, getCurrentOfTask(), currentLine);
                }
            }
            throw failureException;
        }
    }


    /**
     * Positionne un filtre d'import.
     *
     * @param newFilter La nouvelle valeur de filter
     */
    public void setFilter(ImportFilter newFilter) {
        filter = newFilter;
    }


    /**
     * Positionne un processor executé avant et/ou après la lecture du fichier.
     *
     * @param processor Le nouveau processor
     */
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }


    /**
     * Positionne l'attribut fixedReadLine de ImportBehavior qui indique si la lecture d'une ligne (pour le
     * type fichier a longueur fixe) doit etre faites de maniere fixe ou en tenant compte du retour chariot.
     *
     * @param newFixedReadLine La nouvelle valeur de fixedReadLine
     */
    public void setFixedReadLine(boolean newFixedReadLine) {
        fixedReadLine = newFixedReadLine;
    }


    /**
     * Ajoute un FieldImport à ImportBehavior.
     *
     * @param field Le Field à ajouter
     *
     * @throws IllegalArgumentException Ajout d'un Field null
     */
    public void addField(Field field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }

        fieldImportList.add(field);
    }


    /**
     * Lance l'import du fichier <code>inputFile</code>.
     *
     * @param con        Description of the Parameter
     * @param inputFile  Description of the Parameter
     * @param dbDestName Description of the Parameter
     *
     * @throws IOException            Erreur de lecture du fichier
     * @throws SQLException           Erreur d'acces BD
     * @throws InterruptedException   Interruption utilisateur
     * @throws ImportFailureException Probleme lors de l'evaluation d'une expression
     */
    protected void doImport(final Connection con, final File inputFile, final String dbDestName)
          throws IOException, SQLException, InterruptedException, ImportFailureException {
        testInputFile(inputFile);

        if (processor != null) {
            processor.preProceed(con, dbDestName, inputFile);
        }

        BufferedReader reader;
        if (isFixedLength() && isFixedReadLine()) {
            reader = new FixedReader(new FileReader(inputFile), getRecordLength());
        }
        else {
            reader = new BufferedReader(new FileReader(inputFile));
        }

        currentLine = null;
        setCurrentOfTask(0);
        PreparedStatement insertQuery = null;
        try {
            insertQuery = QueryHelper.buildInsertStatement(dbDestName, getDbFieldNameList(), con);

            if (headerLine) {
                reader.readLine();
                incrementCurrentOfTask();
            }
            currentLine = reader.readLine();
            while (currentLine != null) {
                if (filter != null && filter.filteredLine(currentLine)) {
                    currentLine = reader.readLine();
                    incrementCurrentOfTask();
                    continue;
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interruption utilisateur");
                }
                incrementCurrentOfTask();
                fillPreparedStatement(insertQuery, currentLine);
                executeInsert(currentLine, insertQuery);

                insertQuery.clearParameters();
                currentLine = reader.readLine();
            }
        }
        finally {
            reader.close();
            if (insertQuery != null) {
                insertQuery.close();
            }
        }
        if (processor != null) {
            processor.postProceed(con, getDestTable(), inputFile, null);
        }
    }


    /**
     * Positionne la taille totale de la tache.
     *
     * @param newCurrentOfTask The new CurrentOfTask value
     */
    protected void setCurrentOfTask(int newCurrentOfTask) {
        currentOfTask = newCurrentOfTask;
    }


    /**
     * Positionne la taille totale de la tache.
     *
     * @param newLengthOfTask The new LengthOfTask value
     */
    protected void setLengthOfTask(int newLengthOfTask) {
        lengthOfTask = newLengthOfTask;
    }


    void fillPreparedStatement(PreparedStatement st, String line)
          throws ImportFailureException, SQLException {
        Iterator iter = fieldImportList.iterator();

        for (int pos = 1; iter.hasNext(); pos++) {
            Field fi = (Field)iter.next();
            st.setObject(pos, fi.convertFieldToSQL(line), fi.getSQLType());
        }
    }


    /**
     * Retourne la liste des noms de colonne.
     *
     * @return Liste de String.
     */
    private List<String> getDbFieldNameList() {
        List<String> dbFieldNameList = new ArrayList<String>();
        Iterator iter = fieldImportList.iterator();
        while (iter.hasNext()) {
            Field fi = (Field)iter.next();
            dbFieldNameList.add(fi.getDBDestFieldName());
        }
        return dbFieldNameList;
    }


    /**
     * Test la validite du fichier (ecriture, etc.).
     *
     * @param inputFile Le fichier à tester
     *
     * @throws IOException Fichier non valide
     */
    private void testInputFile(File inputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new IOException("Fichier introuvable : " + inputFile.getAbsoluteFile());
        }

        if (!inputFile.isFile()) {
            throw new IOException("Ce n'est pas un fichier : " + inputFile.getAbsoluteFile());
        }

        if (!inputFile.canRead()) {
            throw new IOException("Fichier illisible : " + inputFile.getAbsoluteFile());
        }
    }


    void executeInsert(String line, PreparedStatement insertQuery) throws SQLException {
        int insertedRow = insertQuery.executeUpdate();
        if (insertedRow == 0) {
            if (insertQuery.getWarnings() != null) {
                throw insertQuery.getWarnings();
            }
            else {
                throw new SQLException("Impossible d'inserer en base la ligne: " + line);
            }
        }
    }


    public String getSourceSystem() {
        return sourceSystem;
    }
}
