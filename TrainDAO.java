import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    public boolean addTrain(Train train) {
        String sql = "INSERT INTO Train (id_gare, type_train, numero_train, destination, heure_depart, heure_arrivee, prix_base) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, train.getIdGare());
            stmt.setString(2, train.getTypeTrain());
            stmt.setString(3, train.getNumeroTrain());
            stmt.setString(4, train.getDestination());
            stmt.setTime(5, train.getHeureDepart());
            stmt.setTime(6, train.getHeureArrivee());
            stmt.setDouble(7, train.getPrixBase());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrain(int idTrain) {
        String sql = "DELETE FROM Train WHERE id_train = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTrain);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrain(Train t) {
        String sql = "UPDATE Train SET numero_train=?, type_train=?, destination=?, heure_depart=?, heure_arrivee=?, prix_base=? WHERE id_train=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNumeroTrain());
            ps.setString(2, t.getTypeTrain());
            ps.setString(3, t.getDestination());
            ps.setTime(4, t.getHeureDepart());
            ps.setTime(5, t.getHeureArrivee());
            ps.setDouble(6, t.getPrixBase());
            ps.setInt(7, t.getIdTrain());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Train getTrainById(int idTrain) {
        Train train = null;
        String sql = "SELECT * FROM Train WHERE id_train = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTrain);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                train = new Train();
                train.setIdTrain(rs.getInt("id_train"));
                train.setIdGare(rs.getInt("id_gare"));
                train.setTypeTrain(rs.getString("type_train"));
                train.setNumeroTrain(rs.getString("numero_train"));
                train.setDestination(rs.getString("destination"));
                train.setHeureDepart(rs.getTime("heure_depart"));
                train.setHeureArrivee(rs.getTime("heure_arrivee"));
                train.setPrixBase(rs.getDouble("prix_base"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return train;
    }

    public List<Train> getTrainsByGare(int idGare) {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM Train WHERE id_gare = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGare);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Train train = new Train();
                train.setIdTrain(rs.getInt("id_train"));
                train.setIdGare(rs.getInt("id_gare"));
                train.setTypeTrain(rs.getString("type_train"));
                train.setNumeroTrain(rs.getString("numero_train"));
                train.setDestination(rs.getString("destination"));
                train.setHeureDepart(rs.getTime("heure_depart"));
                train.setHeureArrivee(rs.getTime("heure_arrivee"));
                train.setPrixBase(rs.getDouble("prix_base"));
                trains.add(train);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trains;
    }
}
