# 🧪 Guia de Teste - TelaMontarServico

## ✅ Checklist de Testes

### 1. **Teste de Interface**

#### ✅ Cores dos Ícones
- [ ] Ícone de origem é **verde** 🟢
- [ ] Ícone de destino é **verde** 🟢
- [ ] Linha vertical é **cinza claro**

#### ✅ Linha Vertical
- [ ] **Sem paradas**: linha conecta origem → destino
- [ ] **Com 1 parada**: linha passa pela parada
- [ ] **Com 2 paradas**: linha passa por ambas
- [ ] **Com 3 paradas**: linha passa por todas
- [ ] Linha sempre chega até o destino

---

### 2. **Teste de Autocomplete**

#### ✅ Campo Origem
1. Digite "Av. Paulista" no campo origem
2. Sugestões aparecem após 2 caracteres? ✅
3. Clique em uma sugestão
4. Campo é preenchido automaticamente? ✅

#### ✅ Campo Paradas
1. Clique em "Adicionar parada"
2. Digite um endereço na parada
3. Sugestões aparecem? ✅
4. Selecione uma sugestão
5. Campo é preenchido? ✅
6. Clique no X ao lado da parada
7. Parada é removida? ✅

#### ✅ Campo Destino
1. Digite um endereço no destino
2. Sugestões aparecem? ✅
3. Selecione uma sugestão
4. Campo é preenchido? ✅

---

### 3. **Teste de Validações**

#### ✅ Campos Vazios
1. Deixe origem vazia
2. Clique em "Confirmar Serviço"
3. **Esperado**: Toast "Preencha origem e destino"

#### ✅ Descrição Vazia
1. Preencha origem e destino
2. Deixe descrição vazia
3. Clique em "Confirmar Serviço"
4. **Esperado**: Toast "Preencha a descrição do serviço"

#### ✅ Endereço Digitado (não selecionado)
1. Digite endereço manualmente (sem clicar em sugestão)
2. Clique em "Confirmar Serviço"
3. **Esperado**: Toast "Selecione os endereços das sugestões"

---

### 4. **Teste de Integração com API**

#### ✅ Envio Sem Paradas
**Passos:**
1. Faça login primeiro (para ter token)
2. Selecione origem: "Av. Paulista, 1000"
3. Selecione destino: "Rua Augusta, 500"
4. Preencha descrição: "Teste de integração"
5. Clique em "Confirmar Serviço"

**Esperado:**
- [ ] Botão mostra loading (CircularProgressIndicator)
- [ ] Toast "Serviço criado com sucesso! ID: X"
- [ ] Navega para tela_home
- [ ] Serviço aparece no backend

**Verificar no Log:**
```
Log: Serviço enviado com sucesso
```

#### ✅ Envio Com 1 Parada
**Passos:**
1. Selecione origem
2. Clique "Adicionar parada"
3. Selecione endereço para parada
4. Selecione destino
5. Preencha descrição
6. Clique "Confirmar Serviço"

**Esperado:**
- [ ] Parada é incluída no request
- [ ] Coordenadas da parada são buscadas
- [ ] Serviço criado com sucesso

#### ✅ Envio Com 3 Paradas
**Passos:**
1. Adicione 3 paradas
2. Preencha todos os campos
3. Clique "Confirmar Serviço"

**Esperado:**
- [ ] Todas as 3 paradas são enviadas
- [ ] Request está correto

---

### 5. **Teste de Erros**

#### ✅ Sem Token (não logado)
**Passos:**
1. Limpe SharedPreferences (ou use app sem login)
2. Tente criar serviço

**Esperado:**
- [ ] Mensagem: "Token de autenticação não encontrado. Faça login novamente."

#### ✅ Erro da API (500, 400, etc)
**Passos:**
1. Force um erro (ex: categoria inválida)
2. Clique "Confirmar Serviço"

**Esperado:**
- [ ] Toast mostra código do erro
- [ ] Mensagem de erro vermelha aparece

#### ✅ Sem Internet
**Passos:**
1. Desative WiFi/Dados
2. Tente criar serviço

**Esperado:**
- [ ] Toast com mensagem de erro
- [ ] App não trava

---

### 6. **Teste de UX**

#### ✅ Responsividade
- [ ] Layout se adapta ao número de paradas
- [ ] Scroll funciona quando tem muitas paradas
- [ ] Botões são clicáveis
- [ ] Campos são focáveis

#### ✅ Estados Visuais
- [ ] Campo focado tem borda verde
- [ ] Campo sem foco tem borda cinza
- [ ] Loading desabilita botão
- [ ] Sugestões aparecem em card flutuante

---

## 🔍 Como Verificar se Funcionou

### No Android Studio (Logcat)
Filtro: `PlacesAPI` ou `API_ERROR`

**Logs esperados:**
```
✅ Places API inicializada
✅ Coordenadas encontradas: lat=-23.550520, lng=-46.633308
✅ Serviço enviado com sucesso
```

**Logs de erro:**
```
❌ Error: 401 - Token inválido
❌ Error: 400 - Campos inválidos
❌ Exception: Network error
```

---

## 📱 Teste Completo End-to-End

### Cenário 1: Entrega Simples
```
1. Login → tela_home
2. Navega para tela_endereco
3. Seleciona local no mapa
4. Cadastra endereço
5. Vai para tela_montar_servico
6. Preenche origem (já vem preenchida)
7. Seleciona destino
8. Preenche descrição
9. Confirma serviço
10. ✅ Volta para home
```

### Cenário 2: Entrega com Paradas
```
1. Login → tela_home
2. Vai para tela_montar_servico
3. Seleciona origem
4. Adiciona 2 paradas
5. Preenche ambas as paradas
6. Seleciona destino
7. Preenche descrição
8. Confirma serviço
9. ✅ Serviço criado com 2 paradas
```

### Cenário 3: Correção de Erro
```
1. Preenche formulário
2. Esquece de selecionar sugestão (digita manualmente)
3. Tenta confirmar
4. ✅ Recebe aviso
5. Seleciona sugestão corretamente
6. Confirma novamente
7. ✅ Sucesso
```

---

## 🐛 Problemas Conhecidos e Soluções

### Problema: "Selecione os endereços das sugestões"
**Causa**: Usuário digitou endereço manualmente sem clicar na sugestão  
**Solução**: Sempre clicar em uma sugestão do dropdown

### Problema: Loading infinito
**Causa**: Erro na busca de coordenadas  
**Solução**: Verificar se Places API está ativa no Google Cloud

### Problema: "Token não encontrado"
**Causa**: Usuário não está logado  
**Solução**: Fazer login novamente

### Problema: Erro 401
**Causa**: Token expirado  
**Solução**: Fazer login novamente

---

## ✅ Testes Aprovados

Marque conforme testar:

- [ ] ✅ Cores dos ícones (verde)
- [ ] ✅ Linha conecta até destino
- [ ] ✅ Autocomplete funciona
- [ ] ✅ Adicionar/remover paradas
- [ ] ✅ Validações funcionam
- [ ] ✅ Loading aparece
- [ ] ✅ API recebe dados corretos
- [ ] ✅ Navegação após sucesso
- [ ] ✅ Tratamento de erros

---

**🎉 Se todos os testes passarem, a integração está completa!**

