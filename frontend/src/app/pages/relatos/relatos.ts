import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-relatos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './relatos.html',
  styleUrl: './relatos.css'
})
export class Relatos implements OnInit {
  //private API = 'https://comanonimosjogadoresads3-production.up.railway.app';

  relatos: any[] = [];
  carregando = true;
  erro = false;

  modalAberto = false;
  toast = '';
  private toastTimer: any;

  nome = '';
  idade = '';
  descricao = '';

  ngOnInit() {
    // this.carregarRelatos();
    this.carregando = false;
    this.relatos = [];
  }

  abrirModal() {
    this.modalAberto = true;
  }

  fecharModal() {
    this.modalAberto = false;
    this.nome = '';
    this.idade = '';
    this.descricao = '';
  }

  async enviarRelato() {
    if (!this.nome || !this.idade || !this.descricao) {
      this.mostrarToast('Preencha todos os campos.');
      return;
    }

    try {
      this.mostrarToast('Enviando relato...');
      // const res = await fetch(`${this.API}/feedbacks`, {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify({ nome: this.nome, idade: Number(this.idade), descricao: this.descricao })
      // });
      // if (!res.ok) throw new Error();
      this.mostrarToast('Relato enviado para avaliação!');
      this.fecharModal();
    } catch {
      this.mostrarToast('Erro ao enviar relato.');
    }
  }

  mostrarToast(msg: string) {
    this.toast = msg;
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => this.toast = '', 2800);
  }
}
