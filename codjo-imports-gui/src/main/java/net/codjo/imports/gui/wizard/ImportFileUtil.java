/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.util.file.FileUtil;
import net.codjo.util.file.FileUtil.FileCopyException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 */
public class ImportFileUtil {
    private ImportFileUtil() {
    }


    public static File copyFileToInbox(File src, String importType, String inbox)
          throws IOException {
        File dest = determineDestinationFile(src, importType, inbox);
        if (!checkFileName(src.getName())) {
            throw new FileCopyException("Caractères non autorisés dans le nom du fichier: " + src);
        }
        FileUtil.copyFile(src, dest);
        return dest;
    }


    public static File determineDestinationFile(File src, String importType, String inbox) {
        return new File(inbox, determineDestinationFileName(src, importType));
    }


    public static String determineDestinationFileName(File sourceFile, String importType) {
        String userName = System.getProperty("user.name");
        return userName + "_" + importType + "_" + sourceFile.getName();
    }


    static boolean checkFileName(String str) {
        Pattern pattern = Pattern.compile("[-/a-zA-Z0-9_\\.:\\\\]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
