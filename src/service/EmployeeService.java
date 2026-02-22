package service;

import model.Employee;
import model.Probationary;
import model.Regular;

public class EmployeeService {
    public static Employee createEmployee(String employeeType, int id, String firstName,
                                          String lastName, String email, String phone,
                                          String address, String positionLevel, String department,
                                          String sssNumber, String philHealthNumber,
                                          String tin, String pagIbigNumber, double compensation) {
        switch (employeeType) {
            case "Probationary":
                return new Probationary(id, firstName, lastName, email, phone, address,
                        employeeType, positionLevel, department, sssNumber, philHealthNumber,
                        tin, pagIbigNumber, compensation);
            case "Regular":
                return new Regular(id, firstName, lastName, email, phone, address,
                        employeeType, positionLevel, department, sssNumber, philHealthNumber,
                        tin, pagIbigNumber, compensation);
            default:
                throw new IllegalArgumentException("Invalid employee type: " + employeeType);
        }
    }
}

