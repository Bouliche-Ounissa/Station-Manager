import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BilletDAO {

    // Ajouter un billet
    public boolean addBillet(Billet billet) {
        String sql = "INSERT INTO Billet (id_gestionnaire, id_train, type_billet, prix, date_achat, statut) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (billet.getIdGestionnaire() == 0) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, billet.getIdGestionnaire());
            }

            stmt.setInt(2, billet.getIdTrain());
            stmt.setString(3, billet.getTypeBillet());
            stmt.setDouble(4, billet.getPrix());
            stmt.setString(5, billet.getStatut());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        billet.setIdBillet(keys.getInt(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println(" Erreur addBillet : " + e.getMessage());
            return false;
        }
    }
    public List<Billet> getBilletsDisponiblesByGare(int idGare) {
        List<Billet> billets = new ArrayList<>();

        String sql = "SELECT b.* FROM Billet b " +
                "JOIN Train t ON b.id_train = t.id_train " +
                "WHERE t.id_gare = ? AND b.statut = 'Disponible'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGare);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Billet billet = new Billet();
                    billet.setIdBillet(rs.getInt("id_billet"));
                    billet.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                    billet.setIdTrain(rs.getInt("id_train"));
                    billet.setTypeBillet(rs.getString("type_billet"));
                    billet.setPrix(rs.getDouble("prix"));

                    Timestamp ts = rs.getTimestamp("date_achat");
                    if (ts != null) {
                        billet.setDateAchat(ts.toLocalDateTime());
                    }

                    billet.setStatut(rs.getString("statut"));

                    billets.add(billet);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur getBilletsDisponiblesByGare : " + e.getMessage());
        }

        return billets;
    }

    // Modifier un billet
    public boolean updateBillet(Billet billet) {
        String sql = "UPDATE Billet SET id_gestionnaire=?, id_train=?, type_billet=?, prix=?, statut=? WHERE id_billet=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billet.getIdGestionnaire());
            stmt.setInt(2, billet.getIdTrain());
            stmt.setString(3, billet.getTypeBillet());
            stmt.setDouble(4, billet.getPrix());
            stmt.setString(5, billet.getStatut());
            stmt.setInt(6, billet.getIdBillet());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur updateBillet : " + e.getMessage());
            return false;
        }
    }

    // Supprimer un billet
    public boolean deleteBillet(int idBillet) {
        String sql = "DELETE FROM Billet WHERE id_billet=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBillet);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur deleteBillet : " + e.getMessage());
            return false;
        }
    }
    // Vendre un billet
    public boolean vendreBillet(int idBillet, int idGestionnaire) {
        String sql = "UPDATE Billet SET id_gestionnaire = ?, statut = 'vendu', date_achat = NOW() " +
                "WHERE id_billet = ? AND statut = 'Disponible'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGestionnaire);
            stmt.setInt(2, idBillet);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur vendreBillet : " + e.getMessage());
            return false;
        }
    }
    public List<Billet> getBilletsDisponiblesByTrain(int idTrain) {
        List<Billet> billets = new ArrayList<>();

        String sql = "SELECT * FROM Billet WHERE id_train = ? AND statut = 'Disponible'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTrain);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Billet billet = new Billet();
                billet.setIdBillet(rs.getInt("id_billet"));
                billet.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                billet.setIdTrain(rs.getInt("id_train"));
                billet.setTypeBillet(rs.getString("type_billet"));
                billet.setPrix(rs.getDouble("prix"));
                billet.setDateAchat(rs.getTimestamp("date_achat").toLocalDateTime());
                billet.setStatut(rs.getString("statut"));

                billets.add(billet);
            }

        } catch (SQLException e) {
            System.err.println("Erreur getBilletsDisponiblesByTrain: " + e.getMessage());
        }

        return billets;
    }



    // Récupérer la liste des billets
    public List<Billet> getAllBillets() {
        List<Billet> list = new ArrayList<>();
        String sql = "SELECT * FROM Billet";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Billet billet = new Billet();

                billet.setIdBillet(rs.getInt("id_billet"));
                billet.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                billet.setIdTrain(rs.getInt("id_train"));
                billet.setTypeBillet(rs.getString("type_billet"));
                billet.setPrix(rs.getDouble("prix"));

                Timestamp ts = rs.getTimestamp("date_achat");
                if (ts != null)
                    billet.setDateAchat(ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                billet.setStatut(rs.getString("statut"));

                list.add(billet);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllBillets : " + e.getMessage());
        }

        return list;
    }
}
