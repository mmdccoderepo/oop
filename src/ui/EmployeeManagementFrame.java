package ui;

import interfaces.EmployeeService;
import model.Employee;
import service.EmployeeServiceImpl;

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

public class EmployeeManagementFrame extends JFrame {

    private final EmployeeService EmployeeService;

    // Form components
    private JTextField txtId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cmbPosition;

    private JTextField txtHourlyRate;
    private JTextField txtSalary;

    private JLabel lblHourlyRate;
    private JLabel lblSalary;

    private JLabel lblGrossSalary;
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

    public EmployeeManagementFrame() {
        EmployeeService = new EmployeeServiceImpl();
        initializeUI();
        loadTableData();
    }

    private void initializeUI() {
        setTitle("Employee Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(createFormPanel());
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
        panel.setPreferredSize(new Dimension(350, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(20);
        txtId.setEditable(false);
        panel.add(txtId, gbc);

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(20);
        panel.add(txtFirstName, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(20);
        panel.add(txtLastName, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        String[] positions = {"Employee", "Payroll Admin", "HR Admin"};
        cmbPosition = new JComboBox<>(positions);
        panel.add(cmbPosition, gbc);

        cmbPosition.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedPosition = (String) cmbPosition.getSelectedItem();

                if ("Employee".equals(selectedPosition)) {
                    lblHourlyRate.setVisible(true);
                    txtHourlyRate.setVisible(true);
                    lblSalary.setVisible(false);
                    txtSalary.setVisible(false);
                    txtSalary.setText("");
                } else if ("Payroll Admin".equals(selectedPosition) || "HR Admin".equals(selectedPosition)) {
                    lblHourlyRate.setVisible(false);
                    txtHourlyRate.setVisible(false);
                    txtHourlyRate.setText("");
                    lblSalary.setVisible(true);
                    txtSalary.setVisible(true);
                }
                updateSalaryLabels();
            }
        });

        // Hourly Rate
        gbc.gridx = 0;
        gbc.gridy = 6;
        lblHourlyRate = new JLabel("Hourly Rate:");
        panel.add(lblHourlyRate, gbc);
        gbc.gridx = 1;
        txtHourlyRate = new JTextField(20);
        panel.add(txtHourlyRate, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy = 7;
        lblSalary = new JLabel("Basic Salary:");
        lblSalary.setVisible(false);
        panel.add(lblSalary, gbc);
        gbc.gridx = 1;
        txtSalary = new JTextField(20);
        txtSalary.setVisible(false);
        panel.add(txtSalary, gbc);

        return panel;
    }

    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Salary Details"));
        panel.setPreferredSize(new Dimension(350, 150));

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

        // Deductions label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Deductions:"), gbc);
        gbc.gridx = 1;
        lblDeductions = new JLabel("₱ 0.00");
        panel.add(lblDeductions, gbc);

        // Net Salary label
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Position", "Hourly Rate", "Monthly Salary"};
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

    private void createEmployee() {
        try {
            Employee employee = getEmployeeFromForm();
            employee.setId(0);

            if (EmployeeService.create(employee)) {
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

            if (EmployeeService.update(employee)) {
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

                if (EmployeeService.delete(id)) {
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
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtFirstName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtLastName.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 4).toString());
            cmbPosition.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
            txtHourlyRate.setText(tableModel.getValueAt(selectedRow, 6).toString());
            txtSalary.setText(tableModel.getValueAt(selectedRow, 7).toString());

            switch (cmbPosition.getSelectedItem().toString()) {
                case "Employee":
                    lblHourlyRate.setVisible(true);
                    txtHourlyRate.setVisible(true);
                    lblSalary.setVisible(false);
                    txtSalary.setVisible(false);
                    break;
                case "Payroll Admin":
                case "HR Admin":
                    lblHourlyRate.setVisible(false);
                    txtHourlyRate.setVisible(false);
                    lblSalary.setVisible(true);
                    txtSalary.setVisible(true);
                    break;
            }
            updateSalaryLabels();
        }
    }

    private void loadTableData() {
        List<Employee> employees = EmployeeService.getAll();
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
                    emp.getPosition()
            ));

            switch (emp.getPosition()) {
                case "Employee":
                    model.RegularEmployee regularEmp = (model.RegularEmployee) emp;
                    row.add(String.format("%.2f", regularEmp.getHourlyRate()));
                    row.add("");
                    break;
                case "Payroll Admin":
                case "HR Admin":
                    model.SalariedEmployee salariedEmp = (model.SalariedEmployee) emp;
                    row.add("");
                    row.add(String.format("%.2f", salariedEmp.getBasicSalary()));
                    break;
            }
            tableModel.addRow(row.toArray());
        }
    }

    private Employee getEmployeeFromForm() {
        int id = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String position = (cmbPosition.getSelectedItem() == null) ? "" : cmbPosition.getSelectedItem().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new IllegalArgumentException("First name and last name are required!");
        }

        switch (position) {
            case "Employee":
                double hourlyRate = Double.parseDouble(txtHourlyRate.getText().isEmpty() ? "0" : txtHourlyRate.getText().trim());
                return new model.RegularEmployee(id, firstName, lastName, email, phone, position, hourlyRate);
            case "Payroll Admin":
            case "HR Admin":
                double monthlySalary = Double.parseDouble(txtSalary.getText().isEmpty() ? "0" : txtSalary.getText().trim());
                return new model.SalariedEmployee(id, firstName, lastName, email, phone, position, monthlySalary);
            default:
                throw new IllegalArgumentException("Invalid position! Must be 'Employee', 'Payroll Admin', or 'HR Admin'.");
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        cmbPosition.setSelectedIndex(0);

        txtHourlyRate.setText("");
        txtSalary.setText("");

        lblGrossSalary.setText("₱ 0.00");
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
        double gross = 0.0;
        try {
            String pos = (cmbPosition.getSelectedItem() == null) ? "" : cmbPosition.getSelectedItem().toString();
            if ("Employee".equals(pos)) {
                double hourly = Double.parseDouble(txtHourlyRate.getText().isEmpty() ? "0" : txtHourlyRate.getText().trim());
                gross = hourly * 160;
            } else if ("Payroll Admin".equals(pos) || "HR Admin".equals(pos)) {
                gross = Double.parseDouble(txtSalary.getText().isEmpty() ? "0" : txtSalary.getText().trim());
            }
        } catch (NumberFormatException ex) {
            gross = 0.0;
        }

        double deductions = gross * 0.10;
        double net = gross - deductions;

        lblGrossSalary.setText(String.format("₱ %.2f", gross));
        lblDeductions.setText(String.format("₱ %.2f", deductions));
        lblNetSalary.setText(String.format("₱ %.2f", net));
    }
}
