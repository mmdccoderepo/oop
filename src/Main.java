import dao.*;
import ui.EmployeeManagementFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            EmployeeDAO employeeDAO = new CSVEmployeeDAO();
            AllowanceDAO allowanceDAO = new CSVAllowanceDAO();
            DeductionDAO deductionDAO = new CSVDeductionDAO();
            AttendanceLogDAO attendanceLogDAO = new CSVAttendanceLogDAO();

            EmployeeManagementFrame frame = new EmployeeManagementFrame(employeeDAO, allowanceDAO, deductionDAO, attendanceLogDAO);
            frame.setVisible(true);
        });
    }
}
