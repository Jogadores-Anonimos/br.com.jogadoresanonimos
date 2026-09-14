export interface BoundingBoxUf {
  uf: string;
  latMin: number;
  latMax: number;
  lonMin: number;
  lonMax: number;
}

export const BOUNDING_BOXES_UF: ReadonlyArray<BoundingBoxUf> = [
  { uf: 'AC', latMin: -11.14, latMax: -7.11, lonMin: -73.99, lonMax: -66.62 },
  { uf: 'AL', latMin: -10.5, latMax: -8.81, lonMin: -38.24, lonMax: -35.15 },
  { uf: 'AP', latMin: -1.24, latMax: 4.44, lonMin: -54.88, lonMax: -49.87 },
  { uf: 'AM', latMin: -9.82, latMax: 2.25, lonMin: -73.8, lonMax: -56.09 },
  { uf: 'BA', latMin: -18.35, latMax: -8.53, lonMin: -46.62, lonMax: -37.34 },
  { uf: 'CE', latMin: -7.87, latMax: -2.78, lonMin: -41.42, lonMax: -37.25 },
  { uf: 'DF', latMin: -16.05, latMax: -15.5, lonMin: -48.29, lonMax: -47.31 },
  { uf: 'ES', latMin: -21.31, latMax: -17.89, lonMin: -41.88, lonMax: -39.65 },
  { uf: 'GO', latMin: -19.49, latMax: -12.39, lonMin: -53.25, lonMax: -45.9 },
  { uf: 'MA', latMin: -10.26, latMax: -1.04, lonMin: -48.76, lonMax: -41.79 },
  { uf: 'MT', latMin: -18.04, latMax: -7.35, lonMin: -61.63, lonMax: -50.22 },
  { uf: 'MS', latMin: -24.07, latMax: -17.16, lonMin: -58.17, lonMax: -50.92 },
  { uf: 'MG', latMin: -22.92, latMax: -14.23, lonMin: -51.05, lonMax: -39.86 },
  { uf: 'PA', latMin: -9.84, latMax: 2.59, lonMin: -58.9, lonMax: -46.06 },
  { uf: 'PB', latMin: -8.3, latMax: -6.02, lonMin: -38.76, lonMax: -34.79 },
  { uf: 'PR', latMin: -26.72, latMax: -22.52, lonMin: -54.62, lonMax: -48.02 },
  { uf: 'PE', latMin: -9.48, latMax: -7.31, lonMin: -41.36, lonMax: -32.39 },
  { uf: 'PI', latMin: -10.93, latMax: -2.74, lonMin: -45.99, lonMax: -40.36 },
  { uf: 'RJ', latMin: -23.37, latMax: -20.76, lonMin: -44.89, lonMax: -40.96 },
  { uf: 'RN', latMin: -6.98, latMax: -4.83, lonMin: -38.58, lonMax: -34.97 },
  { uf: 'RS', latMin: -33.75, latMax: -27.08, lonMin: -57.65, lonMax: -49.69 },
  { uf: 'RO', latMin: -13.69, latMax: -7.97, lonMin: -66.81, lonMax: -59.77 },
  { uf: 'RR', latMin: -1.58, latMax: 5.27, lonMin: -64.82, lonMax: -58.85 },
  { uf: 'SC', latMin: -29.35, latMax: -25.96, lonMin: -53.84, lonMax: -48.32 },
  { uf: 'SP', latMin: -25.31, latMax: -19.78, lonMin: -53.11, lonMax: -44.16 },
  { uf: 'SE', latMin: -11.57, latMax: -9.5, lonMin: -38.24, lonMax: -36.39 },
  { uf: 'TO', latMin: -13.46, latMax: -5.17, lonMin: -50.74, lonMax: -45.7 },
];

export function resolverUfPorCoordenada(latitude: number, longitude: number): string | null {
  const candidatas = BOUNDING_BOXES_UF.filter(
    (caixa) =>
      latitude >= caixa.latMin &&
      latitude <= caixa.latMax &&
      longitude >= caixa.lonMin &&
      longitude <= caixa.lonMax,
  );
  if (candidatas.length === 0) {
    return null;
  }
  if (candidatas.length === 1) {
    return candidatas[0].uf;
  }
  const centro = candidatas.reduce((maisProxima, atual) => {
    const centroAtual = {
      lat: (atual.latMin + atual.latMax) / 2,
      lon: (atual.lonMin + atual.lonMax) / 2,
    };
    const centroMaisProxima = {
      lat: (maisProxima.latMin + maisProxima.latMax) / 2,
      lon: (maisProxima.lonMin + maisProxima.lonMax) / 2,
    };
    const distanciaAtual = Math.hypot(latitude - centroAtual.lat, longitude - centroAtual.lon);
    const distanciaMaisProxima = Math.hypot(latitude - centroMaisProxima.lat, longitude - centroMaisProxima.lon);
    return distanciaAtual < distanciaMaisProxima ? atual : maisProxima;
  });
  return centro.uf;
}
