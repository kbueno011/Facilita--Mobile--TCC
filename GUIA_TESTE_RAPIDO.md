# 🧪 GUIA RÁPIDO DE TESTE - Fluxo Uber/99

## ⚡ Teste em 3 Passos

### **1. Sincronize o Projeto**
```bash
# No terminal do Android Studio:
./gradlew build

# Ou clique em: File > Sync Project with Gradle Files
```

### **2. Configure o WebSocket**
Abra: `app/src/main/java/com/exemple/facilita/service/WebSocketManager.kt`

Linha 19, altere:
```kotlin
private const val SOCKET_URL = "ws://localhost:3030"
```

Para:
```kotlin
private const val SOCKET_URL = "wss://servidor-facilita.onrender.com"
```

### **3. Execute o App**
1. Compile e instale no emulador/dispositivo
2. Faça login como **contratante**
3. Navegue: **Home → Buscar (navbar) → Selecione categoria**
4. Crie um serviço e pague
5. **Aguarde** → polling iniciará automaticamente

---

## 🔍 Como Verificar se Está Funcionando

### **Polling (10 em 10 segundos)**
Abra o **Logcat** e filtre por:
```
TelaAguardo
ServicoViewModel
```

Você verá:
```
✅ Monitoramento iniciado para serviço #123
🔄 Buscando serviço ID: 123
✅ Serviço atualizado: Status=AGUARDANDO
```

A cada 10 segundos, nova requisição é feita!

### **Mudança de Status**
Simule na API usando Postman/Insomnia:

**1. Prestador aceita:**
```
PUT /v1/facilita/servico/123/aceitar
```

Logs mostrarão:
```
✅ Prestador aceitou o serviço!
```

**2. Prestador inicia:**
```
PUT /v1/facilita/servico/123/iniciar
```

Logs mostrarão:
```
🚀 Serviço iniciado! Navegando para corrida em andamento...
```

### **WebSocket Tempo Real**
Quando o serviço estiver `EM_ANDAMENTO`:

Logcat mostrará:
```
🔌 Conectando ao WebSocket...
✅ WebSocket conectado!
👤 Autenticação enviada: Contratante
🚪 Entrando na sala do serviço #123
📍 Localização atualizada: -23.55, -46.63
```

---

## 🎯 Teste Completo Passo a Passo

### **Cenário: Solicitar Transporte para Farmácia**

1. **App aberto** → Login como contratante
2. **Navbar** → Clique em "Buscar" (ícone de lupa)
3. **TelaBuscar** → Clique em card "Farmácia"
4. **TelaCriarServico** → Preencha descrição e local
5. **TelaPagamento** → Realize pagamento
6. **TelaAguardo** aparece automaticamente ⏱️
   - Veja "Procurando prestador..."
   - Polling iniciado (veja Logcat)

7. **Simule prestador aceitando** (use Postman):
   ```json
   PUT /v1/facilita/servico/123/aceitar
   Authorization: Bearer {token_prestador}
   ```

8. **App atualiza** (até 10 seg depois):
   - "Prestador encontrado!"
   - Mostra card do prestador

9. **Simule prestador iniciando**:
   ```json
   PUT /v1/facilita/servico/123/iniciar
   Authorization: Bearer {token_prestador}
   ```

10. **App navega automaticamente** para:
    - **TelaCorridaEmAndamento** 🚗
    - Mapa aparece em tela cheia
    - WebSocket conecta
    - Marcador verde (prestador)
    - Marcador vermelho (destino)

11. **Simule localização** (Postman/Node):
    ```javascript
    socket.emit("update_location", {
      servicoId: 123,
      latitude: -23.55052,
      longitude: -46.633308,
      userId: 2
    });
    ```

12. **App atualiza mapa** instantaneamente! 📍

13. **Simule conclusão**:
    ```json
    PUT /v1/facilita/servico/123/concluir
    ```

14. **App navega** para home/avaliação

---

## 🐛 Troubleshooting

### **Problema: Polling não inicia**
**Solução:** Verifique se o token está válido:
```kotlin
val token = TokenManager.obterToken(context)
Log.d("DEBUG", "Token: $token")
```

### **Problema: WebSocket não conecta**
**Soluções:**
1. Verifique a URL em `WebSocketManager.kt`
2. Certifique-se que servidor WebSocket está rodando
3. Use `ws://` para HTTP ou `wss://` para HTTPS
4. Libere permissão de internet no AndroidManifest

### **Problema: Mapa não atualiza**
**Solução:** Verifique se:
1. Google Maps API Key está configurada
2. Localização do prestador está sendo enviada
3. Logcat mostra: `📍 Localização atualizada`

### **Problema: App não navega automaticamente**
**Solução:** Verifique se status está mudando:
```kotlin
Log.d("DEBUG", "Status atual: ${servico?.status}")
```

---

## 📊 Checklist de Teste

- [ ] App compila sem erros
- [ ] Login funciona
- [ ] TelaBuscar tem navbar
- [ ] Clicar em categoria navega para criar serviço
- [ ] Pagamento funciona
- [ ] TelaAguardo aparece após pagamento
- [ ] Polling inicia (veja Logcat a cada 10 seg)
- [ ] Status muda para ACEITO → mostra prestador
- [ ] Status muda para EM_ANDAMENTO → navega para corrida
- [ ] Mapa aparece em tela cheia
- [ ] WebSocket conecta (veja Logcat)
- [ ] Marcadores aparecem no mapa
- [ ] Localização atualiza em tempo real
- [ ] Card de informações mostra dados corretos
- [ ] Status CONCLUIDO → navega para home

---

## 🎨 O que Você Verá

### **TelaAguardo**
```
┌─────────────────────────┐
│  ←  Serviço #123    ℹ️  │
├─────────────────────────┤
│                         │
│     [Animação          │
│      Loading           │
│      Circular]         │
│                         │
│  Procurando prestador...│
│  Isso pode levar        │
│  alguns segundos        │
│                         │
│  Categoria: Transporte  │
│                         │
│ ┌───────────────────┐   │
│ │ [Cancelar Serviço]│   │
│ └───────────────────┘   │
└─────────────────────────┘
```

### **TelaCorridaEmAndamento**
```
┌─────────────────────────┐
│ ┌─ Pedido #12345678 ─┐ │
│ │ 🟢 Em andamento   🚗│ │
│ └─────────────────────┘ │
│                         │
│    [MAPA GOOGLE MAPS]   │
│    📍 Prestador (verde) │
│    📍 Destino (vermelho)│
│                         │
│ ┌───────────────────┐   │
│ │ ─────────────     │   │
│ │                   │   │
│ │ Tempo estimado    │   │
│ │    15 min      🚗 │   │
│ │                   │   │
│ │ [Expandir p/ ver  │   │
│ │  detalhes]        │   │
│ └───────────────────┘   │
└─────────────────────────┘
```

---

## 🚀 Pronto para Testar!

**Comandos úteis:**
```bash
# Ver logs em tempo real
adb logcat | grep -E "TelaAguardo|ServicoViewModel|WebSocket"

# Limpar e recompilar
./gradlew clean build

# Instalar no dispositivo
./gradlew installDebug
```

**Boa sorte! 🎉**

