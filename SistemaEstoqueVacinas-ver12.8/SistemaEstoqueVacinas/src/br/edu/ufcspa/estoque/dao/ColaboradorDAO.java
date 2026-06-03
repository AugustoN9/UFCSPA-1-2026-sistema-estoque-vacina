package br.edu.ufcspa.estoque.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufcspa.estoque.model.Colaborador;

public class ColaboradorDAO {
    private Connection conn;

    public ColaboradorDAO() {
        this.conn = ConexaoMySQL.criarConexao();
    }

    /**
     * Valida o login e retorna o Colaborador completo com o novo campo de email.
     */
    public Colaborador validarLogin(String usuario, String senha) {
        String sql = "SELECT * FROM colaborador WHERE usuario = ? AND senha = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
            	Colaborador c = new Colaborador();
                c.setIdColaborador(rs.getInt("id_colaborador"));
                c.setNome(rs.getString("nome"));
                c.setGenero(rs.getString("genero")); 
                c.setNivelAcesso(rs.getString("nivel_acesso"));
                c.setEmail(rs.getString("email")); 
                c.setCargo(rs.getString("cargo")); 
                
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * NOVO MÉTODO: Para ser usado pelo botão de Cadastrar da nova tela.
     */
    public boolean salvarNovoColaborador(Colaborador colaborador) {
        String sql = "INSERT INTO colaborador (nome, genero, cargo, usuario, email, senha, nivel_acesso) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                   
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getGenero()); 
            stmt.setString(3, colaborador.getCargo());
            stmt.setString(4, colaborador.getUsuario());
            stmt.setString(5, colaborador.getEmail()); 
            stmt.setString(6, colaborador.getSenha());
            stmt.setString(7, colaborador.getNivelAcesso());
            
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar colaborador: " + e.getMessage());
            return false;
        }
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
}