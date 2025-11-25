# 🔍 GUIA DE MONITORAMENTO DO WEBSOCKET
## Como saber se está conectado com o prestador e recebendo localização

---

## 📊 LOGS IMPLEMENTADOS

### 1️⃣ **CONEXÃO WEBSOCKET**

Quando o WebSocket conecta, você verá:

```
╔════════════════════════════════════════════════╗
║  ✅ WEBSOCKET CONECTADO COM SUCESSO!          ║
╚════════════════════════════════════════════════╝
📡 URL: https://facilita-c6hhb9csgygudrdz...
🔌 Estado da conexão: CONECTADO
⏰ Timestamp: 1732485600000
✅ _isConnected atualizado para: true

🎯 AGUARDANDO:
   1️⃣ Entrada na sala do serviço (join_servico)
   2️⃣ Atualizações de localização (location_updated)
   3️⃣ Mensagens de chat (receive_message)
```

**✅ Significa:** Conexão estabelecida com o servidor

---

### 2️⃣ **ENTRADA NA SALA DO SERVIÇO**

Quando você entra na sala do serviço específico:

```
╔════════════════════════════════════════════════╗
║  🚪 ENTRANDO NA SALA DO SERVIÇO               ║
╚════════════════════════════════════════════════╝
🆔 ServicoId: 23
🔌 Socket conectado? true
📡 Emitindo evento: join_servico
✅ Evento join_servico emitido com sucesso
⏳ Aguardando confirmação do servidor...
```

E depois a confirmação:

```
╔════════════════════════════════════════════════╗
║  🎉 CONFIRMAÇÃO: ENTROU NA SALA!              ║
╚════════════════════════════════════════════════╝
📦 Dados da resposta:
{
  "servicoId": "23",
  "message": "Conectado ao serviço 23"
}

✅ SUCESSO!
   🆔 ServicoId: 23
   💬 Mensagem: Conectado ao serviço 23

🎯 AGORA VOCÊ IRÁ RECEBER:
   📍 Atualizações de localização do prestador
   💬 Mensagens de chat do prestador
```

**✅ Significa:** Você está na sala e PRONTO para receber dados do prestador

---

### 3️⃣ **LOCALIZAÇÃO DO PRESTADOR RECEBIDA** ⭐

**ESTE É O LOG MAIS IMPORTANTE!** Quando o prestador enviar a localização dele:

```
═══════════════════════════════════════════════
🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
═══════════════════════════════════════════════
📊 Total de args: 1
📦 Dados RAW completos:
{
  "servicoId": 23,
  "latitude": -23.5482478,
  "longitude": -46.8470907,
  "prestadorName": "Victoria Maria",
  "userId": 2,
  "timestamp": "2025-11-24T23:36:15.566Z"
}

📍 LOCALIZAÇÃO DO PRESTADOR:
   🆔 ServicoId: 23
   👤 Prestador: Victoria Maria
   👤 UserId: 2
   🌍 Latitude: -23.5482478
   🌍 Longitude: -46.8470907
   ⏰ Timestamp: 2025-11-24T23:36:15.566Z

✅ Coordenadas válidas recebidas!
✅ LocationUpdate atualizado no StateFlow!
📊 Valor atual do StateFlow: LocationUpdate(...)
═══════════════════════════════════════════════
```

**✅ Significa:** 
- O prestador está CONECTADO
- Ele está ENVIANDO a localização
- Você está RECEBENDO os dados
- O mapa DEVE atualizar

---

### 4️⃣ **MENSAGENS DE CHAT**

Quando receber mensagem do prestador:

```
🎉🎉🎉 EVENTO RECEIVE_MESSAGE CHAMADO! 🎉🎉🎉
💬 Mensagem de chat recebida!
   Total de args: 1
📦 Dados RAW: {...}
   ✅ ServicoId: 23
   ✅ Mensagem: Estou a caminho!
   ✅ Sender: prestador
   ✅ UserName: Victoria Maria
   ✅ Timestamp: 1732485615566
✅ Mensagem adicionada. Total: 5
```

---

### 5️⃣ **CATCH-ALL (TODOS OS EVENTOS)**

**TODOS** os eventos do servidor serão logados:

```
🔔 EVENTO RECEBIDO: location_updated
   📊 Total de args: 1
   📦 Arg[0]: JSONObject...

🔔 EVENTO RECEBIDO: receive_message
   📊 Total de args: 1
   📦 Arg[0]: JSONObject...
```

---

## 🚨 CENÁRIOS DE PROBLEMAS

### ❌ **PROBLEMA 1: Socket não conecta**

```
❌ ERRO: Socket não está conectado!
   Não é possível entrar na sala sem conexão
```

**Solução:**
- Verificar internet
- Verificar se servidor está online
- Verificar URL no código

---

### ❌ **PROBLEMA 2: Não recebe localização**

Se você vê:

```
⚠️ Prestador sem localização atual
```

**Mas NÃO vê:**

```
🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
```

**Causas possíveis:**

1. **Prestador não está conectado**
   - Verificar app do prestador
   - Verificar se ele fez login

