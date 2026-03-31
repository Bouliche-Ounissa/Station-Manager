import java.sql.Date;

public class Gestionnaire {
    private int idGestionnaire;
    private int idGare;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Date dateEmbauche;

    public Gestionnaire() {}

    public Gestionnaire(int idGare, String nom, String prenom, String email, String motDePasse) {
        this.idGare = idGare;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public int getIdGestionnaire() { return idGestionnaire; }
    public void setIdGestionnaire(int idGestionnaire) { this.idGestionnaire = idGestionnaire; }

    public int getIdGare() { return idGare; }
    public void setIdGare(int idGare) { this.idGare = idGare; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Date getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(Date dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public String getNomComplet() {
        return nom + " " + prenom;
    }

    @Override
    public String toString() {
        return getNomComplet() + " - " + email;
    }
}
