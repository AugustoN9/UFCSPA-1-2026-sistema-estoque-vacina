# 🛡️ DOCUMENTAÇÃO DE ARQUITETURA DO SISTEMA — UFCSPA
**Versão de Engenharia:** v12.19  
**Disciplina:** Informática Biomédica  
**Autor:** Augusto Cristiano Crestani Lopez  
**Stack Tecnológica:** Java Desktop (Swing/AWT) | JDBC | MySQL 8.0  

---

## 1. INTRODUÇÃO E ESCOPO SANITÁRIO

O **Sistema de Gestão de Imunizantes** é uma plataforma arquitetada especificamente para atuar na governança computacional, blindagem regulamentar e gerenciamento preditivo de cadeias de suprimentos de vacinas e insumos biomédicos. 

O software resolve problemas complexos de logística de saúde pública, tais como:
1. **Risco de desabastecimento local** por falta de telemetria preditiva.
2. **Desperdício físico de insumos** decorrente da perda da cadeia de frio ou expiração de validades em prateleira.
3. **Ausência de integridade de dados** atuarial devido ao não cômputo do volume morto (perda técnica estrutural) de frascos multidoses.
4. **Fragilidade de governança**, mitigada pela implementação de controle granular e moderação hierárquica rigorosa de novos operadores.

---

## 2. ARQUITETURA DE SOFTWARE E PADRÕES DE PROJETO

O sistema adota o padrão arquitetural clássico **MVC (Model-View-Controller)** combinado com o padrão de persistência **DAO (Data Access Object)**, garantindo o desacoplamento entre as interfaces visuais, as regras de negócio distribuídas e a infraestrutura de dados.

### Componentes de Engenharia
* **Model (br.edu.ufcspa.estoque.model):** Classes POJO (Vacina, Colaborador) contendo o encapsulamento dos atributos do domínio sanitário e de auditoria.
* **View (br.edu.ufcspa.estoque.view):** Interfaces gráficas robustas projetadas nativamente com componentes Java Swing (JFrame, JPanel, JTable) preparadas para renderização limpa em Spooler de impressão.
* **Controller (br.edu.ufcspa.estoque.controller):** Centralizador do fluxo lógico (VacinaController). Intercepta eventos da View, injeta as barreiras regulamentares temporais de segurança e delega a persistência.
* **DAO (br.edu.ufcspa.estoque.dao):** Camada síncrona de persistência de dados (VacinaDAO, UsuarioDAO). Executa as transações JDBC estruturadas em blocos try-with-resources para prevenção de vazamento de memória (resource leaks).

---

## 3. MODELAGEM RECONSTITUÍDA DO BANCO DE DADOS (MySQL)

Para neutralizar erros em queries cruzadas (cross-table) executadas em sistemas operacionais híbridos, o banco de dados relacional foi configurado estritamente sob a codificação de caracteres e colação unificada:
* **Character Set:** utf8mb4
* **Collation:** utf8mb4_general_ci

### 3.1 Dicionário de Tabelas Críticas

#### Tabela: imunizante (Inventário Físico Analítico)
* id (INT, NOT NULL, PK, Auto-Increment): Identificador único do registro de lote.
* nome_comercial (VARCHAR(150), NOT NULL): Nome comercial indexado do imunizante.
* numero_lote (VARCHAR(50), NOT NULL, UNIQUE): Código físico do lote atribuído pelo fabricante.
* data_validade (VARCHAR(10), NOT NULL): Data limite de expiração regulamentar.
* quantidade_frascos_estoque (INT, YES, Default 0): Saldo físico de frascos disponíveis.
* calculo_total_doses (INT, YES): Quantidade de doses líquidas reais utilizáveis após a perda técnica.
* estoque_minimo_alerta (INT, YES): Limiar atuarial parametrizado pelo gestor para disparo do motor de compras.
* id_colaborador (INT, YES, FK): Código do operador que executou o inventário.

#### Tabela: solicitacao_compra (Central Logística de Reposição)
* id (INT, NOT NULL, PK, Auto-Increment): Identificador único da solicitação.
* nome_imunizante (VARCHAR(150), NOT NULL): Nome do produto demandado.
* quantidade_solicitada (INT, NOT NULL): Volume físico de frascos requisitados.
* prioridade_logistica (VARCHAR(30), NOT NULL): Classificação do risco (ATENÇÃO, CRÍTICO, URGENTE).
* diretriz_recomendada (VARCHAR(255), YES): Texto descritivo contendo a ação sugerida pelo painel.
* responsavel_nome (VARCHAR(100), NOT NULL): Identificação do usuário logado que autorizou o pedido.
* responsavel_nivel (VARCHAR(30), NOT NULL): Cargo corporativo do solicitante.
* status_solicitacao (VARCHAR(30), YES, Default 'PENDENTE'): Estado operacional da requisição.

---

## 4. REGRAS DE NEGÓCIO E ALGORITMOS EMBARCADOS

### 4.1 Cálculo Atuarial de Perda Técnica por Volume Morto
D_tot = F * D_p * (1 - P_t)

Onde:
* D_tot: Total de doses persistidas em calculo_total_doses.
* F: Quantidade física informada em quantidade_frascos_estoque.
* D_p: Número nominal de doses contidas por frasco.
* P_t: Fator de perda técnica (0.10 para frascos multidoses e 0.00 para monodoses).

### 4.2 Barreiras Temporais e Regulamentares de Cadastro
1. Validação Retroativa: Impede o salvamento de lotes com datas de validade inferiores à data atual do servidor.
2. Margem Útil de Utilização Mínima: Bloqueia o cadastro de lotes cuja janela temporal útil residual seja inferior a 30 dias a partir da data de recebimento.

---

## 5. MÓDULOS OPERACIONAIS E COMPORTAMENTO DAS INTERFACES

### 5.1 Autenticação e Central de Governança de Acessos
* Criação de Contas: Novos colaboradores solicitam credenciais detalhando nome completo, usuário, senha, formação, registro de conselho técnico e cargo.
* Perfis Hierárquicos Granulares: GEG (Gestão Executiva), ANL/ANB (Analistas), TEOP (Técnico Operacional) e TECH (Suporte Técnico).
* Quarentena de Segurança: Bloqueio de 7 dias para solicitações rejeitadas.

### 5.2 Dashboard Prescritivo e Emissão de Ordens Dinâmicas
O Dashboard calcula a soma consolidada de todos os lotes ativos por imunizante. Se o estoque mestre violar o limite seguro, ele acende um alerta. Um clique duplo abre uma janela modal para emissão imediata da ordem de compra.

