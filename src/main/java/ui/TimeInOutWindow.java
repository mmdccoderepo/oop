package ui;

import dao.*;
import model.AttendanceLog;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeInOutWindow extends JFrame {

    private final AttendanceLogDAO attendanceLogDAO;
    private final EmployeeDAO employeeDAO;
    private final AllowanceDAO allowanceDAO;
    private final DeductionDAO deductionDAO;
    private final TaxDAO taxDAO;
    private final LeaveDAO leaveDAO;
    private final Employee currentEmployee;

    private JLabel lblTodayStatus;
    private JButton btnTimeIn;
    private JButton btnTimeOut;
    private JTextField txtTimeIn;
    private JTextField txtTimeOut;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    // Holds the time-in for the current session (before time-out)
    private LocalTime sessionTimeIn = null;

    public TimeInOutWindow(EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO,
                           TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO, LeaveDAO leaveDAO,
                           Employee currentEmployee) {
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.leaveDAO = leaveDAO;
        this.currentEmployee = currentEmployee;

        initializeUI();
        refreshTodayStatus();
        loadRecentLogs();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Navigation Menu
        JMenu navMenu = new JMenu("View");
        String role = currentEmployee.getRole();

        if ("HR".equals(role)) {
            JMenuItem empManagementItem = new JMenuItem("Employee Management");
            empManagementItem.setMargin(new Insets(2, 2, 2, 2));
            empManagementItem.addActionListener(e -> openEmployeeManagement());
            navMenu.add(empManagementItem);
        } else if ("Finance".equals(role)) {
            JMenuItem payrollItem = new JMenuItem("Payroll");
            payrollItem.setMargin(new Insets(2, 2, 2, 2));
            payrollItem.addActionListener(e -> openPayroll());
            navMenu.add(payrollItem);
        }

        JMenuItem leavesItem = new JMenuItem("Leave Requests");
        leavesItem.setMargin(new Insets(2, 2, 2, 2));
        leavesItem.addActionListener(e -> openLeaveManagement());
        navMenu.add(leavesItem);

        menuBar.add(navMenu);

        // File Menu
        JMenu fileMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setMargin(new Insets(2, 2, 2, 2));
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow(employeeDAO, allowanceDAO, deductionDAO,
                        taxDAO, attendanceLogDAO, leaveDAO);
                loginWindow.setVisible(true);
            });
        }
    }

    private void initializeUI() {
        setTitle("Time In / Out");
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setResizable(false);

        setJMenuBar(createMenuBar());
        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel title = new JLabel("MotorPH — Time In / Out");
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblLoggedIn = new JLabel("Logged in as: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName()
                + "  |  " + currentEmployee.getRole());
        lblLoggedIn.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(title, BorderLayout.WEST);
        panel.add(lblLoggedIn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Date + time input section
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblDate = new JLabel("Date:", SwingConstants.RIGHT);
        lblDate.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel lblDateValue = new JLabel(LocalDate.now().format(DISPLAY_FMT));
        lblDateValue.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel lblTimeInLabel = new JLabel("Time In (HH:mm):", SwingConstants.RIGHT);
        lblTimeInLabel.setFont(new Font("Arial", Font.BOLD, 13));
        txtTimeIn = new JTextField();
        txtTimeIn.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimeIn.setToolTipText("Enter time in 24-hour format, e.g., 08:00");

        JLabel lblTimeOutLabel = new JLabel("Time Out (HH:mm):", SwingConstants.RIGHT);
        lblTimeOutLabel.setFont(new Font("Arial", Font.BOLD, 13));
        txtTimeOut = new JTextField();
        txtTimeOut.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimeOut.setToolTipText("Enter time in 24-hour format, e.g., 17:00");

        lblTodayStatus = new JLabel("", SwingConstants.CENTER);
        lblTodayStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        lblTodayStatus.setForeground(new Color(80, 80, 80));

        inputPanel.add(lblDate);
        inputPanel.add(lblDateValue);
        inputPanel.add(lblTimeInLabel);
        inputPanel.add(txtTimeIn);
        inputPanel.add(lblTimeOutLabel);
        inputPanel.add(txtTimeOut);
        inputPanel.add(new JLabel(""));
        inputPanel.add(lblTodayStatus);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Recent logs table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Recent Attendance Logs (Last 10)"));

        String[] columns = {"Date", "Time In", "Time Out", "Hours Worked"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Time In/Out buttons
        JPanel timeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnTimeIn = new JButton("Time In");
        btnTimeOut = new JButton("Time Out");

        btnTimeIn.addActionListener(e -> recordTimeIn());
        btnTimeOut.addActionListener(e -> recordTimeOut());

        timeButtonPanel.add(btnTimeIn);
        timeButtonPanel.add(btnTimeOut);

        panel.add(timeButtonPanel, BorderLayout.CENTER);


        return panel;
    }


    private void refreshTodayStatus() {
        LocalDate today = LocalDate.now();
        List<AttendanceLog> logs = attendanceLogDAO.getByEmployeeId(currentEmployee.getId());
        AttendanceLog todayLog = null;
        for (AttendanceLog log : logs) {
            if (log.getDate().equals(today)) {
                todayLog = log;
            }
        }

        if (todayLog == null && sessionTimeIn == null) {
            lblTodayStatus.setText("No log for today. Enter time and click \"Time In\".");
            lblTodayStatus.setForeground(new Color(150, 100, 0));
            btnTimeIn.setEnabled(true);
            btnTimeOut.setEnabled(false);
            txtTimeIn.setEnabled(true);
            txtTimeOut.setEnabled(false);
        } else if (sessionTimeIn != null) {
            // Timed in this session, not yet timed out
            lblTodayStatus.setText("Timed in at " + sessionTimeIn.format(TIME_FMT) + ". Enter time out and click \"Time Out\".");
            lblTodayStatus.setForeground(new Color(0, 120, 0));
            btnTimeIn.setEnabled(false);
            btnTimeOut.setEnabled(true);
            txtTimeIn.setEnabled(false);
            txtTimeOut.setEnabled(true);
        } else {
            // Already has a complete log today
            lblTodayStatus.setText("Today's log: " + todayLog.getTimeIn().format(TIME_FMT)
                    + " – " + todayLog.getTimeOut().format(TIME_FMT)
                    + "  (" + todayLog.getHoursWorked() + " hrs)");
            lblTodayStatus.setForeground(new Color(30, 100, 180));
            btnTimeIn.setEnabled(false);
            btnTimeOut.setEnabled(false);
            txtTimeIn.setEnabled(false);
            txtTimeOut.setEnabled(false);
        }
    }

    private void loadRecentLogs() {
        tableModel.setRowCount(0);
        List<AttendanceLog> logs = attendanceLogDAO.getByEmployeeId(currentEmployee.getId());

        // Show last 10, most recent first
        int start = Math.max(0, logs.size() - 10);
        for (int i = logs.size() - 1; i >= start; i--) {
            AttendanceLog log = logs.get(i);
            tableModel.addRow(new Object[]{
                    log.getDate().format(DATE_FMT),
                    log.getTimeIn().format(TIME_FMT),
                    log.getTimeOut().format(TIME_FMT),
                    log.getHoursWorked() + " hrs"
            });
        }
    }

    private void recordTimeIn() {
        String timeInStr = txtTimeIn.getText().trim();

        if (timeInStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a time in HH:mm format (e.g., 08:00).",
                    "Time Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sessionTimeIn = LocalTime.parse(timeInStr, TIME_FMT);
            JOptionPane.showMessageDialog(this,
                    "Time In recorded at " + sessionTimeIn.format(TIME_FMT),
                    "Time In", JOptionPane.INFORMATION_MESSAGE);
            refreshTodayStatus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid time format. Please use HH:mm format (e.g., 08:00).",
                    "Invalid Time", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recordTimeOut() {
        if (sessionTimeIn == null) {
            JOptionPane.showMessageDialog(this, "Please time in first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String timeOutStr = txtTimeOut.getText().trim();

        if (timeOutStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a time out in HH:mm format (e.g., 17:00).",
                    "Time Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalTime timeOut;
        try {
            timeOut = LocalTime.parse(timeOutStr, TIME_FMT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid time format. Please use HH:mm format (e.g., 17:00).",
                    "Invalid Time", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (timeOut.isBefore(sessionTimeIn) || timeOut.equals(sessionTimeIn)) {
            JOptionPane.showMessageDialog(this,
                    "Time Out must be after Time In.", "Invalid Time", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AttendanceLog log = new AttendanceLog(
                currentEmployee.getId(),
                LocalDate.now(),
                sessionTimeIn,
                timeOut
        );

        if (attendanceLogDAO.addLog(log)) {
            int hours = log.getHoursWorked();
            JOptionPane.showMessageDialog(this,
                    "Time Out recorded at " + timeOut.format(TIME_FMT) + "\nHours worked: " + hours + " hrs",
                    "Time Out", JOptionPane.INFORMATION_MESSAGE);
            sessionTimeIn = null;
            txtTimeIn.setText("");
            txtTimeOut.setText("");
            refreshTodayStatus();
            loadRecentLogs();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save attendance log. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEmployeeManagement() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            EmployeeManagementWindow frame = new EmployeeManagementWindow(
                    employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO, currentEmployee);
            frame.setVisible(true);
        });
    }

    private void openPayroll() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            PayrollWindow payrollWindow = new PayrollWindow(
                    employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO, currentEmployee);
            payrollWindow.setVisible(true);
        });
    }

    private void openLeaveManagement() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            LeaveManagementWindow leaveWindow = new LeaveManagementWindow(
                    leaveDAO, employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, currentEmployee);
            leaveWindow.setVisible(true);
        });
    }
}

