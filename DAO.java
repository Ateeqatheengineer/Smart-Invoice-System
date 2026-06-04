import java.sql.*;    // MySQL connect ,login check fetch/delete
import java.util.ArrayList;


public class DAO {

    // ─────────────────────────────────────────
    //  USER OPERATIONS
    // ─────────────────────────────────────────

    // Login check: username aur password match karo
    public boolean checkUser(String username, String password) {
        try {
            Connection con = DBConnection.getConnection();   // connection establish ho gaya
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);  // Query ready hai, lekin abhi values insert nahi hui
            ps.setString(1, username); // Final query ban jati hai:
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();   // Database se result aaya
            boolean exists = rs.next();   // first row mil gayi
            con.close();
            return exists;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }


// admin check krega user ky invoices

    public boolean checkUser(String username) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            con.close();
            return exists;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }






    // Naya user add karo
    public boolean addUser(String username, String password) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            int rows = ps.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // User delete karo
    public boolean deleteUser(String username) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "DELETE FROM users WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // User ka password change karo
    public boolean changePassword(String username, String newPassword) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Saare users ki list - admin ke liye
    public ArrayList<String[]> getAllUsers() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();  //Database se connection establish hota hai
            String query = "SELECT id, username FROM users";
            PreparedStatement ps = con.prepareStatement(query);  //Database ko message jata hai:
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                                        // Har row ko ek 1D array (String[]) banaya gaya hai
                list.add(new String[]{  //  Aur saari rows mil kar ArrayList ban rahi hai (2D structure)
                    rs.getString("id"),
                    rs.getString("username")
                });
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    // ─────────────────────────────────────────
    //  INVOICE OPERATIONS
    // ─────────────────────────────────────────

    // Invoice save karo database mein
    // Pehle invoice header save hoga, phir uske items
    public boolean saveInvoice(Invoice invoice, String username) {
        try {
            Connection con = DBConnection.getConnection();

            // Step 1: Invoice header save karo
            String q1 = "INSERT INTO invoices (username, invoice_type, total) VALUES (?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(q1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, username);
            ps1.setString(2, invoice.getInvoiceType());
            ps1.setDouble(3, invoice.calculateTotal());
            ps1.executeUpdate();


            // Step 2: Naya bana invoice ka ID lo
            ResultSet keys = ps1.getGeneratedKeys();
            int invoiceId = 0;
            if (keys.next()) invoiceId = keys.getInt(1);

            // Step 3: Har item save karo
            String q2 = "INSERT INTO invoice_items (invoice_id, item_name, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(q2);
            for (InvoiceItem item : invoice.getItems()) {
                ps2.setInt(1, invoiceId);
                ps2.setString(2, item.getItemName());
                ps2.setInt(3, item.getQuantity());
                ps2.setDouble(4, item.getPrice());
                ps2.addBatch();
            }
            ps2.executeBatch();

            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Ek user ki apni invoices
   public ArrayList<Invoice> getUserInvoices(String username) {
    ArrayList<Invoice> list = new ArrayList<>();
    try {
        Connection con = DBConnection.getConnection();
        String query = "SELECT id, invoice_type, total, created_at FROM invoices WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Invoice inv = new SimpleInvoice(
                rs.getString("id"),
                username,
                rs.getString("invoice_type"),
                rs.getString("total"),
                rs.getString("created_at")   // autodate genrated
            );
            list.add(inv);
        }

        con.close();
    } catch (Exception e) {
        System.out.println(e);
    }
    return list;
}

    // Admin ke liye: saare users ki saari invoices
    public ArrayList<String[]> getAllInvoices() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT id, username, invoice_type, total, created_at FROM invoices";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("invoice_type"),
                    rs.getString("total"),
                    rs.getString("created_at")
                });
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    // Admin ke liye: sirf ek user ki invoices
    public ArrayList<String[]> getInvoicesByUser(String username) {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT id, username, invoice_type, total, created_at FROM invoices WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("invoice_type"),
                    rs.getString("total"),
                    rs.getString("created_at")
                });
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    // Ek invoice ki header info (ID, username, type, total, date)
    // Return: String array [id, username, invoice_type, total, created_at]
    // Agar invoice na mile to null return karta hai
    // user will use it
    public String[] getInvoiceInfo(int invoiceId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT id, username, invoice_type, total, created_at FROM invoices WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] info = {
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("invoice_type"),
                    rs.getString("total"),
                    rs.getString("created_at")
                };
                con.close();
                return info;
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null; // Invoice nahi mili
    }

    // Ek invoice ki items detail
    public ArrayList<String[]> getInvoiceItems(int invoiceId) {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT item_name, quantity, price FROM invoice_items WHERE invoice_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("item_name"),
                    rs.getString("quantity"),
                    rs.getString("price")
                });
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
}
