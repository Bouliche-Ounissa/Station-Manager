import java.time.LocalDateTime;

public class Billet {
    private int idBillet;
    private int idGestionnaire;
    private int idTrain;
    private String typeBillet;
    private LocalDateTime dateAchat;
    private double prix;
    private String statut;
    private String source;

    public Billet() {}

    public Billet(int idGestionnaire, int idTrain, String typeBillet, double prix) {
        this.idGestionnaire = idGestionnaire;
        this.idTrain = idTrain;
        this.typeBillet = typeBillet;
        this.prix = prix;
        this.dateAchat = LocalDateTime.now();
        this.statut = "vendu";
        this.source = "gestionnaire";
    }

    public Billet(int idTrain, String typeBillet, double prix) {
        this.idGestionnaire = 0;
        this.idTrain = idTrain;
        this.typeBillet = typeBillet;
        this.prix = prix;
        this.dateAchat = LocalDateTime.now();
        this.statut = "disponible";
        this.source = "admin";
    }

    // Getters et setters
    public int getIdBillet() { return idBillet; }
    public void setIdBillet(int idBillet) { this.idBillet = idBillet; }

    public int getIdGestionnaire() { return idGestionnaire; }
    public void setIdGestionnaire(int idGestionnaire) { this.idGestionnaire = idGestionnaire; }

    public int getIdTrain() { return idTrain; }
    public void setIdTrain(int idTrain) { this.idTrain = idTrain; }

    public String getTypeBillet() { return typeBillet; }
    public void setTypeBillet(String typeBillet) { this.typeBillet = typeBillet; }

    public LocalDateTime getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return typeBillet + " - " + String.format("%.2f €", prix);
    }
}
