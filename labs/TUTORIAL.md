
# Tutorial: Como Aplicar Bean Validation no Projeto de Biblioteca Digital

## Introdução
Este tutorial irá guiar você para adicionar validações nas entidades do projeto **Biblioteca Digital** usando **Bean Validation** (jakarta.validation). A validação será aplicada para garantir que os dados inseridos nas entidades **Autor** e **Livro** sigam regras de negócios, como limitar o tamanho de campos de texto e garantir que o e-mail do autor seja único.

Essas validações melhoram a integridade dos dados e ajudam a evitar problemas com dados inconsistentes, facilitando a manutenção e escalabilidade do sistema.

---

## Pré-requisitos
- O projeto **Sistema de Biblioteca Digital** já clonado e funcionando.
- Java 11 ou superior.
- Maven instalado no sistema.
- O projeto deve estar configurado para rodar com **Quarkus** e **PostgreSQL** (configuração do banco de dados conforme fornecido anteriormente).
- Dependência do **Jakarta Bean Validation** já incluída (verifique se `quarkus-hibernate-orm` já está no `pom.xml`).

---

## Passo a Passo

### Seção 1: Adicionando Dependências
O **Bean Validation** é parte da **Jakarta EE** e, no Quarkus, pode ser facilmente integrado com a dependência `quarkus-hibernate-orm`, que já inclui o suporte necessário. Se não estiver presente, adicione-a ao arquivo `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm</artifactId>
</dependency>

<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>

<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
</dependency>
```

Essas dependências fornecem as anotações e implementações para as validações.

### Seção 2: Modificando a Entidade Autor
Vamos adicionar as validações na entidade **Autor**. Abaixo estão as modificações necessárias para adicionar validação nos campos de **nome**, **email** e **dataNascimento**.

Abra o arquivo `Autor.java` e aplique as anotações de Bean Validation:

```java
package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    private LocalDate dataNascimento;

    @Size(max = 500, message = "A biografia não pode ter mais de 500 caracteres")
    private String biografia;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Livro> livros = new ArrayList<>();

}
```

**Explicação**:
- **@NotBlank**: Garantimos que o campo **nome** e **email** não sejam nulos ou em branco.
- **@Size**: Definimos um limite de tamanho para **nome** (100 caracteres) e **biografia** (500 caracteres).
- **@Email**: Validamos o formato do e-mail.
- **@NotNull**: Poderia ser usado em outras situações, caso necessário.

### Seção 3: Modificando a Entidade Livro
Agora vamos adicionar validações na entidade **Livro**. A entidade **Livro** deve garantir que o **título** e o **isbn** sejam válidos e não vazios.

Abra o arquivo `Livro.java` e aplique as anotações de Bean Validation:

```java
package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "O título não pode ter mais de 200 caracteres")
    private String titulo;

    @NotBlank(message = "ISBN é obrigatório")
    @Size(min = 13, max = 13, message = "O ISBN deve ter exatamente 13 caracteres")
    private String isbn;

    private LocalDate dataPublicacao;
    private Integer numeroPaginas;

    @NotNull(message = "A disponibilidade do livro é obrigatória")
    private Boolean disponivel = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos;

}
```

**Explicação**:
- **@NotBlank**: Garantimos que o campo **título** e **isbn** não sejam nulos ou em branco.
- **@Size**: Definimos o tamanho do **ISBN** (13 caracteres) e o limite de caracteres para o **título**.
- **@NotNull**: Garantimos que o campo **disponível** seja informado.

### Seção 4: Validando os Dados no Controller
Agora que as entidades estão validadas, precisamos garantir que as mensagens de erro de validação sejam exibidas na interface do usuário. Abra o arquivo `BibliotecaBean.java` e adicione a validação ao salvar ou atualizar um autor ou livro.

```java
package com.biblioteca.controller;

import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Livro;
import com.biblioteca.service.BibliotecaService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.faces.view.ViewScoped;

@Named("bibliotecaBean")
@ViewScoped
public class BibliotecaBean {

    @Inject
    private BibliotecaService bibliotecaService;

    @NotNull
    private Autor autor;

    @NotNull
    private Livro livro;

    public void salvarAutor() {
        if (autor != null) {
            try {
                bibliotecaService.salvarAutor(autor);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Autor salvo com sucesso"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar Autor", e.getMessage()));
            }
        }
    }
}
```

---

## Verificando o Resultado

1. **Execute o projeto no modo de desenvolvimento**:
        bash
   ./mvnw compile quarkus:dev

2. **Acesse a aplicação** no navegador: [http://localhost:8080](http://localhost:8080).

3. **Testando a validação**:
   - Ao adicionar ou editar um **Autor** ou **Livro**, preencha os campos com dados inválidos (como um nome vazio ou um e-mail inválido) e tente salvar.
   - O sistema deverá exibir mensagens de erro de validação no front-end.

4. **Verificando mensagens de erro**:
   - Se os dados forem inválidos, as mensagens de erro definidas nas anotações de validação serão exibidas.


