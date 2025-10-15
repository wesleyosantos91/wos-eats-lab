# K6 Performance Tests for Catalog Service

Este diretório contém testes de performance abrangentes para os domínios Kitchen e Restaurant usando K6.

## Estrutura dos Testes

```
k6-tests/
├── data/           # Dados de teste
│   ├── kitchens.json     # Dados de cozinhas para teste
│   └── restaurants.json  # Dados de restaurantes para teste
├── scripts/        # Scripts de teste
│   ├── load-test.js      # Teste de carga
│   ├── stress-test.js    # Teste de estresse
│   ├── spike-test.js     # Teste de pico
│   └── synthetic-test.js # Teste sintético
└── utils/          # Utilitários
    └── api-utils.js      # Funções auxiliares para APIs
```

## Tipos de Teste

### 1. Load Test (Teste de Carga)
- **Objetivo**: Validar performance sob carga normal esperada
- **Perfil**: 50 usuários concorrentes por 10 minutos
- **Cenários**: 
  - 30% navegação (listar cozinhas/restaurantes)
  - 20% criação de cozinhas
  - 20% criação de restaurantes
  - 15% consulta específica de cozinhas
  - 15% consulta específica de restaurantes
- **Thresholds**: 95% das requisições < 1s, taxa de erro < 10%

### 2. Stress Test (Teste de Estresse)
- **Objetivo**: Encontrar pontos de quebra do sistema
- **Perfil**: Carga crescente de 50 → 300 usuários
- **Cenários**: Operações CRUD agressivas, leituras concorrentes
- **Thresholds**: 95% das requisições < 2s, taxa de erro < 20%

### 3. Spike Test (Teste de Pico)
- **Objetivo**: Testar resiliência a picos súbitos de tráfego
- **Perfil**: Pico rápido para 100 usuários em 10s
- **Cenários**: Simulação de flash sales, conteúdo viral
- **Thresholds**: 95% das requisições < 3s, taxa de erro < 30%

### 4. Synthetic Test (Teste Sintético)
- **Objetivo**: Simular jornadas reais de usuários
- **Perfil**: 20 usuários por 10 minutos
- **Jornadas Simuladas**:
  - **Dono de Restaurante** (30%): Criar/atualizar restaurantes
  - **Gerente de Delivery** (30%): Planejamento de rotas, hubs
  - **Admin/Suporte** (25%): Resolução de tickets, auditoria
  - **Cliente** (15%): Descoberta e comparação

## Como Executar

### Pré-requisitos
1. **K6 instalado**: `winget install k6` (Windows)
2. **Aplicação rodando**: `mvn spring-boot:run` na porta 8090
3. **Banco de dados ativo**: PostgreSQL com dados de teste

### Comandos de Execução

```bash
# Navegue para o diretório do projeto
cd c:\dev\poc\wos-eats-lab\apps\java\catalog-service

# Teste de Carga
k6 run k6-tests/scripts/load-test.js

# Teste de Estresse
k6 run k6-tests/scripts/stress-test.js

# Teste de Pico
k6 run k6-tests/scripts/spike-test.js

# Teste Sintético
k6 run k6-tests/scripts/synthetic-test.js

# Executar com saída HTML
k6 run --out html=report.html k6-tests/scripts/load-test.js
```

### Configurações Personalizadas

```bash
# Executar com diferentes configurações
k6 run -e BASE_URL=http://localhost:9090 k6-tests/scripts/load-test.js

# Executar com mais usuários
k6 run --vus 100 --duration 5m k6-tests/scripts/load-test.js
```

## Parâmetros do K6 e Impacto na Execução

### Parâmetros Principais

#### `--vus` (Virtual Users)
```bash
k6 run --vus 50 k6-tests/scripts/load-test.js
```
- **Descrição**: Define o número de usuários virtuais concorrentes
- **Impacto**:
  - **Baixo (1-10)**: Teste de conectividade básica, latência mínima
  - **Médio (10-50)**: Carga realística, detecta gargalos de performance
  - **Alto (50+)**: Teste de estresse, pode saturar recursos do sistema
