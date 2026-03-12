package ui;

import dao.*;
import model.Employee;
import service.EmployeeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmployeeManagementWindow extends JFrame {
    private final EmployeeService employeeService;
    private final EmployeeDAO employeeDAO;
    private final AllowanceDAO allowanceDAO;
    private final DeductionDAO deductionDAO;
    private final TaxDAO taxDAO;
    private final AttendanceLogDAO attendanceLogDAO;
    private final LeaveDAO leaveDAO;
    private final Employee currentEmployee;

    // Form components
    private JTextField txtId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cmbEmployeeType;
    private JComboBox<String> cmbPositionLevel;
    private JComboBox<String> cmbRole;
    private JTextField txtAddress;
    private JTextField txtSssNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtTin;
    private JTextField txtPagIbigNumber;
    private JTextField txtCompensation;
    private JLabel lblCompensation;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    public EmployeeManagementWindow(EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO, TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO, LeaveDAO leaveDAO, Employee currentEmployee) {
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.leaveDAO = leaveDAO;
        this.currentEmployee = currentEmployee;

        this.employeeService = new EmployeeService(employeeDAO);

        initializeUI();
        loadTableData();
    }

    private void initializeUI() {
        setTitle("Employee Management");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());
        add(createTopBar(), BorderLayout.NORTH);

        JScrollPane formScrollPane = new JScrollPane(createFormPanel());
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(formScrollPane, BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        panel.setPreferredSize(new Dimension(450, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ID
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(20);
        txtId.setEditable(false);
        panel.add(txtId, gbc);

        // First Name
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(20);
        panel.add(txtFirstName, gbc);

        // Last Name
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(20);
        panel.add(txtLastName, gbc);

        // Email
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);

        // Phone
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);

        // Address
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        panel.add(txtAddress, gbc);

        // Employee Type
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Employment Type:"), gbc);
        gbc.gridx = 1;
        cmbEmployeeType = new JComboBox<>(new String[]{"Regular", "Probationary"});
        panel.add(cmbEmployeeType, gbc);
        cmbEmployeeType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateCompensationLabel();
            }
        });

        // Compensation
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        lblCompensation = new JLabel("Basic Salary:");
        panel.add(lblCompensation, gbc);
        gbc.gridx = 1;
        txtCompensation = new JTextField(20);
        panel.add(txtCompensation, gbc);

        // Position Level
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Position Level:"), gbc);
        gbc.gridx = 1;
        cmbPositionLevel = new JComboBox<>(new String[]{"Managerial", "Supervisory", "Rank and File"});
        panel.add(cmbPositionLevel, gbc);

        // Role
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"HR", "Finance", "IT", "Employee"});
        panel.add(cmbRole, gbc);

        // SSS Number
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("SSS Number:"), gbc);
        gbc.gridx = 1;
        txtSssNumber = new JTextField(20);
        panel.add(txtSssNumber, gbc);

        // PhilHealth Number
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("PhilHealth Number:"), gbc);
        gbc.gridx = 1;
        txtPhilHealthNumber = new JTextField(20);
        panel.add(txtPhilHealthNumber, gbc);

        // TIN
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("TIN:"), gbc);
        gbc.gridx = 1;
        txtTin = new JTextField(20);
        panel.add(txtTin, gbc);

        // Pag-IBIG Number
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Pag-IBIG Number:"), gbc);
        gbc.gridx = 1;
        txtPagIbigNumber = new JTextField(20);
        panel.add(txtPagIbigNumber, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee List"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTableData());
        topPanel.add(btnRefresh);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Address", "Employment Type", "Position Level", "Role", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number", "Compensation"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectEmployee();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnCreate = new JButton("Create");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnCreate.addActionListener(e -> createEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClear.addActionListener(e -> clearForm());

        panel.add(btnCreate);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);

        return panel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // View Menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem viewTimeInOutItem = new JMenuItem("Time In / Out");
        viewTimeInOutItem.setMargin(new Insets(2, 2, 2, 2));
        viewTimeInOutItem.addActionListener(e -> openTimeInOut());
        viewTimeInOutItem.setIconTextGap(0);
        viewMenu.add(viewTimeInOutItem);

        viewMenu.addSeparator();

        JMenuItem viewAttendanceItem = new JMenuItem("Attendance Logs");
        viewAttendanceItem.setMargin(new Insets(2, 2, 2, 2));
        viewAttendanceItem.addActionListener(e -> openAttendanceLogs());
        viewMenu.add(viewAttendanceItem);

        viewMenu.addSeparator();

        JMenuItem viewLeavesItem = new JMenuItem("Leave Requests");
        viewLeavesItem.setMargin(new Insets(2, 2, 2, 2));
        viewLeavesItem.addActionListener(e -> openLeaveManagement());
        viewMenu.add(viewLeavesItem);

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

        JLabel titleLabel = new JLabel("Employee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Logged in as: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblUser, BorderLayout.EAST);

        return panel;
    }

    private void createEmployee() {
        try {
            Employee employee = getEmployeeFromForm();
            employeeService.addEmployee(employee);
            JOptionPane.showMessageDialog(this, "Employee created successfully!");
            loadTableData();
            clearForm();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an employee to update!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Employee employee = getEmployeeFromForm();
            employeeService.updateEmployee(employee);
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            loadTableData();
            clearForm();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this employee?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtId.getText());
                employeeService.deleteEmployee(id);
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                loadTableData();
                clearForm();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Employee employee;
        try {
            employee = employeeService.getEmployeeById(id);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        populateFormWithEmployee(employee);
        updateCompensationLabel();
    }

    private void loadTableData() {
        List<Employee> employees = employeeService.getAllEmployees();
        updateTable(employees);
    }

    private void updateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getPhoneNumber(),
                    emp.getAddress(),
                    emp.getEmployeeType(),
                    emp.getPositionLevel(),
                    emp.getRole(),
                    emp.getSssNumber(),
                    emp.getPhilHealthNumber(),
                    emp.getTin(),
                    emp.getPagIbigNumber(),
                    String.format("%.2f", emp.getCompensation())
            });
        }
    }

    private Employee buildEmployee(String employeeType, int id, String firstName, String lastName,
                                   String email, String phone, String address, String positionLevel,
                                   String role, String sssNumber, String philHealthNumber,
                                   String tin, String pagIbigNumber) {
        double compensation = getCompensationFromForm();
        Employee employee = employeeService.createEmployee(id, firstName, lastName,
                email, phone, address, employeeType, positionLevel, role, sssNumber, philHealthNumber,
                tin, pagIbigNumber, compensation);
        int totalHoursWorked = attendanceLogDAO.getTotalHoursWorked(id);
        employee.setHoursWorked(totalHoursWorked);
        employee.setAllowances(allowanceDAO.getAll());
        employee.setDeductions(deductionDAO.getAll());
        employee.setTaxBrackets(taxDAO.getAll());
        return employee;
    }

    private Employee getEmployeeFromForm() {
        int id = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String employeeType = (cmbEmployeeType.getSelectedItem() == null) ? "" : cmbEmployeeType.getSelectedItem().toString().trim();
        String positionLevel = (cmbPositionLevel.getSelectedItem() == null) ? "" : cmbPositionLevel.getSelectedItem().toString().trim();
        String role = (cmbRole.getSelectedItem() == null) ? "" : cmbRole.getSelectedItem().toString().trim();
        String sssNumber = txtSssNumber.getText().trim();
        String philHealthNumber = txtPhilHealthNumber.getText().trim();
        String tin = txtTin.getText().trim();
        String pagIbigNumber = txtPagIbigNumber.getText().trim();

        return buildEmployee(employeeType, id, firstName, lastName, email, phone, address,
                positionLevel, role, sssNumber, philHealthNumber, tin, pagIbigNumber);
    }

    private void clearForm() {
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        cmbEmployeeType.setSelectedIndex(0);
        cmbPositionLevel.setSelectedIndex(0);
        cmbRole.setSelectedIndex(0);
        txtSssNumber.setText("");
        txtPhilHealthNumber.setText("");
        txtTin.setText("");
        txtPagIbigNumber.setText("");
        txtCompensation.setText("");
        table.clearSelection();
    }

    private void updateCompensationLabel() {
        String selectedType = (String) cmbEmployeeType.getSelectedItem();
        if ("Probationary".equals(selectedType)) {
            lblCompensation.setText("Hourly Rate:");
        } else {
            lblCompensation.setText("Basic Salary:");
        }
    }

    private void populateFormWithEmployee(Employee employee) {
        txtId.setText(String.valueOf(employee.getId()));
        txtFirstName.setText(employee.getFirstName());
        txtLastName.setText(employee.getLastName());
        txtEmail.setText(employee.getEmail());
        txtPhone.setText(employee.getPhoneNumber());
        txtAddress.setText(employee.getAddress());
        cmbEmployeeType.setSelectedItem(employee.getEmployeeType());
        cmbPositionLevel.setSelectedItem(employee.getPositionLevel());
        cmbRole.setSelectedItem(employee.getRole());
        txtSssNumber.setText(employee.getSssNumber());
        txtPhilHealthNumber.setText(employee.getPhilHealthNumber());
        txtTin.setText(employee.getTin());
        txtPagIbigNumber.setText(employee.getPagIbigNumber());
        txtCompensation.setText(String.format("%.2f", employee.getCompensation()));
    }

    private double getCompensationFromForm() {
        String text = txtCompensation.getText().trim();
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private void openLeaveManagement() {
        LeaveManagementWindow dialog = new LeaveManagementWindow(this, leaveDAO, employeeDAO, currentEmployee);
        dialog.setVisible(true);
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
