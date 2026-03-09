package appmanager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class RecordStoreGUI extends JFrame {
    private StoreManager storeManager;
    private JTabbedPane tabbedPane;
    private JTextArea displayArea;
    
    private JTable recordTable;
    private RecordTableModel recordTableModel;
    
    private JTable customerTable;
    private CustomerTableModel customerTableModel;

    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JLabel totalItemsLabel;
    private JLabel uniqueTitlesLabel;
    private JLabel totalValueLabel;
    
    private JComboBox<Customer> customerCombo;
    private JComboBox<Record> recordCombo;
    private JSpinner quantitySpinner;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private java.util.List<SaleItem> currentSale;
    
    public RecordStoreGUI() {
        storeManager = new StoreManager();
        currentSale = new java.util.ArrayList<>();
        if (!storeManager.loadData()) {
            initializeSampleData();
            storeManager.saveData();
        }
        
        setTitle("Record Store Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Create toolbar
        add(createToolBar(), BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Records", createRecordPanel());
        tabbedPane.addTab("Customers", createCustomerPanel());
        tabbedPane.addTab("Sales", createSalePanel());
        tabbedPane.addTab("Inventory", createInventoryPanel());
        tabbedPane.addTab("Reports", createReportPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create status bar
        add(createStatusBar(), BorderLayout.SOUTH);
        
        // Set window properties
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveStoreData(true));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        
        // Record menu
        JMenu recordMenu = new JMenu("Records");
        JMenuItem addRecordItem = new JMenuItem("Add Record");
        addRecordItem.addActionListener(e -> showAddRecordDialog());
        JMenuItem searchRecordItem = new JMenuItem("Search Records");
        searchRecordItem.addActionListener(e -> showSearchDialog());
        recordMenu.add(addRecordItem);
        recordMenu.add(searchRecordItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(recordMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addRecordBtn = new JButton("Add Record");
        addRecordBtn.addActionListener(e -> showAddRecordDialog());
        toolBar.add(addRecordBtn);
        
        toolBar.addSeparator();
        
        JButton addCustomerBtn = new JButton("Add Customer");
        addCustomerBtn.addActionListener(e -> showAddCustomerDialog());
        toolBar.add(addCustomerBtn);
        
        toolBar.addSeparator();
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAllTables());
        toolBar.add(refreshBtn);

        toolBar.addSeparator();

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> saveStoreData(true));
        toolBar.add(saveBtn);
        
        return toolBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("Ready");
        JLabel recordCount = new JLabel("Records: " + storeManager.getRecords().size() + " | ");
        JLabel customerCount = new JLabel("Customers: " + storeManager.getCustomers().size() + " | ");
        JLabel salesCount = new JLabel("Sales: " + storeManager.getSales().size());
        
        statusBar.add(recordCount);
        statusBar.add(customerCount);
        statusBar.add(salesCount);
        statusBar.add(new JLabel("| "));
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    private JPanel createRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table
        recordTableModel = new RecordTableModel(storeManager.getRecords());
        recordTable = new JTable(recordTableModel);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add right-click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> editSelectedRecord());
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteSelectedRecord());
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        
        recordTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = recordTable.rowAtPoint(e.getPoint());
                    recordTable.setRowSelectionInterval(row, row);
                    popupMenu.show(recordTable, e.getX(), e.getY());
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = recordTable.rowAtPoint(e.getPoint());
                    recordTable.setRowSelectionInterval(row, row);
                    popupMenu.show(recordTable, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(recordTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Record");
        addBtn.addActionListener(e -> showAddRecordDialog());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> recordTableModel.fireTableDataChanged());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table
        customerTableModel = new CustomerTableModel(storeManager.getCustomers());
        customerTable = new JTable(customerTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add right-click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> editSelectedCustomer());
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteSelectedCustomer());
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        
        customerTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = customerTable.rowAtPoint(e.getPoint());
                    customerTable.setRowSelectionInterval(row, row);
                    popupMenu.show(customerTable, e.getX(), e.getY());
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = customerTable.rowAtPoint(e.getPoint());
                    customerTable.setRowSelectionInterval(row, row);
                    popupMenu.show(customerTable, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(e -> showAddCustomerDialog());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> customerTableModel.fireTableDataChanged());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSalePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel - Customer selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Customer:"));
        customerCombo = new JComboBox<>();
        customerCombo.setPreferredSize(new Dimension(200, 25));
        topPanel.add(customerCombo);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Product selection and cart
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Left panel - Product selection
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Add Items"));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Record:"));
        recordCombo = new JComboBox<>();
        recordCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Record) {
                    Record record = (Record) value;
                    value = record.getArtist() + " - " + record.getTitle() + 
                            " ($" + record.getPrice() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, 
                        isSelected, cellHasFocus);
            }
        });
        inputPanel.add(recordCombo);
        
        inputPanel.add(new JLabel("Quantity:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(quantitySpinner);
        
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.addActionListener(e -> addToCart());
        inputPanel.add(new JLabel());
        inputPanel.add(addToCartBtn);
        
        selectionPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Right panel - Cart
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        JPanel cartButtonPanel = new JPanel();
        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> removeFromCart());
        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.addActionListener(e -> clearCart());
        cartButtonPanel.add(removeBtn);
        cartButtonPanel.add(clearBtn);
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(selectionPanel);
        centerPanel.add(cartPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Total and checkout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Checkout"));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        
        JButton checkoutBtn = new JButton("Complete Sale");
        checkoutBtn.setBackground(new Color(76, 175, 80));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> completeSale());
        bottomPanel.add(checkoutBtn, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Load data into combos
        refreshSaleCombos();
        
        return panel;
    }
    
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Title", "Artist", "Genre", "Year", "Price", "Quantity", "Value"};
        inventoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        totalItemsLabel = new JLabel("", SwingConstants.CENTER);
        uniqueTitlesLabel = new JLabel("", SwingConstants.CENTER);
        totalValueLabel = new JLabel("", SwingConstants.CENTER);

        summaryPanel.add(createInfoBox("Total Items", totalItemsLabel, Color.BLUE));
        summaryPanel.add(createInfoBox("Unique Titles", uniqueTitlesLabel, Color.GREEN));
        summaryPanel.add(createInfoBox("Total Value", totalValueLabel, Color.RED));
        
        panel.add(summaryPanel, BorderLayout.SOUTH);
        refreshInventoryView();
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create report area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton salesReportBtn = new JButton("Sales Report");
        salesReportBtn.addActionListener(e -> generateSalesReport());
        JButton inventoryReportBtn = new JButton("Inventory Report");
        inventoryReportBtn.addActionListener(e -> generateInventoryReport());
        
        buttonPanel.add(salesReportBtn);
        buttonPanel.add(inventoryReportBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createInfoBox(String title, String value, Color color) {
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        return createInfoBox(title, valueLabel, color);
    }

    private JPanel createInfoBox(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        panel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAddRecordDialog() {
        JDialog dialog = new JDialog(this, "Add New Record", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(15);
        JTextField titleField = new JTextField(15);
        JTextField artistField = new JTextField(15);
        JTextField genreField = new JTextField(15);
        JTextField yearField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JTextField quantityField = new JTextField(15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Artist:"), gbc);
        gbc.gridx = 1;
        dialog.add(artistField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1;
        dialog.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        dialog.add(yearField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        dialog.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Record record = new Record(
                    idField.getText(),
                    titleField.getText(),
                    artistField.getText(),
                    genreField.getText(),
                    Integer.parseInt(yearField.getText()),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(quantityField.getText())
                );
                storeManager.addRecord(record);
                saveStoreData(false);
                refreshAllTables();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Record added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding record: " + ex.getMessage());
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(saveButton, gbc);
        gbc.gridx = 1;
        dialog.add(cancelButton, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            Customer customer = new Customer(
                idField.getText(),
                nameField.getText(),
                emailField.getText(),
                phoneField.getText()
            );
            storeManager.addCustomer(customer);
            saveStoreData(false);
            refreshAllTables();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(saveButton, gbc);
        gbc.gridx = 1;
        dialog.add(cancelButton, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showSearchDialog() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            java.util.List<Record> results = storeManager.searchRecords(searchTerm);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No records found.");
            } else {
                StringBuilder sb = new StringBuilder("Search Results:\n\n");
                for (Record r : results) {
                    sb.append(r.toString()).append("\n");
                }
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showAboutDialog() {
        String message = "Record Store Management System\n\n" +
                        "Version 1.0\n\n" +
                        "A comprehensive solution for managing\n" +
                        "your record store inventory, customers, and sales.\n\n" +
                        "© 2024 All Rights Reserved";
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editSelectedRecord() {
        int row = recordTable.getSelectedRow();
        if (row >= 0) {
            Record record = storeManager.getRecords().get(row);
            showEditRecordDialog(record);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to edit.");
        }
    }
    
    private void showEditRecordDialog(Record record) {
        JDialog dialog = new JDialog(this, "Edit Record", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField titleField = new JTextField(record.getTitle(), 15);
        JTextField artistField = new JTextField(record.getArtist(), 15);
        JTextField genreField = new JTextField(record.getGenre(), 15);
        JTextField yearField = new JTextField(String.valueOf(record.getYear()), 15);
        JTextField priceField = new JTextField(String.valueOf(record.getPrice()), 15);
        JTextField quantityField = new JTextField(String.valueOf(record.getQuantity()), 15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Artist:"), gbc);
        gbc.gridx = 1;
        dialog.add(artistField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1;
        dialog.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        dialog.add(yearField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        dialog.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                record.setTitle(titleField.getText());
                record.setArtist(artistField.getText());
                record.setGenre(genreField.getText());
                record.setYear(Integer.parseInt(yearField.getText()));
                record.setPrice(Double.parseDouble(priceField.getText()));
                record.setQuantity(Integer.parseInt(quantityField.getText()));
                saveStoreData(false);
                refreshAllTables();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Record updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating record: " + ex.getMessage());
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(saveButton, gbc);
        gbc.gridx = 1;
        dialog.add(cancelButton, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedRecord() {
        int row = recordTable.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete this record?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Record record = storeManager.getRecords().get(row);
                storeManager.removeRecord(record.getId());
                saveStoreData(false);
                refreshAllTables();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.");
        }
    }
    
    private void editSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row >= 0) {
            Customer customer = storeManager.getCustomers().get(row);
            showEditCustomerDialog(customer);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
        }
    }
    
    private void showEditCustomerDialog(Customer customer) {
        JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(customer.getName(), 15);
        JTextField emailField = new JTextField(customer.getEmail(), 15);
        JTextField phoneField = new JTextField(customer.getPhone(), 15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            saveStoreData(false);
            refreshAllTables();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(saveButton, gbc);
        gbc.gridx = 1;
        dialog.add(cancelButton, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete this customer?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Customer customer = storeManager.getCustomers().get(row);
                storeManager.removeCustomer(customer.getId());
                saveStoreData(false);
                refreshAllTables();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
        }
    }
    
    private void addToCart() {
        Record selectedRecord = (Record) recordCombo.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();
        
        if (selectedRecord != null) {
            if (selectedRecord.getQuantity() >= quantity) {
                selectedRecord.setQuantity(selectedRecord.getQuantity() - quantity);
                SaleItem item = new SaleItem(selectedRecord, quantity);
                currentSale.add(item);
                cartListModel.addElement(item.toString());
                updateTotal();
                refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Insufficient stock! Available: " + selectedRecord.getQuantity());
            }
        }
    }
    
    private void removeFromCart() {
        int selectedIndex = cartList.getSelectedIndex();
        if (selectedIndex >= 0) {
            SaleItem removedItem = currentSale.remove(selectedIndex);
            Record record = removedItem.getRecord();
            record.setQuantity(record.getQuantity() + removedItem.getQuantity());
            cartListModel.remove(selectedIndex);
            updateTotal();
            refreshAllTables();
        }
    }
    
    private void clearCart() {
        for (SaleItem item : currentSale) {
            Record record = item.getRecord();
            record.setQuantity(record.getQuantity() + item.getQuantity());
        }
        currentSale.clear();
        cartListModel.clear();
        updateTotal();
        refreshAllTables();
    }
    
    private void updateTotal() {
        double total = currentSale.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
    
    private void completeSale() {
        Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
        
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }
        
        if (currentSale.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        
        // Create sale
        Sale sale = new Sale("S" + System.currentTimeMillis(), selectedCustomer);
        for (SaleItem item : currentSale) {
            sale.addItem(item.getRecord(), item.getQuantity());
        }
        
        storeManager.addSale(sale);
        saveStoreData(false);
        
        // Show receipt
        showReceipt(sale);
        
        // Clear cart
        clearCart();
        
        // Refresh tables
        refreshAllTables();
        
        JOptionPane.showMessageDialog(this, "Sale completed successfully!");
    }
    
    private void showReceipt(Sale sale) {
        JDialog dialog = new JDialog(this, "Receipt", true);
        dialog.setLayout(new BorderLayout());
        
        JTextArea receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("         RECORD STORE\n");
        sb.append("================================\n\n");
        sb.append("Sale ID: ").append(sale.getSaleId()).append("\n");
        sb.append("Date: ").append(sale.getDate()).append("\n");
        sb.append("Customer: ").append(sale.getCustomer().getName()).append("\n\n");
        sb.append("Items:\n");
        sb.append("--------------------------------\n");
        
        for (SaleItem item : sale.getItems()) {
            sb.append(String.format("%s\n  %d x $%.2f = $%.2f\n", 
                item.getRecord().getTitle(),
                item.getQuantity(),
                item.getRecord().getPrice(),
                item.getSubtotal()));
        }
        
        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL: $%.2f\n", sale.getTotal()));
        sb.append("================================\n");
        sb.append("Thank you for your purchase!\n");
        
        receiptArea.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeBtn, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshSaleCombos() {
        customerCombo.removeAllItems();
        for (Customer c : storeManager.getCustomers()) {
            customerCombo.addItem(c);
        }
        
        recordCombo.removeAllItems();
        for (Record r : storeManager.getRecords()) {
            if (r.getQuantity() > 0) {
                recordCombo.addItem(r);
            }
        }
    }
    
    private void refreshAllTables() {
        recordTableModel.fireTableDataChanged();
        customerTableModel.fireTableDataChanged();
        refreshSaleCombos();
        refreshInventoryView();
    }

    private void refreshInventoryView() {
        if (inventoryTableModel == null) {
            return;
        }

        inventoryTableModel.setRowCount(0);
        for (Record record : storeManager.getRecords()) {
            inventoryTableModel.addRow(new Object[] {
                record.getId(),
                record.getTitle(),
                record.getArtist(),
                record.getGenre(),
                record.getYear(),
                String.format("$%.2f", record.getPrice()),
                record.getQuantity(),
                String.format("$%.2f", record.getPrice() * record.getQuantity())
            });
        }

        int totalItems = storeManager.getRecords().stream().mapToInt(Record::getQuantity).sum();
        double totalValue = storeManager.getRecords().stream()
                .mapToDouble(record -> record.getPrice() * record.getQuantity()).sum();

        totalItemsLabel.setText(String.valueOf(totalItems));
        uniqueTitlesLabel.setText(String.valueOf(storeManager.getRecords().size()));
        totalValueLabel.setText(String.format("$%.2f", totalValue));
    }

    private void saveStoreData(boolean showMessage) {
        boolean saved = storeManager.saveData();
        if (showMessage) {
            String message = saved ? "Data saved successfully!" : "Failed to save data.";
            int messageType = saved ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(this, message, "Save Data", messageType);
        }
    }

    private void exitApplication() {
        saveStoreData(false);
        dispose();
        System.exit(0);
    }
    
    private void generateSalesReport() {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("            SALES REPORT\n");
        report.append("========================================\n\n");
        
        if (storeManager.getSales().isEmpty()) {
            report.append("No sales recorded.\n");
        } else {
            double totalSales = 0;
            for (Sale sale : storeManager.getSales()) {
                report.append(sale.toString()).append("\n");
                report.append("----------------------------------------\n");
                totalSales += sale.getTotal();
            }
            report.append(String.format("\nTOTAL SALES: $%.2f\n", totalSales));
            report.append("Number of Transactions: ").append(storeManager.getSales().size()).append("\n");
        }
        
        displayArea.setText(report.toString());
    }
    
    private void generateInventoryReport() {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("          INVENTORY REPORT\n");
        report.append("========================================\n\n");
        
        if (storeManager.getRecords().isEmpty()) {
            report.append("No records in inventory.\n");
        } else {
            int totalItems = 0;
            double totalValue = 0;
            
            report.append(String.format("%-10s %-20s %-15s %-10s %-10s\n", 
                    "ID", "Title", "Artist", "Qty", "Value"));
            report.append("--------------------------------------------------------\n");
            
            for (Record r : storeManager.getRecords()) {
                double value = r.getPrice() * r.getQuantity();
                report.append(String.format("%-10s %-20s %-15s %-10d $%-9.2f\n",
                        r.getId(), 
                        truncate(r.getTitle(), 20),
                        truncate(r.getArtist(), 15),
                        r.getQuantity(),
                        value));
                totalItems += r.getQuantity();
                totalValue += value;
            }
            
            report.append("--------------------------------------------------------\n");
            report.append(String.format("Total Items: %d\n", totalItems));
            report.append(String.format("Total Value: $%.2f\n", totalValue));
        }
        
        displayArea.setText(report.toString());
    }
    
    private String truncate(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str;
    }
    
    private void initializeSampleData() {
        // Add sample records
        storeManager.addRecord(new Record("R001", "Dark Side of the Moon", "Pink Floyd", "Rock", 1973, 25.99, 10));
        storeManager.addRecord(new Record("R002", "Thriller", "Michael Jackson", "Pop", 1982, 22.99, 15));
        storeManager.addRecord(new Record("R003", "Back in Black", "AC/DC", "Rock", 1980, 24.99, 8));
        storeManager.addRecord(new Record("R004", "Rumours", "Fleetwood Mac", "Rock", 1977, 23.99, 12));
        storeManager.addRecord(new Record("R005", "Nevermind", "Nirvana", "Grunge", 1991, 21.99, 7));
        storeManager.addRecord(new Record("R006", "The Wall", "Pink Floyd", "Rock", 1979, 29.99, 5));
        storeManager.addRecord(new Record("R007", "Abbey Road", "The Beatles", "Rock", 1969, 26.99, 9));
        storeManager.addRecord(new Record("R008", "Born to Run", "Bruce Springsteen", "Rock", 1975, 19.99, 6));
        
        // Add sample customers
        storeManager.addCustomer(new Customer("C001", "John Doe", "john@email.com", "555-0101"));
        storeManager.addCustomer(new Customer("C002", "Jane Smith", "jane@email.com", "555-0102"));
        storeManager.addCustomer(new Customer("C003", "Bob Johnson", "bob@email.com", "555-0103"));
        storeManager.addCustomer(new Customer("C004", "Alice Brown", "alice@email.com", "555-0104"));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RecordStoreGUI().setVisible(true);
        });
    }
}
