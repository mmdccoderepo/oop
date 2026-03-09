package dao;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVEmployeeDAO extends CSVBaseDAO implements EmployeeDAO {
    // use the updated source file with detailed employee information
    private final String filePath = getResourceFilePath("EmployeeDetails.csv");

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
        // skip header row (starts with "Employee")
        if (line.trim().startsWith("\"Employee")) {
            return null;
        }

        // split on commas not inside quotes
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        // strip surrounding quotes
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("^\"|\"$", "");
        }

        try {
            int id = Integer.parseInt(parts[0]);
            String lastName = parts.length > 1 ? parts[1] : "";
            String firstName = parts.length > 2 ? parts[2] : "";
            String address = parts.length > 4 ? parts[4] : "";
            String phoneNumber = parts.length > 5 ? parts[5] : "";
            String sssNumber = parts.length > 6 ? parts[6] : "";
            String philHealthNumber = parts.length > 7 ? parts[7] : "";
            String tin = parts.length > 8 ? parts[8] : "";
            String pagIbigNumber = parts.length > 9 ? parts[9] : "";
            String employeeType = parts.length > 10 ? parts[10] : ""; // Status
            String positionLevel = parts.length > 11 ? parts[11] : ""; // Position
            String department = deriveDepartment(positionLevel);

            double compensation = 0.0;
            if (parts.length > 17 && !parts[17].isEmpty()) {
                try {
                    compensation = Double.parseDouble(parts[17]);
                } catch (NumberFormatException ignored) {}
            }

            Employee employee;
            switch (employeeType) {
                case "Probationary":
                    employee = new Probationary(id, firstName, lastName, "", phoneNumber, address,
                            employeeType, positionLevel, department, sssNumber, philHealthNumber,
                            tin, pagIbigNumber, compensation);
                    break;
                case "Regular":
                    switch (department) {
                        case "HR":
                            employee = new HR(id, firstName, lastName, "", phoneNumber, address,
                                    employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        case "Finance":
                            employee = new Finance(id, firstName, lastName, "", phoneNumber, address,
                                    employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        case "IT":
                            employee = new IT(id, firstName, lastName, "", phoneNumber, address,
                                    employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        default:
                            employee = new Regular(id, firstName, lastName, "", phoneNumber, address,
                                    employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                    }
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
            // include header for EmployeeDetails format
            writer.write(getHeader());
            writer.newLine();
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
        // produce minimal output in same column order as details file
        return String.format(
                "\"%d\",\"%s\",\"%s\",\"\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"\",\"\",\"\",\"%.2f\",\"\"",
                employee.getId(),
                employee.getLastName(),
                employee.getFirstName(),
                employee.getAddress(),
                employee.getPhoneNumber(),
                employee.getSssNumber(),
                employee.getPhilHealthNumber(),
                employee.getTin(),
                employee.getPagIbigNumber(),
                employee.getEmployeeType(),
                employee.getPositionLevel(),
                employee.getDepartment(),
                employee.getCompensation());
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

    // help derive a simple department identifier from position/title
    private String deriveDepartment(String position) {
        if (position == null) {
            return "";
        }
        String lower = position.toLowerCase();
        if (lower.contains("hr")) {
            return "HR";
        }
        if (lower.contains("finance") || lower.contains("account")) {
            return "Finance";
        }
        if (lower.contains("it")) {
            return "IT";
        }
        return "";
    }

    private String getHeader() {
        return "\"Employee #\",\"Last Name\",\"First Name\",\"Birthday\",\"Address\",\"Phone Number\",\"SSS #\",\"Philhealth #\",\"TIN #\",\"Pag-ibig #\",\"Status\",\"Position\",\"Immediate Supervisor\",\"Basic Salary\",\"Rice Subsidy\",\"Phone Allowance\",\"Clothing Allowance\",\"Gross Semi-monthly Rate\",\"Hourly Rate\"";
    }}
