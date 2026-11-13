# AcessoLivre ♿

**Global Solution FIAP — 2º semestre de 2026 | CitizenTech**  
**Backend Java — Java Advanced**

> Plataforma colaborativa de informações sobre acessibilidade em locais e equipamentos. O AcessoLivre reúne relatos de cidadãos e avisos operacionais e utiliza IA para produzir resumos claros, sem apresentar registros como monitoramento em tempo real.

## 1. Problema e solução

Pessoas que dependem de elevadores, rampas, plataformas elevatórias e outros recursos de acessibilidade podem encontrar problemas no deslocamento quando não possuem informações suficientes sobre a condição desses equipamentos.

O **AcessoLivre** permite cadastrar locais e seus recursos, receber relatos de cidadãos, registrar avisos operacionais de responsáveis e consultar um resumo produzido com IA generativa a partir das informações disponíveis no banco.

**Importante:** o sistema **não** verifica equipamentos fisicamente nem monitora seu funcionamento em tempo real. Um relato ou uma previsão de retorno não confirma, por si só, a situação atual de um recurso.

## 2. Tecnologias

| Área | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Backend | Spring Boot 4.1.2, Spring MVC |
| Persistência | Spring Data JPA, Hibernate, MySQL 8.0 |
| Segurança | Spring Security, JWT HS256, BCrypt, perfis de acesso |
| Validação | Bean Validation e `@RestControllerAdvice` |
| Inteligência artificial | Spring AI 2.0.2 + Google Gemini |
| Mensageria | RabbitMQ 4, Spring AMQP |
| Documentação da API | Swagger/OpenAPI (Springdoc) |
| Testes | JUnit 5, Mockito, Gradle |
| Desenvolvimento | Gradle Wrapper, Docker Compose, Azure DevOps Repos/Boards |

## 3. Arquitetura e modelagem

A aplicação utiliza uma arquitetura em camadas:

- **Controllers:** endpoints HTTP e entrada dos DTOs.
- **Services:** regras de negócio, autorização e transações.
- **Repositories:** persistência com Spring Data JPA.
- **Entities:** domínio e relacionamentos.
- **Security:** login, emissão e validação de JWT.
- **Evento:** publicação/consumo de mensagens RabbitMQ.
- **IA:** geração de resumo a partir dos dados de relatos e avisos.

```mermaid
flowchart LR
  APP[Cliente Web / Mobile] --> API[API REST Spring Boot]
  API --> SEG[Spring Security / JWT]
  API --> JPA[Services e Spring Data JPA]
  JPA --> MYSQL[(MySQL)]
  API --> IA[Spring AI]
  IA --> GEMINI[Google Gemini]
  JPA --> PUB[Publicador de relatos]
  PUB --> MQ[(RabbitMQ)]
  MQ --> CON[Consumidor assíncrono]
  CON --> MYSQL
```

### Entidades principais

| Entidade | Responsabilidade | Relacionamentos principais |
|---|---|---|
| `Usuario` | Identidade, e-mail, senha protegida e perfil | Autor de relatos/avisos e responsável por locais |
| `Local` | Estabelecimento ou ponto de interesse | Possui recursos; pode ter responsável |
| `RecursoAcesso` | Equipamento ou infraestrutura de acessibilidade | Pertence a um local |
| `Relato` | Observação de cidadão sobre um recurso | Autor (`Usuario`) e `RecursoAcesso` |
| `AvisoOperacional` | Informação operacional publicada sobre um recurso | Autor (`Usuario`) e `RecursoAcesso` |

Uma entidade técnica adicional, `EventoRelatoProcessado`, registra o processamento de eventos recebidos via RabbitMQ.

## 4. Como executar localmente

### Pré-requisitos

- JDK **17**;
- Docker Desktop com Docker Compose;
- Git;
- Acesso à internet para baixar as dependências do Gradle;
- Opcional: IntelliJ IDEA, VS Code, Insomnia ou Postman;
- Para a funcionalidade de IA: chave de API válida do Google Gemini.

### 4.1. Obter o projeto

