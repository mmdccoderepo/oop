package dao;

import model.Allowance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVAllowanceDAO extends CSVBaseDAO implements AllowanceDAO {
    private final String filePath = getResourceFilePath("allowances.csv");

    public CSVAllowanceDAO() {
    }

    public List<Allowance> getAll() {
        List<Allowance> allowances = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return allowances;
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
                Allowance allowance = csvToAllowance(line);
                if (allowance != null) {
                    allowances.add(allowance);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allowances;
    }

    private Allowance csvToAllowance(String line) {
        String[] parts = line.split(",", -1); // -1 to keep empty trailing fields

        try {
            String positionLevel = parts[0];
            String name = parts[1];
            String amount = parts[2];

            return new Allowance(positionLevel, name, Double.parseDouble(amount));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
