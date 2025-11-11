import os
import re

# Diretório das telas
screens_dir = r"C:\Users\24122303\StudioProjects\Facilita--Mobile--TCC\app\src\main\java\com\exemple\facilita\screens"

# Padrões para substituir
patterns = [
    # Dimensões dp
    (r'\.padding\((\d+)\.dp\)', r'.padding(\1.sdp())'),
    (r'\.padding\(horizontal\s*=\s*(\d+)\.dp\)', r'.padding(horizontal = \1.sdp())'),
    (r'\.padding\(vertical\s*=\s*(\d+)\.dp\)', r'.padding(vertical = \1.sdp())'),
    (r'\.padding\(start\s*=\s*(\d+)\.dp', r'.padding(start = \1.sdp()'),
    (r'\.padding\(end\s*=\s*(\d+)\.dp', r'.padding(end = \1.sdp()'),
    (r'\.padding\(top\s*=\s*(\d+)\.dp', r'.padding(top = \1.sdp()'),
    (r'\.padding\(bottom\s*=\s*(\d+)\.dp', r'.padding(bottom = \1.sdp()'),
    (r'\.height\((\d+)\.dp\)', r'.height(\1.sdp())'),
    (r'\.width\((\d+)\.dp\)', r'.width(\1.sdp())'),
    (r'\.size\((\d+)\.dp\)', r'.size(\1.sdp())'),
    (r'elevation\s*=\s*(\d+)\.dp', r'elevation = \1.sdp()'),
    (r'RoundedCornerShape\((\d+)\.dp\)', r'RoundedCornerShape(\1.sdp())'),
    (r'offset\(x\s*=\s*\(?-?(\d+)\)?\.dp', r'offset(x = \1.sdp()'),
    (r'offset\(y\s*=\s*\(?-?(\d+)\)?\.dp', r'offset(y = \1.sdp()'),

    # Tamanhos de texto sp
    (r'fontSize\s*=\s*(\d+)\.sp', r'fontSize = \1.ssp()'),
]

# Imports necessários
imports_to_add = """import com.exemple.facilita.utils.sdp
import com.exemple.facilita.utils.ssp"""

def process_file(filepath):
    """Processa um arquivo Kotlin aplicando dimensões responsivas"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        original_content = content

        # Aplicar substituições
        for pattern, replacement in patterns:
            content = re.sub(pattern, replacement, content)

        # Adicionar imports se necessário
        if '.sdp()' in content or '.ssp()' in content:
            # Verificar se já tem os imports
            if 'import com.exemple.facilita.utils.sdp' not in content:
                # Encontrar a posição dos imports
                import_match = re.search(r'(import .*?)\n\n', content, re.DOTALL)
                if import_match:
                    last_import_pos = import_match.end() - 2
                    content = content[:last_import_pos] + '\n' + imports_to_add + '\n' + content[last_import_pos:]

        # Salvar apenas se houve mudanças
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        return False
    except Exception as e:
        print(f"Erro ao processar {filepath}: {e}")
        return False

def main():
    """Processa todas as telas"""
    if not os.path.exists(screens_dir):
        print(f"Diretório não encontrado: {screens_dir}")
        return

    processed = 0
    skipped = 0

    # Telas já atualizadas manualmente
    skip_files = ['TelaHome.kt', 'TelaLogin.kt']

    for filename in os.listdir(screens_dir):
        if filename.endswith('.kt') and filename not in skip_files:
            filepath = os.path.join(screens_dir, filename)
            print(f"Processando: {filename}")

            if process_file(filepath):
                processed += 1
                print(f"  ✓ Atualizado")
            else:
                skipped += 1
                print(f"  - Sem mudanças")

    print(f"\n{'='*50}")
    print(f"Arquivos processados: {processed}")
    print(f"Arquivos sem mudanças: {skipped}")
    print(f"{'='*50}")

if __name__ == "__main__":
    main()