```bash
git clone <URL_DO_REPOSITORIO_PUBLICO_OU_AUTORIZADO>
cd AcessoLivre
```

> Substitua o endereço acima pelo URL definitivo do repositório. O link público do GitHub ainda precisa ser inserido antes da entrega.

### 4.2. Configurar o `.env` do Docker

Crie na **raiz do projeto**, ao lado de `compose.yaml`, o arquivo `.env`:

```dotenv
MYSQL_ROOT_PASSWORD=SUBSTITUA_POR_SENHA_FORTE_LOCAL
MYSQL_PASSWORD=SUBSTITUA_POR_OUTRA_SENHA_FORTE_LOCAL
RABBITMQ_USER=acessolivre
RABBITMQ_PASSWORD=SUBSTITUA_POR_SENHA_FORTE_LOCAL
```

Use valores reais e particulares no seu computador. **Nunca versionar ou compartilhar o `.env`, chaves de IA, tokens ou senhas.** A senha definida para o MySQL na inicialização do volume deve ser a mesma fornecida à API.

### 4.3. Iniciar MySQL e RabbitMQ

```bash
docker compose up -d
docker compose ps
```

Serviços locais definidos no `compose.yaml`:

| Serviço | Endereço ou porta |
|---|---|
| MySQL | `localhost:3307` (porta interna 3306) |
| RabbitMQ AMQP | `localhost:5672` |
| RabbitMQ Management | `http://localhost:15672` |

O painel do RabbitMQ utiliza `RABBITMQ_USER` e `RABBITMQ_PASSWORD` do ambiente local. Os dados do MySQL e do RabbitMQ são preservados em volumes Docker. **Evite `docker compose down -v` se desejar manter esses dados.**

### 4.4. Preparar as variáveis da aplicação

| Variável | Uso |
|---|---|
| `MYSQL_PASSWORD` | Senha do usuário `acessolivre_user` no MySQL |
| `RABBITMQ_USER` | Usuário do broker RabbitMQ |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ |
| `JWT_SECRET` | Chave aleatória em Base64, com pelo menos 32 bytes após decodificação |
| `AI_CHAT_PROVIDER` | `google-genai` para habilitar Gemini; `none` para desabilitar a geração com modelo |
| `GEMINI_API_KEY` | Chave privada da API do Google Gemini |
| `GEMINI_MODEL` | Modelo Gemini configurável; verificar disponibilidade na sua conta |
| `RABBITMQ_HOST` | Opcional, padrão `localhost` |
| `RABBITMQ_PORT` | Opcional, padrão `5672` |

**Gerar `JWT_SECRET` no PowerShell (Windows):**

```powershell
$b = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($b)
$rng.Dispose()
[Convert]::ToBase64String($b)
```

Guarde o resultado em local privado; o exemplo acima não é uma chave reutilizável. Alterar a chave invalida os JWT emitidos anteriormente.

No **IntelliJ**, use **Run → Edit Configurations → Environment variables** na configuração de execução da aplicação. As variáveis definidas nessa configuração **não são automaticamente transferidas** para um Git Bash ou PowerShell separado.

Se preferir iniciar pelo Git Bash, exporte as mesmas variáveis nessa sessão antes do `bootRun`; não cole senhas em comandos públicos ou arquivos versionados.

### 4.5. Iniciar a API

No **Git Bash** do Windows:

```bash
./gradlew.bat bootRun
```

Em Linux/macOS:

```bash
./gradlew bootRun
```

A API local escuta em `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

> O banco local está configurado para `jdbc:mysql://localhost:3307/acessolivre_db`, com `spring.jpa.hibernate.ddl-auto=update`. Essa é uma configuração **de desenvolvimento**; a nuvem precisa de configuração própria de conexão, credenciais e criação controlada do esquema.

## 5. Autenticação e permissões

Existem três perfis: `CIDADAO`, `RESPONSAVEL` e `ADMIN`.

