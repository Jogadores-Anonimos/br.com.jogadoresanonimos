export interface EnderecoViaCep {
  logradouro?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
  erro?: boolean;
}

export interface ResultadoNominatim {
  lat: string;
  lon: string;
}

export interface MunicipioIbge {
  nome: string;
}
