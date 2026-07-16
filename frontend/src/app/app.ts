import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { WhatsappFlutuante } from './components/whatsapp-flutuante/whatsapp-flutuante';
import { VoltarAoTopo } from './components/voltar-ao-topo/voltar-ao-topo';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar, Footer, WhatsappFlutuante, VoltarAoTopo],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {}
