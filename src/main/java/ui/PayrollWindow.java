package ui;

import dao.*;
import model.Employee;
import service.EmployeeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PayrollWindow extends JFrame {

    private final EmployeeDAO employeeDAO;
    private final AllowanceDAO allowanceDAO;
    private final DeductionDAO deductionDAO;
    private final TaxDAO taxDAO;
    private final AttendanceLogDAO attendanceLogDAO;
    private final LeaveDAO leaveDAO;
    private final Employee currentEmployee;
    private final EmployeeService employeeService;

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JPanel slipPanel;
    private JTextField txtStartDate;
    private JTextField txtEndDate;

    public PayrollWindow(EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO,
                         TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO, LeaveDAO leaveDAO,
                         Employee currentEmployee) {
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.leaveDAO = leaveDAO;
        this.currentEmployee = currentEmployee;
        this.employeeService = new EmployeeService(employeeDAO);

        initializeUI();
        loadEmployees();
    }

    private void initializeUI() {
        setTitle("Payroll Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());
        add(createTopBar(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createEmployeeListPanel(), createPayslipPanel());
        splitPane.setDividerLocation(480);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);
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

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel("Payroll Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Logged in as: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblUser, BorderLayout.EAST);

        return panel;
    }

    private JPanel createEmployeeListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee List"));

        // Date range panel
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Payroll Period (Bi-Weekly)"));

        dateRangePanel.add(new JLabel("Start Date:"));
        txtStartDate = new JTextField(10);
        txtStartDate.setToolTipText("YYYY-MM-DD");
        dateRangePanel.add(txtStartDate);

        dateRangePanel.add(new JLabel("End Date:"));
        txtEndDate = new JTextField(10);
        txtEndDate.setToolTipText("YYYY-MM-DD");
        dateRangePanel.add(txtEndDate);

        // Set default dates to current payroll period
        setDefaultPayrollPeriod();

        panel.add(dateRangePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Employment Type", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(22);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnViewAttendance = new JButton("View Attendance");
        JButton btnComputePayroll = new JButton("Compute Payroll");

        btnRefresh.addActionListener(e -> loadEmployees());
        btnViewAttendance.addActionListener(e -> viewEmployeeAttendance());
        btnComputePayroll.addActionListener(e -> computePayroll());

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnViewAttendance);
        bottomPanel.add(btnComputePayroll);
        panel.add(bottomPanel, BorderLayout.SOUTH);


        return panel;
    }

    private JPanel createPayslipPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Payslip"));

        slipPanel = new JPanel();
        slipPanel.setBackground(Color.WHITE);
        slipPanel.setLayout(new BoxLayout(slipPanel, BoxLayout.Y_AXIS));
        slipPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel placeholder = new JLabel("Select an employee and click \"Compute Payroll\"");
        placeholder.setFont(new Font("Arial", Font.ITALIC, 13));
        placeholder.setForeground(Color.GRAY);
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        slipPanel.add(Box.createVerticalGlue());
        slipPanel.add(placeholder);
        slipPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(slipPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    private void setDefaultPayrollPeriod() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();

        java.time.LocalDate startDate;
        java.time.LocalDate endDate;

        if (dayOfMonth <= 15) {
            // First period: 1st to 15th
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(15);
        } else {
            // Second period: 16th to end of month
            startDate = today.withDayOfMonth(16);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        }

        txtStartDate.setText(startDate.toString());
        txtEndDate.setText(endDate.toString());
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getFirstName() + " " + emp.getLastName(),
                    emp.getEmployeeType(),
                    emp.getRole()
            });
        }
    }

    private void viewEmployeeAttendance() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);

        Employee selectedEmployee;
        try {
            selectedEmployee = employeeService.getEmployeeById(employeeId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open AttendanceLogsWindow for the selected employee
        AttendanceLogsWindow attendanceWindow = new AttendanceLogsWindow(
                this, attendanceLogDAO, employeeDAO, selectedEmployee);
        attendanceWindow.setVisible(true);
    }

    private void computePayroll() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate date range
        String startDateStr = txtStartDate.getText().trim();
        String endDateStr = txtEndDate.getText().trim();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both start date and end date for the payroll period.",
                    "Date Range Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.time.LocalDate startDate;
        java.time.LocalDate endDate;

        try {
            startDate = java.time.LocalDate.parse(startDateStr);
            endDate = java.time.LocalDate.parse(endDateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD format.",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                    "End date cannot be before start date.",
                    "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);

        Employee emp;
        try {
            emp = employeeService.getEmployeeById(employeeId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get hours worked within the date range
        int hoursWorked = attendanceLogDAO.getHoursWorkedInRange(employeeId, startDate, endDate);
        emp.setHoursWorked(hoursWorked);
        emp.setAllowances(allowanceDAO.getAll());
        emp.setDeductions(deductionDAO.getAll());
        emp.setTaxBrackets(taxDAO.getAll());

        double gross = emp.computeGrossSalary();
        double allowances = emp.computeAllowances();
        double deductions = emp.computeDeductions();
        double tax = emp.computeTax();
        double net = emp.computeNetSalary();

        renderPayslip(emp, hoursWorked, gross, allowances, deductions, tax, net, startDateStr, endDateStr);
    }

    private void renderPayslip(Employee emp, int hoursWorked, double gross, double allowances,
                               double deductions, double tax, double net, String startDate, String endDate) {
        slipPanel.removeAll();
        slipPanel.setBackground(Color.WHITE);

        Color headerBg = new Color(30, 60, 114);
        Color sectionBg = new Color(245, 245, 250);
        Color positiveColor = new Color(0, 120, 0);
        Color negativeColor = new Color(160, 0, 0);

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerBg);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel lblCompanyName = new JLabel("MotorPH");
        lblCompanyName.setFont(new Font("Arial", Font.BOLD, 20));
        lblCompanyName.setForeground(Color.WHITE);
        lblCompanyName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPayslipTitle = new JLabel("PAYSLIP");
        lblPayslipTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblPayslipTitle.setForeground(new Color(200, 200, 200));
        lblPayslipTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPeriod = new JLabel("Period: " + startDate + " to " + endDate);
        lblPeriod.setFont(new Font("Arial", Font.PLAIN, 11));
        lblPeriod.setForeground(new Color(180, 180, 180));
        lblPeriod.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(lblCompanyName);
        headerPanel.add(lblPayslipTitle);
        headerPanel.add(lblPeriod);
        slipPanel.add(headerPanel);
        slipPanel.add(Box.createVerticalStrut(15));

        // --- Employee Info ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 4));
        infoPanel.setBackground(sectionBg);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        addInfoRow(infoPanel, "Employee ID:", String.valueOf(emp.getId()));
        addInfoRow(infoPanel, "Full Name:", emp.getFirstName() + " " + emp.getLastName());
        addInfoRow(infoPanel, "Role:", emp.getRole());
        addInfoRow(infoPanel, "Employment:", emp.getEmployeeType() + " – " + emp.getPositionLevel());
        addInfoRow(infoPanel, "Hours Worked:", hoursWorked + (hoursWorked == 1 ? " hr" : " hrs"));
        addInfoRow(infoPanel, "Compensation:", String.format("₱ %,.2f", emp.getCompensation())
                + (emp.getEmployeeType().equals("Probationary") ? " / hr" : " / mo"));

        slipPanel.add(infoPanel);
        slipPanel.add(Box.createVerticalStrut(15));

        // --- Earnings ---
        slipPanel.add(createSectionLabel("EARNINGS", positiveColor));
        slipPanel.add(Box.createVerticalStrut(5));

        JPanel earningsPanel = new JPanel(new GridLayout(0, 2, 10, 4));
        earningsPanel.setBackground(Color.WHITE);
        earningsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        earningsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        addSlipRow(earningsPanel, "Basic / Computed Salary:", String.format("₱ %,.2f", gross - allowances), Color.BLACK);
        addSlipRow(earningsPanel, "Allowances:", String.format("₱ %,.2f", allowances), positiveColor);
        addSlipRow(earningsPanel, "Gross Salary:", String.format("₱ %,.2f", gross), positiveColor);

        slipPanel.add(earningsPanel);
        slipPanel.add(Box.createVerticalStrut(15));

        // --- Deductions ---
        slipPanel.add(createSectionLabel("DEDUCTIONS", negativeColor));
        slipPanel.add(Box.createVerticalStrut(5));

        JPanel deductionsPanel = new JPanel(new GridLayout(0, 2, 10, 4));
        deductionsPanel.setBackground(Color.WHITE);
        deductionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        deductionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        addSlipRow(deductionsPanel, "SSS / PhilHealth / Pag-IBIG:", String.format("₱ %,.2f", deductions - tax), negativeColor);
        addSlipRow(deductionsPanel, "Withholding Tax:", String.format("₱ %,.2f", tax), negativeColor);
        addSlipRow(deductionsPanel, "Total Deductions:", String.format("₱ %,.2f", deductions), negativeColor);

        slipPanel.add(deductionsPanel);
        slipPanel.add(Box.createVerticalStrut(15));

        // --- Net Pay ---
        JPanel netPanel = new JPanel(new BorderLayout());
        netPanel.setBackground(new Color(240, 255, 240));
        netPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(positiveColor, 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        netPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lblNetTitle = new JLabel("NET PAY");
        lblNetTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblNetTitle.setForeground(positiveColor);

        JLabel lblNetAmount = new JLabel(String.format("₱ %,.2f", net));
        lblNetAmount.setFont(new Font("Arial", Font.BOLD, 18));
        lblNetAmount.setForeground(positiveColor);
        lblNetAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        netPanel.add(lblNetTitle, BorderLayout.WEST);
        netPanel.add(lblNetAmount, BorderLayout.EAST);

        slipPanel.add(netPanel);
        slipPanel.add(Box.createVerticalStrut(20));

        slipPanel.revalidate();
        slipPanel.repaint();
    }

    private JLabel createSectionLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(color);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, color));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return label;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(lbl);
        panel.add(val);
    }

    private void addSlipRow(JPanel panel, String label, String value, Color valueColor) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 60));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 12));
        val.setForeground(valueColor);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(lbl);
        panel.add(val);
    }


    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow(employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO);
                loginWindow.setVisible(true);
            });
        }
    }
}

