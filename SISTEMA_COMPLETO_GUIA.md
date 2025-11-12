# 📱 Sistema Completo de Serviços Facilita

## 🎯 Visão Geral

Sistema completo de solicitação, pagamento, monitoramento e rastreamento de serviços em tempo real, integrado com a API Facilita e sistema de carteira digital com PagBank.

---

## 🚀 Funcionalidades Implementadas

### 1. ✅ Sistema de Carteira Digital
- **Persistência Local**: Saldo e transações salvos localmente (não some ao sair)
- **Integração PagBank Sandbox**: Sistema de pagamento real em ambiente de testes
- **Métodos de Depósito**:
  - PIX (com QR Code)
  - Cartão de Crédito
  - Depósito Simulado (para testes)
- **Débito Automático**: Pagamento de serviços debitado da carteira
- **Histórico de Transações**: Todas as operações registradas
- **Saldo em Tempo Real**: Atualizado automaticamente

### 2. 💰 Tela de Pagamento de Serviço
- **Design Futurista**: Interface moderna e inovadora
- **Verificação de Saldo**: Valida saldo antes do pagamento
- **Débito Automático**: Desconta direto da carteira
- **Navegação Inteligente**: Redireciona para recarga se saldo insuficiente
- **Confirmação Visual**: Animação de sucesso ao pagar
- **Detalhes Completos**: Mostra origem, destino e valor

### 3. ⏳ Tela de Aguardo de Serviço
- **Monitoramento em Tempo Real**: Polling a cada 5 segundos na API
- **Status Dinâmico**: 
  - `AGUARDANDO`: Procurando prestador
  - `ACEITO`: Prestador encontrado
  - `EM_ANDAMENTO`: Inicia rastreamento
  - `CONCLUIDO`/`CANCELADO`: Volta para home
- **Animação Futurista**: Círculos pulsantes e ondas expandindo
- **Informações do Prestador**: Nome, avaliação, categoria
- **Tempo Estimado**: Cálculo automático de chegada
- **Botões de Ação**: Ligar, mensagem, cancelar

### 4. 🗺️ Tela de Rastreamento em Tempo Real
- **Google Maps Integrado**: Mapa real com posições
- **Marcadores Dinâmicos**:
  - Prestador (atualiza posição em tempo real)
  - Destino (fixo)
- **Câmera Inteligente**: Segue o prestador automaticamente
- **Card de Informações**: Prestador, veículo, tempo estimado
- **Atualização Contínua**: Polling automático na API
- **Transição Automática**: Navega quando serviço concluído

### 5. 🔔 Sistema de Notificações (Estrutura Pronta)
- **Modelos Criados**: `NotificacaoModels.kt`
- **Tipos de Notificação**:
  - Serviço aceito
  - Prestador a caminho
  - Serviço concluído
  - Pagamento confirmado
  - Atualizações de status
- **Integração com API**: Estrutura pronta para receber notificações push

---

## 🏗️ Arquitetura do Sistema

### ViewModels
- **CarteiraViewModel**: Gerencia carteira, transações e integração PagBank
- **ServicoViewModel**: Gerencia serviços, polling e status em tempo real

### Services (API)
- **ServicoApiService**: Interface Retrofit para comunicação com API
  - `meusServicos()`: Lista serviços do usuário
  - `obterServico()`: Detalhes de um serviço
  - `cancelarServico()`: Cancela serviço ativo

### Repositories
- **CarteiraLocalRepository**: Persistência local de saldo e transações
- **PagBankRepository**: Integração com API PagBank (PIX e Cartão)

### Models
- **ServicoModels.kt**: Modelos completos da API
  - `Servico`, `PrestadorInfo`, `Categoria`, `Localizacao`
  - `StatusServicoApi` enum
  - `MeusServicosResponse`, `ServicoResponse`

---

## 📊 Fluxo Completo do Usuário

