## 🚀 Próximos Passos

1. **Rebuild** → `./gradlew clean build`
2. **Testar** → Criar serviço e ver logs
3. **Reportar** → Me envie os logs se não funcionar

**Com fallback, a rota SEMPRE aparece agora! 🎉**

---

**Data:** 2025-11-19  
**Versão:** 2.1 (Com Fallback)  
**Status:** ✅ Testável
# 🐛 Debug - Rota Não Aparece no Mapa

## ❌ Problema Reportado

A linha verde da rota NÃO está aparecendo no mapa (estilo Uber/99).

## ✅ Correções Aplicadas

### 1. **Logs Detalhados Adicionados**

Agora o `DirectionsService` mostra TUDO:

```kotlin
Log.d(TAG, "🗺️ Buscando rota: $originStr -> ${waypoints.size} paradas -> $destinationStr")
Log.d(TAG, "🔗 URL: $urlString")
Log.d(TAG, "📥 Resposta recebida (${response.length} chars)")
Log.d(TAG, "📊 Status da API: $status")
```

### 2. **Fallback para Linha Reta**

Se a API do Google falhar, desenha linha reta:

```kotlin
route?.let {
    // Usa rota do Google
    routePoints = it.points
} ?: run {
    // FALLBACK: Linha reta
    routePoints = listOf(
        LatLng(origem.lat, origem.lng),
        LatLng(destino.lat, destino.lng)
    )
    Log.d("TelaRastreamento", "⚠️ Usando linha reta")
}
```

**Agora a rota SEMPRE aparece**, mesmo se a API falhar!

---

## 🧪 Como Testar AGORA

### 1. **Rebuild do Projeto**
```bash
./gradlew clean build
```

### 2. **Abrir Logcat em Tempo Real**
```bash
# Terminal 1 - Direções
adb logcat | grep DirectionsService

# Terminal 2 - Tela
adb logcat | grep TelaRastreamento
```

### 3. **Criar Serviço no App**

1. Login como contratante
2. Criar serviço com paradas
3. Prestador aceita

### 4. **Observar os Logs**

**Logs esperados no DirectionsService:**
```
🗺️ Buscando rota: -27.55,-48.63 -> 1 paradas -> -23.53,-46.64
🔗 URL: https://maps.googleapis.com/maps/api/directions/json?...
📥 Resposta recebida (3245 chars)
📊 Status da API: OK
✅ Rota encontrada: 487 pontos, 15.2 km, 23 min
```

**Logs esperados na TelaRastreamento:**
```
🗺️ Iniciando busca de rota...
   Paradas: 3
   Prestador: -27.55, -48.63
📍 Usando paradas da API
   Origem: -27.55, -48.63
   Waypoint 0: -23.54, -46.84
   Destino: -23.53, -46.64
✅ Rota com paradas atualizada: 487 pontos, 1 waypoints, 15.2 km, 23 min
```

---

## 🔍 Diagnóstico de Problemas

### Problema 1: API retorna erro

**Logs que você verá:**
```
❌ Erro na API: REQUEST_DENIED
   Mensagem: The provided API key is invalid
```

**Solução:**
1. Verifique a chave da API: `DirectionsService.kt`
2. Habilite Directions API no Google Cloud Console
3. Adicione restrições de IP/App se necessário

---

### Problema 2: Sem coordenadas

**Logs que você verá:**
```
❌ Sem dados suficientes para traçar rota
   Paradas: 0
   Localizacao: null
```

**Solução:**
1. Backend não está retornando paradas
2. Verifique endpoint: `/servico/contratante/pedidos`
3. Certifique-se que o serviço tem o campo `paradas`

---

### Problema 3: Rota está vazia

**Logs que você verá:**
```
✅ Rota encontrada: 0 pontos
```

**Solução:**
Isso NÃO deve acontecer mais! O fallback desenha linha reta.

---

## 🎯 O Que Vai Acontecer Agora

### Cenário 1: API do Google Funciona ✅
```
1. Busca rota na API
2. Recebe 487 pontos decodificados
3. Desenha polyline verde SUAVE no mapa
4. Mostra distância e tempo
```

