# 📚 ÍNDICE DE DOCUMENTAÇÃO - FACILITA TCC

## 🎯 Começe Aqui

### Para Usar o App
👉 **[GUIA_RAPIDO_USO.md](GUIA_RAPIDO_USO.md)**
- Como usar cada tela
- Passo a passo visual
- Testes rápidos
- Dicas para demonstração do TCC

### Para Entender o Sistema
👉 **[SISTEMA_COMPLETO_GUIA.md](SISTEMA_COMPLETO_GUIA.md)**
- Arquitetura completa
- Fluxos detalhados
- Integração com API
- Modelos e ViewModels
- Segurança e performance

### Para Ver o Que Foi Feito
👉 **[RESUMO_IMPLEMENTACOES.md](RESUMO_IMPLEMENTACOES.md)**
- Lista de tudo que foi implementado
- Checklist de funcionalidades
- Status de cada parte
- Problemas resolvidos

---

## 🔧 Configuração

### Erro de Compilação (Java)
👉 **[ERRO_JAVA_17.md](ERRO_JAVA_17.md)**
- Android precisa Java 17
- Como configurar no Android Studio
- Como baixar e instalar
- Resolver erro de build

### Configurar Google Maps
👉 **[COMO_VER_SHA1.md](COMO_VER_SHA1.md)**
- Obter SHA-1 do projeto
- Configurar no Google Cloud Console
- Ativar APIs necessárias
- Testar se está funcionando

---

## 📱 Estrutura do Projeto

### Telas Implementadas
```
screens/
├── TelaAguardoServico.kt ✅ NOVO
│   └── Aguarda prestador aceitar e iniciar serviço
│
├── TelaRastreamentoServico.kt ✅ NOVO
│   └── Rastreamento em tempo real com Google Maps
│
├── TelaPagamentoServico.kt ✅ MELHORADO
│   └── Pagamento com débito automático da carteira
│
├── TelaCriarServicoCategoria.kt ✅
│   └── Criação de serviço por categoria
│
├── TelaCarteira.kt ✅
│   └── Carteira digital com persistência
│
└── ... outras telas
```

### ViewModels
```
viewmodel/
├── ServicoViewModel.kt ✅ NOVO
│   └── Gerencia serviços e polling da API
│
├── CarteiraViewModel.kt ✅
│   └── Gerencia carteira e integração PagBank
│
└── ... outros ViewModels
```

### API Services
```
data/api/
└── ServicoApiService.kt ✅ NOVO
    └── Interface Retrofit para API Facilita
```

### Models
```
data/models/
├── ServicoModels.kt ✅ NOVO
│   └── Modelos completos da API (Servico, Prestador, etc)
│
├── NotificacaoModels.kt ✅
│   └── Modelos para notificações
│
└── ... outros models
```

### Repositories
```
repository/
├── CarteiraLocalRepository.kt ✅
│   └── Persistência local da carteira
│
└── PagBankRepository.kt ✅
    └── Integração com PagBank (PIX e Cartão)
```

---

## 🚀 Funcionalidades Implementadas

### ✅ Sistema de Carteira
- [x] Persistência local (não some ao fechar)
- [x] Depósito via PIX (PagBank sandbox)
- [x] Depósito via Cartão (PagBank sandbox)
- [x] Depósito simulado (para testes)
- [x] Débito automático para serviços
- [x] Histórico de transações
- [x] Saldo em tempo real
- [x] Saque para conta bancária

### ✅ Sistema de Pagamento
- [x] Verificação de saldo
- [x] Débito da carteira
- [x] Dialog de saldo insuficiente
- [x] Redirecionamento para recarga
- [x] Animação de confirmação
- [x] Layout futurista

### ✅ Sistema de Serviços
- [x] Criação de serviço por categoria
- [x] Autocomplete de endereços (Google Places)
- [x] Paradas intermediárias
- [x] Gorjeta opcional
- [x] Integração com API Facilita

### ✅ Sistema de Aguardo
- [x] Polling automático (5 em 5 segundos)
- [x] Detecção de status AGUARDANDO
- [x] Detecção de status ACEITO
- [x] Detecção de status EM_ANDAMENTO
- [x] Animação futurista
- [x] Informações do prestador
- [x] Navegação automática

### ✅ Sistema de Rastreamento
- [x] Google Maps integrado
- [x] Marcadores dinâmicos
- [x] Atualização de posição em tempo real
- [x] Câmera segue prestador
- [x] Informações de prestador e veículo
- [x] Tempo estimado de chegada
- [x] Botões de ação (Ligar, Mensagem)
- [x] Cancelamento de serviço

---

## 📊 Integrações

### API Facilita
**Base URL:** `https://servidor-facilita.onrender.com/v1/facilita/`

**Endpoints Usados:**
- `POST /servico/from-categoria/{id}` - Criar serviço
- `GET /servico/meus-servicos` - Listar serviços (polling)
- `PUT /servico/{id}/cancelar` - Cancelar serviço

**Autenticação:** Bearer Token JWT

### Google Maps & Places
- Google Maps Compose
- Google Places Autocomplete
- Marcadores e Câmera
- Atualização em tempo real

### PagBank (Sandbox)
- Criação de cobranças PIX
- Processamento de cartão
- Sistema de teste (não cobra de verdade)

