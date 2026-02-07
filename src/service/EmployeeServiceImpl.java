package service;

import interfaces.EmployeeService;
import model.Employee;
import model.RegularEmployee;
import model.SalariedEmployee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final String filePath = getResourceFilePath("employees.csv");

    public EmployeeServiceImpl() {
    }

    @Override
    public boolean create(Employee employee) {
        int nextId = getMaxId() + 1;

        try {
            if (employee.getId() == 0) {
                employee.setId(nextId);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(employeeToCsv(employee));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Employee read(int id) {
        List<Employee> employees = getAll();

        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }

        return null;
    }

    @Override
    public boolean update(Employee employee) {
        List<Employee> employees = getAll();
        boolean found = false;

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == employee.getId()) {
                employees.set(i, employee);
                found = true;
                break;
            }
        }

        if (found) {
            return writeAll(employees);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        List<Employee> employees = getAll();
        boolean removed = false;

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
                employees.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            return writeAll(employees);
        }
        return false;
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return employees;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Employee employee = csvToEmployee(line);
                if (employee != null) {
                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }

    private Employee csvToEmployee(String line) {
        String[] parts = line.split(",");

        try {
            int id = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            String email = parts[3];
            String phoneNumber = parts[4];
            String position = parts[5];
            double hourlyRate = Double.parseDouble(parts[6].isEmpty() ? "0" : parts[6]);
            double hoursWorked = Double.parseDouble(parts[7].isEmpty() ? "0" : parts[7]);
            double monthlySalary = 0.0;

            if (parts.length > 8) {
                monthlySalary = Double.parseDouble(parts[8]);
            }

            switch (position) {
                case "Employee":
                    return new model.RegularEmployee(id, firstName, lastName, email, phoneNumber, position, hourlyRate, hoursWorked);
                case "Payroll Admin":
                case "HR Admin":
                    return new model.SalariedEmployee(id, firstName, lastName, email, phoneNumber, position, monthlySalary);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean writeAll(List<Employee> employees) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Employee employee : employees) {
                writer.write(employeeToCsv(employee));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String employeeToCsv(Employee employee) {
        if (employee instanceof RegularEmployee) {
            RegularEmployee regularEmployee = (RegularEmployee) employee;
            return String.format(
                    "%d,%s,%s,%s,%s,%s,%.2f,%.2f",
                    regularEmployee.getId(),
                    regularEmployee.getFirstName(),
                    regularEmployee.getLastName(),
                    regularEmployee.getEmail(),
                    regularEmployee.getPhoneNumber(),
                    regularEmployee.getPosition(),
                    regularEmployee.getHourlyRate(),
                    regularEmployee.getHoursWorked()
            );
        } else if (employee instanceof SalariedEmployee) {
            SalariedEmployee salariedEmployee = (SalariedEmployee) employee;
            return String.format(
                    "%d,%s,%s,%s,%s,%s,,,%.2f",
                    salariedEmployee.getId(),
                    salariedEmployee.getFirstName(),
                    salariedEmployee.getLastName(),
                    salariedEmployee.getEmail(),
                    salariedEmployee.getPhoneNumber(),
                    salariedEmployee.getPosition(),
                    salariedEmployee.getMonthlySalary()
            );
        }
        return "";
    }

    private int getMaxId() {
        List<Employee> employees = getAll();
        int maxId = 0;

        for (Employee employee : employees) {
            if (employee.getId() > maxId) {
                maxId = employee.getId();
            }
        }

        return maxId;
    }

    private String getResourceFilePath(String fileName) {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "src" + File.separator + "resources" + File.separator + fileName;
    }
}
