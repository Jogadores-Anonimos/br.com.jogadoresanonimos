package br.com.jogadoresanonimos.auth.repository;

import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import jakarta.persistence.PersistenceException;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("qa-integration")
class EmailConfirmacaoTokenRepositoryIT {

    @Autowired
    EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    DataSource dataSource;

    private String sufixoCurto() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Usuario criarUsuario(String sufixo) {
        Usuario usuario = new Usuario("qa-emt-" + sufixo, "qa-email-token-" + sufixo + "@example.com",
                "hashDeSenhaFake", Role.PARTICIPANTE);
        return entityManager.persistAndFlush(usuario);
    }

    @Test
    void schemaJpaValidaTabelaEmailConfirmacaoTokenComTodasAsColunas() {
        Usuario usuario = criarUsuario(sufixoCurto());
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuario.getId(), "hash-" + UUID.randomUUID(),
                Instant.now().plus(24, ChronoUnit.HOURS));
        EmailConfirmacaoToken salvo = entityManager.persistAndFlush(token);
        entityManager.clear();
        EmailConfirmacaoToken recarregado = emailConfirmacaoTokenRepository.findById(salvo.getId()).orElseThrow();
        assertThat(recarregado.getUsuarioId()).isEqualTo(usuario.getId());
        assertThat(recarregado.getUsadoEm()).isNull();
        assertThat(recarregado.getExpiraEm()).isAfter(Instant.now());
    }

    @Test
    void tokenHash_deveTerConstraintUnique() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String tokenHashDuplicado = "hash-duplicado-" + UUID.randomUUID();
        entityManager.persistAndFlush(new EmailConfirmacaoToken(usuario.getId(), tokenHashDuplicado,
                Instant.now().plus(24, ChronoUnit.HOURS)));
        EmailConfirmacaoToken duplicata = new EmailConfirmacaoToken(usuario.getId(), tokenHashDuplicado,
                Instant.now().plus(24, ChronoUnit.HOURS));
        assertThatThrownBy(() -> entityManager.persistAndFlush(duplicata))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deleteDoUsuario_deveApagarTokensPorCascade() throws Exception {
        String sufixo = sufixoCurto();
        UUID usuarioId;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into usuario (id, apelido, email, senha_hash, role, criado_em, senha_alterada_em) " +
                             "values (gen_random_uuid(), ?, ?, 'hashDeSenhaFake', 'PARTICIPANTE', now(), now()) returning id")) {
            statement.setString(1, "qa-emt-cascade-" + sufixo);
            statement.setString(2, "qa-emt-cascade-" + sufixo + "@example.com");
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                usuarioId = (UUID) resultSet.getObject("id");
            }
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into email_confirmacao_token (id, usuario_id, token_hash, expira_em, criado_em) " +
                             "values (gen_random_uuid(), ?, ?, now() + interval '24 hours', now())")) {
            statement.setObject(1, usuarioId);
            statement.setString(2, "hash-cascade-" + UUID.randomUUID());
            statement.executeUpdate();
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from usuario where id = ?")) {
            statement.setObject(1, usuarioId);
            statement.executeUpdate();
        }
        assertThat(emailConfirmacaoTokenRepository.findAll())
                .noneMatch(t -> t.getUsuarioId().equals(usuarioId));
    }

    @Test
    void findByTokenHash_deveRetornarVazioQuandoInexistente() {
        assertThat(emailConfirmacaoTokenRepository.findByTokenHash("hash-que-nao-existe-" + UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void colunaEmailConfirmadoEmDoUsuario_deveSerNullableSemDefault() {
        Usuario usuario = criarUsuario(sufixoCurto());
        entityManager.clear();
        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmailConfirmadoEm()).isNull();
    }

    @Test
    void usuarioExistenteAntesDaMigrationV5_continuaLegivelComEmailConfirmadoEmNulo() throws Exception {
        UUID usuarioId;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into usuario (id, apelido, email, senha_hash, role, criado_em, senha_alterada_em) " +
                             "values (gen_random_uuid(), ?, ?, 'hashDeSenhaFake', 'PARTICIPANTE', now(), now()) returning id")) {
            String sufixo = sufixoCurto();
            statement.setString(1, "qa-legado-" + sufixo);
            statement.setString(2, "qa-legado-" + sufixo + "@example.com");
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                usuarioId = (UUID) resultSet.getObject("id");
            }
        }
        Usuario recarregado = usuarioRepository.findById(usuarioId).orElseThrow();
        assertThat(recarregado.getEmailConfirmadoEm()).isNull();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement delete = connection.prepareStatement("delete from usuario where id = ?")) {
            delete.setObject(1, usuarioId);
            delete.executeUpdate();
        }
    }

    @Test
    void indiceUsuarioId_deveExistirNaTabela() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "select indexname from pg_indexes where tablename = 'email_confirmacao_token' " +
                             "and indexname = 'idx_email_confirmacao_token_usuario'")) {
            assertThat(resultSet.next()).isTrue();
        }
    }
}
