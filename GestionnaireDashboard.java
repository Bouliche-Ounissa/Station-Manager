import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GestionnaireDashboard extends JFrame {
    private Gestionnaire gestionnaire;
    private JTabbedPane tabbedPane;
    private TrainDAO trainDAO;
    private BilletDAO billetDAO;
    private HistoriqueVentePanel historiquePanel;

    public GestionnaireDashboard(Gestionnaire gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.trainDAO = new TrainDAO();
        this.billetDAO = new BilletDAO();

        setTitle("Dashboard Gestionnaire - " + gestionnaire.getNomComplet());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        VenteBilletPanel ventePanel = new VenteBilletPanel(gestionnaire, billetDAO);
        historiquePanel = new HistoriqueVentePanel(gestionnaire);
        ventePanel.setHistoriquePanelReference(historiquePanel);

        ConsultationTrainPanel consultationPanel = new ConsultationTrainPanel(gestionnaire);

        tabbedPane.addTab("🎫 Vente Billets", ventePanel);
        tabbedPane.addTab("🚆 Trains", consultationPanel);
        tabbedPane.addTab("📊 Historique", historiquePanel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, UIStyle.BLUE_DARK,
                        getWidth(), getHeight(), UIStyle.BLUE_PRIMARY
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel titleLabel = new JLabel(
                " Espace Gestionnaire - " + gestionnaire.getNomComplet(),
                JLabel.LEFT
        );
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel userLabel = new JLabel("📍 Gare: " + getNomGare() +
                "  |  ✉ Email: " + gestionnaire.getEmail());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        infoPanel.add(userLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UIStyle.BLUE_DARK);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel footerLabel = new JLabel("Système de Gestion de Gare 2026");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private String getNomGare() {
        GareDAO gareDAO = new GareDAO();
        List<Gare> gares = gareDAO.getAllGares();
        for (Gare g : gares)
            if (g.getIdGare() == gestionnaire.getIdGare())
                return g.getNomGare();
        return "Inconnue";
    }
}


// PANEL VENTE BILLETS
class VenteBilletPanel extends JPanel {
    private Gestionnaire gestionnaire;
    private JComboBox<Train> comboTrains;
    private JComboBox<Billet> comboBilletsDisponibles;
    private JLabel lblPrix;
    private JLabel lblTrain;
    private JLabel lblType;
    private JLabel lblStatut;
    private JButton btnVendre;
    private JButton btnRafraichir;
    private TrainDAO trainDAO;
    private BilletDAO billetDAO;
    private HistoriqueVentePanel historiquePanel;

    public VenteBilletPanel(Gestionnaire gestionnaire, BilletDAO billetDAO) {
        this.gestionnaire = gestionnaire;
        this.trainDAO = new TrainDAO();
        this.billetDAO = billetDAO;

        initComponents();
        layoutComponents();
        addListeners();
        chargerTrains();
    }

    public void setHistoriquePanelReference(HistoriqueVentePanel panel) {
        this.historiquePanel = panel;
    }

    private void initComponents() {
        comboTrains = new JComboBox<>(); // Initialiser le combo des trains
        comboBilletsDisponibles = new JComboBox<>();
        lblPrix = new JLabel("-");
        lblTrain = new JLabel("-");
        lblType = new JLabel("-");
        lblStatut = new JLabel("-");
        btnVendre = UIStyle.createBlueButton("Valider la vente", null);
        btnRafraichir = UIStyle.createGreenButton(" Rafraîchir");

        // Style des labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        lblPrix.setFont(labelFont);
        lblTrain.setFont(labelFont);
        lblType.setFont(labelFont);
        lblStatut.setFont(labelFont);

        lblPrix.setForeground(UIStyle.BLUE_DARK);
        lblTrain.setForeground(UIStyle.BLUE_DARK);
        lblType.setForeground(UIStyle.BLUE_DARK);
        lblStatut.setForeground(Color.GREEN.darker());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // PANEL PRINCIPAL
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BLUE_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TITRE
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titre = new JLabel("🎫 Vente de Billets", JLabel.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(UIStyle.BLUE_DARK);
        mainPanel.add(titre, gbc);

        // SELECTION TRAIN (NOUVEAU)
        gbc.gridwidth = 1; gbc.gridy++;
        JLabel lblTrainSelect = new JLabel("🚆 Sélectionner un train :");
        lblTrainSelect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblTrainSelect, gbc);
        gbc.gridx = 1;
        comboTrains.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboTrains.setPreferredSize(new Dimension(300, 30));
        mainPanel.add(comboTrains, gbc);

        // SELECTION BILLET
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblBillet = new JLabel("🎫 Billet disponible :");
        lblBillet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblBillet, gbc);
        gbc.gridx = 1;
        comboBilletsDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBilletsDisponibles.setPreferredSize(new Dimension(300, 30));
        mainPanel.add(comboBilletsDisponibles, gbc);

        // DETAILS DU BILLET
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblTrainTitle = new JLabel("🚆 Train sélectionné :");
        lblTrainTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblTrainTitle, gbc);
        gbc.gridx = 1;
        JPanel trainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        trainPanel.setBackground(Color.WHITE);
        trainPanel.add(lblTrain);
        mainPanel.add(trainPanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblTypeTitle = new JLabel("🎯 Type :");
        lblTypeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblTypeTitle, gbc);
        gbc.gridx = 1;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(Color.WHITE);
        typePanel.add(lblType);
        mainPanel.add(typePanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblPrixTitle = new JLabel("💶 Prix :");
        lblPrixTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblPrixTitle, gbc);
        gbc.gridx = 1;
        JPanel prixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prixPanel.setBackground(Color.WHITE);
        prixPanel.add(lblPrix);
        mainPanel.add(prixPanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblStatutTitle = new JLabel("📊 Statut :");
        lblStatutTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblStatutTitle, gbc);
        gbc.gridx = 1;
        JPanel statutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statutPanel.setBackground(Color.WHITE);
        statutPanel.add(lblStatut);
        mainPanel.add(statutPanel, gbc);

        // BOUTONS
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnVendre);
        buttonPanel.add(btnRafraichir);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // PANEL INFORMATION
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(UIStyle.BLUE_LIGHT);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        // Quand on sélectionne un train, charger les billets associés
        comboTrains.addActionListener(e -> chargerBilletsParTrain());
        comboBilletsDisponibles.addActionListener(e -> afficherDetailsBillet());
        btnVendre.addActionListener(e -> vendreBillet());
        btnRafraichir.addActionListener(e -> {
            chargerTrains(); // Recharger les trains
            chargerBilletsParTrain(); // Recharger les billets du train sélectionné
        });
    }

    private void chargerTrains() {
        comboTrains.removeAllItems();
        try {
            List<Train> trains = trainDAO.getTrainsByGare(gestionnaire.getIdGare());

            if (trains.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "🚆 Aucun train disponible pour votre gare.\nL'administrateur doit d'abord ajouter des trains.",
                        "Aucun train disponible",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Train train : trains) {
                    comboTrains.addItem(train);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors du chargement des trains : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void chargerBilletsParTrain() {
        comboBilletsDisponibles.removeAllItems();
        Train trainSelectionne = (Train) comboTrains.getSelectedItem();

        if (trainSelectionne == null) {
            return;
        }

        try {
            List<Billet> billetsDisponibles = billetDAO.getBilletsDisponiblesByTrain(trainSelectionne.getIdTrain());

            if (billetsDisponibles.isEmpty()) {
                // Ajouter un item vide pour indiquer qu'aucun billet n'est disponible
                comboBilletsDisponibles.addItem(new Billet() {
                    @Override
                    public String toString() {
                        return "❌ Aucun billet disponible pour ce train";
                    }

                    @Override
                    public int getIdBillet() {
                        return 0; // ID 0 pour indiquer que ce n'est pas un vrai billet
                    }
                });
            } else {
                for (Billet b : billetsDisponibles) {
                    comboBilletsDisponibles.addItem(b);
                }
            }
            afficherDetailsBillet();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors du chargement des billets : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void afficherDetailsBillet() {
        Billet billetSelectionne = (Billet) comboBilletsDisponibles.getSelectedItem();
        Train trainSelectionne = (Train) comboTrains.getSelectedItem();

        if (billetSelectionne != null && trainSelectionne != null && billetSelectionne.getIdBillet() != 0) {
            try {
                // Afficher les détails du train sélectionné
                lblTrain.setText(trainSelectionne.getNumeroTrain() + " - " + trainSelectionne.getDestination() +
                        " (" + trainSelectionne.getHeureDepart() + " → " + trainSelectionne.getHeureArrivee() + ")");

                // Afficher les détails du billet
                lblType.setText(billetSelectionne.getTypeBillet());
                lblPrix.setText(String.format("💶 %.2f DA", billetSelectionne.getPrix()));
                lblStatut.setText("✅ " + billetSelectionne.getStatut().toUpperCase());

                // Changer la couleur du statut
                if ("disponible".equalsIgnoreCase(billetSelectionne.getStatut())) {
                    lblStatut.setForeground(Color.GREEN.darker());
                } else {
                    lblStatut.setForeground(Color.ORANGE);
                }
            } catch (Exception e) {
                lblTrain.setText("❌ Erreur de chargement");
                lblType.setText("❌ Erreur de chargement");
                lblPrix.setText("❌ Erreur de chargement");
                lblStatut.setText("❌ Erreur de chargement");
            }
        } else {
            lblTrain.setText("-");
            lblType.setText("-");
            lblPrix.setText("-");
            lblStatut.setText("-");
        }
    }

    private void vendreBillet() {
        Billet billetSelectionne = (Billet) comboBilletsDisponibles.getSelectedItem();
        Train trainSelectionne = (Train) comboTrains.getSelectedItem();

        if (billetSelectionne == null || trainSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Veuillez sélectionner un train et un billet !",
                    "Sélection incomplète",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier si c'est un billet valide (pas le message "Aucun billet disponible")
        if (billetSelectionne.getIdBillet() == 0) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Aucun billet disponible pour la vente !",
                    "Billet non disponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation de vente
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vendre ce billet ?\n\n" +
                        "📋 Détails du billet :\n" +
                        "• Train: " + trainSelectionne.getNumeroTrain() + " - " + trainSelectionne.getDestination() + "\n" +
                        "• Type: " + billetSelectionne.getTypeBillet() + "\n" +
                        "• Prix: " + String.format("%.2f €", billetSelectionne.getPrix()) + "\n" +
                        "• Gestionnaire: " + gestionnaire.getNomComplet(),
                "Confirmation de vente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Vendre le billet
            if (billetDAO.vendreBillet(billetSelectionne.getIdBillet(), gestionnaire.getIdGestionnaire())) {

                // Créer l'historique de vente
                HistoriqueVente historique = new HistoriqueVente(
                        gestionnaire.getIdGestionnaire(),
                        billetSelectionne.getIdBillet(),
                        LocalDateTime.now(),
                        billetSelectionne.getPrix()
                );

                HistoriqueVenteDao historiqueDao = new HistoriqueVenteDaoImpl();
                historiqueDao.create(historique);

                JOptionPane.showMessageDialog(this,
                        "✅ Vente effectuée avec succès !\n\n" +
                                "🚆 Train: " + trainSelectionne.getNumeroTrain() + " - " + trainSelectionne.getDestination() + "\n" +
                                "💰 Montant: " + String.format("%.2f DA", billetSelectionne.getPrix()) + "\n" +
                                "🎫 Type: " + billetSelectionne.getTypeBillet() + "\n" +
                                "👤 Vendu par: " + gestionnaire.getNomComplet(),
                        "Vente réussie",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recharger l'historique et les billets disponibles
                if (historiquePanel != null) {
                    historiquePanel.chargerHistorique();
                }
                chargerBilletsParTrain(); // Recharger seulement les billets du train sélectionné

            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Erreur lors de la vente du billet !\n" +
                                "Le billet n'a pas pu être vendu.",
                        "Erreur de vente",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors de la vente : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

// PANEL HISTORIQUE VENTES
class HistoriqueVentePanel extends JPanel {
    private Gestionnaire gestionnaire;
    private JTable table;
    private DefaultTableModel tableModel;
    private HistoriqueVenteDao historiqueDao;
    private JLabel lblTotalVentes;
    private JLabel lblMontantTotal;
    private JButton btnRafraichir;

    public HistoriqueVentePanel(Gestionnaire gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.historiqueDao = new HistoriqueVenteDaoImpl();

        initComponents();
        layoutComponents();
        chargerHistorique();
    }

    private void initComponents() {
        String[] cols = {"ID Vente", "ID Billet", "Montant (DA)", "Date de vente"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        UIStyle.styleTable(table);

        lblTotalVentes = new JLabel("0");
        lblMontantTotal = new JLabel("0,00 DA");
        btnRafraichir = UIStyle.createBlueButton("Rafraîchir", null);

        // Style des labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        lblTotalVentes.setFont(labelFont);
        lblMontantTotal.setFont(labelFont);
        lblTotalVentes.setForeground(UIStyle.BLUE_DARK);
        lblMontantTotal.setForeground(Color.GREEN.darker());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titre = new JLabel(" Historique des ventes - " + gestionnaire.getNomComplet(), JLabel.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(UIStyle.BLUE_DARK);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(createStatCard(" Total ventes", lblTotalVentes));
        statsPanel.add(createStatCard(" Montant total", lblMontantTotal));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnRafraichir);

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRafraichir.addActionListener(e -> chargerHistorique());
    }

    private JPanel createStatCard(String titre, JLabel valeur) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyle.BLUE_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BLUE_PRIMARY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titreLabel.setForeground(UIStyle.BLUE_DARK);

        valeur.setHorizontalAlignment(JLabel.CENTER);

        card.add(titreLabel, BorderLayout.NORTH);
        card.add(valeur, BorderLayout.CENTER);

        return card;
    }

    public void chargerHistorique() {
        tableModel.setRowCount(0);
        try {
            List<HistoriqueVente> ventes = historiqueDao.findByGestionnaire(gestionnaire.getIdGestionnaire());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            double totalMontant = 0;
            for (HistoriqueVente vente : ventes) {
                tableModel.addRow(new Object[]{
                        vente.getId(),
                        vente.getIdBillet(),
                        String.format("💶 %.2f DA", vente.getMontant()),
                        vente.getDateVente().format(fmt)
                });
                totalMontant += vente.getMontant();
            }

            // Mettre à jour les statistiques
            lblTotalVentes.setText(String.valueOf(ventes.size()));
            lblMontantTotal.setText(String.format("💶 %.2f DA", totalMontant));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors du chargement de l'historique : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

// PANEL CONSULTATION TRAINS
class ConsultationTrainPanel extends JPanel {
    private Gestionnaire gestionnaire;
    private JTable trainTable;
    private DefaultTableModel tableModel;
    private TrainDAO trainDAO;
    private JButton btnRafraichir;
    private JLabel lblTotalTrains;

    public ConsultationTrainPanel(Gestionnaire gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.trainDAO = new TrainDAO();

        initComponents();
        layoutComponents();
        chargerTrains();
    }

    private void initComponents() {
        String[] columns = {"Numéro", "Type", "Destination", "Départ", "Arrivée", "Prix Base"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        trainTable = new JTable(tableModel);
        UIStyle.styleTable(trainTable);

        btnRafraichir = UIStyle.createBlueButton(" Rafraîchir", null);
        lblTotalTrains = new JLabel("0");

        // Style du label
        lblTotalTrains.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalTrains.setForeground(UIStyle.BLUE_DARK);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titre = new JLabel(" Liste des trains - " + gestionnaire.getNomComplet(), JLabel.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(UIStyle.BLUE_DARK);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(Color.WHITE);
        JLabel lblTitreStats = new JLabel("Total trains : ");
        lblTitreStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitreStats.setForeground(UIStyle.BLUE_DARK);
        statsPanel.add(lblTitreStats);
        statsPanel.add(lblTotalTrains);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnRafraichir);

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(trainTable), BorderLayout.CENTER);

        btnRafraichir.addActionListener(e -> chargerTrains());
    }

    private void chargerTrains() {
        tableModel.setRowCount(0);
        try {
            List<Train> trains = trainDAO.getTrainsByGare(gestionnaire.getIdGare());

            for (Train t : trains) {
                tableModel.addRow(new Object[]{
                        "🚆 " + t.getNumeroTrain(),
                        t.getTypeTrain(),
                        "📍 " + t.getDestination(),
                        "🕐 " + t.getHeureDepart(),
                        "🕐 " + t.getHeureArrivee(),
                        String.format("💶 %.2f DA", t.getPrixBase())
                });
            }

            // Mettre à jour le compteur
            lblTotalTrains.setText(String.valueOf(trains.size()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors du chargement des trains : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
