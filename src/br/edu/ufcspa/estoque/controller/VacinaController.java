package br.edu.ufcspa.estoque.controller;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import br.edu.ufcspa.estoque.dao.VacinaDAO;
import br.edu.ufcspa.estoque.model.Colaborador;
import br.edu.ufcspa.estoque.model.Vacina;
import br.edu.ufcspa.estoque.view.LoginView;
import br.edu.ufcspa.estoque.view.VacinaView;
import javax.swing.JTable;

/**
 * Controller v5.0 - Sincronização e Fluxos Homologados.
 */
public class VacinaController {

    private VacinaView view;
    private VacinaDAO dao;
    private Colaborador usuarioLogado;
    private String modoAtual = "PESQUISA";
    private int idVacinaSelecionada;

    public VacinaController(VacinaView view, Colaborador usuario) {
        this.view = view;
        this.dao = new VacinaDAO();
        this.usuarioLogado = usuario;
        
        initListeners();
        prepararModo("PESQUISA");
        configurarPermissoesPorNivel();
        carregarListaPesquisa();
        if (this.usuarioLogado != null) {
            this.view.inicializarRodape(this.usuarioLogado.getNome(), this.usuarioLogado.getNivelAcesso());
        }
    }

    private void configurarPermissoesPorNivel() {
        if (usuarioLogado != null) {
            String nivel = usuarioLogado.getNivelAcesso();
           
            if ("OPERADOR".equalsIgnoreCase(nivel)) {
                // Regra que você já tinha: Operador não cadastra nem edita
                view.getBtnMenuCadastro().setEnabled(false);
                view.getBtnMenuEdicao().setEnabled(false);
                
            } else if ("APP_MOBILE".equalsIgnoreCase(nivel)) {
                // --- NOVA REGRA PARA O USUÁRIO DO SMARTPHONE ---
                // Desativa o acesso a todas as funções gerenciais de desktop
                view.getBtnMenuCadastro().setEnabled(false);
                view.getBtnMenuPesquisa().setEnabled(false);
                view.getBtnMenuEdicao().setEnabled(false);
                view.getBtnMenuEstoque().setEnabled(false);
                
                // Se o seu painel tiver os botões de Dashboard e Relatório ativos na View:
                if (view.getBtnMenuDashboard() != null) view.getBtnMenuDashboard().setEnabled(false);
                if (view.getBtnMenuRelatorio() != null) view.getBtnMenuRelatorio().setEnabled(false);
                
                // Força o sistema a pular a tela de pesquisa e abrir direto no Scanner QR
                // Usamos o SwingUtilities para garantir que a troca de tela ocorra logo após a inicialização limpa
                javax.swing.SwingUtilities.invokeLater(() -> {
                    prepararModo("APP_QRCODE");
                });
            }
        }
    }
	
