## Aplicação disponivel em:
https://colanapista.vercel.app/pages/login.html

## Usuarios para teste: 

#### Admin
Email: Professor@gmail.com <br>
Senha: Professor@2025

#### Usuario
Email: Checkin@gmail.com <br>
Senha: Checkin@123

## Roteiro para Avaliadores (professores)
Use o passo a passo abaixo como guia ao testar o frontend durante as avaliacoes da disciplina. Ele cobre as funcionalidades principais para os perfis de usuario padrao e administrador.

### 1. Login e criacao de contas
- Utilize as credenciais compartilhadas com o professor Diogo para acessar rapidamente a aplicacao.
- Caso prefira testar com novos perfis, registre-se como novo usuario atraves da tela de login ou ja com usuario **Admin** logado, abra **Usuarios > Novo Usuario** e cadastre um usuario padrao ou marque a opcao **Admin** para validar permissoes elevadas.

### 2. Tela de Reservas (`pages/reserva.html`)
- A lista central mostra todas as reservas vinculadas ao seu usuario; administradores visualizam o consolidado de toda a base.
- Ao clicar em qualquer data do calendario surgem no grafico de barras abaixo os turnos livres/ocupados daquele dia, facilitando identificar o menor indice de ocupação.
- Clique em **Realizar uma reserva** para abrir o formulario e preencha campos obrigatorios (pista, turno, forma de pagamento).

### 3. Check-in e atualizacoes de status
- No dia do agendamento, quando o usuario executar o login ira apresentar uma tela obrigando o usuario a realizar o **Check-in** antes de uma nova reserva.
- Reservas que ja passaram nao podem ser canceladas pelo usuario final, o status da mesma ficara como **Não Realizado**; apenas agendamentos futuros exibem o atalho de cancelamento.

### 4. Cancelamentos e edicoes
- Usuarios comuns podem cancelar somente suas proprias reservas futuras; o card apresenta um icone de lixeira.
- Administradores conseguem cancelar reservas futuras de qualquer usuario e alterar dados cadastrais em **Usuarios > Editar**.
- Tambem e possivel promover perfis a **Admin** diretamente na tela de edicao, acelerando os testes com permissoes diferentes.

### 5. Painel administrativo
- Acesse `pages/dashboard.html` para visualizar indicadores de reservas por periodo, modalidade de pagamento e evolucao diaria.
- Utilize o filtro superior (intervalo de datas) para provar que os graficos sao recalculados conforme o periodo selecionado; apenas administradores veem esta tela.

> Sugestao: percorra o fluxo completo criando uma reserva, realizando o check-in e, em seguida, cancelando como administrador para observar todas as transicoes de status.

# --------------
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
