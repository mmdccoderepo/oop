package ui;

import dao.*;
import model.Employee;
import service.AuthenticationService;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private EmployeeDAO employeeDAO;
    private AllowanceDAO allowanceDAO;
    private DeductionDAO deductionDAO;
    private TaxDAO taxDAO;
    private AttendanceLogDAO attendanceLogDAO;
    private LeaveDAO leaveDAO;
    private AuthenticationService authService;

    public LoginWindow(EmployeeDAO employeeDAO, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO,
                       TaxDAO taxDAO, AttendanceLogDAO attendanceLogDAO, LeaveDAO leaveDAO) {
        this.employeeDAO = employeeDAO;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
        this.taxDAO = taxDAO;
        this.attendanceLogDAO = attendanceLogDAO;
        this.leaveDAO = leaveDAO;
        this.authService = new AuthenticationService(new CSVUserAccountDAO());

        initializeUI();
    }

    private void initializeUI() {
        setTitle("MotorPH Payroll System - Login");
        setSize(400, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("MotorPH Payroll System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // Username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setPreferredSize(new Dimension(80, 25));
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(200, 25));
        usernamePanel.add(lblUsername);
        usernamePanel.add(txtUsername);
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createVerticalStrut(5));

        // Password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setPreferredSize(new Dimension(80, 25));
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 25));
        passwordPanel.add(lblPassword);
        passwordPanel.add(txtPassword);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Login button
        btnLogin = new JButton("Log In");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setPreferredSize(new Dimension(120, 30));
        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        int employeeId = authService.authenticate(username, password);
        if (employeeId < 0) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee employee = employeeDAO.read(employeeId);
        if (employee == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee account not found.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.dispose();

        SwingUtilities.invokeLater(() -> {
            TimeInOutWindow timeInOutWindow = new TimeInOutWindow(
                    employeeDAO, allowanceDAO, deductionDAO, taxDAO, attendanceLogDAO, leaveDAO, employee);
            timeInOutWindow.setVisible(true);
        });
    }
}
