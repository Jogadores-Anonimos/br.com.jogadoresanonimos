package br.com.jogadoresanonimos.hotline.repository;

import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;
import br.com.jogadoresanonimos.hotline.entity.HotlineTelefone;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("qa-integration")
class HotlineRegiaoRepositoryIT {

    @Autowired
    HotlineRegiaoRepository hotlineRegiaoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void existsByUfAndCidadeIgnoreCase_deveDetectarDuplicataCaseInsensitive() {
        entityManager.persistAndFlush(new HotlineRegiao(UnidadeFederativa.BA, "Salvador"));
        boolean existeExato = hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.BA, "Salvador");
        boolean existeCaseDiferente = hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.BA, "SALVADOR");
        boolean naoExisteOutraUf = hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(UnidadeFederativa.SP, "Salvador");
        assertThat(existeExato).isTrue();
        assertThat(existeCaseDiferente).isTrue();
        assertThat(naoExisteOutraUf).isFalse();
    }

    @Test
    void indiceUnico_deveImpedirDuasRegioesComMesmaUfECidadeCaseInsensitiveNoBanco() {
        entityManager.persistAndFlush(new HotlineRegiao(UnidadeFederativa.BA, "Salvador"));
        HotlineRegiao duplicata = new HotlineRegiao(UnidadeFederativa.BA, "SALVADOR");
        assertThatThrownBy(() -> entityManager.persistAndFlush(duplicata))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    void findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc_deveOmitirRegiaoSemTelefone() {
        HotlineRegiao comTelefone = entityManager.persistAndFlush(new HotlineRegiao(UnidadeFederativa.BA, "Salvador"));
        entityManager.persistAndFlush(new HotlineTelefone(comTelefone, "(71) 0000-0000"));
        entityManager.persistAndFlush(new HotlineRegiao(UnidadeFederativa.SP, "Campinas"));
        List<HotlineRegiao> publicas = hotlineRegiaoRepository.findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc();
        assertThat(publicas).hasSize(1);
        assertThat(publicas.get(0).getCidade()).isEqualTo("Salvador");
    }

    @Test
    void findAllByOrderByUfAscCidadeAsc_deveIncluirRegiaoSemTelefonePraAdmin() {
        entityManager.persistAndFlush(new HotlineRegiao(UnidadeFederativa.SP, "Campinas"));
        List<HotlineRegiao> todas = hotlineRegiaoRepository.findAllByOrderByUfAscCidadeAsc();
        assertThat(todas).hasSize(1);
        assertThat(todas.get(0).getTelefones()).isEmpty();
    }
}
