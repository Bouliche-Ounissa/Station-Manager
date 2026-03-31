import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

class BilletManagementPanel extends JPanel {
    private Gare gare;
    private BilletDAO billetDAO;
    private TrainDAO trainDAO;
    private JTable tableBillets;
    private DefaultTableModel model;


    private JComboBox<String> comboTypeBillet;
    private JComboBox<Train> comboTrain;
    private JTextField txtPrix;
    private JSpinner spinnerQuantite;

    private JButton btnAjouter, btnSupprimer;

    public BilletManagementPanel(Gare gare) {
        this.gare = gare;
        this.billetDAO = new BilletDAO();
        this.trainDAO = new TrainDAO();
        initComponents();
        layoutComponents();
        chargerBilletsDisponibles();
    }

    private void initComponents() {
        // Initialisation des composants (inchangée)
        comboTypeBillet = UIStyle.<String>createStyledComboBox();
        comboTypeBillet.addItem("Normal");
        comboTypeBillet.addItem("VIP");

        comboTrain = UIStyle.<Train>createStyledComboBox();
        txtPrix = UIStyle.createStyledTextField();

        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 500, 1);
        spinnerQuantite = new JSpinner(spinnerModel);
        spinnerQuantite.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinnerQuantite.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        btnAjouter = UIStyle.createGreenButton("Ajouter Lot de Billets");
        btnSupprimer = UIStyle.createRedButton("Supprimer");

        model = new DefaultTableModel(new Object[]{
                "ID", "Train", "Type", "Prix", "Statut"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBillets = new JTable(model);
        UIStyle.styleTable(tableBillets);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Titre du panneau en haut
        JLabel title = UIStyle.createTitleLabel("Gestion des Billets Disponibles");
        add(title, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        TitledBorder tableBorder = BorderFactory.createTitledBorder("Inventaire des Billets");
        tableBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        tableBorder.setTitleColor(UIStyle.BLUE_DARK);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                tableBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(tableBillets);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);


        JPanel formPanel = createFormPanel(); // Méthode extraite ci-dessous



        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setResizeWeight(0.65); // Le tableau prend 65% de l'espace
        splitPane.setDividerSize(8); // Petite barre de séparation
        splitPane.setBorder(BorderFactory.createEmptyBorder()); // Supprimer la bordure par défaut

        add(splitPane, BorderLayout.CENTER);

        remplirComboBox();
        btnAjouter.addActionListener(e -> ajouterBillet());
        btnSupprimer.addActionListener(e -> supprimerBillet());
    }


    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 15));
        formPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyle.BLUE_LIGHT.darker()),
                "Ajouter un Lot de Billets"
        );
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        titledBorder.setTitleColor(UIStyle.BLUE_DARK);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Ligne 1: Type Billet ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblType = UIStyle.createSubtitleLabel("Type Billet :");
        lblType.setHorizontalAlignment(JLabel.RIGHT);
        form.add(lblType, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        form.add(comboTypeBillet, gbc);

        // --- Ligne 2: Train associé ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblTrain = UIStyle.createSubtitleLabel("Train associé :");
        lblTrain.setHorizontalAlignment(JLabel.RIGHT);
        form.add(lblTrain, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        form.add(comboTrain, gbc);

        // --- Ligne 3: Prix ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblPrix = UIStyle.createSubtitleLabel("Prix (DA) :");
        lblPrix.setHorizontalAlignment(JLabel.RIGHT);
        form.add(lblPrix, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        form.add(txtPrix, gbc);

        // --- Ligne 4: Quantité ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblQuantite = UIStyle.createSubtitleLabel("Quantité à ajouter :");
        lblQuantite.setHorizontalAlignment(JLabel.RIGHT);
        form.add(lblQuantite, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        form.add(spinnerQuantite, gbc);

        formPanel.add(form, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnSupprimer);


        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(buttonPanel, BorderLayout.EAST);

        formPanel.add(southPanel, BorderLayout.SOUTH);

        return formPanel;
    }



    private void remplirComboBox() {
        try {
            comboTrain.removeAllItems();
            List<Train> trains = trainDAO.getTrainsByGare(gare.getIdGare());
            for (Train t : trains) {
                comboTrain.addItem(t);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement trains : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerBilletsDisponibles() {
        model.setRowCount(0);
        try {
            List<Billet> billetsDisponibles = billetDAO.getBilletsDisponiblesByGare(gare.getIdGare());

            for (Billet b : billetsDisponibles) {
                try {
                    Train t = trainDAO.getTrainById(b.getIdTrain());
                    String trainInfo = (t != null) ? t.getNumeroTrain() + " - " + t.getDestination() : "Train inconnu";

                    model.addRow(new Object[]{
                            b.getIdBillet(),
                            trainInfo,
                            b.getTypeBillet(),
                            String.format("%.2f DA", b.getPrix()),
                            b.getStatut()
                    });
                } catch (Exception e) {
                    System.err.println("Erreur avec le billet ID " + b.getIdBillet() + ": " + e.getMessage());
                    model.addRow(new Object[]{
                            b.getIdBillet(),
                            "Erreur chargement train",
                            b.getTypeBillet(),
                            String.format("%.2f DA", b.getPrix()),
                            b.getStatut()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement billets disponibles : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ajouterBillet() {
        try {
            if (comboTrain.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un train !");
                return;
            }

            double prix;
            int quantite;

            try {
                prix = Double.parseDouble(txtPrix.getText());
                if (prix <= 0) {
                    JOptionPane.showMessageDialog(this, "Le prix doit être supérieur à 0 !");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un prix valide !");
                return;
            }

            try {
                quantite = (int) spinnerQuantite.getValue();
                if (quantite <= 0) {
                    JOptionPane.showMessageDialog(this, "La quantité doit être au moins 1 !");
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Quantité invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Billet b = new Billet(
                    ((Train) comboTrain.getSelectedItem()).getIdTrain(),
                    comboTypeBillet.getSelectedItem().toString(),
                    prix
            );

            int successCount = 0;
            for (int i = 0; i < quantite; i++) {
                if (billetDAO.addBillet(b)) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>" + successCount + " billets ajoutés avec succès !</b><br>" +
                                "Ce lot est maintenant disponible pour la vente.</html>");
                chargerBilletsDisponibles();
                txtPrix.setText("");
                comboTypeBillet.setSelectedIndex(0);
                spinnerQuantite.setValue(1);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout des billets !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur ajout billet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void supprimerBillet() {
        int row = tableBillets.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un billet à supprimer !");
            return;
        }
        try {
            int idBillet = (int) model.getValueAt(row, 0);
            String typeBillet = (String) model.getValueAt(row, 2);
            String prix = (String) model.getValueAt(row, 3);

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer le billet " + typeBillet + " à " + prix + " ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                if (billetDAO.deleteBillet(idBillet)) {
                    JOptionPane.showMessageDialog(this, "Billet supprimé avec succès !");
                    chargerBilletsDisponibles();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du billet !");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur suppression billet : " + e.getMessage());
            e.printStackTrace();
        }
    }
}