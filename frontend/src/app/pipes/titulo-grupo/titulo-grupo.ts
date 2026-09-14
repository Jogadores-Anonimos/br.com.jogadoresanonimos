import { Pipe, PipeTransform } from '@angular/core';

const CONECTIVOS_MINUSCULOS = new Set(['de', 'da', 'do', 'das', 'dos', 'e', 'em', 'a', 'o', 'no', 'na']);

const SIGLAS_CONHECIDAS = new Set([
  'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG',
  'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
  'BH', 'SLZ', 'CG', 'SPH',
]);

function ehNucleoSiglaValido(nucleo: string): boolean {
  return nucleo.length >= 2 && nucleo.length <= 4 && /^[A-ZÀ-Ú]+$/.test(nucleo);
}

function capitalizarPalavra(palavra: string, indice: number, stringOriginalTodaMaiuscula: boolean): string {
  if (!palavra) return palavra;
  const match = palavra.match(/^(\W*)([A-Za-zÀ-ÿ]+)(\W*)$/);
  if (match) {
    const [, prefixo, nucleo, sufixo] = match;
    const nucleoMaiusculo = nucleo.toUpperCase();
    if (SIGLAS_CONHECIDAS.has(nucleoMaiusculo)) {
      return prefixo + nucleoMaiusculo + sufixo;
    }
    const ehSiglaCandidata = ehNucleoSiglaValido(nucleoMaiusculo) && nucleo === nucleoMaiusculo;
    if (ehSiglaCandidata && !stringOriginalTodaMaiuscula) {
      return prefixo + nucleoMaiusculo + sufixo;
    }
    const nucleoMinusculo = nucleo.toLowerCase();
    if (indice > 0 && CONECTIVOS_MINUSCULOS.has(nucleoMinusculo) && !prefixo && !sufixo) {
      return nucleoMinusculo;
    }
    return prefixo + nucleoMinusculo.charAt(0).toUpperCase() + nucleoMinusculo.slice(1) + sufixo;
  }
  return palavra;
}

@Pipe({
  name: 'tituloGrupo',
  standalone: true,
})
export class TituloGrupoPipe implements PipeTransform {
  transform(valor: string | null | undefined): string {
    if (!valor) return '';
    const normalizado = valor.trim().replace(/\s+/g, ' ');
    if (!normalizado) return normalizado;
    const letras = normalizado.match(/[A-Za-zÀ-ÿ]/g) ?? [];
    const stringOriginalTodaMaiuscula = letras.length > 0 && normalizado === normalizado.toUpperCase();
    return normalizado
      .split(' ')
      .map((palavra, indice) => capitalizarPalavra(palavra, indice, stringOriginalTodaMaiuscula))
      .join(' ');
  }
}