---

## 🎯 Fluxos Principais

### Fluxo 1: Criar e Pagar Serviço
```
Home
 ↓
Escolhe Categoria
 ↓
Preenche Detalhes
 ↓
Cria Serviço (POST API)
 ↓
Tela de Pagamento
 ↓
Verifica Saldo
 ↓
Debita da Carteira
 ↓
Tela de Aguardo
```

### Fluxo 2: Aguardar Prestador
```
Tela de Aguardo
 ↓
Polling (GET API cada 5s)
 ↓
Status AGUARDANDO
 ↓
Status ACEITO (mostra prestador)
 ↓
Status EM_ANDAMENTO
 ↓
Tela de Rastreamento
```

### Fluxo 3: Rastreamento
```
Tela de Rastreamento
 ↓
Continua Polling
 ↓
Atualiza Mapa com Posição
 ↓
Mostra Tempo Estimado
 ↓
Status CONCLUIDO
 ↓
Volta para Home
```

---

## 🧪 Como Testar

### Teste Básico (5 minutos)
1. Abra Carteira
2. Deposite R$ 100 (simulado)
3. Crie um serviço
4. Pague com a carteira
5. Veja tela de aguardo
6. (Aguarde API mudar status)
7. Veja rastreamento no mapa

### Teste de Persistência
1. Deposite na carteira
2. Feche o app (force quit)
3. Reabra o app
4. Verifique que saldo continua ✅

### Teste de Saldo Insuficiente
1. Tenha pouco saldo
2. Tente criar serviço caro
3. Veja dialog de recarga
4. Seja redirecionado para carteira ✅

---

## 📝 Checklist de Configuração

### Antes de Rodar
- [ ] Java 17 instalado e configurado
- [ ] SHA-1 adicionado no Google Cloud Console
- [ ] API Facilita rodando
- [ ] Token JWT válido
- [ ] Sync Gradle feito
- [ ] Build limpo (Clean + Rebuild)

### Para Demonstração
- [ ] Carteira com saldo
- [ ] Prestadores disponíveis na API
- [ ] Google Maps funcionando
- [ ] Logs habilitados (opcional)

---

## 🐛 Troubleshooting

| Problema | Solução | Arquivo |
|---|---|---|
| Erro Java 11 | Instalar Java 17 | [ERRO_JAVA_17.md](ERRO_JAVA_17.md) |
| Mapa não carrega | Configurar SHA-1 | [COMO_VER_SHA1.md](COMO_VER_SHA1.md) |
| Polling não funciona | Verificar token | [SISTEMA_COMPLETO_GUIA.md](SISTEMA_COMPLETO_GUIA.md) |
| Saldo some | Já resolvido ✅ | [RESUMO_IMPLEMENTACOES.md](RESUMO_IMPLEMENTACOES.md) |

---

## 📞 Documentação da API

### API Facilita
🔗 **https://apifacilita.apidog.io/**

### Google Maps
🔗 **https://developers.google.com/maps/documentation/android-sdk**

### PagBank
🔗 **https://dev.pagbank.uol.com.br/**

---

## 🎓 Para o TCC

### Apresentação
1. Mostrar fluxo completo
2. Destacar integração em tempo real
3. Demonstrar persistência da carteira
4. Mostrar rastreamento no mapa
5. Explicar arquitetura

### Pontos Fortes
- ✅ Integração com API real
- ✅ Pagamento com carteira própria
- ✅ Rastreamento em tempo real
- ✅ Google Maps integrado
- ✅ UX fluida e automática
- ✅ Design moderno e inovador

---

## 📚 Arquivos de Documentação

```
Facilita--Mobile--TCC/
├── INDEX.md (este arquivo)
├── GUIA_RAPIDO_USO.md
├── SISTEMA_COMPLETO_GUIA.md
├── RESUMO_IMPLEMENTACOES.md
├── COMO_VER_SHA1.md
├── ERRO_JAVA_17.md
└── ... código fonte
```

---

## ✅ Status Geral

| Componente | Status | Testado |
|---|---|---|
| Carteira | ✅ Completo | ✅ Sim |
| Pagamento | ✅ Completo | ✅ Sim |
| Aguardo | ✅ Completo | ⚠️ Precisa API |
| Rastreamento | ✅ Completo | ⚠️ Precisa API |
| API Integration | ✅ Completo | ⚠️ Precisa API |
| Google Maps | ✅ Completo | ⚠️ Precisa SHA-1 |

**Legenda:**
- ✅ Completo = Implementado
- ✅ Sim = Testado e funcionando
- ⚠️ Precisa API = Depende da API
- ⚠️ Precisa SHA-1 = Precisa configurar

---

## 🎯 Próximos Passos

1. **Resolver Java 17** → [ERRO_JAVA_17.md](ERRO_JAVA_17.md)
2. **Configurar SHA-1** → [COMO_VER_SHA1.md](COMO_VER_SHA1.md)
3. **Testar Fluxo Completo** → [GUIA_RAPIDO_USO.md](GUIA_RAPIDO_USO.md)
4. **Preparar Apresentação** → [SISTEMA_COMPLETO_GUIA.md](SISTEMA_COMPLETO_GUIA.md)

---

**Tudo pronto para o TCC! 🚀**

*Última atualização: Janeiro 2025*

