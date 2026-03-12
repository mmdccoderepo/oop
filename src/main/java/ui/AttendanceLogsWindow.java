package ui;

import dao.AttendanceLogDAO;
import dao.EmployeeDAO;
import model.AttendanceLog;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceLogsWindow extends JFrame {

    private final AttendanceLogDAO attendanceLogDAO;
    private final EmployeeDAO employeeDAO;
    private final Employee currentEmployee;

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalHours;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public AttendanceLogsWindow(Frame parent, AttendanceLogDAO attendanceLogDAO,
                                EmployeeDAO employeeDAO, Employee currentEmployee) {
        this.attendanceLogDAO = attendanceLogDAO;
        this.employeeDAO = employeeDAO;
        this.currentEmployee = currentEmployee;

        initializeUI();
        loadAttendanceLogs();
    }

    private void initializeUI() {
        setTitle("Attendance Logs");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        add(createTopBar(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel title = new JLabel("Attendance Logs");
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblEmployee = new JLabel("Employee: " + currentEmployee.getFirstName() + " " +
                currentEmployee.getLastName() + " (ID: " + currentEmployee.getId() + ")");
        lblEmployee.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(title, BorderLayout.WEST);
        panel.add(lblEmployee, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        String[] columns = {"Date", "Time In", "Time Out", "Hours Worked"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(24);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 12));
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        lblTotalHours = new JLabel("Total Hours Worked: 0 hrs");
        lblTotalHours.setFont(new Font("Arial", Font.BOLD, 13));
        summaryPanel.add(lblTotalHours);

        panel.add(summaryPanel, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadAttendanceLogs());

        buttonPanel.add(btnRefresh);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void loadAttendanceLogs() {
        tableModel.setRowCount(0);

        List<AttendanceLog> logs = attendanceLogDAO.getByEmployeeId(currentEmployee.getId());
        int totalHours = 0;

        for (int i = logs.size() - 1; i >= 0; i--) {
            AttendanceLog log = logs.get(i);
            int hours = log.getHoursWorked();
            totalHours += hours;

            tableModel.addRow(new Object[]{
                    log.getDate().format(DATE_FMT),
                    log.getTimeIn().format(TIME_FMT),
                    log.getTimeOut().format(TIME_FMT),
                    hours + " hrs"
            });
        }

        lblTotalHours.setText("Total Hours Worked: " + totalHours + " hrs  |  Total Records: " + logs.size());
    }
}

