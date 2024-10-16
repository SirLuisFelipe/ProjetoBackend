
# ProjetoBackend

## Requisitos
- Java 17
- Docker
- PostgreSQL

## Documentação
A documentação dos endpoints via swagger estará disponível no caminho `/reservation/swagger-ui.html`

## Como Rodar Localmente

### Passo 1: Subir o Banco de Dados
Você precisa iniciar o banco de dados PostgreSQL usando Docker. Execute o seguinte comando no seu terminal:

```bash
docker run --name reservation_db -e POSTGRES_DB=reservation -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

### Passo 2: Rodar o Projeto
Com o banco de dados rodando, você pode iniciar a aplicação Spring Boot com o seguinte comando:

```bash
./mvnw spring-boot:run
```

## Rodando com Docker

### Passo 1: Configurar o Host do Banco de Dados
No seu arquivo `application.yaml`, altere o host do banco de dados de `localhost` para `database`.

### Passo 2: Construir e Subir os Contêineres
Agora, execute o seguinte comando para construir e iniciar a aplicação e o banco de dados usando Docker Compose:

```bash
docker-compose up --build
```

Isso irá construir as imagens Docker e iniciar a aplicação junto com o banco de dados PostgreSQL.
