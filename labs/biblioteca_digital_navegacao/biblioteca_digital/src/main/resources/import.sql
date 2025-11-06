
-- Inserir autores
INSERT INTO autores (nome, email, data_nascimento, biografia) VALUES
('José de Alencar', 'alencar@literatura.com', '1829-05-01', 'Escritor brasileiro, representante do Romantismo'),
('Machado de Assis', 'machado@academia.com', '1839-06-21', 'Fundador da Academia Brasileira de Letras'),
('Clarice Lispector', 'clarice@contos.com', '1920-12-10', 'Uma das mais importantes escritoras do século XX');

-- Inserir livros
INSERT INTO livros (titulo, isbn, data_publicacao, numero_paginas, disponivel, autor_id) VALUES
('O Guarani', '123-4567890123', '1857-01-01', 320, true, 1),
('Dom Casmurro', '123-4567890125', '1899-01-01', 256, false, 2),
('A Hora da Estrela', '123-4567890127', '1977-01-01', 96, true, 3);
