# 🔴 ERRO 403 - ACESSO NEGADO

## 🐛 O que significa Erro 403?

**HTTP 403 Forbidden** significa que o servidor entendeu a requisição, mas se recusa a autorizá-la. No seu caso, pode ser por:

---

## 🔍 Possíveis Causas e Soluções

### 1. ✅ **Perfil de Contratante Não Completado**

**Problema:** Após criar a conta, você precisa completar o perfil de CONTRATANTE para poder criar serviços.

**Como verificar:**
1. Após fazer login, verifique no Logcat:
```
LOGIN_DEBUG: Tipo de conta: CONTRATANTE
```

2. Se aparecer `null` ou `PRESTADOR`, você não tem perfil de contratante

**Solução:**
- Complete seu perfil na tela "Completar Perfil Contratante"
- Normalmente é direcionado automaticamente após o cadastro
- Verifique se o campo `proximo_passo` no login indica alguma ação

---

### 2. ✅ **Token Expirado**

**Problema:** Tokens JWT expiram após um tempo (geralmente 8 horas).

**Como verificar no Logcat:**
```
API_ERROR: Código: 403
API_ERROR: Body: {"message": "Token expirado"}
```

**Solução:**
- Faça **logout** e **login** novamente
- O token será renovado

---

### 3. ✅ **Token Corrompido ou Inválido**

**Problema:** Token pode ter sido salvo incorretamente ou está corrompido.

**Como verificar:**
1. No Logcat após login:
```
LOGIN_DEBUG: Token recebido: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
LOGIN_DEBUG: Token salvo verificado: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

2. No Logcat ao criar serviço:
```
API_DEBUG: Token sendo usado: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

3. **Os dois devem ser iguais!** Se forem diferentes, há problema.

**Solução:**
- Desinstale o app
- Instale novamente
- Faça login

---

### 4. ✅ **Endpoint Requer Permissões Específicas**

**Problema:** O endpoint `/v1/facilita/servico` pode exigir que o usuário:
- Seja do tipo CONTRATANTE
- Tenha perfil completo
- Tenha endereço cadastrado

**Como verificar:**
Veja o erro completo no Logcat:
```
API_ERROR: Body: {"message": "Perfil de contratante não encontrado"}
```

**Solução:**
- Complete todas as etapas de configuração do perfil
- Cadastre pelo menos um endereço

---

## 🧪 Como Debugar

### Passo 1: Verificar Login
```
1. Faça login
2. Abra o Logcat (filtro: LOGIN_DEBUG)
3. Verifique:
   - Token recebido? ✅
   - Token salvo? ✅
   - Tipo de conta: CONTRATANTE? ✅
```

### Passo 2: Verificar Criação de Serviço
```
1. Tente criar serviço
2. Abra o Logcat (filtro: API_DEBUG e API_ERROR)
3. Verifique:
   - Token sendo usado? ✅
   - Dados estão corretos? ✅
   - Erro 403 retorna mensagem? ✅
```

### Passo 3: Comparar Tokens
```
Token do LOGIN == Token na API?
Se NÃO: Problema no TokenManager
Se SIM: Problema no backend (permissões)
```

---

## 📋 Checklist de Solução

Teste na ordem:

- [ ] 1. **Desinstalar e reinstalar o app** (limpa dados antigos)
- [ ] 2. **Fazer novo cadastro** ou **login novamente**
- [ ] 3. **Completar perfil de contratante** (se solicitado)
- [ ] 4. **Cadastrar endereço** (se necessário)
- [ ] 5. **Tentar criar serviço novamente**

---

## 🔧 Código de Debug Adicionado

### TelaLogin.kt
```kotlin
// Após login bem-sucedido:
Log.d("LOGIN_DEBUG", "Token recebido: ${token.take(50)}...")
Log.d("LOGIN_DEBUG", "Token salvo verificado: ${tokenSalvo?.take(50)}...")
Log.d("LOGIN_DEBUG", "Tipo de conta: ${response.usuario.tipo_conta}")
```

### TelaMontarServico.kt
```kotlin
// Antes de enviar para API:
Log.d("API_DEBUG", "Token sendo usado: Bearer ${token.take(30)}...")
Log.d("API_DEBUG", "Categoria: $idCategoria")
Log.d("API_DEBUG", "Descrição: $descricao")

// Erro 403:
when (response.code()) {
    403 -> "Acesso negado. Verifique se:
            1. Você completou seu perfil de contratante
            2. Seu token não expirou
            3. Você tem permissão para criar serviços"
}
```

---

## 💡 Mensagem de Erro Melhorada

Agora quando der erro 403, você verá:

```
❌ Acesso negado. Verifique se:
1. Você completou seu perfil de contratante
2. Seu token não expirou  
3. Você tem permissão para criar serviços
```

E no Logcat terá todos os detalhes para debug!

---

## 🎯 Próximos Passos

1. **Teste agora com os logs ativados**
2. **Copie e me envie os logs do Logcat**
3. **Vou identificar exatamente o problema**

**Filtros no Logcat:**
- `LOGIN_DEBUG` - Logs do login
- `API_DEBUG` - Logs da API
- `API_ERROR` - Erros da API

---

**🔍 Com os logs vou conseguir identificar exatamente o problema do erro 403!**

