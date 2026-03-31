import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireDAO {

    public Gestionnaire authenticate(String email, String password) {
        String trimmedEmail = email.trim();
        String trimmedPassword = password.trim();

        System.out.println("=== Tentative de connexion Gestionnaire ===");
        System.out.println("Email fourni    : '" + trimmedEmail + "'");
        System.out.println("Mot de passe fourni : '" + trimmedPassword + "'");

        String sql = "SELECT * FROM Gestionnaire WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trimmedEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("mot_de_passe").trim();
                System.out.println("Mot de passe en DB : '" + dbPassword + "'");

                if (dbPassword.equals(trimmedPassword)) {
                    System.out.println("✅ Authentification Gestionnaire réussie");
                    Gestionnaire gestionnaire = new Gestionnaire();
                    gestionnaire.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                    gestionnaire.setIdGare(rs.getInt("id_gare"));
                    gestionnaire.setNom(rs.getString("nom"));
                    gestionnaire.setPrenom(rs.getString("prenom"));
                    gestionnaire.setEmail(rs.getString("email"));
                    gestionnaire.setMotDePasse(dbPassword);
                    gestionnaire.setDateEmbauche(rs.getDate("date_embauche"));
                    return gestionnaire;
                } else {
                    System.out.println("❌ Mot de passe incorrect Gestionnaire");
                    return null;
                }
            } else {
                System.out.println("❌ Email inexistant Gestionnaire");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL Gestionnaire : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addGestionnaire(Gestionnaire gestionnaire) {
        String sql = "INSERT INTO Gestionnaire (id_gare, nom, prenom, email, mot_de_passe) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gestionnaire.getIdGare());
            stmt.setString(2, gestionnaire.getNom());
            stmt.setString(3, gestionnaire.getPrenom());
            stmt.setString(4, gestionnaire.getEmail());
            stmt.setString(5, gestionnaire.getMotDePasse());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Gestionnaire> getGestionnairesByGare(int idGare) {
        List<Gestionnaire> gestionnaires = new ArrayList<>();
        String sql = "SELECT * FROM Gestionnaire WHERE id_gare = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idGare);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Gestionnaire gestionnaire = new Gestionnaire();
                gestionnaire.setIdGestionnaire(rs.getInt("id_gestionnaire"));
                gestionnaire.setIdGare(rs.getInt("id_gare"));
                gestionnaire.setNom(rs.getString("nom"));
                gestionnaire.setPrenom(rs.getString("prenom"));
                gestionnaire.setEmail(rs.getString("email"));
                gestionnaire.setDateEmbauche(rs.getDate("date_embauche"));
                gestionnaires.add(gestionnaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gestionnaires;
    }

    public boolean deleteGestionnaire(int idGestionnaire) {
        String sql = "DELETE FROM Gestionnaire WHERE id_gestionnaire = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idGestionnaire);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
