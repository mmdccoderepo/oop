package model;

public class Deduction {
    private String name;
    private double salaryMin;
    private double salaryMax;
    private double employeeShare;
    private double employerShare;

    public Deduction(String name, double salaryMin, double salaryMax, double employeeShare, double employerShare) {
        this.name = name;
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
        this.salaryMin = salaryMin;
    }

    public double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(double salaryMax) {
        this.salaryMax = salaryMax;
    }

    public double getEmployeeShare() {
        return employeeShare;
    }

    public void setEmployeeShare(double employeeShare) {
        this.employeeShare = employeeShare;
    }

    public double getEmployerShare() {
        return employerShare;
    }

    public void setEmployerShare(double employerShare) {
        this.employerShare = employerShare;
    }
}