	private void initListeners() {
	    // 1. Ouvintes do Menu Principal (Comuns a todos)
	    view.getBtnMenuCadastro().addActionListener(e -> prepararModo("CADASTRO"));
	    view.getBtnMenuPesquisa().addActionListener(e -> prepararModo("PESQUISA"));
	    view.getBtnMenuEdicao().addActionListener(e -> {
	        limparCampos(); 
	        if (view.getCbBuscaRapida().getItemCount() > 0) {
	            view.getCbBuscaRapida().setSelectedIndex(0);
	        }
	        prepararModo("EDICAO");
	    });
	    view.getBtnMenuEstoque().addActionListener(e -> prepararModo("ESTOQUE"));
	    view.getBtnMenuAppQR().addActionListener(e -> prepararModo("APP_QRCODE"));
	    
	    view.getBtnLogout().addActionListener(e -> efetuarLogout());
	    view.getBtnRegistrarConsumo().addActionListener(e -> registrarConsumoAppQRCode());
	    view.getChkRegistrarPerda().addActionListener(e -> {
	        boolean statusPerda = view.getChkRegistrarPerda().isSelected();
	        view.getCbMotivoPerda().setEnabled(statusPerda);
	        
	        if (statusPerda) {
	            view.getBtnRegistrarConsumo().setText("REGISTRAR PERDA / DESCARTE");
	            view.getBtnRegistrarConsumo().setBackground(new java.awt.Color(192, 57, 43));
	        } else {
	            view.getBtnRegistrarConsumo().setText("REGISTRAR CONSUMO MÓVEL");
	            view.getBtnRegistrarConsumo().setBackground(new java.awt.Color(39, 174, 96));
	            view.getCbMotivoPerda().setSelectedIndex(0);
	        }
	    });
	    //view.getBtnSalvar().addActionListener(e -> salvarRegistro());
	    //view.getBtnLimpar().addActionListener(e -> limparCampos());
	 // =====================================================================
        // CORREÇÃO: Remove ouvintes antigos e isola o clique do botão Salvar/Atualizar
        // =====================================================================
        for (java.awt.event.ActionListener al : view.getBtnSalvar().getActionListeners()) {
            view.getBtnSalvar().removeActionListener(al);
        }
        view.getBtnSalvar().addActionListener(e -> {
            System.out.println("DEBUG: Botão clicado. Modo atual: " + modoAtual + " | ID Selecionado: " + idVacinaSelecionada);
            salvarRegistro();
        });
        
        // Readiciona o botão limpar logo abaixo de forma segura
        view.getBtnLimpar().addActionListener(e -> limparCampos());
        // =====================================================================
	   
	    if (view.getBtnMenuRelatorio() != null) {
	        view.getBtnMenuRelatorio().addActionListener(e -> prepararModo("RELATORIO"));
	    }
	    if (view.getBtnMenuDashboard() != null) {
	        view.getBtnMenuDashboard().addActionListener(e -> prepararModo("DASHBOARD"));
	    }
	    if (view.getBtnMenuSolicitacoes() != null) {
            view.getBtnMenuSolicitacoes().addActionListener(e -> {
                java.awt.CardLayout card = (java.awt.CardLayout) view.getPainelCentro().getLayout();
                card.show(view.getPainelCentro(), "SOLICITACOES");
                atualizarDestaqueMenu(view.getBtnMenuSolicitacoes());
                atualizarListaSolicitacoes(); // Carrega os dados na tabela do histórico
            });
        }
	    
	 // Ouvinte do Botão FILTRAR (Verde)
	    if (view.getBtnFiltrarRelatorio() != null) {
	        view.getBtnFiltrarRelatorio().addActionListener(e -> processarFiltroRelatorio());
	    }
	    
	    // CORREÇÃO: Vincula a limpeza ao botão dinâmico de Atualizar (Azul)
	    if (view.getBtnAtualizarRelatorio() != null) {
	        view.getBtnAtualizarRelatorio().addActionListener(e -> {
	            view.getTxtFiltroData().setDate(null); // Reseta o JCalendar para vazio
	            view.getCbFiltroOperador().setSelectedIndex(0);
	            view.getCbFiltroImunizante().setSelectedIndex(0);
	            view.getCbFiltroLocal().setSelectedIndex(0);
	            atualizarTabelaRelatorioGeral(); // Recarrega a tabela limpa
	        });
	    }
	
	    // 3. Escuta a seleção de nome no terminal QR para buscar o lote dinamicamente
	    view.getCbQRCodeNome().addActionListener(e -> {
	        String nomeSelecionado = (String) view.getCbQRCodeNome().getSelectedItem();
	        if (nomeSelecionado != null && !nomeSelecionado.equals("Selecione um imunizante...")) {
	            Vacina v = dao.buscarPorNome(nomeSelecionado);
	            if (v != null) {
	                view.getTxtQRCodeLote().setText(v.getLote());
	            }
	        }
	    });
	
	    // 4. Carrega o ouvinte de busca rápida por último (evita disparos nulos na carga inicial)
	    view.getCbBuscaRapida().addActionListener(e -> carregarDadosSelecionados());
	    
	    view.getLblKpiAlertas().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

	    view.getLblKpiAlertas().addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            exibirJanelaDetalhesAlerta();
	        }
	    });
	    
	    // 5. Filtro e botao imprimir Listagem ESTOQUE
	    view.getCbFiltroStatusEstoque().addActionListener(e -> aplicarFiltroEstoqueMesa());
	    view.getBtnImprimirEstoque().addActionListener(e -> imprimirListaEstoque());
	    configurarCliqueAuditoriaDescarte();
	    // =====================================================================
        // INTERFACE v12.7: FILTRO EM TEMPO REAL CONFORME O USUÁRIO DIGITA
        // =====================================================================
        if (view.getTxtFiltroDigitado() != null) {
            view.getTxtFiltroDigitado().addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    String termo = view.getTxtFiltroDigitado().getText().trim().toLowerCase();
                    
                    // Se o campo for limpo, recarrega a lista original completa
                    if (termo.isEmpty()) {
                        carregarListaPesquisa();
                        return;
                    }
                    
                    // Pega todos os identificadores que estão atualmente no banco
                    List<String> todosItens = dao.buscarNomesELotesParaPesquisa();
                    
                    // Desativa temporariamente o ouvinte do combo para não disparar eventos de clique falsos
                    java.awt.event.ActionListener[] listeners = view.getCbBuscaRapida().getActionListeners();
                    for (java.awt.event.ActionListener al : listeners) {
                        view.getCbBuscaRapida().removeActionListener(al);
                    }
                    
                    // Limpa o combo e adiciona o cabeçalho padrão
                    view.getCbBuscaRapida().removeAllItems();
                    view.getCbBuscaRapida().addItem("Selecione um imunizante...");
                    
                    // Filtra os itens: adiciona no combo apenas o que bater com o que foi digitado
                    for (String item : todosItens) {
                        if (item.toLowerCase().contains(termo)) {
                            view.getCbBuscaRapida().addItem(item);
                        }
                    }
                    
                    // Reativa o ouvinte do combo box após a filtragem
                    for (java.awt.event.ActionListener al : listeners) {
                        view.getCbBuscaRapida().addActionListener(al);
                    }
                    
                    // Se houver apenas um resultado encontrado, já deixa ele pré-selecionado e abre o combo
                    if (view.getCbBuscaRapida().getItemCount() == 2) {
                        view.getCbBuscaRapida().setSelectedIndex(1);
                    } else if (view.getCbBuscaRapida().getItemCount() > 2) {
                        view.getCbBuscaRapida().setPopupVisible(true); // Abre a lista suspensa para o usuário ver as opções restantes
                    }
                }
            });
        }
	}
	
	private void salvarRegistro() {
		// 1. EXTRAÇÃO E VALIDAÇÃO DOS DADOS DA VIEW
        String nome = view.getTxtNomeComercial().getText().trim();
        String lote = view.getTxtLote().getText().trim();
        
        if (nome.isEmpty() || lote.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Os campos 'Nome Comercial' e 'Número do Lote' são obrigatórios.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. MONTAGEM DO OBJETO DE MODELO
        Vacina v = new Vacina();
        
        if (this.idVacinaSelecionada == 0) {
            this.modoAtual = "CADASTRO";
        }
        
        // >>> INSERIR ESTA CONDIÇÃO AQUI PARA ADICIONAR O ID NO MODO EDIÇÃO <<<
        if (!"CADASTRO".equals(modoAtual)) {
            v.setId(this.idVacinaSelecionada); 
        }
        
        try {
            java.util.Date dataSelecionada = view.getTxtValidade().getDate();
            if (dataSelecionada == null) {
                JOptionPane.showMessageDialog(view, "Por favor, selecione uma data de validade usando o calendário.", "Data Obrigatória", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.time.LocalDate dataValidade = dataSelecionada.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate hoje = java.time.LocalDate.now();

            if (dataValidade.isBefore(hoje)) {
                JOptionPane.showMessageDialog(view, "CRÍTICO: Não é permitido cadastrar imunizantes com a validade vencida!", "Bloqueio de Segurança Sanitária", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoje, dataValidade);
            if (diasRestantes < 30) {
                JOptionPane.showMessageDialog(view, "REJEITADO: O produto possui apenas " + diasRestantes + " dias até o vencimento.\nMargem útil deve ser superior a 30 dias.", "Margem Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            v.setNome(view.getTxtNomeComercial().getText().trim());
            v.setAntigenoAlvo(view.getTxtAntigeno().getText().trim());
            v.setFabricante(view.getTxtFabricante().getText().trim());
            v.setPlataforma(view.getCbPlataforma().getSelectedItem().toString());
            v.setLote(view.getTxtLote().getText().trim());
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            v.setValidade(sdf.format(dataSelecionada)); 
            
            v.setPaisOrigem(view.getTxtPaisOrigem().getText().trim());
            v.setRegistroAnvisa(view.getTxtAnvisa().getText().trim());
            v.setFornecedor(view.getTxtFornecedor().getText().trim());
            v.setApresentacao(view.getCbApresentacao().getSelectedItem().toString());
            
            int qtdFrascos = Integer.parseInt(view.getTxtQuantidade().getText().trim());
            int estoqueMinimo = Integer.parseInt(view.getTxtEstoqueMinimo().getText().trim());
            
            // =====================================================================
            // CÁLCULO DE DOSES COM EXTRAÇÃO DE DÍGITOS E MARGEM DE PERDA TÉCNICA (10%)
            // =====================================================================
            int dosesPorFrasco = 1; 
            String stringApresentacao = v.getApresentacao();
            
            // Limpa o texto mantendo apenas o número (Ex: "Multidose (6 doses)" vira "6")
            String apenasNumeros = stringApresentacao.replaceAll("[^0-9]", "");
            
            if (!apenasNumeros.isEmpty()) {
                dosesPorFrasco = Integer.parseInt(apenasNumeros);
            }
            
            int totalDosesCalculado;
            
            // Se for multidose (mais de 1 dose por frasco), aplica fator de perda de 10%
            if (dosesPorFrasco > 1) {
                int totalBruto = qtdFrascos * dosesPorFrasco;
                // Multiplica por 0.9 (90% aproveitável) e trunca para inteiro com Math.floor
                totalDosesCalculado = (int) Math.floor(totalBruto * 0.9);
            } else {
                // Monodose não sofre penalidade de perda técnica por volume morto
                totalDosesCalculado = qtdFrascos * dosesPorFrasco;
            }
            
            v.setQuantidade(qtdFrascos);
            v.setTotalDoses(totalDosesCalculado); // Seta o estoque real calculado com segurança
            v.setEstoqueMinimo(estoqueMinimo);
            // =====================================================================
            
            v.setFaixaTemperatura(view.getCbTemperatura().getSelectedItem().toString());
            v.setEstabilidade(view.getCbEstabilidade().getSelectedItem().toString());
            v.setStatusLiberacao(view.getCbStatusLiberacao().getSelectedItem().toString());

            if (this.usuarioLogado != null) {
                v.setIdColaborador(this.usuarioLogado.getIdColaborador());
            }

            boolean sucesso = "CADASTRO".equals(modoAtual) ? dao.salvar(v) : dao.atualizar(v);

            if (sucesso) {
            	
            	// =====================================================================
                // INTERCEPÇÃO v12.6: FECHAMENTO REAL E AUTOMÁTICO DO FLUXO LOGÍSTICO
                // =====================================================================
                if ("CADASTRO".equals(modoAtual) && view.getCbVinculoSolicitacao() != null) {
                    Object itemSelecionadoObj = view.getCbVinculoSolicitacao().getSelectedItem();
                    if (itemSelecionadoObj != null) {
                        String itemSelecionado = itemSelecionadoObj.toString().trim();
                        
                        // Se o operador vinculou explicitamente a uma ordem pendente na tela
                        if (!"Nenhuma / Entrada Avulsa".equals(itemSelecionado)) {
                            boolean baixado = dao.resolverSolicitacaoCompra(itemSelecionado);
                            if (baixado) {
                                System.out.println("LOGISTICA SUCCESS: Ordem para " + itemSelecionado + " alterada para RESOLVIDA no banco.");
                            }
                        }
                    }
                }
                // =====================================================================
            	
                JOptionPane.showMessageDialog(view, "Registro processado com sucesso!\nResponsável: " + usuarioLogado.getNome());
                carregarListaPesquisa();
                prepararModo("PESQUISA");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Erro: Os campos numéricos (Qtd. Frascos e Estoque Mínimo) devem conter apenas inteiros válidos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }
    
// =====================================================================
    // ATUALIZAÇÃO V9.7: FLUXO UNIFICADO DE CONSUMO E PERDAS NO TERMINAL MÓVEL
    // =====================================================================
    private void registrarConsumoAppQRCode() {
        String lote = view.getTxtQRCodeLote().getText().trim();
        
        // Proteção contra valores nulos ou vazios no JSpinner
        int quantidade = 1;
        if (view.getSpinQtdConsumo() != null && view.getSpinQtdConsumo().getValue() != null) {
            quantidade = (int) view.getSpinQtdConsumo().getValue();
        }
        
        String local = (String) view.getCbQRCodeLocal().getSelectedItem();

        if (lote.isEmpty() || quantidade <= 0 || local == null || local.equals("Selecione um posto...")) {
            JOptionPane.showMessageDialog(view, "Por favor, preencha todos os campos e selecione um Posto de Saúde válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (this.usuarioLogado != null) ? this.usuarioLogado.getIdColaborador() : 1;

        // Proteção anti-NullPointerException se a View falhar na inicialização
        boolean isPerdaSelecionada = (view.getChkRegistrarPerda() != null) && view.getChkRegistrarPerda().isSelected();

        // =====================================================================
        // FLUXO DE DESCARTE SANITÁRIO (PERDA)
        // =====================================================================
        if (isPerdaSelecionada) {
            String motivo = view.getCbMotivoPerda().getSelectedItem().toString();

            if (view.getCbMotivoPerda().getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(view, "Por favor, selecione um motivo técnico de descarte válido.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Executa o novo método no DAO
            boolean sucessoPerda = dao.registrarPerdaEstoque(lote, quantidade, motivo, local, idUsuario);

            if (sucessoPerda) {
                JOptionPane.showMessageDialog(view, "DESCARTE SANITÁRIO COMPUTADO!\nMotivo: " + motivo + "\nLocal: " + local, "Controle de Perdas", JOptionPane.WARNING_MESSAGE);
                resetarInterfaceMobile(); 
            } else {
                JOptionPane.showMessageDialog(view, "FALHA OPERACIONAL:\nLote não localizado ou saldo em estoque insuficiente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } 
        // =====================================================================
        // FLUXO DE CONSUMO NORMAL
        // =====================================================================
        else {
            // CORREÇÃO DA LINHA 316: Trocado de quantity para quantidade
            boolean sucessoConsumo = dao.registrarConsumoPorQRCode(lote, quantidade, local, idUsuario);

            if (sucessoConsumo) {
                JOptionPane.showMessageDialog(view, "SUCESSO!\nConsumo registrado no local: " + local, "Estoque Atualizado", JOptionPane.INFORMATION_MESSAGE);
                resetarInterfaceMobile(); 
            } else {
                JOptionPane.showMessageDialog(view, "FALHA OPERACIONAL:\nLote não encontrado ou saldo em estoque insuficiente.", "Erro na Baixa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Método auxiliar para limpar os campos e restaurar o estado visual padrão do terminal
     */
    private void resetarInterfaceMobile() {
        view.getTxtQRCodeLote().setText("");
        view.getCbQRCodeLocal().setSelectedIndex(0); 
        view.getSpinQtdConsumo().setValue(1);
        view.getChkRegistrarPerda().setSelected(false);
        view.getCbMotivoPerda().setEnabled(false);
        view.getBtnRegistrarConsumo().setText("REGISTRAR CONSUMO MÓVEL");
        view.getBtnRegistrarConsumo().setBackground(new Color(39, 174, 96)); // Restaura o verde padrão
        
        // Atualiza as tabelas em tempo real
        atualizarTabelaEstoque();          
        atualizarTabelaConsumoPessoal();  
    }


    public void prepararModo(String modo) {
        this.modoAtual = modo;
        java.awt.CardLayout card = (java.awt.CardLayout) view.getPainelCentro().getLayout();

        if ("ESTOQUE".equals(modo)) {
            card.show(view.getPainelCentro(), "ESTOQUE");
            atualizarDestaqueMenu(view.getBtnMenuEstoque());
            atualizarTabelaEstoque();
            return;
        } else if ("APP_QRCODE".equals(modo)) {
            card.show(view.getPainelCentro(), "APP_QRCODE");
            atualizarDestaqueMenu(view.getBtnMenuAppQR());
            atualizarTabelaConsumoPessoal(); 
            return;
        }
        // ======================================================================
        // CONDICIONAL UNIFICADA E CORRIGIDA: Carrega os filtros antes de listar
        // ======================================================================
        else if ("RELATORIO".equals(modo)) {
            card.show(view.getPainelCentro(), "RELATORIO");
            atualizarDestaqueMenu(view.getBtnMenuRelatorio());
            
            // 1. Primeiro popula os ComboBoxes com os dados dinâmicos do banco
            carregarOpcoesDosFiltros(); 
            
            // 2. Depois atualiza a tabela com os dados do histórico geral
            atualizarTabelaRelatorioGeral();
            return;
        }
        // ======================================================================
        // NOVA CONDICIONAL DA V9.0: Ativa a tela e renderiza os gráficos nativos
        // ======================================================================
        else if ("DASHBOARD".equals(modo)) {
            card.show(view.getPainelCentro(), "DASHBOARD");
            atualizarDestaqueMenu(view.getBtnMenuDashboard());
            
            // Dispara a busca e atualização dos gráficos
            atualizarDadosDashboard();
            return;
        }
        // ======================================================================
        else {
            card.show(view.getPainelCentro(), "FORM");
        }

        switch (modo) {
            case "CADASTRO":
                atualizarDestaqueMenu(view.getBtnMenuCadastro());                
                limparCampos();
                
                this.idVacinaSelecionada = 0;
                
                if (view.getCbBuscaRapida().getItemCount() > 0) {
                    view.getCbBuscaRapida().setSelectedIndex(0);
                }
                
                // -----------------------------------------------------------------
                // GATILHO LOGÍSTICO v12.4: Alimenta o combo com as ordens pendentes
                // -----------------------------------------------------------------
                carregarComboVinculoSolicitacoes();
                if (view.getCbVinculoSolicitacao() != null) {
                    view.getCbVinculoSolicitacao().setEnabled(true);
                }
                // -----------------------------------------------------------------
                
                setEstadoCampos(true);
                view.getBtnLimpar().setEnabled(true);
                view.getBtnLimpar().setBackground(new Color(149, 165, 166));
                view.getCbBuscaRapida().setEnabled(false);
                view.getBtnSalvar().setText("SALVAR NOVO");
                view.getBtnSalvar().setBackground(new Color(39, 174, 96));
                view.getBtnSalvar().setEnabled(true);
                break;

            case "PESQUISA":
                atualizarDestaqueMenu(view.getBtnMenuPesquisa());
                
                limparCampos();
                if (view.getCbBuscaRapida().getItemCount() > 0) {
                    view.getCbBuscaRapida().setSelectedIndex(0);
                }
                
                // -----------------------------------------------------------------
                // GATILHO LOGÍSTICO v12.4: Esvazia e tranca o combo em modo leitura
                // -----------------------------------------------------------------
                if (view.getCbVinculoSolicitacao() != null) {
                    view.getCbVinculoSolicitacao().removeAllItems();
                    view.getCbVinculoSolicitacao().setEnabled(false);
                }
                // -----------------------------------------------------------------
                
                setEstadoCampos(false);
                view.getBtnLimpar().setEnabled(false);
                view.getBtnLimpar().setBackground(Color.LIGHT_GRAY);
                view.getCbBuscaRapida().setEnabled(true);
                view.getBtnSalvar().setText("MODO LEITURA");
                view.getBtnSalvar().setBackground(Color.LIGHT_GRAY);
                view.getBtnSalvar().setEnabled(false);
                break;

            case "EDICAO":
                atualizarDestaqueMenu(view.getBtnMenuEdicao());
                
                if (view.getCbBuscaRapida().getItemCount() > 0) {
                    view.getCbBuscaRapida().setSelectedIndex(0);
                }
                
                // -----------------------------------------------------------------
                // GATILHO LOGÍSTICO v12.4: Tranca o combo (a baixa ocorre na entrada do lote)
                // -----------------------------------------------------------------
                if (view.getCbVinculoSolicitacao() != null) {
                    view.getCbVinculoSolicitacao().removeAllItems();
                    view.getCbVinculoSolicitacao().setEnabled(false);
                }
                // -----------------------------------------------------------------
                
                setEstadoCampos(true);
                view.getBtnLimpar().setEnabled(true);
                view.getCbBuscaRapida().setEnabled(true);
                view.getBtnSalvar().setText("ATUALIZAR DADOS");
                view.getBtnSalvar().setBackground(new Color(241, 196, 15));
                view.getBtnSalvar().setEnabled(true);
                break;
        }
    }
    private void atualizarTabelaEstoque() {
        List<Vacina> lista = dao.listarEstoqueCompleto();
        DefaultTableModel modelo = (DefaultTableModel) view.getTabelaEstoque().getModel();
        modelo.setRowCount(0);
        
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate trintaDiasDepois = hoje.plusDays(30);
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Vacina v : lista) {
            // Calcula o Status Textual dinamicamente
            String status = "Normal";
            try {
                java.time.LocalDate validade = java.time.LocalDate.parse(v.getValidade(), dtf);
                if (validade.isBefore(hoje)) {
                    status = "Vencido";
                } else if (v.getQuantidade() <= v.getEstoqueMinimo()) {
                    status = "Crítico";
                } else if (validade.isBefore(trintaDiasDepois)) {
                    status = "A vencer";
                } else if (v.getQuantidade() <= (v.getEstoqueMinimo() * 2)) {
                    status = "Atenção";
                }
            } catch(Exception e) {
                status = "Normal";
            }
            
            int qtdDescartes = v.getId(); 
            String descartesCelularText;
            
            if (qtdDescartes > 0) {
                // Se houver descartes, quebra a linha e coloca o aviso azul sublinhado clicável
                descartesCelularText = "<html><center>" + qtdDescartes + "<br><font size='2' color='#2980b9'><u>Ver Detalhes</u></font></center></html>";
            } else {
                // Se for zero, deixa apenas o número limpo centralizado
                descartesCelularText = "<html><center>0</center></html>";
            }

            modelo.addRow(new Object[]{
                v.getNome(), 
                v.getLote(), 
                v.getQuantidade(), 
                descartesCelularText, 
                v.getEstoqueMinimo(), 
                v.getValidade(),
                status, 
                v.getNomeColaborador() != null ? v.getNomeColaborador() : "Carga Inicial",
                ""
            });
        }
        
        // =====================================================================
        // RESOLUÇÃO DEFINITIVA v10.2: MIX DE CLASSIFICAÇÃO DE INSTÂNCIA
        // =====================================================================
        javax.swing.ImageIcon imgEditar = null;
        javax.swing.ImageIcon imgExcluir = null;

        try {
            // Mapeamento dos caminhos físicos do pacote de imagens
            java.net.URL urlEditar = getClass().getResource("/br/edu/ufcspa/estoque/images/iconEdit.png");
            java.net.URL urlExcluir = getClass().getResource("/br/edu/ufcspa/estoque/images/iconDelete.png");

            if (urlEditar != null) imgEditar = new javax.swing.ImageIcon(urlEditar);
            if (urlExcluir != null) imgExcluir = new javax.swing.ImageIcon(urlExcluir);
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar os ícones customizados: " + e.getMessage());
        }

        // 1. CLASSES DE INSTÂNCIA (view.new): Renderer dos botões exige qualificação
        view.getTabelaEstoque().getColumnModel().getColumn(8).setCellRenderer(
            view.new ButtonRenderer(imgEditar, imgExcluir)
        );
        
        // 2. CLASSES DE INSTÂNCIA (view.new): Editor dos botões exige qualificação e os 4 parâmetros
        br.edu.ufcspa.estoque.view.VacinaView.ButtonEditor editorBotoes = 
            view.new ButtonEditor(
                new javax.swing.JCheckBox(), 
                imgEditar, 
                imgExcluir, 
                view
            );
        
        editorBotoes.setController(this);
        view.getTabelaEstoque().getColumnModel().getColumn(8).setCellEditor(editorBotoes);

        // 3. CLASSE ESTÁTICA (new comum): Como SinalizadorEstoqueRenderer é static na View, 
        // nós NÃO podemos usar "view.new". Usamos o new tradicional.
        br.edu.ufcspa.estoque.view.VacinaView.SinalizadorEstoqueRenderer sinalizador = 
            new br.edu.ufcspa.estoque.view.VacinaView.SinalizadorEstoqueRenderer();        
            
        for (int i = 0; i < 8; i++) {
            view.getTabelaEstoque().getColumnModel().getColumn(i).setCellRenderer(sinalizador);
        }
    }
     
        
    
    /**
     * Aplica o filtro em tempo real na tabela de estoque baseado na lógica de cores sanitárias
     */
    private void aplicarFiltroEstoqueMesa() {
        DefaultTableModel modelo = (DefaultTableModel) view.getTabelaEstoque().getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        view.getTabelaEstoque().setRowSorter(sorter);

        String selecao = view.getCbFiltroStatusEstoque().getSelectedItem().toString();

        if ("Todos".equals(selecao)) {
            view.getTabelaEstoque().setRowSorter(null); // Remove qualquer filtro
            return;
        }
        
        // =====================================================================
        // NOVA REGRA DE FILTRAGEM v10.2: TRATAMENTO PARA A CATEGORIA DESCARTES
        // =====================================================================
        if ("Descartes".equals(selecao)) {
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    try {
                        // Limpa o HTML da coluna 3 em tempo de execução no filtro
                        String textoPuro = entry.getStringValue(3).replaceAll("<[^>]*>", "").trim();
                        String numeroPuro = textoPuro.replace("Ver Detalhes", "").trim();
                        
                        int totalDescartes = Integer.parseInt(numeroPuro);
                        return totalDescartes > 0;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            });
            return;
        }
        // =====================================================================
        
     // Mapeia a seleção do Combo para a String exata gravada na coluna 5
        String filtroTexto = "Normal";
        if (selecao.contains("Crítico"))  filtroTexto = "Crítico";
        if (selecao.contains("Atenção"))  filtroTexto = "Atenção";
        if (selecao.contains("Normal") || selecao.contains("Normal"))   filtroTexto = "Normal";
        if (selecao.contains("Vencidos") || selecao.contains("Vencido")) filtroTexto = "Vencido";
        if (selecao.contains("A vencer")) filtroTexto = "A vencer";

        final String termoFinal = filtroTexto;
        sorter.setRowFilter(RowFilter.regexFilter("^" + termoFinal + "$", 6));
    }

    /**
     * Dispara a rotina nativa de impressão do Java para exportar a tabela exatamente como está filtrada
     */
    
    private void imprimirListaEstoque() {
        try {
            String statusSelecionado = view.getCbFiltroStatusEstoque().getSelectedItem().toString();

            // 1. Ativa a propriedade para o renderizador remover o fundo colorido na folha
            view.getTabelaEstoque().putClientProperty("imprimindo", true);

            // 2. Alarga as colunas no spooler para o texto caber sem gerar "..."
            view.getTabelaEstoque().getColumnModel().getColumn(0).setPreferredWidth(220); // Vacina
            view.getTabelaEstoque().getColumnModel().getColumn(1).setPreferredWidth(80);  // Lote
            view.getTabelaEstoque().getColumnModel().getColumn(2).setPreferredWidth(50);  // Qtd
            view.getTabelaEstoque().getColumnModel().getColumn(3).setPreferredWidth(60);  // Mínimo
            view.getTabelaEstoque().getColumnModel().getColumn(4).setPreferredWidth(95);  // Validade
            view.getTabelaEstoque().getColumnModel().getColumn(5).setPreferredWidth(90);  // Status
            view.getTabelaEstoque().getColumnModel().getColumn(6).setPreferredWidth(120); // Colaborador

            // 3. Oculta a coluna 7 (Ações) na impressão
            view.getTabelaEstoque().getColumnModel().getColumn(7).setMinWidth(0);
            view.getTabelaEstoque().getColumnModel().getColumn(7).setMaxWidth(0);

            // 4. CONFIGURAÇÃO DO TRABALHO DE IMPRESSÃO (PrinterJob)
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            
            // Formatador do rodapé da página
            final java.text.MessageFormat rodape = new java.text.MessageFormat("Página {0} | Gerado em " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Puxa o renderizador nativo da tabela configurado para ocupar a largura da página (FIT_WIDTH)
            final java.awt.print.Printable tabelaPrintable = view.getTabelaEstoque().getPrintable(
                javax.swing.JTable.PrintMode.FIT_WIDTH, 
                null, // Remove o cabeçalho gigante nativo que estava cortando
                rodape
            );

            // Criamos o provedor que desenha o nosso cabeçalho e depois manda a JTable se desenhar abaixo
            java.awt.print.Printable printableCustom = new java.awt.print.Printable() {
                @Override
                public int print(Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) throws java.awt.print.PrinterException {
                    // O delegate nativo gerencia o fim das páginas automaticamente
                    Graphics2D g2d = (Graphics2D) graphics;
                    
                    // Desenha o nosso cabeçalho customizado apenas na primeira página do relatório
                    if (pageIndex == 0) {
                        g2d.setColor(Color.BLACK);

                        // Linha 1: Nome da Instituição
                        g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
                        String t1 = "UFCSPA";
                        int x1 = (int) ((pageFormat.getImageableWidth() - g2d.getFontMetrics().stringWidth(t1)) / 2 + pageFormat.getImageableX());
                        g2d.drawString(t1, x1, (int) pageFormat.getImageableY() + 20);

                        // Linha 2: Nome do Documento
                        g2d.setFont(new Font("Tahoma", Font.PLAIN, 11));
                        String t2 = "Relatório de Inventário Sanitário de Imunizantes";
                        int x2 = (int) ((pageFormat.getImageableWidth() - g2d.getFontMetrics().stringWidth(t2)) / 2 + pageFormat.getImageableX());
                        g2d.drawString(t2, x2, (int) pageFormat.getImageableY() + 38);

                        // Linha 3: Filtro Selecionado
                        g2d.setFont(new Font("Tahoma", Font.BOLD, 11));
                        String t3 = "Filtro Status: " + statusSelecionado;
                        int x3 = (int) ((pageFormat.getImageableWidth() - g2d.getFontMetrics().stringWidth(t3)) / 2 + pageFormat.getImageableX());
                        g2d.drawString(t3, x3, (int) pageFormat.getImageableY() + 53);

                        // Linha Divisória Cinza
                        g2d.setColor(new Color(200, 200, 200));
                        g2d.drawLine((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY() + 65, 
                                     (int) (pageFormat.getImageableX() + pageFormat.getImageableWidth()), (int) pageFormat.getImageableY() + 65);
                    }

                    // Transforma o PageFormat criando uma margem superior de 80 pixels para dar espaço ao topo artesanal
                    java.awt.print.PageFormat pfAlterado = (java.awt.print.PageFormat) pageFormat.clone();
                    java.awt.print.Paper papel = pfAlterado.getPaper();
                    
                    // Se for a primeira página, empurra o início da JTable para baixo da linha divisória
                    if (pageIndex == 0) {
                        papel.setImageableArea(
                            papel.getImageableX(), 
                            papel.getImageableY() + 80, 
                            papel.getImageableWidth(), 
                            papel.getImageableHeight() - 80
                        );
                        pfAlterado.setPaper(papel);
                    }

                    // Envia o comando de renderização para a JTable desenhar as linhas no espaço restante
                    return tabelaPrintable.print(graphics, pfAlterado, pageIndex);
                }
            };

            // Associa o nosso renderizador customizado ao trabalho de impressão
            job.setPrintable(printableCustom);

            // Exibe a caixa de diálogo nativa do sistema operacional (salvar como PDF/impressora)
            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(view, "Relatório PDF gerado com sucesso!", "Impressão Concluída", JOptionPane.INFORMATION_MESSAGE);
            }

            // 5. RESTAURAÇÃO: Devolve o layout colorido e a coluna de ações para a interface do operador
            view.getTabelaEstoque().putClientProperty("imprimindo", null);
            view.getTabelaEstoque().getColumnModel().getColumn(7).setMinWidth(100);
            view.getTabelaEstoque().getColumnModel().getColumn(7).setMaxWidth(100);
            view.getTabelaEstoque().getColumnModel().getColumn(7).setPreferredWidth(100);
            
            view.getTabelaEstoque().repaint();

        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(view, "Erro ao processar impressão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception Hex) {
            System.err.println("Erro geral na impressão: " + Hex.getMessage());
        }
    }
    
    private void atualizarTabelaConsumoPessoal() {
        if (this.usuarioLogado == null || view.getTabelaConsumoPessoal() == null) {
            return;
        }
        
        // Busca a lista de objetos criada no DAO filtrada pelo ID do usuário logado
        List<Object[]> historicoPessoal = dao.listarConsumoPorColaborador(this.usuarioLogado.getIdColaborador());
        DefaultTableModel modelo = (DefaultTableModel) view.getTabelaConsumoPessoal().getModel();
        modelo.setRowCount(0); // Limpa a tabela antes de recarregar
        
        for (Object[] linha : historicoPessoal) {
            modelo.addRow(linha);
        }
    }

    private void atualizarDestaqueMenu(JButton botaoAtivo) {
        Color corAzul = new Color(52, 73, 94);
        Color corLaranja = new Color(230, 126, 34); // Laranja feedback original
        JButton[] botoes = {
            view.getBtnMenuCadastro(), 
            view.getBtnMenuPesquisa(), 
            view.getBtnMenuEdicao(), 
            view.getBtnMenuEstoque(), 
            view.getBtnMenuDashboard(),
            view.getBtnMenuRelatorio(),
            view.getBtnMenuAppQR(),
            view.getBtnMenuSolicitacoes()
        };
        for (JButton b : botoes) {
            if (b == botaoAtivo) {
                b.setBackground(corLaranja);
                b.setFont(new Font("Tahoma", Font.BOLD, 12));
            } else {
                b.setBackground(corAzul);
                b.setFont(new Font("Tahoma", Font.PLAIN, 12));
            }
        }
    }
    
    private void atualizarDadosDashboard() {
        // 1. Busca os dados dos cartões de KPIs antigos no banco
        int[] kpis = dao.buscarMetricasKPIs();         
        // 2. NOVA BUSCA v10.5: Busca o total volumétrico de perdas diretamente da tabela
        int totalDescartes = dao.buscarTotalFrascosDescartados();
        
        // Atualiza os textos dos cartões na View
        view.getLblKpiFrascos().setText(String.valueOf(kpis[0])); //
        view.getLblKpiPostos().setText(String.valueOf(kpis[1])); //
        view.getLblKpiAlertas().setText(String.valueOf(kpis[2])); //
        
        // Seta o novo card de descartes na tela
        if (view.getLblKpiDescartes() != null) {
            view.getLblKpiDescartes().setText(String.valueOf(totalDescartes));
        }

        // 3. Busca e renderiza os dados do Gráfico de Barras (Postos)
        java.util.Map<String, Integer> dadosPostos = dao.buscarConsumoPorPosto(); //
        view.getGraficoBarrasPosto().setDados(dadosPostos); //

        // 4. Busca e renderiza os dados do Gráfico de Pizza (Imunizantes)
        java.util.Map<String, Integer> dadosImunizantes = dao.buscarConsumoPorImunizante(); //
        view.getGraficoPizzaVacina().setDados(dadosImunizantes); //
        
        // 5. NOVO GRÁFICO v10.5: Alimenta a pizza de motivos na linha inferior dedicada
        if (view.getGraficoPizzaMotivoDescarte() != null) {
            java.util.Map<String, Integer> dadosDescartes = dao.buscarDescartesPorMotivo();
            view.getGraficoPizzaMotivoDescarte().setDados(dadosDescartes);
        }
        
        // =====================================================================
        // 3. REORGANIZAÇÃO v11.6: MOTOR PRESCRITIVO COM ÍNDICE, ORDEM E CONTADOR
        // =====================================================================
        // =====================================================================
        // 3. INTEGRAÇÃO v12.0: MOTOR PRESCRITIVO COM MONITORAMENTO DE STATUS OPERACIONAL
        // =====================================================================
        if (view.getModelSugestoesCompra() != null) {
            view.getModelSugestoesCompra().setRowCount(0);
            
            // Dispara a consulta das vacinas vulneráveis no banco com a nova coluna de status
            java.util.List<Object[]> recomendacoesOriginal = dao.buscarSugestoesDeAcordoComEstoque();
            
            // ORDENAÇÃO: Mantém o critério estável de 4 pesos homologado na v11.8
            recomendacoesOriginal.sort((a, b) -> {
                String pA = (String) a[0];
                String pB = (String) b[0];
                int qtdA = (int) a[2];
                int qtdB = (int) b[2];
                int pesoA = "URGENTE".equals(pA) ? (qtdA == 0 ? 4 : 3) : ("CRÍTICO".equals(pA) ? 2 : 1);
                int pesoB = "URGENTE".equals(pB) ? (qtdB == 0 ? 4 : 3) : ("CRÍTICO".equals(pB) ? 2 : 1);
                
                if (pesoA != pesoB) {
                    return Integer.compare(pesoB, pesoA); 
                } else {
                    return Integer.compare(qtdA, qtdB); 
                }
            });

            int totalSugestoes = recomendacoesOriginal.size();
            int indiceLinha = 1;

            // Alimenta a tabela adicionando dinamicamente o número sequencial e o status operacional
            for (Object[] dadosOriginal : recomendacoesOriginal) {
                Object[] linhaComIndice = new Object[7]; // Expandido para 7 colunas
                linhaComIndice[0] = String.format("%02dº", indiceLinha++); 
                linhaComIndice[1] = dadosOriginal[0]; // Prioridade Logística
                linhaComIndice[2] = dadosOriginal[1]; // Imunizante
                linhaComIndice[3] = dadosOriginal[2]; // Estoque Atual Calculado (0 se vencido)
                linhaComIndice[4] = dadosOriginal[3]; // Estoque Mínimo
                linhaComIndice[5] = dadosOriginal[4]; // Diretriz Operacional Recomendada
                linhaComIndice[6] = dadosOriginal[5]; // Status Operacional ('PENDENTE' ou 'SOLICITAÇÃO REALIZADA')
                
                view.getModelSugestoesCompra().addRow(linhaComIndice);
            }
            
            // ATUALIZAÇÃO RECONFIGURADA DO TÍTULO COM O CONTADOR RESPONSIVO
            javax.swing.border.TitledBorder borda = view.getBordaAcoesDashboard();
            if (borda != null) {
                String tituloBase = "Plano de Ação Inteligente - Sugestão Automática de Aquisição e Reposição";
                borda.setTitle(tituloBase + "   [ Alertas: " + totalSugestoes + " ]");
                view.getPainelDashboard().repaint();
            }

            // SEGURANÇA MOUSE: Remove listeners antigos da JTable do Dashboard para não duplicar na troca de abas
            for (java.awt.event.MouseListener ml : view.getTabelaSugestoesCompra().getMouseListeners()) {
                if (ml.getClass().getName().contains("DashboardModalClick")) {
                    view.getTabelaSugestoesCompra().removeMouseListener(ml);
                }
            }

            // GATILHO INTERATIVO: Escuta o clique duplo na linha da sugestão para abrir a Modal de Compra
            view.getTabelaSugestoesCompra().addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Vinculamos a ação ao clique duplo para evitar disparos acidentais na rolagem
                    if (e.getClickCount() == 2) { 
                        JTable tabela = (JTable) e.getSource();
                        int linhaSel = tabela.getSelectedRow();
                        if (linhaSel != -1) {
                            String statusOp = (String) tabela.getValueAt(linhaSel, 6);
                            
                            // Regra de negócio: Se o pedido já foi encaminhado, bloqueia novas aberturas
                            if ("SOLICITAÇÃO REALIZADA".equals(statusOp)) {
                                JOptionPane.showMessageDialog(view, "Esta reposição já foi encaminhada para o setor de compras e aguarda recebimento físico de novas doses.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                            
                            // Extrai os metadados fixos da linha selecionada
                            String prioridade = (String) tabela.getValueAt(linhaSel, 1);
                            String imunizante = (String) tabela.getValueAt(linhaSel, 2);
                            String diretriz = (String) tabela.getValueAt(linhaSel, 5);
                            
                            // Dispara a montagem da caixinha modal pop-up (Criada abaixo)
                            abrirModalSolicitacaoCompra(prioridade, imunizante, diretriz);
                        }
                    }
                }
            });
        }
        // =====================================================================
              
        // Força a atualização estrutural dos componentes flutuantes
        view.getPainelCentro().repaint();
    }
    
    private void exibirJanelaDetalhesAlerta() {
        // 1. Busca os dados atualizados do banco
        List<Object[]> lotesCriticos = dao.buscarDetalhesLotesAlerta();
        
        if (lotesCriticos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum lote em alerta crítico no momento!", "Status Seguro", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 2. Cria a estrutura da tabela flutuante
        String[] colunas = {"Imunizante", "Lote", "Qtd. Atual (Frascos)", "Estoque Mínimo"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Bloqueia edição
        };

        for (Object[] linha : lotesCriticos) {
            modeloTabela.addRow(linha);
        }

        javax.swing.JTable tabelaAlerta = new javax.swing.JTable(modeloTabela);
        tabelaAlerta.setRowHeight(24);
        tabelaAlerta.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        
        // Customização visual sutil para indicar criticidade (vermelho suave nos textos)
        tabelaAlerta.setForeground(new Color(192, 57, 43)); 

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(tabelaAlerta);
        scrollPane.setPreferredSize(new java.awt.Dimension(550, 200));

        // 3. Monta e exibe a janela modal flutuante (JDialog)
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(view), "Lotes em Alerta Crítico de Desabastecimento", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Painel de cabeçalho da janelinha
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAviso = new JLabel(" Os lotes abaixo atingiram ou estão abaixo do estoque mínimo de segurança:");
        lblAviso.setFont(new Font("Tahoma", Font.BOLD, 11));
        painelTopo.add(lblAviso);
        
        dialog.add(painelTopo, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(view); // Centraliza em cima da tela do painel principal
        dialog.setVisible(true);
    }
    
    private void carregarOpcoesDosFiltros() {
        // 1. Popula Imunizantes
        view.getCbFiltroImunizante().removeAllItems();
        view.getCbFiltroImunizante().addItem("Todos");
        for (String nome : dao.buscarNomesComerciais()) {
            view.getCbFiltroImunizante().addItem(nome);
        }

        // 2. Popula Locais / Postos
        view.getCbFiltroLocal().removeAllItems();
        view.getCbFiltroLocal().addItem("Todos");
        for (String posto : dao.buscarPostosSaude()) {
            view.getCbFiltroLocal().addItem(posto);
        }

        // 3. Popula Operadores (Buscando direto do seu histórico para listar apenas quem já trabalhou)
        view.getCbFiltroOperador().removeAllItems();
        view.getCbFiltroOperador().addItem("Todos");
        
        List<String> listaOperadores = dao.buscarOperadoresDoHistorico();
        if (listaOperadores != null) {
            for (String nomeOperador : listaOperadores) {
                view.getCbFiltroOperador().addItem(nomeOperador);
            }
        }
    }

    private void processarFiltroRelatorio() {
        // 1. CAPTURA A DATA DO JCALENDAR
        java.util.Date dataSelecionada = view.getTxtFiltroData().getDate();
        String dataFormatada = null;

        // Se o usuário selecionou alguma data no calendário, formata para string
        if (dataSelecionada != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            dataFormatada = sdf.format(dataSelecionada);
        }

        // 2. CAPTURA OS VALORES DOS COMBOBOXES
        String operador = (String) view.getCbFiltroOperador().getSelectedItem();
        String imunizante = (String) view.getCbFiltroImunizante().getSelectedItem();
        String local = (String) view.getCbFiltroLocal().getSelectedItem();
        
        // 3. TRATAMENTO DOS FILTROS VAZIOS ("Todos")
        if (operador != null && operador.equals("Todos")) operador = null;
        if (imunizante != null && imunizante.equals("Todos")) imunizante = null;
        if (local != null && local.equals("Todos")) local = null;

        // 4. DISPARA A CONSULTA DINÂMICA NO DAO
        List<Object[]> dadosFiltrados = dao.filtrarRelatorioConsumo(dataFormatada, operador, imunizante, local);
        
        // 5. ATUALIZA A TABELA GRAFICAMENTE
        DefaultTableModel modelo = (DefaultTableModel) view.getTabelaRelatorioGeral().getModel();
        modelo.setRowCount(0); // Limpa as linhas antigas

        for (Object[] linha : dadosFiltrados) {
            modelo.addRow(linha);
        }
    }
    
    private void atualizarTabelaRelatorioGeral() {
        if (view.getTabelaRelatorioGeral() == null) {
            return;
        }
        
        List<Object[]> dadosRelatorio = dao.listarRelatorioConsumoGeral();
        DefaultTableModel modelo = (DefaultTableModel) view.getTabelaRelatorioGeral().getModel();
        modelo.setRowCount(0); // Limpa registros antigos antes de renderizar
        
        for (Object[] linha : dadosRelatorio) {
            modelo.addRow(linha);
        }
    }

    private void setEstadoCampos(boolean editavel) {
        view.getTxtNomeComercial().setEditable(editavel);
        view.getTxtAntigeno().setEditable(editavel);
        view.getTxtFabricante().setEditable(editavel);
        view.getTxtLote().setEditable(editavel);
        view.getTxtValidade().setEnabled(editavel);
        view.getTxtPaisOrigem().setEditable(editavel);
        view.getTxtAnvisa().setEditable(editavel);
        view.getTxtFornecedor().setEditable(editavel);
        view.getTxtQuantidade().setEditable(editavel);
        view.getTxtEstoqueMinimo().setEditable(editavel);
        view.getCbPlataforma().setEnabled(editavel);
        view.getCbApresentacao().setEnabled(editavel);
        view.getCbTemperatura().setEnabled(editavel);
        view.getCbEstabilidade().setEnabled(editavel);
        view.getCbStatusLiberacao().setEnabled(editavel);
    }

    private void limparCampos() {
        view.getTxtNomeComercial().setText("");
        view.getTxtAntigeno().setText("");
        view.getTxtFabricante().setText("");
        view.getTxtLote().setText("");
        view.getTxtValidade().setDate(null);
        view.getTxtPaisOrigem().setText("");
        view.getTxtAnvisa().setText("");
        view.getTxtFornecedor().setText("");
        view.getTxtQuantidade().setText("");
        view.getTxtTotalDoses().setText("0");
        view.getTxtEstoqueMinimo().setText("");
        view.getCbPlataforma().setSelectedIndex(0);
        view.getCbApresentacao().setSelectedIndex(0);
        view.getCbTemperatura().setSelectedIndex(0);
        view.getCbEstabilidade().setSelectedIndex(0);
        view.getCbStatusLiberacao().setSelectedIndex(0);
    }

    private void carregarListaPesquisa() {
        atualizarComboBoxMobile();       
        
        // 1. Carrega os Postos no ComboBox Mobile
        view.getCbQRCodeLocal().removeAllItems();
        view.getCbQRCodeLocal().addItem("Selecione um posto...");
        List<String> postos = dao.buscarPostosSaude();
        if (postos != null) {
            for (String p : postos) view.getCbQRCodeLocal().addItem(p);
        }
        
        // 2. CORREÇÃO v12.5: Alimenta o combo de busca rápida usando a combinação Nome + Lote
        view.getCbBuscaRapida().removeAllItems();
        view.getCbBuscaRapida().addItem("Selecione um imunizante...");
        List<String> identificadores = dao.buscarNomesELotesParaPesquisa();
        if (identificadores != null) {
            for (String iden : identificadores) {
                view.getCbBuscaRapida().addItem(iden);
            }
        }
     // GATILHO VISUAL v12.7: Ativa o renderizador gráfico de triângulos de atenção
        view.getCbBuscaRapida().setRenderer(new br.edu.ufcspa.estoque.view.VacinaView.ComboAlertaRenderer());
    }

    private void carregarDadosSelecionados() {
        String sel = (String) view.getCbBuscaRapida().getSelectedItem();
        if (sel != null && !sel.equals("Selecione um imunizante...")) {
            
            // 1. Identifica se o lote selecionado possui o marcador de risco sanitário
            boolean ehVencido = sel.contains("⚠️ [VENCIDO]");
            
            Vacina v = dao.buscarPorIdentificadorCombinado(sel);
            if (v != null) {
                this.idVacinaSelecionada = v.getId();
                
                view.getTxtNomeComercial().setText(v.getNome());
                view.getTxtAntigeno().setText(v.getAntigenoAlvo());
                view.getTxtFabricante().setText(v.getFabricante());
                view.getTxtLote().setText(v.getLote());
                try {
                    java.util.Date date = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(v.getValidade());
                    view.getTxtValidade().setDate(date);
                } catch (Exception e) {
                    view.getTxtValidade().setDate(null);
                }
                view.getTxtPaisOrigem().setText(v.getPaisOrigem());
                view.getTxtAnvisa().setText(v.getRegistroAnvisa());
                view.getTxtFornecedor().setText(v.getFornecedor());
                view.getTxtQuantidade().setText(String.valueOf(v.getQuantidade()));
                view.getTxtTotalDoses().setText(String.valueOf(v.getTotalDoses()));
                view.getTxtEstoqueMinimo().setText(String.valueOf(v.getEstoqueMinimo()));
                view.getCbPlataforma().setSelectedItem(v.getPlataforma());
                view.getCbApresentacao().setSelectedItem(v.getApresentacao());
                view.getCbTemperatura().setSelectedItem(v.getFaixaTemperatura());
                view.getCbEstabilidade().setSelectedItem(v.getEstabilidade());
                view.getCbStatusLiberacao().setSelectedItem(v.getStatusLiberacao());
                
                // 2. TRATAMENTO v12.7: Se estiver vencido, tranca a interface e emite alerta visual
                if (ehVencido) {
                    setEstadoCampos(false); // Tranca todos os campos de digitação
                    view.getBtnSalvar().setText("LOTE VENCIDO - BLOQUEADO");
                    view.getBtnSalvar().setBackground(new Color(192, 57, 43)); // Vermelho Alerta
                    view.getBtnSalvar().setEnabled(false); // Desativa o clique
                    
                    JOptionPane.showMessageDialog(view, 
                        "ATENÇÃO: Este lote está vencido e retido para descarte técnico.\n" +
                        "Qualquer alteração ou movimentação de saída foi bloqueada pelo sistema.", 
                        "Segurança Sanitária UFCSPA", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Se o modo atual for edição, mantém liberado; caso contrário segue a regra do modo
                    if ("EDICAO".equals(this.modoAtual)) {
                        setEstadoCampos(true);
                        view.getBtnSalvar().setText("ATUALIZAR DADOS");
                        view.getBtnSalvar().setBackground(new Color(241, 196, 15));
                        view.getBtnSalvar().setEnabled(true);
                    }
                }
            }
        }
    }
    
    private void atualizarComboBoxMobile() {
        view.getCbQRCodeNome().removeAllItems();
        view.getCbQRCodeNome().addItem("Selecione um imunizante...");
        List<String> nomes = dao.buscarNomesComerciais();
        if (nomes != null) {
            for (String n : nomes) {
                view.getCbQRCodeNome().addItem(n);
            }
        }
    }
    
    private void efetuarLogout() {
        view.dispose();
        LoginView lv = new LoginView();
        new LoginController(lv);
        lv.setVisible(true);
    }

    public void excluirVacinaPelaTabela(int linha) {
        String nomeVacina = (String) view.getTabelaEstoque().getValueAt(linha, 0);
        String loteVacina = (String) view.getTabelaEstoque().getValueAt(linha, 1);

        int confirma = JOptionPane.showConfirmDialog(view, "Deseja excluir permanentemente o imunizante: " + nomeVacina + " (Lote: " + loteVacina + ")?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirma == JOptionPane.YES_OPTION) {
            if (dao.excluir(nomeVacina, loteVacina)) {
                JOptionPane.showMessageDialog(view, "Imunizante removido com sucesso!");
                atualizarTabelaEstoque();
            } else {
                JOptionPane.showMessageDialog(view, "Erro ao excluir do banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void editarVacinaPelaTabela(int linha) {
        // Converte o índice da linha caso a tabela esteja com algum filtro ativo
        if (linha != -1) {
            linha = view.getTabelaEstoque().convertRowIndexToModel(linha);
        }

        // 1. Captura o nome da vacina diretamente da linha selecionada na tabela
        String nomeVacina = (String) view.getTabelaEstoque().getModel().getValueAt(linha, 0);
        
        // 2. Busca o objeto completo com todas as propriedades técnicas no banco de dados
        Vacina v = dao.buscarPorNome(nomeVacina);
        
        if (v != null) {
            // Guarda o ID da vacina para sabermos quem atualizar no banco no momento do clique em salvar
            this.idVacinaSelecionada = v.getId();
            
            // =====================================================================
            // CORREÇÃO VISUAL DE ALOCAÇÃO DE OBJETOS NO COMBOBOX REAL (cbBuscaRapida)
            // =====================================================================
            if (view.getCbBuscaRapida() != null) {
                // Modificado para aceitar String puro em conformidade com o JComboBox<String> da View
                javax.swing.JComboBox<String> combo = view.getCbBuscaRapida();
                boolean selecionado = false;
                
                for (int i = 0; i < combo.getItemCount(); i++) {
                    Object item = combo.getItemAt(i);
                    // Compara o texto visível da linha do combo com o nome vindo da tabela
                    if (item != null && item.toString().equalsIgnoreCase(nomeVacina)) {
                        combo.setSelectedIndex(i);
                        selecionado = true;
                        break;
                    }
                }
                
                // Se não localizou no laço, força a seleção direta pelo objeto/texto
                if (!selecionado) {
                    combo.setSelectedItem(nomeVacina);
                }
            }
            // =====================================================================
            
            // 3. Popula rigorosamente todos os campos da tela de cadastro/edição
            view.getTxtNomeComercial().setText(v.getNome());
            view.getTxtAntigeno().setText(v.getAntigenoAlvo());
            view.getTxtFabricante().setText(v.getFabricante());
            view.getTxtLote().setText(v.getLote());
            
            try {
                java.util.Date date = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(v.getValidade());
                view.getTxtValidade().setDate(date);
            } catch (Exception e) {
                view.getTxtValidade().setDate(null);
            }
            
            view.getTxtPaisOrigem().setText(v.getPaisOrigem());
            view.getTxtAnvisa().setText(v.getRegistroAnvisa());
            view.getTxtFornecedor().setText(v.getFornecedor());
            view.getTxtQuantidade().setText(String.valueOf(v.getQuantidade()));
            view.getTxtTotalDoses().setText(String.valueOf(v.getTotalDoses()));
            view.getTxtEstoqueMinimo().setText(String.valueOf(v.getEstoqueMinimo()));
            
            // Sincroniza as caixas de seleção técnica (ComboBoxes)
            view.getCbPlataforma().setSelectedItem(v.getPlataforma());
            view.getCbApresentacao().setSelectedItem(v.getApresentacao());
            view.getCbTemperatura().setSelectedItem(v.getFaixaTemperatura());
            view.getCbEstabilidade().setSelectedItem(v.getEstabilidade());
            view.getCbStatusLiberacao().setSelectedItem(v.getStatusLiberacao());
            
            // 4. Altera o modo do sistema e muda o foco visual para a tela do formulário
            prepararModo("EDICAO");
        }
    }
    
    /**
     * Intercepta cliques na coluna de Descartes para abrir os dados de auditoria
     */
    private void configurarCliqueAuditoriaDescarte() {
        // Remove quaisquer listeners antigos para não duplicar chamadas na memória
        for (java.awt.event.MouseListener ml : view.getTabelaEstoque().getMouseListeners()) {
            if (ml.getClass().getName().contains("AuditoriaDescarte")) {
                view.getTabelaEstoque().removeMouseListener(ml);
            }
        }

        view.getTabelaEstoque().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTable tabela = (JTable) e.getSource();
                int linhaSelecionada = tabela.getSelectedRow();
                int colunaSelecionada = tabela.getSelectedColumn();

                // Converte o índice da linha caso a tabela esteja filtrada
                if (linhaSelecionada != -1) {
                    linhaSelecionada = tabela.convertRowIndexToModel(linhaSelecionada);
                }

                // Coluna 3 é a coluna "Descartes (Fr.)"
                if (colunaSelecionada == 3 && linhaSelecionada != -1) {
                    Object valorCelula = tabela.getModel().getValueAt(linhaSelecionada, 3);
                    if (valorCelula != null) {
                        // Remove todas as tags HTML mantendo apenas o número puro
                        String textoPuro = valorCelula.toString().replaceAll("<[^>]*>", "").trim();
                        // Remove a frase "Ver Detalhes" para isolar o número
                        String numeroPuro = textoPuro.replace("Ver Detalhes", "").trim();
                        
                        try {
                            int totalDescartes = Integer.parseInt(numeroPuro);
                            String lote = tabela.getModel().getValueAt(linhaSelecionada, 1).toString();
                            
                            if (totalDescartes > 0) {
                                exibirJanelaDetalhesDescarte(lote);
                            }
                        } catch (NumberFormatException ex) {
                            // Proteção caso a célula esteja vazia ou inconsistente
                        }
                    }
                }
            }
        });
    }

    /**
     * Renderiza o JDialog com as informações completas solicitadas (Quem, Onde, Quando, Por quê)
     */
    private void exibirJanelaDetalhesDescarte(String lote) {
        List<Object[]> logs = dao.buscarDetalhesDescarteLote(lote);
        
        String[] colunas = {"Data/Hora", "Responsável", "Motivo", "Local", "Quant. Frasco(s)"};
        DefaultTableModel modeloModal = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Object[] linha : logs) {
            modeloModal.addRow(linha);
        }

        JTable tabelaModal = new JTable(modeloModal);
        tabelaModal.setRowHeight(24);
        tabelaModal.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        tabelaModal.setForeground(new Color(192, 57, 43)); // Destaque visual vermelho suave de descarte

        JScrollPane scroll = new JScrollPane(tabelaModal);
        scroll.setPreferredSize(new java.awt.Dimension(750, 250));

        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(view), "Relatório de Auditoria e Rastreabilidade de Descarte - Lote: " + lote, true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        try {
            java.net.URL urlLogo = getClass().getResource("/br/edu/ufcspa/estoque/images/icon_vaccine.png");
            if (urlLogo != null) {
                java.awt.Image iconeJanela = new javax.swing.ImageIcon(urlLogo).getImage();
                dialog.setIconImage(iconeJanela); // Substitui o ícone genérico do café pelo logo real
            }
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar o logotipo na modal: " + e.getMessage());
        }
        
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAviso = new JLabel(" Histórico completo de perdas sanitárias registradas para este lote:");
        lblAviso.setFont(new Font("Tahoma", Font.BOLD, 14));
        painelTopo.add(lblAviso);
        
        dialog.add(painelTopo, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }
    
    // =====================================================================
    // OPERAÇÃO v12.3: RENDERIZAR MODAL OPERACIONAL DE COMPRAS (COMPLETO)
    // =====================================================================
    private void abrirModalSolicitacaoCompra(String prioridade, String imunizante, String diretriz) {
        // 1. Inicialização da Janela Modal Ancorada na View
        javax.swing.JDialog modal = new javax.swing.JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(view), "Ordem de Requisição de Insumos - UFCSPA", true);
        modal.setSize(580, 260);
        modal.setLocationRelativeTo(view);
        
        // Painel Principal com Layout de Caixa Vertical (Empilha os elementos de cima para baixo)
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new javax.swing.BoxLayout(painelConteudo, javax.swing.BoxLayout.Y_AXIS));
        painelConteudo.setBackground(Color.WHITE);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Margens internas de respiro

        // 2. Elementos de Texto do Painel
        JLabel lblInfo = new JLabel("Confirma a abertura do pedido de aquisição para o imunizante abaixo?");
        lblInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblInfo.setForeground(new Color(44, 62, 80));
        lblInfo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Subpainel para alinhar as informações do produto lado a lado
        JPanel pDados = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));
        pDados.setBackground(Color.WHITE);
        JLabel lblProd = new JLabel("Imunizante: "); lblProd.setFont(new Font("Tahoma", Font.BOLD, 12));
        JLabel lblProdNome = new JLabel(imunizante); lblProdNome.setFont(new Font("Tahoma", Font.PLAIN, 12));
        pDados.add(lblProd);
        pDados.add(lblProdNome);

        // Subpainel para o regulador numérico da quantidade
        JPanel pQuantidade = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pQuantidade.setBackground(Color.WHITE);
        JLabel lblQtd = new JLabel("Quantidade de Frascos: "); lblQtd.setFont(new Font("Tahoma", Font.BOLD, 12));
        javax.swing.JSpinner spinQtd = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(50, 1, 10000, 10));
        spinQtd.setFont(new Font("Tahoma", Font.BOLD, 13));
        spinQtd.setPreferredSize(new java.awt.Dimension(90, 26));
        pQuantidade.add(lblQtd);
        pQuantidade.add(spinQtd);

        // 3. Configuração dos Botões com Padrão Sólido Comercial UFCSPA
        JButton btnConfirmar = new JButton("CONFIRMAR SOLICITAÇÃO");
        btnConfirmar.setBackground(new Color(39, 174, 96)); // Verde Comercial
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setPreferredSize(new java.awt.Dimension(200, 36));

        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setBackground(new Color(149, 165, 166)); // Cinza de Controle
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setPreferredSize(new java.awt.Dimension(100, 36));

        // Subpainel do Rodapé para abrigar os dois botões alinhados à direita
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pBotoes.setBackground(Color.WHITE);
        pBotoes.add(btnVoltar);
        pBotoes.add(btnConfirmar);

        // 4. Montagem Sequencial do Painel
        painelConteudo.add(lblInfo);
        painelConteudo.add(pDados);
        painelConteudo.add(pQuantidade);
        painelConteudo.add(pBotoes);
        
        modal.add(painelConteudo);

        // 5. Mapeamento das Ações dos Botões
        btnVoltar.addActionListener(e -> modal.dispose());

        btnConfirmar.addActionListener(e -> {
            int qtdPedida = (int) spinQtd.getValue();
            
            // Resgata o usuário ativo da sessão corporativa de forma dinâmica
            String nomeOperador = (usuarioLogado != null) ? usuarioLogado.getNome() : "Administrador Geral";
            String nivelAcesso = (usuarioLogado != null) ? usuarioLogado.getNivelAcesso() : "ADMIN";

            // Dispara persistência oficial da ordem de compra
            boolean gravou = dao.salvarSolicitacaoCompra(imunizante, qtdPedida, prioridade, diretriz, nomeOperador, nivelAcesso);

            if (gravou) {
                JOptionPane.showMessageDialog(modal, "SOLICITAÇÃO ENVIADA COM SUCESSO!\nA requisição foi registrada no livro oficial de logística.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                modal.dispose();
                
                // Recarrega o painel do dashboard para atualizar os sinalizadores "SOLICITAÇÃO REALIZADA"
                atualizarDadosDashboard();
            } else {
                JOptionPane.showMessageDialog(modal, "Erro crítico de banco de dados ao salvar a ordem.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            }
        });

        modal.setVisible(true);
    }

    // =====================================================================
    // OPERAÇÃO v12.0: CARREGAR DADOS NA NOVA TELA DE HISTÓRICO DE COMPRAS
    // =====================================================================
    private void atualizarListaSolicitacoes() {
        if (view.getModelViewSolicitacoes() != null) {
            view.getModelViewSolicitacoes().setRowCount(0);
            
            // Busca o histórico ordenado gravado na tabela nova
            List<Object[]> pedidos = dao.listarSolicitacoesRealizadas();
            
            for (Object[] linha : pedidos) {
                view.getModelViewSolicitacoes().addRow(linha);
            }
        }
    }
    
    // =====================================================================
    // CONTROLE LOGÍSTICO v12.4: POPULAR O COMBOBOX DE VINCULAÇÃO DE COMPRAS
    // =====================================================================
    private void carregarComboVinculoSolicitacoes() {
        if (view.getCbVinculoSolicitacao() != null) {
            view.getCbVinculoSolicitacao().removeAllItems();
            view.getCbVinculoSolicitacao().addItem("Nenhuma / Entrada Avulsa");
            
            // Puxa do DAO apenas as vacinas com pedidos abertos (PENDENTE)
            List<String> pendentes = dao.listarNomesImunizantesPendentes();
            if (pendentes != null) {
                for (String nome : pendentes) {
                    view.getCbVinculoSolicitacao().addItem(nome);
                }
            }
        }
    }
    
}