# 🔍 Guia de Debug - Como Saber se Está Funcionando

## ✅ CHECKLIST VISUAL - Acompanhe no Logcat

### 1️⃣ CONEXÃO ESTABELECIDA

```logcat
WebSocketManager  D  ╔════════════════════════════════════════════════╗
WebSocketManager  D  ║  ✅ WEBSOCKET CONECTADO COM SUCESSO!          ║
WebSocketManager  D  ╚════════════════════════════════════════════════╝
WebSocketManager  D  📡 URL: https://facilita-...azurewebsites.net
WebSocketManager  D  🔌 Estado da conexão: CONECTADO
WebSocketManager  D  ✅ _isConnected atualizado para: true
WebSocketManager  D  
WebSocketManager  D  🎯 AGUARDANDO: 
WebSocketManager  D     1️⃣ Entrada na sala do serviço (join_servico)
WebSocketManager  D     2️⃣ Atualizações de localização (location_updated)
WebSocketManager  D     3️⃣ Mensagens de chat (receive_message)
```

**✅ O QUE ISSO SIGNIFICA**:
- WebSocket conectou com sucesso
- Pronto para receber eventos em tempo real

---

### 2️⃣ PRESTADOR IDENTIFICADO

```logcat
TelaRastreamento  D  ╔════════════════════════════════════════════════╗
TelaRastreamento  D  ║  🚗 PRESTADOR CONECTADO AO SERVIÇO            ║
TelaRastreamento  D  ╚════════════════════════════════════════════════╝
TelaRastreamento  D     👤 Nome: Victoria Maria
TelaRastreamento  D     📞 Telefone: (11) 98765-4321
TelaRastreamento  D  
TelaRastreamento  D  📡 LOCALIZAÇÃO EM TEMPO REAL
TelaRastreamento  D     • A posição será atualizada via WebSocket
TelaRastreamento  D     • Evento: location_updated
TelaRastreamento  D     • Intervalo: ~5 segundos
TelaRastreamento  D  
TelaRastreamento  D  ⏳ Aguardando primeira posição via WebSocket...
```

**✅ O QUE ISSO SIGNIFICA**:
- Dados do prestador carregados da API
- Sistema pronto para receber localização
- Aguardando primeira atualização

---

### 3️⃣ PRIMEIRA LOCALIZAÇÃO RECEBIDA ⭐

```logcat
WebSocketManager  D  ═══════════════════════════════════════════════
WebSocketManager  D  🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
WebSocketManager  D  ═══════════════════════════════════════════════
WebSocketManager  D  📊 Total de args: 1
WebSocketManager  D  📦 Dados RAW completos:
WebSocketManager  D  {
WebSocketManager  D    "servicoId": 29,
WebSocketManager  D    "latitude": 37.4219983,
WebSocketManager  D    "longitude": -122.084,
WebSocketManager  D    "userId": 3,
WebSocketManager  D    "userName": "Victoria Maria",
WebSocketManager  D    "timestamp": "2025-11-25T00:18:34.832Z"
WebSocketManager  D  }
WebSocketManager  D  
WebSocketManager  D  ═══════════════════════════════════════════════
WebSocketManager  D  📍 LOCALIZAÇÃO DO PRESTADOR RECEBIDA:
WebSocketManager  D  ═══════════════════════════════════════════════
WebSocketManager  D     🆔 ServicoId: 29
WebSocketManager  D     👤 Prestador: Victoria Maria
WebSocketManager  D     👤 UserId: 3
WebSocketManager  D     🌍 Latitude: 37.4219983
WebSocketManager  D     🌍 Longitude: -122.084
WebSocketManager  D     ⏰ Timestamp: 2025-11-25T00:18:34.832Z
WebSocketManager  D  
WebSocketManager  D  ✅ ✅ ✅ COORDENADAS VÁLIDAS RECEBIDAS! ✅ ✅ ✅
WebSocketManager  D  
WebSocketManager  D  🎯 O PRESTADOR ESTÁ CONECTADO E ENVIANDO LOCALIZAÇÃO!
```

**✅ O QUE ISSO SIGNIFICA**:
- 🎉 **SUCESSO!** Localização recebida do servidor
- Coordenadas válidas (não é 0,0)
- Dados prontos para atualizar o mapa

---

### 4️⃣ MARCADOR ATUALIZADO NO MAPA ⭐⭐⭐

