# 🎨 Guia Visual - Rastreamento Estilo Uber/99

## 📱 Como Ficará na Tela

### 🗺️ Mapa Principal

```
┌─────────────────────────────────────────┐
│  ← [Voltar]    EM ANDAMENTO    [⋮]     │
│     🟢 Ao vivo                          │
│     📍 15.2 km  ⏱️ 23 min              │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│                                         │
│      🔵 Origem                          │
│         ╲                               │
│          ━━━━━━━━ (verde)              │
│                 ╲                       │
│                  🟠 Parada 1            │
│                     ╲                   │
│                      ━━━━━━            │
│                            ╲            │
│                             🟠 Parada 2 │
│                                ╲        │
│                                 ━━━━    │
│           🟢 Prestador (movendo) ╲      │
│                                    ╲    │
│                                     🔴  │
│                                  Destino│
│                                         │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│   ═══  (linha decorativa)               │
│                                         │
│   👤  Hugo Lopes                        │
│       ⭐⭐⭐⭐⭐ 5.0                     │
│       📞 (11) 98765-4321                │
│                                         │
│   [📞 Ligar]     [💬 Chat]              │
│                                         │
│   ─────────────────────────────────     │
│                                         │
│   🚗 Veículo                            │
│   Modelo: Honda Civic                   │
│   Placa: ABC-1234                       │
│   Cor: Prata                            │
│                                         │
│   ─────────────────────────────────     │
│                                         │
│   📋 Detalhes do Serviço                │
│   Status: Em andamento                  │
│   Categoria: Transporte                 │
│   Valor: R$ 45,00                       │
│                                         │
│   [❌ Cancelar Serviço]                 │
│                                         │
└─────────────────────────────────────────┘
```

---

## 🎨 Cores dos Marcadores

### Legenda Visual

```
🔵 AZUL (HUE_AZURE)
   Tipo: "origem"
   Título: "🚩 Origem"
   Exemplo: Ponto de partida da corrida

🟠 LARANJA (HUE_ORANGE)
   Tipo: "parada"
   Título: "📍 Parada 1", "📍 Parada 2"...
   Exemplo: Shopping, Casa de amigo, etc

🔴 VERMELHO (HUE_RED)
   Tipo: "destino"
   Título: "🏁 Destino"
   Exemplo: Ponto final da corrida

🟢 VERDE (HUE_GREEN)
   Tipo: prestador (tempo real)
   Título: Nome do prestador
   Exemplo: "Hugo Lopes 🟢 Ao vivo"
```

---

## 🛣️ Tipos de Rotas Suportadas

### 1. Rota Simples (2 pontos)
```
🚩 Origem
  │
  │ ━━━━━ Rota direta
  │
🏁 Destino

Exemplo: Casa → Trabalho
```

### 2. Rota com 1 Parada (3 pontos)
```
🚩 Origem
  │
  │ ━━━━━
  │
📍 Parada 1
  │
  │ ━━━━━
  │
🏁 Destino

Exemplo: Casa → Shopping → Trabalho
```

### 3. Rota com Múltiplas Paradas (4+ pontos)
```
🚩 Origem
  │
  │ ━━━━━
  │
📍 Parada 1
  │
  │ ━━━━━
  │
📍 Parada 2
  │
  │ ━━━━━
  │
📍 Parada 3
  │
  │ ━━━━━
  │
🏁 Destino

Exemplo: Restaurante → Casa 1 → Casa 2 → Casa 3 → Base
```

---

## 🎬 Animações e Comportamento

### 1. Conexão em Tempo Real
```
🟢 Ao vivo (pulsando)
   ↓
   Opacidade: 30% → 100% → 30%
   Velocidade: 1 segundo
   Loop: Infinito
```

### 2. Movimento do Prestador
```
Posição A  →  Posição B
    🟢    →      🟢
    
Transição: 1 segundo (suave)
Via: WebSocket
Atualização: Tempo real
```

### 3. Ajuste da Câmera
```
Estado Inicial:
  Zoom: 15
  Centro: Prestador

Após Carregar Rota:
  Zoom: Auto
  Bounds: Inclui TODOS os pontos
  Padding: 150dp
  Animação: 1.5 segundos
```

---

## 📊 Informações Exibidas

### Header Superior (Fixo)
```
┌─────────────────────────────────┐
│ ← Voltar | EM ANDAMENTO | ⋮    │
│          🟢 Ao vivo             │
│    📍 15.2 km  ⏱️ 23 min       │
└─────────────────────────────────┘
```

### Card Inferior (Rolável)
```
┌─────────────────────────────────┐
│ ═══                             │
│                                 │
│ 👤 Prestador                    │
│    Nome: Hugo Lopes             │
│    Avaliação: ⭐⭐⭐⭐⭐ 5.0    │
│    Telefone: (11) 98765-4321    │
│                                 │
│ [📞 Ligar] [💬 Chat]            │
│                                 │
│ ─────────────────────────────   │
│                                 │
│ 🚗 Veículo                      │
│    Honda Civic Prata            │
│    ABC-1234                     │
│                                 │
│ ─────────────────────────────   │
│                                 │
│ 📋 Detalhes                     │
│    Categoria: Transporte        │
│    Valor: R$ 45,00              │
│                                 │
│ [❌ Cancelar]                   │
└─────────────────────────────────┘
```