- O cadastro público cria contas com perfil **`CIDADAO`**; o usuário não escolhe o próprio perfil.
- O login retorna um **JWT** com validade de 60 minutos. A identificação (`sub`) utiliza o **ID imutável do usuário**, e não seu e-mail.
- O Spring consulta o perfil atual no banco para autorizar operações protegidas.
- Administradores gerenciam locais e podem atribuir um responsável a um local.
- Responsáveis gerenciam avisos operacionais conforme o vínculo com seus locais.
- Cidadãos podem registrar relatos; a atualização/exclusão respeita autoria ou privilégios de administrador.

Envie o token em operações protegidas:

```http
Authorization: Bearer <SEU_TOKEN_JWT>
```

Não há endpoint público para promover usuários a `ADMIN`. O **administrador inicial deve ser provisionado de maneira controlada no ambiente de teste**, antes de testar funções administrativas. Não utilize alterações manuais de perfil como procedimento público ou de produção.

## 6. Endpoints principais

As rotas usam prefixo `/api`.

| Método | Rota | Finalidade |
|---|---|---|
| `POST` | `/api/usuarios/cadastro` | Criar conta cidadã |
| `POST` | `/api/auth/login` | Autenticar e obter JWT |
| `GET` | `/api/usuarios` | Listar usuários (ADMIN) |
| `GET/PUT/DELETE` | `/api/usuarios/{id}` | Consultar, atualizar ou excluir conforme autorização |
| `GET/POST` | `/api/locais` | Listar / criar locais |
| `GET/PUT/DELETE` | `/api/locais/{id}` | Consultar / atualizar / excluir local |
| `PUT` | `/api/locais/{id}/responsavel` | Atribuir responsável (ADMIN) |
| `GET/POST` | `/api/recursos` | Listar / criar recursos de acessibilidade |
| `GET/PUT/DELETE` | `/api/recursos/{id}` | Consultar / atualizar / excluir recurso |
| `GET/POST` | `/api/relatos` | Listar / registrar relatos |
| `GET/PUT/DELETE` | `/api/relatos/{id}` | Consultar / atualizar / excluir relato |
| `GET/POST` | `/api/avisos-operacionais` | Listar / registrar avisos |
| `GET/PUT/DELETE` | `/api/avisos-operacionais/{id}` | Consultar / atualizar / excluir aviso |
| `POST` | `/api/ia/recursos/{recursoId}/resumo` | Resumo contextual com Spring AI |

As listagens usam paginação do Spring Data (`page`, `size`, `sort`). Algumas aceitam filtros opcionais: `localId` em `/api/recursos` e `recursoId` em `/api/relatos` ou `/api/avisos-operacionais`.

**Observação:** as operações `GET` de locais, recursos, relatos e avisos são públicas na configuração atual. Demais permissões são aplicadas pela configuração de segurança e pelas regras de serviço. Para a lista detalhada de esquemas e respostas, consulte o Swagger.

### Exemplos de requisições

**Cadastro:**

```http
POST /api/usuarios/cadastro
Content-Type: application/json
```

```json
{
  "nome": "Pessoa Exemplo",
  "email": "pessoa.exemplo@example.com",
  "senha": "SenhaTeste123!"
}
```

