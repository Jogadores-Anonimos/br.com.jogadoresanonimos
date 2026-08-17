package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import br.com.jogadoresanonimos.security.JwtService;
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
class ReuniaoAdminEnderecoIT {

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ReuniaoRepository reuniaoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private final List<UUID> reunioesCriadasNoTeste = new ArrayList<>();
    private final List<UUID> usuariosCriadosNoTeste = new ArrayList<>();

    @AfterEach
    void limparDadosDeTeste() {
        reunioesCriadasNoTeste.forEach(reuniaoRepository::deleteById);
        reunioesCriadasNoTeste.clear();
        usuariosCriadosNoTeste.forEach(usuarioRepository::deleteById);
        usuariosCriadosNoTeste.clear();
    }

    private String adminReunioesUrl() {
        return "http://localhost:" + port + "/api/v1/admin/reunioes";
    }

    private String adminReunioesUrl(UUID id) {
        return adminReunioesUrl() + "/" + id;
    }

    private String reunioesPublicasUrl() {
        return "http://localhost:" + port + "/api/v1/reunioes";
    }

    private Usuario criarAdmin(String apelido) {
        Usuario admin = new Usuario(apelido, apelido + "@example.com", passwordEncoder.encode("senhaValida123"), Role.ADMIN);
        Usuario salvo = usuarioRepository.saveAndFlush(admin);
        usuariosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private String tokenDe(Usuario usuario) {
        return jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
    }

    private HttpHeaders headersAutenticados(Usuario usuario) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(usuario));
        return headers;
    }

    private ResponseEntity<String> post(String url, Usuario solicitante, String corpoJson) {
        try {
            return restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(corpoJson, headersAutenticados(solicitante)), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> put(String url, Usuario solicitante, String corpoJson) {
        try {
            return restTemplate.exchange(url, HttpMethod.PUT,
                    new HttpEntity<>(corpoJson, headersAutenticados(solicitante)), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> get(String url) {
        try {
            return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private UUID extrairId(String corpoJson) {
        int inicio = corpoJson.indexOf("\"id\":\"") + 6;
        int fim = corpoJson.indexOf("\"", inicio);
        return UUID.fromString(corpoJson.substring(inicio, fim));
    }

    private void rastrear(UUID id) {
        reunioesCriadasNoTeste.add(id);
    }

    private String corpoReuniaoPresencialComEndereco(String nome) {
        return "{"
                + "\"nome\":\"" + nome + "\","
                + "\"categoria\":\"presencial\","
                + "\"descricao\":\"Grupo de apoio sintético para teste de integração\","
                + "\"dia\":\"Terça-feira\","
                + "\"horario\":\"19h00\","
                + "\"local\":\"Rua Fictícia, 100 - Bairro Teste\","
                + "\"cep\":\"01310-100\","
                + "\"numero\":\"100\","
                + "\"logradouro\":\"Rua Fictícia\","
                + "\"bairro\":\"Bairro Teste\","
                + "\"cidade\":\"São Paulo\","
                + "\"estado\":\"SP\""
                + "}";
    }

    private String corpoReuniaoPresencialSemEndereco(String nome) {
        return "{"
                + "\"nome\":\"" + nome + "\","
                + "\"categoria\":\"presencial\","
                + "\"descricao\":\"Reunião legada simulada, sem endereço estruturado\","
                + "\"dia\":\"Quarta-feira\","
                + "\"horario\":\"18h30\","
                + "\"local\":\"Endereço em texto livre, como cadastro antigo\""
                + "}";
    }

    private String corpoReuniaoVirtualComLink(String nome) {
        return "{"
                + "\"nome\":\"" + nome + "\","
                + "\"categoria\":\"virtual\","
                + "\"descricao\":\"Reunião virtual sintética de teste\","
                + "\"dia\":\"Quinta-feira\","
                + "\"horario\":\"20h00\","
                + "\"local\":\"Sala virtual\","
                + "\"linkReuniao\":\"https://meet.example.com/sala-teste\""
                + "}";
    }

    @Test
    void devePersistirCepENumeroNaCriacaoERetornarOsMesmosValoresNaListagemPublicaEAdmin() {
        Usuario admin = criarAdmin("qa-reuniao-cep-numero");
        String nome = "Grupo QA Endereço " + UUID.randomUUID();
        ResponseEntity<String> criacao = post(adminReunioesUrl(), admin, corpoReuniaoPresencialComEndereco(nome));
        assertThat(criacao.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criacao.getBody()).contains("\"cep\":\"01310-100\"").contains("\"numero\":\"100\"");
        UUID id = extrairId(criacao.getBody());
        rastrear(id);

        ResponseEntity<String> listagemAdmin = get(adminReunioesUrl());
        assertThat(listagemAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listagemAdmin.getBody())
                .contains("\"id\":\"" + id + "\"")
                .contains("\"cep\":\"01310-100\"")
                .contains("\"numero\":\"100\"")
                .contains("\"logradouro\":\"Rua Fictícia\"")
                .contains("\"bairro\":\"Bairro Teste\"")
                .contains("\"cidade\":\"São Paulo\"")
                .contains("\"estado\":\"SP\"");

        ResponseEntity<String> listagemPublica = get(reunioesPublicasUrl());
        assertThat(listagemPublica.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listagemPublica.getBody())
                .contains("\"id\":\"" + id + "\"")
                .contains("\"cep\":\"01310-100\"")
                .contains("\"numero\":\"100\"");
    }

    @Test
    void devePreservarCepENumeroQuandoPutEditaOutroCampoSemTocarNoEndereco() {
        Usuario admin = criarAdmin("qa-reuniao-preserva-endereco");
        String nome = "Grupo QA Preservação " + UUID.randomUUID();
        ResponseEntity<String> criacao = post(adminReunioesUrl(), admin, corpoReuniaoPresencialComEndereco(nome));
        UUID id = extrairId(criacao.getBody());
        rastrear(id);

        String corpoEdicaoSoHorario = "{"
                + "\"nome\":\"" + nome + "\","
                + "\"categoria\":\"presencial\","
                + "\"descricao\":\"Grupo de apoio sintético para teste de integração\","
                + "\"dia\":\"Terça-feira\","
                + "\"horario\":\"20h30\","
                + "\"local\":\"Rua Fictícia, 100 - Bairro Teste\","
                + "\"cep\":\"01310-100\","
                + "\"numero\":\"100\","
                + "\"logradouro\":\"Rua Fictícia\","
                + "\"bairro\":\"Bairro Teste\","
                + "\"cidade\":\"São Paulo\","
                + "\"estado\":\"SP\""
                + "}";
        ResponseEntity<String> edicao = put(adminReunioesUrl(id), admin, corpoEdicaoSoHorario);
        assertThat(edicao.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(edicao.getBody())
                .contains("\"horario\":\"20h30\"")
                .contains("\"cep\":\"01310-100\"")
                .contains("\"numero\":\"100\"");

        ResponseEntity<String> listagemAposEdicao = get(adminReunioesUrl());
        assertThat(listagemAposEdicao.getBody())
                .contains("\"id\":\"" + id + "\"")
                .contains("\"horario\":\"20h30\"")
                .contains("\"cep\":\"01310-100\"")
                .contains("\"numero\":\"100\"");
    }

    @Test
    void deveCriarReuniaoPresencialLegadaSemNenhumCampoDeEnderecoSemFalharValidacao() {
        Usuario admin = criarAdmin("qa-reuniao-legado-sem-endereco");
        String nome = "Grupo QA Legado " + UUID.randomUUID();
        ResponseEntity<String> criacao = post(adminReunioesUrl(), admin, corpoReuniaoPresencialSemEndereco(nome));
        assertThat(criacao.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criacao.getBody())
                .contains("\"success\":true")
                .contains("\"cep\":null")
                .contains("\"numero\":null")
                .contains("\"logradouro\":null")
                .contains("\"bairro\":null")
                .contains("\"cidade\":null")
                .contains("\"estado\":null");
        rastrear(extrairId(criacao.getBody()));
    }

    @Test
    void deveCriarReuniaoVirtualComLinkReuniaoESemNenhumCampoDeEndereco() {
        Usuario admin = criarAdmin("qa-reuniao-virtual-link");
        String nome = "Grupo QA Virtual " + UUID.randomUUID();
        ResponseEntity<String> criacao = post(adminReunioesUrl(), admin, corpoReuniaoVirtualComLink(nome));
        assertThat(criacao.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criacao.getBody())
                .contains("\"success\":true")
                .contains("\"categoria\":\"virtual\"")
                .contains("\"linkReuniao\":\"https://meet.example.com/sala-teste\"")
                .contains("\"cep\":null")
                .contains("\"numero\":null")
                .contains("\"logradouro\":null")
                .contains("\"bairro\":null")
                .contains("\"cidade\":null")
                .contains("\"estado\":null");
        rastrear(extrairId(criacao.getBody()));
    }
}