---

## 🎯 Casos de Uso

### Caso 1: Uber/99 Tradicional
```json
{
  "paradas": [
    {
      "ordem": 0,
      "tipo": "origem",
      "endereco_completo": "Rua A, 123 - Centro"
    },
    {
      "ordem": 1,
      "tipo": "destino",
      "endereco_completo": "Rua B, 456 - Bairro X"
    }
  ]
}
```
**Resultado:** Linha verde direta A → B

---

### Caso 2: Corrida com Parada no Caminho
```json
{
  "paradas": [
    {
      "ordem": 0,
      "tipo": "origem",
      "endereco_completo": "Casa"
    },
    {
      "ordem": 1,
      "tipo": "parada",
      "endereco_completo": "Shopping ABC"
    },
    {
      "ordem": 2,
      "tipo": "destino",
      "endereco_completo": "Trabalho"
    }
  ]
}
```
**Resultado:** Linha verde Casa → Shopping → Trabalho

---

### Caso 3: Delivery com Múltiplos Endereços
```json
{
  "paradas": [
    {
      "ordem": 0,
      "tipo": "origem",
      "endereco_completo": "Restaurante Sabor"
    },
    {
      "ordem": 1,
      "tipo": "parada",
      "endereco_completo": "Rua das Flores, 10"
    },
    {
      "ordem": 2,
      "tipo": "parada",
      "endereco_completo": "Av. Principal, 500"
    },
    {
      "ordem": 3,
      "tipo": "parada",
      "endereco_completo": "Rua do Comércio, 88"
    },
    {
      "ordem": 4,
      "tipo": "destino",
      "endereco_completo": "Base Central"
    }
  ]
}
```
**Resultado:** Linha verde conectando 5 pontos

---

## 🔴 Estados Visuais

### 1. Conectado (Online)
```
🟢 Ao vivo (verde pulsante)
Marcador do prestador: Verde
Linha da rota: Verde vibrante
```

### 2. Desconectado (Offline)
```
🔴 Offline (vermelho estático)
Marcador do prestador: Cinza
Linha da rota: Verde (mantém)
```

### 3. Carregando
```
⏳ Carregando...
Overlay: Semi-transparente
Spinner: Verde (#00B14F)
```

### 4. Erro
```
❌ Erro ao conectar
Toast: Vermelho
Botão: Tentar novamente
```

---

## 📱 Responsividade

### Smartphone (Portrait)
- Mapa: 60% da tela
- Card inferior: 40% rolável
- Header: Fixo no topo

### Tablet (Landscape)
- Mapa: 70% da tela
- Card inferior: 30% mais largo
- Botões: Maiores

---

## 🎨 Paleta de Cores

```css
/* Principal */
Verde Facilita:    #019D31
Verde Escuro:      #00B14F
Verde Claro:       #06C755

/* Marcadores */
Azul (Origem):     #007FFF (HUE_AZURE)
Laranja (Parada):  #FF8C00 (HUE_ORANGE)
Vermelho (Dest):   #FF0000 (HUE_RED)
Verde (Prestador): #00FF00 (HUE_GREEN)

/* UI */
Texto Escuro:      #2D2D2D
Texto Médio:       #6D6D6D
Fundo Card:        #FFFFFF
Divisor:           #E0E0E0

/* Linha da Rota */
Fundo (Escuro):    #2D2D2D (12dp)
Principal (Verde): #019D31 (8dp)
```

---

## ✅ Checklist Visual

### Mapa
- [x] Polyline verde (2 camadas)
- [x] Marcadores coloridos
- [x] Câmera ajustada automaticamente
- [x] Zoom/pan habilitados
- [x] Prestador em tempo real

### Header
- [x] Botão voltar
- [x] Status "Em andamento"
- [x] Indicador "Ao vivo" pulsante
- [x] Distância e tempo
- [x] Botão expandir/recolher

### Card Inferior
- [x] Avatar do prestador
- [x] Nome e avaliação (estrelas)
- [x] Telefone
- [x] Botão "Ligar" funcional
- [x] Botão "Chat"
- [x] Info do veículo
- [x] Detalhes do serviço
- [x] Botão "Cancelar"

### Comportamento
- [x] Atualização em tempo real via WebSocket
- [x] Polling de 10s via API
- [x] Navegação ao concluir/cancelar
- [x] Toast de feedback
- [x] Dialog de confirmação

---

**Status:** ✅ 100% Implementado  
**Visual:** 🎨 Estilo Uber/99  
**Funcional:** 🚀 Tempo Real

