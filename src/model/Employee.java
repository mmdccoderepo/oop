package model;

import interfaces.Payable;

abstract public class Employee implements Payable {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String position;

    public Employee(int id, String firstName, String lastName, String email,
                    String phoneNumber, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.position = position;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPosition() {
        return position;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s %s', position='%s', monthly salary=%.2f}",
                id, getFirstName(), getLastName(), position, calculateMonthlySalary());
    }


    abstract public double calculateMonthlySalary();
}
