import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription, fromEvent } from 'rxjs';
import { auditTime } from 'rxjs/operators';

const SCROLL_THRESHOLD_PX = 300;

const SCROLL_AUDIT_MS = 100;

@Component({
  selector: 'app-voltar-ao-topo',
  standalone: true,
  imports: [],
  templateUrl: './voltar-ao-topo.html',
  styleUrl: './voltar-ao-topo.css'
})
export class VoltarAoTopo implements OnInit, OnDestroy {
  visivel = false;

  private scrollSub?: Subscription;

  ngOnInit(): void {
    this.scrollSub = fromEvent(window, 'scroll', { passive: true })
      .pipe(auditTime(SCROLL_AUDIT_MS))
      .subscribe(() => {
        this.visivel = window.scrollY > SCROLL_THRESHOLD_PX;
      });
  }

  ngOnDestroy(): void {
    this.scrollSub?.unsubscribe();
  }

  voltarAoTopo(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
