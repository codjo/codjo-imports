package net.codjo.imports.plugin.filter.kernel;
import net.codjo.expression.FunctionHolder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
/**
 *
 */
public class DefaultImportFunctionHolder implements FunctionHolder {
    private final FilterFunctions filterFunctions = new FilterFunctions();


    public String getName() {
        return "utils";
    }


    public List<String> getAllFunctions() {
        return null;
    }


    public BigDecimal round(BigDecimal value, int precision) {
        return value.setScale(Double.valueOf(precision).intValue(), RoundingMode.HALF_UP);
    }


    public Date extractDateFrom(String line, String format, String delimiter,
                                String locale) throws IllegalAccessException, NoSuchFieldException {
        if (line == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, getLocale(locale));

        java.util.Date date = null;

        for (String token : line.split(delimiter)) {
            if (token.contains(" ")) {
                continue;
            }

            try {
                date = dateFormat.parse(token);
            }
            catch (ParseException e) {
                continue;
            }
            break;
        }

        if (date == null) {
            return null;
        }
        else {
            return new Date(date.getTime());
        }
    }


    public String removeChar(String value, String carac) {
        String result = value;

        if (value != null && carac.length() == 1) {
            if (result.startsWith(carac)) {
                result = result.substring(1, result.length());
            }

            if (result.endsWith(carac)) {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }


    public Date stringToDate(String value, String format, String locale)
          throws NoSuchFieldException, IllegalAccessException {
        SimpleDateFormat df =
              (SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT,
                                                                 getLocale(locale));
        if (format != null) {
            df.applyPattern(format);
        }

        Date result;
        try {
            result = new Date(df.parse(value).getTime());
        }
        catch (ParseException e) {
            result = new Date(0);
        }
        return result;
    }


    public BigDecimal stringToDecimal(String value, String format) {
        DecimalFormat df = (DecimalFormat)DecimalFormat.getNumberInstance(Locale.US);
        if (format != null) {
            df.applyPattern(format);
        }

        BigDecimal result;
        try {
            String stringValue = df.parse(value).toString();
            result = new BigDecimal(stringValue);
        }
        catch (ParseException e) {
            result = new BigDecimal(0);
        }

        return result;
    }


    public String extractField(String line, String separator, int index) {
        return filterFunctions.extractField(line, separator, index);
    }


    private Locale getLocale(String locale)
          throws IllegalAccessException, NoSuchFieldException {
        return (Locale)Locale.class.getDeclaredField(locale).get(null);
    }
}
