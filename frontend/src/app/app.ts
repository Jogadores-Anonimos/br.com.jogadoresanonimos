import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import {Chatbot} from './components/chatbot/chatbot';
import {BarraDeslizante} from './components/barra-deslizante/barra-deslizante';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, Chatbot, Footer, BarraDeslizante],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
      }
    });
  }
}
