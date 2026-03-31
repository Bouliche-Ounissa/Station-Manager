import java.sql.Time;

public class Train {
    private int idTrain;
    private int idGare;
    private String typeTrain;
    private String numeroTrain;
    private String destination;
    private Time heureDepart;
    private Time heureArrivee;
    private double prixBase;

    public Train() {}

    public Train(int idGare, String typeTrain, String numeroTrain, String destination, Time heureDepart, Time heureArrivee, double prixBase) {
        this.idGare = idGare;
        this.typeTrain = typeTrain;
        this.numeroTrain = numeroTrain;
        this.destination = destination;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.prixBase = prixBase;
    }

    // Getters et Setters
    public int getIdTrain() { return idTrain; }
    public void setIdTrain(int idTrain) { this.idTrain = idTrain; }

    public int getIdGare() { return idGare; }
    public void setIdGare(int idGare) { this.idGare = idGare; }

    public String getTypeTrain() { return typeTrain; }
    public void setTypeTrain(String typeTrain) { this.typeTrain = typeTrain; }

    public String getNumeroTrain() { return numeroTrain; }
    public void setNumeroTrain(String numeroTrain) { this.numeroTrain = numeroTrain; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Time getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Time heureDepart) { this.heureDepart = heureDepart; }

    public Time getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(Time heureArrivee) { this.heureArrivee = heureArrivee; }

    public double getPrixBase() { return prixBase; }
    public void setPrixBase(double prixBase) { this.prixBase = prixBase; }

    @Override
    public String toString() {
        return numeroTrain + " - " + destination + " (" + heureDepart + " → " + heureArrivee + ")";
    }
}
