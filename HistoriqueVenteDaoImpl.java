import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueVenteDaoImpl implements HistoriqueVenteDao {

    @Override
    public int create(HistoriqueVente h) throws Exception {
        String sql = "INSERT INTO Historique_Ventes (id_gestionnaire, id_billet, date_vente, montant) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, h.getIdGestionnaire());
            ps.setInt(2, h.getIdBillet());
            ps.setTimestamp(3, Timestamp.valueOf(h.getDateVente() == null ? LocalDateTime.now() : h.getDateVente()));
            ps.setDouble(4, h.getMontant());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    h.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public List<HistoriqueVente> findByGestionnaire(int idGestionnaire) throws Exception {
        List<HistoriqueVente> list = new ArrayList<>();
        String sql = "SELECT * FROM Historique_Ventes WHERE id_gestionnaire = ? ORDER BY date_vente DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idGestionnaire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistoriqueVente h = new HistoriqueVente();
                    h.setId(rs.getInt("id_historique"));
                    h.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                    h.setIdBillet(rs.getInt("id_billet"));
                    h.setDateVente(rs.getTimestamp("date_vente").toLocalDateTime());
                    h.setMontant(rs.getDouble("montant"));
                    list.add(h);
                }
            }
        }
        return list;
    }

    public List<HistoriqueVente> findByGare(int idGare) throws Exception {
        List<HistoriqueVente> list = new ArrayList<>();

        String sql =
                "SELECT hv.* FROM Historique_Ventes hv " +
                        "JOIN Billet b ON hv.id_billet = b.id_billet " +
                        "JOIN Train t ON b.id_train = t.id_train " +
                        "WHERE t.id_gare = ? " +
                        "ORDER BY hv.date_vente DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idGare);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistoriqueVente h = new HistoriqueVente();
                    h.setId(rs.getInt("id_historique"));
                    h.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                    h.setIdBillet(rs.getInt("id_billet"));
                    h.setDateVente(rs.getTimestamp("date_vente").toLocalDateTime());
                    h.setMontant(rs.getDouble("montant"));
                    list.add(h);
                }
            }
        }

        return list;
    }
    @Override
    public List<HistoriqueVente> findAll() throws Exception {
        List<HistoriqueVente> list = new ArrayList<>();
        String sql = "SELECT * FROM Historique_Ventes ORDER BY date_vente DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HistoriqueVente h = new HistoriqueVente();
                h.setId(rs.getInt("id_historique"));
                h.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                h.setIdBillet(rs.getInt("id_billet"));
                h.setDateVente(rs.getTimestamp("date_vente").toLocalDateTime());
                h.setMontant(rs.getDouble("montant"));
                list.add(h);
            }
        }
        return list;
    }
}
