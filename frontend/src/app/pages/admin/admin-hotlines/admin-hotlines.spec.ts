import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminHotlines } from './admin-hotlines';
import { HotlineService } from '../../../services/hotline/hotline';
import { HotlineRegiao } from '../../../services/hotline/hotline.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function regiaoFake(overrides: Partial<HotlineRegiao> = {}): HotlineRegiao {
  return {
    id: 'regiao-1',
    uf: 'SP',
    cidade: 'São Paulo',
    telefones: [{ id: 'telefone-1', telefone: '(11) 4321-0000' }],
    ...overrides,
  };
}

describe('AdminHotlines', () => {
  let component: AdminHotlines;
  let fixture: ComponentFixture<AdminHotlines>;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const regiao1 = regiaoFake({ id: 'regiao-1', uf: 'SP', cidade: 'São Paulo' });
  const regiao2 = regiaoFake({ id: 'regiao-2', uf: 'RJ', cidade: 'Rio de Janeiro', telefones: [] });

  beforeEach(async () => {
    serviceSpy = {
      listarHotlinesAdmin: vi.fn().mockReturnValue(of(apiSuccess([regiao1, regiao2]))),
      criarRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      editarRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      excluirRegiaoAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      adicionarTelefoneAdmin: vi.fn().mockReturnValue(of(apiSuccess(regiaoFake()))),
      editarTelefoneAdmin: vi.fn().mockReturnValue(of(apiSuccess({ id: 'telefone-1', telefone: '(11) 4321-0000' }))),
      excluirTelefoneAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
    };

    await TestBed.configureTestingModule({
      imports: [AdminHotlines],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: HotlineService, useValue: serviceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminHotlines);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('criação de região', () => {
    it('cria região com UF e cidade válidas e atualiza a lista localmente', () => {
      component.abrirNovaRegiao();
      component.formularioRegiao.uf = 'MG';
      component.formularioRegiao.cidade = 'Belo Horizonte';

      component.salvarRegiao();

      expect(serviceSpy['criarRegiaoAdmin']).toHaveBeenCalledWith({ uf: 'MG', cidade: 'Belo Horizonte' });
      expect(serviceSpy['listarHotlinesAdmin']).toHaveBeenCalledTimes(1);
      expect(component.modoFormularioRegiao).toBe('fechado');
    });

    it('não salva e mostra erro quando UF está ausente', () => {
      component.abrirNovaRegiao();
      component.formularioRegiao.uf = '';
      component.formularioRegiao.cidade = 'Belo Horizonte';

      component.salvarRegiao();

      expect(serviceSpy['criarRegiaoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioRegiao).toBe('Selecione uma UF.');
    });

    it('não salva e mostra erro quando cidade está vazia', () => {
      component.abrirNovaRegiao();
      component.formularioRegiao.uf = 'MG';
      component.formularioRegiao.cidade = '   ';

      component.salvarRegiao();

      expect(serviceSpy['criarRegiaoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioRegiao).toBe('Cidade é obrigatória.');
    });

    it('mostra mensagem orientando a editar a região existente quando o service retorna CONFLICT', () => {
      serviceSpy['criarRegiaoAdmin'].mockReturnValue(
        of(apiError('CONFLICT', 'Já existe uma região com esta UF e cidade.')),
      );
      component.abrirNovaRegiao();
      component.formularioRegiao.uf = 'SP';
      component.formularioRegiao.cidade = 'São Paulo';

      component.salvarRegiao();

      expect(component.erroFormularioRegiao).toBe(
        'Já existe uma região com esta UF e cidade. Edite a região existente em vez de criar outra.',
      );
    });
  });

  describe('edição de região', () => {
    it('edita região existente enviando o id correto', () => {
      component.abrirEdicaoRegiao(regiao1);
      expect(component.formularioRegiao.uf).toBe('SP');
      expect(component.formularioRegiao.cidade).toBe('São Paulo');

      component.formularioRegiao.uf = 'PR';
      component.formularioRegiao.cidade = 'Curitiba';
      component.salvarRegiao();

      expect(serviceSpy['editarRegiaoAdmin']).toHaveBeenCalledWith('regiao-1', { uf: 'PR', cidade: 'Curitiba' });
    });

    it('mostra erro de conflito também ao editar região para uma combinação já usada', () => {
      serviceSpy['editarRegiaoAdmin'].mockReturnValue(
        of(apiError('CONFLICT', 'Já existe uma região com esta UF e cidade.')),
      );
      component.abrirEdicaoRegiao(regiao1);
      component.formularioRegiao.uf = 'RJ';
      component.formularioRegiao.cidade = 'Rio de Janeiro';

      component.salvarRegiao();

      expect(component.erroFormularioRegiao).toContain('Edite a região existente');
    });
  });

  describe('lista dinâmica de telefones do formulário', () => {
    beforeEach(() => {
      component.abrirFormularioTelefones(regiao1);
    });

    it('adiciona telefone vazio à lista do formulário', () => {
      expect(component.telefonesFormulario).toHaveLength(1);

      component.adicionarTelefoneFormulario();

      expect(component.telefonesFormulario).toHaveLength(2);
      expect(component.telefonesFormulario[1].telefone).toBe('');
    });

    it('remove telefone da lista do formulário pelo id', () => {
      component.adicionarTelefoneFormulario();
      const idParaRemover = component.telefonesFormulario[0].id;

      component.removerTelefoneFormulario(idParaRemover);

      expect(component.telefonesFormulario).toHaveLength(1);
      expect(component.telefonesFormulario.find((t) => t.id === idParaRemover)).toBeUndefined();
    });

    it('não salva quando algum telefone da lista está incompleto', () => {
      component.adicionarTelefoneFormulario();

      component.salvarTelefones(regiao1);

      expect(serviceSpy['adicionarTelefoneAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioTelefones).toBe('Preencha o telefone, ou remova a linha incompleta.');
    });

    it('adiciona um telefone novo via service ao salvar', () => {
      component.adicionarTelefoneFormulario();
      component.telefonesFormulario[1].telefone = '(11) 90000-0000';

      component.salvarTelefones(regiao1);

      expect(serviceSpy['adicionarTelefoneAdmin']).toHaveBeenCalledWith('regiao-1', {
        telefone: '(11) 90000-0000',
      });
    });

    it('exclui telefone removido da lista via service ao salvar', () => {
      const idParaRemover = component.telefonesFormulario[0].id;
      component.removerTelefoneFormulario(idParaRemover);

      component.salvarTelefones(regiao1);

      expect(serviceSpy['excluirTelefoneAdmin']).toHaveBeenCalledWith('telefone-1');
    });
  });
});
