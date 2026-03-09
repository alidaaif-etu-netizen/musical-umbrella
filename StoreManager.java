package appmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreManager {
    private static final String DATA_FILE = "store_data.ser";

    private static class StoreData implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Record> records;
        private final List<Customer> customers;
        private final List<Sale> sales;

        StoreData(List<Record> records, List<Customer> customers, List<Sale> sales) {
            this.records = records;
            this.customers = customers;
            this.sales = sales;
        }
    }

    private List<Record> records;
    private List<Customer> customers;
    private List<Sale> sales;
    
    public StoreManager() {
        records = new ArrayList<>();
        customers = new ArrayList<>();
        sales = new ArrayList<>();
    }
    
    // Record management
    public void addRecord(Record record) {
        records.add(record);
    }
    
    public boolean removeRecord(String id) {
        return records.removeIf(r -> r.getId().equals(id));
    }
    
    public Record findRecordById(String id) {
        return records.stream()
                     .filter(r -> r.getId().equals(id))
                     .findFirst()
                     .orElse(null);
    }
    
    public List<Record> searchRecords(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return records.stream()
                .filter(r -> r.getTitle().toLowerCase().contains(term) ||
                            r.getArtist().toLowerCase().contains(term) ||
                            r.getGenre().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }
    
    public List<Record> getRecords() { return records; }
    
    // Customer management
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
    
    public boolean removeCustomer(String id) {
        return customers.removeIf(c -> c.getId().equals(id));
    }
    
    public Customer findCustomerById(String id) {
        return customers.stream()
                       .filter(c -> c.getId().equals(id))
                       .findFirst()
                       .orElse(null);
    }
    
    public List<Customer> getCustomers() { return customers; }
    
    // Sales management
    public void addSale(Sale sale) {
        sales.add(sale);
    }
    
    public List<Sale> getSales() { return sales; }

    public boolean saveData() {
        StoreData data = new StoreData(records, customers, sales);
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            outputStream.writeObject(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return false;
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(file))) {
            StoreData data = (StoreData) inputStream.readObject();
            records = data.records != null ? data.records : new ArrayList<>();
            customers = data.customers != null ? data.customers : new ArrayList<>();
            sales = data.sales != null ? data.sales : new ArrayList<>();
            return true;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return false;
        }
    }
}
