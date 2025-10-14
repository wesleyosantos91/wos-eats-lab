# language: pt
Funcionalidade: Gerenciamento de Cozinhas (Kitchen)
  Como um usuário da plataforma
  Eu quero gerenciar cozinhas
  Para organizar os tipos de culinária disponíveis

  Contexto:
    Dado que o serviço de catálogo está disponível

  @smoke @regression
  Cenário: Criar uma nova cozinha com sucesso
    Quando eu crio uma cozinha com o nome "Italiana"
    Então a cozinha deve ser criada com sucesso
    E o status code deve ser 201
    E a resposta deve conter o nome "Italiana"

  @regression
  Cenário: Buscar uma cozinha por ID
    Dado que existe uma cozinha cadastrada com o nome "Japonesa"
    Quando eu busco a cozinha pelo ID
    Então o status code deve ser 200
    E a resposta deve conter o nome "Japonesa"

  @regression
  Cenário: Listar cozinhas com paginação
    Dado que existem as seguintes cozinhas cadastradas:
      | nome       |
      | Brasileira |
      | Mexicana   |
      | Tailandesa |
    Quando eu listo todas as cozinhas
    Então o status code deve ser 200
    E a resposta deve conter pelo menos 3 cozinhas

  @regression
  Cenário: Atualizar uma cozinha existente
    Dado que existe uma cozinha cadastrada com o nome "Chinesa"
    Quando eu atualizo o nome da cozinha para "Chinesa Tradicional"
    Então o status code deve ser 200
    E a resposta deve conter o nome "Chinesa Tradicional"

  @regression
  Cenário: Deletar uma cozinha
    Dado que existe uma cozinha cadastrada com o nome "Indiana"
    Quando eu deleto a cozinha
    Então o status code deve ser 204

  @regression
  Cenário: Tentar criar cozinha com nome duplicado
    Dado que existe uma cozinha cadastrada com o nome "Francesa"
    Quando eu tento criar uma cozinha com o nome "Francesa"
    Então o status code deve ser 409

  @regression
  Cenário: Buscar cozinha inexistente
    Quando eu busco uma cozinha com ID inexistente
    Então o status code deve ser 404
