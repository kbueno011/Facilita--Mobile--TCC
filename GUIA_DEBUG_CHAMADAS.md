# 🔍 GUIA DE DEBUG - Sistema de Chamadas

## 🎯 PROBLEMA ATUAL

Quando você clica para ligar, a outra pessoa não recebe notificação da chamada.

---

## ✅ VERIFICAÇÕES ESSENCIAIS

### 1. WebSocket Está Conectado?

**No Logcat, procure por:**

#### Ao abrir o app:
```log
🚀 Iniciando app...
✅ App iniciado com sucesso!

╔════════════════════════════════════════════════╗
║  📞 CallMonitorService INICIADO               ║
╚════════════════════════════════════════════════╝

✅ Socket obtido: true
🔌 Registrando listener para 'call:incoming'...
✅ Listener 'call:incoming' REGISTRADO com sucesso!

📊 STATUS FINAL:
   Socket conectado: true
   Socket ID: ABC123XYZ
   Listeners ativos: true
⏳ Aguardando chamadas...
```

**❌ Se ver:**
```log
❌❌❌ SOCKET É NULL! WebSocket não está conectado!
```

**Solução:** O WebSocket não está conectado. Você precisa:
1. Fazer login no app
2. Abrir qualquer tela que use WebSocket (chat, rastreamento)
3. Isso vai conectar o WebSocket

---

### 2. Ao Clicar para Ligar

**Procure por:**

```log
╔════════════════════════════════════════════════╗
║  📞 INICIANDO CHAMADA                         ║
╚════════════════════════════════════════════════╝
   ServicoId: 10
   TargetUserId: 2
   CallType: video

✅ Socket conectado: ABC123XYZ

📤 EMITINDO EVENTO 'call:initiate'
📦 Payload:
{
  "servicoId": "10",
  "callerId": "1",
  "callerName": "João",
  "targetUserId": "2",
  "callType": "video",
  "offer": {...}
}

✅ Evento 'call:initiate' ENVIADO!
⏳ Aguardando resposta do destinatário...
⏳ O servidor deve enviar 'call:incoming' para o targetUserId: 2
```

**❌ Se ver:**
```log
❌❌❌ SOCKET NÃO ESTÁ CONECTADO!
   Não é possível iniciar chamada
```

**Solução:** WebSocket desconectou. Feche e abra o app novamente.

---

### 3. No Dispositivo que RECEBE a Chamada

**Procure por:**

```log
╔════════════════════════════════════════════════╗
║  🔥🔥🔥 CALL:INCOMING EVENTO RECEBIDO! 🔥🔥🔥 ║
╚════════════════════════════════════════════════╝

📦 Total de argumentos: 1
📦 Tipo do primeiro arg: JSONObject

📄 JSON completo recebido:
{
  "servicoId": "10",
  "callerId": "1",
  "callerName": "João",
  "targetUserId": "2",
  "callType": "video",
  "callId": "10_1_1234567890"
}

📋 DADOS EXTRAÍDOS:
   👤 De: João
   📱 Tipo: video
   🆔 CallId: 10_1_1234567890
   🏠 ServiceId: 10
   👥 CallerId: 1

🚀 Criando Intent para abrir IncomingCallActivity...
🎬 Chamando startActivity...

✅✅✅ IncomingCallActivity INICIADA COM SUCESSO! ✅✅✅
```

**❌ Se NÃO ver nada:**

O evento `call:incoming` NÃO está chegando. Possíveis causas:

1. **Backend não está encaminhando o evento**
   - O backend recebe `call:initiate` mas não envia `call:incoming`
   
2. **TargetUserId errado**
   - O evento está sendo enviado para o usuário errado

3. **WebSocket desconectado no receptor**
   - O receptor não está conectado ao WebSocket

---

## 🧪 TESTE PASSO A PASSO

### Dispositivo 1 (João - Iniciador):

1. **Abra o Logcat** e filtre por: `WebRTCManager`
2. **Abra o chat** com Maria
3. **Clique no ícone de vídeo** 📹
4. **Verifique os logs:**
   - ✅ "Socket conectado: true"
   - ✅ "Evento 'call:initiate' ENVIADO!"

### Dispositivo 2 (Maria - Receptor):

1. **Abra o Logcat** e filtre por: `CallMonitorService`
2. **Verifique se o serviço está rodando:**
   - ✅ "CallMonitorService INICIADO"
   - ✅ "Socket conectado: true"
   - ✅ "Listener 'call:incoming' REGISTRADO"
3. **Aguarde o evento** (João liga)
4. **Deve aparecer:**
   - ✅ "🔥🔥🔥 CALL:INCOMING EVENTO RECEBIDO!"
   - ✅ "IncomingCallActivity INICIADA"

---

## 🔧 SOLUÇÃO DE PROBLEMAS

### Problema 1: Socket é NULL

**Sintoma:**
```log
❌❌❌ SOCKET É NULL! WebSocket não está conectado!
```

