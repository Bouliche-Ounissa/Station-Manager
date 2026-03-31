import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class UIStyle {
    public static final Color BLUE_PRIMARY = new Color(30, 120, 210);
    public static final Color BLUE_DARK = new Color(15, 70, 140);
    public static final Color BLUE_LIGHT = new Color(220, 230, 245);
    public static final Color GREEN_SUCCESS = new Color(46, 125, 50);
    public static final Color RED_ERROR = new Color(211, 47, 47);
    public static final Color ORANGE_WARNING = new Color(245, 124, 0);
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color BLUE_PRIMARY1 = new Color(52, 152, 219);
    public static final Color GREEN_PRIMARY = new Color(39, 174, 96);
    public static final Color RED_PRIMARY = new Color(231, 76, 60);
    public static final Color BLUE_DARK1 = new Color(41, 128, 185);
    private static final Border BUTTON_PADDING = new EmptyBorder(10, 20, 10, 20);
    private static final int CORNER_RADIUS = 12;



    private static class RoundedButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JButton button = (JButton) c;
            int width = button.getWidth();
            int height = button.getHeight();

            g2.setColor(button.getBackground());
            Shape shape = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, CORNER_RADIUS, CORNER_RADIUS);
            g2.fill(shape);

            g2.setColor(button.getBackground().darker());
            g2.draw(shape);

            g2.dispose();
            super.paint(g, c);
        }
    }

    public static JButton createBlueButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setBackground(BLUE_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setUI(new RoundedButtonUI()); // ✅ Appliquer le style arrondi

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(iconPath);
                Image img = originalIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            } catch (Exception e) {
                System.err.println("Icône non trouvée: " + iconPath);
            }
        }

        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BLUE_DARK);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BLUE_PRIMARY);
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return btn;
    }

    public static JButton createGreenButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(GREEN_SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setUI(new RoundedButtonUI());
        btn.setBorder(BUTTON_PADDING);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(35, 100, 40));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(GREEN_SUCCESS);
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return btn;
    }

    public static JButton createRedButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(RED_ERROR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setUI(new RoundedButtonUI());
        btn.setBorder(BUTTON_PADDING);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(180, 40, 40));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(RED_ERROR);
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return btn;
    }

    // 2. STYLE DES TABLES (Ajout des lignes zébrées)

    public static void styleTable(JTable table) {
        table.getTableHeader().setBackground(BLUE_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 35));

        table.setRowHeight(30);
        table.setGridColor(BLUE_LIGHT.darker());
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        table.setSelectionBackground(new Color(180, 210, 240));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : BLUE_LIGHT);
                }
                c.setForeground(TEXT_COLOR);

                setHorizontalAlignment(JLabel.CENTER);

                setBorder(new EmptyBorder(0, 5, 0, 5));

                return c;
            }
        });
    }


    public static JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Augmenter le padding
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(BLUE_DARK);
        return label;
    }
    public static ImageIcon loadIcon(String name) {
        String path = "resources/icons/" + name + ".png";
        URL imgURL = UIStyle.class.getClassLoader().getResource(path);

        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } else {
            System.err.println("Impossible de trouver l'icône : " + path);
            return null;
        }
    }
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(BLUE_PRIMARY);
        return label;
    }



    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }


    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return comboBox;
    }
}