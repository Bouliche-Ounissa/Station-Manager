import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class TrainManagementPanel extends JPanel {
    private Gare gare;
    private JTable trainTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;
    private TrainDAO trainDAO; // Supposé exister

    public TrainManagementPanel(Gare gare) {
        this.gare = gare;
        this.trainDAO = new TrainDAO();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        addListeners();
        loadTrains();
    }

    private void initComponents() {
        String[] columns = {"ID", "Numéro", "Type", "Destination", "Départ", "Arrivée", "Prix"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trainTable = new JTable(tableModel);
        UIStyle.styleTable(trainTable);

        btnAdd = UIStyle.createGreenButton(" Ajouter Train");
        btnEdit = UIStyle.createBlueButton(" Modifier", null);
        btnDelete = UIStyle.createRedButton(" Supprimer");
    }

    private void layoutComponents() {
        JLabel title = UIStyle.createTitleLabel("Gestion des Trains");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(trainTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(buttonPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> showAddTrainDialog());
        btnDelete.addActionListener(e -> deleteSelectedTrain());
        btnEdit.addActionListener(e -> showEditTrainDialog());
    }

    private void loadTrains() {
        tableModel.setRowCount(0);
        List<Train> trains = trainDAO.getTrainsByGare(gare.getIdGare());

        for (Train train : trains) {
            tableModel.addRow(new Object[]{
                    train.getIdTrain(),
                    train.getNumeroTrain(),
                    train.getTypeTrain(),
                    train.getDestination(),
                    train.getHeureDepart(),
                    train.getHeureArrivee(),
                    String.format("%.2f DA", train.getPrixBase())
            });
        }
    }

    private void showAddTrainDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un Train", true);
        JPanel mainPanel = UIStyle.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNumero = UIStyle.createStyledTextField();
        JComboBox<String> comboType = UIStyle.<String>createStyledComboBox();
        comboType.addItem("TGV"); comboType.addItem("NORMAL");
        JTextField txtDestination = UIStyle.createStyledTextField();
        JTextField txtHeureDepart = UIStyle.createStyledTextField();
        txtHeureDepart.setText("HH:MM:SS");
        JTextField txtHeureArrivee = UIStyle.createStyledTextField();
        txtHeureArrivee.setText("HH:MM:SS");
        JTextField txtPrix = UIStyle.createStyledTextField();

        int y = 0;

        var formAdder = new Object() {
            void add(String labelText, JComponent component, int row) {
                // Label (colonne 0)
                gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
                JLabel label = UIStyle.createSubtitleLabel(labelText);
                label.setHorizontalAlignment(JLabel.RIGHT);
                formPanel.add(label, gbc);

                // Champ (colonne 1)
                gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
                formPanel.add(component, gbc);
            }
        };

        formAdder.add("Numéro train:", txtNumero, y++);
        formAdder.add("Type:", comboType, y++);
        formAdder.add("Destination:", txtDestination, y++);
        formAdder.add("Heure départ :", txtHeureDepart, y++);
        formAdder.add("Heure arrivée :", txtHeureArrivee, y++);
        formAdder.add("Prix base (DA):", txtPrix, y++);

        // Boutons stylisés
        JButton btnSave = UIStyle.createGreenButton(" Enregistrer");
        JButton btnCancel = UIStyle.createRedButton(" Annuler");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(UIStyle.createTitleLabel("Nouveau Train"), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        btnSave.addActionListener(e -> {
            try {
                Train train = new Train();
                train.setIdGare(gare.getIdGare());
                train.setNumeroTrain(txtNumero.getText());
                train.setTypeTrain((String) comboType.getSelectedItem());
                train.setDestination(txtDestination.getText());
                train.setHeureDepart(Time.valueOf(txtHeureDepart.getText()));
                train.setHeureArrivee(Time.valueOf(txtHeureArrivee.getText()));
                train.setPrixBase(Double.parseDouble(txtPrix.getText()));

                if (trainDAO.addTrain(train)) {
                    JOptionPane.showMessageDialog(dialog, "Train ajouté avec succès!");
                    loadTrains();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout du train", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Format de données invalide. Assurez-vous d'utiliser HH:MM:SS pour l'heure et un nombre pour le prix.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // --- DIALOGUE DE MODIFICATION  ---
    private void showEditTrainDialog() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un train à modifier", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTrain = (int) tableModel.getValueAt(selectedRow, 0);
        Train train = trainDAO.getTrainById(idTrain);
        if (train == null) {
            JOptionPane.showMessageDialog(this, "Impossible de charger le train.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier Train", true);
        JPanel mainPanel = UIStyle.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNumero = UIStyle.createStyledTextField(); txtNumero.setText(train.getNumeroTrain());
        JComboBox<String> comboType = UIStyle.<String>createStyledComboBox();
        comboType.addItem("TGV"); comboType.addItem("NORMAL");
        comboType.setSelectedItem(train.getTypeTrain());
        JTextField txtDestination = UIStyle.createStyledTextField(); txtDestination.setText(train.getDestination());
        JTextField txtHeureDepart = UIStyle.createStyledTextField(); txtHeureDepart.setText(train.getHeureDepart().toString());
        JTextField txtHeureArrivee = UIStyle.createStyledTextField(); txtHeureArrivee.setText(train.getHeureArrivee().toString());
        JTextField txtPrix = UIStyle.createStyledTextField(); txtPrix.setText(String.valueOf(train.getPrixBase()));

        var formAdder = new Object() {
            void add(String labelText, JComponent component, int row) {
                // Label (colonne 0)
                gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
                JLabel label = UIStyle.createSubtitleLabel(labelText);
                label.setHorizontalAlignment(JLabel.RIGHT);
                formPanel.add(label, gbc);

                // Champ (colonne 1)
                gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
                formPanel.add(component, gbc);
            }
        };

        int y = 0;
        formAdder.add("Numéro train:", txtNumero, y++);
        formAdder.add("Type:", comboType, y++);
        formAdder.add("Destination:", txtDestination, y++);
        formAdder.add("Heure départ :", txtHeureDepart, y++);
        formAdder.add("Heure arrivée :", txtHeureArrivee, y++);
        formAdder.add("Prix base (DA):", txtPrix, y++);


        // Boutons stylisés
        JButton btnSave = UIStyle.createBlueButton(" Sauvegarder", null);
        JButton btnCancel = UIStyle.createRedButton(" Annuler");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(UIStyle.createTitleLabel("Modifier Train"), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        btnSave.addActionListener(e -> {
            try {
                // ... (Logique de modification)
                train.setNumeroTrain(txtNumero.getText());
                train.setTypeTrain((String) comboType.getSelectedItem());
                train.setDestination(txtDestination.getText());
                train.setHeureDepart(Time.valueOf(txtHeureDepart.getText()));
                train.setHeureArrivee(Time.valueOf(txtHeureArrivee.getText()));
                train.setPrixBase(Double.parseDouble(txtPrix.getText()));

                if (trainDAO.updateTrain(train)) {
                    JOptionPane.showMessageDialog(dialog, "Train modifié avec succès!");
                    loadTrains();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Format de données invalide. Assurez-vous d'utiliser HH:MM:SS pour l'heure et un nombre pour le prix.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un train à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTrain = (int) tableModel.getValueAt(selectedRow, 0);
        String numeroTrain = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce train " + numeroTrain + "?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (trainDAO.deleteTrain(idTrain)) {
                JOptionPane.showMessageDialog(this, "Train supprimé avec succès!");
                loadTrains();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}