import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GareDAO {

    public Gare authenticate(String email, String password) {
        String trimmedEmail = email.trim();
        String trimmedPassword = password.trim();

        System.out.println("=== Tentative de connexion Gare ===");
        System.out.println("Email fourni    : '" + trimmedEmail + "'");
        System.out.println("Mot de passe fourni : '" + trimmedPassword + "'");

        String sql = "SELECT * FROM Gare WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trimmedEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("mot_de_passe").trim();
                System.out.println("Mot de passe en DB : '" + dbPassword + "'");

                if (dbPassword.equals(trimmedPassword)) {
                    System.out.println("✅ Authentification réussie");
                    Gare gare = new Gare();
                    gare.setIdGare(rs.getInt("id_gare"));
                    gare.setNomGare(rs.getString("nom_gare"));
                    gare.setLocalisation(rs.getString("localisation"));
                    gare.setEmail(rs.getString("email"));
                    gare.setMotDePasse(dbPassword);

                    Timestamp dateCreationTimestamp = rs.getTimestamp("date_creation");
                    gare.setDateCreation(dateCreationTimestamp != null ? dateCreationTimestamp.toLocalDateTime() : null);
                    return gare;
                } else {
                    System.out.println("❌ Mot de passe incorrect");
                    return null;
                }
            } else {
                System.out.println("❌ Email inexistant dans la base");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Gare> getAllGares() {
        List<Gare> gares = new ArrayList<>();
        String sql = "SELECT * FROM Gare";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Gare gare = new Gare();
                gare.setIdGare(rs.getInt("id_gare"));
                gare.setNomGare(rs.getString("nom_gare"));
                gare.setLocalisation(rs.getString("localisation"));
                gare.setEmail(rs.getString("email"));
                gares.add(gare);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gares;
    }
}
