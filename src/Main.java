import dao.*;
import ui.EmployeeManagementWindow;

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
            TaxDAO taxDAO = new CSVTaxDAO();
            AttendanceLogDAO attendanceLogDAO = new CSVAttendanceLogDAO();
            LeaveDAO leaveDAO = new CSVLeaveDAO();

            EmployeeManagementWindow frame = new EmployeeManagementWindow(employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO);
            frame.setVisible(true);
        });
    }
}