2. **Prestador não entrou na mesma sala**
   - Verificar se o `servicoId` é o mesmo nos dois apps

3. **Prestador não está enviando localização**
   - Verificar se GPS do prestador está ativo
   - Verificar se app do prestador tem permissão de localização
   - Verificar logcat do app do prestador

4. **Nome do evento está diferente no backend**
   - Verificar documentação do backend
   - Testar com nomes alternativos

---

### ❌ **PROBLEMA 3: Coordenadas zeradas**

```
⚠️ AVISO: Coordenadas zeradas! Prestador pode não ter GPS ativo
```

**Significa:** 
- O prestador ESTÁ conectado
- Mas GPS dele não está funcionando
- Ou não deu permissão de localização

---

## 📱 COMO TESTAR

### Teste 1: Verificar Conexão

1. Abra o app contratante
2. Entre no rastreamento de um serviço
3. **Procure no Logcat:**
   ```
   ✅ WEBSOCKET CONECTADO COM SUCESSO!
   ```

✅ **Se aparecer:** Conexão OK  
❌ **Se NÃO aparecer:** Problema de internet/servidor

---

### Teste 2: Verificar Entrada na Sala

1. Continue na tela de rastreamento
2. **Procure no Logcat:**
   ```
   🎉 CONFIRMAÇÃO: ENTROU NA SALA!
   ```

✅ **Se aparecer:** Você está na sala correta  
❌ **Se NÃO aparecer:** Problema ao entrar na sala

---

### Teste 3: Verificar Localização do Prestador

**ESTE É O TESTE MAIS IMPORTANTE!**

1. Prestador deve abrir o app dele
2. Prestador deve entrar no mesmo serviço
3. Prestador deve ter GPS ativo
4. **Procure no Logcat do CONTRATANTE:**
   ```
   🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
   📍 Latitude: -23.5482478
   📍 Longitude: -46.8470907
   ```

✅ **Se aparecer:** TUDO FUNCIONANDO!  
❌ **Se NÃO aparecer:** Prestador não está enviando

**Neste caso, verificar o Logcat do PRESTADOR:**
- Procurar por: `update_location emitido`
- Se não aparecer, GPS do prestador está desligado

---

## 🔍 FILTROS DO LOGCAT

Para facilitar, use esses filtros no Logcat:

### Ver TODOS os logs do WebSocket:
```
WebSocketManager
```

### Ver APENAS conexões:
```
WebSocketManager CONECTADO
```

### Ver APENAS localizações:
```
WebSocketManager LOCALIZAÇÃO RECEBIDA
```

### Ver APENAS mensagens de chat:
```
WebSocketManager EVENTO RECEIVE_MESSAGE
```

### Ver TODOS os eventos:
```
WebSocketManager EVENTO RECEBIDO
```

---

## 📊 FLUXO COMPLETO (O QUE DEVE APARECER)

Quando tudo funciona corretamente:

```
1. ✅ WEBSOCKET CONECTADO COM SUCESSO!
   ↓
2. 🚪 ENTRANDO NA SALA DO SERVIÇO
   ↓
3. 🎉 CONFIRMAÇÃO: ENTROU NA SALA!
   ↓
4. 🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR! (a cada X segundos)
   ↓
5. 💬 MENSAGEM DE CHAT RECEBIDA (quando prestador enviar)
```

---

## 🎯 CHECKLIST DE VERIFICAÇÃO

### No App do CONTRATANTE:

- [ ] ✅ WebSocket conectado?
- [ ] ✅ Entrou na sala do serviço?
- [ ] ✅ Recebendo localização do prestador?
- [ ] ✅ Coordenadas são válidas (não zeradas)?
- [ ] ✅ Mapa atualiza com a localização?

### No App do PRESTADOR:

- [ ] ✅ GPS está ativo?
- [ ] ✅ App tem permissão de localização?
- [ ] ✅ Prestador entrou no mesmo serviço?
- [ ] ✅ App do prestador está enviando localização? (ver logcat dele)

---

## 🚀 RESUMO

### ✅ CONEXÃO OK = Você verá:

1. `WEBSOCKET CONECTADO COM SUCESSO`
2. `ENTROU NA SALA`
3. `LOCALIZAÇÃO RECEBIDA DO PRESTADOR` (repetindo a cada X segundos)

### ❌ PROBLEMA = Você verá:

- `Socket não está conectado`
- `Prestador sem localização atual` (sem o log de recebimento)
- `Coordenadas zeradas`

---

## 📞 SUPORTE

Se mesmo com todos os logs você não conseguir identificar o problema:

1. Copie TODOS os logs do Logcat (filtro: `WebSocketManager`)
2. Verifique o logcat do app do prestador também
3. Compare os `servicoId` dos dois apps
4. Verifique se o backend está rodando
5. Teste a conexão WebSocket diretamente (usando ferramentas como Postman)

---

## 🎉 CONCLUSÃO

Com esses logs detalhados, você consegue identificar:

✅ Se conectou no WebSocket  
✅ Se entrou na sala correta  
✅ Se está recebendo dados do prestador  
✅ Qual o problema exato se algo não funcionar

**Agora é só rodar o app e acompanhar os logs!** 🚀

