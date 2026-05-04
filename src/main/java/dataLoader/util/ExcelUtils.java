package dataLoader.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public class ExcelUtils {

    private static final DataFormatter formatter = new DataFormatter();

    public static String getString(Row row, int index) {
        if (row == null || row.getCell(index) == null) return "";

        try {
            return formatter.formatCellValue(row.getCell(index)).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static int getInt(Row row, int index) {
        if (row == null || row.getCell(index) == null) return 0;

        try {
            String valor = formatter.formatCellValue(row.getCell(index));

            if (valor == null || valor.isEmpty()) return 0;

            return (int) Double.parseDouble(valor.replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getLong(Row row, int index) {
        if (row == null || row.getCell(index) == null) return 0L;

        try {
            String valor = formatter.formatCellValue(row.getCell(index));

            if (valor == null || valor.isEmpty()) return 0L;

            return (long) Double.parseDouble(valor.replace(",", "."));
        } catch (Exception e) {
            return 0L;
        }
    }
}
