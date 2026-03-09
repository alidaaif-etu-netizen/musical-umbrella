package appmanager;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class RecordTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Title", "Artist", "Genre", "Year", "Price", "Quantity"};
    private final List<Record> records;

    public RecordTableModel(List<Record> records) {
        this.records = records;
    }

    @Override
    public int getRowCount() {
        return records.size();
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
        Record record = records.get(rowIndex);
        switch (columnIndex) {
            case 0: return record.getId();
            case 1: return record.getTitle();
            case 2: return record.getArtist();
            case 3: return record.getGenre();
            case 4: return record.getYear();
            case 5: return record.getPrice();
            case 6: return record.getQuantity();
            default: return null;
        }
    }
}
