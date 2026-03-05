# 🚧 Projeto Cancela — Documentação Técnica Completa

API REST desenvolvida em **Java com Spring Boot** para monitoramento em tempo real do fluxo de veículos em um estacionamento. Registra entradas e saídas, calcula vagas disponíveis e gera relatórios por dia, semana e turno de trabalho.

---

## 📑 Índice

1. [Arquitetura do Projeto](#-arquitetura-do-projeto)
2. [Estrutura de Pacotes](#-estrutura-de-pacotes)
3. [Camada de Domínio — Model e Repository](#1-camada-de-domínio--model-e-repository)
   - [RegistroCancela.java](#registrocancelajava)
   - [RegistroCancelaRepository.java](#registrocancelarepositoryiava)
4. [Camada de Aplicação — Services e Helpers](#2-camada-de-aplicação--services-e-helpers)
   - [DataUtil.java](#datautiljava)
   - [EntradasService.java](#entradasservicejava)
   - [SaidasService.java](#saidasservicejava)
   - [RelatorioService.java](#relatorioservicejava)
5. [Camada de Infraestrutura — Controllers, DTOs e Mapper](#3-camada-de-infraestrutura--controllers-dtos-e-mapper)
   - [EntradasController.java](#entradascontrollerjava)
   - [SaidasController.java](#saidascontrollerjava)
   - [EntradasTurnoController.java](#entradasturnocontrollerjava)
   - [SaidasTurnoController.java](#saidasturnocontrollerjava)
   - [RegistroCancelaDTO.java](#registrocanceladtojava)
   - [RegistroCancelaMapper.java](#registrocancelamapperjava)
6. [Inicialização](#4-inicialização)
   - [ProjetoCancelaApplication.java](#projetocancelaapplicationjava)
7. [Mapa de Endpoints](#-mapa-completo-de-endpoints)
8. [Tecnologias Utilizadas](#-tecnologias-utilizadas)

---

## 🏛 Arquitetura do Projeto

O projeto segue a **Arquitetura em Camadas**, separando responsabilidades de forma clara:

```
┌─────────────────────────────────────────┐
│         INFRA / WEB (Controllers)        │  ← Recebe requisições HTTP
├─────────────────────────────────────────┤
│        APPLICATION (Services)            │  ← Regras de negócio
├─────────────────────────────────────────┤
│          DOMAIN (Model + Repository)     │  ← Estrutura de dados e banco
└─────────────────────────────────────────┘
```

| Camada | Pacote | Responsabilidade |
|--------|--------|-----------------|
| **Domain** | `domain.model` / `domain.repository` | Estrutura dos dados e acesso ao banco |
| **Application** | `application.service` / `application.helpers` | Regras de negócio e cálculos |
| **Infra/Web** | `infra.web.controller` / `dto` / `mapper` | Endpoints HTTP, DTOs e mapeamento |

---

## 📁 Estrutura de Pacotes

```
com.weg.projeto_cancela/
│
├── ProjetoCancelaApplication.java          # Ponto de entrada da aplicação
│
├── domain/
│   ├── model/
│   │   └── RegistroCancela.java            # Entidade do banco de dados
│   └── repository/
│       └── RegistroCancelaRepository.java  # Interface de acesso ao MongoDB
│
├── application/
│   ├── helpers/
│   │   └── DataUtil.java                   # Utilitários de data/hora
│   └── service/
│       ├── EntradasService.java            # Regras de negócio de entradas
│       ├── SaidasService.java              # Regras de negócio de saídas
│       └── RelatorioService.java           # Geração de relatório CSV
│
└── infra/
    └── web/
        ├── controller/
        │   ├── EntradasController.java         # Endpoints /api/entradas
        │   ├── SaidasController.java           # Endpoints /api/saidas
        │   └── turno/
        │       ├── EntradasTurnoController.java # Endpoints /api/turno/entradas
        │       └── SaidasTurnoController.java   # Endpoints /api/turno/saidas
        ├── dto/
        │   └── RegistroCancelaDTO.java         # Objeto de transferência de dados
        └── mapper/
            └── RegistroCancelaMapper.java      # Converte Model → DTO
```

---

## 1. Camada de Domínio — Model e Repository

### `RegistroCancela.java`
**Pacote:** `domain.model`

Representa a **entidade principal** — o molde de cada documento salvo no MongoDB.

```java
@Document(collection = "historico")
public class RegistroCancela {
```
- `@Document(collection = "historico")` — Anota a classe como um documento MongoDB. Cada instância desta classe será salva como um documento dentro da coleção chamada `historico`.

```java
    @Id
    private String id;
```
- `@Id` — Marca este campo como a **chave primária** do documento. O MongoDB gera automaticamente um valor único (ObjectId) para este campo quando um novo registro é criado.

```java
    private String evento;
```
- Armazena o **tipo do evento** registrado. Os valores possíveis são:
  - `"Carro Entrando"` — sensor detectou entrada por loop
  - `"Carro Saindo"` — sensor detectou saída
  - `"Aberta por: Botao Fisico"` — cancela aberta manualmente

```java
    private String data;
```
- Armazena a **data e hora do evento em formato UTC** (ex: `"2024-06-10T14:35:00Z"`). O uso do UTC permite que a API converta para qualquer fuso horário sem perder precisão.

```java
    @Field("data_formatada")
    private String dataFormatada;
```
- `@Field("data_formatada")` — Mapeia este campo Java para o campo `data_formatada` no documento MongoDB (os nomes diferem). Armazena a data já formatada para leitura humana.

```java
    private Integer hora;
```
- Armazena o **número da hora** do evento (0–23), permitindo filtros rápidos por hora sem precisar fazer parse de string.

> Os **getters, setters e construtores** são os métodos padrão Java que permitem leitura e escrita segura de cada atributo por outras classes.

---

### `RegistroCancelaRepository.java`
**Pacote:** `domain.repository`

Interface que serve como **ponte direta com o MongoDB**. O Spring Data interpreta os nomes dos métodos e gera as queries automaticamente — sem precisar escrever SQL ou código de banco.

```java
public interface RegistroCancelaRepository extends MongoRepository<RegistroCancela, String> {
```
- `extends MongoRepository<RegistroCancela, String>` — Herda todas as operações CRUD básicas (`save`, `findAll`, `deleteById`, etc.). O primeiro parâmetro é o tipo do documento (`RegistroCancela`) e o segundo é o tipo do ID (`String`).

```java
    List<RegistroCancela> findByEvento(String evento);
```
- Busca **todos os registros** onde o campo `evento` seja igual ao valor passado. Ex: `findByEvento("Carro Entrando")` retorna todos os registros de entrada.

```java
    long countByEvento(String evento);
```
- **Conta** quantos registros existem para um evento, sem trazer a lista inteira para a memória. Muito mais eficiente para o cálculo de vagas.

```java
    List<RegistroCancela> findByEventoContainingIgnoreCase(String texto);
```
- Busca registros cujo campo `evento` **contém** o texto informado, ignorando maiúsculas/minúsculas. Útil para buscas parciais como `"Botao"`.

```java
    List<RegistroCancela> findByEventoAndDataStartingWith(String evento, String data);
```
- Filtra registros por evento **e** por data que **começa com** um valor (ex: `"2024-06-10"`). Usado para filtros por dia específico.

```java
    List<RegistroCancela> findByEventoAndDataBetween(String evento, String dataInicio, String dataFim);
```
- Filtra registros por evento cujo campo `data` está **entre** dois valores UTC. É o método central para todos os filtros temporais (hoje, semana, etc.).

```java
    long countByEventoAndDataStartingWith(String evento, String data);
```
- Combina a contagem com o filtro de prefixo de data — retorna apenas o número, economizando memória.

---

## 2. Camada de Aplicação — Services e Helpers

### `DataUtil.java`
**Pacote:** `application.helpers`

Classe utilitária com métodos de **conversão e cálculo de datas/horas**. Centraliza a lógica de fuso horário para evitar repetição nos services.

```java
@Component
public class DataUtil {
```
- `@Component` — Registra a classe no contexto do Spring, permitindo que seja injetada em outras classes via construtor.

```java
    public List<RegistroCancela> filtrarPorHorario(
            List<RegistroCancela> registroCancelas,
            LocalTime inicio,
            LocalTime fim) {
```
- Recebe uma lista de registros e dois horários (início e fim), e retorna apenas os registros cujo horário esteja dentro do intervalo. Usado para separar os turnos de trabalho.

```java
        ZonedDateTime dataUtc = ZonedDateTime.parse(r.getData());
        LocalTime horaRegistro = dataUtc
            .withZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
            .toLocalTime();
```
- Faz o parse da string UTC armazenada no banco, converte para o **fuso horário de São Paulo** e extrai apenas a hora local. Isso garante que um evento registrado às `17:00 UTC` seja tratado como `14:00 BRT`.

```java
        if (!horaRegistro.isBefore(inicio) && !horaRegistro.isAfter(fim)) {
            filtro.add(r);
        }
```
- Verifica se a hora do registro está **dentro do intervalo** (inclusivo nos dois extremos). `!isBefore(inicio)` significa `>= inicio`; `!isAfter(fim)` significa `<= fim`.

```java
    public String[] obterLimitesDiaUTC(LocalDate dataBrasil) {
        ZonedDateTime inicioBR = dataBrasil.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimBR = dataBrasil
            .atTime(23, 59, 59, 999999999)
            .atZone(ZoneId.of("America/Sao_Paulo"));

        return new String[]{
            inicioBR.toInstant().toString(),
            fimBR.toInstant().toString()
        };
    }
```
- Recebe uma data local brasileira e retorna um array com **início** (`00:00:00`) e **fim** (`23:59:59.999`) do dia convertidos para UTC como strings. Esses valores são usados diretamente nas queries `findByEventoAndDataBetween`.

```java
    public String[] obterLimitesPeriodoUTC(LocalDate dataInicioBR, LocalDate dataFimBR) {
```
- Igual ao método anterior, mas aceita **dois dias diferentes** para montar intervalos maiores (como uma semana inteira).

---

### `EntradasService.java`
**Pacote:** `application.service`

Contém todas as **regras de negócio relacionadas às entradas** de veículos, incluindo o cálculo de vagas e os filtros temporais.

```java
@Service
public class EntradasService {
    private final RegistroCancelaRepository repository;
    private final DataUtil dataUtil;
```
- `@Service` — Indica ao Spring que esta é uma classe de serviço gerenciada pelo container.
- As dependências (`repository` e `dataUtil`) são injetadas pelo construtor — padrão recomendado pelo Spring.

```java
    private final int CAPACIDADE_TOTAL = 1381;
```
- Constante com a **capacidade máxima física** do estacionamento. Usada como base para o cálculo de vagas disponíveis.

```java
    public List<RegistroCancela> buscarEntradas() {
        List<RegistroCancela> listaCompleta = new ArrayList<>();
        listaCompleta.addAll(repository.findByEvento("Aberta por: Botao Fisico"));
        listaCompleta.addAll(repository.findByEvento("Carro Entrando"));
        return listaCompleta;
    }
```
- Retorna **todos os registros de entrada** do histórico completo, combinando entradas por sensor e por botão físico em uma única lista.

```java
    public int calcularVagasDisponiveis() {
        long totalEntradas = repository.countByEvento("Carro Entrando");
        long totalEntradasBotao = repository.countByEvento("Aberta por: Botao Fisico");
        long totalSaidas = repository.countByEvento("Carro Saindo");

        long carrosDentro = (totalEntradas + totalEntradasBotao) - totalSaidas;

        if (carrosDentro < 0) { carrosDentro = 0; }

        return (int) (CAPACIDADE_TOTAL - carrosDentro);
    }
```
- Implementa a fórmula central do sistema:

$$\text{Vagas} = 1381 - ((\text{Entradas} + \text{BotoesFisicos}) - \text{Saidas})$$

- Usa `count` em vez de `findAll` para **não carregar listas inteiras na memória**.
- O `if (carrosDentro < 0)` é uma proteção contra dados inconsistentes no banco — evita retornar um número negativo de carros dentro.

```java
    public List<RegistroCancela> ListarEntradasHoje() {
        String[] limites = dataUtil.obterLimitesDiaUTC(
            LocalDate.now(ZoneId.of("America/Sao_Paulo"))
        );
        // ...
        listaCompleta.addAll(repository.findByEventoAndDataBetween(
            "Carro Entrando", limites[0], limites[1]
        ));
        listaCompleta.addAll(repository.findByEventoAndDataBetween(
            "Aberta por: Botao Fisico", limites[0], limites[1]
        ));
        return listaCompleta;
    }
```
- Obtém a data atual no **fuso de Brasília**, converte para limites UTC via `DataUtil` e busca entradas dos dois tipos dentro daquele intervalo.

```java
    public List<RegistroCancela> listarEntradasOntem() {
        String[] limites = dataUtil.obterLimitesDiaUTC(
            LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(1)
        );
        // ...
    }
```
- Idêntico ao `ListarEntradasHoje()`, mas usa `.minusDays(1)` para obter os limites do **dia anterior**.

```java
    public List<RegistroCancela> listarEntradasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaAtual = hoje.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );
        LocalDate domingoAtual = hoje.with(
            TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)
        );
        // ...
    }
```
- `TemporalAdjusters.previousOrSame(MONDAY)` — Encontra a **Segunda-feira** da semana atual (ou hoje mesmo, se já for segunda).
- `TemporalAdjusters.nextOrSame(SUNDAY)` — Encontra o **Domingo** da semana atual.
- Esses dois pontos formam o intervalo da semana vigente passado para `obterLimitesPeriodoUTC`.

```java
    public List<RegistroCancela> listarEntradasPassada() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaPassada = hoje
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);
        // ...
    }
```
- Calcula a Segunda da semana atual e subtrai **7 dias** para chegar na Segunda passada.
- Soma 6 dias para chegar no Domingo da semana passada, formando o intervalo completo.

```java
    public List<RegistroCancela> buscarEntradasTurno(int turno) {
        List<RegistroCancela> entradasHoje = ListarEntradasHoje();

        switch (turno) {
            case 1:
                return dataUtil.filtrarPorHorario(
                    entradasHoje, LocalTime.of(5, 0), LocalTime.of(14, 18)
                );
            case 2:
                return dataUtil.filtrarPorHorario(
                    entradasHoje, LocalTime.of(14, 24), LocalTime.of(23, 18)
                );
            case 3:
                List<RegistroCancela> entradasOntem = listarEntradasOntem();
                List<RegistroCancela> turno3 = new ArrayList<>();
                turno3.addAll(dataUtil.filtrarPorHorario(
                    entradasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)
                ));
                turno3.addAll(dataUtil.filtrarPorHorario(
                    entradasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)
                ));
                return turno3;
        }
        return new ArrayList<>();
    }
```
- **Turno 1 e 2** — Simples: filtra a lista de hoje dentro do horário do turno.
- **Turno 3** — Complexo, pois o turno **cruza a meia-noite**:
  - Busca registros de **ontem** das `23:24` até `23:59`
  - Busca registros de **hoje** das `00:00` até `05:00`
  - Une as duas listas para formar o turno completo

**Resumo dos Turnos:**

| Turno | Início | Fim |
|-------|--------|-----|
| 1º | 05:00 | 14:18 |
| 2º | 14:24 | 23:18 |
| 3º | 23:24 *(ontem)* | 05:00 *(hoje)* |

---

### `SaidasService.java`
**Pacote:** `application.service`

Estrutura **idêntica ao `EntradasService`**, mas voltada exclusivamente para os registros de saída (`"Carro Saindo"`). As diferenças são:

- Não possui `calcularVagasDisponiveis()` — essa lógica pertence ao `EntradasService` pois precisa cruzar entradas e saídas.
- Cada método de listagem usa **apenas o evento `"Carro Saindo"`**, sem precisar combinar dois tipos de evento como nas entradas.
- `buscarSaidasTurno(int turno)` — Mesma lógica de turnos do `EntradasService`, aplicada às saídas.

```java
    public List<RegistroCancela> buscarSaidas() {
        return repository.findByEvento("Carro Saindo");
    }
```
- Retorna o histórico completo de saídas.

Os métodos `ListarSaidasHoje()`, `listarSaidasOntem()`, `listarSaidasSemana()` e `listarSaidasPassada()` seguem a mesma lógica dos equivalentes em `EntradasService`, usando `DataUtil` para gerar os limites UTC e passando-os ao repositório.

---

### `RelatorioService.java`
**Pacote:** `application.service`

Responsável pela **geração do arquivo CSV** para exportação.

```java
    public String gerarRelatorio() {
        List<RegistroCancela> todosRegistros = repository.findAll();

        StringBuilder exelData = new StringBuilder();
        exelData.append("ID;Evento;Data;Local\n");

        for (RegistroCancela registroCancela : todosRegistros) {
            exelData.append(registroCancela.getId()).append(";")
                    .append(registroCancela.getEvento()).append(";")
                    .append(registroCancela.getData()).append("\n");
        }

        return exelData.toString();
    }
```
- `repository.findAll()` — Carrega **todos os registros** do banco de dados.
- `StringBuilder` — Estrutura eficiente para concatenar strings em loop (mais performático que `+` com `String`).
- `append("ID;Evento;Data;Local\n")` — Adiciona o **cabeçalho** do CSV. O separador `;` é o padrão para Excel em idioma português (que usa `,` como separador decimal).
- O loop percorre todos os registros e formata cada um como uma **linha CSV**: `id;evento;data`.
- O resultado é uma `String` com todo o conteúdo do arquivo, que o controller converte em download.

---

## 3. Camada de Infraestrutura — Controllers, DTOs e Mapper

### `EntradasController.java`
**Pacote:** `infra.web.controller`

Expõe todos os **endpoints HTTP de entradas** na URL base `/api/entradas`.

```java
@RestController
@RequestMapping("/api/entradas")
@CrossOrigin(origins = "*")
public class EntradasController {
```
- `@RestController` — Combina `@Controller` + `@ResponseBody`. Faz com que todos os métodos retornem dados JSON automaticamente.
- `@RequestMapping("/api/entradas")` — Define o prefixo de URL para todos os endpoints desta classe.
- `@CrossOrigin(origins = "*")` — Permite que **qualquer origem** (front-end em outro domínio/porta) consuma esta API sem bloqueio de CORS pelo navegador.

```java
    @GetMapping
    public List<RegistroCancela> getEntradas() {
        return service.buscarEntradas();
    }
```
- `GET /api/entradas` — Retorna todo o histórico de entradas.

```java
    @GetMapping("/botao")
    public List<RegistroCancela> getAberturasBotao() {
        return service.buscarAberturasPorBotao();
    }
```
- `GET /api/entradas/botao` — Retorna apenas as aberturas manuais via botão físico.

```java
    @GetMapping("/vagas")
    public int getVagas() {
        return service.calcularVagasDisponiveis();
    }
```
- `GET /api/entradas/vagas` — Retorna o número inteiro de vagas disponíveis no momento.

```java
    @GetMapping(value = "/relatorio/excel", produces = "text/csv")
    public ResponseEntity<String> baixarExel() {
        String conteudo = relatorioService.gerarRelatorio();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_cancela.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok().headers(headers).body(conteudo);
    }
```
- `produces = "text/csv"` — Informa ao Spring que este endpoint produz um arquivo CSV.
- `ResponseEntity<String>` — Permite controlar tanto o **corpo** quanto os **cabeçalhos HTTP** da resposta.
- `CONTENT_DISPOSITION: attachment; filename=...` — Instrui o navegador a **baixar o arquivo** em vez de exibi-lo, com o nome `relatorio_cancela.csv`.
- `charset=UTF-8` — Garante que acentos e caracteres especiais sejam preservados no arquivo.

**Tabela de endpoints:**

| Método | URL | Descrição |
|--------|-----|-----------|
| `GET` | `/api/entradas` | Todas as entradas (histórico completo) |
| `GET` | `/api/entradas/botao` | Apenas aberturas por botão físico |
| `GET` | `/api/entradas/vagas` | Vagas disponíveis agora |
| `GET` | `/api/entradas/hoje` | Entradas do dia atual |
| `GET` | `/api/entradas/ontem` | Entradas do dia anterior |
| `GET` | `/api/entradas/semana` | Entradas da semana vigente |
| `GET` | `/api/entradas/semanapassada` | Entradas da semana passada |
| `GET` | `/api/entradas/relatorio/excel` | Download do CSV completo |

---

### `SaidasController.java`
**Pacote:** `infra.web.controller`

Espelho do `EntradasController` para saídas, com URL base `/api/saidas`. Injeta o `SaidasService` e delega todas as chamadas a ele.

| Método | URL | Descrição |
|--------|-----|-----------|
| `GET` | `/api/saidas` | Todas as saídas (histórico completo) |
| `GET` | `/api/saidas/hoje` | Saídas do dia atual |
| `GET` | `/api/saidas/ontem` | Saídas do dia anterior |
| `GET` | `/api/saidas/semana` | Saídas da semana vigente |
| `GET` | `/api/saidas/passada` | Saídas da semana passada |

---

### `EntradasTurnoController.java`
**Pacote:** `infra.web.controller.turno`

Expõe os endpoints de **entradas filtradas por turno** na URL base `/api/turno/entradas`.

```java
@GetMapping("/primeiro")
public List<RegistroCancela> getEntradaPrimeiroTurno() {
    return service.buscarEntradasTurno(1);
}
```
- Cada método chama `buscarEntradasTurno(n)` passando o número do turno como parâmetro. O service encapsula toda a lógica de horários.

| Método | URL | Turno |
|--------|-----|-------|
| `GET` | `/api/turno/entradas/primeiro` | 05:00 → 14:18 |
| `GET` | `/api/turno/entradas/segundo` | 14:24 → 23:18 |
| `GET` | `/api/turno/entradas/terceiro` | 23:24 → 05:00 |

---

### `SaidasTurnoController.java`
**Pacote:** `infra.web.controller.turno`

Idêntico ao `EntradasTurnoController`, mas para saídas, com URL base `/api/turno/saidas`.

| Método | URL | Turno |
|--------|-----|-------|
| `GET` | `/api/turno/saidas/primeiro` | 05:00 → 14:18 |
| `GET` | `/api/turno/saidas/segundo` | 14:24 → 23:18 |
| `GET` | `/api/turno/saidas/terceiro` | 23:24 → 05:00 |

---

### `RegistroCancelaDTO.java`
**Pacote:** `infra.web.dto`

```java
public record RegistroCancelaDTO(
        String id,
        String evento,
        String data
) {}
```

- `record` — Recurso do Java 16+ que cria automaticamente uma classe **imutável** com construtor, getters, `equals`, `hashCode` e `toString`.
- O DTO (Data Transfer Object) é uma versão **simplificada e controlada** da entidade `RegistroCancela`, expondo apenas os campos necessários para o consumidor da API (`id`, `evento`, `data`) — sem expor campos internos como `dataFormatada` ou `hora`.

---

### `RegistroCancelaMapper.java`
**Pacote:** `infra.web.mapper`

```java
@Component
public class RegistroCancelaMapper {
    public RegistroCancelaDTO toEntity(RegistroCancela registroCancela) {
        return new RegistroCancelaDTO(
                registroCancela.getId(),
                registroCancela.getEvento(),
                registroCancela.getData()
        );
    }
}
```

- Responsável por **converter** um objeto `RegistroCancela` (entidade do banco) em um `RegistroCancelaDTO` (objeto de resposta da API).
- Centraliza a conversão em um único lugar, facilitando manutenção: se o DTO mudar, basta alterar o mapper.
- `@Component` — Registrado no contexto do Spring para ser injetado onde necessário.

---

## 4. Inicialização

### `ProjetoCancelaApplication.java`

```java
@SpringBootApplication
public class ProjetoCancelaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjetoCancelaApplication.class, args);
    }
}
```

- `@SpringBootApplication` — Macro-anotação que engloba três anotações:
  - `@Configuration` — Marca a classe como fonte de configurações do Spring.
  - `@EnableAutoConfiguration` — Ativa a configuração automática (detecta MongoDB, Web, etc. no classpath).
  - `@ComponentScan` — Varre todos os subpacotes procurando por `@Service`, `@Repository`, `@Controller` e `@Component` para registrá-los no container.
- `SpringApplication.run(...)` — Inicia o **servidor Tomcat embutido**, carrega todas as configurações, conecta ao MongoDB e deixa a API pronta para receber requisições.

---

## 🗺 Mapa Completo de Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/entradas` | Histórico completo de entradas |
| `GET` | `/api/entradas/botao` | Aberturas por botão físico |
| `GET` | `/api/entradas/vagas` | Vagas disponíveis em tempo real |
| `GET` | `/api/entradas/hoje` | Entradas de hoje |
| `GET` | `/api/entradas/ontem` | Entradas de ontem |
| `GET` | `/api/entradas/semana` | Entradas da semana atual |
| `GET` | `/api/entradas/semanapassada` | Entradas da semana passada |
| `GET` | `/api/entradas/relatorio/excel` | Download CSV completo |
| `GET` | `/api/saidas` | Histórico completo de saídas |
| `GET` | `/api/saidas/hoje` | Saídas de hoje |
| `GET` | `/api/saidas/ontem` | Saídas de ontem |
| `GET` | `/api/saidas/semana` | Saídas da semana atual |
| `GET` | `/api/saidas/passada` | Saídas da semana passada |
| `GET` | `/api/turno/entradas/primeiro` | Entradas — 1º Turno (05:00–14:18) |
| `GET` | `/api/turno/entradas/segundo` | Entradas — 2º Turno (14:24–23:18) |
| `GET` | `/api/turno/entradas/terceiro` | Entradas — 3º Turno (23:24–05:00) |
| `GET` | `/api/turno/saidas/primeiro` | Saídas — 1º Turno (05:00–14:18) |
| `GET` | `/api/turno/saidas/segundo` | Saídas — 2º Turno (14:24–23:18) |
| `GET` | `/api/turno/saidas/terceiro` | Saídas — 3º Turno (23:24–05:00) |

---

## 🛠 Tecnologias Utilizadas

| Tecnologia | Uso |
|------------|-----|
| Java 17+ | Linguagem principal |
| Spring Boot 3+ | Framework base (Web + Data) |
| Spring Data MongoDB | Abstração do acesso ao banco |
| MongoDB | Banco de dados NoSQL |
| Maven | Gerenciador de dependências |
