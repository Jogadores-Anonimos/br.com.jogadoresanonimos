import { Component, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { BarraDeslizante } from './components/barra-deslizante/barra-deslizante';
import { WhatsappFlutuante } from './components/whatsapp-flutuante/whatsapp-flutuante';
import { VoltarAoTopo } from './components/voltar-ao-topo/voltar-ao-topo';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar, Footer, BarraDeslizante, WhatsappFlutuante, VoltarAoTopo],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  mostrarBarraDeslizante = signal(true);

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.mostrarBarraDeslizante.set(!event.urlAfterRedirects.startsWith('/minha-conta'));
      });
  }
}
