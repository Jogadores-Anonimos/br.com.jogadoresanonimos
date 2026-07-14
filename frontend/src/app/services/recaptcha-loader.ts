import { Injectable } from '@angular/core';

const RECAPTCHA_SCRIPT_SRC = 'https://www.google.com/recaptcha/api.js';

@Injectable({ providedIn: 'root' })
export class RecaptchaLoader {
  private carregamento: Promise<void> | null = null;

  carregar(): Promise<void> {
    if (this.carregamento) {
      return this.carregamento;
    }

    const scriptExistente = document.querySelector<HTMLScriptElement>(
      `script[src="${RECAPTCHA_SCRIPT_SRC}"]`,
    );
    if (scriptExistente) {
      this.carregamento = Promise.resolve();
      return this.carregamento;
    }

    this.carregamento = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = RECAPTCHA_SCRIPT_SRC;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => {
        this.carregamento = null;
        reject(new Error('Falha ao carregar o reCAPTCHA.'));
      };
      document.head.appendChild(script);
    });

    return this.carregamento;
  }
}
