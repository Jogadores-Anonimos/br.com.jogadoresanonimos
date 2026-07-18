import { Component, OnInit, inject, signal } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { FaqService } from '../../services/faq/faq';
import { Faq } from '../../services/faq/faq.model';

@Component({
  selector: 'app-perguntas-e-respostas',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './perguntas-e-respostas.html',
  styleUrl: './perguntas-e-respostas.css',
})
export class PerguntasERespostas implements OnInit {
  private readonly faqService = inject(FaqService);

  readonly faqs = signal<Faq[]>([]);

  ngOnInit(): void {
    this.faqService.listarFaq().subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.faqs.set(resposta.data);
      }
    });
  }
}
