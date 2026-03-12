package dao;

import model.*;

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
        if (line.trim().isEmpty()) {
            return null;
        }
        // skip header row
        if (line.startsWith("id,")) {
            return null;
        }

        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("^\"|\"$", "").trim();
        }

        if (parts.length < 14) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String firstName = parts[1];
            String lastName = parts[2];
            String email = parts[3];
            String phoneNumber = parts[4];
            String address = parts[5];
            String employeeType = parts[6];
            String positionLevel = parts[7];
            String role = parts[8];
            String sssNumber = parts[9];
            String philHealthNumber = parts[10];
            String tin = parts[11];
            String pagIbigNumber = parts[12];

            double compensation = 0.0;
            try {
                compensation = Double.parseDouble(parts[13].trim());
            } catch (NumberFormatException ignored) {
            }

            Employee employee;
            switch (employeeType) {
                case "Probationary":
                    employee = new Probationary(id, firstName, lastName, email, phoneNumber, address,
                            employeeType, positionLevel, role, sssNumber, philHealthNumber,
                            tin, pagIbigNumber, compensation);
                    break;
                case "Regular":
                    switch (role) {
                        case "HR":
                            employee = new HR(id, firstName, lastName, email, phoneNumber, address,
                                    employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        case "Finance":
                            employee = new Finance(id, firstName, lastName, email, phoneNumber, address,
                                    employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        case "IT":
                            employee = new IT(id, firstName, lastName, email, phoneNumber, address,
                                    employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                    tin, pagIbigNumber, compensation);
                            break;
                        default:
                            employee = new Regular(id, firstName, lastName, email, phoneNumber, address,
                                    employeeType, positionLevel, role, sssNumber, philHealthNumber,
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
            writer.write("id,firstName,lastName,email,phoneNumber,address,employeeType,positionLevel,role,sssNumber,philHealthNumber,tin,pagIbigNumber,compensation");
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
        String address = employee.getAddress().contains(",")
                ? "\"" + employee.getAddress() + "\""
                : employee.getAddress();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f",
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                address,
                employee.getEmployeeType(),
                employee.getPositionLevel(),
                employee.getRole(),
                employee.getSssNumber(),
                employee.getPhilHealthNumber(),
                employee.getTin(),
                employee.getPagIbigNumber(),
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
}
