# 📱 Guia Visual - Tela de Rastreamento Estilo Uber

## 🎨 Layout Completo

```
┌─────────────────────────────────────────────┐
│  ┌─────────────────────────────────────┐   │
│  │  ← Serviço em andamento         ▼  │   │ ← Header
│  │     🟢 Ao vivo                      │   │   Expandível
│  │     ⏱️ Chega em ~5 min              │   │
│  │                                     │   │
│  │  [DETALHES EXPANDÍVEIS]            │   │
│  │  📦 Categoria: Entrega             │   │
│  │  💰 Valor: R$ 15,00                │   │
│  │  📍 Destino: Rua XYZ, 123          │   │
│  └─────────────────────────────────────┘   │
│                                             │
│           🗺️  GOOGLE MAPS                  │
│                                             │
│         🟢 Prestador (Ao vivo)             │
│              ↓                              │
│         [ Animação em                       │
│           tempo real ]                      │
│              ↓                              │
│         🔴 Destino                          │
│                                             │
│  ┌─────────────────────────────────────┐   │
│  │         ━━━━━━                      │   │ ← Drag Handle
│  │                                     │   │
│  │  ╭─────────╮                        │   │
│  │  │  ┌───┐  │  João Silva           │   │ ← Prestador
│  │  │  │👤 │  │  ⭐⭐⭐⭐⭐ 4.9        │   │   com borda
│  │  │  └───┘  │  📞 (11) 99999-9999   │   │   gradiente
│  │  ╰─────────╯                        │   │
│  │                                     │   │
│  │  ┌──────────┐  ┌──────────┐        │   │
│  │  │ 📞 Ligar │  │ 💬 Chat  │        │   │ ← Botões de ação
│  │  └──────────┘  └──────────┘        │   │
│  │                                     │   │
│  │  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │   │
│  │                                     │   │
│  │  🚗 Veículo                         │   │
│  │  ┌─────────────────────────────┐   │   │
│  │  │ Modelo:  Honda Civic        │   │   │ ← Seção Veículo
│  │  │ Placa:   ABC-1234           │   │   │
│  │  │ Cor:     Preto              │   │   │
│  │  │ Ano:     2022               │   │   │
│  │  └─────────────────────────────┘   │   │
│  │                                     │   │
│  │  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │   │
│  │                                     │   │
│  │  📋 Detalhes do Serviço             │   │
│  │  ┌─────────────────────────────┐   │   │
│  │  │ Status:    Em andamento     │   │   │ ← Seção Serviço
│  │  │ Categoria: Entrega          │   │   │
│  │  │ Valor:     R$ 15,00         │   │   │
│  │  │ Descrição: Entrega urgente  │   │   │
│  │  └─────────────────────────────┘   │   │
│  │                                     │   │
│  │  ┌─────────────────────────────┐   │   │
│  │  │   ❌ Cancelar Serviço       │   │   │ ← Botão Cancelar
│  │  └─────────────────────────────┘   │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## 🎯 Elementos Principais

### 1. Header (Topo)
```
┌─────────────────────────────────────┐
│  ← [Voltar]  Serviço em andamento  ▼│
│              🟢 Ao vivo              │ ← Pulsante
│              ⏱️ Chega em ~5 min      │
└─────────────────────────────────────┘
```

**Estados do Indicador**:
- 🟢 Verde pulsante = Conectado e ao vivo
- 🔴 Vermelho = Desconectado

### 2. Detalhes Expandíveis (ao clicar em ▼)
```
┌─────────────────────────────────────┐
│  📦 Categoria: Entrega Express      │
│  💰 Valor: R$ 15,00                 │
│  📍 Destino: Av. Paulista, 1000     │
│     São Paulo - SP                  │
└─────────────────────────────────────┘
```

### 3. Mapa (Centro)
```
        🗺️ Google Maps
        
    🟢 ← Marcador Verde
    │    (Prestador)
    │    Atualiza em tempo real
    │
    ↓ (Animação suave)
    
    🔴 ← Marcador Vermelho
         (Destino fixo)
