# ⚠️ Erro: Java 11 → Precisa Java 17

## O Problema
```
Android Gradle plugin requires Java 17 to run. You are currently using Java 11.
```

## Solução Rápida (No Android Studio)

### Opção 1: Configurar JDK no Android Studio
1. **Abra o Android Studio**
2. **Android Studio** → **Settings** (ou `Cmd + ,` no Mac)
3. Vá para: **Build, Execution, Deployment** → **Build Tools** → **Gradle**
4. Em **Gradle JDK**, selecione **Java 17** ou **Embedded JDK (17)**
5. Clique em **OK**
6. **File** → **Sync Project with Gradle Files**
7. Depois tente rodar o app novamente

### Opção 2: Baixar Java 17
Se não aparecer Java 17 nas opções:

1. **Baixe o JDK 17:**
   - https://www.oracle.com/java/technologies/downloads/#java17
   - Ou: https://adoptium.net/ (OpenJDK)

2. **Instale no Mac:**
   ```bash
   # Se baixou o .dmg, apenas execute-o
   # Ou via Homebrew:
   brew install openjdk@17
   ```

3. **Configure no Android Studio:**
   - Settings → Build Tools → Gradle
   - Gradle JDK → Add JDK
   - Navegue até: `/Library/Java/JavaVirtualMachines/jdk-17.x.x.jdk/Contents/Home`
   - Selecione e OK

## Verificar Java Instalado

```bash
# Ver versão atual
java -version

# Ver todos os JDKs instalados
/usr/libexec/java_home -V

# Trocar Java (temporário)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
java -version
```

## Alternativa: Ajustar gradle.properties

Se não quiser trocar o Java globalmente:

1. Abra: `Facilita--Mobile--TCC/gradle.properties`
2. Adicione a linha (com o caminho do seu Java 17):
   ```properties
   org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.x.x.jdk/Contents/Home
   ```
3. Salve e sincronize

## Testar no Terminal

```bash
cd /Users/24122303/AndroidStudioProjects/Facilita--Mobile--TCC

# Com Java 17 configurado
./gradlew assembleDebug
```

## ✅ Depois de Resolver

1. **Sync Gradle**
2. **Build** → **Clean Project**
3. **Build** → **Rebuild Project**
4. **Run App** (Shift + F10)

---

**Nota:** O código está 100% correto! É apenas uma questão de versão do Java.

