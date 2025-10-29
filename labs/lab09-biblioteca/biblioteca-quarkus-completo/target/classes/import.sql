-- Inserir autores
INSERT INTO autores (id, nome, email, data_nascimento, biografia) VALUES
(1, 'José de Alencar', 'alencar@literatura.com', '1829-05-01', 'Escritor brasileiro, representante do Romantismo'),
(2, 'Machado de Assis', 'machado@academia.com', '1839-06-21', 'Fundador da Academia Brasileira de Letras'),
(3, 'Clarice Lispector', 'clarice@contos.com', '1920-12-10', 'Uma das mais importantes escritoras do século XX');

-- Inserir livros
INSERT INTO livros (id, titulo, isbn, data_publicacao, numero_paginas, disponivel, autor_id) VALUES
(1, 'O Guarani', '123-4567890123', '1857-01-01', 320, true, 1),
(2, 'Dom Casmurro', '123-4567890125', '1899-01-01', 256, false, 2),
(3, 'A Hora da Estrela', '123-4567890127', '1977-01-01', 96, true, 3);

-- Inserir empréstimos
INSERT INTO emprestimos (id, nome_usuario, email_usuario, data_emprestimo, data_devolucao_prevista, livro_id) VALUES
(1, 'João Santos', 'joao@email.com', '2024-11-10', '2024-11-24', 2),
(2, 'Ana Costa', 'ana@email.com', '2024-11-15', '2024-11-29', 3);
