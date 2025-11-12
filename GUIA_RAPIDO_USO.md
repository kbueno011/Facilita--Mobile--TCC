# 🚀 GUIA RÁPIDO DE USO - FACILITA

## ⚡ Quick Start (3 Passos)

### 1️⃣ Adicionar Saldo na Carteira
```
Home → Menu → Carteira
    ↓
Botão "Depositar"
    ↓
Escolha "Depósito Simulado" (para testes)
    ↓
Digite: R$ 100,00
    ↓
Confirmar
    ↓
✅ Saldo atualizado!
```

### 2️⃣ Criar um Serviço
```
Home → Card de Categoria (ex: Farmácia)
    ↓
Preencha:
- "O que você precisa?": Comprar remédio
- Origem: Rua ABC, 123
- Destino: Av XYZ, 456
    ↓
Botão "Criar Serviço"
    ↓
✅ Vai para pagamento
```

### 3️⃣ Pagar e Acompanhar
```
Tela de Pagamento
    ↓
Veja: Saldo disponível vs Valor do serviço
    ↓
Botão "Confirmar Pagamento"
    ↓
✅ Débito automático da carteira
    ↓
Tela de Aguardo (procurando prestador)
    ↓
⏳ Aguarde API retornar ACEITO
    ↓
✅ Prestador encontrado!
    ↓
⏳ Aguarde API retornar EM_ANDAMENTO
    ↓
🗺️ Rastreamento em Tempo Real (mapa)
    ↓
✅ Serviço Concluído!
```

---

## 🎯 Telas Principais

### 🏠 TelaHome
**O que fazer:**
- Ver categorias disponíveis
- Clicar em uma categoria para criar serviço
- Acessar menu (Carteira, Perfil, Notificações)

### 💰 TelaCarteira
**O que fazer:**
- Ver saldo atual
- Ver histórico de transações
- Depositar:
  - PIX (gera QR Code)
  - Cartão de Crédito
  - Simulado (para testes)
- Sacar para conta bancária

**Importante:** Saldo não some ao fechar app! ✅

### 🛒 TelaCriarServicoCategoria
**O que fazer:**
- Escolher categoria (já escolhida ao clicar no card)
- Descrever o que precisa
- Informar endereço de origem
- Informar endereço de destino
- (Opcional) Adicionar paradas intermediárias
- (Opcional) Adicionar gorjeta
- Criar serviço

**Autocomplete:** Digite endereço e selecione das sugestões

### 💳 TelaPagamentoServico
**O que fazer:**
- Revisar detalhes do serviço
- Ver saldo disponível
- Conferir valor total
- Confirmar pagamento

**Se não tiver saldo:**
- Aparece dialog
- Clique em "Adicionar Saldo"
- Vai para carteira
- Deposite
- Volte e pague

### ⏳ TelaAguardoServico
**O que acontece:**
- Sistema busca prestador disponível
- Faz polling na API (verifica status a cada 5s)
- Mostra animação de busca
- Quando prestador aceita:
  - Animação muda
  - Mostra dados do prestador
  - Continua aguardando ele iniciar
- Quando prestador inicia:
  - Navega automático para rastreamento

**Você pode:**
- Ver status atual
- Ver tempo estimado
- Cancelar serviço (se necessário)

### 🗺️ TelaRastreamentoServico
**O que ver:**
- **Mapa com 2 marcadores:**
  - 🟢 Verde = Prestador (posição atualiza em tempo real)
  - 🔴 Vermelho = Destino (seu destino)
- **Card inferior:**
  - Foto/Nome do prestador
  - Avaliação (estrelas)
  - Veículo (marca, modelo, placa)
  - Tempo estimado de chegada

**Você pode:**
- Ligar para o prestador
- Enviar mensagem
- Cancelar serviço
- Acompanhar no mapa

---

## 🎨 Funcionalidades Especiais

### 🔄 Persistência de Dados
**Carteira:**
- Saldo salvo localmente
- Transações salvas localmente
- Não some ao fechar app
- Sincroniza quando necessário

### ⚡ Tempo Real
**Serviços:**
- Polling automático (5 em 5 segundos)
- Detecta mudanças de status
- Atualiza posição do prestador
- Navega automaticamente entre telas

### 🎯 Validações
**Pagamento:**
- Verifica saldo antes de pagar
- Impede pagamento se saldo insuficiente
- Mostra quanto falta
- Redireciona para recarga

### 🔔 Notificações (Estrutura Pronta)
- Sistema preparado para notificações push
- Modelos criados
- Falta apenas integrar Firebase

---

## 🧪 Como Testar Cada Parte

### Teste 1: Persistência da Carteira
```
1. Abra Carteira
2. Deposite R$ 50 (simulado)
3. Feche o app (force quit)
4. Abra o app novamente
5. Vá para Carteira
6. ✅ Saldo deve estar R$ 50
```

### Teste 2: Pagamento com Saldo
```
1. Tenha R$ 100 na carteira
2. Crie serviço de R$ 25
3. Confirme pagamento
4. ✅ Saldo deve ficar R$ 75
5. ✅ Ver transação no histórico
```

### Teste 3: Pagamento Sem Saldo
```
1. Tenha R$ 10 na carteira
2. Crie serviço de R$ 25
3. Tente pagar
4. ✅ Dialog "Saldo Insuficiente"
5. ✅ Mostra quanto falta (R$ 15)
6. Clique "Adicionar Saldo"
7. ✅ Vai para carteira
```

