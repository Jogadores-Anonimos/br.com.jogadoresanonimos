import { Component, OnInit, OnDestroy, AfterViewInit, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const DASH_LABELS_CURTOS = [
  'Jogou mais tempo que planejado','Jogo como alívio emocional',
  'Pensa em jogar em outros momentos','"Só mais uma rodada" recorrente',
  'Precisou apostar mais para mesma emoção','Voltou para recuperar o que perdeu',
  'Tentou parar e não conseguiu','Irritação/ansiedade ao não poder jogar',
  'Problemas financeiros por causa do jogo','Familiar/amigo preocupado com o jogo',
  'Jogo ocupou espaço de outras atividades','Escondeu tempo/dinheiro gasto',
  'Jogo gerou conflitos em relacionamentos','Deixou de cumprir compromissos por jogar',
  'Pediu dinheiro emprestado para cobrir perdas',
];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  encapsulation: ViewEncapsulation.None
})
export class Dashboard implements OnInit, OnDestroy, AfterViewInit {
  constructor(private cdr: ChangeDetectorRef) {}
  temDadosLocais = false;
  dadosLocais: string[] = [];
  tabelaLocal: any[] = [];
  metricas: any = {};
  risco: any = {};
  private charts: any = {};

  ngOnInit() {
    const saved = localStorage.getItem('autoaval_resultado');
    const dados = saved ? (JSON.parse(saved).respostas || []) : [];
    this.dadosLocais = dados;
    this.temDadosLocais = dados.length > 0;
  }

  ngAfterViewInit() {
    if (this.temDadosLocais) {
      setTimeout(() => this.renderLocal(), 500);
    }
  }

  ngOnDestroy() {
    Object.values(this.charts).forEach((c: any) => c.destroy());
  }

  renderLocal() {
    const dados = this.dadosLocais;
    const n = dados.length;
    const totalSim = dados.filter(r => r === 'sim').length;
    const simArr = dados.map(r => r === 'sim' ? 1 : 0);
    const cor = totalSim >= 9 ? '#742C24' : totalSim >= 4 ? '#D4A843' : '#16a34a';
    const label = totalSim >= 9 ? 'Alto risco' : totalSim >= 4 ? 'Risco moderado' : 'Baixo risco';
    const pct = Math.round(totalSim / n * 100);

    this.metricas = {
      total: n, sim: totalSim,
      simPct: pct + '%',
      alerta: totalSim >= 9, atencao: totalSim >= 4 && totalSim < 9, ok: totalSim < 4
    };

    this.risco = { label, pct, cor, texto: totalSim + ' de ' + n + ' sinais' };

    this.tabelaLocal = dados.map((r, i) => ({
      idx: i + 1, pergunta: DASH_LABELS_CURTOS[i] || '—',
      resposta: r === 'sim' ? 'Sim' : 'Não', isSim: r === 'sim'
    }));

    // Força o Angular a atualizar o DOM antes de criar os gráficos
    this.cdr.detectChanges();

    // Agora os canvas existem, cria os gráficos
    this.mkChart('dash-c-simnao', {
      type: 'doughnut',
      data: {
        labels: ['Sim', 'Não'],
        datasets: [{ data: [totalSim, n - totalSim], backgroundColor: ['#742C24', '#D4A843'], borderWidth: 0 }]
      },
      options: { responsive: true, maintainAspectRatio: false, cutout: '62%', plugins: { legend: { display: false } } }
    });

    this.mkChart('dash-c-porpergunta', {
      type: 'bar',
      data: {
        labels: simArr.map((_, i) => `P${i + 1}`),
        datasets: [
          { label: 'Sim', data: simArr, backgroundColor: '#742C24' },
          { label: 'Não', data: simArr.map(s => 1 - s), backgroundColor: '#D4A843' }
        ]
      },
      options: {
        responsive: true, maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: { x: { ticks: { font: { size: 10 } } }, y: { beginAtZero: true, max: 1 } }
      }
    });
  }
  mkChart(id: string, cfg: any) {
    if (this.charts[id]) this.charts[id].destroy();
    const ctx = document.getElementById(id) as HTMLCanvasElement;
    if (!ctx) return;
    this.charts[id] = new Chart(ctx, cfg);
  }
}
