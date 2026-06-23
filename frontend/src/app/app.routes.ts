import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { QuemSomos } from './pages/quem-somos/quem-somos';
import { Enderecos } from './pages/enderecos/enderecos';
import { Reunioes } from './pages/reunioes/reunioes';
import { Autoaval } from './pages/autoaval/autoaval';
import { DozePassos } from './pages/doze-passos/doze-passos';
import { Relatos } from './pages/relatos/relatos';
import { Contato } from './pages/contato/contato';
import { Dashboard } from './pages/dashboard/dashboard';
import { Avisos } from './pages/avisos/avisos';
import { Reflexoes } from './pages/reflexoes/reflexoes';
import { LiteraturaComponent } from './pages/literatura/literatura';
import { Contribuir } from './pages/contribuir/contribuir';
import { Podcast } from './pages/podcast/podcast';
import {Anonimato} from './pages/anonimato/anonimato';
import {JogoCompulsivo} from './pages/jogo-compulsivo/jogo-compulsivo';
import {NovaChance} from './pages/nova-chance/nova-chance';
import {PerguntasFrequentes} from './pages/perguntas-frequentes/perguntas-frequentes';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'avisos', component: Avisos },
  { path: 'quem-somos', component: QuemSomos },
  { path: 'reunioes', component: Reunioes },
  { path: 'enderecos', component: Enderecos},
  { path: 'autoaval', component: Autoaval },
  { path: 'doze-passos', component: DozePassos },
  { path: 'reflexoes', component: Reflexoes },
  { path: 'literatura', component: LiteraturaComponent },
  { path: 'relatos', component: Relatos },
  { path: 'contribuir', component: Contribuir },
  { path: 'contato', component: Contato },
  { path: 'dashboard', component: Dashboard },
  { path: 'podcast', component: Podcast },
  { path: 'anonimato', component: Anonimato },
  { path: 'jogo-compulsivo', component: JogoCompulsivo },
  { path: 'uma-nova-chance', component: NovaChance },
  { path: 'perguntas-frequentes', component: PerguntasFrequentes }
];
