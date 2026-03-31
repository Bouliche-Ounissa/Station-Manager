import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> comboUserType;
    private JButton btnLogin;
    private ImageIcon logoIcon;

    private final Color BLUE_DARK = new Color(15, 70, 140);
    private final Color BLUE_PRIMARY = new Color(30, 120, 210);
    private final Color BTN_COLOR = new Color(70, 130, 180);
    private final Color BTN_HOVER = new Color(100, 160, 210);

    public LoginFrame() {
        setTitle("RailFlowGare - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        txtEmail = new JTextField(20);
        txtPassword = new JPasswordField(20);
        comboUserType = new JComboBox<>(new String[]{"Administrateur Gare", "Gestionnaire"});

        btnLogin = new JButton("Se connecter");
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(BTN_COLOR);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(BTN_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(BTN_COLOR);
            }
        });

        //  logo
        try {
            java.net.URL imageUrl = getClass().getResource("/RailFlowGare.png");
            if (imageUrl != null) logoIcon = new ImageIcon(imageUrl);
            else logoIcon = new ImageIcon("RailFlowGare.png");

            if (logoIcon != null && logoIcon.getImage() != null) {
                Image img = logoIcon.getImage();
                Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Le logo n'a pas pu être chargé.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            logoIcon = null;
        }
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, BLUE_DARK, getWidth(), getHeight(), BLUE_PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(getWidth(), 90));
        header.setLayout(new GridBagLayout());

        JLabel logoLabel = new JLabel();
        if (logoIcon != null) logoLabel.setIcon(logoIcon);

        JLabel title = new JLabel("RailFlowGare");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitle = new JLabel("Connexion au système de gestion de gare");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2; gbc.anchor = GridBagConstraints.CENTER;
        header.add(logoLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.anchor = GridBagConstraints.SOUTHWEST;
        header.add(title, gbc);

        gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        header.add(subtitle, gbc);

        add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Type d'utilisateur:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(comboUserType, gbc);

        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(btnLogin, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        btnLogin.addActionListener(this::authenticate);
        txtPassword.addActionListener(e -> authenticate(null));
    }

    private void authenticate(ActionEvent e) {
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        String userType = (String) comboUserType.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("Administrateur Gare".equals(userType)) {
            GareDAO gareDAO = new GareDAO();
            Gare gare = gareDAO.authenticate(email, password);
            if (gare != null) {
                new AdminDashboard(gare).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            GestionnaireDAO gestDAO = new GestionnaireDAO();
            Gestionnaire gestionnaire = gestDAO.authenticate(email, password);
            if (gestionnaire != null) {
                new GestionnaireDashboard(gestionnaire).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) { ex.printStackTrace(); }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
