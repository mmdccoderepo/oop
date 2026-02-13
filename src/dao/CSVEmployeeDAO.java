package dao;

import model.Employee;
import model.Probationary;
import model.Regular;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVEmployeeDAO extends CSVBaseDAO implements EmployeeDAO {
    private final String filePath = getResourceFilePath("employees.csv");

    public CSVEmployeeDAO() {
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
        String[] parts = line.split(",", -1); // -1 to keep empty trailing fields

        try {
            int id = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            String email = parts[3];
            String phoneNumber = parts[4];
            String address = parts[5];
            String employeeType = parts[6];
            String positionLevel = parts[7];
            String department = parts[8];
            String sssNumber = parts[9];
            String philHealthNumber = parts[10];
            String tin = parts[11];
            String pagIbigNumber = parts[12];

            double hourlyRate = parts.length > 13 && !parts[13].isEmpty() ? Double.parseDouble(parts[13]) : 0.0;
            double basicSalary = parts.length > 14 && !parts[14].isEmpty() ? Double.parseDouble(parts[14]) : 0.0;


            Employee employee = null;
            switch (employeeType) {
                case "Probationary":
                    employee = new Probationary(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, department, sssNumber, philHealthNumber, tin, pagIbigNumber, hourlyRate);
                    break;
                case "Regular":
                    employee = new Regular(id, firstName, lastName, email, phoneNumber, address, employeeType, positionLevel, department, sssNumber, philHealthNumber, tin, pagIbigNumber, basicSalary);
                    break;
                default:
                    return null;
            }

            return employee;
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
        String baseInfo = String.format(
                "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getEmployeeType(),
                employee.getPositionLevel(),
                employee.getDepartment(),
                employee.getSssNumber(),
                employee.getPhilHealthNumber(),
                employee.getTin(),
                employee.getPagIbigNumber()
        );

        String specificInfo;
        if (employee instanceof Probationary) {
            specificInfo = String.format(",%.2f,", ((Probationary) employee).getHourlyRate());
        } else if (employee instanceof Regular) {
            specificInfo = String.format(",,%.2f", ((Regular) employee).getBasicSalary());
        } else {
            specificInfo = ",,0.00";
        }

        return baseInfo + specificInfo;
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
}
