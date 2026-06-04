import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainGUI extends JFrame {
    private DAO dao = new DAO();
    // CardLayout constants - public taake AdminPanel aur UserPanel use kar sakein
    public static final String LOGIN = "LOGIN";
    public static final String ADMIN = "ADMIN";
    public static final String USER  = "USER";

    // screens ko handle karega
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Sub-panels
    private AdminPanel adminPanel;
    private UserPanel  userPanel;

    public MainGUI() {
        setTitle("Billing System");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // jframe center  mein khulega.

        // ── Build sub-panels ──
        adminPanel = new AdminPanel(dao, cardLayout, mainPanel);
        userPanel  = new UserPanel(dao, cardLayout, mainPanel);

        // ── Add to CardLayout ── // 3 screens
        mainPanel.add(buildLoginPanel(), LOGIN);  // screen 1
        mainPanel.add(adminPanel,        ADMIN);  // screen 2
        mainPanel.add(userPanel,         USER);     // screen 3

        add(mainPanel);
        cardLayout.show(mainPanel, LOGIN);
        setVisible(true);
    }

    // ── LOGIN Panel ────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));  //GridLayout components ko rows aur columns (grid) me arrange karta hai
        panel.setBorder(BorderFactory.createEmptyBorder(150, 250, 150, 250));

        panel.add(new JLabel("Username:"));
        JTextField userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        JPasswordField passField = new JPasswordField();
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        panel.add(new JLabel(""));
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();

            if (u.isEmpty() || p.isEmpty()) {
                showMsg("Username or Password is Empty!", "Error");
                return;
            }

            // Admin check
            if (u.equals("admin") && p.equals("12345")) {
                userField.setText(""); passField.setText("");
                cardLayout.show(mainPanel, ADMIN);
                return;
            }

            // Normal user check
            if (dao.checkUser(u, p)) {
                userField.setText(""); passField.setText("");
                userPanel.setLoggedInUser(u);   // UserPanel ko username deta hai
                cardLayout.show(mainPanel, USER);
            } else {
                showMsg("Username or Password is incorrect!", "Error");
            }
        });

        return panel;
    }

    // ── Helper ─────────────────────────────────────────
    private void showMsg(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);  //popup message
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }   //GUI ko safely start karo"
}
