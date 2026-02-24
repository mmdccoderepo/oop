package ui;

import dao.*;
import model.Employee;
import service.EmployeeService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementWindow extends JFrame {
    private final EmployeeService employeeService;
    private final EmployeeDAO employeeDAO;
    private final AllowanceDAO allowanceDAO;
    private final DeductionDAO deductionDAO;
    private final TaxDAO taxDAO;
    private final AttendanceLogDAO attendanceLogDAO;
    private final LeaveDAO leaveDAO;

    // Form components
    private JTextField txtId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cmbEmployeeType;
    private JComboBox<String> cmbPositionLevel;
    private JComboBox<String> cmbDepartment;
    private JTextField txtAddress;
    private JTextField txtSssNumber;
    private JTextField txtPhilHealthNumber;
    private JTextField txtTin;
    private JTextField txtPagIbigNumber;
    private JTextField txtCompensation;
    private JLabel lblCompensation;

    private JLabel lblGrossSalary;
    private JLabel lblAllowances;
    private JLabel lblDeductions;
    private JLabel lblNetSalary;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    public EmployeeManagementWindow(EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO, TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO, LeaveDAO leaveDAO) {
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.leaveDAO = leaveDAO;

        this.employeeService = new EmployeeService(employeeDAO);

        initializeUI();
        loadTableData();
    }

    private void initializeUI() {
        setTitle("Employee Management");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Add form panel in a scroll pane
        JScrollPane formScrollPane = new JScrollPane(createFormPanel());
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftPanel.add(formScrollPane);

        leftPanel.add(Box.createVerticalStrut(10)); // Add spacing between panels
        leftPanel.add(createSalaryPanel());

        add(leftPanel, BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        panel.setPreferredSize(new Dimension(450, 500));

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
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        String[] positions = {"Regular", "Probationary"};
        cmbEmployeeType = new JComboBox<>(positions);
        panel.add(cmbEmployeeType, gbc);

        cmbEmployeeType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateCompensationLabel();
                updateSalaryLabels();
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
        attachDocumentListener(txtCompensation);
        panel.add(txtCompensation, gbc);

        // Position Level
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Position Level:"), gbc);
        gbc.gridx = 1;
        String[] positionLevels = {"Managerial", "Supervisory", "Rank and File"};
        cmbPositionLevel = new JComboBox<>(positionLevels);
        panel.add(cmbPositionLevel, gbc);

        // Department
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        String[] departments = {"HR", "Finance", "IT"};
        cmbDepartment = new JComboBox<>(departments);
        panel.add(cmbDepartment, gbc);

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

    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Salary Details"));
        panel.setPreferredSize(new Dimension(450, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Gross Salary label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Gross Salary:"), gbc);
        gbc.gridx = 1;
        lblGrossSalary = new JLabel("₱ 0.00");
        panel.add(lblGrossSalary, gbc);

        // Allowances label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Total Allowances:"), gbc);
        gbc.gridx = 1;
        lblAllowances = new JLabel("₱ 0.00");
        lblAllowances.setForeground(new Color(0, 100, 0)); // Dark green
        panel.add(lblAllowances, gbc);

        // Deductions label
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Deductions:"), gbc);
        gbc.gridx = 1;
        lblDeductions = new JLabel("₱ 0.00");
        lblDeductions.setForeground(new Color(150, 0, 0)); // Dark red
        panel.add(lblDeductions, gbc);

        // Net Salary label
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Net Salary:"), gbc);
        gbc.gridx = 1;
        lblNetSalary = new JLabel("₱ 0.00");
        lblNetSalary.setFont(lblNetSalary.getFont().deriveFont(Font.BOLD, 16f));
        lblNetSalary.setForeground(new Color(0, 100, 0)); // Dark green color
        panel.add(lblNetSalary, gbc);


        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee List"));

        // Refresh button panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTableData());
        topPanel.add(btnRefresh);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Address", "Position Level", "Department", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number", "Hourly Rate", "Monthly Salary"};
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

        TableColumnModel columnModel = table.getColumnModel();
        TableColumn hourlyRateCol = table.getColumn("Hourly Rate");
        TableColumn salaryCol = table.getColumn("Monthly Salary");
        columnModel.removeColumn(hourlyRateCol);
        columnModel.removeColumn(salaryCol);

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
        JButton btnLeaveManagement = new JButton("Leave Requests");

        btnCreate.addActionListener(e -> createEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClear.addActionListener(e -> clearForm());
        btnLeaveManagement.addActionListener(e -> openLeaveManagement());

        panel.add(btnCreate);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        panel.add(btnLeaveManagement);

        return panel;
    }

    private void createEmployee() {
        try {
            Employee employee = getEmployeeFromForm();
            employee.setId(0);

            if (employeeDAO.create(employee)) {
                JOptionPane.showMessageDialog(this, "Employee created successfully!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
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

            if (employeeDAO.update(employee)) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtId.getText());

                if (employeeDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Employee employee = employeeDAO.read(id);

        if (employee == null) return;

        populateFormWithEmployee(employee);
        updateCompensationLabel();
        updateSalaryLabels();
    }

    private void loadTableData() {
        // TODO: Move employeeDAO to a service layer for business logic data validation
        List<Employee> employees = employeeDAO.getAll();
        updateTable(employees);
    }

    private void updateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            List<String> row = new ArrayList<String>(List.of(
                    String.valueOf(emp.getId()),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getPhoneNumber(),
                    emp.getEmployeeType(),
                    emp.getPositionLevel(),
                    emp.getDepartment(),
                    emp.getSssNumber(),
                    emp.getPhilHealthNumber(),
                    emp.getTin(),
                    emp.getPagIbigNumber()
            ));

            row.add(String.format("%.2f", emp.getCompensation()));

            tableModel.addRow(row.toArray());
        }
    }

    private Employee createEmployee(String employeeType, int id, String firstName, String lastName,
                                    String email, String phone, String address, String positionLevel,
                                    String department, String sssNumber, String philHealthNumber,
                                    String tin, String pagIbigNumber) {
        double compensation = getCompensationFromForm();

        Employee employee = employeeService.createEmployee(id, firstName, lastName,
                email, phone, address, employeeType, positionLevel, department, sssNumber, philHealthNumber,
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
        String department = (cmbDepartment.getSelectedItem() == null) ? "" : cmbDepartment.getSelectedItem().toString().trim();
        String sssNumber = txtSssNumber.getText().trim();
        String philHealthNumber = txtPhilHealthNumber.getText().trim();
        String tin = txtTin.getText().trim();
        String pagIbigNumber = txtPagIbigNumber.getText().trim();

        return createEmployee(employeeType, id, firstName, lastName, email, phone, address,
                positionLevel, department, sssNumber, philHealthNumber, tin, pagIbigNumber);
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
        cmbDepartment.setSelectedIndex(0);
        txtSssNumber.setText("");
        txtPhilHealthNumber.setText("");
        txtTin.setText("");
        txtPagIbigNumber.setText("");
        txtCompensation.setText("");

        lblGrossSalary.setText("₱ 0.00");
        lblAllowances.setText("₱ 0.00");
        lblDeductions.setText("₱ 0.00");
        lblNetSalary.setText("₱ 0.00");

        table.clearSelection();
    }

    private void attachDocumentListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSalaryLabels();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSalaryLabels();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSalaryLabels();
            }
        });
    }

    private void updateSalaryLabels() {
        try {
            Employee employee = getEmployeeFromForm();

            double gross = employee.computeGrossSalary();
            double totalAllowances = employee.computeAllowances();
            double totalDeductions = employee.computeDeductions();
            double net = employee.computeNetSalary();

            lblGrossSalary.setText(String.format("₱ %.2f", gross));
            lblAllowances.setText(String.format("₱ %.2f", totalAllowances));
            lblDeductions.setText(String.format("₱ %.2f", totalDeductions));
            lblNetSalary.setText(String.format("₱ %.2f", net));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        cmbDepartment.setSelectedItem(employee.getDepartment());
        txtSssNumber.setText(employee.getSssNumber());
        txtPhilHealthNumber.setText(employee.getPhilHealthNumber());
        txtTin.setText(employee.getTin());
        txtPagIbigNumber.setText(employee.getPagIbigNumber());
        txtCompensation.setText(String.format("%.2f", employee.getCompensation()));
    }

    private double getCompensationFromForm() {
        return Double.parseDouble(txtCompensation.getText().isEmpty() ? "0" : txtCompensation.getText().trim());
    }

    private void openLeaveManagement() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LeaveManagementWindow dialog = new LeaveManagementWindow(this, leaveDAO, employeeDAO, employee);
        dialog.setVisible(true);
    }
}
