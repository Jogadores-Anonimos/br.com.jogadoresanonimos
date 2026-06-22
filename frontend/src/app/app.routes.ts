import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { QuemSomos } from './pages/quem-somos/quem-somos';
import { Reunioes } from './pages/reunioes/reunioes';
import { Autoaval } from './pages/autoaval/autoaval';
import { DozePassos } from './pages/doze-passos/doze-passos';
import { Relatos } from './pages/relatos/relatos';
import { Contato } from './pages/contato/contato';
import { Dashboard } from './pages/dashboard/dashboard';
import { Avisos } from './pages/avisos/avisos';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'avisos', component: Avisos },
  { path: 'quem-somos', component: QuemSomos },
  { path: 'reunioes', component: Reunioes },
  { path: 'autoaval', component: Autoaval },
  { path: 'doze-passos', component: DozePassos },
  { path: 'relatos', component: Relatos },
  { path: 'contato', component: Contato },
  { path: 'dashboard', component: Dashboard },
];
