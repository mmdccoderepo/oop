package dao;

import model.AttendanceLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVAttendanceLogDAO extends CSVBaseDAO implements AttendanceLogDAO {
    private final String filePath = getResourceFilePath("attendance_logs.csv");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public CSVAttendanceLogDAO() {
    }

    @Override
    public List<AttendanceLog> getAll() {
        List<AttendanceLog> logs = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header row if present
            reader.mark(1024);
            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.split(",", -1);
                boolean isHeader = parts.length > 0 && !isNumeric(parts[0]);
                if (!isHeader) {
                    reader.reset();
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                AttendanceLog log = csvToAttendanceLog(line);
                if (log != null) {
                    logs.add(log);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logs;
    }

    @Override
    public List<AttendanceLog> getByEmployeeId(int employeeId) {
        List<AttendanceLog> allLogs = getAll();
        List<AttendanceLog> employeeLogs = new ArrayList<>();

        for (AttendanceLog log : allLogs) {
            if (log.getEmployeeId() == employeeId) {
                employeeLogs.add(log);
            }
        }

        return employeeLogs;
    }

    @Override
    public int getTotalHoursWorked(int employeeId) {
        List<AttendanceLog> employeeLogs = getByEmployeeId(employeeId);
        int totalHours = 0;

        for (AttendanceLog log : employeeLogs) {
            totalHours += log.getHoursWorked();
        }

        return totalHours;
    }

    private AttendanceLog csvToAttendanceLog(String line) {
        String[] parts = line.split(",", -1);

        try {
            int employeeId = Integer.parseInt(parts[0].trim());
            LocalDate date = LocalDate.parse(parts[1].trim(), DATE_FORMAT);
            LocalTime timeIn = LocalTime.parse(parts[2].trim(), TIME_FORMAT);
            LocalTime timeOut = LocalTime.parse(parts[3].trim(), TIME_FORMAT);

            return new AttendanceLog(employeeId, date, timeIn, timeOut);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
