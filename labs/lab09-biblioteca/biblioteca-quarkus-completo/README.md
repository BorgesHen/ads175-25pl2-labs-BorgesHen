    # Biblioteca Digital (Quarkus + Maven)

    ## Requisitos
    - Java 17+
- Maven
- PostgreSQL rodando em localhost:5432 com um banco `biblioteca_digital` e usuário `biblioteca`/`biblioteca123` (ou ajuste application.properties)

    ## Como executar
    1. Criar banco Postgres:
   - `CREATE DATABASE biblioteca_digital;`
   - Criar usuário/permissão conforme necessário.
2. Rodar em modo dev:
   - `./mvnw compile quarkus:dev`
3. Abrir no navegador:
   - `http://localhost:8080`

    Observações:
    - O Hibernate está configurado para `drop-and-create` e o script `import.sql` será executado automaticamente. Ajuste em `application.properties` para outro comportamento.
    - Para desenvolvimento rápido sem Postgres, altere `quarkus.datasource.db-kind` para `h2` e ajuste a URL.
