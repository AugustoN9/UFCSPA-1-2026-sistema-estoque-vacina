package br.edu.ufcspa.estoque.model;

import java.time.LocalDateTime;

public class HistoricoConsumo {
    private int idHistorico;
    private String numeroLote;
    private int quantidadeFrascos;
    private String localImunizacao;
    private LocalDateTime dataHoraConsumo;
    private int idColaborador;

    // Getters e Setters
    public int getIdHistorico() { return idHistorico; }
    public void setIdHistorico(int idHistorico) { this.idHistorico = idHistorico; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public int getQuantidadeFrascos() { return quantidadeFrascos; }
    public void setQuantidadeFrascos(int quantidadeFrascos) { this.quantidadeFrascos = quantidadeFrascos; }
    public String getLocalImunizacao() { return localImunizacao; }
    public void setLocalImunizacao(String localImunizacao) { this.localImunizacao = localImunizacao; }
    public LocalDateTime getDataHoraConsumo() { return dataHoraConsumo; }
    public void setDataHoraConsumo(LocalDateTime dataHoraConsumo) { this.dataHoraConsumo = dataHoraConsumo; }
    public int getIdColaborador() { return idColaborador; }
    public void setIdColaborador(int idColaborador) { this.idColaborador = idColaborador; }
}
