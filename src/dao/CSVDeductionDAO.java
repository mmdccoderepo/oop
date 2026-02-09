package dao;

import model.Deduction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDeductionDAO implements DeductionDAO {
    private final String filePath = getResourceFilePath("deductions.csv");

    public CSVDeductionDAO() {
    }

    public List<Deduction> getAll() {
        List<Deduction> deductions = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return deductions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header row if present
            reader.mark(1024);
            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.split(",", -1);
                boolean isHeader = parts.length > 0 && !isNumeric(parts[1]);
                if (!isHeader) {
                    reader.reset();
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                Deduction deduction = csvToDeduction(line);
                if (deduction != null) {
                    deductions.add(deduction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return deductions;
    }

    private Deduction csvToDeduction(String line) {
        String[] parts = line.split(",", -1); // -1 to keep empty trailing fields
        try {
            String name = parts[0];
            String salaryMin = parts[1];
            String salaryMax = parts[2];
            String employeeShare = parts[3];
            String employerShare = parts[4];
            return new Deduction(
                    name,
                    Double.parseDouble(salaryMin),
                    Double.parseDouble(salaryMax),
                    Double.parseDouble(employeeShare),
                    Double.parseDouble(employerShare)
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getResourceFilePath(String fileName) {
        String projectRoot = System.getProperty("user.dir");
        // Fix: resources directory name
        return projectRoot + File.separator + "src" + File.separator + "resource" + File.separator + fileName;
    }

    private boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
