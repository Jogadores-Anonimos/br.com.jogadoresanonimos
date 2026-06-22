package site.psi.ads3.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.psi.ads3.entity.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long>{}