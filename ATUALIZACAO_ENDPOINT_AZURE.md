po # ✅ Atualização de Endpoint Concluída

## 📝 Resumo
O endpoint da API foi atualizado de **Render** para **Azure**.

---

## 🔄 Mudanças Aplicadas

### Endpoint Antigo (Render):
```
https://servidor-facilita.onrender.com
```

### Novo Endpoint (Azure):
```
https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net
```

---

## 📂 Arquivos Atualizados

### 1. **ServicoViewModel.kt**
**Local:** `app/src/main/java/com/exemple/facilita/viewmodel/ServicoViewModel.kt`

**Mudança:**
```kotlin
// ANTES
.baseUrl("https://servidor-facilita.onrender.com/v1/facilita/")

// DEPOIS
.baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/v1/facilita/")
```

---

### 2. **NotificacaoViewModel.kt**
**Local:** `app/src/main/java/com/exemple/facilita/viewmodel/NotificacaoViewModel.kt`

**Mudança:**
```kotlin
// ANTES
.baseUrl("https://servidor-facilita.onrender.com/v1/facilita/")

// DEPOIS
.baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/v1/facilita/")
```

---

### 3. **RetrofitFactory.kt**
**Local:** `app/src/main/java/com/exemple/facilita/service/RetrofitFactory.kt`

**Mudança:**
```kotlin
// ANTES
.baseUrl("https://servidor-facilita.onrender.com/")

// DEPOIS
.baseUrl("https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/")
```

---

### 4. **WebSocketManager.kt**
**Local:** `app/src/main/java/com/exemple/facilita/network/WebSocketManager.kt`

**Mudança:**
```kotlin
// ANTES
private const val SERVER_URL = "https://servidor-facilita.onrender.com"

// DEPOIS
private const val SERVER_URL = "https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net"
```

---

## ✅ Verificações Realizadas

- ✅ Todos os arquivos Kotlin atualizados
- ✅ Sem erros de compilação
- ✅ WebSocket configurado para Azure
- ✅ Retrofit configurado para Azure
- ✅ ViewModels atualizados

---

## 🚀 Próximos Passos

1. **Rebuild do projeto:**
   ```bash
   ./gradlew clean build
   ```

2. **Testar a conexão:**
   - Abra o app
   - Verifique se as chamadas de API funcionam
   - Teste notificações e WebSocket
   - Verifique os logs para confirmar conexão com Azure

3. **Monitorar logs:**
   - Procure por mensagens de conexão bem-sucedida
   - Verifique se não há erros 404 ou timeouts
   - Confirme que o WebSocket conecta corretamente

---

## 📌 Observações Importantes

- O endpoint Azure usa HTTPS (SSL ativo)
- WebSocket deve usar WSS (WebSocket Secure)
- Todas as rotas `/v1/facilita/` foram preservadas
- A estrutura da API permanece a mesma

---

## 🔧 Em Caso de Problemas

Se houver erros de conexão:

1. Verifique se o backend Azure está online
2. Confirme que as rotas da API não mudaram
3. Verifique certificados SSL
4. Teste manualmente com ferramentas como Postman:
   ```
   GET https://facilita-c6hhb9csgygudrdz.canadacentral-01.azurewebsites.net/v1/facilita/
   ```

---

**Data da Atualização:** 2025-11-19
**Status:** ✅ Concluído com Sucesso