- **Recomendação**: Comece com 10 VUs e aumente gradualmente

#### `--duration` 
```bash
k6 run --duration 5m k6-tests/scripts/load-test.js
```
- **Descrição**: Tempo total de execução do teste
- **Formatos**: `30s`, `5m`, `1h30m`, `3600s`
- **Impacto**:
  - **Curto (30s-2m)**: Teste rápido, detecta problemas imediatos
  - **Médio (5-15m)**: Estabilização do sistema, detecção de memory leaks
  - **Longo (30m+)**: Teste de endurance, degradação ao longo do tempo
- **Recomendação**: 5-10 minutos para testes de carga, 30s para validações rápidas

#### `--iterations`
```bash
k6 run --iterations 1000 k6-tests/scripts/load-test.js
```
- **Descrição**: Número total de iterações ao invés de duração
- **Impacto**:
  - Controle preciso sobre volume de requisições
  - Útil para testes de volume específico
  - Não garante tempo de execução fixo
- **Uso**: Alternativa ao `--duration` quando você precisa de um número exato de execuções

### Parâmetros de Configuração Avançada

#### `--stage` (Ramping)
```bash
k6 run --stage 2m:10,5m:50,2m:0 k6-tests/scripts/load-test.js
```
- **Descrição**: Define perfis de carga escalonados
- **Formato**: `duration:target_vus`
- **Exemplo**: `2m:10` = aumentar para 10 VUs em 2 minutos
- **Impacto**:
  - Evita picos súbitos que podem mascarar problemas reais
  - Permite observar comportamento durante ramp-up e ramp-down
  - Simula cenários realísticos de crescimento de tráfego

#### `--max-vus`
```bash
k6 run --max-vus 200 --stage 1m:50,2m:100,1m:150 k6-tests/scripts/load-test.js
```
- **Descrição**: Limite máximo de VUs que podem ser executados
- **Impacto**: Previne overconsumption de recursos da máquina de teste
- **Recomendação**: Configure baseado na capacidade da máquina de teste

### Parâmetros de Saída e Relatórios

#### `--out` (Output)
```bash
# Relatório HTML
k6 run --out html=performance-report.html k6-tests/scripts/load-test.js

# JSON detalhado
k6 run --out json=results.json k6-tests/scripts/load-test.js

# InfluxDB (para monitoramento em tempo real)
k6 run --out influxdb=http://localhost:8086/k6 k6-tests/scripts/load-test.js

# CSV para análise
k6 run --out csv=metrics.csv k6-tests/scripts/load-test.js
```
- **Impacto**: 
  - HTML: Relatórios visuais para stakeholders
  - JSON: Análise programática e integração
  - InfluxDB: Monitoramento em tempo real com Grafana
  - CSV: Análise em Excel/Python

#### `--summary-trend-stats`
```bash
k6 run --summary-trend-stats="min,med,avg,p(95),p(99),max" k6-tests/scripts/load-test.js
```
- **Descrição**: Estatísticas customizadas no sumário final
- **Impacto**: Foco nas métricas mais relevantes para seu contexto

### Parâmetros de Controle e Debug

#### `--http-debug`
```bash
k6 run --http-debug k6-tests/scripts/load-test.js
```
- **Descrição**: Mostra todas as requisições HTTP em detalhes
- **Impacto**: 
  - ✅ Útil para debug de problemas específicos
  - ❌ Gera muito output, não use em testes longos
- **Uso**: Apenas durante desenvolvimento/troubleshooting

#### `--verbose` / `--quiet`
```bash
# Mais detalhes
k6 run --verbose k6-tests/scripts/load-test.js

# Apenas resultados essenciais
k6 run --quiet k6-tests/scripts/load-test.js
```
- **verbose**: Mostra logs detalhados de inicialização e execução
- **quiet**: Suprime logs, mantém apenas métricas finais
- **Uso**: `--quiet` em CI/CD, `--verbose` para debugging