### Teste 4: Aguardo de Prestador
```
1. Crie e pague serviço
2. ✅ Vai para tela de aguardo
3. ✅ Veja animação girando
4. ✅ Texto: "Procurando prestador..."
5. API retorna ACEITO:
   ✅ Animação muda
   ✅ Aparece card prestador
   ✅ Texto: "Prestador encontrado!"
6. API retorna EM_ANDAMENTO:
   ✅ Navega automático para rastreamento
```

### Teste 5: Rastreamento
```
1. Estando na tela de rastreamento
2. ✅ Mapa carrega
3. ✅ Vê 2 marcadores (prestador + destino)
4. ✅ Card inferior mostra info
5. API atualiza posição:
   ✅ Marcador se move
   ✅ Câmera segue
6. API retorna CONCLUIDO:
   ✅ Mensagem de sucesso
   ✅ Volta para home
```

---

## 🎓 Dicas de Uso

### Para Demonstração (TCC)
1. **Prepare antes:**
   - Deposite R$ 200 na carteira
   - Feche e abra app (mostrar que persiste)
   - Mostre histórico vazio

2. **Durante apresentação:**
   - Crie serviço passo a passo
   - Mostre verificação de saldo
   - Confirme pagamento
   - Mostre débito imediato
   - Mostre aguardo (pode simular status na API)
   - Mostre rastreamento no mapa

3. **Destaque:**
   - Persistência: "Veja, fechei e o saldo continuou"
   - Integração: "Conectado com API real"
   - Tempo real: "Atualiza a cada 5 segundos"
   - UX: "Navegação automática entre etapas"

### Para Desenvolvimento
- Use logs para debug:
  ```kotlin
  Log.d("TelaAguardo", "Status atual: ${servico?.status}")
  Log.d("CarteiraViewModel", "Saldo: R$ ${saldo.value.saldoDisponivel}")
  ```

- Filtros no Logcat:
  - `ServicoViewModel` - polling e status
  - `CarteiraViewModel` - transações
  - `TelaAguardo` - mudanças de estado

### Para Testar com API Real
- Configure token válido
- Tenha prestadores disponíveis
- Prestadores devem aceitar e iniciar serviços
- Prestadores devem enviar coordenadas

---

## ⚙️ Configurações Importantes

### Google Maps
**Pré-requisito:**
- SHA-1 configurado no Google Cloud Console
- API Key válida (já está no código)

**Ver guia:** `COMO_VER_SHA1.md`

### PagBank
**Pré-requisito:**
- Nenhum! Já funciona em modo sandbox
- Sistema de pagamento funciona como real
- Apenas para testes (não cobra de verdade)

### API Facilita
**Pré-requisito:**
- Token JWT válido
- Base URL: `https://servidor-facilita.onrender.com/v1/facilita/`
- Endpoints disponíveis (veja documentação)

---

## 📱 Fluxo Visual

```
┌─────────────┐
│  TelaHome   │
└──────┬──────┘
       │
       ├─────→ [Clica Categoria] ─────→ ┌──────────────────────┐
       │                                 │ TelaCriarServico     │
       │                                 │ (preenche detalhes)  │
       │                                 └──────────┬───────────┘
       │                                            │
       │                                            ↓
       │                                 ┌──────────────────────┐
       │                                 │ TelaPagamento        │
       │                                 │ (verifica saldo)     │
       │                                 └──────────┬───────────┘
       │                                            │
       │                                   ┌────────┴────────┐
       │                                   │                 │
       │                            [Tem saldo]      [Não tem saldo]
       │                                   │                 │
       │                                   ↓                 ↓
       │                         ┌─────────────────┐  ┌──────────┐
       │                         │ Debita Carteira │  │ Dialog + │
       │                         │ + Cria Transação│  │ Recarga  │
       │                         └────────┬────────┘  └────┬─────┘
       │                                  │                 │
       │                                  ↓                 ↓
       │                         ┌────────────────┐   ┌──────────┐
       │                         │ TelaAguardo    │   │ Carteira │
       │                         │ (polling API)  │   └──────────┘
       │                         └────────┬───────┘
       │                                  │
       │                         [Status ACEITO]
       │                                  │
       │                                  ↓
       │                         [Status EM_ANDAMENTO]
       │                                  │
       │                                  ↓
       │                         ┌────────────────┐
       │                         │ TelaRastreamento│
       │                         │ (mapa + polling)│
       │                         └────────┬───────┘
       │                                  │
       │                         [Status CONCLUIDO]
       │                                  │
       └──────────────────────────────────┘
                [Volta para Home]
```

---

## 🎉 Resumo Final

### ✅ O que funciona:
- Carteira com persistência
- Pagamento com débito automático
- Aguardo com polling
- Rastreamento em tempo real
- Integração com API
- Google Maps

### ⚠️ O que precisa para funcionar 100%:
- Java 17 (veja `ERRO_JAVA_17.md`)
- SHA-1 configurado (veja `COMO_VER_SHA1.md`)
- API retornando dados corretos
- Prestadores aceitando e iniciando serviços

### 📚 Documentação Completa:
- `SISTEMA_COMPLETO_GUIA.md` - Guia técnico completo
- `RESUMO_IMPLEMENTACOES.md` - Resumo das mudanças
- `COMO_VER_SHA1.md` - Configurar Google Maps
- `ERRO_JAVA_17.md` - Resolver erro de Java

---

**Pronto para usar! 🚀**

