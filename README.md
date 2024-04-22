# Sistema Escolar Avançado

Nesta API Rest, você encontrará uma solução para um sistema escolar. Neste sistema, há 5 **papéis de usuário**, cada um com usos e acessos específicos. Abaixo, você encontrará informações sobre o que cada um dos papéis faz e ao que eles têm acesso.

A API conta com diversas verificações e validações para garantir que cada **papel** só acesse aquilo que é permitido, além de **logs** de informações e erros.

Utilizando esta API, você pode fazer login no sistema recebendo um token JWT (para verificações de segurança), cadastrar novos usuários atribuindo um dos cinco papéis disponíveis. Buscar, cadastrar, e atualizar cada uma das entidades: docente, aluno, turma, curso, materia e nota, além de algumas outras funcionalidades para algumas das entidades, como por exemplo uma pontuação final para cada aluno.

Neste projeto, foram utilizadas as seguintes tecnologias:

- Spring Boot
- Spring Security
- Spring Data
- Maven
- Docker
- PostgreSQL

## Funcionalidades dos Papéis de Usuário

- **Admin**: acessa **TUDO** do sistema, além de ser o **ÚNICO** que pode deletar entidades.
- **Pedagógico**: acessa tudo sobre **TURMA** / **CURSO** / **PROFESSOR** / **ALUNO**.
- **Recruiter**: acessa tudo sobre **PROFESSOR**.
- **Professor**: acessa tudo sobre **NOTA**.
- **Aluno**: acessa suas próprias **NOTAS** e a sua pontuação total.

## Execução

### Configuração do Docker

Para rodar a API, você deve criar um contêiner com a imagem do PostgreSQL. Para fazer isso rapidamente, abra o Docker e, em seguida, abra o prompt de comando e cole o seguinte código:


```
docker run --hostname=e29f61f24ef8 --env=POSTGRES_PASSWORD=minhaSenha --env=POSTGRES_USER=meuUsuario --env=POSTGRES_DB=sistema-escolar --env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/postgresql/16/bin --env=GOSU_VERSION=1.17 --env=LANG=en_US.utf8 --env=PG_MAJOR=16 --env=PG_VERSION=16.2-1.pgdg120+2 --env=PGDATA=/var/lib/postgresql/data --volume=/var/lib/postgresql/data -p 1432:5432 --restart=no --runtime=runc -d postgres:latest
```

Isso irá criar um container novo com tudo nescessário, agora rode o container criado e siga as próximas instruções.


### Configuração do banco de dados

Para este projeto foi utilizado o pgAdmin 4, as instruções serão passadas para a utilização do mesmo:

    Para criar um novo server, escolha o nome que preferir e coloque as seguintes informações:
    Vá em Connection
    Host name/address: localhost
    Port: 1432
    Maintenance database: sistema-escolar
    Username: meuUsuario
    Password: minhaSenha
    Clique em Save

Após isso o bando de dados estará configurado e você poderá seguir para a próxima etapa.

### Executando a API

Para poder executar a API clone o repositório usando:

```
  git clone git@github.com:vXultz/sistema-escolar-avancado.git
```
Caso não dê para rodar a aplicação logo após o clone, faça o seguinte:

    1. Clique com o botão direito no arquivo **pom.xml**.
    2. Clique em **add as maven projet**, deve estar nas últimas opções.
   
 
Após isso a aplicação deve rodar normalmente.

### Requisições

Para facilidade, na pasta principal do repositório há o arquivo **Projeto Final.postman_collection.json**

Foi utilizado o programa **Postman** para fazer as requisições, com esse arquivo você pode importá-lo para o Postman e já ter pronto todas requisições possíveis.

## Documentação da API

Ao rodar a API pela primeira vez, será populado o banco de dados com os 5 papéis possíveis, assim como um usuário para cada um.

Os logins para cada um são:

    1. admin
    2. pedagogico
    3. recruiter
    4. professor
    5. aluno

A senha por padrão é **1234**

Para fazer login entre na requisição **Token** e coloque o login e senha do usuário desejado.

Para fazer uso das requisição copie o token jwt que você recebeu ao fazer login e cole na aba de Authorization da requisição, escolhendo a opção Bearer Token, lembre-se de fazer isso sempre que fizer login com outro usuário, assim você pode testar a fundo todas funcionalidades desta API.

Para cadastrar novos usuários você deve logar como um usuário **admin**.

O projeto roda na porta localhost:8080, caso você tenha importado o .json para o postman, essa porta vai estar configurada como uma variável de ambiente. No corpo de cada requisição terá todas informações necessárias para cadastrar ou atualizar algum item, como por exemplo no cadastro de um novo docente.

```
{
    "nome": "Nome do docente",
    "usuario": 1
}
```

### Lista das requisições

#### Retorna todos os itens

```http
  GET localhost:8080/nome da entidade/
```

| Descrição                           |
| :---------------------------------- |
| Retorna uma lista de itens da entidade em questão |

#### Retorna um item

```http
  GET localhost:8080/nome da entidade/{id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `id`      | Retorna busca o item com id em questão |

#### Cadastrar um item

```http
  POST localhost:8080/nome da entidade
```

| Descrição                                   |
| :------------------------------------------ |
| Cadastra um novo item na tabela em questão |

#### Atualizar um item

```http
  PUT localhost:8080/nome da entidade/{id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `id`      | Atualiza informações do item em questão |

#### Deletar  um item

```http
  DELETE localhost:8080/nome da entidade/{id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `id`      | Deleta o item em questão |


### Requisições adicionais

#### Buscar Materia por Curso id

```http
  GET localhost:8080/materia/cursos/{curso_id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `curso_id`      |Busca todas as matérias referente ao curso em questão |


#### Buscar Nota por Docente id

```http
  GET localhost:8080/nota/docentes/{docente_id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `docente_id`      |Busca todas as notas que o docente em questão lançou |

#### Buscar Nota por Aluno id

```http
  GET localhost:8080/nota/alunos/{aluno_id}
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `aluno_id`      |Busca todas as notas do aluno em questão |


#### Buscar Pontuação por Aluno id

```http
  GET localhost:8080/nota/alunos/{aluno_id}/pontuacao
```

| Parâmetro   | Descrição                                   |
| :---------- | :------------------------------------------ |
| `aluno_id`      |Busca a pontuação total do aluno em questão |
