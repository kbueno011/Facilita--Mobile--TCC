# 🔑 Como Obter o SHA-1 do Projeto Android

## Método 1: Via Terminal (Recomendado)

### No macOS/Linux:
```bash
cd /Users/24122303/AndroidStudioProjects/Facilita--Mobile--TCC

# Para Debug
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Para Release (se já tiver keystore)
keytool -list -v -keystore /caminho/para/seu/keystore.jks -alias seu_alias
```

### Saída Esperada:
```
Certificate fingerprints:
	 SHA1: 5E:8F:16:06:2E:A3:CD:2C:4A:0D:54:78:76:BA:A6:F3:8C:AB:F6:25
	 SHA-256: FA:C6:17:45:DC:09:03:78:6F:B9:ED:E6:2A:96:2B:39:9F:73:48:F0:BB:6F:89:9B:83:32:66:75:91:03:3B:9C
```

## Método 2: Via Android Studio

1. **Abrir Gradle Tool Window**
   - View → Tool Windows → Gradle
   - Ou clique no ícone do elefante (Gradle) na lateral direita

2. **Executar signingReport**
   ```
   Facilita--Mobile--TCC
   └── app
       └── Tasks
           └── android
               └── signingReport (clique duplo)
   ```

3. **Ver Output**
   - A saída aparecerá no painel "Run" na parte inferior
   - Procure por "SHA1" e copie o valor

## Método 3: Via Gradlew (Terminal no Projeto)

```bash
cd /Users/24122303/AndroidStudioProjects/Facilita--Mobile--TCC

# No macOS/Linux
./gradlew signingReport

# No Windows
gradlew.bat signingReport
```

## O Que Fazer com o SHA-1?

### 1. Google Maps API
1. Acesse: https://console.cloud.google.com/
2. Vá para "APIs & Services" → "Credentials"
3. Edite sua API Key do Maps
4. Em "Restrict usage to your Android apps", adicione:
   ```
   Package name: com.exemple.facilita
   SHA-1: (cole seu SHA-1 aqui)
   ```

### 2. Google Places API
- Use o mesmo processo acima
- Certifique-se que Places API está habilitada

### 3. Firebase (se usar)
1. Acesse Firebase Console
2. Project Settings
3. Add Fingerprint
4. Cole o SHA-1

## Exemplo de SHA-1 Debug (Keystore Padrão)

**Localização do debug.keystore:**
```
macOS/Linux: ~/.android/debug.keystore
Windows: C:\Users\[seu-usuario]\.android\debug.keystore
```

**Credenciais Padrão:**
- Keystore password: `android`
- Key alias: `androiddebugkey`
- Key password: `android`

## SHA-1 no seu projeto

Execute o comando e salve o resultado:

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1
```

**Anote o SHA-1 aqui:**
```
SHA1: ________________________________________
```

## Troubleshooting

### "keytool: command not found"
**Solução:** Adicione o Java ao PATH
```bash
export PATH=$PATH:/Library/Java/JavaVirtualMachines/jdk-XX.jdk/Contents/Home/bin
```

### "keystore file does not exist"
**Solução:** Execute o app uma vez no emulador/dispositivo para gerar o keystore

### "Gradle task not found"
**Solução:** 
```bash
# Limpe e reconstrua
./gradlew clean
./gradlew assembleDebug
./gradlew signingReport
```

## Configuração Atual do Projeto

### AndroidManifest.xml
Certifique-se de ter a API Key configurada:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyBKFwfrLdbTreqsOwnpMS9-zt9KD-HEH28"/>
```

### MainActivity.kt
Inicialização do Places:
```kotlin
if (!Places.isInitialized()) {
    Places.initialize(applicationContext, "AIzaSyBKFwfrLdbTreqsOwnpMS9-zt9KD-HEH28")
}
```

## Verificar se está Funcionando

### Teste Google Maps
1. Execute o app
2. Navegue até `TelaRastreamentoServico`
3. Se o mapa carregar → SHA-1 está correto ✅
4. Se aparecer erro → Verifique SHA-1 no Console

### Teste Google Places
1. Execute o app
2. Navegue até `TelaCriarServicoCategoria`
3. Digite um endereço
4. Se aparecer sugestões → SHA-1 está correto ✅
5. Se não aparecer → Verifique SHA-1 no Console

## Comandos Rápidos

```bash
# Ver SHA-1 debug
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1

# Copiar SHA-1 para clipboard (macOS)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1 | pbcopy

# Ver todos os certificados
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

---

**Importante:** Sempre use o SHA-1 correto para cada ambiente:
- **Debug**: Use o SHA-1 do `debug.keystore`
- **Release**: Use o SHA-1 do seu keystore de produção

