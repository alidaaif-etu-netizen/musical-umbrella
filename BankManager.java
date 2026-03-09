// BankManager.java


import java.io.*;
import java.util.*;

import bankapp.Account;
import bankapp.SavingsAccount;

public class BankManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Account> accounts;
    private transient Scanner scanner;
    private static final String DATA_FILE = "bank_data.ser";

    public BankManager() {
        this.accounts = new HashMap<>();
        this.scanner = new Scanner(System.in);
        loadData();
    }

    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginToAccount();
                    break;
                case 3:
                    listAllAccounts();
                    break;
                case 4:
                    saveData();
                    System.out.println("Thank you for using the Bank Management System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== BANK MANAGEMENT SYSTEM ===");
        System.out.println("1. Create New Account");
        System.out.println("2. Login to Account");
        System.out.println("3. List All Accounts");
        System.out.println("4. Exit");
    }

    private void createAccount() {
        System.out.println("\n=== CREATE NEW ACCOUNT ===");
        System.out.println("Select Account Type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.println("3. Business Account");
        
        int type = getIntInput("Enter choice: ");
        
        String accountNumber = generateAccountNumber();
        System.out.println("Generated Account Number: " + accountNumber);
        
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine();
        
        double initialDeposit = getDoubleInput("Enter Initial Deposit: $");
        
        Account account = null;
        
        switch (type) {
            case 1: // Savings
                double interestRate = getDoubleInput("Enter Interest Rate (%): ") / 100;
                account = new SavingsAccount(accountNumber, name, initialDeposit, interestRate);
                break;
            case 2: // Checking
                double overdraft = getDoubleInput("Enter Overdraft Limit: $");
                double monthlyFee = getDoubleInput("Enter Monthly Fee: $");
                account = new CheckingAccount(accountNumber, name, initialDeposit, overdraft, monthlyFee);
                break;
            case 3: // Business
                double businessOverdraft = getDoubleInput("Enter Business Overdraft Limit: $");
                account = new BusinessAccount(accountNumber, name, initialDeposit, businessOverdraft);
                break;
        }
        
        if (account != null) {
            accounts.put(accountNumber, account);
            System.out.println("Account created successfully!");
            saveData();
        }
    }

    private void loginToAccount() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found!");
            return;
        }
        
        accountMenu(account);
    }

    private void accountMenu(Account account) {
        while (true) {
            System.out.println("\n=== ACCOUNT MENU ===");
            System.out.println("Account: " + account.getAccountNumber());
            System.out.println("Holder: " + account.getAccountHolderName());
            System.out.println("Balance: $" + String.format("%.2f", account.getBalance()));
            System.out.println("\n1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View Statement");
            System.out.println("5. Apply Monthly Fee (Admin)");
            System.out.println("6. Logout");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    double depositAmount = getDoubleInput("Enter deposit amount: $");
                    account.deposit(depositAmount);
                    saveData();
                    break;
                case 2:
                    double withdrawAmount = getDoubleInput("Enter withdrawal amount: $");
                    account.withdraw(withdrawAmount);
                    saveData();
                    break;
                case 3:
                    transferMoney(account);
                    break;
                case 4:
                    account.printStatement();
                    break;
                case 5:
                    account.applyMonthlyFee();
                    System.out.println("Monthly fee applied");
                    saveData();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private void transferMoney(Account source) {
        System.out.print("Enter destination account number: ");
        String destNumber = scanner.nextLine();
        
        Account destination = accounts.get(destNumber);
        if (destination == null) {
            System.out.println("Destination account not found!");
            return;
        }
        
        double amount = getDoubleInput("Enter transfer amount: $");
        
        if (source.transfer(destination, amount)) {
            System.out.println("Transfer successful!");
            saveData();
        } else {
            System.out.println("Transfer failed!");
        }
    }

    private void listAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts in the system.");
            return;
        }
        
        System.out.println("\n=== ALL ACCOUNTS ===");
        for (Account acc : accounts.values()) {
            System.out.printf("Number: %s | Holder: %s | Type: %s | Balance: $%.2f%n",
                    acc.getAccountNumber(), acc.getAccountHolderName(),
                    acc.getAccountType(), acc.getBalance());
        }
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() % 10000;
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                accounts = (Map<String, Account>) ois.readObject();
                System.out.println("Data loaded successfully. Found " + accounts.size() + " accounts.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading data: " + e.getMessage());
                accounts = new HashMap<>();
            }
        }
    }
}