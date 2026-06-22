import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
  encapsulation: ViewEncapsulation.None
})
export class Reunioes implements AfterViewInit {

  tabAtiva = 'presenciais';
  filtroAtivo = 'todas';
  reuniaoSelecionada: any = null;

  map!: L.Map;
  markers = new Map<number, L.Marker>();

  ngAfterViewInit() {

    setTimeout(() => {

      this.map = L.map('map').setView([-15.7801, -47.9292], 5);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(this.map);

      this.adicionarMarcadores();

      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (pos) => {
            this.map.setView(
              [pos.coords.latitude, pos.coords.longitude],
              12
            );
          },
          () => {
            this.map.setView([-15.7801, -47.9292], 5);
          }
        );
      }

    }, 100);
  }

  adicionarMarcadores() {

    this.reunioesMock.forEach(reuniao => {

      const marker = L.marker([
        reuniao.lat,
        reuniao.lng
      ])
        .addTo(this.map)
        .bindPopup(`
        <strong>${reuniao.nome}</strong><br>
        ${reuniao.horario}<br>
        ${reuniao.endereco}
      `);

      this.markers.set(reuniao.id, marker);

    });

  }

  abrirModal(reuniao: any) {

    this.reuniaoSelecionada = reuniao;

    const marker = this.markers.get(reuniao.id);

    if (marker) {

      marker.openPopup();

      this.map.flyTo(
        marker.getLatLng(),
        15,
        {
          duration: 1.5
        }
      );
    }
  }

  fecharModal() {
    this.reuniaoSelecionada = null;
  }

  reunioesMock = [
    {
      id: 1,
      nome: 'Grupo Esperança',
      dia: '23',
      mes: 'MAR',
      semana: 'SEG',
      horario: '19:30 às 21:00',
      tipo: 'Presencial • Centro',
      cidade: 'Campinas/SP',
      endereco: 'Rua Barão de Jaguara, 450',
      descricao: 'Grupo aberto para novos membros.',
      lat: -22.9056,
      lng: -47.0608
    },
    {
      id: 2,
      nome: 'Grupo Renovação',
      dia: '24',
      mes: 'MAR',
      semana: 'TER',
      horario: '20:00 às 21:30',
      tipo: 'Presencial • Taquaral',
      cidade: 'Campinas/SP',
      endereco: 'Av. Heitor Penteado, 1200',
      descricao: 'Reunião semanal de apoio e recuperação.',
      lat: -22.8865,
      lng: -47.0490
    },
    {
      id: 3,
      nome: 'Grupo Novo Caminho',
      dia: '25',
      mes: 'MAR',
      semana: 'QUA',
      horario: '19:00 às 20:30',
      tipo: 'Presencial • Hortolândia',
      cidade: 'Hortolândia/SP',
      endereco: 'Rua Luiz Camilo de Camargo, 300',
      descricao: 'Grupo voltado para recém-chegados.',
      lat: -22.8583,
      lng: -47.2200
    }
  ];
  abrirBrasil(event: Event) {
    event.preventDefault();

    this.tabAtiva = 'presenciais';

    setTimeout(() => {
      document
        .querySelector('.reunioes-map')
        ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  }

  abrirOnline(event: Event) {
    event.preventDefault();

    this.tabAtiva = 'online';

    setTimeout(() => {
      document
        .querySelector('.online-content')
        ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  }
}
