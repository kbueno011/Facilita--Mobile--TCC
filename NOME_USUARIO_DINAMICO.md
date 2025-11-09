# Sistema de Nome de Usuário Dinâmico

## 📋 Resumo das Alterações

Implementado sistema para exibir o nome real do usuário logado em todas as telas da aplicação, substituindo os nomes fixos (como "Lara", "Adriana", etc.) pelo nome que vem da API durante o login.

## 🔧 Arquivos Modificados

### 1. TokenManager.kt
**Localização:** `app/src/main/java/com/exemple/facilita/utils/TokenManager.kt`

**Mudanças:**
- ✅ Adicionada constante `USER_NAME_KEY` para armazenar o nome do usuário
- ✅ Atualizado método `salvarToken()` para aceitar parâmetro `nomeUsuario`
- ✅ Adicionado método `obterNomeUsuario()` para recuperar o nome salvo
- ✅ Compatibilidade com SharedPreferences antigo mantida

**Código adicionado:**
```kotlin
private const val USER_NAME_KEY = "user_name"

fun salvarToken(context: Context, token: String, tipoConta: String? = null, userId: Int? = null, nomeUsuario: String? = null) {
    // ... salva também o nome do usuário
    nomeUsuario?.let { putString(USER_NAME_KEY, it) }
}

fun obterNomeUsuario(context: Context): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var nome = prefs.getString(USER_NAME_KEY, null)
    
    // Compatibilidade com código legado
    if (nome == null) {
        val legacyPrefs = context.getSharedPreferences("FacilitaPrefs", Context.MODE_PRIVATE)
        nome = legacyPrefs.getString("nomeUsuario", null)
    }
    
    return nome
}
```

### 2. TelaLogin.kt
**Localização:** `app/src/main/java/com/exemple/facilita/screens/TelaLogin.kt`

**Mudanças:**
- ✅ Captura o nome do usuário da resposta da API durante o login
- ✅ Salva o nome junto com o token usando `TokenManager.salvarToken()`
- ✅ Adicionado log para debug do nome salvo

**Código modificado:**
```kotlin
val response: LoginResponse = facilitaApi.loginUser(login).await()

val token = response.token
val tipoConta = response.usuario.tipo_conta
val userId = response.usuario.id
val nomeUsuario = response.usuario.nome  // ✅ Captura o nome

Log.d("LOGIN_DEBUG", "Nome do usuário: $nomeUsuario")

TokenManager.salvarToken(context, token, tipoConta, userId, nomeUsuario)  // ✅ Salva o nome

// Verificação
val nomeSalvo = TokenManager.obterNomeUsuario(context)
Log.d("LOGIN_DEBUG", "Nome salvo: $nomeSalvo")
```

### 3. TelaHome.kt
**Localização:** `app/src/main/java/com/exemple/facilita/screens/TelaHome.kt`

**Mudanças:**
- ✅ Importado `LocalContext` e `TokenManager`
- ✅ Busca o nome do usuário dinamicamente ao carregar a tela
- ✅ Substituído "Olá, Lara" por "Olá, $nomeUsuario"

**Antes:**
```kotlin
Text(
    text = "Olá, Lara",
    fontSize = 24.sp,
    fontWeight = FontWeight.ExtraBold,
    color = Color(0xFF2D2D2D)
)
```

**Depois:**
```kotlin
val context = LocalContext.current
val nomeUsuario = TokenManager.obterNomeUsuario(context) ?: "Usuário"

Text(
    text = "Olá, $nomeUsuario",
    fontSize = 24.sp,
    fontWeight = FontWeight.ExtraBold,
    color = Color(0xFF2D2D2D)
)
```

### 4. TelaCarteira.kt
**Localização:** `app/src/main/java/com/exemple/facilita/screens/TelaCarteira.kt`

**Mudanças:**
- ✅ Importado `LocalContext` e `TokenManager`
- ✅ Substituído nome fixo "Adriana" pela busca dinâmica
- ✅ Nome do usuário exibido no header da carteira e no avatar

