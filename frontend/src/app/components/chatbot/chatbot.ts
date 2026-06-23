import { Component, OnInit } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Message {
  text: string;
  type: 'user' | 'bot';
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule, NgOptimizedImage],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.css'
})
export class Chatbot implements OnInit {

  constructor(private router: Router) {}

  isOpen = false;

  messages: Message[] = [];

  inputText = '';

  awaitingName = false;

  routes = [
    {
      page: 'quem-somos',
      keywords: ['quem somos', 'sobre site', 'o que e esse site', 'site'],
      reply: 'Somos uma plataforma de apoio e informação sobre jogos e recuperação.'
    },
    {
      page: 'reunioes',
      keywords: ['reunioes', 'reuniao', 'grupo', 'ajuda', 'encontro'],
      reply: 'Aqui você encontra reuniões e grupos de apoio próximos.'
    },
    {
      page: 'autoaval',
      keywords: ['autoavaliacao', 'teste', 'avaliacao', 'auto avaliacao'],
      reply: 'Faça sua autoavaliação para entender sua relação com jogos.'
    },
    {
      page: 'doze-passos',
      keywords: ['12 passos', 'doze passos', 'passos', 'recuperacao'],
      reply: 'O programa dos 12 passos ajuda na recuperação.'
    },
    {
      page: 'relatos',
      keywords: ['relato', 'historias', 'experiencia', 'depoimento'],
      reply: 'Leia relatos reais de pessoas que passaram por isso.'
    },
    {
      page: 'contato',
      keywords: ['contato', 'falar com alguem', 'suporte', 'ajuda', 'whatsapp'],
      reply: 'Você pode falar com nossa equipe na página de contato.'
    },

    {
      page: 'podcast',
      keywords: ['podcast', 'audio', 'ouvir', 'historia audio', 'episodio'],
      reply: 'Ouça histórias e experiências compartilhadas em áudio.'
    },
    {
      page: 'anonimato',
      keywords: ['anonimato', 'anonimo', 'privacidade', 'sigilo'],
      reply: 'O anonimato é um princípio fundamental da nossa irmandade.'
    },
    {
      page: 'jogo-compulsivo',
      keywords: ['jogo compulsivo', 'compulsao', 'vicio jogo', 'dependencia jogo'],
      reply: 'Entenda como funciona a compulsão por jogos e seus impactos.'
    },
    {
      page: 'uma-nova-chance',
      keywords: ['nova chance', 'recomeco', 'esperanca', 'recomecar'],
      reply: 'A recuperação é possível. Sempre existe uma nova chance.'
    },
    {
      page: 'perguntas-frequentes',
      keywords: ['faq', 'duvidas', 'perguntas frequentes', 'ajuda perguntas'],
      reply: 'Veja respostas para as dúvidas mais comuns sobre o programa.'
    },
    {
      page: 'literatura',
      keywords: ['literatura', 'livros', 'material', 'folhetos'],
      reply: 'Acesse materiais e literatura de apoio à recuperação.'
    },
    {
      page: 'dashboard',
      keywords: ['painel', 'dashboard', 'resultados', 'minha conta'],
      reply: 'Aqui está seu painel de controle.'
    }
  ];

  ngOnInit(): void {
    this.initChat();
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  getName(): string | null {
    return sessionStorage.getItem('chat_name');
  }

  setName(name: string) {
    sessionStorage.setItem('chat_name', name);
  }

  addMessage(type: 'user' | 'bot', text: string) {
    this.messages.push({
      type,
      text
    });
  }

  sendMessage() {
    const text = this.inputText.trim();
    if (!text) return;

    this.addMessage('user', text);
    this.inputText = '';

    const name = this.getName();

    if (!name) {
      if (!this.awaitingName) {
        this.addMessage('bot', 'Antes de continuar, qual é o seu nome?');
        this.awaitingName = true;
        return;
      }

      const cleanName = text
        .replace(/^(oi|olá|ola|eai|opa)[,\s]*/i, '')
        .replace(/meu nome e/i, '')
        .trim();

      if (cleanName.length < 2) {
        this.addMessage('bot', 'Pode me dizer seu nome corretamente?');
        return;
      }

      this.setName(cleanName);
      this.awaitingName = false;
      this.addMessage('bot', `Prazer, ${cleanName}! Como posso te ajudar?`);
      return;
    }

    const route = this.findRoute(text);

    if (route) {
      this.addMessage('bot', `${name}, ${route.reply}`);
      setTimeout(() => {
        this.router.navigate([route.page]);
        this.isOpen = false;
      }, 1500);
      return;
    }

    this.addMessage('bot', `${name}, não entendi. Tente algo como reuniões, autoavaliação ou vídeos.`);
  }

  findRoute(text: string) {

    text = text
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .trim();

    return this.routes.find(route =>
      route.keywords.some(keyword =>
        text.includes(keyword)
      )
    );
  }

  initChat() {

    const name = this.getName();

    if (name) {
      this.addMessage(
        'bot',
        `Olá ${name}, em que posso te ajudar?`
      );
    } else {
      this.addMessage(
        'bot',
        '👋 Olá! Antes de começar, qual é o seu nome?'
      );
      this.awaitingName = true;
    }
  }
}
