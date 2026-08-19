import { copyFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const arquivosParaCopiar = ['maplibre-gl-worker.mjs', 'maplibre-gl-shared.mjs'];
const raizFrontend = path.resolve(fileURLToPath(new URL('.', import.meta.url)), '..');
const origemDir = path.join(raizFrontend, 'node_modules', 'maplibre-gl', 'dist');
const destinoDir = path.join(raizFrontend, 'src', 'public');

async function sincronizar() {
  for (const arquivo of arquivosParaCopiar) {
    const origem = path.join(origemDir, arquivo);
    const destino = path.join(destinoDir, arquivo);
    await copyFile(origem, destino);
    console.log(`[sync-maplibre-worker] copiado: ${arquivo}`);
  }
}

sincronizar().catch((erro) => {
  console.error('[sync-maplibre-worker] falha ao sincronizar assets do MapLibre GL:', erro);
  process.exit(1);
});
