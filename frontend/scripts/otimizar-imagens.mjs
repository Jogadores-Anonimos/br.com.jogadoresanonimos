import sharp from 'sharp';
import { existsSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const imagensDir = join(__dirname, '..', 'src', 'public', 'images', 'home');

const imagens = [
  { entrada: 'hero-reuniao.jpg', saida: 'hero-reuniao.webp' },
  { entrada: 'reuniao-discussao.jpg', saida: 'reuniao-discussao.webp' },
];

async function otimizar({ entrada, saida }) {
  const caminhoEntrada = join(imagensDir, entrada);
  const caminhoSaida = join(imagensDir, saida);
  if (!existsSync(caminhoEntrada)) {
    console.warn(`Arquivo nao encontrado, pulando: ${caminhoEntrada}`);
    return;
  }
  await sharp(caminhoEntrada)
    .resize({ width: 1920, withoutEnlargement: true })
    .webp({ quality: 78 })
    .toFile(caminhoSaida);
  console.log(`Gerado: ${saida}`);
}

for (const imagem of imagens) {
  await otimizar(imagem);
}
