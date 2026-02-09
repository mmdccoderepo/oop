package dao;

import model.Employee;

import java.util.List;

public interface EmployeeDAO {
    boolean create(Employee employee);

    Employee read(int id);

    boolean update(Employee employee);

    boolean delete(int id);

    List<Employee> getAll();
}
