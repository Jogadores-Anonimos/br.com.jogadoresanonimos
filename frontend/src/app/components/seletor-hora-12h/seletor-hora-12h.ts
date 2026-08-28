import { CommonModule } from '@angular/common';
import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

type PeriodoDia = 'AM' | 'PM';

function paraDoisDigitos(valor: number): string {
  return String(valor).padStart(2, '0');
}

function horario24Valido(valor: string): boolean {
  return /^([01]\d|2[0-3]):[0-5]\d$/.test(valor);
}

@Component({
  selector: 'app-seletor-hora-12h',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './seletor-hora-12h.html',
  styleUrl: './seletor-hora-12h.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SeletorHora12h),
      multi: true,
    },
  ],
})
export class SeletorHora12h implements ControlValueAccessor {
  @Input() idBase = 'seletor-hora';
  @Input() rotuloAcessivel = 'Horário';

  readonly horas = Array.from({ length: 12 }, (_, indice) => indice + 1);
  readonly minutos = Array.from({ length: 60 }, (_, indice) => indice);

  hora = 7;
  minuto = 0;
  periodo: PeriodoDia = 'PM';

  private valorEmitido = '';
  private disabledInterno = false;
  private onChange: (valor: string) => void = () => {};
  private onTouched: () => void = () => {};

  get desabilitado(): boolean {
    return this.disabledInterno;
  }

  writeValue(valor: string | null): void {
    if (!valor || !horario24Valido(valor)) {
      this.valorEmitido = '';
      return;
    }
    this.valorEmitido = valor;
    const [horaTexto, minutoTexto] = valor.split(':');
    const hora24 = Number(horaTexto);
    this.minuto = Number(minutoTexto);
    this.periodo = hora24 >= 12 ? 'PM' : 'AM';
    const hora12 = hora24 % 12;
    this.hora = hora12 === 0 ? 12 : hora12;
  }

  registerOnChange(fn: (valor: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabledInterno = isDisabled;
  }

  atualizar(): void {
    let hora24 = this.hora % 12;
    if (this.periodo === 'PM') {
      hora24 += 12;
    }
    const novoValor = `${paraDoisDigitos(hora24)}:${paraDoisDigitos(this.minuto)}`;
    this.valorEmitido = novoValor;
    this.onChange(novoValor);
    this.onTouched();
  }

  selecionarPeriodo(novoPeriodo: PeriodoDia): void {
    if (this.desabilitado || this.periodo === novoPeriodo) {
      return;
    }
    this.periodo = novoPeriodo;
    this.atualizar();
  }
}
