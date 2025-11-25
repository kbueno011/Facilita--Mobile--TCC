# 🚨 PROBLEMA: Prestador não está enviando localização

## ✅ O que ESTÁ funcionando:

```logcat
✅ WebSocket conectado com sucesso
✅ Join na sala do serviço confirmado  
✅ Contratante recebendo conexão
✅ Sistema de rastreamento pronto
✅ Listeners registrados corretamente
```

## ❌ O que NÃO está funcionando:

```logcat
❌ Prestador NÃO envia eventos location_updated
❌ Marcador do prestador invisível no mapa
❌ Log: "⚠️ Prestador sem localização atual"
❌ Status: "⏳ Aguardando GPS"
```

---

## 🔍 DIAGNÓSTICO COMPLETO

### 1. Verifique o app do PRESTADOR

O prestador precisa:

#### ✅ Ter GPS ativado
```
Configurações > Localização > Ativado
```

#### ✅ Dar permissões de localização
```
Configurações > Aplicativos > Facilita > Permissões > Localização:
• Permitir o tempo todo (recomendado)
• Permitir apenas durante o uso
```

#### ✅ Estar com o app ABERTO e na tela correta
```
App precisa estar:
• Em primeiro plano
• Na tela "Serviço em Andamento"
• Com GPS ligado
```

#### ✅ Enviar localização via WebSocket
```kotlin
// O prestador deve executar isto a cada 5 segundos:
webSocketManager.updateLocation(
    servicoId = servicoId,
    latitude = gpsLatitude,
    longitude = gpsLongitude,
    userId = prestadorId
)
```

---

### 2. Verifique os logs do PRESTADOR

No app do prestador, filtre por: `WebSocketManager|LocationService`

#### ✅ Deve aparecer:

```logcat
✅ WebSocketManager: 📡 Enviando localização: lat=-23.xxx, lng=-46.xxx
✅ WebSocketManager: ✅ update_location emitido com sucesso
✅ LocationService: 📍 GPS atualizado: -23.xxx, -46.xxx
✅ LocationService: ⏱️ Enviando a cada 5 segundos
```

#### ❌ Se aparecer isto:

```logcat
❌ LocationService: ⚠️ Permissão de localização negada
❌ LocationService: ❌ GPS desativado
❌ WebSocketManager: ❌ Socket não conectado
```

---

### 3. Teste com LOCALIZAÇÃO FALSA

Se o prestador não tiver GPS real, use localização falsa:

#### Opção 1: Android Studio Emulator
```
1. Abra o emulador
2. Clique nos "..." (Extended Controls)
3. Location
4. Digite coordenadas: -23.5530637, -46.8374162
5. Send
```

#### Opção 2: Código de teste no prestador
```kotlin
// APENAS PARA TESTES!
LaunchedEffect(Unit) {
    while (true) {
        val latFake = -23.5530637 + Random.nextDouble(-0.001, 0.001)
        val lngFake = -46.8374162 + Random.nextDouble(-0.001, 0.001)
        
        webSocketManager.updateLocation(
            servicoId = servicoId,
            latitude = latFake,
            longitude = lngFake,
            userId = prestadorId
        )
        
        delay(5000) // A cada 5 segundos
    }
}
```

---

### 4. Verifique o BACKEND (servidor)

O servidor precisa:

#### ✅ Receber `update_location` do prestador
```javascript
// No servidor Node.js:
socket.on('update_location', (data) => {
    console.log('📍 Localização recebida do prestador:', data);
    
    // Envia para TODOS da sala do serviço
    socket.to(`servico_${data.servicoId}`).emit('location_updated', {
        servicoId: data.servicoId,
        latitude: data.latitude,
        longitude: data.longitude,
        userId: data.userId,
        userName: 'Victoria Maria',
        timestamp: new Date().toISOString()
    });
});
```

#### ✅ Logs do servidor devem mostrar:
```
✅ Prestador conectou: userId=3, userType=prestador
✅ Entrou na sala: servico_31
✅ Localização recebida: lat=-23.xxx, lng=-46.xxx
✅ Broadcast para sala: servico_31
```

---

## 🎯 CHECKLIST DE SOLUÇÃO

### No app do PRESTADOR:

- [ ] GPS está ativado?
- [ ] Permissão de localização concedida?
- [ ] App está aberto e em primeiro plano?
- [ ] WebSocket conectado?
- [ ] Código de `updateLocation()` está sendo chamado?
- [ ] A cada 5 segundos está enviando?

