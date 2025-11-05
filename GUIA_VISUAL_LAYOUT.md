# 🎨 Guia Visual - TelaMontarServico

## Layout Geral

```
┌─────────────────────────────────────────┐
│   ← Monte o seu serviço                 │  ← Header Verde com Gradiente
└─────────────────────────────────────────┘

    ┌───────────────────────────────────┐
    │                                   │
    │  🟢  ─┐                           │  ← Origem
    │       │  Origem                   │     (Autocomplete ativo)
    │       │  [De onde você sai?____]  │
    │       │                           │
    │       │  Sugestões:               │
    │       │  📍 Av. Paulista, 1000... │
    │       │  📍 Av. Paulista, 1200... │
    │       │                           │
    │      ─┤                           │
    │       │  Parada 1            [X]  │  ← Parada com botão X
    │       │  [Adicione uma parada__]  │     (Autocomplete individual)
    │       │                           │
    │      ─┤                           │
    │       │  + Adicionar parada (1/3) │  ← Botão adicionar
    │       │                           │
    │      ─┤                           │
    │  📍  ─┘  Destino                  │  ← Destino
    │          [Para onde você vai?__]  │     (Autocomplete ativo)
    │                                   │
    │  ─────────────────────────────────│
    │                                   │
    │  Descrição do serviço             │
    │  ┌─────────────────────────────┐ │
    │  │ Ex: Comprar remédios...     │ │
    │  │                             │ │
    │  │                             │ │
    │  └─────────────────────────────┘ │
    │                                   │
    │  ┌─────────────────────────────┐ │
    │  │   Confirmar Serviço         │ │  ← Botão verde gradiente
    │  └─────────────────────────────┘ │
    │                                   │
    └───────────────────────────────────┘
```

## Cores Utilizadas

- **Verde Principal**: `#00A651` (ações, bordas focadas)
- **Verde Escuro**: `#019D31` (gradientes)
- **Verde Claro**: `#06C755` (gradientes)
- **Vermelho**: `#D32F2F` (ícone de destino)
- **Cinza Escuro**: `#2A2A2A` (textos principais)
- **Cinza Médio**: `#6D6D6D` (labels)
- **Cinza Claro**: `#E0E0E0` (bordas, linha de rota)
- **Cinza Extra Claro**: `#F0F2F5` (background)
- **Branco**: `#FFFFFF` (cards)

## Elementos Visuais

### 1. Indicador de Rota
```
🟢 ← Círculo verde sólido (12dp)
│
│  ← Linha vertical cinza (2dp de largura)
│
📍 ← Ícone pin vermelho (20dp)
```

### 2. Campo com Autocomplete
```
┌────────────────────────────────┐
│ Label (13sp, cinza médio)      │
│                                │
│ ┌────────────────────────────┐ │
│ │ Campo de texto             │ │ ← OutlinedTextField
│ │ (borda verde quando foco)  │ │    (12dp border radius)
│ └────────────────────────────┘ │
│                                │
│ ┌────────────────────────────┐ │
│ │ 📍 Sugestão 1              │ │
│ │ ─────────────────────────  │ │ ← Card de sugestões
│ │ 📍 Sugestão 2              │ │    (8dp border radius)
│ │ ─────────────────────────  │ │    (max 200dp altura)
│ │ 📍 Sugestão 3              │ │
│ └────────────────────────────┘ │
└────────────────────────────────┘
```

### 3. Botão Adicionar Parada
```
┌─────────────────────────────────┐
│  ➕  Adicionar parada (2/3)     │ ← Borda verde tracejada
└─────────────────────────────────┘    Texto verde
```

### 4. Botão Confirmar
```
┌───────────────────────────────────┐
│                                   │
│     Confirmar Serviço             │ ← Gradiente horizontal
│                                   │    Verde claro → Verde escuro
└───────────────────────────────────┘    Texto branco bold
```

## Estados Interativos

### Campo Normal (sem foco)
- Borda: Cinza claro `#E0E0E0`
- Background: Cinza muito claro `#FAFAFA`

### Campo com Foco
- Borda: Verde `#00A651`
- Background: Branco `#FFFFFF`
- Sugestões aparecem automaticamente

### Campo Preenchido
- Texto: Preto `#2A2A2A`
- Tamanho: 15sp

### Sugestão ao Passar Mouse
- Background muda para cinza claro
- Cursor pointer

## Espaçamentos

- **Padding do Card**: 20dp
- **Espaçamento entre campos**: 12dp
- **Espaçamento vertical geral**: 16dp
- **Altura do Header**: 88dp
- **Altura botão confirmar**: 56dp
- **Altura campo descrição**: 100dp
- **Border radius padrão**: 12dp
- **Border radius sugestões**: 8dp

## Responsividade

A tela se adapta dinamicamente:
- **0 paradas**: Linha de 60dp de altura
- **1 parada**: Linha de 140dp de altura
- **2 paradas**: Linha de 220dp de altura  
- **3 paradas**: Linha de 300dp de altura

Cálculo: `60 + (número_paradas * 80)`

## Animações

- ✨ Campos aparecem com transição suave
- ✨ Sugestões aparecem com fade in
- ✨ Botão X com hover effect
- ✨ Focus ring animado

---

**Design inspirado em**: Uber, 99, Google Maps
**Framework**: Jetpack Compose + Material 3

