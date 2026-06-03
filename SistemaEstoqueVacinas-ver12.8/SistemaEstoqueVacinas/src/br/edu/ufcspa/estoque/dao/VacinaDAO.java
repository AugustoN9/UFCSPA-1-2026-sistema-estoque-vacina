package br.edu.ufcspa.estoque.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import br.edu.ufcspa.estoque.model.Vacina;

/**
 * Classe de persistência v5.0 - Sincronizada e Corrigida.
 */
public class VacinaDAO {

	public boolean salvar(Vacina vacina) {
        // 1. GATILHO DE SEGURANÇA v12.5: Valida se o LOTE já existe para não quebrar a restrição UNIQUE do banco
        String sqlCheck = "SELECT id FROM imunizante WHERE numero_lote = ?";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {
             
            stmtCheck.setString(1, vacina.getLote());
            try (ResultSet rsCheck = stmtCheck.executeQuery()) {
                if (rsCheck.next()) {
                    System.err.println("REJEITADO: Já existe um imunizante cadastrado com o número de lote: " + vacina.getLote());
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao validar unicidade de lote: " + e.getMessage());
            return false;
        }

        // 2. INSERT ATUALIZADO: Inclui os campos reais de data identificados no inventário físico
        String sql = "INSERT INTO imunizante (nome_comercial, antigeno_alvo, laboratorio_fabricante, " +
                     "plataforma_tecnologica, numero_lote, data_validade, pais_origem, " +
                     "numero_registro_anvisa, fornecedor, apresentacao, quantidade_frascos_estoque, " +
                     "calculo_total_doses, estoque_minimo_alerta, faixa_temperatura, " +
                     "tempo_estabilidade, status_liberacao, id_colaborador, data_chegada, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Preenche as 16 primeiras colunas usando o seu método auxiliar estático
            preencherStatement(stmt, vacina);
            
            // Seta o ID do colaborador logado na 17ª posição
            stmt.setInt(17, vacina.getIdColaborador());
            
            // Seta a data de chegada (puxando a validade formatada ou data atual como fallback) na 18ª posição
            stmt.setString(18, vacina.getValidade()); 

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar imunizante no inventário v12.5: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(Vacina vacina) {
        // CORREÇÃO: nome_comercial entra no SET para poder ser alterado, e o WHERE busca pelo ID fixo
        String sql = "UPDATE imunizante SET nome_comercial=?, antigeno_alvo=?, laboratorio_fabricante=?, " +
                     "plataforma_tecnologica=?, numero_lote=?, data_validade=?, pais_origem=?, " +
                     "numero_registro_anvisa=?, fornecedor=?, apresentacao=?, quantidade_frascos_estoque=?, " +
                     "calculo_total_doses=?, estoque_minimo_alerta=?, faixa_temperatura=?, " +
                     "tempo_estabilidade=?, status_liberacao=? WHERE id=?"; 

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ajuste dos índices (Adicionado o nome no índice 1, empurrando os outros)
            stmt.setString(1, vacina.getNome()); 
            stmt.setString(2, vacina.getAntigenoAlvo());
            stmt.setString(3, vacina.getFabricante());
            stmt.setString(4, vacina.getPlataforma());
            stmt.setString(5, vacina.getLote());
            stmt.setString(6, vacina.getValidade());
            stmt.setString(7, vacina.getPaisOrigem());
            stmt.setString(8, vacina.getRegistroAnvisa());
            stmt.setString(9, vacina.getFornecedor());
            stmt.setString(10, vacina.getApresentacao());
            stmt.setInt(11, vacina.getQuantidade());
            stmt.setInt(12, vacina.getTotalDoses());
            stmt.setInt(13, vacina.getEstoqueMinimo());
            stmt.setString(14, vacina.getFaixaTemperatura());
            stmt.setString(15, vacina.getEstabilidade()); 
            stmt.setString(16, vacina.getStatusLiberacao());
            stmt.setInt(17, vacina.getId()); 

            int lines = stmt.executeUpdate();
            return lines > 0;
        } catch (SQLException e) {
            System.err.println("Erro crítico ao atualizar: " + e.getMessage());
            return false;
        }
    }
    
    private void preencherStatement(PreparedStatement stmt, Vacina v) throws SQLException {
        stmt.setString(1, v.getNome());
        stmt.setString(2, v.getAntigenoAlvo());
        stmt.setString(3, v.getFabricante());
        stmt.setString(4, v.getPlataforma());
        stmt.setString(5, v.getLote());
        stmt.setString(6, v.getValidade());
        stmt.setString(7, v.getPaisOrigem());
        stmt.setString(8, v.getRegistroAnvisa());
        stmt.setString(9, v.getFornecedor());
        stmt.setString(10, v.getApresentacao());
        stmt.setInt(11, v.getQuantidade());
        stmt.setInt(12, v.getTotalDoses());
        stmt.setInt(13, v.getEstoqueMinimo());
        stmt.setString(14, v.getFaixaTemperatura());
        stmt.setString(15, v.getEstabilidade());
        stmt.setString(16, v.getStatusLiberacao());
        stmt.setInt(17, v.getId());
    }

    public Vacina buscarPorNome(String nome) {
        String sql = "SELECT * FROM imunizante WHERE nome_comercial = ?";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vacina v = new Vacina();
                    
                    v.setId(rs.getInt("id"));
                    v.setNome(rs.getString("nome_comercial"));
                    v.setAntigenoAlvo(rs.getString("antigeno_alvo"));
                    v.setFabricante(rs.getString("laboratorio_fabricante"));
                    
                    v.setNome(rs.getString("nome_comercial"));
                    v.setAntigenoAlvo(rs.getString("antigeno_alvo"));
                    v.setFabricante(rs.getString("laboratorio_fabricante"));
                    v.setPlataforma(rs.getString("plataforma_tecnologica"));
                    v.setLote(rs.getString("numero_lote"));
                    v.setValidade(rs.getString("data_validade"));
                    v.setPaisOrigem(rs.getString("pais_origem"));
                    v.setRegistroAnvisa(rs.getString("numero_registro_anvisa"));
                    v.setFornecedor(rs.getString("fornecedor"));
                    v.setApresentacao(rs.getString("apresentacao"));
                    v.setQuantidade(rs.getInt("quantidade_frascos_estoque"));
                    v.setTotalDoses(rs.getInt("calculo_total_doses"));
                    v.setEstoqueMinimo(rs.getInt("estoque_minimo_alerta"));
                    v.setFaixaTemperatura(rs.getString("faixa_temperatura"));
                    v.setEstabilidade(rs.getString("tempo_estabilidade"));
                    v.setStatusLiberacao(rs.getString("status_liberacao"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por nome: " + e.getMessage());
        }
        return null;
    }

    public List<String> buscarNomesComerciais() {
        List<String> nomes = new ArrayList<>();
        String sql = "SELECT nome_comercial FROM imunizante ORDER BY nome_comercial ASC";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nomes.add(rs.getString("nome_comercial"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar nomes: " + e.getMessage());
        }
        return nomes;
    }

    public List<Vacina> listarEstoqueCompleto() {
        List<Vacina> lista = new ArrayList<>();
        String sql = "SELECT i.*, c.nome as nome_colaborador, " +
                     "(SELECT COALESCE(SUM(p.quantidade_frascos), 0) FROM historico_perdas p WHERE p.numero_lote = i.numero_lote) as total_descartes " +
                     "FROM imunizante i " +
                     "LEFT JOIN colaborador c ON i.id_colaborador = c.id_colaborador " +
                     "ORDER BY i.nome_comercial ASC";

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vacina v = new Vacina();
                v.setNome(rs.getString("nome_comercial"));
                v.setLote(rs.getString("numero_lote"));
                v.setQuantidade(rs.getInt("quantidade_frascos_estoque"));
                v.setEstoqueMinimo(rs.getInt("estoque_minimo_alerta"));
                v.setValidade(rs.getString("data_validade"));
                v.setNomeColaborador(rs.getString("nome_colaborador"));
                
                // Usaremos temporariamente um campo livre ou passaremos o valor customizado
                v.setId(rs.getInt("total_descartes")); // Guardando temporariamente o valor aqui para não alterar o model
                
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estoque completo com perdas: " + e.getMessage());
        }
        return lista;
    }
    
    public List<Object[]> buscarDetalhesLotesAlerta() {
        List<Object[]> lista = new java.util.ArrayList<>();
        
        // Query idêntica à que gera o número 4 no seu painel de KPIs
        String sql = "SELECT nome_comercial, numero_lote, quantidade_frascos_estoque, estoque_minimo_alerta " +
                     "FROM imunizante " +
                     "WHERE quantidade_frascos_estoque <= estoque_minimo_alerta";
        
        try (Connection conn = ConexaoMySQL.criarConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                // Extração segura por índice posicional da Query (1, 2, 3, 4)
                lista.add(new Object[]{
                    rs.getString(1), // nome
                    rs.getString(2), // lote
                    rs.getInt(3),    // quantidade_frascos_estoque
                    rs.getInt(4)     // estoque_minimo_alerta
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro crítico ao buscar detalhes de alertas: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean excluir(String nome, String lote) {
        String sql = "DELETE FROM imunizante WHERE nome_comercial = ? AND numero_lote = ?";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, lote);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir vacina: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarConsumoPorQRCode(String lote, int quantidadeConsumida, String local, int idUsuario) {
    Vacina v = null;
    String sqlBusca = "SELECT quantidade_frascos_estoque, calculo_total_doses FROM imunizante WHERE numero_lote = ?";
    
    Connection conn = null;
    try {
        conn = ConexaoMySQL.criarConexao();
        conn.setAutoCommit(false); // Inicia uma transação segura

        // 1. Busca os dados atuais do imunizante
        try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {
            stmtBusca.setString(1, lote);
            try (ResultSet rs = stmtBusca.executeQuery()) {
                if (rs.next()) {
                    v = new Vacina();
                    v.setQuantidade(rs.getInt("quantidade_frascos_estoque"));
                    v.setTotalDoses(rs.getInt("calculo_total_doses"));
                }
            }
        }

        // Valida se o lote existe e se há estoque suficiente
        if (v == null || v.getQuantidade() < quantidadeConsumida) {
            conn.rollback();
            return false;
        }

        // Calcula a proporção de doses
        int fatorDosesPorFrasco = 1;
        if (v.getQuantidade() > 0 && v.getTotalDoses() > 0) {
            fatorDosesPorFrasco = v.getTotalDoses() / v.getQuantidade();
        }

        int novaQtdFrascos = v.getQuantidade() - quantidadeConsumida;
        int novoTotalDoses = v.getTotalDoses() - (quantidadeConsumida * fatorDosesPorFrasco);
        if (novoTotalDoses < 0) novoTotalDoses = 0;

        // 2. Atualiza a tabela 'imunizante'
        String sqlUpdate = "UPDATE imunizante SET quantidade_frascos_estoque = ?, calculo_total_doses = ? WHERE numero_lote = ?";
        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setInt(1, novaQtdFrascos);
            stmtUpdate.setInt(2, novoTotalDoses);
            stmtUpdate.setString(3, lote);
            stmtUpdate.executeUpdate();
        }

        // 3. Insere o registro na tabela 'historico_consumo'
        String sqlHistorico = "INSERT INTO historico_consumo (numero_lote, quantidade_frascos, local_imunizacao, data_hora_consumo, id_colaborador) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmtHist = conn.prepareStatement(sqlHistorico)) {
            stmtHist.setString(1, lote);
            stmtHist.setInt(2, quantidadeConsumida);
            stmtHist.setString(3, local);
            // Passa a data e hora atual do sistema para o MySQL
            stmtHist.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmtHist.setInt(5, idUsuario);
            stmtHist.executeUpdate();
        }

        conn.commit(); // Confirma as duas alterações com sucesso
        return true;

	    } catch (SQLException e) {
	        System.err.println("Erro na transação de baixa via QR Code: " + e.getMessage());
	        if (conn != null) {
	            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
	        }
	        return false;
	    } finally {
	        if (conn != null) {
	            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
	    }
    }
    
    public List<Object[]> listarConsumoPorColaborador(int idColaborador) {
        List<Object[]> lista = new ArrayList<>();
        String sql = 
        		"SELECT h.data_hora_consumo, i.nome_comercial, h.numero_lote, h.quantidade_frascos, h.local_imunizacao, 'CONSUMO' as tipo " +
                "FROM historico_consumo h " +
                "JOIN imunizante i ON h.numero_lote = i.numero_lote " +
                "WHERE h.id_colaborador = ? " +
                "UNION ALL " +
                "SELECT p.data_hora_perda, i.nome_comercial, p.numero_lote, p.quantidade_frascos, p.local_ocorrencia, 'DESCARTE' as tipo " +
                "FROM historico_perdas p " +
                "JOIN imunizante i ON p.numero_lote = i.numero_lote " +
                "WHERE p.id_colaborador = ? " +
                "ORDER BY data_hora_consumo DESC";

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idColaborador);
            stmt.setInt(2, idColaborador);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getTimestamp("data_hora_consumo"),
                        rs.getString("nome_comercial"),
                        rs.getString("numero_lote"),
                        rs.getInt("quantidade_frascos"),
                        rs.getString("local_imunizacao"),
                        rs.getString("tipo")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar consumo pessoal: " + e.getMessage());
        }
        return lista;
    }
    
    // =====================================================================
    // NOVO MÉTODO V9.7: PERSISTÊNCIA DE DESCARTE SANITÁRIO E CONTROLE DE PERDAS
    // =====================================================================
    public boolean registrarPerdaEstoque(String lote, int quantidadePerdida, String motivo, String local, int idUsuario) {
        Vacina v = null;
        String sqlBusca = "SELECT quantidade_frascos_estoque, calculo_total_doses FROM imunizante WHERE numero_lote = ?";
        
        Connection conn = null;
        try {
            conn = ConexaoMySQL.criarConexao();
            conn.setAutoCommit(false); // Inicia uma transação segura (ACID)

            // 1. Busca os saldos atuais do imunizante pelo lote
            try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {
                stmtBusca.setString(1, lote);
                try (ResultSet rs = stmtBusca.executeQuery()) {
                    if (rs.next()) {
                        v = new Vacina();
                        v.setQuantidade(rs.getInt("quantidade_frascos_estoque"));
                        v.setTotalDoses(rs.getInt("calculo_total_doses"));
                    }
                }
            }

            // Valida se o lote existe e se há saldo em estoque suficiente para a perda
            if (v == null || v.getQuantidade() < quantidadePerdida) {
                conn.rollback();
                return false;
            }

            // Calcula a proporção de doses por frasco para abater o total proporcionalmente
            int fatorDosesPorFrasco = 1;
            if (v.getQuantidade() > 0 && v.getTotalDoses() > 0) {
                fatorDosesPorFrasco = v.getTotalDoses() / v.getQuantidade();
            }

            int novaQtdFrascos = v.getQuantidade() - quantidadePerdida;
            int novoTotalDoses = v.getTotalDoses() - (quantidadePerdida * fatorDosesPorFrasco);
            if (novoTotalDoses < 0) novoTotalDoses = 0;

            // 2. Deduz os frascos e as doses da tabela mestre 'imunizante'
            String sqlUpdate = "UPDATE imunizante SET quantidade_frascos_estoque = ?, calculo_total_doses = ? WHERE numero_lote = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setInt(1, novaQtdFrascos);
                stmtUpdate.setInt(2, novoTotalDoses);
                stmtUpdate.setString(3, lote);
                stmtUpdate.executeUpdate();
            }

            // 3. Grava o log detalhado com o motivo na nova tabela 'historico_perdas'
            String sqlPerda = "INSERT INTO historico_perdas (numero_lote, quantidade_frascos, motivo_perda, local_ocorrencia, data_hora_perda, id_colaborador) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtPerda = conn.prepareStatement(sqlPerda)) {
                stmtPerda.setString(1, lote);
                stmtPerda.setInt(2, quantidadePerdida);
                stmtPerda.setString(3, motivo);
                stmtPerda.setString(4, local);
                // Registra data e hora exatas da ocorrência
                stmtPerda.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                stmtPerda.setInt(6, idUsuario);
                stmtPerda.executeUpdate();
            }

            conn.commit(); // Efetiva as duas atualizações juntas com sucesso
            return true;

        } catch (SQLException e) {
            System.err.println("Erro na transação de descarte técnico (DAO): " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    public List<Object[]> listarRelatorioConsumoGeral() {
        List<Object[]> lista = new ArrayList<>();
        // Faz o JOIN para resgatar o nome amigável do imunizante e do colaborador responsável
        String sql = "SELECT h.data_hora_consumo, c.nome AS operador, i.nome_comercial, h.numero_lote, h.quantidade_frascos, h.local_imunizacao " +
                     "FROM historico_consumo h " +
                     "JOIN imunizante i ON h.numero_lote = i.numero_lote " +
                     "JOIN colaborador c ON h.id_colaborador = c.id_colaborador " +
                     "ORDER BY h.data_hora_consumo DESC";

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getTimestamp("data_hora_consumo"),
                    rs.getString("operador"),
                    rs.getString("nome_comercial"),
                    rs.getString("numero_lote"),
                    rs.getInt("quantidade_frascos"),
                    rs.getString("local_imunizacao")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório geral: " + e.getMessage());
        }
        return lista;
    }
    
    public List<String> buscarPostosSaude() {
        List<String> postos = new ArrayList<>();
        String sql = "SELECT nome_posto FROM posto_saude ORDER BY nome_posto ASC";
        
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                postos.add(rs.getString("nome_posto"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar postos de saúde: " + e.getMessage());
        }
        return postos;
    }
    

    public List<Object[]> filtrarRelatorioConsumo(String data, String operador, String imunizante, String local) {
        List<Object[]> lista = new ArrayList<>();
        
        // Query base utilizando os JOINs que você já homologou
        StringBuilder sql = new StringBuilder(
            "SELECT h.data_hora_consumo, c.nome AS operador, i.nome_comercial, h.numero_lote, h.quantidade_frascos, h.local_imunizacao " +
            "FROM historico_consumo h " +
            "JOIN imunizante i ON h.numero_lote = i.numero_lote " +
            "JOIN colaborador c ON h.id_colaborador = c.id_colaborador WHERE 1=1 "
        );

        // 1. MONTAGEM DINÂMICA DA QUERY (Ignora o filtro se for nulo, vazio ou "Todos")
        if (data != null && !data.trim().isEmpty()) {
            sql.append("AND DATE_FORMAT(h.data_hora_consumo, '%d/%m/%Y') = ? ");
        }
        if (operador != null && !operador.trim().isEmpty() && !operador.equals("Todos")) {
            sql.append("AND c.nome = ? ");
        }
        if (imunizante != null && !imunizante.trim().isEmpty() && !imunizante.equals("Todos")) {
            sql.append("AND i.nome_comercial = ? ");
        }
        if (local != null && !local.trim().isEmpty() && !local.equals("Todos")) {
            sql.append("AND h.local_imunizacao = ? ");
        }
        
        sql.append("ORDER BY h.data_hora_consumo DESC");

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            // 2. PREENCHIMENTO SEGURO DOS PARÂMETROS
            if (data != null && !data.trim().isEmpty()) {
                stmt.setString(paramIndex++, data);
            }
            if (operador != null && !operador.trim().isEmpty() && !operador.equals("Todos")) {
                stmt.setString(paramIndex++, operador);
            }
            if (imunizante != null && !imunizante.trim().isEmpty() && !imunizante.equals("Todos")) {
                stmt.setString(paramIndex++, imunizante);
            }
            if (local != null && !local.trim().isEmpty() && !local.equals("Todos")) {
                stmt.setString(paramIndex++, local);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getTimestamp("data_hora_consumo"),
                        rs.getString("operador"),
                        rs.getString("nome_comercial"),
                        rs.getString("numero_lote"),
                        rs.getInt("quantidade_frascos"),
                        rs.getString("local_imunizacao")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro crítico ao filtrar relatório no banco: " + e.getMessage());
        }
        return lista;
    }
    
    public List<String> buscarOperadoresDoHistorico() {
        List<String> operadores = new ArrayList<>();
        // Busca os nomes únicos (DISTINCT) de quem tem registro na tabela historico_consumo
        String sql = "SELECT DISTINCT c.nome FROM historico_consumo h " +
                     "JOIN colaborador c ON h.id_colaborador = c.id_colaborador " +
                     "ORDER BY c.nome ASC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                operadores.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar operadores do histórico: " + e.getMessage());
        }
        return operadores;
    }
    
 // 1. QUERY PARA OS VALORES DOS CARTÕES (KPIs)
    public int[] buscarMetricasKPIs() {
        int[] kpis = new int[3]; // [0] = Total Frascos, [1] = Total Postos, [2] = Lotes Alerta
        
        String sqlConsumo = "SELECT SUM(quantidade_frascos) FROM historico_consumo";
        String sqlPostos = "SELECT COUNT(DISTINCT local_imunizacao) FROM historico_consumo";
        String sqlAlerta = "SELECT COUNT(*) FROM imunizante WHERE quantidade_frascos_estoque <= estoque_minimo_alerta";

        try (Connection conn = ConexaoMySQL.criarConexao()) {
            // Busca Total Frascos
            try (PreparedStatement stmt = conn.prepareStatement(sqlConsumo); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) kpis[0] = rs.getInt(1);
            }
            // Busca Total Postos Atendidos
            try (PreparedStatement stmt = conn.prepareStatement(sqlPostos); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) kpis[1] = rs.getInt(1);
            }
            // Busca Lotes em Alerta Mínimo
            try (PreparedStatement stmt = conn.prepareStatement(sqlAlerta); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) kpis[2] = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar métricas de KPIs: " + e.getMessage());
        }
        return kpis;
    }

    // 2. QUERY PARA O GRÁFICO DE BARRAS (Consumo por Posto)
    public java.util.Map<String, Integer> buscarConsumoPorPosto() {
        java.util.Map<String, Integer> dados = new java.util.LinkedHashMap<>();
        String sql = "SELECT local_imunizacao, SUM(quantidade_frascos) AS total " +
                     "FROM historico_consumo GROUP BY local_imunizacao ORDER BY total DESC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dados.put(rs.getString("local_imunizacao"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar consumo por posto: " + e.getMessage());
        }
        return dados;
    }

    // 3. QUERY PARA O GRÁFICO DE PIZZA (Consumo por Imunizante)
    public java.util.Map<String, Integer> buscarConsumoPorImunizante() {
        java.util.Map<String, Integer> dados = new java.util.LinkedHashMap<>();
        String sql = "SELECT i.nome_comercial, SUM(h.quantidade_frascos) AS total " +
                     "FROM historico_consumo h " +
                     "JOIN imunizante i ON h.numero_lote = i.numero_lote " +
                     "GROUP BY i.nome_comercial ORDER BY total DESC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dados.put(rs.getString("nome_comercial"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar consumo por imunizante: " + e.getMessage());
        }
        return dados;
    }
    
    // =====================================================================
    // NOVO MÉTODO AUDITORIA v10.2: DETALHAMENTO DE DESCARTE POR LOTE
    // =====================================================================
    public List<Object[]> buscarDetalhesDescarteLote(String lote) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.data_hora_perda, c.nome AS operador, p.motivo_perda, p.local_ocorrencia, p.quantidade_frascos " +
                     "FROM historico_perdas p " +
                     "LEFT JOIN colaborador c ON p.id_colaborador = c.id_colaborador " +
                     "WHERE p.numero_lote = ? " +
                     "ORDER BY p.data_hora_perda DESC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, lote);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getTimestamp("data_hora_perda"),
                        rs.getString("operador") != null ? rs.getString("operador") : "Não Identificado",
                        rs.getString("motivo_perda"),
                        rs.getString("local_ocorrencia"),
                        rs.getInt("quantidade_frascos")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao auditar lote de descarte: " + e.getMessage());
        }
        return lista;
    }
    
    // =====================================================================
    // METRICAS v10.5: BUSCA TOTAL DE DESCARTES E DISTRIBUIÇÃO POR MOTIVO
    // =====================================================================
    public int buscarTotalFrascosDescartados() {
        String sql = "SELECT COALESCE(SUM(quantidade_frascos), 0) AS total FROM historico_perdas";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar KPI de descartes: " + e.getMessage());
        }
        return 0;
    }

    public java.util.Map<String, Integer> buscarDescartesPorMotivo() {
        java.util.Map<String, Integer> dados = new java.util.LinkedHashMap<>();
        String sql = "SELECT motivo_perda, SUM(quantidade_frascos) AS total " +
                     "FROM historico_perdas " +
                     "GROUP BY motivo_perda " +
                     "ORDER BY total DESC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dados.put(rs.getString("motivo_perda"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar motivos de descarte para gráfico: " + e.getMessage());
        }
        return dados;
    }
    
    // =====================================================================
    // CONTROLE LOGÍSTICO v12.4: RECOMENDAÇÃO COM FILTRO DE STATUS ATUALIZADO
    // =====================================================================
    public java.util.List<Object[]> buscarSugestoesDeAcordoComEstoque() {
        java.util.List<Object[]> listaRecomendacoes = new java.util.ArrayList<>();
        
        // Query v12.4: A subquery agora filtra APENAS por solicitações com status 'PENDENTE'
        String sql = "SELECT i.nome_comercial, i.data_validade, i.quantidade_frascos_estoque, i.estoque_minimo_alerta, " +
                     "       (SELECT COALESCE(SUM(h.quantidade_frascos), 0) FROM historico_consumo h WHERE h.numero_lote = i.numero_lote) AS total_consumido, " +
                     "       CASE WHEN (SELECT COUNT(*) FROM solicitacao_compra sc WHERE sc.nome_imunizante COLLATE utf8mb4_unicode_ci = i.nome_comercial AND sc.status_solicitacao = 'PENDENTE') > 0 " +
                     "            THEN 'SOLICITAÇÃO REALIZADA' ELSE 'PENDENTE' END AS status_operacional " +
                     "FROM imunizante i";

        java.text.SimpleDateFormat formatadorBR = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dataAtual = new java.util.Date();

        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome_comercial");
                int qtdFisicaReal = rs.getInt("quantidade_frascos_estoque");
                int minimo = rs.getInt("estoque_minimo_alerta");
                int saidas = rs.getInt("total_consumido");
                String statusOperacional = rs.getString("status_operacional");
                String validadeStr = rs.getString("data_validade");
                
                int qtdValida = qtdFisicaReal;
                boolean ehVencido = false;

                if (validadeStr != null && !validadeStr.trim().isEmpty()) {
                    try {
                        java.util.Date dataValidadeConvertida = formatadorBR.parse(validadeStr.trim());
                        if (dataValidadeConvertida.before(dataAtual)) {
                            ehVencido = true;
                            qtdValida = 0; 
                        }
                    } catch (java.text.ParseException pe) {
                        // Ignora inconsistências de data no log
                    }
                }

                if (ehVencido || qtdFisicaReal <= minimo) {
                    String prioridade = "ATENÇÃO";
                    String acaoRecomendada = "Planejar inclusão na próxima remessa trimestral.";
                    
                    if (ehVencido) {
                        prioridade = "URGENTE";
                        acaoRecomendada = "LOTE VENCIDO EM ESTOQUE (" + qtdFisicaReal + " fr.). Recolher para descarte e solicitar reposição imediata.";
                    } else if (qtdFisicaReal == 0) {
                        prioridade = "URGENTE";
                        acaoRecomendada = "ESTOQUE COMPLETAMENTE ZERADO! Risco de desabastecimento. Emitir ordem de compra emergencial.";
                    } else if (qtdFisicaReal <= (minimo / 2) && saidas > 20) {
                        prioridade = "URGENTE";
                        acaoRecomendada = "Alto consumo nas unidades de saúde. Solicitar reposição imediata.";
                    } else if (qtdFisicaReal <= minimo) {
                        prioridade = "CRÍTICO";
                        acaoRecomendada = "Estoque de segurança violado. Solicitar nova remessa de insumos regulamentares.";
                    }

                    listaRecomendacoes.add(new Object[]{
                        prioridade, nome, qtdValida, minimo, acaoRecomendada, statusOperacional 
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar motor de sugestões v12.4: " + e.getMessage());
        }
        return listaRecomendacoes;
    }

    // =====================================================================
    // CONTROLE LOGÍSTICO v12.4: LISTAR APENAS NOMES DE VACINAS PENDENTES
    // =====================================================================
    public List<String> listarNomesImunizantesPendentes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT nome_imunizante FROM solicitacao_compra WHERE status_solicitacao = 'PENDENTE' ORDER BY nome_imunizante ASC";
        
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nome_imunizante"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar nomes de vacinas pendentes: " + e.getMessage());
        }
        return lista;
    }

    // =====================================================================
    // CONTROLE LOGÍSTICO v12.4: MARCAR SOLICITAÇÃO COMO RESOLVIDA (BAIXA)
    // =====================================================================
    public boolean resolverSolicitacaoCompra(String nomeImunizante) {
        String sql = "UPDATE solicitacao_compra SET status_solicitacao = 'RESOLVIDA' " +
                     "WHERE nome_imunizante = ? AND status_solicitacao = 'PENDENTE'";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeImunizante);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao resolver solicitação de compra: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // CONTROLE LOGÍSTICO v12.0: PERSISTIR NOVA SOLICITAÇÃO NO BANCO
    // =====================================================================
    public boolean salvarSolicitacaoCompra(String nomeImunizante, int qtdSolicitada, String prioridade, String diretriz, String nomeUser, String nivelUser) {
        String sql = "INSERT INTO solicitacao_compra (nome_imunizante, quantidade_solicitada, prioridade_logistica, diretriz_recomendada, data_hora_solicitacao, responsavel_nome, responsavel_nivel, status_solicitacao) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, ?, 'PENDENTE')";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeImunizante);
            stmt.setInt(2, qtdSolicitada);
            stmt.setString(3, prioridade);
            stmt.setString(4, diretriz);
            stmt.setString(5, nomeUser);
            stmt.setString(6, nivelUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao gravar solicitação de compra: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // CONTROLE LOGÍSTICO v12.0: BUSCAR HISTÓRICO PARA A TELA DE REQUISIÇÕES
    // =====================================================================
    public java.util.List<Object[]> listarSolicitacoesRealizadas() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, data_hora_solicitacao, responsavel_nome, responsavel_nivel, nome_imunizante, quantidade_solicitada, prioridade_logistica, status_solicitacao " +
                     "FROM solicitacao_compra ORDER BY data_hora_solicitacao DESC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("data_hora_solicitacao");
                String dataFormatada = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts);
                
                // Retorna os dados incluindo o status real ('PENDENTE' ou 'RESOLVIDA') para a JTable
                lista.add(new Object[]{
                    rs.getInt("id"),
                    dataFormatada,
                    rs.getString("responsavel_nome"),
                    rs.getString("responsavel_nivel"),
                    rs.getString("nome_imunizante"),
                    rs.getInt("quantidade_solicitada"),
                    rs.getString("status_solicitacao") 
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar histórico de solicitações: " + e.getMessage());
        }
        return lista;
    }
    
    // =====================================================================
    // CONTROLE DE INTERFACE v12.7: BUSCAR IDENTIFICADOR COM ALERTA DE VENCIMENTO
    // =====================================================================
    public List<String> buscarNomesELotesParaPesquisa() {
        List<String> lista = new ArrayList<>();
        
        // Query v12.7: Converte a string de validade para data no MySQL e avalia se já venceu em relação ao NOW()
        String sql = "SELECT CASE " +
                     "    WHEN STR_TO_DATE(data_validade, '%d/%m/%Y') < NOW() " +
                     "    THEN CONCAT('⚠️ [VENCIDO] ', nome_comercial, ' [Lote: ', numero_lote, ']') " +
                     "    ELSE CONCAT(nome_comercial, ' [Lote: ', numero_lote, ']') " +
                     "END as identificador FROM imunizante ORDER BY nome_comercial ASC";
                     
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("identificador"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar identificadores com alerta v12.7: " + e.getMessage());
        }
        return lista;
    }
    
    // =====================================================================
    // CONTROLE DE INTERFACE v12.7: BUSCAR PRODUTO EXPURGANDO A TAG DE VENCIDO
    // =====================================================================
    public Vacina buscarPorIdentificadorCombinado(String identificador) {
        // Remove o marcador de segurança caso ele exista na String selecionada
        if (identificador.contains("⚠️ [VENCIDO] ")) {
            identificador = identificador.replace("⚠️ [VENCIDO] ", "").trim();
        }
        
        if (!identificador.contains(" [Lote: ")) return null;
        
        String nome = identificador.substring(0, identificador.indexOf(" [Lote: ")).trim();
        String lote = identificador.substring(identificador.indexOf(" [Lote: ") + 8, identificador.length() - 1).trim();
        
        String sql = "SELECT * FROM imunizante WHERE nome_comercial = ? AND numero_lote = ?";
        try (Connection conn = ConexaoMySQL.criarConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, nome);
            stmt.setString(2, lote);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vacina v = new Vacina();
                    v.setId(rs.getInt("id"));
                    v.setNome(rs.getString("nome_comercial"));
                    v.setAntigenoAlvo(rs.getString("antigeno_alvo"));
                    v.setFabricante(rs.getString("laboratorio_fabricante"));
                    v.setPlataforma(rs.getString("plataforma_tecnologica"));
                    v.setLote(rs.getString("numero_lote"));
                    v.setValidade(rs.getString("data_validade"));
                    v.setPaisOrigem(rs.getString("pais_origem"));
                    v.setRegistroAnvisa(rs.getString("numero_registro_anvisa"));
                    v.setFornecedor(rs.getString("fornecedor"));
                    v.setApresentacao(rs.getString("apresentacao"));
                    v.setQuantidade(rs.getInt("quantidade_frascos_estoque"));
                    v.setTotalDoses(rs.getInt("calculo_total_doses"));
                    v.setEstoqueMinimo(rs.getInt("estoque_minimo_alerta"));
                    v.setFaixaTemperatura(rs.getString("faixa_temperatura"));
                    v.setEstabilidade(rs.getString("tempo_estabilidade"));
                    v.setStatusLiberacao(rs.getString("status_liberacao"));
                    v.setIdColaborador(rs.getInt("id_colaborador"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por identificador v12.7: " + e.getMessage());
        }
        return null;
    }

    
}