```logcat
TelaRastreamento  D  
TelaRastreamento  D  ╔════════════════════════════════════════════════╗
TelaRastreamento  D  ║  📡 ATUALIZAÇÃO DE LOCALIZAÇÃO RECEBIDA       ║
TelaRastreamento  D  ╚════════════════════════════════════════════════╝
TelaRastreamento  D     🆔 ServicoId recebido: 29
TelaRastreamento  D     🎯 ServicoId esperado: 29
TelaRastreamento  D     🌍 Latitude: 37.4219983
TelaRastreamento  D     🌍 Longitude: -122.084
TelaRastreamento  D     👤 Prestador: Victoria Maria
TelaRastreamento  D     ⏰ Timestamp: 2025-11-25T00:18:34.832Z
TelaRastreamento  D  
TelaRastreamento  D  🔍 Validações:
TelaRastreamento  D     • Serviço correto? true
TelaRastreamento  D     • Coordenadas válidas? true
TelaRastreamento  D  
TelaRastreamento  D  ✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO! ✅ ✅ ✅
TelaRastreamento  D  
TelaRastreamento  D  🎉 PRIMEIRA ATUALIZAÇÃO! Marcador agora VISÍVEL no mapa!
TelaRastreamento  D  📍 Posição anterior: 0.0, 0.0
TelaRastreamento  D  📍 Nova posição: 37.4219983, -122.084
TelaRastreamento  D  📏 Distância movida: 0,00 metros
TelaRastreamento  D  
TelaRastreamento  D  🗺️ MARCADOR:
TelaRastreamento  D     • Tipo: Círculo verde pulsante (estilo Uber)
TelaRastreamento  D     • Visível: SIM
TelaRastreamento  D     • Coordenadas: LatLng(37.4219983, -122.084)
TelaRastreamento  D  
TelaRastreamento  D  🎥 Câmera seguirá automaticamente o prestador
TelaRastreamento  D  ╚════════════════════════════════════════════════╝
```

**✅ O QUE ISSO SIGNIFICA**:
- 🎉 **MARCADOR VISÍVEL NO MAPA!**
- Todas validações passaram
- Câmera vai seguir automaticamente

---

### 5️⃣ MARCADOR SENDO DESENHADO

```logcat
TelaRastreamento  D  🎨 Desenhando marcador do prestador em: 37.4219983, -122.084
TelaRastreamento  D  🎯 Desenhando 3 marcadores modernos
TelaRastreamento  D     🟢 Origem (círculo verde)
TelaRastreamento  D     ⚪ Parada 1 (círculo branco)
TelaRastreamento  D     🔴 Destino (pin vermelho)
```

**✅ O QUE ISSO SIGNIFICA**:
- Marcador do prestador sendo renderizado
- Marcadores das paradas também desenhados
- Mapa completo com todos elementos

---

### 6️⃣ ATUALIZAÇÕES CONTÍNUAS

```logcat
TelaRastreamento  D  ✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO! ✅ ✅ ✅
TelaRastreamento  D  
TelaRastreamento  D  📍 Posição anterior: 37.4219983, -122.084
TelaRastreamento  D  📍 Nova posição: 37.4220150, -122.083850
TelaRastreamento  D  📏 Distância movida: 18,50 metros
TelaRastreamento  D  
TelaRastreamento  D  🗺️ MARCADOR:
TelaRastreamento  D     • Tipo: Círculo verde pulsante (estilo Uber)
TelaRastreamento  D     • Visível: SIM
TelaRastreamento  D     • Coordenadas: LatLng(37.4220150, -122.083850)
TelaRastreamento  D  
TelaRastreamento  D  🎥 Câmera seguirá automaticamente o prestador
```

**✅ O QUE ISSO SIGNIFICA**:
- 🚗 Prestador está se movendo!
- Marcador atualizando suavemente
- Sistema funcionando perfeitamente

---

## 🚨 PROBLEMAS COMUNS E COMO IDENTIFICAR

### ❌ Problema 1: "Prestador sem localização atual"

**Você verá**:
```logcat
ServicoViewModel  W     ⚠️ Prestador sem localização atual
```

**O que significa**:
- API não retornou `latitudeAtual` e `longitudeAtual`
- Isso é NORMAL! A localização vem do WebSocket

