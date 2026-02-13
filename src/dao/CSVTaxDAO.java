package dao;

import model.TaxBracket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVTaxDAO extends CSVBaseDAO implements TaxDAO {
    private final String filePath = getResourceFilePath("tax_brackets.csv");

    public CSVTaxDAO() {
    }

    public List<TaxBracket> getAll() {
        List<TaxBracket> taxs = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return taxs;
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
                TaxBracket taxBracket = csvToTax(line);
                if (taxBracket != null) {
                    taxs.add(taxBracket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taxs;
    }

    private TaxBracket csvToTax(String line) {
        String[] parts = line.split(",", -1); // -1 to keep empty trailing fields
        try {
            String salaryMin = parts[0];
            String salaryMax = parts[1];
            String baseAmount = parts[2];
            String rate = parts[3];
            return new TaxBracket(
                    Double.parseDouble(salaryMin),
                    Double.parseDouble(salaryMax),
                    Double.parseDouble(baseAmount),
                    Double.parseDouble(rate)
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }


}