### 1️⃣ Criação de Serviço
```
TelaCriarServicoCategoria
    ↓
Preenche detalhes (origem, destino, descrição)
    ↓
Cria serviço via API POST /servico/from-categoria/{id}
    ↓
Recebe ID do serviço criado
```

### 2️⃣ Pagamento
```
TelaPagamentoServico
    ↓
Verifica saldo na carteira
    ↓
SE saldo suficiente:
    - Debita valor da carteira
    - Cria transação de débito
    - Confirma pagamento
SE saldo insuficiente:
    - Mostra dialog
    - Redireciona para recarga
    ↓
Navega para TelaAguardoServico
```

### 3️⃣ Aguardo de Prestador
```
TelaAguardoServico
    ↓
Inicia polling (GET /servico/meus-servicos a cada 5s)
    ↓
STATUS: AGUARDANDO
    - Animação de busca
    - "Procurando prestador..."
    ↓
STATUS: ACEITO
    - Mostra dados do prestador
    - "Prestador encontrado!"
    - Continua aguardando...
    ↓
STATUS: EM_ANDAMENTO
    - Prestador iniciou o serviço
    - Navega para TelaRastreamentoServico
```

### 4️⃣ Rastreamento em Tempo Real
```
TelaRastreamentoServico
    ↓
Continua polling (GET /servico/meus-servicos)
    ↓
Atualiza mapa com:
    - Posição do prestador (lat/lng da API)
    - Destino do serviço
    - Câmera segue prestador
    ↓
Mostra informações:
    - Tempo estimado de chegada
    - Dados do prestador e veículo
    - Status atual
    ↓
STATUS: CONCLUIDO
    - Para polling
    - Mostra mensagem de sucesso
    - Volta para home
```

---

## 🔗 Integração com API Facilita

### Base URL
```
https://servidor-facilita.onrender.com/v1/facilita/
```

### Endpoints Utilizados

#### 1. Criar Serviço por Categoria
```http
POST /servico/from-categoria/{id_categoria}
Authorization: Bearer {token}
Content-Type: application/json

{
  "descricao_personalizada": "string",
  "valor_adicional": 0.0,
  "origem_lat": -23.550520,
  "origem_lng": -46.633308,
  "origem_endereco": "string",
  "destino_lat": -23.561414,
  "destino_lng": -46.656139,
  "destino_endereco": "string",
  "paradas": []
}

Response:
{
  "status_code": 200,
  "message": "string",
  "data": {
    "servico": {
      "id": 34,
      "status": "AGUARDANDO",
      "valor": "20.00",
      ...
    }
  }
}
```

#### 2. Buscar Meus Serviços (Polling)
```http
GET /servico/meus-servicos
Authorization: Bearer {token}

Response:
{
  "status_code": 200,
  "data": [
    {
      "id": 34,
      "status": "EM_ANDAMENTO",
      "prestador": {
        "nome": "João Silva",
        "avaliacao": 4.8,
        "latitude_atual": -23.550520,
        "longitude_atual": -46.633308,
        "veiculo": {
          "marca": "Honda",
          "modelo": "CG 160",
          "placa": "ABC1234"
        }
      },
      "categoria": {
        "nome": "Transporte"
      },
      "localizacao": {
        "latitude": -23.561414,
        "longitude": -46.656139
      }
    }
  ]
}
```

#### 3. Cancelar Serviço
```http
PUT /servico/{id}/cancelar
Authorization: Bearer {token}

Response:
{
  "status_code": 200,
  "data": {
    "id": 34,
    "status": "CANCELADO"
  }
}
```

---

## 💾 Sistema de Persistência Local

### CarteiraLocalRepository
Utiliza `SharedPreferences` para salvar:

