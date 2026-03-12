package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class CSVUserAccountDAO extends CSVBaseDAO implements UserAccountDAO {

    private final String filePath = getResourceFilePath("user_account.csv");

    @Override
    public Map<String, String> getAll() {
        return loadAccounts();
    }
     @Override
    public boolean create(String username, String password) {
        File file = new File(filePath);
        boolean isNewFile = !file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            // If the file is new, write header first
            if (isNewFile) {
                writer.write("Username,Password");
                writer.newLine();
            }

            writer.write(username + "," + password);
            writer.newLine();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> loadAccounts() {
        Map<String, String> accounts = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // skip header
                    if (line.startsWith("Username")) continue;
                }
                if (line.trim().isEmpty()) continue;

                int comma = line.indexOf(',');
                if (comma < 0) continue;
                String username = line.substring(0, comma).trim();
                String password = line.substring(comma + 1).trim();
                accounts.put(username, password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}