### No BACKEND:

- [ ] Servidor recebe evento `update_location`?
- [ ] Servidor faz broadcast `location_updated`?
- [ ] Sala do serviço está criada corretamente?
- [ ] Evento está sendo enviado para todos da sala?

### No app do CONTRATANTE (você):

- [x] WebSocket conectado ✅
- [x] Entrou na sala do serviço ✅
- [x] Listener `location_updated` registrado ✅
- [ ] Recebendo eventos? ❌ (problema está aqui)

---

## 🚀 SOLUÇÃO TEMPORÁRIA: Simulação no Contratante

Enquanto o prestador não enviar, você pode **simular** no contratante para testar a UI:

### Adicione este botão na tela de rastreamento:

```kotlin
// APENAS PARA TESTES - REMOVER EM PRODUÇÃO
if (BuildConfig.DEBUG) {
    FloatingActionButton(
        onClick = {
            // Simula localização próxima à origem
            val latFake = -23.5428573 + Random.nextDouble(-0.002, 0.002)
            val lngFake = -46.8482856 + Random.nextDouble(-0.002, 0.002)
            
            // Injeta manualmente no StateFlow
            webSocketManager._locationUpdate.value = LocationUpdate(
                servicoId = servicoId.toInt(),
                latitude = latFake,
                longitude = lngFake,
                prestadorName = "Victoria (TESTE)",
                timestamp = System.currentTimeMillis().toString()
            )
            
            Log.d("TESTE", "📍 Localização FAKE injetada: $latFake, $lngFake")
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        containerColor = Color.Red
    ) {
        Icon(Icons.Default.BugReport, "Simular GPS")
    }
}
```

---

## 📊 COMO CONFIRMAR QUE RESOLVEU

### Logs do CONTRATANTE devem mostrar:

```logcat
✅ WebSocketManager: 🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
✅ WebSocketManager: 📍 LOCALIZAÇÃO DO PRESTADOR RECEBIDA
✅ WebSocketManager:    🆔 ServicoId: 31
✅ WebSocketManager:    👤 Prestador: Victoria Maria
✅ WebSocketManager:    🌍 Latitude: -23.5428573
✅ WebSocketManager:    🌍 Longitude: -46.8482856
✅ TelaRastreamento: ✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO! ✅ ✅ ✅
✅ TelaRastreamento: 🎉 PRIMEIRA ATUALIZAÇÃO! Marcador agora VISÍVEL no mapa!
✅ TelaRastreamento: 🗺️ MARCADOR: Visível: SIM
```

### No mapa você verá:

```
╔═══════════════════════════════════════╗
║         MAPA DO RASTREAMENTO          ║
║                                       ║
║   🟢 ← Origem                         ║
║    |                                  ║
║    | ← Rota (linha verde)             ║
║    |                                  ║
║   🚗⊙⊙⊙ ← PRESTADOR (pulsante!) ✅  ║
║    |                                  ║
║    |                                  ║
║   ⚪ ← Parada                         ║
║    |                                  ║
║    |                                  ║
║   🔴 ← Destino                        ║
║                                       ║
║  Header:                              ║
║  🟢 Conectado • 🚗 Rastreando  ✅    ║
║  📍 2.2 km  ⏱️ 7 min                  ║
╚═══════════════════════════════════════╝
```

---

## 🆘 AINDA NÃO FUNCIONOU?

### Compartilhe estes logs:

1. **Log do PRESTADOR** (últimos 100 linhas)
2. **Log do CONTRATANTE** (você) - já temos
3. **Log do SERVIDOR Node.js**

### Comandos para capturar:

```bash
# Prestador
adb logcat -s WebSocketManager:D LocationService:D *:S > log_prestador.txt

# Contratante  
adb logcat -s WebSocketManager:D TelaRastreamento:D *:S > log_contratante.txt

# Servidor (no terminal do backend)
npm start | tee log_servidor.txt
```

---

## ✅ RESUMO DO PROBLEMA

**Situação atual:**
- ✅ Contratante: PRONTO para receber
- ❌ Prestador: NÃO está enviando
- ❌ Servidor: Provavelmente OK (precisa confirmar)

**O que fazer:**
1. Verificar app do prestador
2. Verificar GPS do prestador
3. Verificar código de envio no prestador
4. Verificar servidor está fazendo broadcast

**Próximo passo:**
🔍 Abra o app do PRESTADOR e verifique os logs!