**Antes:**
```kotlin
val nomeUsuario = "Adriana"
```

**Depois:**
```kotlin
val context = LocalContext.current
val nomeUsuario = TokenManager.obterNomeUsuario(context) ?: "Usuário"
```

### 5. TelaCompletarPerfilContratante.kt
**Localização:** `app/src/main/java/com/exemple/facilita/screens/TelaCompletarPerfilContratante.kt`

**Mudanças:**
- ✅ Importado `TokenManager`
- ✅ Substituído funções locais `getNomeUsuario()` e `getTokenFromPreferences()` por `TokenManager`
- ✅ Código mais consistente e centralizado

**Antes:**
```kotlin
val nomeUsuario by remember { mutableStateOf(getNomeUsuario(context)) }
val tokenUsuario by remember { mutableStateOf(getTokenFromPreferences(context)) }

// Funções locais duplicadas
fun getNomeUsuario(context: Context): String {
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("nomeUsuario", "") ?: ""
}
```

**Depois:**
```kotlin
val nomeUsuario by remember { mutableStateOf(TokenManager.obterNomeUsuario(context) ?: "") }
val tokenUsuario by remember { mutableStateOf(TokenManager.obterToken(context) ?: "") }

// Funções locais removidas - usar TokenManager ao invés
```

## 📱 Telas Afetadas

### ✅ Atualizadas
1. **TelaHome** - Exibe "Olá, [Nome do Usuário]"
2. **TelaCarteira** - Exibe nome no header e avatar
3. **TelaCompletarPerfilContratante** - Usa TokenManager para buscar nome

### ℹ️ Já Funcionais
3. **TelaPerfilContratante** - Já busca dados do usuário via API/ViewModel

## 🔄 Fluxo de Funcionamento

```
┌─────────────────┐
│   Usuário faz   │
│     Login       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  API retorna    │
│  LoginResponse  │
│  com nome       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  TokenManager   │
│  salva nome em  │
│SharedPreferences│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Qualquer tela  │
│  recupera nome  │
│  do TokenManager│
└─────────────────┘
```

## 🧪 Como Testar

1. **Faça logout** (se estiver logado)
2. **Faça login** novamente com um usuário
3. **Navegue para a Home** - Deve aparecer "Olá, [Seu Nome]"
4. **Navegue para a Carteira** - Deve aparecer seu nome no topo
5. **Feche e reabra o app** - O nome deve persistir (salvo no SharedPreferences)

## 📝 Observações Importantes

- ✅ O nome é salvo automaticamente no login
- ✅ O nome persiste entre sessões (SharedPreferences)
- ✅ Se o nome não estiver disponível, exibe "Usuário" como fallback
- ✅ Compatibilidade mantida com código legado
- ✅ Não é necessário fazer logout/login se o nome já estava na API

## 🔍 Verificação de Logs

Para verificar se está funcionando, busque por estes logs no Logcat:

```
D/LOGIN_DEBUG: Nome do usuário: [Nome capturado]
D/LOGIN_DEBUG: Nome salvo: [Nome verificado]
```

## 🚀 Próximos Passos (Opcional)

Se houver outras telas que mostram o nome do usuário com valor fixo:

1. Adicione: `val context = LocalContext.current`
2. Busque o nome: `val nomeUsuario = TokenManager.obterNomeUsuario(context) ?: "Usuário"`
3. Use a variável no lugar do texto fixo

## ✨ Benefícios

- ✅ **Personalização**: Cada usuário vê seu próprio nome
- ✅ **Consistência**: Nome vem sempre da mesma fonte (login)
- ✅ **Manutenibilidade**: Fácil adicionar em novas telas
- ✅ **Persistência**: Nome salvo localmente, não precisa buscar API toda vez
- ✅ **Segurança**: Nome armazenado de forma segura no SharedPreferences privado

