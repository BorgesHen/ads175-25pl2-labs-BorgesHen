# Biblioteca Digital — Navegação JSF (Quarkus)

Esta atualização implementa:

- **Dashboard `index.xhtml`** com estatísticas e visões gerais.
- **Páginas `autores.xhtml` e `livros.xhtml`** com listagem completa.
- **Template compartilhado** em `META-INF/resources/templates/template.xhtml` com cabeçalho, menu e rodapé.
- **`BibliotecaBean`** com `@Named`, `@ViewScoped`, `@PostConstruct`, injeção de `BibliotecaService` e *getters*.
- **`BibliotecaService`** com métodos de listagem e contagem (consultas JPA).

## Executar
```bash
./mvnw quarkus:dev
# Acesse http://localhost:8080
```

> Observação: Os *entities* `Autor`, `Livro` e `Emprestimo` devem existir no projeto conforme o enunciado. As consultas do serviço assumem:
> - `Livro.disponivel` (boolean).
> - `Emprestimo.dataDevolucao` nula **ou** campo `Emprestimo.ativo=true` para identificar empréstimos ativos.

## Navegação
- **Dashboard**: `/index.xhtml`
- **Autores**: `/autores.xhtml`
- **Livros**: `/livros.xhtml`

## Recarregar dados
Na página inicial, há um botão **"Recarregar Dados"** que chama `#{bibliotecaBean.recarregarDados()}`.
