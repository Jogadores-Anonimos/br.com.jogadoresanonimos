const MAPLIBRE_WORKER_URL = '/maplibre-gl-worker.mjs';

export function configurarMapLibreWorker(maplibregl: typeof import('maplibre-gl')): void {
  maplibregl.setWorkerUrl(MAPLIBRE_WORKER_URL);
}
