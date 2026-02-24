package ui;

import dao.EmployeeDAO;
import dao.LeaveDAO;
import model.Employee;
import model.HR;
import model.Leave;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LeaveManagementWindow extends JDialog {
    private final LeaveService leaveService;
    private final EmployeeDAO employeeDAO;
    private final Employee currentEmployee;
    private final boolean isHR;

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
        super(parent, "Leave Management", true);
        this.leaveService = new LeaveService(leaveDAO);
        this.employeeDAO = employeeDAO;
        this.currentEmployee = currentEmployee;
        this.isHR = currentEmployee instanceof HR;

        initializeUI();
        loadLeaveData();
    }

    private void initializeUI() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblEmployeeName = new JLabel("Employee Name: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        lblEmployeeName.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblEmployeeName, BorderLayout.WEST);

        if (isHR) {
            JLabel lblRole = new JLabel("Role: HR");
            lblRole.setFont(new Font("Arial", Font.BOLD, 14));
            lblRole.setForeground(new Color(0, 100, 0));
            panel.add(lblRole, BorderLayout.EAST);
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

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

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
}

