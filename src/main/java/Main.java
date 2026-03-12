import dao.*;
import ui.LoginWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Override Windows L&F menu padding
            UIManager.put("MenuItem.margin", new javax.swing.plaf.InsetsUIResource(2, 2, 2, 2));
            UIManager.put("MenuItem.checkIcon", null);
            UIManager.put("MenuItem.minimumTextOffset", 0);
            UIManager.put("MenuItem.afterCheckIconGap", 0);
            UIManager.put("MenuItem.checkIconOffset", 0);
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

            LoginWindow loginWindow = new LoginWindow(employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO);
            loginWindow.setVisible(true);
        });
    }
}
