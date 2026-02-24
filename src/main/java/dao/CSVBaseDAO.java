package dao;

import java.io.File;

public abstract class CSVBaseDAO {
    protected String getResourceFilePath(String fileName) {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + fileName;
    }

    protected boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
