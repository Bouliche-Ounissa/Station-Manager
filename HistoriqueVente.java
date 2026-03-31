import java.time.LocalDateTime;

public class HistoriqueVente {
    private int id;
    private int idGestionnaire;
    private int idBillet;
    private LocalDateTime dateVente;
    private double montant;

    public HistoriqueVente() {}

    public HistoriqueVente(int idGestionnaire, int idBillet, LocalDateTime dateVente, double montant) {
        this.idGestionnaire = idGestionnaire;
        this.idBillet = idBillet;
        this.dateVente = dateVente;
        this.montant = montant;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdGestionnaire() { return idGestionnaire; }
    public void setIdGestionnaire(int idGestionnaire) { this.idGestionnaire = idGestionnaire; }

    public int getIdBillet() { return idBillet; }
    public void setIdBillet(int idBillet) { this.idBillet = idBillet; }

    public LocalDateTime getDateVente() { return dateVente; }
    public void setDateVente(LocalDateTime dateVente) { this.dateVente = dateVente; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
}
