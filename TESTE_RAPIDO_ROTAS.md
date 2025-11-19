
### Visual
- [ ] Polyline verde vibrante
- [ ] Marcadores identificáveis
- [ ] Header fixo no topo
- [ ] Card inferior rolável
- [ ] Botões bem espaçados
- [ ] Textos legíveis
- [ ] Ícones alinhados

### Performance
- [ ] Rota carrega em < 3s
- [ ] Sem travamentos
- [ ] Animações suaves
- [ ] WebSocket estável
- [ ] Memória controlada

---

## 🐛 Problemas Comuns e Soluções

### ❌ Rota não aparece

**Problema:** Linha verde não desenha

**Soluções:**
1. Verifique API Key do Google:
   ```kotlin
   // DirectionsService.kt
   private const val API_KEY = "SUA_CHAVE_AQUI"
   ```

2. Habilite Directions API no Google Cloud Console

3. Verifique logs:
   ```bash
   adb logcat | grep DirectionsService
   ```

**Logs esperados:**
```
🗺️ Buscando rota: -27.55,-48.63 -> 1 paradas -> -23.53,-46.64
✅ Rota encontrada: 487 pontos, 15.2 km, 23 min
```

---

### ❌ Marcadores não aparecem

**Problema:** Não vejo os pins no mapa

**Soluções:**
1. Verifique se API retorna `paradas`:
   ```bash
   adb logcat | grep "🛣️ Serviço com"
   ```

2. Certifique-se que `lat` e `lng` são válidos (não null)

3. Verifique o tipo das paradas:
   ```kotlin
   // Deve ser exatamente:
   "origem", "parada", "destino"
   ```

---

### ❌ WebSocket desconecta

**Problema:** Fica mostrando "🔴 Offline"

**Soluções:**
1. Verifique URL do WebSocket:
   ```kotlin
   // WebSocketManager.kt
   private const val SERVER_URL = 
       "https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net"
   ```

2. Backend deve ter WebSocket habilitado

3. Verifique logs:
   ```bash
   adb logcat | grep WebSocketManager
   ```

---

### ❌ Câmera não ajusta

**Problema:** Mapa não mostra toda a rota

**Soluções:**
1. Aguarde a rota carregar completamente

2. Verifique bounds:
   ```kotlin
   // TelaRastreamentoServico.kt
   // Linha ~215
   val boundsBuilder = LatLngBounds.Builder()
   routePoints.forEach { boundsBuilder.include(it) }
   ```

3. Aumente o padding:
   ```kotlin
   CameraUpdateFactory.newLatLngBounds(bounds, 200) // Era 150
   ```

---

## 📊 Métricas de Sucesso

### ✅ Teste Passou Se:

1. **Rota Visível**
   - Linha verde conectando pontos
   - Sem quebras ou falhas

2. **Marcadores Corretos**
   - Cores: Azul → Laranja → Vermelho
   - Quantidade: Igual às paradas
   - Posições: Corretas no mapa

3. **Tempo Real**
   - 🟢 Ao vivo pulsando
   - Prestador se movendo
   - Distância atualizando

4. **Interatividade**
   - Zoom/pan funciona
   - Botões respondem
   - Dialogs aparecem

5. **Performance**
   - Carrega rápido (< 5s)
   - Sem lag ao mover
   - FPS estável

---

## 🎬 Fluxo de Teste Completo

### Passo a Passo:

1. **Abrir app como Contratante**
2. **Criar serviço de Transporte**
3. **Adicionar paradas:** Origem → Parada 1 → Destino
4. **Aguardar prestador aceitar**
5. **Entrar na tela de rastreamento**
6. **Verificar:**
   - ✅ Rota verde completa
   - ✅ 3 marcadores (azul, laranja, vermelho)
   - ✅ 🟢 Ao vivo pulsando
   - ✅ Distância e tempo
7. **Testar botão "Ligar"**
8. **Mover o mapa** (zoom in/out)
9. **Aguardar prestador se mover**
10. **Verificar atualização em tempo real**

**Tempo total:** ~5 minutos

---

## 📸 Capturas de Tela

### Tire prints de:
1. Mapa com rota completa
2. Marcadores em cada parada
3. Header com "Ao vivo"
4. Card inferior com info
5. Logs do Logcat

---

## ✅ Resultado Final Esperado

```
╔═══════════════════════════════════════╗
║  TESTE: Sistema de Rotas              ║
║  STATUS: ✅ APROVADO                  ║
╠═══════════════════════════════════════╣
║  ✅ Compilação sem erros              ║
║  ✅ API retorna paradas               ║
║  ✅ Rota desenha corretamente         ║
║  ✅ Marcadores nas cores certas       ║
║  ✅ WebSocket conectado               ║
║  ✅ Tempo real funcionando            ║
║  ✅ UI responsiva                     ║
║  ✅ Performance adequada              ║
╚═══════════════════════════════════════╝
```

---

