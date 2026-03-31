import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GestionnaireManagementPanel extends JPanel {
    private Gare gare;
    private JTable gestionnaireTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnDelete;
    private GestionnaireDAO gestionnaireDAO;

    public GestionnaireManagementPanel(Gare gare) {
        this.gare = gare;
        this.gestionnaireDAO = new GestionnaireDAO();


        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        addListeners();
        loadGestionnaires();
    }

    private void initComponents() {
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Date Embauche"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gestionnaireTable = new JTable(tableModel);

        UIStyle.styleTable(gestionnaireTable);

        btnAdd = UIStyle.createGreenButton(" Ajouter Gestionnaire");
        btnDelete = UIStyle.createRedButton(" Supprimer");
    }

    private void layoutComponents() {
        JLabel title = UIStyle.createTitleLabel("Gestion des Gestionnaires de Gare");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gestionnaireTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(buttonPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> showAddGestionnaireDialog());
        btnDelete.addActionListener(e -> deleteSelectedGestionnaire());
    }

    private void loadGestionnaires() {
        tableModel.setRowCount(0);
        try {
            List<Gestionnaire> gestionnaires = gestionnaireDAO.getGestionnairesByGare(gare.getIdGare());

            for (Gestionnaire gest : gestionnaires) {
                tableModel.addRow(new Object[]{
                        gest.getIdGestionnaire(),
                        gest.getNom(),
                        gest.getPrenom(),
                        gest.getEmail(),
                        gest.getDateEmbauche()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des gestionnaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddGestionnaireDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un Gestionnaire", true);

        JPanel mainPanel = UIStyle.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));


        dialog.setLocationRelativeTo(this);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Composants de saisie stylisés
        JTextField txtNom = UIStyle.createStyledTextField();
        JTextField txtPrenom = UIStyle.createStyledTextField();
        JTextField txtEmail = UIStyle.createStyledTextField();
        JPasswordField txtPassword = new JPasswordField();

        // Style pour JPasswordField
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));


        var formAdder = new Object() {
            void add(String labelText, JComponent component, int row) {
                // Label (colonne 0)
                gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
                JLabel label = UIStyle.createSubtitleLabel(labelText); // Label stylisé
                label.setHorizontalAlignment(JLabel.RIGHT);
                formPanel.add(label, gbc);

                // Champ (colonne 1)
                gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
                formPanel.add(component, gbc);
            }
        };

        // Remplissage du formulaire
        formAdder.add("Nom:", txtNom, 0);
        formAdder.add("Prénom:", txtPrenom, 1);
        formAdder.add("Email:", txtEmail, 2);
        formAdder.add("Mot de passe:", txtPassword, 3);


        // Boutons stylisés
        JButton btnSave = UIStyle.createGreenButton(" Enregistrer");
        JButton btnCancel = UIStyle.createRedButton(" Annuler");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // Assemblage du dialogue
        mainPanel.add(UIStyle.createTitleLabel("Nouveau Gestionnaire"), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);


        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, 350));
        dialog.setLocationRelativeTo(this);

        btnSave.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Gestionnaire gestionnaire = new Gestionnaire(gare.getIdGare(), nom, prenom, email, password);

            try {
                if (gestionnaireDAO.addGestionnaire(gestionnaire)) {
                    JOptionPane.showMessageDialog(dialog, "Gestionnaire ajouté avec succès!");
                    loadGestionnaires();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout du gestionnaire", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteSelectedGestionnaire() {
        int selectedRow = gestionnaireTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un gestionnaire à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idGestionnaire = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer " + prenom + " " + nom + "?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (gestionnaireDAO.deleteGestionnaire(idGestionnaire)) {
                    JOptionPane.showMessageDialog(this, "Gestionnaire supprimé avec succès!");
                    loadGestionnaires();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}