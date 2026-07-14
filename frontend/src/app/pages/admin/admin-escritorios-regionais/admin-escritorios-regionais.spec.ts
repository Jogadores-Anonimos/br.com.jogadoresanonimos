import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminEscritoriosRegionais } from './admin-escritorios-regionais';
import { EscritorioRegionalService } from '../../../services/escritorio-regional';
import {
  EscritorioServico,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
} from '../../../services/escritorio-regional.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function grupoFake(overrides: Partial<GrupoIntergrupo> = {}): GrupoIntergrupo {
  return {
    id: 'grupo-1',
    estadoId: 'estado-1',
    nome: 'Grupo Fake',
    endereco: 'Rua Fake, 100',
    bairro: 'Bairro Fake',
    cidade: 'Cidade Fake - UF',
    telefones: [],
    diasSemana: ['SEGUNDA'],
    horario: '19:00',
    ordem: 0,
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

function estadoFake(overrides: Partial<EstadoIntergrupo> = {}): EstadoIntergrupo {
  return {
    id: 'estado-1',
    regiaoId: 'regiao-1',
    uf: 'SP',
    nome: 'São Paulo',
    ordem: 0,
    grupos: [],
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

function regiaoFake(overrides: Partial<RegiaoIntergrupo> = {}): RegiaoIntergrupo {
  return {
    id: 'regiao-1',
    nome: 'Região Fake',
    ordem: 0,
    estados: [],
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

function escritorioFake(overrides: Partial<EscritorioServico> = {}): EscritorioServico {
  return {
    id: 'escritorio-1',
    nome: 'Escritório Fake',
    endereco: 'Rua Fake, 200',
    complemento: null,
    cidade: 'Cidade Fake - UF',
    emails: [],
    ordem: 0,
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

describe('AdminEscritoriosRegionais', () => {
  let component: AdminEscritoriosRegionais;
  let fixture: ComponentFixture<AdminEscritoriosRegionais>;
  let httpMock: HttpTestingController;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const regiao1 = regiaoFake({ id: 'regiao-1', nome: 'Região Um', ordem: 0 });
  const regiao2 = regiaoFake({ id: 'regiao-2', nome: 'Região Dois', ordem: 1 });
  const regiao3 = regiaoFake({ id: 'regiao-3', nome: 'Região Três', ordem: 2 });

  const estado1 = estadoFake({ id: 'estado-1', regiaoId: 'regiao-1', uf: 'SP', nome: 'São Paulo', ordem: 0 });
  const estado2 = estadoFake({ id: 'estado-2', regiaoId: 'regiao-1', uf: 'RJ', nome: 'Rio de Janeiro', ordem: 1 });

  const grupo1 = grupoFake({ id: 'grupo-1', estadoId: 'estado-1', nome: 'Grupo Um', ordem: 0 });
  const grupo2 = grupoFake({ id: 'grupo-2', estadoId: 'estado-1', nome: 'Grupo Dois', ordem: 1 });

  const escritorio1 = escritorioFake({ id: 'escritorio-1', nome: 'Escritório Um', ordem: 0 });
  const escritorio2 = escritorioFake({ id: 'escritorio-2', nome: 'Escritório Dois', ordem: 1 });

  function estruturaCompleta(): EscritoriosRegionais {
    return {
      escritorios: [escritorio1, escritorio2],
      regioes: [
        { ...regiao1, estados: [{ ...estado1, grupos: [grupo1, grupo2] }, estado2] },
        regiao2,
        regiao3,
      ],
    };
  }

  beforeEach(async () => {
    serviceSpy = {
      listarEstruturaAdmin: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
      criarRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      editarRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      excluirRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverRegiaoParaCima: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      moverRegiaoParaBaixo: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      criarEstadoAdmin: vi.fn().mockReturnValue(of(apiSuccess(estadoFake()))),
      editarEstadoAdmin: vi.fn().mockReturnValue(of(apiSuccess(estadoFake()))),
      excluirEstadoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverEstadoParaCima: vi.fn().mockReturnValue(of(apiSuccess(estadoFake()))),
      moverEstadoParaBaixo: vi.fn().mockReturnValue(of(apiSuccess(estadoFake()))),
      criarGrupoAdmin: vi.fn().mockReturnValue(of(apiSuccess(grupoFake()))),
      editarGrupoAdmin: vi.fn().mockReturnValue(of(apiSuccess(grupoFake()))),
      excluirGrupoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverGrupoParaCima: vi.fn().mockReturnValue(of(apiSuccess(grupoFake()))),
      moverGrupoParaBaixo: vi.fn().mockReturnValue(of(apiSuccess(grupoFake()))),
      criarEscritorioServicoAdmin: vi.fn().mockReturnValue(of(apiSuccess(escritorioFake()))),
      editarEscritorioServicoAdmin: vi.fn().mockReturnValue(of(apiSuccess(escritorioFake()))),
      excluirEscritorioServicoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverEscritorioServicoParaCima: vi.fn().mockReturnValue(of(apiSuccess(escritorioFake()))),
      moverEscritorioServicoParaBaixo: vi.fn().mockReturnValue(of(apiSuccess(escritorioFake()))),
    };

    await TestBed.configureTestingModule({
      imports: [AdminEscritoriosRegionais],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: EscritorioRegionalService, useValue: serviceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminEscritoriosRegionais);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('criação/edição de estado', () => {
    beforeEach(() => {
      component.verEstados(component.dados.regioes[0]);
    });

    it('cria estado com UF válida com sucesso', () => {
      component.abrirNovoEstado();
      component.formularioEstado.uf = 'MG';

      component.salvarEstado();

      expect(serviceSpy['criarEstadoAdmin']).toHaveBeenCalledWith('regiao-1', { uf: 'MG' });
      expect(component.erroFormularioEstado).toBe('');
    });

    it('não salva e mostra erro quando UF está ausente', () => {
      component.abrirNovoEstado();
      component.formularioEstado.uf = '';

      component.salvarEstado();

      expect(serviceSpy['criarEstadoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioEstado).toBe('Selecione uma UF.');
    });

    it('mostra mensagem orientando a editar o estado existente quando o service retorna CONFLICT', () => {
      serviceSpy['criarEstadoAdmin'].mockReturnValue(
        of(apiError('CONFLICT', 'Já existe um estado com esta UF nesta região.')),
      );
      component.abrirNovoEstado();
      component.formularioEstado.uf = 'SP';

      component.salvarEstado();

      expect(component.erroFormularioEstado).toContain('Edite o estado existente');
    });

    it('edita estado com UF válida com sucesso', () => {
      component.abrirEdicaoEstado(estado1);
      component.formularioEstado.uf = 'PR';

      component.salvarEstado();

      expect(serviceSpy['editarEstadoAdmin']).toHaveBeenCalledWith('regiao-1', 'estado-1', { uf: 'PR' });
    });

    it('mostra erro de conflito também ao editar estado para uma UF já usada na região', () => {
      serviceSpy['editarEstadoAdmin'].mockReturnValue(
        of(apiError('CONFLICT', 'Já existe um estado com esta UF nesta região.')),
      );
      component.abrirEdicaoEstado(estado1);
      component.formularioEstado.uf = 'RJ';

      component.salvarEstado();

      expect(component.erroFormularioEstado).toContain('Edite o estado existente');
    });
  });

  describe('criação/edição de grupo — dias da semana e telefones', () => {
    beforeEach(() => {
      const regiaoCarregada = component.dados.regioes[0];
      component.verEstados(regiaoCarregada);
      component.verGrupos(regiaoCarregada.estados[0]);
    });

    it('salva grupo com múltiplos dias da semana selecionados e horário único', () => {
      component.abrirNovoGrupo();
      component.formularioGrupo.nome = 'Grupo Multi-dias';
      component.formularioGrupo.endereco = 'Rua X, 1';
      component.formularioGrupo.cidade = 'Cidade X - UF';
      component.alternarDiaSemana('SEGUNDA', true);
      component.alternarDiaSemana('QUARTA', true);
      component.alternarDiaSemana('SEXTA', true);
      component.formularioGrupo.horario = '20:00';

      component.salvarGrupo();

      expect(serviceSpy['criarGrupoAdmin']).toHaveBeenCalledWith(
        'estado-1',
        expect.objectContaining({
          diasSemana: ['SEGUNDA', 'QUARTA', 'SEXTA'],
          horario: '20:00',
        }),
      );
    });

    it('não salva grupo quando nenhum dia da semana está selecionado', () => {
      component.abrirNovoGrupo();
      component.formularioGrupo.nome = 'Grupo Sem Dia';
      component.formularioGrupo.endereco = 'Rua X, 1';
      component.formularioGrupo.cidade = 'Cidade X - UF';
      component.formularioGrupo.horario = '20:00';

      component.salvarGrupo();

      expect(serviceSpy['criarGrupoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioGrupo).toContain('dia da semana');
    });

    it('adiciona telefone à lista do formulário antes de salvar', () => {
      component.abrirNovoGrupo();
      expect(component.formularioGrupo.telefones).toHaveLength(0);

      component.adicionarTelefoneGrupo();

      expect(component.formularioGrupo.telefones).toHaveLength(1);
    });

    it('edita telefone já adicionado ao formulário antes de salvar', () => {
      component.abrirNovoGrupo();
      component.adicionarTelefoneGrupo();
      component.formularioGrupo.telefones[0].telefone = '11-90000-1234';
      component.formularioGrupo.telefones[0].label = 'Plantão';

      expect(component.formularioGrupo.telefones[0].telefone).toBe('11-90000-1234');
      expect(component.formularioGrupo.telefones[0].label).toBe('Plantão');
    });

    it('remove telefone da lista do formulário antes de salvar', () => {
      component.abrirNovoGrupo();
      component.adicionarTelefoneGrupo();
      component.adicionarTelefoneGrupo();
      const idParaRemover = component.formularioGrupo.telefones[0].id;

      component.removerTelefoneGrupo(idParaRemover);

      expect(component.formularioGrupo.telefones).toHaveLength(1);
      expect(component.formularioGrupo.telefones.find((t) => t.id === idParaRemover)).toBeUndefined();
    });

    it('envia a lista de telefones editada no payload ao salvar o grupo', () => {
      component.abrirNovoGrupo();
      component.formularioGrupo.nome = 'Grupo Telefones';
      component.formularioGrupo.endereco = 'Rua X, 1';
      component.formularioGrupo.cidade = 'Cidade X - UF';
      component.alternarDiaSemana('TERCA', true);
      component.formularioGrupo.horario = '19:00';
      component.adicionarTelefoneGrupo();
      component.formularioGrupo.telefones[0].telefone = '11-98888-7777';
      component.formularioGrupo.telefones[0].label = 'Central';

      component.salvarGrupo();

      expect(serviceSpy['criarGrupoAdmin']).toHaveBeenCalledWith(
        'estado-1',
        expect.objectContaining({
          telefones: [{ label: 'Central', telefone: '11-98888-7777' }],
        }),
      );
    });

    it('não salva quando há telefone incompleto (sem número) na lista', () => {
      component.abrirNovoGrupo();
      component.formularioGrupo.nome = 'Grupo Telefone Incompleto';
      component.formularioGrupo.endereco = 'Rua X, 1';
      component.formularioGrupo.cidade = 'Cidade X - UF';
      component.alternarDiaSemana('TERCA', true);
      component.formularioGrupo.horario = '19:00';
      component.adicionarTelefoneGrupo();

      component.salvarGrupo();

      expect(serviceSpy['criarGrupoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioGrupo).toContain('telefones incompletos');
    });
  });

  describe('busca de CEP no formulário de grupo', () => {
    beforeEach(() => {
      const regiaoCarregada = component.dados.regioes[0];
      component.verEstados(regiaoCarregada);
      component.verGrupos(regiaoCarregada.estados[0]);
      component.abrirNovoGrupo();
    });

    it('preenche endereço, bairro e cidade automaticamente ao buscar um CEP válido', () => {
      component.cepGrupo = '01310-100';
      component.numeroGrupo = '123';

      component.buscarCepGrupo();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/01310100/json/');
      chamada.flush({
        logradouro: 'Avenida Paulista',
        bairro: 'Bela Vista',
        localidade: 'São Paulo',
        uf: 'SP',
      });

      expect(component.formularioGrupo.endereco).toBe('Avenida Paulista, 123');
      expect(component.formularioGrupo.bairro).toBe('Bela Vista');
      expect(component.formularioGrupo.cidade).toBe('São Paulo - SP');
      expect(component.erroCepGrupo).toBe('');
    });

    it('mostra erro e não altera o formulário quando o CEP não é encontrado', () => {
      component.cepGrupo = '00000-000';

      component.buscarCepGrupo();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/00000000/json/');
      chamada.flush({ erro: true });

      expect(component.erroCepGrupo).toBe('CEP não encontrado.');
      expect(component.formularioGrupo.endereco).toBe('');
    });

    it('mostra erro de CEP inválido sem chamar a API quando o CEP tem menos de 8 dígitos', () => {
      component.cepGrupo = '123';

      component.buscarCepGrupo();

      httpMock.expectNone('https://viacep.com.br/ws/123/json/');
      expect(component.erroCepGrupo).toContain('inválido');
    });

    it('trata falha de rede na busca de CEP sem quebrar o formulário', () => {
      component.cepGrupo = '01310-100';

      component.buscarCepGrupo();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/01310100/json/');
      chamada.error(new ProgressEvent('network error'));

      expect(component.erroCepGrupo).toBe('CEP não encontrado.');
    });
  });

  describe('botões de mover — regiões', () => {
    it('desabilita mover para cima na primeira região e mover para baixo na última', () => {
      expect(component.ehPrimeiraRegiao(regiao1)).toBe(true);
      expect(component.ehUltimaRegiao(regiao1)).toBe(false);
      expect(component.ehPrimeiraRegiao(regiao3)).toBe(false);
      expect(component.ehUltimaRegiao(regiao3)).toBe(true);
      expect(component.ehPrimeiraRegiao(regiao2)).toBe(false);
      expect(component.ehUltimaRegiao(regiao2)).toBe(false);
    });

    it('reflete o disabled correto no DOM para os botões de seta de região', () => {
      const botoesSubir = fixture.nativeElement.querySelectorAll('[aria-label*="Mover região"][aria-label*="para cima"]');
      const botoesDescer = fixture.nativeElement.querySelectorAll('[aria-label*="Mover região"][aria-label*="para baixo"]');
      expect(botoesSubir[0].disabled).toBe(true);
      expect(botoesDescer[botoesDescer.length - 1].disabled).toBe(true);
      expect(botoesSubir[botoesSubir.length - 1].disabled).toBe(false);
      expect(botoesDescer[0].disabled).toBe(false);
    });

    it('chama moverRegiaoParaCima/Baixo com o id correto', () => {
      component.moverRegiao(regiao2, 'cima');
      expect(serviceSpy['moverRegiaoParaCima']).toHaveBeenCalledWith('regiao-2');

      component.moverRegiao(regiao2, 'baixo');
      expect(serviceSpy['moverRegiaoParaBaixo']).toHaveBeenCalledWith('regiao-2');
    });
  });

  describe('botões de mover — estados', () => {
    beforeEach(() => {
      component.verEstados(component.dados.regioes[0]);
    });

    it('desabilita mover para cima no primeiro estado e mover para baixo no último', () => {
      expect(component.ehPrimeiroEstado(estado1)).toBe(true);
      expect(component.ehUltimoEstado(estado1)).toBe(false);
      expect(component.ehPrimeiroEstado(estado2)).toBe(false);
      expect(component.ehUltimoEstado(estado2)).toBe(true);
    });

    it('chama moverEstadoParaCima/Baixo com regiaoId e id corretos', () => {
      component.moverEstado(estado2, 'cima');
      expect(serviceSpy['moverEstadoParaCima']).toHaveBeenCalledWith('regiao-1', 'estado-2');

      component.moverEstado(estado2, 'baixo');
      expect(serviceSpy['moverEstadoParaBaixo']).toHaveBeenCalledWith('regiao-1', 'estado-2');
    });
  });

  describe('botões de mover — grupos', () => {
    beforeEach(() => {
      const regiaoCarregada = component.dados.regioes[0];
      component.verEstados(regiaoCarregada);
      component.verGrupos(regiaoCarregada.estados[0]);
    });

    it('desabilita mover para cima no primeiro grupo e mover para baixo no último', () => {
      expect(component.ehPrimeiroGrupo(grupo1)).toBe(true);
      expect(component.ehUltimoGrupo(grupo1)).toBe(false);
      expect(component.ehPrimeiroGrupo(grupo2)).toBe(false);
      expect(component.ehUltimoGrupo(grupo2)).toBe(true);
    });

    it('chama moverGrupoParaCima/Baixo com estadoId e id corretos', () => {
      component.moverGrupo(grupo2, 'cima');
      expect(serviceSpy['moverGrupoParaCima']).toHaveBeenCalledWith('estado-1', 'grupo-2');

      component.moverGrupo(grupo2, 'baixo');
      expect(serviceSpy['moverGrupoParaBaixo']).toHaveBeenCalledWith('estado-1', 'grupo-2');
    });
  });

  describe('botões de mover — escritórios de serviço', () => {
    it('desabilita mover para cima no primeiro escritório e mover para baixo no último', () => {
      expect(component.ehPrimeiroEscritorio(escritorio1)).toBe(true);
      expect(component.ehUltimoEscritorio(escritorio1)).toBe(false);
      expect(component.ehPrimeiroEscritorio(escritorio2)).toBe(false);
      expect(component.ehUltimoEscritorio(escritorio2)).toBe(true);
    });

    it('reflete o disabled correto no DOM para os botões de seta de escritório', () => {
      const botoesSubir = fixture.nativeElement.querySelectorAll('[aria-label*="Mover escritório"][aria-label*="para cima"]');
      const botoesDescer = fixture.nativeElement.querySelectorAll('[aria-label*="Mover escritório"][aria-label*="para baixo"]');
      expect(botoesSubir[0].disabled).toBe(true);
      expect(botoesDescer[botoesDescer.length - 1].disabled).toBe(true);
    });

    it('chama moverEscritorioServicoParaCima/Baixo com o id correto', () => {
      component.moverEscritorio(escritorio2, 'cima');
      expect(serviceSpy['moverEscritorioServicoParaCima']).toHaveBeenCalledWith('escritorio-2');

      component.moverEscritorio(escritorio2, 'baixo');
      expect(serviceSpy['moverEscritorioServicoParaBaixo']).toHaveBeenCalledWith('escritorio-2');
    });
  });
});
