package appmanager;

import java.io.Serializable;

public class SaleItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Record record;
    private int quantity;
    private double subtotal;
    
    public SaleItem(Record record, int quantity) {
        this.record = record;
        this.quantity = quantity;
        this.subtotal = record.getPrice() * quantity;
    }
    
    public Record getRecord() { return record; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
    
    @Override
    public String toString() {
        return String.format("%s x%d - $%.2f", record.getTitle(), quantity, subtotal);
    }
}
