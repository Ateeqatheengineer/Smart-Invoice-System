import javax.swing.*;
import javax.swing.table.*;  //JTable) handle , DefaultTableModel
import java.awt.*;  // layout manager
import java.awt.event.*;  //Events handle
import java.util.ArrayList;

public class UserPanel extends JPanel {

    private DAO dao;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String loggedInUser;

    private ArrayList<InvoiceItem> currentItems = new ArrayList<>();
    private DefaultTableModel itemsModel;
    private DefaultTableModel myInvoicesModel;
    private JLabel subtotalLabel;

    public UserPanel(DAO dao, CardLayout cardLayout, JPanel mainPanel) {
        this.dao = dao;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        buildUserPanel();
    }

    // ── Logged-in user set/get ─────────────────────────
    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        loadMyInvoices();
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    // ── Main User Panel Build ──────────────────────────
    private void buildUserPanel() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("User Panel"));
        JButton logout = new JButton("Logout");
        top.add(logout);
        logout.addActionListener(e -> {
            loggedInUser = "";
            cardLayout.show(mainPanel, MainGUI.LOGIN);
        });
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Create Invoice", buildCreateInvoiceTab());
        tabs.addTab("My Invoices",    buildMyInvoicesTab());
        tabs.addTab("Invoice Detail", buildInvoiceDetailTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Create Invoice Tab ─────────────────────────────
    private JPanel buildCreateInvoiceTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top: customer + type
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField customerField = new JTextField(12);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Product", "Service", "Subscription"});
        top.add(new JLabel("Customer:")); top.add(customerField);
        top.add(new JLabel("Type:"));     top.add(typeBox);
        panel.add(top, BorderLayout.NORTH);

        // Center: items table
        itemsModel = new DefaultTableModel(new String[]{"Item Name", "Qty", "Price", "Subtotal"}, 0); // invoice detail table
        JTable itemsTable = new JTable(itemsModel);
        panel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // Bottom: add item + save
        JPanel bottom = new JPanel(new GridLayout(2, 1));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameF  = new JTextField(10);
        JTextField qtyF   = new JTextField(4);
        JTextField priceF = new JTextField(6);
        subtotalLabel     = new JLabel("Subtotal: Rs 0.00");
        JButton addBtn    = new JButton("Add Item");
        JButton removeBtn = new JButton("Remove");
        addRow.add(new JLabel("Name:"));  addRow.add(nameF);
        addRow.add(new JLabel("Qty:"));   addRow.add(qtyF);
        addRow.add(new JLabel("Price:")); addRow.add(priceF);
        addRow.add(addBtn); addRow.add(removeBtn); addRow.add(subtotalLabel);

        JPanel saveRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Invoice");
        saveRow.add(saveBtn);

        bottom.add(addRow); bottom.add(saveRow);
        panel.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim(), qs = qtyF.getText().trim(), ps = priceF.getText().trim();
            if (name.isEmpty() || qs.isEmpty() || ps.isEmpty()) { showMsg("Enter all Details!", "Error"); return; }
            try {
                int qty = Integer.parseInt(qs);
                double prc = Double.parseDouble(ps);
                if (qty < 1 || prc < 1) { showMsg("Qty or Price must be more than 1!", "Error"); return; }
                InvoiceItem item = new InvoiceItem(name, qty, prc);
                currentItems.add(item);
                itemsModel.addRow(new Object[]{name, qty, prc, item.getSubtotal()});
                updateSubtotal();
                nameF.setText(""); qtyF.setText(""); priceF.setText("");
            } catch (NumberFormatException ex) { showMsg("Qty or Price must be number!", "Error"); }
        });

        removeBtn.addActionListener(e -> {
            int row = itemsTable.getSelectedRow();
            if (row < 0) { showMsg("Selest any item first!", "Error"); return; }
            currentItems.remove(row); itemsModel.removeRow(row); updateSubtotal();
        });

        saveBtn.addActionListener(e -> {
            String customer = customerField.getText().trim();
            if (customer.isEmpty()) { showMsg("Enter Customer Name!", "Error"); return; }
            if (currentItems.isEmpty()) { showMsg("Enter atleast 1 Item!", "Error"); return; }
            String type = (String) typeBox.getSelectedItem();
            Invoice invoice;
            if      (type.equals("Product"))  invoice = new ProductInvoice(customer);
            else if (type.equals("Service"))   invoice = new ServiceInvoice(customer);
            else                               invoice = new SubscriptionInvoice(customer);
            for (InvoiceItem item : currentItems) invoice.addItem(item);
            invoice.display();
            if (dao.saveInvoice(invoice, loggedInUser)) {
                showMsg("Invoice saved!\nTotal: Rs " + invoice.calculateTotal(), "Saved");
                currentItems.clear(); itemsModel.setRowCount(0); updateSubtotal(); customerField.setText("");
                loadMyInvoices();
            } else showMsg("Error , Not Saved!", "Error");
        });

        return panel;
    }

    // ── My Invoices Tab ──────────────────────────────── inside invoice details
    private JPanel buildMyInvoicesTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        myInvoicesModel = new DefaultTableModel(new String[]{"ID", "Type", "Total", "Date"}, 0);
        JTable table = new JTable(myInvoicesModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // ── Invoice Detail Tab ─────────────────────────────
    private JPanel buildInvoiceDetailTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(8);
        JButton searchBtn = new JButton("Search");
        top.add(new JLabel("Invoice ID:")); top.add(idField); top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JLabel idLbl   = new JLabel("ID: -");
        JLabel userLbl = new JLabel("User: -");
        JLabel typeLbl = new JLabel("Type: -");
        JLabel totLbl  = new JLabel("Total: -");
        JLabel dateLbl = new JLabel("Date: -");
        infoRow.add(idLbl); infoRow.add(userLbl); infoRow.add(typeLbl);
        infoRow.add(totLbl); infoRow.add(dateLbl);

        DefaultTableModel detailModel = new DefaultTableModel(
            new String[]{"Item Name", "Quantity", "Price (Rs)", "Subtotal (Rs)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable detailTable = new JTable(detailModel);

        JLabel grandLbl = new JLabel("Grand Total: Rs -");
        grandLbl.setFont(new Font("Arial", Font.BOLD, 13));
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRow.add(grandLbl);

        JPanel center = new JPanel(new BorderLayout(3, 3));
        center.add(infoRow, BorderLayout.NORTH);
        center.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        center.add(bottomRow, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String txt = idField.getText().trim();
            if (txt.isEmpty()) { showMsg("Enter Invoice Id!", "Error"); return; }
            int invoiceId;
            try { invoiceId = Integer.parseInt(txt); }
            catch (NumberFormatException ex) { showMsg("ID should be only numbers!", "Error"); return; }

            String[] info = dao.getInvoiceInfo(invoiceId);
            if (info == null) {
                showMsg("Invoice not found!", "Not Found");
                idLbl.setText("ID: -"); userLbl.setText("User: -"); typeLbl.setText("Type: -");
                totLbl.setText("Total: -"); dateLbl.setText("Date: -");
                detailModel.setRowCount(0); grandLbl.setText("Grand Total: Rs -");
                return;
            }

            idLbl.setText("ID: " + info[0]);     userLbl.setText("User: " + info[1]);
            typeLbl.setText("Type: " + info[2]);  totLbl.setText("Total: Rs " + info[3]);
            dateLbl.setText("Date: " + info[4]);

            detailModel.setRowCount(0);
            double grand = 0;
            for (String[] item : dao.getInvoiceItems(invoiceId)) {
                double sub = Double.parseDouble(item[1]) * Double.parseDouble(item[2]);
                grand += sub;
                detailModel.addRow(new Object[]{item[0], item[1], item[2], String.format("%.2f", sub)});
            }
            grandLbl.setText("Subtotal: Rs " + String.format("%.2f", grand) +
                "   |   Final: Rs " + info[3]);
        });

        return panel;
    }

    // ── Helpers ────────────────────────────────────────
    private void updateSubtotal() {
        double sub = 0;
        for (InvoiceItem i : currentItems) sub += i.getSubtotal();
        subtotalLabel.setText("Subtotal: Rs " + String.format("%.2f", sub));
    }
// loads database for new data  in my invoice tab
    public void loadMyInvoices() {
        if (myInvoicesModel == null || loggedInUser == null || loggedInUser.isEmpty()) return;
        myInvoicesModel.setRowCount(0);
        for (Invoice inv : dao.getUserInvoices(loggedInUser)) {
            SimpleInvoice s = (SimpleInvoice) inv;
            myInvoicesModel.addRow(new Object[]{s.getId(), inv.getInvoiceType(), s.getTotal(), s.getDate()});
        }
    }

    private void showMsg(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
    }
}
