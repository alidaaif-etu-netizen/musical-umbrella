package bankapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String accountHolderName;
    private AccountType accountType;
    protected double balance;

    private List<String> transactionHistory;

    public Account(String accountNumber, String accountHolderName,
                   double initialBalance, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.accountType = accountType;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account opened", initialBalance);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }
    public AccountType getAccountType() { return accountType; }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount");
            return;
        }
        balance += amount;
        addTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount");
            return false;
        }
        if (balance - amount >= 0) {
            balance -= amount;
            addTransaction("Withdrawal", -amount);
            return true;
        } else {
            System.out.println("Insufficient funds");
            return false;
        }
    }

    public boolean transfer(Account destination, double amount) {
        if (destination == null || amount <= 0) {
            return false;
        }
        if (this.withdraw(amount)) {
            destination.deposit(amount);
            return true;
        }
        return false;
    }

    public void applyMonthlyFee() {
        // Default: no monthly fee
    }

    protected void addTransaction(String description, double amount) {
        transactionHistory.add(String.format("%s: $%.2f", description, amount));
    }

    public void printStatement() {
        System.out.println("\n=== ACCOUNT STATEMENT ===");
        System.out.println("Account: " + accountNumber);
        System.out.println("Holder: " + accountHolderName);
        System.out.println("Type: " + accountType);
        System.out.println("Balance: $" + String.format("%.2f", balance));
        System.out.println("Transactions:");
        if (transactionHistory.isEmpty()) {
            System.out.println("  No transactions");
        } else {
            for (String transaction : transactionHistory) {
                System.out.println("  " + transaction);
            }
        }
    }
}
