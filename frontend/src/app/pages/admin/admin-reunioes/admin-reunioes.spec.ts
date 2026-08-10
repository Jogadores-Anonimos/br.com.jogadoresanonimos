import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminReunioes } from './admin-reunioes';
import { ReuniaoService } from '../../../services/reuniao/reuniao';
import { Reuniao } from '../../../services/reuniao/reuniao.model';
import { EnderecoLookupService } from '../../../services/endereco-lookup/endereco-lookup';
import { EnderecoViaCep, ResultadoNominatim } from '../../../services/endereco-lookup/endereco-lookup.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function reuniaoFake(overrides: Partial<Reuniao> = {}): Reuniao {
  return {
    id: 'reuniao-1',
    nome: 'Grupo Esperança',
    categoria: 'presencial',
    descricao: 'Reunião de apoio mútuo.',
    dia: 'Segunda',
    horario: '19:00',
    local: 'Rua Fictícia, 100, Bairro Teste, Cidade Teste, TS',
    whatsapp: null,
    diaSemana: 'Segunda',
    periodo: 'Noite',
    latitude: null,
    longitude: null,
    ...overrides,
  };
}

describe('AdminReunioes', () => {
  let component: AdminReunioes;
  let fixture: ComponentFixture<AdminReunioes>;
  let reuniaoServiceSpy: Record<string, ReturnType<typeof vi.fn>>;
  let enderecoLookupServiceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const reuniao1 = reuniaoFake({ id: 'reuniao-1', nome: 'Grupo Esperança', categoria: 'presencial' });
  const reuniao2 = reuniaoFake({
    id: 'reuniao-2',
    nome: 'Grupo Virtual Recomeço',
    categoria: 'virtual',
    diaSemana: 'Quarta',
    local: 'Link enviado por e-mail',
  });

  async function configurarTeste(
    reunioesIniciais: Reuniao[] = [reuniao1, reuniao2],
    listarReunioesAdminOverride?: ReturnType<typeof vi.fn>,
  ): Promise<void> {
    reuniaoServiceSpy = {
      listarReunioesAdmin: listarReunioesAdminOverride ?? vi.fn().mockReturnValue(of(apiSuccess(reunioesIniciais))),
      criarReuniaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(reuniaoFake()))),
      editarReuniaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(reuniaoFake()))),
      excluirReuniaoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
    };
    enderecoLookupServiceSpy = {
      buscarCep: vi.fn().mockReturnValue(
        of<EnderecoViaCep>({
          logradouro: 'Praça da Sé',
          bairro: 'Sé',
          localidade: 'São Paulo',
          uf: 'SP',
        }),
      ),
      geocodificar: vi.fn().mockReturnValue(of<ResultadoNominatim[]>([{ lat: '-23.55', lon: '-46.63' }])),
    };

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [AdminReunioes],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ReuniaoService, useValue: reuniaoServiceSpy },
        { provide: EnderecoLookupService, useValue: enderecoLookupServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminReunioes);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await configurarTeste();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('listagem', () => {
    it('carrega as reuniões ao iniciar e exibe todas quando não há filtro', () => {
      expect(reuniaoServiceSpy['listarReunioesAdmin']).toHaveBeenCalledTimes(1);
      expect(component.reunioes()).toHaveLength(2);
      expect(component.reunioesFiltradas()).toHaveLength(2);
      expect(component.carregando).toBe(false);
      expect(component.erroCarregamento).toBe('');
    });

    it('mostra mensagem de erro quando o carregamento falha', async () => {
      const listarReunioesAdminComErro = vi.fn().mockReturnValue(
        of(apiError('INTERNAL_ERROR', 'Não foi possível carregar as reuniões.')),
      );

      await configurarTeste([], listarReunioesAdminComErro);

      expect(component.erroCarregamento).toBe('Não foi possível carregar as reuniões.');
      expect(component.reunioes()).toHaveLength(0);
    });

    it('trata lista vazia sem erro', async () => {
      await configurarTeste([]);

      expect(component.erroCarregamento).toBe('');
      expect(component.reunioes()).toHaveLength(0);
      expect(component.reunioesFiltradas()).toHaveLength(0);
    });
  });

  describe('filtro', () => {
    it('filtra por texto no nome ou local', () => {
      component.filtroTexto.set('virtual');

      expect(component.reunioesFiltradas()).toHaveLength(1);
      expect(component.reunioesFiltradas()[0].id).toBe('reuniao-2');
    });

    it('filtra por categoria', () => {
      component.filtroCategoria.set('presencial');

      expect(component.reunioesFiltradas()).toHaveLength(1);
      expect(component.reunioesFiltradas()[0].id).toBe('reuniao-1');
    });

    it('filtra por dia da semana', () => {
      component.filtroDiaSemana.set('Quarta');

      expect(component.reunioesFiltradas()).toHaveLength(1);
      expect(component.reunioesFiltradas()[0].id).toBe('reuniao-2');
    });

    it('retorna lista vazia quando nenhum resultado combina com o filtro', () => {
      component.filtroTexto.set('inexistente-xyz');

      expect(component.reunioesFiltradas()).toHaveLength(0);
    });

    it('limparFiltros reseta todos os filtros', () => {
      component.filtroTexto.set('virtual');
      component.filtroCategoria.set('virtual');
      component.filtroDiaSemana.set('Quarta');

      component.limparFiltros();

      expect(component.filtroTexto()).toBe('');
      expect(component.filtroCategoria()).toBe('');
      expect(component.filtroDiaSemana()).toBe('');
      expect(component.filtroAtivo()).toBe(false);
    });
  });

  describe('formulário — salvar', () => {
    it('não salva e mostra erro quando campos obrigatórios estão vazios', () => {
      component.abrirNovo();

      component.salvar();

      expect(reuniaoServiceSpy['criarReuniaoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormulario).toContain('obrigatórios');
      expect(component.errosCampo['nome']).toBeTruthy();
    });

    it('cria reunião com dados válidos e volta para a lista', () => {
      component.abrirNovo();
      component.formulario.nome = 'Novo Grupo';
      component.formulario.descricao = 'Descrição válida.';
      component.formulario.diaSemana = 'Terça';
      component.formulario.horario = '20:00';
      component.formulario.local = 'Rua Nova, 1';

      component.salvar();

      expect(reuniaoServiceSpy['criarReuniaoAdmin']).toHaveBeenCalledWith(
        expect.objectContaining({
          nome: 'Novo Grupo',
          descricao: 'Descrição válida.',
          diaSemana: 'Terça',
          horario: '20:00',
          local: 'Rua Nova, 1',
        }),
      );
      expect(component.modo).toBe('lista');
    });

    it('edita reunião existente enviando o id correto', () => {
      component.abrirEdicao(reuniao1);
      component.formulario.descricao = 'Descrição atualizada.';

      component.salvar();

      expect(reuniaoServiceSpy['editarReuniaoAdmin']).toHaveBeenCalledWith(
        'reuniao-1',
        expect.objectContaining({ descricao: 'Descrição atualizada.' }),
      );
    });

    it('mostra erro do service e mapeia erros de campo quando o salvamento falha', () => {
      reuniaoServiceSpy['criarReuniaoAdmin'].mockReturnValue(
        of(
          apiError('VALIDATION_ERROR', 'Verifique os campos informados.', [
            { campo: 'nome', mensagem: 'Nome já utilizado por outra reunião.' },
          ]),
        ),
      );
      component.abrirNovo();
      component.formulario.nome = 'Grupo Duplicado';
      component.formulario.descricao = 'Descrição válida.';
      component.formulario.diaSemana = 'Terça';
      component.formulario.horario = '20:00';
      component.formulario.local = 'Rua Nova, 1';

      component.salvar();

      expect(component.erroFormulario).toBe('Verifique os campos informados.');
      expect(component.errosCampo['nome']).toBe('Nome já utilizado por outra reunião.');
      expect(component.modo).toBe('formulario');
    });

    it('não salva quando latitude informada não é um número válido', () => {
      component.abrirNovo();
      component.formulario.nome = 'Grupo X';
      component.formulario.descricao = 'Descrição válida.';
      component.formulario.diaSemana = 'Terça';
      component.formulario.horario = '20:00';
      component.formulario.local = 'Rua Nova, 1';
      component.formulario.latitude = 'não-é-numero';

      component.salvar();

      expect(reuniaoServiceSpy['criarReuniaoAdmin']).not.toHaveBeenCalled();
      expect(component.errosCampo['latitude']).toBeTruthy();
    });
  });

  describe('busca de CEP e geocodificação', () => {
    beforeEach(() => {
      component.abrirNovo();
    });

    it('não busca quando o CEP tem menos de 8 dígitos', () => {
      component.cep = '123';

      component.buscarCep();

      expect(enderecoLookupServiceSpy['buscarCep']).not.toHaveBeenCalled();
      expect(component.erroCep).toContain('inválido');
    });

    it('preenche endereço e coordenadas ao buscar CEP válido com sucesso', () => {
      component.cep = '01001-000';
      component.numero = '100';

      component.buscarCep();

      expect(enderecoLookupServiceSpy['buscarCep']).toHaveBeenCalledWith('01001000');
      expect(enderecoLookupServiceSpy['geocodificar']).toHaveBeenCalledWith('Praça da Sé, 100, Sé, São Paulo, SP');
      expect(component.formulario.local).toBe('Praça da Sé, 100, Sé, São Paulo, SP');
      expect(component.formulario.latitude).toBe('-23.55');
      expect(component.formulario.longitude).toBe('-46.63');
      expect(component.buscandoCep).toBe(false);
    });

    it('mostra erro quando o CEP não é encontrado', () => {
      enderecoLookupServiceSpy['buscarCep'].mockReturnValue(of(null));
      component.cep = '99999999';

      component.buscarCep();

      expect(component.erroCep).toBe('CEP não encontrado.');
      expect(component.buscandoCep).toBe(false);
      expect(enderecoLookupServiceSpy['geocodificar']).not.toHaveBeenCalled();
    });

    it('mostra erro quando o ViaCEP retorna endereço marcado como erro', () => {
      enderecoLookupServiceSpy['buscarCep'].mockReturnValue(of({ erro: true } as EnderecoViaCep));
      component.cep = '00000000';

      component.buscarCep();

      expect(component.erroCep).toBe('CEP não encontrado.');
      expect(enderecoLookupServiceSpy['geocodificar']).not.toHaveBeenCalled();
    });

    it('mostra aviso quando o endereço é encontrado mas a geocodificação não retorna coordenadas', () => {
      enderecoLookupServiceSpy['geocodificar'].mockReturnValue(of([]));
      component.cep = '01001000';

      component.buscarCep();

      expect(component.erroCep).toContain('não foi possível obter as coordenadas');
      expect(component.buscandoCep).toBe(false);
    });
  });

  describe('exclusão com confirmação', () => {
    it('pedirConfirmacaoExclusao marca a reunião como pendente de confirmação sem excluir', () => {
      component.pedirConfirmacaoExclusao(reuniao1);

      expect(component.confirmandoExclusao).toBe('reuniao-1');
      expect(reuniaoServiceSpy['excluirReuniaoAdmin']).not.toHaveBeenCalled();
    });

    it('cancelarExclusao limpa o estado de confirmação sem excluir', () => {
      component.pedirConfirmacaoExclusao(reuniao1);

      component.cancelarExclusao();

      expect(component.confirmandoExclusao).toBeNull();
      expect(reuniaoServiceSpy['excluirReuniaoAdmin']).not.toHaveBeenCalled();
    });

    it('confirmarExclusao remove a reunião da lista ao ter sucesso', () => {
      component.pedirConfirmacaoExclusao(reuniao1);

      component.confirmarExclusao(reuniao1);

      expect(reuniaoServiceSpy['excluirReuniaoAdmin']).toHaveBeenCalledWith('reuniao-1');
      expect(component.reunioes().find((r) => r.id === 'reuniao-1')).toBeUndefined();
      expect(component.confirmandoExclusao).toBeNull();
    });

    it('mostra erro e mantém a reunião na lista quando a exclusão falha', () => {
      reuniaoServiceSpy['excluirReuniaoAdmin'].mockReturnValue(
        of(apiError('INTERNAL_ERROR', 'Não foi possível excluir a reunião.')),
      );
      component.pedirConfirmacaoExclusao(reuniao1);

      component.confirmarExclusao(reuniao1);

      expect(component.erroExclusao).toBe('Não foi possível excluir a reunião.');
      expect(component.reunioes().find((r) => r.id === 'reuniao-1')).toBeDefined();
    });
  });
});
