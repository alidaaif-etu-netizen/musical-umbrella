// BusinessAccount.java

import bankapp.Account;

public class BusinessAccount extends Account {
    private static final long serialVersionUID = 1L;
    private double overdraftLimit;
    private double transactionFee;
    private int freeTransactions;
    private int transactionCount;

    public BusinessAccount(String accountNumber, String accountHolderName, 
                          double initialBalance, double overdraftLimit) {
        super(accountNumber, accountHolderName, initialBalance, AccountType.BUSINESS);
        this.overdraftLimit = overdraftLimit;
        this.transactionFee = 0.50;
        this.freeTransactions = 100;
        this.transactionCount = 0;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount");
            return false;
        }
        
        double totalAmount = amount;
        if (transactionCount >= freeTransactions) {
            totalAmount += transactionFee;
        }
        
        if (balance - totalAmount >= -overdraftLimit) {
            balance -= totalAmount;
            transactionCount++;
            addTransaction("Business Withdrawal", -amount);
            return true;
        } else {
            System.out.println("Overdraft limit exceeded");
            return false;
        }
    }

    @Override
    public void applyMonthlyFee() {
        // Reset transaction count at month end
        transactionCount = 0;
    }

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
        transactionCount++;
    }
}