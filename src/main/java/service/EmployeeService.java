package service;

import dao.EmployeeDAO;
import model.*;

import dao.UserAccountDAO;
import dao.CSVUserAccountDAO;
import java.util.Map;
import java.util.List;

public class EmployeeService {
    private EmployeeDAO employeeDAO;

    private UserAccountDAO userAccountDAO = new CSVUserAccountDAO();
    
    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }
    
    
    private void validateEmployee(Employee employee) throws IllegalArgumentException {
        if (employee.getFirstName() == null || employee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (employee.getLastName() == null || employee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!employee.getEmail().matches("^[\\w.+\\-]+@[\\w\\-]+(?:\\.[a-zA-Z]+)*$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (employee.getPhoneNumber() == null || employee.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (!employee.getPhoneNumber().matches("^[\\d\\-\\s()]+$")) {
            throw new IllegalArgumentException("Phone number must contain only digits, dashes, spaces, or parentheses (e.g. 966-860-270).");
        }
        if (employee.getAddress() == null || employee.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (employee.getSssNumber() == null || employee.getSssNumber().isBlank()) {
            throw new IllegalArgumentException("SSS number is required.");
        }
        if (!employee.getSssNumber().matches("^\\d{2}-\\d{7}-\\d$")) {
            throw new IllegalArgumentException("SSS number must be in the format ##-#######-# (e.g. 44-4506057-3).");
        }
        if (employee.getPhilHealthNumber() == null || employee.getPhilHealthNumber().isBlank()) {
            throw new IllegalArgumentException("PhilHealth number is required.");
        }
        if (!employee.getPhilHealthNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("PhilHealth number must be exactly 12 digits (e.g. 820126853951).");
        }
        if (employee.getTin() == null || employee.getTin().isBlank()) {
            throw new IllegalArgumentException("TIN is required.");
        }
        if (!employee.getTin().matches("^\\d{3}-\\d{3}-\\d{3}-\\d{3}$")) {
            throw new IllegalArgumentException("TIN must be in the format ###-###-###-### (e.g. 442-605-657-000).");
        }
        if (employee.getPagIbigNumber() == null || employee.getPagIbigNumber().isBlank()) {
            throw new IllegalArgumentException("Pag-IBIG number is required.");
        }
        if (!employee.getPagIbigNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Pag-IBIG number must be exactly 12 digits (e.g. 691295330870).");
        }
        if (employee.getCompensation() <= 0) {
            throw new IllegalArgumentException("Compensation must be greater than zero.");
        }
    }

    public void addEmployee(Employee employee) throws IllegalArgumentException {
        validateEmployee(employee);
        employee.setId(0);
        if (!employeeDAO.create(employee)) {
            throw new RuntimeException("Failed to save the employee. Please try again.");
        }
       
        String username = employee.getFirstName().toLowerCase() + "." + employee.getLastName().toLowerCase();

    
        Map<String, String> accounts = userAccountDAO.getAll();
        if (accounts.containsKey(username)) {
        username = username + employee.getId(); 
        }

        
        String password = String.valueOf(employee.getId());

        
        userAccountDAO.create(username, password);
        
    }

    public void updateEmployee(Employee employee) throws IllegalArgumentException {
        if (employee.getId() <= 0) {
            throw new IllegalArgumentException("A valid employee must be selected before updating.");
        }
        validateEmployee(employee);
        if (!employeeDAO.update(employee)) {
            throw new RuntimeException("Failed to update the employee. Please try again.");
        }
    }

    public void deleteEmployee(int id) throws IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("A valid employee must be selected before deleting.");
        }
        if (employeeDAO.read(id) == null) {
            throw new IllegalArgumentException("Employee with ID " + id + " does not exist.");
        }
        if (!employeeDAO.delete(id)) {
            throw new RuntimeException("Failed to delete the employee. Please try again.");
        }
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAll();
    }

    public Employee createEmployee(int id, String firstName,
                                   String lastName, String email, String phone,
                                   String address, String employeeType, String positionLevel, String role,
                                   String sssNumber, String philHealthNumber,
                                   String tin, String pagIbigNumber, double compensation) {
        switch (employeeType) {
            case "Probationary":
                return new Probationary(id, firstName, lastName, email, phone, address,
                        employeeType, positionLevel, role, sssNumber, philHealthNumber,
                        tin, pagIbigNumber, compensation);
            case "Regular":
                switch (role) {
                    case "HR":
                        return new HR(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    case "Finance":
                        return new Finance(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    case "IT":
                        return new IT(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    default:
                        return new Regular(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, role, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                }
            default:
                throw new IllegalArgumentException("Invalid employee type: " + employeeType);
        }
    }

    public Employee getEmployeeById(int id) {
        Employee emp = employeeDAO.read(id);

        if (emp == null) {
            throw new IllegalArgumentException("Employee with ID " + id + " not found");
        }

        return createEmployee(emp.getId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getPhoneNumber(),
                emp.getAddress(), emp.getEmployeeType(), emp.getPositionLevel(), emp.getRole(),
                emp.getSssNumber(), emp.getPhilHealthNumber(), emp.getTin(), emp.getPagIbigNumber(),
                emp.getCompensation());
    }
}

