package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.model.Usuario;
import br.upf.ads175.critiquehub.exception.DadosDuplicadosException;
import br.upf.ads175.critiquehub.exception.EntidadeNaoEncontradaException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("Testes de Integração para UsuarioService")
class UsuarioServiceIntegrationTest {

    @Inject
    UsuarioService usuarioService;

    @Test
    @Transactional
    @DisplayName("Deve executar o ciclo CRUD completo para um usuário")
    void deveExecutarCicloCRUDCompleto() {
        // 1. CREATE
        Usuario usuarioCriado = usuarioService.criarUsuario(
            "crud.teste@email.com", "crudteste", "CRUD Teste"
        );
        assertNotNull(usuarioCriado.getId());

        // 2. READ
        Usuario usuarioLido = usuarioService.buscarPorIdObrigatorio(usuarioCriado.getId());
        assertEquals("crudteste", usuarioLido.getNomeUsuario());

        // 3. UPDATE
        String novaBio = "Biografia atualizada pelo teste.";
        Usuario usuarioAtualizado = usuarioService.atualizarPerfil(usuarioLido.getId(), usuarioLido.getNomeCompleto(), novaBio);
        assertEquals(novaBio, usuarioAtualizado.getBiografia());

        // 4. DELETE
        Long idDeletado = usuarioAtualizado.getId();
        usuarioService.removerDefinitivamente(idDeletado);

        // 5. VERIFY DELETE
        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            usuarioService.buscarPorIdObrigatorio(idDeletado);
        }, "Buscar um usuário deletado deve lançar EntidadeNaoEncontradaException");
    }

    @Test
    @Transactional
    @DisplayName("Não deve permitir a criação de um usuário com email duplicado")
    void naoDeveCriarUsuarioComEmailDuplicado() {
        // Given: Um usuário já existe com um email específico
        String emailExistente = "duplicado@email.com";
        usuarioService.criarUsuario(emailExistente, "user1", "Usuário Um");

        // When & Then: Tentar criar outro usuário com o mesmo email deve lançar uma exceção
        DadosDuplicadosException exception = assertThrows(DadosDuplicadosException.class, () -> {
            usuarioService.criarUsuario(emailExistente, "user2", "Usuário Dois");
        });

        // Opcional: Verificar a mensagem da exceção
        assertTrue(exception.getMessage().contains("Email já está em uso"));
    }

    @Test
    @Transactional
    @DisplayName("Deve lançar exceção ao buscar um usuário com ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        Long idInexistente = 9999L;
        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            usuarioService.buscarPorIdObrigatorio(idInexistente);
        });
    }
}