**Solução:**
1. Certifique-se de estar logado
2. Abra o chat ou rastreamento (isso conecta o WebSocket)
3. Verifique se vê: `✅ WEBSOCKET CONECTADO COM SUCESSO!`

---

### Problema 2: Evento não chega no receptor

**Sintoma:** Nada aparece no Logcat do receptor quando João liga

**Possíveis causas:**

#### Causa A: Backend não implementou corretamente

O backend deve fazer:
```javascript
// Ao receber call:initiate do João
socket.on('call:initiate', (data) => {
    console.log('Recebido call:initiate:', data);
    
    // Encontrar socket do targetUserId (Maria)
    const targetSocket = findSocketByUserId(data.targetUserId);
    
    if (targetSocket) {
        // Enviar call:incoming para Maria
        targetSocket.emit('call:incoming', {
            servicoId: data.servicoId,
            callerId: data.callerId,
            callerName: data.callerName,
            callType: data.callType,
            callId: generateCallId(),
            offer: data.offer
        });
        
        console.log('Enviado call:incoming para userId:', data.targetUserId);
    }
});
```

#### Causa B: TargetUserId errado

Verifique nos logs:
```log
   TargetUserId: 2  ← Este é o ID correto da Maria?
```

Se não for, o evento está sendo enviado para o usuário errado.

#### Causa C: WebSocket desconectado no receptor

No dispositivo de Maria, procure por:
```log
❌ Socket conectado: false
```

Se aparecer, o WebSocket dela está desconectado.

---

### Problema 3: Tela não abre

**Sintoma:** Logs mostram que evento chegou mas tela não abre

Procure por:
```log
❌❌❌ ERRO CRÍTICO ao processar chamada! ❌❌❌
```

Veja o stack trace logo abaixo para identificar o erro.

---

## 📊 CHECKLIST COMPLETO

### No Iniciador (João):

- [ ] WebSocket conectado (`Socket conectado: true`)
- [ ] Evento `call:initiate` enviado
- [ ] Payload contém: servicoId, callerId, callerName, targetUserId, callType

### No Receptor (Maria):

- [ ] CallMonitorService iniciado
- [ ] WebSocket conectado
- [ ] Listener `call:incoming` registrado
- [ ] Evento `call:incoming` recebido
- [ ] IncomingCallActivity aberta

### No Backend:

- [ ] Recebe evento `call:initiate`
- [ ] Identifica targetUserId
- [ ] Encontra socket do targetUserId
- [ ] Envia evento `call:incoming` para esse socket

---

## 🎯 TESTE RÁPIDO

### Abra 2 Logcats Lado a Lado

**Logcat 1 (João):**
```
Filtro: WebRTCManager
```

**Logcat 2 (Maria):**
```
Filtro: CallMonitorService
```

### Ação:
João clica em "Chamada de Vídeo"

### Resultado Esperado:

**Logcat 1:**
```
✅ Evento 'call:initiate' ENVIADO!
```

**Logcat 2 (3 segundos depois):**
```
🔥🔥🔥 CALL:INCOMING EVENTO RECEBIDO!
✅✅✅ IncomingCallActivity INICIADA!
```

---

## 🔍 COMANDOS ADB ÚTEIS

### Ver logs em tempo real:

**Para o iniciador:**
```bash
adb logcat | findstr "WebRTCManager"
```

**Para o receptor:**
```bash
adb logcat | findstr "CallMonitorService"
```

### Ver logs do WebSocket:
```bash
adb logcat | findstr "WebSocketManager"
```

### Ver TUDO relacionado a chamadas:
```bash
adb logcat | findstr /C:"call:" /C:"CHAMADA" /C:"CallMonitor"
```

---

## 📱 TESTE FINAL

1. **Dispositivo A:** Inicie o app, faça login
2. **Dispositivo B:** Inicie o app, faça login
3. **Ambos:** Verifique se WebSocket conectou
4. **Dispositivo A:** Abra chat e clique em vídeo
5. **Resultado:** Dispositivo B deve mostrar tela em tela cheia

**✅ Se funcionar:** Sistema OK!

**❌ Se não funcionar:** 
- Envie os logs dos 2 dispositivos
- Verifique se backend está encaminhando os eventos
- Confirme que targetUserId está correto

---

## 🆘 ÚLTIMO RECURSO

Se nada funcionar, teste manualmente:

1. Abra o Chrome
2. Vá para: `chrome://inspect#devices`
3. Encontre seu dispositivo Android
4. Clique em "inspect"
5. No console, digite:
```javascript
// Simular evento recebido
window.postMessage({
    type: 'call:incoming',
    data: {
        callerName: 'João',
        callType: 'video',
        servicoId: '10',
        callerId: '1',
        callId: 'test123'
    }
}, '*');
```

Se a tela abrir com este teste, o problema é 100% no backend.

---

**Data:** 2025-01-12  
**Status:** 🔍 **SISTEMA COM LOGS DETALHADOS PARA DEBUG**

