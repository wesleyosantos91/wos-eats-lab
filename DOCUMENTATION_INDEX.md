# 📚 Guia de Documentação do Projeto WOS Eats Lab

Este documento serve como índice e guia para toda a documentação do projeto.

## 🗂️ Estrutura da Documentação

### 📖 Documentação Principal

#### 1. READMEs Técnicos
- **[README.md](./README.md)** - Roadmap Unificado
  - Visão geral consolidada dos 3 pilares tecnológicos
  - Arquitetura geral do projeto
  - Cronograma completo de 20 sprints
  - Checklists transversais
  - ~350 linhas

- **[README-GO.md](./README-GO.md)** - Roadmap Microsserviços Go
  - 15 sprints de desenvolvimento Go
  - Padrões Go específicos
  - Stack: Go 1.23+, Linkerd, KEDA
  - Clean Architecture e padrões de resiliência
  - ~240 linhas

- **[README-JAVA.md](./README-JAVA.md)** - Roadmap Microsserviços Java
  - 15 sprints de desenvolvimento Java
  - Spring Boot 3.5.6
  - Resilience4j, Spring Kafka
  - DDD e Arquitetura Hexagonal
  - ~250 linhas

- **[README-PYTHON.md](./README-PYTHON.md)** - Roadmap Engenharia de Dados
  - 6 sprints de Data Engineering
  - AWS Glue, Athena, Redshift
  - Arquitetura Bronze-Silver-Gold
  - DataOps e governança
  - ~190 linhas

#### 2. Documentação de Infraestrutura
- **[platform-infra/README.md](./platform-infra/README.md)** - Guia de Plataforma
  - Setup completo do ambiente
  - Descrição de todos os serviços
  - Troubleshooting
  - Endpoints e credenciais
  - 487 linhas de guia detalhado

### 📋 Documentação de Release e Atividades

#### 3. Changelog e Versionamento
- **[CHANGELOG.md](./CHANGELOG.md)** - Histórico de Mudanças
  - Seguindo formato Keep a Changelog
  - Versionamento semântico
  - Release 0.0.0 documentada
  - Template para futuras releases

#### 4. Notas de Release
- **[RELEASE_NOTES_0.0.0.md](./RELEASE_NOTES_0.0.0.md)** - Release Notes (English)
  - Documentação completa da versão 0.0.0
  - Quick start guide
  - Arquitetura e stack técnica
  - Roadmap de aprendizado
  - ~370 linhas

- **[RELEASE_NOTES_0.0.0_PT.md](./RELEASE_NOTES_0.0.0_PT.md)** - Release Notes (Português)
  - Versão em português das notas de release
  - Conteúdo traduzido e adaptado
  - Mesmo nível de detalhe que versão EN
  - ~390 linhas

#### 5. Documentação da Branch
- **[BRANCH_ACTIVITIES.md](./BRANCH_ACTIVITIES.md)** - Relatório de Atividades
  - Análise detalhada de todas as mudanças
  - 28 arquivos criados/modificados
  - Estatísticas completas
  - DoD do Sprint 0
  - ~340 linhas

- **[BRANCH_SUMMARY_PT.md](./BRANCH_SUMMARY_PT.md)** - Resumo Executivo
  - Resumo das atividades em português
  - Overview do projeto
  - Próximos passos
  - Links úteis
  - ~200 linhas

#### 6. Instruções de Release
- **[RELEASE_INSTRUCTIONS.md](./RELEASE_INSTRUCTIONS.md)** - Guia para Criar Release
  - 3 métodos diferentes (GitHub UI, Git CLI, gh CLI)
  - Passo a passo detalhado
  - Verificação pós-release
  - Troubleshooting
  - ~210 linhas

### 🔧 Documentação Técnica de Infraestrutura

#### 7. Configurações Docker
- **[platform-infra/docker/docker-compose.yml](./platform-infra/docker/docker-compose.yml)** - Stack Completo
  - 12 serviços containerizados
  - Configurações de rede e volumes
  - Variáveis de ambiente
  - 259 linhas

#### 8. Configurações de Observabilidade
- `platform-infra/docker/otel/config.yaml` - OpenTelemetry Collector
- `platform-infra/docker/prometheus/prometheus.yml` - Prometheus
- `platform-infra/docker/grafana/provisioning/` - Grafana datasources e dashboards
- `platform-infra/docker/loki/config.yaml` - Loki
- `platform-infra/docker/tempo/tempo.yaml` - Tempo
- `platform-infra/docker/blackbox/blackbox.yml` - Blackbox Exporter

#### 9. Configurações Kubernetes
- `platform-infra/k8s/kind/kind-config.yaml` - Cluster kind
- `platform-infra/k8s/kind/apps/hello/` - Aplicação de exemplo
- `platform-infra/k8s/argo/` - Configurações ArgoCD

#### 10. Infraestrutura como Código
- `platform-infra/terraform/` - Configurações Terraform
  - providers.tf
  - variables.tf
  - outputs.tf
  - main.tf

#### 11. Automação
- **[platform-infra/Makefile](./platform-infra/Makefile)** - Automação Completa
  - 30+ targets
  - Gestão de ciclo de vida
  - Validações
  - 362 linhas

## 🎯 Como Usar Esta Documentação

### Para Iniciantes
1. Comece com **[README.md](./README.md)** para visão geral
2. Leia **[RELEASE_NOTES_0.0.0_PT.md](./RELEASE_NOTES_0.0.0_PT.md)** para entender a release atual
3. Siga o **[platform-infra/README.md](./platform-infra/README.md)** para setup
4. Escolha um roadmap específico (Go, Java ou Python)

