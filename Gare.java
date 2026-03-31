import java.time.LocalDateTime;

public class Gare {
    private int idGare;
    private String nomGare;
    private String localisation;
    private String email;
    private String motDePasse;
    private LocalDateTime dateCreation;

    public Gare() {}

    public Gare(String nomGare, String localisation, String email, String motDePasse) {
        this.nomGare = nomGare;
        this.localisation = localisation;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public int getIdGare() { return idGare; }
    public void setIdGare(int idGare) { this.idGare = idGare; }

    public String getNomGare() { return nomGare; }
    public void setNomGare(String nomGare) { this.nomGare = nomGare; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return nomGare + " - " + localisation;
    }
}
