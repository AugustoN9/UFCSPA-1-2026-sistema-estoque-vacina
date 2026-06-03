package br.edu.ufcspa.estoque.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {
    
    private static Connection conn;
    // Ajuste o nome do banco se necessário (ex: 'atividade_ufcspa')
    private static String usuario = "augusto";
    private static String senha = "aulatep";
    private static String servidor = "localhost";
    private static String banco = "db_vacinas"; 
    private static String url = "jdbc:mysql://" + servidor + ":3306/" + banco;
    
    public static Connection criarConexao() {
        try {
            // Se a conexão já existir e estiver aberta, apenas a retorna
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, usuario, senha);
                System.out.println("Conexão estabelecida com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Falha ao conectar com o banco: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
    
    public static void encerraConexao() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexão encerrada com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