#### `--no-setup` / `--no-teardown`
```bash
k6 run --no-setup k6-tests/scripts/load-test.js
```
- **Descrição**: Pula as fases de setup ou teardown
- **Impacto**: Útil para testes de iteração rápida durante desenvolvimento

### Parâmetros de Ambiente

#### `-e` (Environment Variables)
```bash
# URL diferente
k6 run -e BASE_URL=https://production.api.com k6-tests/scripts/load-test.js

# Configurações de teste
k6 run -e TEST_DURATION=10m -e MAX_VUS=100 k6-tests/scripts/load-test.js

# Dados de teste
k6 run -e USE_REAL_DATA=true -e DATA_SIZE=large k6-tests/scripts/load-test.js
```
- **Impacto**: 
  - Torna testes reutilizáveis entre ambientes
  - Permite configuração dinâmica sem alterar código
  - Facilita integração com CI/CD

### Parâmetros de Rate Limiting

#### `--rps`
```bash
k6 run --rps 100 k6-tests/scripts/load-test.js
```
- **Descrição**: Limita requisições por segundo
- **Impacto**:
  - Controla throughput independente do número de VUs
  - Útil para testar SLAs específicos
  - Evita sobrecarregar sistemas durante desenvolvimento

### Exemplos Práticos de Combinações

#### Teste de Desenvolvimento Rápido
```bash
k6 run --vus 2 --duration 30s --quiet k6-tests/scripts/simple-test.js
```
- **Uso**: Validação rápida após mudanças de código
- **Tempo**: ~30 segundos
- **Recurso**: Baixo impacto

#### Teste de Aceitação
```bash
k6 run --vus 10 --duration 2m --out html=acceptance-report.html k6-tests/scripts/load-test.js
```
- **Uso**: Validação antes de deploy
- **Tempo**: ~2 minutos
- **Output**: Relatório para equipe

#### Teste de Performance Completo
```bash
k6 run --stage 1m:10,5m:50,10m:50,1m:0 --out json=full-performance.json k6-tests/scripts/load-test.js
```
- **Uso**: Análise detalhada de performance
- **Tempo**: ~17 minutos
- **Análise**: Dados completos para otimização

#### Teste de Stress com Monitoramento
```bash
k6 run --stage 2m:50,5m:100,3m:200,2m:0 --out influxdb=http://localhost:8086/k6 --summary-trend-stats="avg,p(95),p(99),max" k6-tests/scripts/stress-test.js
```
- **Uso**: Encontrar limites do sistema
- **Monitoramento**: Tempo real via Grafana
- **Métricas**: Foco em percentis altos

### Impacto na Performance da Máquina de Teste

#### Recursos Consumidos por VUs
- **1-10 VUs**: ~10-50 MB RAM, CPU desprezível
- **50 VUs**: ~200-500 MB RAM, 10-20% CPU
- **100+ VUs**: 1+ GB RAM, 30%+ CPU

#### Limitações e Recomendações
```bash
# Para máquinas limitadas
k6 run --vus 20 --duration 5m k6-tests/scripts/load-test.js

# Para servidores dedicados
k6 run --max-vus 500 --stage 5m:100,10m:300,5m:500,5m:0 k6-tests/scripts/stress-test.js
```

### Scripts de Execução Recomendados

#### Script de Validação Diária
```bash
#!/bin/bash
echo "=== Teste de Conectividade ==="
k6 run --vus 5 --duration 30s --quiet k6-tests/scripts/simple-test.js

echo "=== Teste de Carga Básica ==="
k6 run --vus 20 --duration 2m --quiet k6-tests/scripts/load-test.js

echo "=== Teste de Jornadas de Usuário ==="
k6 run --vus 10 --duration 3m --quiet k6-tests/scripts/synthetic-test.js
```

