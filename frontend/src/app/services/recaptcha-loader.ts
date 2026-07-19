import { Injectable } from '@angular/core';

const RECAPTCHA_SCRIPT_BASE_SRC = 'https://www.google.com/recaptcha/api.js';
const RECAPTCHA_IDIOMA = 'pt-BR';
const RECAPTCHA_ONLOAD_CALLBACK_NAME = 'ngOnRecaptchaApiLoad';

declare global {
  interface Window {
    [RECAPTCHA_ONLOAD_CALLBACK_NAME]?: () => void;
  }
}

@Injectable({ providedIn: 'root' })
export class RecaptchaLoader {
  private carregamento: Promise<void> | null = null;

  carregar(): Promise<void> {
    if (this.carregamento) {
      return this.carregamento;
    }

    if (typeof (window as any).grecaptcha !== 'undefined') {
      this.carregamento = Promise.resolve();
      return this.carregamento;
    }

    const scriptExistente = document.querySelector<HTMLScriptElement>(
      `script[src^="${RECAPTCHA_SCRIPT_BASE_SRC}"]`,
    );
    if (scriptExistente) {
      this.carregamento = new Promise((resolve) => {
        window[RECAPTCHA_ONLOAD_CALLBACK_NAME] = resolve;
      });
      return this.carregamento;
    }

    this.carregamento = new Promise((resolve, reject) => {
      window[RECAPTCHA_ONLOAD_CALLBACK_NAME] = resolve;
      const script = document.createElement('script');
      script.src = `${RECAPTCHA_SCRIPT_BASE_SRC}?hl=${RECAPTCHA_IDIOMA}&onload=${RECAPTCHA_ONLOAD_CALLBACK_NAME}&render=explicit`;
      script.async = true;
      script.defer = true;
      script.onerror = () => {
        this.carregamento = null;
        reject(new Error('Falha ao carregar o reCAPTCHA.'));
      };
      document.head.appendChild(script);
    });

    return this.carregamento;
  }
}
