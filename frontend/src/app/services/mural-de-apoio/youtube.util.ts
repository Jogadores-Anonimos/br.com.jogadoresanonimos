const PADROES_YOUTUBE = [
  /(?:youtube\.com\/watch\?v=)([a-zA-Z0-9_-]{11})/,
  /(?:youtube\.com\/embed\/)([a-zA-Z0-9_-]{11})/,
  /(?:youtu\.be\/)([a-zA-Z0-9_-]{11})/,
];

export function extrairYoutubeVideoId(url: string | null | undefined): string | null {
  if (!url) return null;
  for (const padrao of PADROES_YOUTUBE) {
    const match = url.match(padrao);
    if (match) return match[1];
  }
  return null;
}

export function paraYoutubeEmbedUrl(url: string | null | undefined): string | null {
  const videoId = extrairYoutubeVideoId(url);
  return videoId ? `https://www.youtube-nocookie.com/embed/${videoId}?autoplay=1` : null;
}

export function paraYoutubeThumbnailUrl(url: string | null | undefined): string | null {
  const videoId = extrairYoutubeVideoId(url);
  return videoId ? `https://img.youtube.com/vi/${videoId}/hqdefault.jpg` : null;
}
