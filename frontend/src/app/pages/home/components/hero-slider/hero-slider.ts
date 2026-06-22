import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface Slide {
  titulo: string;
  descricao: string;
  imagem: string;
  rota: string;
  botao: string;
  corBotao?: 'red' | 'yellow';
}

@Component({
  selector: 'app-hero-slider',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './hero-slider.html',
  styleUrl: './hero-slider.css',
  encapsulation: ViewEncapsulation.None
})
export class HeroSlider implements OnInit, OnDestroy {

  currentSlide = 0;
  private sliderInterval: any;

  slides: Slide[] = [

    {
      titulo: 'O Desejo de Parar é o Início',
      descricao: 'Somos uma uma irmandade cujos membros compartilham suas experiências...',
      imagem: 'images/main-page/quem-somos.jpg',
      rota: '/quem-somos',
      botao: 'Quem somos?',
      corBotao: 'red'
    },

    {
      titulo: 'Avisos Importantes',
      descricao: 'Fique por dentro das novidades, alterações de reuniões, eventos e informações importantes da irmandade.',
      imagem: 'images/main-page/avisos.jpg',
      rota: '/avisos',
      botao: 'Veja todos avisos',
      corBotao: 'yellow'
    },

    {
      titulo: 'Reuniões',
      descricao: 'Localizar reuniões de ajuda próximas a você...',
      imagem: 'images/main-page/reunioes.jpg',
      rota: '/reunioes',
      botao: 'Localizar Reuniões',
      corBotao: 'red'
    },

    {
      titulo: 'Endereços',
      descricao: 'Consulte os endereços dos grupos e locais de reunião.',
      imagem: 'images/main-page/enderecos.jpg',
      rota: '/enderecos',
      botao: 'Ver endereços',
      corBotao: 'yellow'
    },

    {
      titulo: 'Os 12 Passos',
      descricao: 'Um caminho estruturado de recuperação baseado em princípios de mudança.',
      imagem: 'images/main-page/doze-passos.jpg',
      rota: '/doze-passos',
      botao: 'Ver os 12 Passos',
      corBotao: 'red'
    },

    {
      titulo: 'As 20 Perguntas',
      descricao: 'Quer entender melhor sua relação com os jogos?',
      imagem: 'images/main-page/20-perguntas.jpg',
      rota: '/autoaval',
      botao: 'Quero entender',
      corBotao: 'yellow'
    },

    {
      titulo: 'Reflexões do Dia',
      descricao: 'Mensagens diárias para fortalecer sua recuperação.',
      imagem: 'images/main-page/reflexoes.jpg',
      rota: '/reflexoes',
      botao: 'Ler reflexão',
      corBotao: 'red'
    },

    {
      titulo: 'Literatura',
      descricao: 'Materiais e leituras para apoiar sua jornada.',
      imagem: 'images/main-page/literatura.jpg',
      rota: '/literatura',
      botao: 'Ver literatura',
      corBotao: 'yellow'
    },

    {
      titulo: 'Relatos Reais',
      descricao: 'Histórias de pessoas que enfrentaram o jogo compulsivo.',
      imagem: 'images/main-page/relatos.png',
      rota: '/relatos',
      botao: 'Ver relatos',
      corBotao: 'red'
    },

    {
      titulo: '7º Passo da Unidade',
      descricao: 'Fortaleça nossa irmandade, passe a mensagem adiante.',
      imagem: 'images/main-page/unidade.jpg',
      rota: '/setimo-passo',
      botao: 'Saiba mais',
      corBotao: 'yellow'
    },

    {
      titulo: 'Contato',
      descricao: 'Entre em contato com representantes da irmandade.',
      imagem: 'images/main-page/contatos.jpg',
      rota: '/contato',
      botao: 'Ver contatos',
      corBotao: 'red'
    },

    {
      titulo: 'Podcast',
      descricao: 'Ouça experiências e histórias de recuperação.',
      imagem: 'images/main-page/podcast.jpg',
      rota: '/podcast',
      botao: 'Ouvir podcast',
      corBotao: 'yellow'
    },

    {
      titulo: 'Painel',
      descricao: 'Visualização de respostas da autoavaliação.',
      imagem: 'images/main-page/painel.jpg',
      rota: '/dashboard',
      botao: 'Acessar painel',
      corBotao: 'red'
    },

    {
      titulo: 'Anonimato',
      descricao: 'Entenda um dos pilares fundamentais da irmandade.',
      imagem: 'images/main-page/anonimato.jpg',
      rota: '/anonimato',
      botao: 'Saiba mais',
      corBotao: 'yellow'
    },

    {
      titulo: 'Jogo Compulsivo',
      descricao: 'Informações e orientações sobre a compulsão pelo jogo.',
      imagem: 'images/main-page/jogo-compulsivo.jpg',
      rota: '/jogo-compulsivo',
      botao: 'Entender',
      corBotao: 'red'
    },

    {
      titulo: 'Uma Nova Chance',
      descricao: 'A recuperação começa com um único passo.',
      imagem: 'images/main-page/nova-chance.jpg',
      rota: '/uma-nova-chance',
      botao: 'Começar',
      corBotao: 'yellow'
    },

    {
      titulo: 'Perguntas Frequentes',
      descricao: 'Respostas para as dúvidas mais comuns.',
      imagem: 'images/main-page/faq.jpg',
      rota: '/faq',
      botao: 'Ver perguntas',
      corBotao: 'red'
    }
  ];

  get totalSlides() {
    return this.slides.length;
  }

  ngOnInit() {
    this.startSlider();
  }

  ngOnDestroy() {
    this.stopSlider();
  }

  goSlide(index: number) {
    this.currentSlide = index;
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.totalSlides;
  }

  prevSlide() {
    this.currentSlide =
      (this.currentSlide - 1 + this.totalSlides) % this.totalSlides;
  }

  startSlider() {
    this.stopSlider();

    this.sliderInterval = setInterval(() => {
      this.nextSlide();
    }, 10000);
  }

  stopSlider() {
    if (this.sliderInterval) {
      clearInterval(this.sliderInterval);
    }
  }
}
