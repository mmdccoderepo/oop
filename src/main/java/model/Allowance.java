package model;

public class Allowance {
    private String positionLevel;
    private String name;
    private double amount;

    public Allowance(String positionLevel, String name, double amount) {
        this.positionLevel = positionLevel;
        this.name = name;
        if (amount < 0) {
            throw new IllegalArgumentException("Allowance amount cannot be negative.");
        }
        this.amount = amount;
    }

    public String getPositionLevel() {
        return positionLevel;
    }

    public void setPositionLevel(String positionLevel) {
        this.positionLevel = positionLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Allowance amount cannot be negative.");
        }
        this.amount = amount;
    }
}