**Login:**

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "pessoa.exemplo@example.com",
  "senha": "SenhaTeste123!"
}
```

**Criar local (ADMIN):**

```http
POST /api/locais
Authorization: Bearer <TOKEN_ADMIN>
Content-Type: application/json
```

```json
{
  "nome": "Estação Exemplo",
  "tipo": "Estação de transporte",
  "endereco": "Endereço fictício para testes"
}
```

**Criar recurso (ADMIN):**

```http
POST /api/recursos
Authorization: Bearer <TOKEN_ADMIN>
Content-Type: application/json
```

```json
{
  "nome": "Elevador principal",
  "tipo": "ELEVADOR",
  "localizacaoInterna": "Entrada principal",
  "localId": 1
}
```

**Criar relato (usuário autenticado):**

```http
POST /api/relatos
Authorization: Bearer <TOKEN_CIDADAO>
Content-Type: application/json
```

```json
{
  "recursoId": 1,
  "descricao": "Elevador apresentou falha durante a visita.",
  "condicao": "COM_PROBLEMA",
  "observadoEm": "2026-11-13T09:30:00"
}
```

**Atribuir responsável (ADMIN):**

```http
PUT /api/locais/1/responsavel
Authorization: Bearer <TOKEN_ADMIN>
Content-Type: application/json
```

```json
{
  "responsavelId": 2
}
```

O recurso e os IDs acima são **exemplos**, não dados garantidos em uma instalação nova. Utilize os IDs retornados pelos cadastros e datas válidas para seus testes.

**Resumo por IA:**

```http
POST /api/ia/recursos/1/resumo
Authorization: Bearer <TOKEN_VALIDO>
```

Essa requisição **não requer corpo JSON**.

## 7. Spring AI + Gemini

O endpoint de resumo consulta um recurso e recupera **até 10 relatos** e **até 10 avisos operacionais** relacionados a ele. O `ResumoIaService` organiza as informações e envia o contexto a um modelo Gemini por meio de `ChatClient` do Spring AI.

A instrução enviada ao modelo solicita um resumo em português, diferencia relatos de avisos, considera datas e conflitos de informação e **proíbe inventar confirmações, diagnósticos ou prazos**. Descrições escritas pelos usuários são tratadas como dados, nunca como instruções ao modelo.

O resultado contém o ID, nome do recurso, local, quantidade de registros analisados, resumo e horário de geração. Se não houver registros, a aplicação informa que não é possível confirmar a condição do recurso. Se o serviço de IA estiver indisponível, a API pode retornar erro de serviço, sem fabricar um resumo.

**Verificação realizada durante o desenvolvimento:** o endpoint retornou HTTP `200 OK` em teste manual com uma chave Gemini válida. Isso não substitui testes de integração em todos os ambientes.

## 8. Arquitetura distribuída — opção A: RabbitMQ

Para o requisito de arquitetura distribuída da disciplina Java, foi escolhida a **opção A: mensageria assíncrona com RabbitMQ**.

```mermaid
sequenceDiagram
  participant C as Cliente
  participant API as Spring Boot
  participant DB as MySQL
  participant MQ as RabbitMQ
  participant W as Consumidor
  C->>API: POST /api/relatos
  API->>DB: Salva relato
  DB-->>API: Commit confirmado
  API->>MQ: Publica RelatoCriadoEvento (AFTER_COMMIT)
  API-->>C: 201 Created
  MQ-->>W: Entrega evento em outra thread
  W->>DB: Grava EventoRelatoProcessado
