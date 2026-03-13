package model;

public class Deduction {
    private String name;
    private double salaryMin;
    private double salaryMax;
    private double employeeShare;
    private double employerShare;

    public Deduction(String name, double salaryMin, double salaryMax, double employeeShare, double employerShare) {
        this.name = name;
        if (salaryMin < 0) {
            throw new IllegalArgumentException("Salary minimum cannot be negative.");
        }
        if (salaryMax < 0) {
            throw new IllegalArgumentException("Salary maximum cannot be negative.");
        }
        if (salaryMin > salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        if (employeeShare < 0) {
            throw new IllegalArgumentException("Employee share cannot be negative.");
        }
        if (employerShare < 0) {
            throw new IllegalArgumentException("Employer share cannot be negative.");
        }
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.employeeShare = employeeShare;
        this.employerShare = employerShare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(double salaryMin) {
        if (salaryMin < 0) {
            throw new IllegalArgumentException("Salary minimum cannot be negative.");
        }
        if (salaryMin > this.salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        this.salaryMin = salaryMin;
    }

    public double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(double salaryMax) {
        if (salaryMax < 0) {
            throw new IllegalArgumentException("Salary maximum cannot be negative.");
        }
        if (this.salaryMin > salaryMax) {
            throw new IllegalArgumentException("Salary minimum cannot be greater than salary maximum.");
        }
        this.salaryMax = salaryMax;
    }

    public double getEmployeeShare() {
        return employeeShare;
    }

    public void setEmployeeShare(double employeeShare) {
        if (employeeShare < 0) {
            throw new IllegalArgumentException("Employee share cannot be negative.");
        }
        this.employeeShare = employeeShare;
    }

    public double getEmployerShare() {
        return employerShare;
    }

    public void setEmployerShare(double employerShare) {
        if (employerShare < 0) {
            throw new IllegalArgumentException("Employer share cannot be negative.");
        }
        this.employerShare = employerShare;
    }
}
