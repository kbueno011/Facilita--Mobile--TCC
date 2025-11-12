# 🚀 GUIA RÁPIDO - ATIVAR RASTREAMENTO EM TEMPO REAL

## ⚡ IMPLEMENTAÇÃO COMPLETA - 3 PASSOS

---

## PASSO 1: ADICIONAR API KEY DO GOOGLE MAPS 🗺️

### 1. Obter API Key
1. Acesse: https://console.cloud.google.com/
2. Crie um projeto ou selecione existente
3. Vá em "APIs e Serviços" > "Credenciais"
4. Clique em "+ CRIAR CREDENCIAIS" > "Chave de API"
5. Copie a chave gerada

### 2. Ativar APIs necessárias
No console do Google Cloud, ative:
- **Maps SDK for Android**
- **Places API**
- **Directions API**

### 3. Adicionar no AndroidManifest.xml
Abra: `app/src/main/AndroidManifest.xml`

Adicione dentro de `<application>`:
```xml
<application
    ...>
    
    <!-- ADICIONE ESTA LINHA -->
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="SUA_CHAVE_API_AQUI"/>
        
    ...
</application>
```

---

## PASSO 2: CONFIGURAR URL DA SUA API 🌐

### 1. Abrir ServicoViewModel.kt
Caminho: `app/src/main/java/com/exemple/facilita/viewmodel/ServicoViewModel.kt`

### 2. Alterar linha 21
```kotlin
// ANTES:
.baseUrl("https://api.facilita.com/api/")

// DEPOIS (use sua URL real):
.baseUrl("https://apifacilita.apidog.io/api/")
```

### 3. Verificar estrutura da API
Sua API deve retornar neste formato:
```json
{
  "success": true,
  "data": {
    "id": "123",
    "status": "a_caminho",
    "prestador": {
      "nome": "João",
      "avaliacao": 4.8,
      "latitude_atual": -23.555,
      "longitude_atual": -46.640,
      "veiculo": {
        "marca": "Toyota",
        "modelo": "Corolla",
        "placa": "ABC-1234"
      }
    }
  }
}
```

---

## PASSO 3: ATUALIZAR NAVEGAÇÃO 🧭

### 1. Abrir TelaPagamentoServico.kt
Caminho: `app/src/main/java/com/exemple/facilita/screens/TelaPagamentoServico.kt`

### 2. Procurar linha ~455 (onde navega para aguardo)
```kotlin
// ANTES:
navController.navigate("tela_aguardo_servico/$servicoId/$origemEndereco/$destinoEndereco")

// DEPOIS:
navController.navigate("tela_aguardo_servico/$servicoId")
```

### 3. Abrir arquivo de Navegação
Procure onde está definida a rota `tela_aguardo_servico`

### 4. Substituir por:
```kotlin
composable("tela_aguardo_servico/{servicoId}") {
    val servicoId = it.arguments?.getString("servicoId") ?: ""
    TelaAguardoServicoAtualizada(
        navController = navController,
        servicoId = servicoId
    )
}
```

---

## ✅ VERIFICAR SE ESTÁ FUNCIONANDO

### 1. Build do Projeto
```bash
./gradlew clean
./gradlew assembleDebug
```

### 2. Testar Fluxo
1. ✅ Criar um serviço
2. ✅ Pagar (débito da carteira)
3. ✅ Ir para tela de aguardo
4. ✅ Ver animação "Procurando..."
5. ✅ Logs no Logcat:
```
ServicoViewModel: ✅ Serviço atualizado: Status=AGUARDANDO
```

### 3. Testar Rastreamento
Quando prestador aceitar:
1. ✅ Mapa aparece automaticamente
2. ✅ 3 marcadores visíveis (verde, azul, vermelho)
3. ✅ Card do prestador com informações
4. ✅ Tempo estimado calculado

---

## 🎯 ESTRUTURA CRIADA

### Novos Arquivos:
```
app/src/main/java/com/exemple/facilita/
├── data/
│   ├── models/
│   │   └── ServicoModels.kt ✅
│   └── api/
│       └── ServicoApiService.kt ✅
├── viewmodel/
│   └── ServicoViewModel.kt ✅
└── screens/
    └── TelaAguardoServicoAtualizada.kt ✅
```

---

## 📊 COMO FUNCIONA

### Polling Automático (a cada 5s)
```
App ---GET /servicos/{id}---> API
    <---JSON com status---

Loop:
1. Busca dados do serviço
2. Atualiza UI
3. Se prestador tem lat/lon, atualiza mapa
4. Espera 5 segundos
5. Repete

Para quando:
- Status = CONCLUIDO
- Status = CANCELADO
- Usuário sai da tela
```

