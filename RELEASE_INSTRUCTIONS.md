# Instruções para Criar a Release 0.0.0

## 📋 Informações da Release

- **Tag**: `0.0.0`
- **Branch alvo**: `main`
- **Commit SHA**: `c4b00be4011fb867dd9cbf7629d090afb4d42a1a`
- **Tipo**: Initial Release
- **Data sugerida**: 12/10/2025

## 🚀 Opção 1: Via GitHub Web Interface (Recomendado)

### Passo a Passo

1. **Acesse a página de Releases**
   - Vá para: https://github.com/wesleyosantos91/wos-eats-lab/releases
   - Clique em "Draft a new release"

2. **Configure a Release**
   - **Choose a tag**: Digite `0.0.0` (será criada ao publicar)
   - **Target**: Selecione `main` branch
   - **Release title**: `Release 0.0.0 - Foundation Release` ou `Release 0.0.0 - Release Fundacional`

3. **Adicione a Descrição**
   - Copie o conteúdo de `RELEASE_NOTES_0.0.0_PT.md` para usuários brasileiros
   - Ou use `RELEASE_NOTES_0.0.0.md` para versão em inglês
   - Ou combine ambas as versões

4. **Opções Adicionais**
   - ✅ Marque "Set as the latest release"
   - ✅ Pode marcar "Create a discussion for this release" (opcional)
   - ⚠️ NÃO marque "Set as a pre-release" (esta é uma release estável)

5. **Publicar**
   - Clique em "Publish release"

## 🔧 Opção 2: Via Git Command Line

### Comandos

```bash
# 1. Certifique-se de estar na branch main atualizada
git checkout main
git pull origin main

# 2. Verifique que está no commit correto
git log -1
# Deve mostrar: c4b00be4011fb867dd9cbf7629d090afb4d42a1a

# 3. Crie a tag anotada
git tag -a 0.0.0 -m "Release 0.0.0 - Foundation Release

Complete cloud-native platform infrastructure with:
- Docker Compose stack (12 services)
- Observability 360° (Prometheus, Grafana, Loki, Tempo)
- Kubernetes + ArgoCD + Helm
- Complete documentation (20-sprint roadmap)
- Automation via Makefile (30+ targets)
- Sprint 0 complete: Bootstrap & Platform Base

See RELEASE_NOTES_0.0.0.md for full details."

# 4. Push a tag para o repositório remoto
git push origin 0.0.0

# 5. Crie a release no GitHub via gh CLI (se instalado)
gh release create 0.0.0 \
  --title "Release 0.0.0 - Foundation Release" \
  --notes-file RELEASE_NOTES_0.0.0_PT.md \
  --target main \
  --latest
```

## 📝 Opção 3: Via GitHub CLI (gh)

```bash
# Certifique-se de ter gh CLI instalado e autenticado
gh --version

# Crie a release diretamente
gh release create 0.0.0 \
  --title "Release 0.0.0 - Foundation Release / Release Fundacional" \
  --notes-file RELEASE_NOTES_0.0.0_PT.md \
  --target main \
  --latest

# Ou com a versão em inglês
gh release create 0.0.0 \
  --title "Release 0.0.0 - Foundation Release" \
  --notes-file RELEASE_NOTES_0.0.0.md \
  --target main \
  --latest
```

## ✅ Verificação Pós-Release

Após criar a release, verifique:

1. **Tag criada**
   ```bash
   git tag -l
   # Deve listar: 0.0.0
   ```

2. **Release publicada**
   - Acesse: https://github.com/wesleyosantos91/wos-eats-lab/releases
   - Verifique que a release 0.0.0 está visível
   - Confirme que está marcada como "Latest"

3. **Conteúdo da Release**
   - Título correto
   - Notas de release completas
   - Tag 0.0.0 presente
   - Target: main branch

## 📄 Conteúdo Sugerido para a Release

### Título (Português)
```
Release 0.0.0 - Release Fundacional
```

### Título (Inglês)
```
Release 0.0.0 - Foundation Release
```

### Descrição
Use o conteúdo completo de:
- `RELEASE_NOTES_0.0.0_PT.md` (para versão em português)
- `RELEASE_NOTES_0.0.0.md` (para versão em inglês)

Ou crie uma versão bilíngue combinando ambos.

## 🎯 Próximos Passos Após a Release

1. **Anunciar a Release**
   - Compartilhe nas redes sociais
   - Notifique colaboradores
   - Atualize documentação externa se houver

2. **Validar o Setup**
   ```bash
   # Clone com a tag
   git clone --branch 0.0.0 https://github.com/wesleyosantos91/wos-eats-lab.git
   cd wos-eats-lab
   
   # Siga o quick start
   cd platform-infra
   make setup
   make validate-all
   ```

3. **Preparar Sprint 1**
   - Iniciar desenvolvimento do Catalog Service
   - Criar nova branch de desenvolvimento
   - Seguir roadmap documentado

## 📚 Documentos de Referência

- `BRANCH_ACTIVITIES.md` - Detalhes técnicos completos
- `CHANGELOG.md` - Histórico de mudanças
- `RELEASE_NOTES_0.0.0.md` - Notas de release (EN)
- `RELEASE_NOTES_0.0.0_PT.md` - Notas de release (PT)
- `BRANCH_SUMMARY_PT.md` - Resumo executivo
- `README.md` - Documentação principal
- `platform-infra/README.md` - Guia de setup

## ⚠️ Notas Importantes

1. **Commit Correto**: A tag deve apontar para o commit `c4b00be` da branch main
2. **Branch Correta**: A release deve ser criada na branch `main`, não na branch atual
3. **Versionamento**: Esta é a versão 0.0.0 (initial release)
4. **Próxima Versão**: Seguir Semantic Versioning (0.1.0, 0.2.0, 1.0.0, etc.)

## 🆘 Troubleshooting

### Tag já existe
```bash
# Deletar tag local
git tag -d 0.0.0

# Deletar tag remota
git push origin :refs/tags/0.0.0

# Recriar a tag
git tag -a 0.0.0 -m "Release 0.0.0"
git push origin 0.0.0
```

### Erro de permissão
- Certifique-se de ter permissão de escrita no repositório
- Verifique autenticação do git/gh CLI
- Use token de acesso pessoal se necessário

### Release não aparece
- Aguarde alguns segundos (pode haver delay)
- Atualize a página
- Verifique em: https://github.com/wesleyosantos91/wos-eats-lab/tags

---

**Criado em**: 12/10/2025  
**Autor**: Copilot  
**Branch**: copilot/update-branch-description-and-release