## 🚀 Próximo Passo

Se todos os testes passaram:
1. ✅ Commit das mudanças
2. ✅ Deploy para homologação
3. ✅ Teste com usuários reais
4. ✅ Monitorar logs em produção

---

**Data:** 2025-11-19  
**Versão:** 1.0  
**Status:** ✅ Pronto para Testar
# 🧪 Teste Rápido - Sistema de Rotas com Paradas

## ⚡ Teste em 5 Minutos

### 1. Verificar Compilação ✅

```bash
# No terminal do Android Studio
./gradlew clean build
```

**Resultado Esperado:**
```
BUILD SUCCESSFUL in 45s
```

---

### 2. Verificar Logs da API 📡

#### Aceitar um serviço como prestador

Depois, como **contratante**, verificar no Logcat:

```bash
adb logcat | grep ServicoViewModel
```

**Logs Esperados:**
```
🚀 Iniciando monitoramento do serviço ID: 188
🔄 Buscando serviço ID: 188
✅ Serviço encontrado com status: EM_ANDAMENTO
🛣️ Serviço com 3 paradas:
  0: origem - Origem
  1: parada - snjazkakkz
  2: destino - Destino
📍 Prestador em: -27.5537851, -48.6307681
```

---

### 3. Verificar Rota no Logcat 🗺️

```bash
adb logcat | grep TelaRastreamento
```

**Logs Esperados:**
```
🗺️ Buscando rota completa com 3 pontos...
✅ Rota atualizada: 487 pontos, 1 paradas, 15.2 km, 23 min
📍 Posição atualizada via WebSocket: -27.553, -48.630
```

---

### 4. Teste Visual no App 📱

#### O que você deve ver:

**No Mapa:**
1. ✅ Linha verde conectando todos os pontos
2. ✅ Marcador AZUL na origem (🔵)
3. ✅ Marcador LARANJA nas paradas (🟠)
4. ✅ Marcador VERMELHO no destino (🔴)
5. ✅ Marcador VERDE no prestador (🟢)

**No Header:**
1. ✅ "🟢 Ao vivo" pulsando
2. ✅ "📍 15.2 km"
3. ✅ "⏱️ 23 min"

**No Card Inferior:**
1. ✅ Nome do prestador
2. ✅ Avaliação (estrelas)
3. ✅ Botões "Ligar" e "Chat"
4. ✅ Info do veículo
5. ✅ Detalhes do serviço

---

## 🎯 Cenários de Teste

### Teste 1: Serviço Simples (2 Pontos)

**Backend retorna:**
```json
{
  "paradas": [
    {"ordem": 0, "tipo": "origem", "lat": -27.55, "lng": -48.63},
    {"ordem": 1, "tipo": "destino", "lat": -23.53, "lng": -46.64}
  ]
}
```

**Resultado:**
- ✅ Linha verde direta origem → destino
- ✅ 2 marcadores (azul e vermelho)

---

### Teste 2: Serviço com 1 Parada (3 Pontos)

**Backend retorna:**
```json
{
  "paradas": [
    {"ordem": 0, "tipo": "origem", "lat": -27.55, "lng": -48.63},
    {"ordem": 1, "tipo": "parada", "lat": -23.54, "lng": -46.84},
    {"ordem": 2, "tipo": "destino", "lat": -23.53, "lng": -46.64}
  ]
}
```

**Resultado:**
- ✅ Linha verde passando pela parada
- ✅ 3 marcadores (azul, laranja, vermelho)
- ✅ Parada aparece como "📍 Parada 1"

---

### Teste 3: Serviço com Múltiplas Paradas (5 Pontos)

**Backend retorna:**
```json
{
  "paradas": [
    {"ordem": 0, "tipo": "origem", "lat": -27.55, "lng": -48.63},
    {"ordem": 1, "tipo": "parada", "lat": -23.54, "lng": -46.84},
    {"ordem": 2, "tipo": "parada", "lat": -23.56, "lng": -46.70},
    {"ordem": 3, "tipo": "parada", "lat": -23.55, "lng": -46.65},
    {"ordem": 4, "tipo": "destino", "lat": -23.53, "lng": -46.64}
  ]
}
```

**Resultado:**
- ✅ Linha verde conectando 5 pontos
- ✅ 5 marcadores (1 azul, 3 laranjas, 1 vermelho)
- ✅ Paradas numeradas: "📍 Parada 1", "📍 Parada 2", "📍 Parada 3"

---

## 🔍 Checklist de Verificação

### Funcionalidades
- [ ] Rota desenha corretamente no mapa
- [ ] Marcadores aparecem nas cores certas
- [ ] Câmera mostra toda a rota
- [ ] WebSocket conecta (🟢 Ao vivo)
- [ ] Prestador se move em tempo real
- [ ] Distância e tempo aparecem
- [ ] Botão "Ligar" funciona
- [ ] Pode cancelar o serviço
- [ ] Navega ao concluir/cancelar

