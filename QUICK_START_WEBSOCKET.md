# 🚀 GUIA RÁPIDO - Como Saber se o WebSocket Está Funcionando

## 📋 CHECKLIST RÁPIDO

### ✅ 1. CONECTOU AO SERVIDOR?

**Procure no Logcat:**
```
🌟🌟🌟 CONECTADO AO SERVIDOR! 🌟🌟🌟
```

**Se aparecer = SUCESSO!** ✅  
**Se NÃO aparecer = Problema de rede** ❌

---

### ✅ 2. IDENTIFICOU O USUÁRIO?

**Procure no Logcat:**
```
╔════════════════════════════════════════════════╗
║  👤 ENVIANDO IDENTIFICAÇÃO DO USUÁRIO         ║
╚════════════════════════════════════════════════╝
```

**Se aparecer = SUCESSO!** ✅  
**Se NÃO aparecer = Socket não conectou antes** ❌

---

### ✅ 3. ENTROU NA SALA DO SERVIÇO?

**Procure no Logcat:**
```
╔════════════════════════════════════════════════╗
║  🎉 CONFIRMAÇÃO: ENTROU NA SALA!              ║
╚════════════════════════════════════════════════╝
```

**Se aparecer = SUCESSO!** ✅  
**Se NÃO aparecer = Não chamou joinServico()** ❌

---

### ✅ 4. RECEBENDO LOCALIZAÇÃO DO PRESTADOR?

**Procure no Logcat:**
```
═══════════════════════════════════════════════
🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
═══════════════════════════════════════════════
```

**Se aparecer = SUCESSO!** ✅  
**Se NÃO aparecer = Prestador não está enviando** ❌

---

## 🔍 DIAGNÓSTICO DE PROBLEMAS

### Problema: "Prestador sem localização atual"

**Causas possíveis:**

1. **Prestador não está online**
   - O app do prestador está fechado
   - O prestador não aceitou o serviço ainda

2. **Prestador não entrou na sala**
   - O app do prestador não chamou `joinServico()`

3. **GPS do prestador desligado**
   - O prestador não está compartilhando localização

4. **Nome do evento diferente**
   - O servidor está enviando com nome diferente de `location_updated`
   - **SOLUÇÃO:** Procure no Logcat por:
     ```
     🚨🚨🚨 EVENTO ALTERNATIVO DETECTADO
     ```

---

## 📱 COMO TESTAR AGORA

1. **Abra o Logcat** no Android Studio
2. **Filtre por:** `WebSocketManager`
3. **Inicie o rastreamento** de um serviço
4. **Procure os logs acima** na ordem (1, 2, 3, 4)

---

## 🎯 LOGS IMPORTANTES

### ✅ Log de Sucesso Total:

```
🌟🌟🌟 CONECTADO AO SERVIDOR! 🌟🌟🌟
      ↓
👤 ENVIANDO IDENTIFICAÇÃO DO USUÁRIO
      ↓
🎉 CONFIRMAÇÃO: ENTROU NA SALA!
      ↓
🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
```

Se você ver essa sequência = **TUDO FUNCIONANDO!** 🎉

---

## 🚨 TROUBLESHOOTING

| Log que Aparece | O que significa | Ação |
|----------------|-----------------|------|
| Nenhum log | Socket não inicializou | Verifique se `WebSocketManager.connect()` foi chamado |
| Só `CONECTADO` | Não identificou usuário | Verifique `emitUserConnected()` |
| Sem `ENTROU NA SALA` | Não chamou joinServico | Verifique `joinServico()` |
| Sem `LOCALIZAÇÃO` | Prestador offline ou GPS desligado | Verifique app do prestador |

---

## 📞 EXEMPLO PRÁTICO

```kotlin
// 1. Conectar ao WebSocket
val wsManager = WebSocketManager.getInstance()
wsManager.connect(
    userId = 5,
    userType = "contratante",
    userName = "João"
)

// 2. Entrar na sala do serviço
wsManager.joinServico(servicoId = "26")

// 3. Observar localização
LaunchedEffect(Unit) {
    wsManager.locationUpdate.collect { location ->
        if (location != null) {
            Log.d("APP", "📍 Prestador em: ${location.latitude}, ${location.longitude}")
        }
    }
}
```

**Resultado esperado no Logcat:**
```
🌟🌟🌟 CONECTADO AO SERVIDOR! 🌟🌟🌟
👤 ENVIANDO IDENTIFICAÇÃO DO USUÁRIO
🎉 CONFIRMAÇÃO: ENTROU NA SALA!
🎯 LOCALIZAÇÃO RECEBIDA DO PRESTADOR!
   📍 Latitude: -23.5482478
   📍 Longitude: -46.8470907
```

---

## ✅ CHECKLIST FINAL

- [ ] Socket conectou? (`🌟 CONECTADO`)
- [ ] Usuário identificado? (`👤 ENVIANDO IDENTIFICAÇÃO`)
- [ ] Entrou na sala? (`🎉 ENTROU NA SALA`)
- [ ] Recebe localização? (`🎯 LOCALIZAÇÃO RECEBIDA`)

**SE TODOS MARCADOS = FUNCIONANDO!** 🎉

