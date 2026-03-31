import java.util.List;

public interface HistoriqueVenteDao {
    int create(HistoriqueVente h) throws Exception;
    List<HistoriqueVente> findByGestionnaire(int idGestionnaire) throws Exception;
    List<HistoriqueVente> findAll() throws Exception;
    List<HistoriqueVente> findByGare(int idGare) throws Exception;
}
