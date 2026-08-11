package br.com.jogadoresanonimos.security;

import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.relato.entity.Relato;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import br.com.jogadoresanonimos.relato.repository.RelatoRepository;
import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.usuario.biblioteca.entity.UsuarioArquivo;
import br.com.jogadoresanonimos.usuario.biblioteca.repository.UsuarioArquivoRepository;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
class IdorEndpointsSensiveisIT {

    private static final String SENHA = "senhaValida123";

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RelatoRepository relatoRepository;

    @Autowired
    UsuarioArquivoRepository usuarioArquivoRepository;

    @Autowired
    ArquivoRepository arquivoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private final List<UUID> usuariosCriadosNoTeste = new ArrayList<>();
    private final List<UUID> relatosCriadosNoTeste = new ArrayList<>();
    private final List<UUID> arquivosCriadosNoTeste = new ArrayList<>();

    @AfterEach
    void limparDadosDeTeste() {
        relatosCriadosNoTeste.forEach(relatoRepository::deleteById);
        relatosCriadosNoTeste.clear();
        arquivosCriadosNoTeste.forEach(id -> {
            usuariosCriadosNoTeste.forEach(usuarioId ->
                    usuarioArquivoRepository.deleteById_UsuarioIdAndId_ArquivoId(usuarioId, id));
            arquivoRepository.deleteById(id);
        });
        arquivosCriadosNoTeste.clear();
        usuariosCriadosNoTeste.forEach(usuarioRepository::deleteById);
        usuariosCriadosNoTeste.clear();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String sufixoCurto() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Usuario criarUsuario(String sufixo) {
        Usuario usuario = new Usuario("qa-idor-" + sufixo, "qa-idor-" + sufixo + "@example.com",
                passwordEncoder.encode(SENHA), Role.PARTICIPANTE);
        Usuario salvo = usuarioRepository.saveAndFlush(usuario);
        usuariosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private String tokenDe(Usuario usuario) {
        return jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
    }

    private Relato criarRelato(UUID autorId, StatusRelato status) {
        Relato relato = new Relato("Texto de relato sintético para teste de IDOR, sem dado real de usuário.",
                null, autorId);
        if (status == StatusRelato.approved) {
            relato.aprovar();
        } else if (status == StatusRelato.rejected) {
            relato.rejeitar();
        }
        Relato salvo = relatoRepository.saveAndFlush(relato);
        relatosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private Arquivo criarArquivo(UUID criadoPor, String sufixo) {
        Arquivo arquivo = new Arquivo("arquivo-" + sufixo + ".pdf", "chave-" + sufixo, 1024L,
                "Título de teste " + sufixo, "Descrição de teste", criadoPor);
        Arquivo salvo = arquivoRepository.saveAndFlush(arquivo);
        arquivosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private void vincularNaBiblioteca(UUID usuarioId, UUID arquivoId) {
        usuarioArquivoRepository.saveAndFlush(new UsuarioArquivo(usuarioId, arquivoId));
    }

    private ResponseEntity<String> getRelatoPorId(Usuario autenticadoComo, UUID relatoId) {
        HttpHeaders headers = new HttpHeaders();
        if (autenticadoComo != null) {
            headers.setBearerAuth(tokenDe(autenticadoComo));
        }
        try {
            return restTemplate.exchange(url("/api/v1/relatos/" + relatoId), HttpMethod.GET,
                    new HttpEntity<>(headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> deleteBiblioteca(Usuario autenticadoComo, UUID arquivoId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.exchange(url("/api/v1/usuarios/me/biblioteca/" + arquivoId), HttpMethod.DELETE,
                    new HttpEntity<>(headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> putUsuariosMe(Usuario autenticadoComo, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.exchange(url("/api/v1/usuarios/me"), HttpMethod.PUT,
                    new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> putSenha(Usuario autenticadoComo, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.exchange(url("/api/v1/usuarios/me/senha"), HttpMethod.PUT,
                    new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> postSolicitarTrocaEmail(Usuario autenticadoComo, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.postForEntity(url("/api/v1/usuarios/me/email/solicitar-troca"),
                    new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> getBiblioteca(Usuario autenticadoComo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.exchange(url("/api/v1/usuarios/me/biblioteca"), HttpMethod.GET,
                    new HttpEntity<>(headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> postAdicionarBiblioteca(Usuario autenticadoComo, UUID arquivoId, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.postForEntity(url("/api/v1/usuarios/me/biblioteca/" + arquivoId),
                    new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> postRelato(Usuario autenticadoComo, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(autenticadoComo));
        try {
            return restTemplate.postForEntity(url("/api/v1/relatos"), new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    @Test
    void getRelatoPorId_relatoPendingDeOutroUsuarioRetorna404() {
        Usuario donoB = criarUsuario(sufixoCurto());
        Usuario atacanteA = criarUsuario(sufixoCurto());
        Relato relatoPendingDeB = criarRelato(donoB.getId(), StatusRelato.pending);

        ResponseEntity<String> resposta = getRelatoPorId(atacanteA, relatoPendingDeB.getId());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("RELATO_NAO_ENCONTRADO");
    }

    @Test
    void getRelatoPorId_relatoRejectedDeOutroUsuarioRetorna404() {
        Usuario donoB = criarUsuario(sufixoCurto());
        Usuario atacanteA = criarUsuario(sufixoCurto());
        Relato relatoRejectedDeB = criarRelato(donoB.getId(), StatusRelato.rejected);

        ResponseEntity<String> resposta = getRelatoPorId(atacanteA, relatoRejectedDeB.getId());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("RELATO_NAO_ENCONTRADO");
    }

    @Test
    void getRelatoPorId_relatoInexistenteRetorna404ComMesmoFormatoDoOwnershipNegado() {
        Usuario atacanteA = criarUsuario(sufixoCurto());

        ResponseEntity<String> resposta = getRelatoPorId(atacanteA, UUID.randomUUID());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("RELATO_NAO_ENCONTRADO");
    }

    @Test
    void getRelatoPorId_relatoApprovedDeOutroUsuarioEhPublicoRetorna200() {
        Usuario donoB = criarUsuario(sufixoCurto());
        Usuario visitanteA = criarUsuario(sufixoCurto());
        Relato relatoApprovedDeB = criarRelato(donoB.getId(), StatusRelato.approved);

        ResponseEntity<String> resposta = getRelatoPorId(visitanteA, relatoApprovedDeB.getId());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true");
    }

    @Test
    void deleteBiblioteca_vinculoDeOutroUsuarioNaoEhAfetadoERetorna200Idempotente() {
        Usuario donoB = criarUsuario(sufixoCurto());
        Usuario atacanteA = criarUsuario(sufixoCurto());
        Arquivo arquivoDeB = criarArquivo(donoB.getId(), sufixoCurto());
        vincularNaBiblioteca(donoB.getId(), arquivoDeB.getId());

        ResponseEntity<String> resposta = deleteBiblioteca(atacanteA, arquivoDeB.getId());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true");
        assertThat(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(donoB.getId(), arquivoDeB.getId()))
                .isTrue();
    }

    @Test
    void deleteBiblioteca_arquivoInexistenteRetornaMesmoStatusQueVinculoDeOutroUsuario() {
        Usuario atacanteA = criarUsuario(sufixoCurto());

        ResponseEntity<String> resposta = deleteBiblioteca(atacanteA, UUID.randomUUID());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true");
    }

    @Test
    void deleteBiblioteca_removeApenasOProprioVinculoQuandoExiste() {
        Usuario dono = criarUsuario(sufixoCurto());
        Arquivo arquivo = criarArquivo(dono.getId(), sufixoCurto());
        vincularNaBiblioteca(dono.getId(), arquivo.getId());

        ResponseEntity<String> resposta = deleteBiblioteca(dono, arquivo.getId());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(dono.getId(), arquivo.getId()))
                .isFalse();
    }

    @Test
    void putUsuariosMe_idForjadoNoPayloadNaoAlteraOutroUsuario() {
        Usuario alvo = criarUsuario(sufixoCurto());
        Usuario atacante = criarUsuario(sufixoCurto());
        String apelidoOriginalAlvo = alvo.getApelido();
        String corpo = "{\"apelido\":\"" + atacante.getApelido() + "-novo\",\"id\":\"" + alvo.getId()
                + "\",\"usuarioId\":\"" + alvo.getId() + "\"}";

        ResponseEntity<String> resposta = putUsuariosMe(atacante, corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Usuario alvoRecarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(alvoRecarregado.getApelido()).isEqualTo(apelidoOriginalAlvo);
        Usuario atacanteRecarregado = usuarioRepository.findById(atacante.getId()).orElseThrow();
        assertThat(atacanteRecarregado.getApelido()).isEqualTo(atacante.getApelido() + "-novo");
    }

    @Test
    void putSenha_idForjadoNoPayloadAlteraApenasAPropriaSenha() {
        Usuario alvo = criarUsuario(sufixoCurto());
        Usuario atacante = criarUsuario(sufixoCurto());
        String novaSenha = "novaSenhaForte123";
        String corpo = "{\"senhaAtual\":\"" + SENHA + "\",\"novaSenha\":\"" + novaSenha
                + "\",\"confirmarNovaSenha\":\"" + novaSenha + "\",\"usuarioId\":\"" + alvo.getId() + "\"}";

        ResponseEntity<String> resposta = putSenha(atacante, corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Usuario alvoRecarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(SENHA, alvoRecarregado.getSenhaHash())).isTrue();
        Usuario atacanteRecarregado = usuarioRepository.findById(atacante.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(novaSenha, atacanteRecarregado.getSenhaHash())).isTrue();
    }

    @Test
    void solicitarTrocaEmail_idForjadoNoPayloadNaoGeraTokenParaOutroUsuario() {
        Usuario alvo = criarUsuario(sufixoCurto());
        Usuario atacante = criarUsuario(sufixoCurto());
        String novoEmail = "sequestro-" + sufixoCurto() + "@example.com";
        String corpo = "{\"senhaAtual\":\"" + SENHA + "\",\"novoEmail\":\"" + novoEmail + "\",\"usuarioId\":\""
                + alvo.getId() + "\",\"id\":\"" + alvo.getId() + "\"}";

        ResponseEntity<String> resposta = postSolicitarTrocaEmail(atacante, corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Usuario alvoRecarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(alvoRecarregado.getEmail()).isEqualTo(alvo.getEmail());
    }

    @Test
    void getBiblioteca_idForjadoNoQueryOuHeaderNaoAfetaEscopo() {
        Usuario dono = criarUsuario(sufixoCurto());
        Usuario outroUsuario = criarUsuario(sufixoCurto());
        Arquivo arquivoDoOutro = criarArquivo(outroUsuario.getId(), sufixoCurto());
        vincularNaBiblioteca(outroUsuario.getId(), arquivoDoOutro.getId());

        ResponseEntity<String> resposta = getBiblioteca(dono);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).doesNotContain(arquivoDoOutro.getId().toString());
    }

    @Test
    void postAdicionarBiblioteca_idForjadoNoPayloadNaoVinculaAOutroUsuario() {
        Usuario alvo = criarUsuario(sufixoCurto());
        Usuario atacante = criarUsuario(sufixoCurto());
        Arquivo arquivo = criarArquivo(atacante.getId(), sufixoCurto());
        String corpo = "{\"usuarioId\":\"" + alvo.getId() + "\",\"id\":\"" + alvo.getId() + "\"}";

        ResponseEntity<String> resposta = postAdicionarBiblioteca(atacante, arquivo.getId(), corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(atacante.getId(), arquivo.getId()))
                .isTrue();
        assertThat(usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(alvo.getId(), arquivo.getId()))
                .isFalse();
    }

    @Test
    void postRelato_autorIdForjadoNoPayloadNaoAssumeAutoriaDeOutroUsuario() {
        Usuario alvo = criarUsuario(sufixoCurto());
        Usuario atacante = criarUsuario(sufixoCurto());
        String corpo = "{\"texto\":\"Texto sintético de teste com tamanho suficiente para passar na validação de "
                + "cinquenta caracteres mínimos exigidos pelo endpoint de envio de relato.\",\"autorId\":\""
                + alvo.getId() + "\",\"usuarioId\":\"" + alvo.getId() + "\"}";

        ResponseEntity<String> resposta = postRelato(atacante, corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        List<Relato> relatosDoAtacante = relatoRepository.findAll().stream()
                .filter(r -> r.getAutorId().equals(atacante.getId()))
                .toList();
        assertThat(relatosDoAtacante).hasSize(1);
        relatosCriadosNoTeste.add(relatosDoAtacante.get(0).getId());
        List<Relato> relatosDoAlvo = relatoRepository.findAll().stream()
                .filter(r -> r.getAutorId().equals(alvo.getId()))
                .toList();
        assertThat(relatosDoAlvo).isEmpty();
    }
}
