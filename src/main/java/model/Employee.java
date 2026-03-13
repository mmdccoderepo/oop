package model;

import java.util.ArrayList;
import java.util.List;

abstract public class Employee implements Payable {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String employeeType;
    private String positionLevel;
    private String role;
    private String sssNumber;
    private String philHealthNumber;
    private String tin;
    private String pagIbigNumber;

    private int hoursWorked;
    private final List<Allowance> allowances = new ArrayList<>();
    private final List<Deduction> deductions = new ArrayList<>();
    private final List<TaxBracket> taxBrackets = new ArrayList<>();

    public Employee(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                    String employeeType, String positionLevel, String role, String sssNumber,
                    String philHealthNumber, String tin, String pagIbigNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.employeeType = employeeType;
        this.positionLevel = positionLevel;
        this.role = role;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tin = tin;
        this.pagIbigNumber = pagIbigNumber;
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

    public String getAddress() {
        return address;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public String getPositionLevel() {
        return positionLevel;
    }

    public String getRole() {
        return role;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public String getTin() {
        return tin;
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public void setPositionLevel(String positionLevel) {
        this.positionLevel = positionLevel;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = pagIbigNumber;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        if (hoursWorked < 0) {
            throw new IllegalArgumentException("Hours worked cannot be negative.");
        }
        if (hoursWorked > 336) {
            throw new IllegalArgumentException("Hours worked exceeds bi-weekly maximum of 336 hours.");
        }
        this.hoursWorked = hoursWorked;
    }

    public List<Allowance> getAllowances() {
        return allowances;
    }

    public void setAllowances(List<Allowance> allowances) {
        this.allowances.clear();
        if (allowances != null) {
            this.allowances.addAll(allowances);
        }
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<Deduction> deductions) {
        this.deductions.clear();
        if (deductions != null) {
            this.deductions.addAll(deductions);
        }
    }

    public List<TaxBracket> getTaxBrackets() {
        return taxBrackets;
    }

    public void setTaxBrackets(List<TaxBracket> taxBrackets) {
        this.taxBrackets.clear();
        if (taxBrackets != null) {
            this.taxBrackets.addAll(taxBrackets);
        }
    }

    public double computeDeductions() {
        // If no hours worked, no deductions
        if (getHoursWorked() == 0) {
            return 0.0;
        }

        double gross = computeGrossSalary();
        double totalDeductions = 0.0;
        totalDeductions += getDeduction("SSS", gross);
        totalDeductions += getDeduction("PhilHealth", gross);
        totalDeductions += getDeduction("Pag-IBIG", gross);
        totalDeductions += computeTax();
        return totalDeductions;
    }

    private double getDeduction(String deductionType, double gross) {
        List<Deduction> deductions = new ArrayList<>();
        for (Deduction deduction : getDeductions()) {
            if (deductionType.equals(deduction.getName())) {
                deductions.add(deduction);
            }
        }
        if (deductions.isEmpty()) {
            return 0.0;
        }

        Deduction matchingBracket = null;
        Deduction highestBracket = null;

        for (Deduction deduction : deductions) {
            if (highestBracket == null || deduction.getSalaryMax() > highestBracket.getSalaryMax()) {
                highestBracket = deduction;
            }

            if (gross >= deduction.getSalaryMin() && gross <= deduction.getSalaryMax()) {
                if (matchingBracket == null || deduction.getSalaryMin() > matchingBracket.getSalaryMin()) {
                    matchingBracket = deduction;
                }
            }
        }

        if (matchingBracket == null && highestBracket != null && gross > highestBracket.getSalaryMax()) {
            matchingBracket = highestBracket;
        }

        if (matchingBracket == null) {
            return 0.0;
        }

        return matchingBracket.getEmployeeShare();
    }

    public double computeTax() {
        // If no hours worked, no tax
        if (getHoursWorked() == 0) {
            return 0.0;
        }

        double taxableIncome = computeGrossSalary() + computeAllowances();

        if (taxBrackets.isEmpty()) {
            return 0.0;
        }

        TaxBracket matchingBracket = null;
        TaxBracket highestBracket = null;

        for (TaxBracket taxBracket : taxBrackets) {
            if (highestBracket == null || taxBracket.getSalaryMax() > highestBracket.getSalaryMax()) {
                highestBracket = taxBracket;
            }

            if (taxableIncome >= taxBracket.getSalaryMin() && taxableIncome <= taxBracket.getSalaryMax()) {
                matchingBracket = taxBracket;
            }
        }

        if (matchingBracket == null && highestBracket != null && taxableIncome > highestBracket.getSalaryMax()) {
            matchingBracket = highestBracket;
            double excess = taxableIncome - matchingBracket.getSalaryMin();
            return matchingBracket.getBaseAmount() + (excess * matchingBracket.getRate());
        }

        if (matchingBracket == null) {
            return 0.0;
        }

        double excess = taxableIncome - matchingBracket.getSalaryMin();
        return matchingBracket.getBaseAmount() + (excess * matchingBracket.getRate());
    }

    public double computeNetSalary() {
        double gross = computeGrossSalary();
        double deductions = computeDeductions();
        return gross - deductions;
    }
}
