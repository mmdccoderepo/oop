package service;

import dao.EmployeeDAO;
import model.*;

public class EmployeeService {
    private EmployeeDAO employeeDAO;

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public Employee createEmployee(int id, String firstName,
                                   String lastName, String email, String phone,
                                   String address, String employeeType, String positionLevel, String department,
                                   String sssNumber, String philHealthNumber,
                                   String tin, String pagIbigNumber, double compensation) {
        switch (employeeType) {
            case "Probationary":
                return new Probationary(id, firstName, lastName, email, phone, address,
                        employeeType, positionLevel, department, sssNumber, philHealthNumber,
                        tin, pagIbigNumber, compensation);
            case "Regular":
                switch (department) {
                    case "HR":
                        return new HR(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    case "Finance":
                        return new Finance(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    case "IT":
                        return new IT(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, department, sssNumber, philHealthNumber,
                                tin, pagIbigNumber, compensation);
                    default:
                        return new Regular(id, firstName, lastName, email, phone, address,
                                employeeType, positionLevel, department, sssNumber, philHealthNumber,
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
                emp.getAddress(), emp.getEmployeeType(), emp.getPositionLevel(), emp.getDepartment(),
                emp.getSssNumber(), emp.getPhilHealthNumber(), emp.getTin(), emp.getPagIbigNumber(),
                emp.getCompensation());
    }
}

