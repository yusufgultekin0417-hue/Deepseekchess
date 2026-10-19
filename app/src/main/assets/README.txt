Bu klasöre iki dosya konacak:

1) stockfish
   - Android ARM64 Stockfish binary
   - https://github.com/official-stockfish/Stockfish/releases
   - "stockfish-android-arm64" indir, adını "stockfish" yap, buraya yükle

2) chess_pieces.tflite
   - Taş sınıflandırma modeli (P N B R Q K p n b r q k)
   - Yoksa FEN üretimi çalışmaz, uygulama "model eksik" der
   - Kaggle'da "chess piece classification tflite" araması ile bulunabilir
