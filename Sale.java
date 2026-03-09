package appmanager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    private String saleId;
    private Customer customer;
    private List<SaleItem> items;
    private LocalDateTime date;
    private double total;
    
    public Sale(String saleId, Customer customer) {
        this.saleId = saleId;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.date = LocalDateTime.now();
        this.total = 0.0;
    }
    
    public void addItem(Record record, int quantity) {
        SaleItem item = new SaleItem(record, quantity);
        items.add(item);
        calculateTotal();
    }
    
    private void calculateTotal() {
        total = items.stream()
                    .mapToDouble(SaleItem::getSubtotal)
                    .sum();
    }
    
    public String getSaleId() { return saleId; }
    public Customer getCustomer() { return customer; }
    public List<SaleItem> getItems() { return items; }
    public LocalDateTime getDate() { return date; }
    public double getTotal() { return total; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Sale ID: ").append(saleId).append("\n");
        sb.append("Date: ").append(date.format(formatter)).append("\n");
        sb.append("Customer: ").append(customer.getName()).append("\n");
        sb.append("Items:\n");
        for (SaleItem item : items) {
            sb.append("  - ").append(item).append("\n");
        }
        sb.append(String.format("Total: $%.2f", total));
        return sb.toString();
    }
}