```kotlin
// Estrutura de dados salvos
{
  "saldo_disponivel": 150.00,
  "saldo_bloqueado": 0.00,
  "saldo_total": 150.00,
  "transacoes": [
    {
      "id": "DEP_PIX_1234567890",
      "tipo": "DEPOSITO",
      "valor": 100.00,
      "status": "CONCLUIDO",
      "metodo": "PIX",
      "data": "15/01/2025 14:30"
    },
    {
      "id": "DEB_SERVICO_34",
      "tipo": "DEBITO",
      "valor": 25.00,
      "status": "CONCLUIDO",
      "descricao": "Pagamento do serviço #34",
      "data": "15/01/2025 15:00"
    }
  ]
}
```

### Operações Disponíveis
- `obterSaldo()`: Retorna saldo atual
- `adicionarSaldo(valor)`: Adiciona ao saldo
- `debitarSaldo(valor)`: Debita do saldo (valida se tem suficiente)
- `salvarTransacao()`: Registra transação
- `obterTransacoes()`: Lista todas as transações
- `limparDados()`: Reset completo

---

## 🎨 Design System

### Cores Principais
```kotlin
Primary Green: Color(0xFF00B14F)
Dark Green: Color(0xFF3C604B)
Background: Color(0xFFF5F5F7)
Text Primary: Color(0xFF2D2D2D)
Text Secondary: Color(0xFF6D6D6D)
Error Red: Color(0xFFFF6B6B)
Warning Yellow: Color(0xFFFFA726)
```

### Componentes Customizados
- **AnimacaoLoadingFuturista**: Círculos pulsantes com ondas
- **CardPrestador**: Card com informações do prestador
- **CardPercurso**: Mostra origem → destino
- **DialogoCancelamento**: Confirmação de cancelamento

---

## 🔧 Como Usar

### 1. Configurar Token de Autenticação
O token é gerenciado pelo `TokenManager`:
```kotlin
val token = TokenManager.obterToken(context)
```

### 2. Criar um Serviço
```kotlin
// Na TelaCriarServicoCategoria
navController.navigate(
    "tela_pagamento_servico/$servicoId/$valorServico/$origemEndereco/$destinoEndereco"
)
```

### 3. Processar Pagamento
```kotlin
// Na TelaPagamentoServico
viewModel.debitarParaServico(
    valorServico = 25.0,
    servicoId = "34",
    descricaoServico = "Pagamento do serviço #34",
    onSuccess = {
        // Navega para aguardo
        navController.navigate("tela_aguardo_servico/$servicoId/$origem/$destino")
    },
    onError = { erro ->
        // Mostra erro
    }
)
```

### 4. Monitorar Serviço
```kotlin
// Na TelaAguardoServico
viewModel.iniciarMonitoramento(token, servicoId)
// Polling automático a cada 5s
```

### 5. Rastrear em Tempo Real
```kotlin
// Na TelaRastreamentoServico
// Mapa atualiza automaticamente com posição do prestador
val prestadorPos = LatLng(
    servico?.prestador?.latitudeAtual ?: 0.0,
    servico?.prestador?.longitudeAtual ?: 0.0
)
```

---

## 📱 Navegação Entre Telas

```
TelaHome
    ↓
TelaCriarServicoCategoria (escolhe categoria)
    ↓
TelaPagamentoServico (paga com carteira)
    ↓
TelaAguardoServico (aguarda prestador aceitar e iniciar)
    ↓
TelaRastreamentoServico (rastreamento em tempo real)
    ↓
TelaHome (concluído/cancelado)
```

### Rotas de Navegação
```kotlin
// Pagamento
"tela_pagamento_servico/{servicoId}/{valorServico}/{origem}/{destino}"

// Aguardo
"tela_aguardo_servico/{servicoId}/{origem}/{destino}"

// Rastreamento
"tela_rastreamento_servico/{servicoId}"
```

---

## 🧪 Como Testar

### Teste 1: Fluxo Completo com Saldo
1. Abrir `TelaCarteira`
2. Fazer depósito simulado de R$ 100
3. Criar um novo serviço
4. Confirmar pagamento (verá débito na carteira)
5. Aguardar prestador aceitar
6. Ver rastreamento em tempo real

