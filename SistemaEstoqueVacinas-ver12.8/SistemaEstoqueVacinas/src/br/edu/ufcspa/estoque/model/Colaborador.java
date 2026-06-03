package br.edu.ufcspa.estoque.model;

/**
 * Model que representa o Colaborador/Utilizador do sistema.
 * Reflete a estrutura da tabela 'colaborador' do banco de dados db_vacinas.
 */
public class Colaborador {
    
    private int idColaborador;
    private String nome;
    private String genero;
    private String cargo;
    private String usuario;
    private String email;
    private String senha;
    private String nivelAcesso; // ADMIN, OPERADOR ou APP_MOBILE

    // Construtor Vazio (Necessário para o DAO)
    public Colaborador() {
    }

    // Construtor Completo
    public Colaborador(int idColaborador, String nome, String genero, String cargo, String usuario, String email, String senha, String nivelAcesso) {
        this.idColaborador = idColaborador;
        this.nome = nome;
        this.genero = genero;
        this.cargo = cargo;
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    // Getters e Setters
    public int getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(int idColaborador) {
        this.idColaborador = idColaborador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public String getEmail() { 
    	return email; } 
    
    public void setEmail(String email) { 
    	this.email = email; }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    // Método auxiliar para facilitar debug ou exibição em logs
    @Override
    public String toString() {
        return "Colaborador{" +
                "id=" + idColaborador +
                ", nome='" + nome + '\'' +
                ", perfil='" + nivelAcesso + '\'' +
                '}';
    }
}