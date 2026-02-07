package interfaces;

import model.Employee;

import java.util.List;

public interface EmployeeService {
    boolean create(Employee employee);

    Employee read(int id);

    boolean update(Employee employee);

    boolean delete(int id);

    List<Employee> getAll();
}
