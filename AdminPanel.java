import javax.swing.*;   // gui compomenets provide kndo aw
import javax.swing.table.*;  // jtable khe manage
import java.awt.*;  // layout managers
import java.awt.event.*;  // ActionListener to handle
import java.util.ArrayList;  //  resizable array used to store

public class AdminPanel extends JPanel {
    private DAO dao;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AdminPanel(DAO dao, CardLayout cardLayout, JPanel mainPanel) {
        this.dao = dao;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        buildAdminPanel();
    }

    private void buildAdminPanel() {
        setLayout(new BorderLayout());

        // ── Top bar ──
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));  // components left side se shuru hongi.
        top.add(new JLabel("Admin Panel"));
        JButton logout = new JButton("Logout");
        top.add(logout);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, MainGUI.LOGIN);
            }
        });
        add(top, BorderLayout.NORTH);

        // ── Tabs ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users",          buildAdminUsersTab());
        tabs.addTab("All Invoices",   buildAdminAllInvoicesTab());
        tabs.addTab("User Invoice",   buildAdminUserInvoiceTab());
        tabs.addTab("Invoice Detail", buildInvoiceDetailTab());
        tabs.addTab("Tax Settings",   buildTaxSettingsTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Users Tab ──────────────────────────────────────
    private JPanel buildAdminUsersTab() {  //screen (panel) bana ke return karega”
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Username"}, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField uField = new JTextField(10);
        JPasswordField pField = new JPasswordField(8);
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
        JButton chgBtn = new JButton("Change Password");
        JButton refBtn = new JButton("Refresh");
        bottom.add(new JLabel("Username:")); bottom.add(uField);
        bottom.add(new JLabel("Password:")); bottom.add(pField);
        bottom.add(addBtn); bottom.add(delBtn);
        bottom.add(chgBtn); bottom.add(refBtn);
        panel.add(bottom, BorderLayout.SOUTH);


        // JTable ko database se refresh (reload)
        Runnable load = () -> {   // "Users ko DB se le kar table me show karna"
            model.setRowCount(0);
            for (String[] r : dao.getAllUsers()) model.addRow(r);
        };
        load.run();

        addBtn.addActionListener(e -> {
            String u = uField.getText().trim(), p = new String(pField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) { showMsg("Username or Password is Empty!", "Error"); return; }
            if (dao.addUser(u, p)) { showMsg("User added!", "Success"); load.run(); }
            else showMsg("this user is already exist!", "Error");
        });

        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showMsg("Select user first!", "Error"); return; }
            if (dao.deleteUser((String) model.getValueAt(row, 1)))  // only the required column value (username) is extracted
            { showMsg("Deleted!", "Success"); load.run(); }
            else showMsg("Error!", "Error");
        });

        chgBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showMsg("Select User First!", "Error"); return; }
            String p = new String(pField.getPassword()).trim();
            if (p.isEmpty()) { showMsg("Enter New Password!", "Error"); return; }
            if (dao.changePassword((String) model.getValueAt(row, 1), p)) showMsg("Password changed!", "Success");
            else showMsg("Error!", "Error");
        });

        refBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load.run();
            }
        });

        return panel;
    }

    // ── All Invoices Tab ───────────────────────────────
    private JPanel buildAdminAllInvoicesTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "User", "Type", "Total", "Date"}, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        for (String[] row : dao.getAllInvoices()) {
            model.addRow(row);
        }

        return panel;
    }

    // ── User Invoice Tab ───────────────────────────────
    private JPanel buildAdminUserInvoiceTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField uField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        top.add(new JLabel("Username:"));
        top.add(uField);
        top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "User", "Type", "Total", "Date"}, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener( new ActionListener ()  {
            public void actionPerformed(ActionEvent e) {
            String u = uField.getText().trim();
            if (u.isEmpty()) { showMsg("Enter Username!", "Error"); return; }
            model.setRowCount(0);
            ArrayList<String[]> rows = dao.getInvoicesByUser(u);
            if (rows.isEmpty()) showMsg("invoice not Found!", "Info");
            for (String[] r : rows) model.addRow(r);
        } });

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



    // ── Tax Settings Tab ───────────────────────────────
    private JPanel buildTaxSettingsTab() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 8));
     panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));


        JTextField taxField  = new JTextField(String.valueOf(TaxSettings.productTaxPercent));
        JTextField discField = new JTextField(String.valueOf(TaxSettings.productDiscountPercent));
        JTextField svcField  = new JTextField(String.valueOf(TaxSettings.serviceCharge));
        JButton saveBtn = new JButton("Save");

        panel.add(new JLabel("Product Tax (%):")); panel.add(taxField);
        panel.add(new JLabel("Product Discount (%):")); panel.add(discField);
        panel.add(new JLabel("Service Charge (Rs):")); panel.add(svcField);
        panel.add(new JLabel("")); panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                TaxSettings.productTaxPercent      = Double.parseDouble(taxField.getText().trim());
                TaxSettings.productDiscountPercent = Double.parseDouble(discField.getText().trim());
                TaxSettings.serviceCharge          = Double.parseDouble(svcField.getText().trim());
                showMsg("Saved!", "Success");
            } catch (NumberFormatException ex) { showMsg("Enter Only NUmbers!", "Error"); }
        });

        return panel;
    }

    // ── Helper ─────────────────────────────────────────
    private void showMsg(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE); //popup dialog
    }
}
