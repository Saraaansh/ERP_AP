package edu.univ.erp.util;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CSVExporter {

    /**
     * Export raw rows to CSV.
     * rows = list of String[] where each String[] is a row.
     *
     * Example:
     * rows.add(new String[]{"ID", "Name"});
     * rows.add(new String[]{"1", "Alice"});
     */
    public static boolean export(String filePath, List<String[]> rows) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(rows);
            System.out.println("✅ CSV exported → " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error writing CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Export ANY domain object list automatically using reflection.
     * Works with:
     * Student, Instructor, Course, Section, Enrollment, Grade, User...
     *
     * Fields become CSV columns automatically.
     */
    public static <T> boolean exportObjects(String filePath, List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            System.err.println("❌ No data to export.");
            return false;
        }

        Class<?> clazz = objects.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();

        List<String[]> rows = new ArrayList<>();

        // 1. Build header row
        String[] header = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            header[i] = fields[i].getName();
        }

        rows.add(header);

        // 2. Build data rows
        for (T obj : objects) {
            String[] row = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(obj);
                    row[i] = value != null ? value.toString() : "";
                } catch (IllegalAccessException e) {
                    row[i] = "";
                }
            }

            rows.add(row);
        }

        // 3. Delegate to the basic exporter
        return export(filePath, rows);
    }
}