```

Configuração usada no código:

- Exchange: `acessolivre.eventos`
- Fila: `acessolivre.relatos.criados`
- Routing key: `relato.criado`
- Evento: `RelatoCriadoEvento` (`relatoId`, `recursoId`, `condicao`)

O consumidor registra o evento processado no MySQL e sinaliza condições que merecem atenção operacional. A gravação em tabela de auditoria permite verificar o consumo.

**Verificação realizada durante o desenvolvimento:** logs de publicação e consumo e registro de auditoria no MySQL foram observados em um teste manual de ponta a ponta.

**Limitação conhecida:** se o envio da mensagem falhar após o commit do relato, o código registra o erro, mas não mantém uma fila persistente de reenvio (*outbox*). O caminho de sucesso foi testado; recuperação de falhas ainda não foi demonstrada.

## 9. Testes

O projeto possui **14 testes automatizados**, escritos com **JUnit 5** e **Mockito**, cobrindo regras de autenticação, JWT por ID, permissões, atribuição de responsáveis e eventos de relatos.

Execute:

**Windows (Git Bash):**

```bash
./gradlew.bat clean test
```

**Linux/macOS:**

```bash
./gradlew clean test
```

Relatório local gerado em:

```text
build/reports/tests/test/index.html
```

Na execução de 13/11/2026, após integração das alterações à branch `main`, os **14 testes passaram**, com `BUILD SUCCESSFUL`.

Esses são principalmente **testes unitários com simulações (mocks)**: comprovam regras específicas, mas **não substituem** ensaios completos de rede, banco, RabbitMQ e Gemini nem comprovam a implantação Azure. Testes HTTP manuais também foram realizados durante o desenvolvimento.

## 10. Usuários de teste e preparação de dados

O repositório **não garante contas pré-cadastradas** em uma instalação nova: o MySQL pode iniciar vazio. Utilize `POST /api/usuarios/cadastro` para criar uma conta fictícia de perfil `CIDADAO`, como a pessoa do exemplo acima.

Para testar as rotas de administrador e atribuição de responsáveis, o ambiente de demonstração deve possuir uma conta `ADMIN` **provisionada de forma controlada** pelo responsável do banco de testes. Não exponha senha de administrador nem publique dados reais no repositório.

**Antes da entrega**, preencher aqui os usuários fictícios efetivamente preparados para demonstração (identificador e perfil; senhas compartilhadas apenas por canal privado, se necessário):

| Usuário de demonstração | Perfil | Situação |
|---|---|---|
| `pessoa.exemplo@example.com` | `CIDADAO` | Exemplo de cadastro; não pré-instalado |
| **[definir conta de demonstração]** | `ADMIN` | Provisionamento de teste pendente de documentar |
| **[definir conta de demonstração]** | `RESPONSAVEL` | Atribuído por um ADMIN durante a demonstração |

## 11. Situação do deploy e integração Mobile

- **Backend local:** implementado, com testes unitários aprovados e integrações Gemini/RabbitMQ verificadas manualmente durante o desenvolvimento.
- **Azure:** provisionamento, pipelines CI/CD, banco na nuvem e URL de implantação fazem parte da frente DevOps e ainda precisam ser confirmados neste README após a publicação.
- **URL da API publicada:** **[adicionar quando houver deploy funcional]**.
- **Integração Mobile:** **[adicionar link do aplicativo e evidência do consumo da API quando estiver concluída]**.
- **Vídeo pitch Java (até 5 minutos):** **[adicionar link quando publicado]**.

Não considere uma implantação ou integração concluída apenas porque a API funciona localmente.

## 12. Limitações e melhorias futuras

- Registrar e utilizar a data de atualização dos avisos operacionais nos resumos da IA, além da data original de publicação.
- Testar de ponta a ponta a continuidade do acesso após alteração de e-mail, apesar de os testes unitários já cobrirem a identidade do token por ID.
- Implementar um mecanismo persistente de reenvio de eventos (por exemplo, *transactional outbox*) e cenários de falha do RabbitMQ.
- Definir um processo seguro de criação do primeiro administrador por ambiente.
- Substituir `ddl-auto=update` por migrações/esquema controlado na infraestrutura de entrega.
- Concluir e evidenciar deploy na Azure, integração Mobile e testes integrados adicionais.

## 13. Declaração de uso de inteligência artificial

Este projeto utiliza IA de duas formas distintas:

1. **Na solução:** Google Gemini, integrado via Spring AI, produz resumos contextuais a partir de relatos e avisos operacionais armazenados pelo AcessoLivre.
2. **Durante o desenvolvimento:** ChatGPT foi utilizado como apoio para planejamento, explicações técnicas, revisão, depuração, elaboração de testes e documentação. O grupo é responsável pela avaliação das sugestões, pela execução dos testes e pela validação do código entregue.

As respostas de IA não devem ser tratadas como verificações físicas da acessibilidade dos locais.

## 14. Integrantes e entrega

**Instituição:** FIAP  
**Global Solution:** 2026/2 — CitizenTech  
**Projeto:** AcessoLivre  
**Turma:** **[confirmar turma e período]**

| Integrante                           | RM             |
|--------------------------------------|----------------|
| **[Emily Maria de Oliveria Macedo]** | **[RM554882]** |


**Repositório público GitHub:** **[inserir URL após publicação]**  
**Link do vídeo pitch:** **[inserir URL]**  
**Deploy:** **[inserir URL, se houver]**

A entrega de Java também exige o arquivo `.txt` de identificação dentro do ZIP entregue no Portal do Aluno, conforme o enunciado oficial. Não incluir `.env`, tokens, chaves, senhas, diretório `.git` ou artefatos desnecessários na compactação pública.
