import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { BarraDeslizante } from './components/barra-deslizante/barra-deslizante';
import { WhatsappFlutuante } from './components/whatsapp-flutuante/whatsapp-flutuante';
import { VoltarAoTopo } from './components/voltar-ao-topo/voltar-ao-topo';
import { Seo } from './services/seo';
import { Auth } from './services/auth/auth';
import { AvisoEmailNaoConfirmado } from './components/aviso-email-nao-confirmado/aviso-email-nao-confirmado';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar, Footer, BarraDeslizante, WhatsappFlutuante, VoltarAoTopo, AvisoEmailNaoConfirmado],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  private readonly PREFIXOS_SEM_BARRA_DESLIZANTE = ['/minha-conta', '/login', '/dashboard', '/admin'];

  mostrarBarraDeslizante = signal(true);
  private readonly seo = inject(Seo);
  readonly auth = inject(Auth);

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        const ocultar = this.PREFIXOS_SEM_BARRA_DESLIZANTE.some((prefixo) =>
          event.urlAfterRedirects.startsWith(prefixo)
        );
        this.mostrarBarraDeslizante.set(!ocultar);
      });
  }
}
