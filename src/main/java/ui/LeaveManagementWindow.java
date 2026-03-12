package ui;

import dao.*;
import model.Employee;
import model.Leave;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LeaveManagementWindow extends JFrame {
    private final LeaveService leaveService;
    private final LeaveDAO leaveDAO;
    private final EmployeeDAO employeeDAO;
    private final AllowanceDAO allowanceDAO;
    private final DeductionDAO deductionDAO;
    private final TaxDAO taxDAO;
    private final AttendanceLogDAO attendanceLogDAO;
    private final Employee currentEmployee;
    private final boolean isHR;
    private final boolean isStandalone;

    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtReason;
    private JButton btnFileLeave;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnRefresh;
    private JLabel lblEmployeeName;

    public LeaveManagementWindow(Frame parent, LeaveDAO leaveDAO, EmployeeDAO employeeDAO, Employee currentEmployee) {
        this(leaveDAO, employeeDAO, null, null, null, null, currentEmployee, false);
    }

    public LeaveManagementWindow(LeaveDAO leaveDAO, EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO,
                                 DeductionDAO deductionDAO, TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO,
                                 Employee currentEmployee) {
        this(leaveDAO, employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, currentEmployee, true);
    }

    private LeaveManagementWindow(LeaveDAO leaveDAO, EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO,
                                  DeductionDAO deductionDAO, TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO,
                                  Employee currentEmployee, boolean isStandalone) {
        this.leaveService = new LeaveService(leaveDAO);
        this.leaveDAO = leaveDAO;
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.currentEmployee = currentEmployee;
        this.isHR = "HR".equals(currentEmployee.getRole());
        this.isStandalone = isStandalone;

        initializeUI();
        loadLeaveData();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // View Menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem viewTimeInOutItem = new JMenuItem("Time In / Out");
        viewTimeInOutItem.setMargin(new Insets(2, 2, 2, 2));
        viewTimeInOutItem.addActionListener(e -> openTimeInOut());
        viewMenu.add(viewTimeInOutItem);

        viewMenu.addSeparator();

        JMenuItem viewAttendanceItem = new JMenuItem("Attendance Logs");
        viewAttendanceItem.setMargin(new Insets(2, 2, 2, 2));
        viewAttendanceItem.addActionListener(e -> openAttendanceLogs());
        viewMenu.add(viewAttendanceItem);

        menuBar.add(viewMenu);

        // Account Menu
        JMenu accountMenu = new JMenu("Account");

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setMargin(new Insets(2, 2, 2, 2));
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);

        menuBar.add(accountMenu);

        return menuBar;
    }

    private void openTimeInOut() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            TimeInOutWindow timeWindow = new TimeInOutWindow(
                    employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO, currentEmployee);
            timeWindow.setVisible(true);
        });
    }

    private void openAttendanceLogs() {
        AttendanceLogsWindow attendanceWindow = new AttendanceLogsWindow(
                this, attendanceLogDAO, employeeDAO, currentEmployee);
        attendanceWindow.setVisible(true);
    }

    private void initializeUI() {
        setTitle("Leave Management");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        if (isStandalone) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setJMenuBar(createMenuBar());
        } else {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (isStandalone) {
            lblEmployeeName = new JLabel("Logged in as: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
            lblEmployeeName.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(lblEmployeeName, BorderLayout.WEST);
        }

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Leave Requests"));

        String[] columns = {"Leave ID", "Employee ID", "Employee Name", "Start Date", "End Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaveTable = new JTable(tableModel);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(leaveTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel fileLeavePanel = createFileLeavePanel();
        mainPanel.add(fileLeavePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadLeaveData());
        buttonPanel.add(btnRefresh);

        if (isHR) {
            btnApprove = new JButton("Approve Selected");
            btnApprove.addActionListener(e -> approveLeave());
            buttonPanel.add(btnApprove);

            btnReject = new JButton("Reject Selected");
            btnReject.addActionListener(e -> rejectLeave());
            buttonPanel.add(btnReject);
        }


        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createFileLeavePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("File New Leave"));

        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField(12);
        panel.add(txtStartDate);

        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        txtEndDate = new JTextField(12);
        panel.add(txtEndDate);

        panel.add(new JLabel("Reason:"));
        txtReason = new JTextField(20);
        panel.add(txtReason);

        btnFileLeave = new JButton("File Leave");
        btnFileLeave.addActionListener(e -> fileLeave());
        panel.add(btnFileLeave);

        return panel;
    }

    private void loadLeaveData() {
        tableModel.setRowCount(0);

        try {
            List<Leave> leaves;
            if (isHR) {
                leaves = leaveService.getAll(currentEmployee);
            } else {
                leaves = leaveService.getEmployeeLeaves(currentEmployee.getId());
            }

            for (Leave leave : leaves) {
                Employee emp = employeeDAO.read(leave.getEmployeeId());
                String employeeName = emp != null ? emp.getFirstName() + " " + emp.getLastName() : "Unknown";

                Object[] row = {
                        leave.getId(),
                        leave.getEmployeeId(),
                        employeeName,
                        leave.getStartDate().format(DateTimeFormatter.ISO_DATE),
                        leave.getEndDate().format(DateTimeFormatter.ISO_DATE),
                        leave.getReason().substring(0, Math.min(leave.getReason().length(), 30)) + (leave.getReason().length() > 30 ? "..." : ""),
                        leave.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (IllegalAccessException e) {
            showMessage("Access denied: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private void fileLeave() {
        try {
            String startDateStr = txtStartDate.getText().trim();
            String endDateStr = txtEndDate.getText().trim();
            String reason = txtReason.getText().trim();

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                showMessage("Please enter both start and end dates", MessageType.VALIDATION);
                return;
            }

            if (reason.isEmpty()) {
                showMessage("Please enter a reason for the leave", MessageType.VALIDATION);
                return;
            }

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            if (endDate.isBefore(startDate)) {
                showMessage("End date cannot be before start date", MessageType.VALIDATION);
                return;
            }

            leaveService.fileLeave(currentEmployee, startDate, endDate, reason);

            showMessage("Leave request filed successfully", MessageType.SUCCESS);

            txtStartDate.setText("");
            txtEndDate.setText("");
            txtReason.setText("");
            loadLeaveData();

        } catch (DateTimeParseException e) {
            showMessage("Invalid date format. Please use YYYY-MM-DD format", MessageType.ERROR);
        } catch (Exception e) {
            showMessage("Error filing leave: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private void approveLeave() {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select a leave request to approve", MessageType.WARNING);
            return;
        }

        try {
            int leaveId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

            if ("APPROVED".equals(currentStatus)) {
                showMessage("This leave request is already approved", MessageType.INFO);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to approve this leave request?",
                    "Confirm Approval",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                leaveService.approveLeave(currentEmployee, leaveId);
                showMessage("Leave request approved successfully", MessageType.SUCCESS);
                loadLeaveData();
            }
        } catch (IllegalAccessException e) {
            showMessage("Access denied: " + e.getMessage(), MessageType.ERROR);
        } catch (Exception e) {
            showMessage("Error approving leave: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private void rejectLeave() {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select a leave request to reject", MessageType.WARNING);
            return;
        }

        try {
            int leaveId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

            if ("REJECTED".equals(currentStatus)) {
                showMessage("This leave request is already rejected", MessageType.INFO);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reject this leave request?",
                    "Confirm Rejection",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                leaveService.rejectLeave(currentEmployee, leaveId);
                showMessage("Leave request rejected", MessageType.SUCCESS);
                loadLeaveData();
            }
        } catch (IllegalAccessException e) {
            showMessage("Access denied: " + e.getMessage(), MessageType.ERROR);
        } catch (Exception e) {
            showMessage("Error rejecting leave: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private enum MessageType {
        ERROR, WARNING, INFO, SUCCESS, VALIDATION
    }

    private void showMessage(String message, MessageType type) {
        String title;
        int messageType;

        switch (type) {
            case ERROR:
                title = "Error";
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            case WARNING:
                title = "Warning";
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            case INFO:
                title = "Information";
                messageType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case SUCCESS:
                title = "Success";
                messageType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case VALIDATION:
                title = "Validation Error";
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            default:
                title = "Message";
                messageType = JOptionPane.INFORMATION_MESSAGE;
        }

        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void logout() {
        if (!isStandalone) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow(employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO);
                loginWindow.setVisible(true);
            });
        }
    }
}

