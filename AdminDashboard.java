import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class AdminDashboard extends JFrame {
    private Gare gare;
    private JTabbedPane tabbedPane;
    private JButton btnDeconnexion;
    private StatistiquesPanel statsPanel;

    private final Color BLUE_DARK = UIStyle.BLUE_DARK;
    private final Color BLUE_PRIMARY = UIStyle.BLUE_PRIMARY;

    public AdminDashboard(Gare gare) {
        this.gare = gare;
        setTitle("Dashboard Admin - " + gare.getNomGare());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Initialisation de tous les panneaux de gestion
        TrainManagementPanel trainPanel = new TrainManagementPanel(gare);
        GestionnaireManagementPanel gestPanel = new GestionnaireManagementPanel(gare);
        statsPanel = new StatistiquesPanel(gare); // Ce panneau est modifié
        BilletManagementPanel billetPanel = new BilletManagementPanel(gare);
        AdminHistoriquePanel historiquePanel = new AdminHistoriquePanel(gare);

        tabbedPane.addTab("Gestion des Trains", trainPanel);
        tabbedPane.addTab("Gestion des Gestionnaires", gestPanel);
        tabbedPane.addTab("Statistiques", statsPanel);
        tabbedPane.addTab("Gestion des Billets", billetPanel);
        tabbedPane.addTab("Historique des Ventes", historiquePanel);

        btnDeconnexion = UIStyle.createRedButton("Déconnexion");
        btnDeconnexion.addActionListener(e -> deconnecter());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, BLUE_DARK, getWidth(), getHeight(), BLUE_PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel(" Administration - " + gare.getNomGare());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel info = new JLabel("Admin: " + gare.getEmail());
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        infoPanel.setOpaque(false);
        infoPanel.add(info);
        infoPanel.add(btnDeconnexion);

        header.add(title, BorderLayout.WEST);
        header.add(infoPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(BLUE_DARK);
        JLabel footerLabel = new JLabel("Système de Gestion de Gare 2026");
        footerLabel.setForeground(Color.WHITE);
        footer.add(footerLabel);
        add(footer, BorderLayout.SOUTH);
    }

    private void deconnecter() {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Déconnexion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmation == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }


    class StatistiquesPanel extends JPanel {
        private Gare gare;
        private JLabel lblTotalTrains, lblTotalGestionnaires, lblTotalVentes;
        private TrainDAO trainDAO;
        private GestionnaireDAO gestionnaireDAO;
        private BilletDAO billetDAO;

        public StatistiquesPanel(Gare gare) {
            this.gare = gare;
            this.trainDAO = new TrainDAO();
            this.gestionnaireDAO = new GestionnaireDAO();
            this.billetDAO = new BilletDAO();
            initComponents();
            layoutComponents();
            chargerStatistiques();
        }

        private void initComponents() {

            lblTotalTrains = new JLabel("0", JLabel.CENTER);
            lblTotalGestionnaires = new JLabel("0", JLabel.CENTER);
            lblTotalVentes = new JLabel("0", JLabel.CENTER);

            Font f = new Font("Segoe UI", Font.BOLD, 48);
            lblTotalTrains.setFont(f);
            lblTotalGestionnaires.setFont(f);
            lblTotalVentes.setFont(f);


        }

        private void layoutComponents() {
            setLayout(new BorderLayout(30, 30));
            setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            setBackground(new Color(245, 245, 245)); // Fond léger pour accentuer les cartes

            JLabel titre = UIStyle.createTitleLabel("Vue d'ensemble et Statistiques Clés");
            titre.setHorizontalAlignment(JLabel.CENTER);
            titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            titre.setBackground(new Color(245, 245, 245));


            JPanel panelCards = new JPanel(new GridLayout(1, 3, 30, 30));
            panelCards.setOpaque(false);

            panelCards.add(createCard("Trains", "Total des Trains", lblTotalTrains, UIStyle.BLUE_PRIMARY, "train_icon"));
            panelCards.add(createCard("Gestionnaires", "Personnel Actif", lblTotalGestionnaires, UIStyle.GREEN_PRIMARY, "user_icon"));
            panelCards.add(createCard("Ventes", "Billets Vendus", lblTotalVentes, UIStyle.RED_PRIMARY, "ticket_icon"));

            add(titre, BorderLayout.NORTH);
            add(panelCards, BorderLayout.CENTER);
        }

        private JPanel createCard(String category, String title, JLabel valueLabel, Color color, String iconName) {

            JPanel cardContainer = UIStyle.createCardPanel();
            cardContainer.setLayout(new BorderLayout());

            JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
            contentPanel.setBackground(color);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Padding interne

            JPanel headerPanel = new JPanel(new GridLayout(2, 1));
            headerPanel.setOpaque(false);

            JLabel categoryLabel = new JLabel(category.toUpperCase());
            categoryLabel.setForeground(Color.WHITE);
            categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            headerPanel.add(categoryLabel);
            headerPanel.add(titleLabel);

            JPanel valueAndIconPanel = new JPanel(new BorderLayout(20, 0));
            valueAndIconPanel.setOpaque(false);

            JLabel iconLabel = new JLabel(UIStyle.loadIcon(iconName));
            iconLabel.setPreferredSize(new Dimension(48, 48));

            valueLabel.setForeground(Color.WHITE);
            valueLabel.setHorizontalAlignment(JLabel.LEFT);

            valueAndIconPanel.add(iconLabel, BorderLayout.WEST);
            valueAndIconPanel.add(valueLabel, BorderLayout.CENTER);


            contentPanel.add(headerPanel, BorderLayout.NORTH);
            contentPanel.add(valueAndIconPanel, BorderLayout.CENTER);

            cardContainer.add(contentPanel, BorderLayout.CENTER);

            return cardContainer;
        }


        private void chargerStatistiques() {
            try {
                lblTotalTrains.setText(String.valueOf(trainDAO.getTrainsByGare(gare.getIdGare()).size()));
                lblTotalGestionnaires.setText(String.valueOf(gestionnaireDAO.getGestionnairesByGare(gare.getIdGare()).size()));

                HistoriqueVenteDao historiqueDao = new HistoriqueVenteDaoImpl();
                List<HistoriqueVente> ventes = historiqueDao.findByGare(gare.getIdGare());

                lblTotalVentes.setText(String.valueOf(ventes.size()));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur chargement des statistiques: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    class AdminHistoriquePanel extends JPanel {
        private Gare gare;
        private JTable table;
        private DefaultTableModel tableModel;
        private HistoriqueVenteDao historiqueDao;

        public AdminHistoriquePanel(Gare gare) {
            this.gare = gare;
            this.historiqueDao = new HistoriqueVenteDaoImpl();

            initComponents();
            layoutComponents();
            chargerHistorique();
        }

        private void initComponents() {
            String[] cols = {"ID Vente", "ID Gestionnaire", "ID Billet", "Montant (DA)", "Date"};
            tableModel = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            table = new JTable(tableModel);
            UIStyle.styleTable(table);
        }

        private void layoutComponents() {
            setLayout(new BorderLayout(15, 15));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            setBackground(Color.WHITE);

            JLabel titre = UIStyle.createTitleLabel("Historique Complet des Ventes");
            titre.setHorizontalAlignment(JLabel.LEFT);

            JButton btnRafraichir = UIStyle.createBlueButton(" Rafraîchir", null);
            btnRafraichir.addActionListener(e -> chargerHistorique());

            JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.add(titre, BorderLayout.WEST);
            headerPanel.add(btnRafraichir, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            add(headerPanel, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            add(scrollPane, BorderLayout.CENTER);
        }

        private void chargerHistorique() {
            tableModel.setRowCount(0);
            try {
                List<HistoriqueVente> ventes = historiqueDao.findByGare(gare.getIdGare());

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (HistoriqueVente vente : ventes) {
                    tableModel.addRow(new Object[]{
                            vente.getId(),
                            vente.getIdGestionnaire(),
                            vente.getIdBillet(),
                            String.format("%.2f DA", vente.getMontant()),
                            vente.getDateVente().format(fmt)
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur chargement historique: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}