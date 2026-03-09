package appmanager;


import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CustomerTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Name", "Email", "Phone"};
    private List<Customer> customers;
    
    public CustomerTableModel(List<Customer> customers) {
        this.customers = customers;
    }
    
    @Override
    public int getRowCount() {
        return customers.size();
    }
    
    @Override
    public int getColumnCount() {
        return columns.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0: return customer.getId();
            case 1: return customer.getName();
            case 2: return customer.getEmail();
            case 3: return customer.getPhone();
            default: return null;
        }
    }
}