### Para Desenvolvedores
1. **[README-GO.md](./README-GO.md)** ou **[README-JAVA.md](./README-JAVA.md)** - escolha sua linguagem
2. **[platform-infra/README.md](./platform-infra/README.md)** - configure o ambiente
3. **[platform-infra/Makefile](./platform-infra/Makefile)** - comandos disponíveis
4. Documentação específica dos serviços em `platform-infra/docker/`

### Para Engenheiros de Dados
1. **[README-PYTHON.md](./README-PYTHON.md)** - roadmap completo
2. **[README.md](./README.md)** - contexto geral
3. Sprints 15-20 para sequência de aprendizado

### Para DevOps/SRE
1. **[platform-infra/README.md](./platform-infra/README.md)** - infraestrutura completa
2. **[platform-infra/Makefile](./platform-infra/Makefile)** - automação
3. Configurações em `platform-infra/docker/` e `platform-infra/k8s/`
4. **[README.md](./README.md)** - arquitetura e observabilidade

### Para Gestores/Tech Leads
1. **[RELEASE_NOTES_0.0.0_PT.md](./RELEASE_NOTES_0.0.0_PT.md)** - visão da release
2. **[README.md](./README.md)** - roadmap e objetivos
3. **[BRANCH_SUMMARY_PT.md](./BRANCH_SUMMARY_PT.md)** - resumo executivo
4. **[CHANGELOG.md](./CHANGELOG.md)** - histórico de mudanças

## 📊 Estatísticas da Documentação

### Total de Documentação
- **~3.500 linhas** de documentação
- **6 documentos principais** (READMEs)
- **5 documentos de release/atividades**
- **1 guia de instruções**
- **20+ arquivos de configuração** documentados

### Por Categoria
- **Roadmaps**: ~1.030 linhas (README.md + README-*.md)
- **Infraestrutura**: ~487 linhas (platform-infra/README.md)
- **Release Notes**: ~760 linhas (ambas versões)
- **Atividades**: ~540 linhas (BRANCH_ACTIVITIES + BRANCH_SUMMARY)
- **Changelog**: ~120 linhas
- **Instruções**: ~210 linhas
- **Configurações**: ~500 linhas (docker, k8s, terraform configs)

## 🔍 Busca Rápida

### Precisa de...?

#### Setup e Instalação
→ [platform-infra/README.md](./platform-infra/README.md)

#### Roadmap Completo
→ [README.md](./README.md)

#### Comandos Make
→ [platform-infra/Makefile](./platform-infra/Makefile)

#### Notas da Release Atual
→ [RELEASE_NOTES_0.0.0_PT.md](./RELEASE_NOTES_0.0.0_PT.md)

#### Histórico de Mudanças
→ [CHANGELOG.md](./CHANGELOG.md)

#### Criar Nova Release
→ [RELEASE_INSTRUCTIONS.md](./RELEASE_INSTRUCTIONS.md)

#### Roadmap Go
→ [README-GO.md](./README-GO.md)

#### Roadmap Java
→ [README-JAVA.md](./README-JAVA.md)

#### Roadmap Python/Data
→ [README-PYTHON.md](./README-PYTHON.md)

#### Configuração Docker
→ [platform-infra/docker/docker-compose.yml](./platform-infra/docker/docker-compose.yml)

#### Detalhes Técnicos da Branch
→ [BRANCH_ACTIVITIES.md](./BRANCH_ACTIVITIES.md)

## 🎓 Fluxo de Aprendizado Sugerido

### Semana 1: Fundação
1. Ler README.md (visão geral)
2. Setup do ambiente (platform-infra/README.md)
3. Explorar serviços e dashboards

### Semana 2: Sprint 0
1. Validar todos os serviços
2. Explorar observabilidade (Grafana, Prometheus)
3. Testar comandos Make

### Semana 3+: Sprints Específicos
1. Escolher track (Go, Java ou Python)
2. Seguir roadmap correspondente
3. Implementar serviços incrementalmente

## 🔗 Links Externos Úteis

### Padrões e Práticas
- [Microservices.io](https://microservices.io/) - Padrões de microsserviços
- [12-Factor App](https://12factor.net/) - Metodologia
- [CNCF Landscape](https://landscape.cncf.io/) - Tecnologias cloud-native

### Tecnologias
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Go](https://go.dev/)
- [Python](https://www.python.org/)
- [Kubernetes](https://kubernetes.io/)
- [ArgoCD](https://argo-cd.readthedocs.io/)
- [Prometheus](https://prometheus.io/)
- [OpenTelemetry](https://opentelemetry.io/)

### AWS
- [AWS Glue](https://aws.amazon.com/glue/)
- [AWS Athena](https://aws.amazon.com/athena/)
- [AWS Step Functions](https://aws.amazon.com/step-functions/)

## 📝 Manutenção da Documentação

### Atualizações Necessárias Quando:
- ✅ Nova sprint iniciada → Atualizar README correspondente
- ✅ Nova release → Atualizar CHANGELOG.md e criar RELEASE_NOTES
- ✅ Novo serviço → Atualizar platform-infra/README.md
- ✅ Nova configuração → Documentar em arquivo correspondente
- ✅ Mudança de arquitetura → Atualizar README.md

### Padrão de Documentação
- Usar Markdown
- Incluir emojis para melhor navegação
- Manter código formatado
- Incluir exemplos práticos
- Manter índices atualizados

---

**Última Atualização**: 12/10/2025  
**Versão da Documentação**: 0.0.0  
**Mantenedor**: @wesleyosantos91
