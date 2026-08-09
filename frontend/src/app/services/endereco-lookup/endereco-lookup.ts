import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { EnderecoViaCep, ResultadoNominatim } from './endereco-lookup.model';

@Injectable({ providedIn: 'root' })
export class EnderecoLookupService {
  private readonly http = inject(HttpClient);

  buscarCep(cepLimpo: string): Observable<EnderecoViaCep | null> {
    return this.http
      .get<EnderecoViaCep>(`https://viacep.com.br/ws/${cepLimpo}/json/`)
      .pipe(catchError(() => of(null)));
  }

  geocodificar(endereco: string): Observable<ResultadoNominatim[]> {
    return this.http
      .get<ResultadoNominatim[]>('https://nominatim.openstreetmap.org/search', {
        params: { format: 'json', q: endereco, limit: '1' },
      })
      .pipe(catchError(() => of([])));
  }
}
