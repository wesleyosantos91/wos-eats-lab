# language: pt
Funcionalidade: Gerenciamento de Restaurantes (Restaurant)
  Como um usuário da plataforma
  Eu quero gerenciar restaurantes
  Para organizar os estabelecimentos disponíveis

  Contexto:
    Dado que o serviço de catálogo de restaurantes está disponível

  @smoke @regression
  Cenário: Criar um novo restaurante com sucesso
    Quando eu crio um restaurante com o nome "Pizzaria Don Luigi"
    Então o restaurante deve ser criado com sucesso
    E o status code do restaurante deve ser 201
    E a resposta do restaurante deve conter o nome "Pizzaria Don Luigi"

  @regression
  Cenário: Buscar um restaurante por ID
    Dado que existe um restaurante cadastrado com o nome "Sushi Yamamoto"
    Quando eu busco o restaurante pelo ID
    Então o status code do restaurante deve ser 200
    E a resposta do restaurante deve conter o nome "Sushi Yamamoto"

  @regression
  Cenário: Listar restaurantes com paginação
    Dado que existem os seguintes restaurantes cadastrados:
      | nome                |
      | Churrascaria Pampa  |
      | Tacos El Mariachi   |
      | Thai Garden         |
    Quando eu listo todos os restaurantes
    Então o status code do restaurante deve ser 200
    E a resposta deve conter pelo menos 3 restaurantes

  @regression
  Cenário: Atualizar um restaurante existente
    Dado que existe um restaurante cadastrado com o nome "Restaurante Pequim"
    Quando eu atualizo o nome do restaurante para "Restaurante Pequim Premium"
    Então o status code do restaurante deve ser 200
    E a resposta do restaurante deve conter o nome "Restaurante Pequim Premium"

  @regression
  Cenário: Deletar um restaurante
    Dado que existe um restaurante cadastrado com o nome "Curry Palace"
    Quando eu deleto o restaurante
    Então o status code do restaurante deve ser 204

  @regression
  Cenário: Tentar criar restaurante com nome duplicado
    Dado que existe um restaurante cadastrado com o nome "Le Petit Bistro"
    Quando eu tento criar um restaurante com o nome "Le Petit Bistro"
    Então o status code do restaurante deve ser 409

  @regression
  Cenário: Buscar restaurante inexistente
    Quando eu busco um restaurante com ID inexistente
    Então o status code do restaurante deve ser 404