### Atualização do Mapa
```
API retorna:
{
  "prestador": {
    "latitude_atual": -23.555,
    "longitude_atual": -46.640
  }
}

↓

Mapa move câmera para nova posição
Marcador verde atualiza
Tempo estimado recalcula
```

---

## 🔧 ENDPOINTS NECESSÁRIOS NA SUA API

### 1. Obter Serviço (Polling usa este)
```
GET /api/servicos/{id}
Headers: Authorization: Bearer {token}

Response:
{
  "success": true,
  "data": {
    "id": "abc123",
    "status": "a_caminho",
    "prestador": {
      "nome": "João",
      "latitude_atual": -23.555,
      "longitude_atual": -46.640
    }
  }
}
```

### 2. Cancelar Serviço
```
PUT /api/servicos/{id}/cancelar
Headers: Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Serviço cancelado"
}
```

### 3. Status possíveis
- `aguardando` - Procurando prestador
- `aceito` - Prestador aceitou
- `a_caminho` - Prestador indo buscar
- `em_andamento` - Serviço iniciado
- `concluido` - Finalizado
- `cancelado` - Cancelado

---

## 🎨 VISUAL FINAL

### Tela: AGUARDANDO
```
╔════════════════════════════╗
║ 🟢 Pedido #abc123         ║
╠════════════════════════════╣
║                            ║
║      [Animação]            ║
║   Círculo girando          ║
║   "Procurando..."          ║
║                            ║
╠════════════════════════════╣
║ Status: Aguardando         ║
║                            ║
║ 📍 Origem → Destino        ║
║                            ║
║ [Cancelar Pedido]          ║
╚════════════════════════════╝
```

### Tela: PRESTADOR ACEITO
```
╔════════════════════════════╗
║ 🟢 Pedido #abc123         ║
╠════════════════════════════╣
║                            ║
║   ╔══ GOOGLE MAPS ══╗      ║
║   ║ 🟢 Prestador    ║      ║
║   ║ 🔵 Origem       ║      ║
║   ║ 🔴 Destino      ║      ║
║   ╚═════════════════╝      ║
║                            ║
╠════════════════════════════╣
║ A caminho • 8 min          ║
║                            ║
║ 👤 João Silva ⭐ 4.8      ║
║ Toyota Corolla ABC-1234    ║
║ [📞] [💬]                 ║
║                            ║
║ 📍 Origem → Destino        ║
║                            ║
║ [Cancelar Pedido]          ║
╚════════════════════════════╝
```

---

## 🐛 TROUBLESHOOTING

### Problema: "Null pointer exception"
**Causa:** API Key não configurada
**Solução:** Adicionar API Key no AndroidManifest.xml

### Problema: Mapa não carrega
**Causa:** API Key inválida ou APIs não ativadas
**Solução:** 
1. Verificar API Key
2. Ativar Maps SDK no console Google

### Problema: Polling não funciona
**Causa:** URL da API errada
**Solução:** Verificar baseUrl no ServicoViewModel

### Problema: Marcador não move
**Causa:** API não retorna latitude_atual/longitude_atual
**Solução:** Verificar estrutura JSON da resposta

---

## 📱 LOGS PARA DEBUG

Abra Logcat e filtre por `ServicoViewModel`:

```
✅ Sucesso:
ServicoViewModel: ✅ Serviço atualizado: Status=ACEITO
ServicoViewModel: 📍 Prestador em: -23.555, -46.640

❌ Erro:
ServicoViewModel: ❌ Erro na resposta: ...
ServicoViewModel: ❌ Exceção ao buscar serviço
```

---

## ⏭️ PRÓXIMOS PASSOS

Após implementar o rastreamento:

1. **Tela de Avaliação** - Quando serviço concluir
2. **Chat com Prestador** - Mensagens em tempo real
3. **Notificações Push** - Avisos de status
4. **Histórico de Viagens** - Lista de serviços anteriores

---

## 💡 DICA PRO

### Testar sem prestador real:
Crie um endpoint na sua API:
```
POST /api/servicos/{id}/simular
Body: {
  "latitude": -23.555,
  "longitude": -46.640
}
```

Use para simular o prestador se movendo e testar o mapa!

---

**Implementação:** ✅ COMPLETA  
**Tempo estimado:** 10 minutos  
**Dificuldade:** ⭐⭐ (Fácil)

🎉 **Seu sistema de rastreamento está pronto!**

