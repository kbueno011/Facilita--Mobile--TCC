# ⚡ GUIA RÁPIDO - 3 PASSOS PARA TESTAR

## 🎯 INTEGRAÇÃO PAGBANK PRONTA!

Sua carteira está **100% integrada** com o PagBank Sandbox. Siga os 3 passos:

---

## 📝 PASSO 1: CONFIGURE O TOKEN (2 minutos)

### 1.1 Obter Token Sandbox

1. Acesse: https://sandbox.pagseguro.uol.com.br/
2. Crie uma conta (gratuita)
3. Faça login
4. Vá em: **Integrações > Tokens**
5. Clique em **"Criar novo token"**
6. Copie o token gerado

### 1.2 Colar no App

Abra: `app/src/main/java/com/exemple/facilita/network/PagBankClient.kt`

Linha 13:
```kotlin
private const val SANDBOX_TOKEN = "SEU_TOKEN_AQUI"
```

Cole seu token:
```kotlin
private const val SANDBOX_TOKEN = "E899DA6E-4620-4F51-8A99-B6E2D0A1F6C0"
```

**✅ TOKEN CONFIGURADO!**

---

## 🔨 PASSO 2: COMPILE E INSTALE (1 minuto)

### No Android Studio:

```
1. Clique em "Build" > "Rebuild Project"
2. Aguarde completar (1-2 minutos)
3. Clique no botão "Run" (▶️)
4. Selecione seu dispositivo/emulador
5. App vai instalar automaticamente
```

**✅ APP INSTALADO!**

---

## 🧪 PASSO 3: TESTE OS PAGAMENTOS (3 minutos)

### Teste A: Depósito via PIX

```
1. Abra o app
2. Navegue para "Carteira"
3. Clique em "Depositar"
4. Digite: R$ 100,00
5. Clique em "PIX"
6. AGUARDE: QR Code será gerado
7. Veja o QR Code e código PIX
```

**RESULTADO:** QR Code PIX gerado! ✅

### Teste B: Depósito via Cartão

```
1. Clique em "Depositar"
2. Digite: R$ 50,00
3. Clique em "Cartão de Crédito"
4. Preencha:
   📋 Número: 4111 1111 1111 1111
   👤 Nome: TESTE APROVADO
   📅 Mês: 12
   📅 Ano: 30
   🔒 CVV: 123
5. Clique em "Pagar"
6. AGUARDE o processamento
```

**RESULTADO:** Pagamento aprovado! Saldo atualizado! ✅

### Teste C: Cartão Recusado

```
1. Clique em "Depositar"
2. Digite: R$ 20,00
3. Use o cartão:
   📋 Número: 4111 1111 1111 1234
   👤 Nome: TESTE RECUSADO
   📅 Mês: 12
   📅 Ano: 30
   🔒 CVV: 123
4. Clique em "Pagar"
```

**RESULTADO:** "Cartão recusado" (erro tratado) ❌

---

## 💳 CARTÕES DE TESTE SANDBOX

### ✅ Aprovado
```
Número: 4111 1111 1111 1111
Nome: TESTE APROVADO
Validade: 12/30
CVV: 123
```

### ❌ Recusado
```
Número: 4111 1111 1111 1234
Nome: TESTE RECUSADO
Validade: 12/30
CVV: 123
```

---

## 📊 VERIFICAR LOGS

Para ver o que está acontecendo:

```
1. Android Studio > Logcat
2. Filtre por: "PagBank"
3. Veja os logs em tempo real:
   - Criação de cobrança
   - QR Code gerado
   - Pagamento aprovado/recusado
```

---

## ✅ CHECKLIST

Marque conforme testa:

- [ ] Token configurado no PagBankClient.kt
- [ ] App compilou sem erros
- [ ] App instalado no dispositivo
- [ ] Abri a tela Carteira
- [ ] Testei depósito via PIX
- [ ] Vi o QR Code gerado
- [ ] Testei depósito via Cartão (aprovado)
- [ ] Vi o saldo atualizar
- [ ] Testei cartão recusado
- [ ] Vi a mensagem de erro

### 🎉 TODOS MARCADOS? PARABÉNS!

**SUA INTEGRAÇÃO PAGBANK ESTÁ FUNCIONANDO! ✅**

---

## 🐛 PROBLEMAS?

### Token não funciona
- Verifique se copiou corretamente
- Confirme que é token SANDBOX
- Tente gerar novo token

### QR Code não aparece
- Veja os logs no Logcat
- Verifique conexão internet
- Token pode estar inválido

### Cartão sempre recusado
- Use exatamente: 4111 1111 1111 1111
- Não adicione espaços extras
- CVV deve ter 3 dígitos

### App não compila
- Build > Clean Project
- Build > Rebuild Project
- Restart Android Studio

---

## 📱 O QUE VOCÊ TEM AGORA

✅ Sistema de pagamentos real  
✅ PIX com QR Code  
✅ Cartão de crédito  
✅ Validações completas  
✅ Saldo atualizado automaticamente  
✅ Tratamento de erros  
✅ Loading states  
✅ Ambiente de testes seguro  

---

## 🚀 PRÓXIMO NÍVEL

Quando estiver pronto:

1. **Webhooks** - Receber notificações de pagamento
2. **Produção** - Migrar para ambiente real
3. **Histórico** - Melhorar tela de transações
4. **QR Code** - Tela dedicada para PIX

---

## 📚 DOCUMENTAÇÃO

- **Completa:** `INTEGRACAO_PAGBANK_COMPLETA.md`
- **API PagBank:** https://dev.pagseguro.uol.com.br/
- **Cartões Teste:** https://dev.pagseguro.uol.com.br/reference/test-cards

---

**ESTÁ FUNCIONANDO? SHOW! 🎊**

**TEM DÚVIDAS? Leia a documentação completa! 📖**

**BOA SORTE NOS TESTES! 🍀**

