# Transaction Authorizer

O Transaction Authorizer é um sistema exemplo desenvolvido para autorizar transações financeiras com base em regras de negócio, como MCC e verificação de saldos. O foco do projeto é avaliar a organização do código, aplicação de design patterns e cobertura de testes, sem objetivo de uso em produção.

## Pré-requisitos

Certifique-se de ter os seguintes pré-requisitos instalados em sua máquina:

- [Docker](https://www.docker.com/get-started)
- [Java 22](https://docs.oracle.com/en/java/javase/22/install/overview-jdk-installation.html)

## Como rodar o projeto

1. **Clone o repositório:**

```bash
git clone https://github.com/guibernardino/transaction-authorizer.git
cd transaction-authorizer
```
   
2. **Inicie o projeto usando Docker Compose:**

Este comando irá baixar todas as dependências necessárias, criar os contêineres do Docker e iniciar a aplicação.

```bash
docker compose up
```

3. **Acesse a aplicação:**

Após a inicialização, a aplicação estará disponível em http://localhost:8080/swagger-ui/index.html.

## Desenvolvimento local

Para desenvolvimento local, é necessário ter Docker instalado para executar os serviços auxiliares, como o banco de dados. No entanto, você não precisa rodar o Docker manualmente. Basta executar o seguinte comando, e o Spring Boot se encarregará de levantar os serviços necessários via Docker:

```bash
./mvnw spring-boot:run
```

## Requisitos

- O sistema deve processar transações de forma a autorizar ou rejeitar a transação com base no saldo disponível na categoria apropriada da carteira do usuário.
- O sistema deve utilizar o MCC (Merchant Category Code) e o nome do estabelecimento para determinar a categoria correta da carteira que será debitada, assegurando que o saldo da categoria correspondente seja utilizado.
- Se o saldo na categoria mapeada for insuficiente para cobrir o valor total da transação, o sistema deve automaticamente tentar debitar o saldo disponível na categoria CASH. Caso o saldo da categoria CASH também seja insuficiente, a transação deve ser rejeitada.
- O sistema deve garantir a consistência dos dados ao processar transações simultâneas para a mesma conta, evitando assim condições de corrida que poderiam levar a inconsistências nos saldos.
- O sistema deve ser implementado como uma API, permitindo que transações sejam submetidas e processadas utilizando o protocolo HTTP.

## Goals

- **Avaliação de Código e Estrutura**: Demonstrar a capacidade de escrever código limpo, organizado e bem estruturado.
- **Aplicação de Design Patterns**: Implementar padrões de design apropriados para resolver problemas específicos no contexto do sistema.
- **Cobertura de Testes**: Fornecer uma cobertura de testes abrangente, incluindo testes unitários e de integração, para assegurar o funcionamento correto de todos os componentes do sistema.
- **Modelagem de Domínio**: Mostrar uma boa modelagem de domínio, refletindo as regras de negócio claramente através das entidades e casos de uso.

## Non-goals

O projeto não foi pensado para ser utilizado em ambiente produtivo, e, portanto, não inclui considerações de segurança, monitoramento, escalabilidade, ou disponibilidade que seriam necessárias em um sistema real.

## Métricas

Listei algumas métricas que considerei importantes a serem monitoradas, cobrindo tanto aspectos de negócio quanto de tecnologia.

### Métricas de Negócio
- Volume Total de Pagamentos (TPV)
- Número Total de Pagamentos (TPN)
- Valor Médio das Transações
- Taxa de Aprovação de Transações
- Taxa de Rejeição de Transações por Insuficiência de Saldo por Categoria
- Distribuição de Transações por Categoria (FOOD, MEAL, CASH)

### Métricas de Aplicaçao
- Uptime
- Tempo de Resposta (Response Time)
- Taxa de Erros (Error Rate)
- Latência
- Throughput
- Uso de Recursos (CPU, Memória, Disco e Rede)


## Transações simultâneas

Uma das principais abordagens para garantir a consistência dos dados ao processar transações simultâneas em sistemas distribuídos é a utilização de uma estratégia de bloqueio de recursos. Essa técnica assegura que apenas um processo possa acessar um recurso específico por vez, evitando assim condições de corrida que podem causar conflitos e inconsistências. A seguir, explorarei algumas abordagens que podemos utilizar para implementar esse recurso.

### RDBMS (Relational Database Management System)

Os RDBMS já oferecem mecanismos integrados de bloqueio que podemos utilizar para garantir a consistência dos dados. Esses mecanismos se dividem essencialmente em dois tipos principais: Bloqueio Pessimista e Bloqueio Otimista.

No bloqueio pessimista, assume-se que as colisões entre transações são prováveis, por isso o sistema protege os recursos antecipadamente. Isso significa que o acesso a um recurso é completamente bloqueado para outras transações, tanto para leitura quanto para escrita, até que a transação que detém o bloqueio seja concluída.

Por outro lado, no modelo otimista, não há bloqueio antecipado dos recursos. O sistema permite que várias transações leiam e tentem modificar os mesmos dados simultaneamente. Somente no momento do commit, é verificado se o dado foi alterado desde a última leitura; se uma alteração for detectada, a transação pode falhar ou ser reiniciada para evitar inconsistências.

### Bloqueios Distribuídos

Em sistemas distribuídos, onde múltiplos serviços, processos ou threads acessam simultaneamente recursos compartilhados, é essencial utilizar mecanismos de sincronização para evitar conflitos e garantir a consistência dos dados. Uma solução comum é a utilização de serviços externos para coordenar e gerenciar esses bloqueios.

As duas principais ferramentas amplamente utilizadas para essa coordenação são o Redis Lock e o ZooKeeper. Embora cada uma tenha suas especificidades, o princípio básico é similar: o serviço A adquire um bloqueio (lock) sobre um recurso e o libera após o uso. Caso o serviço B tente adquirir o bloqueio no mesmo recurso antes que o serviço A o libere, o bloqueio não será concedido, e o serviço B deverá aguardar até que o recurso esteja disponível novamente.