```

**Marcadores**:
- 🟢 **Verde**: Prestador (move em tempo real)
- 🔴 **Vermelho**: Destino (fixo)

### 4. Card do Prestador
```
┌─────────────────────────────────────┐
│          ━━━━━━                     │ ← Drag handle
│                                     │
│   ╭───────────╮                     │
│   │  ┌─────┐  │  João Silva        │
│   │  │ 👤  │  │  ⭐⭐⭐⭐⭐ 4.9     │
│   │  └─────┘  │  📞 (11) 99999     │
│   ╰───────────╯                     │
│   ↑ Avatar com                      │
│     borda gradiente                 │
└─────────────────────────────────────┘
```

**Componentes**:
- Avatar: Círculo com borda gradiente verde
- Nome: Fonte 20sp, Bold
- Estrelas: 5 estrelas visuais (preenchidas conforme nota)
- Telefone: Ícone + número

### 5. Botões de Ação
```
┌──────────────────┐  ┌─────────────────┐
│  📞  Ligar       │  │  💬  Chat       │
│  (Verde sólido)  │  │  (Outline)      │
└──────────────────┘  └─────────────────┘
```

**Funcionalidade**:
- **Ligar**: Abre discador com número do prestador ✅
- **Chat**: Preparado para implementação futura

### 6. Seção Veículo
```
┌─────────────────────────────────────┐
│  🚗 Veículo                          │
│  ┌─────────────────────────────┐   │
│  │ Modelo    Honda Civic       │   │
│  │ Placa     ABC-1234          │   │
│  │ Cor       Preto             │   │
│  │ Ano       2022              │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