### Teste 2: Saldo Insuficiente
1. Criar serviço com saldo menor que valor
2. Tentar pagar
3. Ver dialog de saldo insuficiente
4. Clicar em "Adicionar Saldo"
5. Ser redirecionado para carteira

### Teste 3: Persistência da Carteira
1. Depositar R$ 50 na carteira
2. Fechar aplicativo completamente
3. Reabrir aplicativo
4. Verificar que saldo continua R$ 50
5. Ver histórico de transações preservado

### Teste 4: Monitoramento de Serviço
1. Criar e pagar serviço
2. Ver tela de aguardo
3. Observar animação de busca
4. Aguardar API retornar status `ACEITO`
5. Ver dados do prestador
6. Aguardar status mudar para `EM_ANDAMENTO`
7. Ser redirecionado automaticamente para rastreamento

---

## 🐛 Troubleshooting

### Problema: Saldo não persiste
**Solução**: Verificar se `CarteiraLocalRepository` está salvando corretamente
```kotlin
// No CarteiraViewModel
private val localRepository = CarteiraLocalRepository(application.applicationContext)
```

### Problema: Polling não funciona
**Solução**: Verificar se token está válido
```kotlin
val token = TokenManager.obterToken(context)
if (token.isNotEmpty()) {
    viewModel.iniciarMonitoramento(token, servicoId)
}
```

### Problema: Mapa não carrega
**Solução**: Verificar API Key do Google Maps em `AndroidManifest.xml`
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY"/>
```

### Problema: Navegação não funciona
**Solução**: Verificar rotas em `MainActivity.kt`
```kotlin
// Todas as rotas devem estar registradas no NavHost
```

---

## 📈 Melhorias Futuras Possíveis

### Sistema de Notificações Push
- Firebase Cloud Messaging
- Notificações quando prestador aceitar
- Notificações de chegada
- Notificações de conclusão

### Chat em Tempo Real
- WebSocket para mensagens
- Chat entre usuário e prestador
- Envio de localização

### Avaliação e Feedback
- Tela de avaliação pós-serviço
- Comentários e estrelas
- Sistema de gorjetas

### Histórico Completo
- Lista de todos os serviços
- Filtros por status e data
- Exportar comprovantes

---

## 🔐 Segurança

- ✅ Token JWT em todas as requisições
- ✅ Dados sensíveis salvos localmente
- ✅ Validações de saldo antes de débito
- ✅ Timeout em requisições (evita loops infinitos)
- ✅ Tratamento de erros completo

---

## 📚 Documentação da API

Para documentação completa da API Facilita, acesse:
**https://apifacilita.apidog.io/**

---

## ✨ Características Especiais

### Animações Fluidas
- Transições suaves entre telas
- Animações de loading futuristas
- Feedback visual em todas as ações

### Responsividade
- Design adaptável a diferentes tamanhos
- Componentes escaláveis
- Suporte a diferentes densidades

### Offline First (Carteira)
- Dados salvos localmente
- Funciona sem internet para consultas
- Sincronização quando conectado

### Performance
- Polling otimizado (5s de intervalo)
- Cancelamento automático de polling
- Recursos liberados corretamente

---

## 🎯 Status do Projeto

| Funcionalidade | Status |
|---|---|
| Carteira Digital | ✅ Completo |
| Pagamento de Serviços | ✅ Completo |
| Aguardo de Prestador | ✅ Completo |
| Rastreamento em Tempo Real | ✅ Completo |
| Integração API | ✅ Completo |
| Google Maps | ✅ Completo |
| Persistência Local | ✅ Completo |
| Sistema de Notificações | 🚧 Estrutura Pronta |

---

## 📞 Suporte

Para dúvidas ou problemas:
1. Verificar logs no Logcat (TAG: `ServicoViewModel`, `CarteiraViewModel`)
2. Consultar documentação da API
3. Verificar erros de compilação no arquivo

---

**Desenvolvido com ❤️ para o TCC Facilita**

