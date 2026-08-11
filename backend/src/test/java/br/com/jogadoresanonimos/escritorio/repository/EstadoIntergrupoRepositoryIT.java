package br.com.jogadoresanonimos.escritorio.repository;

import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("qa-integration")
class EstadoIntergrupoRepositoryIT {

    @Autowired
    EstadoIntergrupoRepository estadoIntergrupoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    DataSource dataSource;

    @Test
    void contextoJpaValidaSchemaPosV29_semMismatchDeTipoNaColunaUf() {
        RegiaoIntergrupo regiao = entityManager.persistAndFlush(new RegiaoIntergrupo("Sudeste", RegiaoCodigo.SUDESTE, 1));
        EstadoIntergrupo estado = entityManager.persistAndFlush(new EstadoIntergrupo(regiao, UnidadeFederativa.SP, 1));
        entityManager.clear();
        EstadoIntergrupo recarregado = estadoIntergrupoRepository.findById(estado.getId()).orElseThrow();
        assertThat(recarregado.getUf()).isEqualTo(UnidadeFederativa.SP);
    }

    @Test
    void ufPersistida_naoDeveTerPaddingOuTruncamentoAposMigrationParaVarchar() {
        RegiaoIntergrupo regiao = entityManager.persistAndFlush(new RegiaoIntergrupo("Nordeste", RegiaoCodigo.NORDESTE, 1));
        entityManager.persistAndFlush(new EstadoIntergrupo(regiao, UnidadeFederativa.BA, 1));
        entityManager.clear();
        String ufBruta = (String) entityManager.getEntityManager()
                .createNativeQuery("select uf from estado_intergrupo where regiao_id = :regiaoId")
                .setParameter("regiaoId", regiao.getId())
                .getSingleResult();
        assertThat(ufBruta).isEqualTo("BA");
        assertThat(ufBruta).hasSize(2);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void dadoPersistidoAntesDaMigrationV29_continuaLegivelDepois() throws Exception {
        UUID regiaoId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "insert into regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) " +
                             "values (gen_random_uuid(), 'Sul', 1, now(), now()) returning id")) {
            resultSet.next();
            regiaoId = (UUID) resultSet.getObject("id");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into estado_intergrupo (id, regiao_id, uf, ordem, criado_em, atualizado_em) " +
                             "values (gen_random_uuid(), ?, 'PR', 1, now(), now())")) {
            statement.setObject(1, regiaoId);
            statement.executeUpdate();
        }
        List<EstadoIntergrupo> estados = estadoIntergrupoRepository.findAllByRegiaoIdOrderByOrdemAsc(regiaoId);
        assertThat(estados).hasSize(1);
        assertThat(estados.get(0).getUf()).isEqualTo(UnidadeFederativa.PR);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteEstado = connection.prepareStatement("delete from estado_intergrupo where regiao_id = ?");
             PreparedStatement deleteRegiao = connection.prepareStatement("delete from regiao_intergrupo where id = ?")) {
            deleteEstado.setObject(1, regiaoId);
            deleteEstado.executeUpdate();
            deleteRegiao.setObject(1, regiaoId);
            deleteRegiao.executeUpdate();
        }
    }
}