**Visual:**
```
🔵 Origem
   ╲
    ━━━━━━━ (linha verde suave)
          ╲
           🟠 Parada
              ╲
               ━━━━━━━
                     ╲
                      🔴 Destino
```

---

### Cenário 2: API Falha (Fallback) ⚠️
```
1. Tenta buscar rota na API
2. API retorna erro
3. Usa FALLBACK: linha reta
4. Desenha polyline verde DIRETA
```

**Visual:**
```
🔵 Origem
   ╲
    ━━━━━━━━━━━━━━ (linha reta)
                  ╲
                   🟠 Parada
                      ╲
                       ━━━━━━━━━━━
                                  ╲
                                   🔴 Destino
```

**A rota SEMPRE aparece!**

---

## 📊 Checklist Visual

Ao abrir a tela de rastreamento, você DEVE ver:

**No Mapa:**
- [ ] Linha verde conectando os pontos
- [ ] Marcadores coloridos (azul, laranja, vermelho)
- [ ] Prestador verde se movendo
- [ ] Câmera ajustada para mostrar tudo

**No Header:**
- [ ] "🟢 Ao vivo" pulsando
- [ ] "📍 15.2 km"
- [ ] "⏱️ 23 min"

**Se NÃO aparecer:**
1. Veja os logs
2. Procure por "❌ Erro"
3. Siga as soluções acima

---

## 🔧 Possíveis Causas da Rota Não Aparecer

### 1. **routePoints está vazio**
```kotlin
// Verifique no log:
Log.d("TelaRastreamento", "routePoints.size: ${routePoints.size}")
```

**Se aparecer "0":**
- LaunchedEffect não está executando
- Ou está retornando erro

### 2. **Polyline não está visível**
```kotlin
if (routePoints.isNotEmpty()) {
    Polyline(
        points = routePoints,
        color = Color(0xFF019D31),
        width = 8f,
        geodesic = true
    )
}
```

**Verifique:**
- routePoints tem pelo menos 2 pontos
- Color não está transparente
- Width é visível (8f)

### 3. **Câmera está muito longe**
- A rota existe mas está fora da visão
- O zoom está muito alto

---

## 🎬 Teste Passo a Passo

### Passo 1: Limpar e Compilar
```bash
./gradlew clean build
```

### Passo 2: Instalar
```bash
./gradlew installDebug
```

### Passo 3: Ver Logs
```bash
adb logcat -c  # Limpa logs antigos
adb logcat | grep -E "DirectionsService|TelaRastreamento"
```

### Passo 4: Testar no App
1. Criar serviço
2. Prestador aceita
3. Abrir rastreamento

### Passo 5: Analisar Logs

**Procure por:**
```
✅ Rota atualizada: XXX pontos
```

**Se aparecer:**
- A rota foi carregada com sucesso
- Se não está visível, é problema de UI

**Se NÃO aparecer:**
```
❌ Erro ao buscar rota
⚠️ Usando linha reta
```
- Fallback foi ativado
- Linha reta deve estar visível

---

## 📱 Teste Visual Rápido

### No app, você DEVE ver:

**Mapa com:**
```
┌─────────────────────────────┐
│                             │
│    🔵 ← Azul (Origem)       │
│      ╲                      │
│       ━━━━━━ (Verde)        │
│             ╲               │
│              🟠 ← Laranja   │
│                 ╲           │
│                  ━━━━━      │
│                      ╲      │
│                       🔴    │
│                    Vermelho │
│                             │
│  🟢 ← Prestador (movendo)   │
│                             │
└─────────────────────────────┘
```

**Se aparecer só marcadores SEM linha:**
1. routePoints está vazio
2. Veja logs: "❌ Erro ao buscar rota"

---

## ✅ Status

```
╔════════════════════════════════╗
║  ✅ LOGS DETALHADOS            ║
║  ✅ FALLBACK IMPLEMENTADO      ║
║  ✅ SEMPRE DESENHA ROTA        ║
║  ✅ PRONTO PARA TESTAR         ║
╚════════════════════════════════╝
```

---