#### Script de Release
```bash
#!/bin/bash
echo "=== Executando Suite Completa de Performance ==="

k6 run --out html=reports/load-test.html k6-tests/scripts/load-test.js
k6 run --out html=reports/stress-test.html k6-tests/scripts/stress-test.js  
k6 run --out html=reports/spike-test.html k6-tests/scripts/spike-test.js
k6 run --out html=reports/synthetic-test.html k6-tests/scripts/synthetic-test.js

echo "=== Relatórios gerados em /reports/ ==="
```

## Métricas Importantes

### Métricas de Performance
- **http_req_duration**: Tempo de resposta das requisições
- **http_req_failed**: Taxa de falha das requisições
- **http_reqs**: Total de requisições por segundo
- **vus**: Usuários virtuais ativos

### Métricas de Negócio
- **synthetic_user_journey_duration**: Duração das jornadas de usuário
- **api_success_rate**: Taxa de sucesso por operação
- **data_consistency**: Consistência dos dados criados/atualizados

## Interpretação dos Resultados

### ✅ Cenários de Sucesso
- Load Test: Todas as métricas dentro dos thresholds
- Stress Test: Sistema degrada graciosamente
- Spike Test: Recuperação rápida após o pico
- Synthetic Test: Jornadas completadas com sucesso

### ⚠️ Cenários de Atenção
- Aumento gradual no tempo de resposta
- Taxa de erro entre 5-10%
- Degradação em operações específicas

### ❌ Cenários Críticos
- Taxa de erro > 20%
- Timeout em operações críticas
- Falha na recuperação após stress
- Jornadas de usuário incompletas

## APIs Testadas

### Kitchen API
- `GET /v1/kitchens` - Listar cozinhas
- `GET /v1/kitchens/{id}` - Buscar cozinha
- `POST /v1/kitchens` - Criar cozinha
- `PUT /v1/kitchens/{id}` - Atualizar cozinha
- `DELETE /v1/kitchens/{id}` - Remover cozinha

### Restaurant API
- `GET /v1/restaurants` - Listar restaurantes
- `GET /v1/restaurants/{id}` - Buscar restaurante
- `POST /v1/restaurants` - Criar restaurante
- `PUT /v1/restaurants/{id}` - Atualizar restaurante
- `DELETE /v1/restaurants/{id}` - Remover restaurante

## Dados de Teste

### Kitchens (20 entradas)
Cozinhas variadas representando diferentes tipos culinários globais.

### Restaurants (20 entradas)
Restaurantes com nomes realistas e taxas de delivery variadas (R$ 3,99 - R$ 9,00).

## Monitoramento e Alertas

### Thresholds Configurados
- **Load**: p95 < 1s, erro < 10%
- **Stress**: p95 < 2s, erro < 20%
- **Spike**: p95 < 3s, erro < 30%

### Recomendações de Monitoramento
1. **Configurar alertas** para taxa de erro > 15%
2. **Monitorar logs** durante execução dos testes
3. **Verificar recursos** (CPU, memória, conexões DB)
4. **Analisar padrões** de degradação

## Troubleshooting

### Problemas Comuns
1. **Conexão recusada**: Verificar se aplicação está rodando na porta 8090
2. **Taxa de erro alta**: Verificar logs da aplicação e banco de dados
3. **Timeout**: Ajustar thresholds ou investigar performance queries
4. **Dados inconsistentes**: Verificar transações e rollbacks

### Logs Úteis
```bash
# Ver logs da aplicação durante teste
tail -f logs/catalog-service.log

# Monitorar conexões do banco
SELECT count(*) FROM pg_stat_activity;
```

---

## Integração com CI/CD

Os testes podem ser integrados em pipelines:

```yaml
# GitHub Actions exemplo
- name: Run Performance Tests
  run: |
    k6 run --quiet k6-tests/scripts/load-test.js
    k6 run --quiet k6-tests/scripts/synthetic-test.js
```

## Relatórios

Execute com `--out html=report.html` para gerar relatórios visuais detalhados.