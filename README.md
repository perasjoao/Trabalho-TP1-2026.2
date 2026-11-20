# Sistema de Análise de Pesquisas Eleitorais

Trabalho prático da disciplina **Técnicas de Programação 1 (CIC0197)** – Universidade de Brasília, semestre 2026.2.
Profa. Roberta Barbosa Oliveira. Grupo 12.

> **Projeto exclusivamente acadêmico e simulado.** Todos os candidatos, partidos, eleitores,
> respostas e resultados são fictícios. O sistema não deve ser usado para campanha eleitoral,
> propaganda política ou avaliação real de candidatos.

## Integrantes

| Nome | Matrícula | Módulo |
|---|---|---|
| Antonio Fagundes Rodrigues de Souza | 252009232 | Pesquisas |
| Geovanne Junio Menezes Lima | 252001154 | Coleta e Amostra |
| Henrique Puttini de Almeida | 252030147 | Candidatos e Partidos |
| Joao Victor Martins Pereira | 252001216 | Análise Estatística e Relatórios |
| Levi dos Santos Oliveira Prado | 252009223 | Administração |

A partir da Etapa 2, Henrique e João trocaram de módulo: na Etapa 1, Henrique fez os protótipos da Análise e João, os de Candidatos e Partidos.

## Sobre o sistema

Aplicação desktop para apoiar a coleta, organização, processamento e análise de dados de
pesquisas eleitorais simuladas, dividida em cinco módulos integrados:

| Módulo | Responsabilidade |
|---|---|
| Administração | Login, usuários, perfis de acesso, parâmetros gerais e relatório de usuários |
| Pesquisas | Cadastro, configuração (perguntas, regiões e candidatos), consulta e acompanhamento das pesquisas |
| Coleta e Amostra | Configuração da amostra, registro das respostas e consulta das entrevistas |
| Análise Estatística e Relatórios | Relatórios com filtros, comparação entre pesquisas (tabela e gráfico) e exportação em CSV |
| Candidatos e Partidos | Cadastro, associação e consulta de candidatos e partidos e indicadores cadastrais |

## Acesso e perfis

O sistema abre na tela de **Login**. Na primeira execução é criado um administrador padrão:

| E-mail | Senha |
|---|---|
| `admin@email.com` | `admin123` |

Pelo módulo de Administração, o administrador cadastra os demais usuários. Cada perfil vê no Menu Principal apenas os módulos permitidos:

| Perfil | Módulos |
|---|---|
| Administrador | Todos |
| Pesquisador | Pesquisas e Coleta e Amostra |
| Analista | Pesquisas e Análise Estatística e Relatórios |

O botão **Sair** do Menu Principal encerra a sessão e volta ao Login.

## Tecnologias

- Java 21
- JavaFX 21.0.2 (interface gráfica, telas em FXML e estilos em CSS)
- Maven (gerenciamento de dependências e execução)
- IntelliJ IDEA e Scene Builder
- Git e GitHub

## Como executar

Requisitos: **JDK 21 ou superior**.

**Pelo IntelliJ:** abra a aba **Maven** → **Plugins → javafx → javafx:run**.

**Pelo terminal** (com Maven instalado):

    mvn javafx:run

## Dados

Os dados são gravados em arquivos na pasta em que o sistema é executado, sem banco de dados.
Os arquivos são criados automaticamente na primeira execução e **não são versionados** no Git
(estão no `.gitignore`), para que cada um teste com os seus próprios dados:

| Arquivo | Conteúdo |
|---|---|
| `usuarios.dat` | Usuários |
| `parametros.dat` | Parâmetros gerais |
| `candidatos_partidos.dat` | Candidatos, candidaturas e partidos |
| `pesquisas_data.txt` | Pesquisas |
| `entrevistas.dat` | Entrevistas e respostas |

Para recomeçar do zero, basta fechar o sistema e apagar esses arquivos.

## Estrutura do projeto

    src/main/java/org/example/
        models/               Classes de domínio, separadas por módulo
        serviceControllers/   Regras de negócio e persistência
        viewControllers/      Controladores das telas, separados por módulo
        exceptions/           Exceções personalizadas
        utils/                Sessão do usuário e gravação em arquivos
    src/main/resources/       Telas em FXML (separadas por módulo) e estilos em CSS
    pom.xml                   Dependências e configuração do Maven

## Etapas de desenvolvimento

- [x] Etapa 1 – Estrutura Inicial (ambiente e protótipos das telas)
- [x] Etapa 2 – Design e Modelagem
- [x] Etapa 3 – Implementação da Lógica do Software
- [x] Etapa 4 – Integração e Navegabilidade
