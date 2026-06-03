package br.edu.ufcspa.estoque.model;

/**
 * Classe de modelo que representa um Imunizante no sistema de estoque.
 * Atualizada para suportar rastreabilidade completa e gestão logística.
 */
public class Vacina {
    
    // 1. IDENTIFICAÇÃO TÉCNICA
	private int id;
    private String nome;
    private String antigenoAlvo;
    private String fabricante;
    private String plataforma;

    // 2. CONTROLE DE LOTE E RASTREABILIDADE
    private String lote;
    private String validade;
    private String paisOrigem;
    private String registroAnvisa;
    private String fornecedor;

    // 3. GESTÃO DE ESTOQUE E APRESENTAÇÃO
    private String apresentacao;
    private int quantidade; // Quantidade de frascos
    private int totalDoses; // Resultado do cálculo (frascos * fator)
    private int estoqueMinimo;
    private int idColaborador;
    private String nomeColaborador; 
    private String dataChegada;
    private String dataCadastro;

    // 4. LOGÍSTICA E CADEIA DE FRIO
    private String faixaTemperatura;
    private String estabilidade;
    private String statusLiberacao;

    // Construtor Padrão
    public Vacina() {
    }

    // Getters e Setters (Essenciais para o DAO e o Controller)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAntigenoAlvo() { return antigenoAlvo; }
    public void setAntigenoAlvo(String antigenoAlvo) { this.antigenoAlvo = antigenoAlvo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }

    public String getPaisOrigem() { return paisOrigem; }
    public void setPaisOrigem(String paisOrigem) { this.paisOrigem = paisOrigem; }

    public String getRegistroAnvisa() { return registroAnvisa; }
    public void setRegistroAnvisa(String registroAnvisa) { this.registroAnvisa = registroAnvisa; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public String getApresentacao() { return apresentacao; }
    public void setApresentacao(String apresentacao) { this.apresentacao = apresentacao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getTotalDoses() { return totalDoses; }
    public void setTotalDoses(int totalDoses) { this.totalDoses = totalDoses; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    
    public int getIdColaborador() { return idColaborador; }
    public void setIdColaborador(int idColaborador) { this.idColaborador = idColaborador; }
    
    public String getNomeColaborador() { return nomeColaborador; }
    public void setNomeColaborador(String nomeColaborador) { this.nomeColaborador = nomeColaborador; }
    
    public String getDataChegada() { return dataChegada; }
    public void setDataChegada(String dataChegada) { this.dataChegada = dataChegada; }
    
    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getFaixaTemperatura() { return faixaTemperatura; }
    public void setFaixaTemperatura(String faixaTemperatura) { this.faixaTemperatura = faixaTemperatura; }

    public String getEstabilidade() { return estabilidade; }
    public void setEstabilidade(String estabilidade) { this.estabilidade = estabilidade; }

    public String getStatusLiberacao() { return statusLiberacao; }
    public void setStatusLiberacao(String statusLiberacao) { this.statusLiberacao = statusLiberacao; }
}