**✅ Solução**: 
- AGUARDE a primeira atualização via WebSocket
- Verifique se há logs de `location_updated`

---

### ❌ Problema 2: Coordenadas 0,0

**Você verá**:
```logcat
TelaRastreamento  W  ⚠️ COORDENADAS INVÁLIDAS RECEBIDAS (0,0)
TelaRastreamento  W     O prestador pode não estar com GPS ativo
```

**O que significa**:
- Prestador não está enviando localização válida
- GPS pode estar desativado

**✅ Solução**:
1. Verificar se prestador ativou GPS
2. Verificar permissões de localização do prestador
3. Verificar se prestador está com app aberto

---

### ❌ Problema 3: Serviço diferente

**Você verá**:
```logcat
TelaRastreamento  W  ⚠️ UPDATE IGNORADO - Serviço diferente
TelaRastreamento  W     Esperado: 29
TelaRastreamento  W     Recebido: 28
```

**O que significa**:
- Recebeu localização de outro serviço
- Sistema ignora automaticamente

**✅ Solução**:
- Isso é normal e correto
- Sistema filtra automaticamente

---

### ❌ Problema 4: Marcador não visível

**Você verá**:
```logcat
TelaRastreamento  W  ⚠️ Marcador do prestador NÃO VISÍVEL - aguardando primeira localização
```

**O que significa**:
- Ainda não recebeu nenhuma localização válida
- `prestadorVisivel = false`

**✅ Solução**:
1. Aguarde primeira atualização
2. Verifique logs de WebSocket
3. Verifique se prestador está online

---

## 📊 INDICADORES VISUAIS NO APP

### No Header da Tela

```
╔════════════════════════════════════════╗
║  ← Serviço em andamento          📊   ║
║                                        ║
║  🟢 Conectado • 🚗 Rastreando         ║  ← ✅ TUDO OK!
║  📍 2.5 km  ⏱️ 8 min                  ║
╚════════════════════════════════════════╝
```

### Estados Possíveis:

#### ✅ Tudo Funcionando
```
🟢 Conectado • 🚗 Rastreando
```

#### ⏳ Aguardando GPS
```
🟢 Conectado • ⏳ Aguardando GPS
```

#### ❌ Sem Conexão
```
🔴 Offline • ⏳ Aguardando GPS
```

---

## 🎯 TESTE RÁPIDO - 3 Passos

### 1️⃣ Abra o Logcat
Filtro recomendado: `TelaRastreamento|WebSocketManager`

### 2️⃣ Inicie o Rastreamento
- Entre na tela de rastreamento do serviço
- Aguarde 5-10 segundos

### 3️⃣ Procure por:
```logcat
✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO! ✅ ✅ ✅
```

**Se ver isso**: 🎉 **FUNCIONANDO PERFEITAMENTE!**

---

## 📸 O que você DEVE ver no mapa

### Marcadores Visíveis:

1. **🟢 Origem** - Círculo verde com halo
2. **⚪ Paradas** - Círculos brancos com borda verde
3. **🔴 Destino** - Pin vermelho estilo Google Maps
4. **🚗 PRESTADOR** - Círculo verde PULSANTE (o principal!)

### Efeitos Visuais:

- ✨ Halo verde pulsando ao redor do prestador
- 📱 Indicador "🚗 Rastreando" no header
- 🗺️ Câmera seguindo suavemente o movimento
- 📍 Linha verde conectando todos os pontos

---

## 🆘 AINDA NÃO FUNCIONA?

### Checklist Final:

- [ ] WebSocket conectado? (🟢 Conectado)
- [ ] Prestador com GPS ativo?
- [ ] Coordenadas diferentes de 0,0?
- [ ] ServicoId correto?
- [ ] Logs mostram "MARCADOR ATUALIZADO"?
- [ ] `prestadorVisivel = true`?

### Se TUDO checado e AINDA não funciona:

1. **Compartilhe os logs completos** começando de:
   ```logcat
   TelaRastreamento  D  🚗 PRESTADOR CONECTADO AO SERVIÇO
   ```
   até:
   ```logcat
   TelaRastreamento  D  ✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO
   ```

2. **Tire print** da tela mostrando os indicadores

3. **Verifique** se o prestador está realmente enviando localização

---

**✅ Com esses logs você terá VISIBILIDADE COMPLETA do que está acontecendo!**