**Layout**:
- Background: Cinza claro (#F5F5F5)
- Border radius: 12dp
- Padding: 12dp
- Espaçamento entre linhas: 10dp

### 7. Seção Detalhes do Serviço
```
┌─────────────────────────────────────┐
│  📋 Detalhes do Serviço              │
│  ┌─────────────────────────────┐   │
│  │ Status      Em andamento    │   │
│  │ Categoria   Entrega         │   │
│  │ Valor       R$ 15,00        │   │
│  │ Descrição   Entrega urgente │   │
│  │             do centro para  │   │
│  │             zona leste      │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### 8. Botão Cancelar
```
┌─────────────────────────────────────┐
│  ┌─────────────────────────────┐   │
│  │  ❌  Cancelar Serviço       │   │
│  │      (Vermelho outline)      │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## 🎨 Paleta de Cores Visual

```
Verde Principal    ████████  #019D31
Verde Claro        ████████  #06C755
Verde Ao Vivo      ████████  #00FF00
Vermelho Offline   ████████  #FF0000
Vermelho Cancel    ████████  #FF4444
Ouro Estrelas      ████████  #FFD700
Branco             ████████  #FFFFFF
Cinza Muito Claro  ████████  #F5F5F5
Cinza Claro        ████████  #E0E0E0
Cinza Médio        ████████  #6D6D6D
Cinza Escuro       ████████  #2D2D2D
Preto              ████████  #000000
```

---

## 🎭 Estados e Animações

### Estado 1: Conectado (Ao Vivo)
```
Header:  🟢 Ao vivo (pulsante)
Mapa:    Marcador verde com snippet "🟢 Ao vivo"
Status:  Conexão ativa, recebendo atualizações
```

### Estado 2: Desconectado (Offline)
```
Header:  🔴 Offline (estático)
Mapa:    Marcador verde com snippet "⚪ Offline"
Status:  Sem conexão, tentando reconectar
```

### Estado 3: Serviço Concluído
```
Toast:   "✅ Serviço concluído com sucesso!"
Ação:    Desconecta WebSocket
         Aguarda 2 segundos
         Navega para Home
```

---

## 🔄 Animações

### 1. Indicador Ao Vivo (Pulse)
```
Frame 1:  🟢 (alpha = 0.3)
   ↓      
Frame 2:  🟢 (alpha = 0.5)
   ↓      
Frame 3:  🟢 (alpha = 0.7)
   ↓      
Frame 4:  🟢 (alpha = 1.0)
   ↓      
Frame 3:  🟢 (alpha = 0.7)
   ↓      
Frame 2:  🟢 (alpha = 0.5)
   ↓      
Frame 1:  🟢 (alpha = 0.3)
```
Duração: 1000ms (loop infinito)

### 2. Marcador do Prestador (Movimento)
```
Posição 1: Lat -23.550, Lng -46.633
    ↓ (animação 1000ms)
Posição 2: Lat -23.551, Lng -46.634
    ↓ (animação 1000ms)
Posição 3: Lat -23.552, Lng -46.635
```
Animação suave via CameraUpdateFactory

### 3. Expansão de Detalhes
```
Estado Fechado:
┌─────────────────┐
│  Serviço...  ▼ │
└─────────────────┘

      ↓ (clique)

Estado Aberto:
┌─────────────────┐
│  Serviço...  ▲ │
│  ━━━━━━━━━━━━━ │
│  📦 Categoria   │
│  💰 Valor       │
│  📍 Destino     │
└─────────────────┘
```

---

## 📱 Responsividade

### Dispositivos Pequenos (< 360dp)
- Card inferior: scrollable
- Fonte reduzida: -1sp
- Padding reduzido: 16dp

### Dispositivos Médios (360-480dp)
- Layout padrão
- Fonte normal
- Padding: 20dp

### Dispositivos Grandes (> 480dp)
- Mais espaço entre elementos
- Fonte ampliada: +1sp
- Padding: 24dp

---

## 🎯 Interações do Usuário

### 1. Clicar em "←" (Voltar)
```
Ação: navController.popBackStack()
Efeito: Volta para tela anterior
```

### 2. Clicar em "▼" (Expandir)
```
Ação: mostrarDetalhes = !mostrarDetalhes
Efeito: Mostra/esconde detalhes do serviço
```

### 3. Clicar em "📞 Ligar"
```
Ação: Intent(Intent.ACTION_DIAL)
Efeito: Abre discador com número do prestador
```

### 4. Clicar em "💬 Chat"
```
Ação: Toast("Chat em breve!")
Efeito: Mostra mensagem (preparado para futuro)
```

### 5. Clicar em "❌ Cancelar Serviço"
```
Ação: mostrarDialogoCancelar = true
Efeito: Mostra dialog de confirmação
       ↓ (confirmar)
       Cancela serviço via API
       Desconecta WebSocket
       Navega para Home
```

### 6. Scroll no Card Inferior
```
Ação: Arrastar dedo para cima/baixo
Efeito: Scroll vertical para ver mais informações
```

### 7. Zoom no Mapa
```
Ação: Pinch to zoom
Efeito: Aumenta/diminui zoom do mapa
```

---

## 💡 Dicas de UX

### 1. Feedback Visual
- ✅ Indicador pulsante mostra conexão ativa
- ✅ Marcadores coloridos facilitam identificação
- ✅ Botões com ícones são auto-explicativos
- ✅ Loading overlay durante operações

### 2. Informação Hierárquica
- **Nível 1**: Nome do prestador (maior destaque)
- **Nível 2**: Avaliação e telefone
- **Nível 3**: Veículo e detalhes
- **Nível 4**: Botões de ação

### 3. Espaçamento Respirável
- Padding: 20dp em cards
- Spacing: 12-16dp entre seções
- Margin: 16dp nas bordas

### 4. Cores Significativas
- 🟢 Verde = Ativo, Sucesso, Ao vivo
- 🔴 Vermelho = Offline, Cancelar, Erro
- 🟡 Amarelo/Ouro = Avaliação, Destaque
- ⚪ Cinza = Neutro, Secundário

---

## 🎊 Resultado Final

Uma tela de rastreamento **moderna**, **funcional** e **completa**, inspirada no Uber, com:

- ✅ Rastreamento em tempo real via WebSocket
- ✅ Layout limpo e organizado
- ✅ Todas as informações do prestador
- ✅ Funcionalidade de ligar implementada
- ✅ Animações suaves
- ✅ Feedback visual claro
- ✅ Experiência de usuário premium

**Status**: 🚀 PRONTO PARA PRODUÇÃO!

