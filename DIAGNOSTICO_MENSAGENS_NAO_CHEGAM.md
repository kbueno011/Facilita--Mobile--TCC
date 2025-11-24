# 🔍 DIAGNÓSTICO - Mensagens Não Chegam

## ❌ PROBLEMA

- ✅ Suas mensagens são **enviadas** (`✅ Mensagem enviada via WebSocket`)
- ❌ Mensagens do prestador **NÃO chegam** (falta log `💬 Mensagem recebida`)

---

## 🔧 CORREÇÕES APLICADAS

### 1. Listener Global de Debug
Adicionado para capturar **TODOS** os eventos que chegam:
```kotlin
socket?.on(Socket.Emitter.Listener { args ->
    Log.d(TAG, "🔍 [DEBUG] Evento genérico capturado!")
    Log.d(TAG, "   Args total: ${args.size}")
})
```

### 2. Logs Detalhados em receive_message
```kotlin
Log.d(TAG, "🎉🎉🎉 EVENTO RECEIVE_MESSAGE CHAMADO! 🎉🎉🎉")
Log.d(TAG, "   Total de args: ${args.size}")
Log.d(TAG, "📦 Dados RAW: $data")
```

### 3. Confirmação de Registro
```kotlin
Log.d(TAG, "✅ Listener 'receive_message' REGISTRADO!")
```

---

## 🧪 TESTE AGORA

### 1. Clean & Rebuild
```
Build > Clean Project
Build > Rebuild Project
```

### 2. Execute
```
Run > Run 'app'
```

### 3. Filtre Logcat
```
Filtro: "WebSocketManager"
```

### 4. Fluxo de Teste
1. Abra chat
2. **PROCURE POR:**
   - `✅ Listener 'receive_message' REGISTRADO!`
   - `🔍 [DEBUG] Evento genérico capturado!` (quando houver qualquer evento)
3. **Peça ao prestador enviar mensagem**
4. **Observe:**
   - Se aparecer `🔍 [DEBUG] Evento genérico` → Servidor está enviando ALGO
   - Se aparecer `🎉 EVENTO RECEIVE_MESSAGE CHAMADO` → Listener funcionando!
   - Se NÃO aparecer NADA → Servidor não está enviando ou nome do evento está errado

---

## 📊 LOGS ESPERADOS

### Ao Conectar
```log
WebSocketManager: 📡 Registrando listeners...
WebSocketManager: ✅ Listener 'receive_message' REGISTRADO!
WebSocketManager: ✅ Socket conectado!
```

### Ao Enviar Mensagem
```log
TelaChat: 📤 Enviando mensagem: teste
WebSocketManager: 💬 Enviando mensagem de chat:
   ServicoId: 9
   Mensagem: teste
WebSocketManager: ✅ Mensagem de chat enviada via WebSocket
```

### Ao Receber Mensagem (ESPERADO)
```log
WebSocketManager: 🔍 [DEBUG] Evento genérico capturado!
   Args total: 1
   Arg[0]: {...}

WebSocketManager: 🎉🎉🎉 EVENTO RECEIVE_MESSAGE CHAMADO! 🎉🎉🎉
WebSocketManager: 💬 Mensagem de chat recebida!
   Total de args: 1
WebSocketManager: 📦 Dados RAW: {"servicoId":9,"mensagem":"oi","sender":"prestador",...}
   ✅ ServicoId: 9
   ✅ Mensagem: oi
   ✅ Sender: prestador
WebSocketManager: ✅ Mensagem adicionada. Total: 2
```

---

## 🎯 CENÁRIOS POSSÍVEIS

### Cenário 1: NÃO aparece NENHUM log de evento genérico
**Significa:** Servidor não está enviando NADA para você
**Possível causa:**
- Você não está na sala correta
- Servidor não está fazendo broadcast
- Prestador não está conectado na mesma sala

**Solução:** Verificar se `join_servico` está sendo chamado com o ID correto

### Cenário 2: Aparece evento genérico MAS NÃO é receive_message
**Significa:** Servidor está enviando com nome de evento diferente
**Possível causa:**
- Backend usa nome diferente (ex: `message_received`, `new_message`)

**Solução:** Veja o nome do evento no log e registre listener com esse nome

### Cenário 3: Aparece receive_message MAS sem dados
**Significa:** Evento correto mas formato errado
**Possível causa:**
- JSON não tem os campos esperados
- Estrutura diferente

**Solução:** Veja `📦 Dados RAW` e ajuste parsing

---

## 🔍 PRÓXIMOS PASSOS

### Depois de Testar:

1. **SE aparecer** `🔍 [DEBUG] Evento genérico`:
   - Copie o log completo
   - Me envie para eu ver que evento está chegando

2. **SE aparecer** `🎉 EVENTO RECEIVE_MESSAGE`:
   - Copie os dados RAW
   - Veja se tem os campos: servicoId, mensagem, sender, userName

3. **SE NÃO aparecer NADA**:
   - Verifique se entrou na sala: procure `✅ Entrou na sala do serviço`
   - Confirme que prestador está na mesma sala

---

## 📝 CHECKLIST DE DEBUG

- [ ] Rebuild completo
- [ ] Logcat filtrado por "WebSocketManager"
- [ ] Vejo log: `✅ Listener 'receive_message' REGISTRADO!`
- [ ] Vejo log: `✅ Socket conectado!`
- [ ] Vejo log: `🚪 Entrando na sala do serviço: 9`
- [ ] Prestador envia mensagem
- [ ] Aguardo 5 segundos
- [ ] Verifico se aparece `🔍 [DEBUG] Evento genérico`
- [ ] Verifico se aparece `🎉 EVENTO RECEIVE_MESSAGE`

---

**Execute agora e me envie os logs! Vou descobrir o que está acontecendo!** 🔍🚀

