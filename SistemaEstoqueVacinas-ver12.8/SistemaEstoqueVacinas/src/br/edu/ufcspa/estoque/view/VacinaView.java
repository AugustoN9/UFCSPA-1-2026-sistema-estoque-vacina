package br.edu.ufcspa.estoque.view;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import br.edu.ufcspa.estoque.controller.VacinaController;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Interface v5.0 - Design Original de Alta Definição Restaurado.
 */
public class VacinaView extends JFrame {
	
    private Font fonteTexto = new Font("Tahoma", Font.PLAIN, 14);
    private Font fonteTitulo = new Font("Tahoma", Font.BOLD, 14);

    // --- Componentes de Menu (Estilo Original Preservado) ---
    private JButton btnMenuCadastro, btnMenuPesquisa, btnMenuEdicao, btnMenuEstoque, btnMenuDashboard, btnMenuRelatorio, btnMenuAppQR, btnLogout;
    private JComboBox<String> cbBuscaRapida;

    // --- Painéis de Conteúdo ---
    private JPanel painelCentro, painelFormulario, painelEstoque, painelAppQRCode;

    // --- Componentes do Formulário ---
    private JTextField txtNomeComercial, txtAntigeno, txtFabricante, txtLote,  
                       txtPaisOrigem, txtAnvisa, txtFornecedor, txtQuantidade, 
                       txtTotalDoses, txtEstoqueMinimo;
    
    private com.toedter.calendar.JDateChooser txtValidade;
    
    private JComboBox<String> cbPlataforma, cbApresentacao, cbTemperatura, cbEstabilidade, cbStatusLiberacao;
    private JButton btnSalvar, btnLimpar;

    // --- Componentes da Tabela ---
    private JTable tabelaEstoque;
    private DefaultTableModel modelTabela;
    private JLabel lblStatusUsuario;
    
    // --- Componentes Terminal QR ---
    private JComboBox<String> cbQRCodeNome;
    private JTextField txtQRCodeLote;
    private JSpinner spinQtdConsumo;
    private JButton btnRegistrarConsumo;
    private JCheckBox chkRegistrarPerda;
    private JComboBox<String> cbMotivoPerda;
    
    //private JTextField txtQRCodeLocal;
    private JComboBox<String> cbQRCodeLocal;
    private JTable tabelaConsumoPessoal;
    
    private JPanel painelRelatorio;
    private JTable tabelaRelatorioGeral;
    
    private com.toedter.calendar.JDateChooser txtFiltroData;
    private JComboBox<String> cbFiltroOperador, cbFiltroImunizante, cbFiltroLocal;
    private JButton btnFiltrarRelatorio;
    private JButton btnAtualizarRelatorio;
    
    private JPanel painelDashboard;
    private JLabel lblKpiFrascos, lblKpiPostos, lblKpiAlertas;
    private PainelGraficoBarras graficoBarrasPosto;
    private PainelGraficoPizza graficoPizzaVacina;
    
    private JComboBox<String> cbFiltroStatusEstoque;
    private JButton btnImprimirEstoque;
    
    private JLabel lblKpiDescartes;
    private PainelGraficoPizza graficoPizzaMotivoDescarte;
    private JTable tabelaSugestoesCompra;
    private DefaultTableModel modelSugestoesCompra;    
    

    private JButton btnMenuSolicitacoes;
    private JPanel painelSolicitacoes;
    private JTable tabelaViewSolicitacoes;
    private DefaultTableModel modelViewSolicitacoes;
    
    private javax.swing.JComboBox<String> cbVinculoSolicitacao;
    private JTextField txtFiltroDigitado;


    public VacinaView() {
        setTitle("SISTEMA DE GESTÃO DE IMUNIZANTES v12.8 - UFCSPA");
        try {
            Image iconeSistema = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/edu/ufcspa/estoque/images/icon_vaccine.png"));
            setIconImage(iconeSistema);
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da janela: " + e.getMessage());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        configurarMenuLateral();

        painelCentro = new JPanel(new CardLayout());
        configurarPainelFormulario();
        configurarPainelEstoque();
        configurarPainelAppQRCode();
        configurarPainelRelatorio(); 
        configurarPainelDashboard();
        configurarPainelSolicitacoes();
       
        painelCentro.add(painelFormulario, "FORM");
        painelCentro.add(painelEstoque, "ESTOQUE");
        painelCentro.add(painelAppQRCode, "APP_QRCODE");
        painelCentro.add(painelRelatorio, "RELATORIO");
        painelCentro.add(painelDashboard, "DASHBOARD");
        painelCentro.add(painelSolicitacoes, "SOLICITACOES");

        add(painelCentro, BorderLayout.CENTER);
    }

	private void configurarMenuLateral() {
        // Correção do erro de sintaxe: usando push para empurrar os botões perfeitamente
        JPanel menu = new JPanel(new MigLayout("wrap 1, insets 20 15 20 15, fillx", "[grow]", "[]25[]10[]10[]10[]10[]10[]10[]10[]push[]"));
        menu.setBackground(new Color(44, 62, 80)); // Cor azul escuro fosco original do vídeo
        menu.setPreferredSize(new Dimension(240, 0));

        JLabel lblLogo = new JLabel("MENU PRINCIPAL");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Tahoma", Font.BOLD, 16));
        menu.add(lblLogo, "center, gapbottom 15");

        btnMenuCadastro = criarBotaoMenu("NOVO CADASTRO");
        btnMenuPesquisa = criarBotaoMenu("PESQUISAR");
        btnMenuEdicao = criarBotaoMenu("EDITAR REGISTRO");
        btnMenuEstoque = criarBotaoMenu("ESTOQUE");
        btnMenuDashboard = criarBotaoMenu("DASHBOARD");
        btnMenuRelatorio = criarBotaoMenu("RELATÓRIO");
        btnMenuAppQR = criarBotaoMenu("APP QRCODE");
        btnMenuSolicitacoes = criarBotaoMenu("SOLICITAÇÕES");
        
        btnLogout = new JButton("LOGOUT/SAIR");
        btnLogout.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnMenuSolicitacoes.setBackground(new Color(39, 174, 96));

        menu.add(btnMenuCadastro, "growx, h 40!");
        menu.add(btnMenuPesquisa, "growx, h 40!");
        menu.add(btnMenuEdicao, "growx, h 40!");
        menu.add(btnMenuEstoque, "growx, h 40!");
        menu.add(btnMenuDashboard, "growx, h 40!");
        menu.add(btnMenuRelatorio, "growx, h 40!");
        menu.add(btnMenuAppQR, "growx, h 40!");
        menu.add(btnMenuSolicitacoes, "growx, h 40!");
        menu.add(btnLogout, "growx, h 40!, bottom");

        add(menu, BorderLayout.WEST);
    }

    private void configurarPainelFormulario() {
        painelFormulario = new JPanel(new MigLayout("fillx, insets 20", "[grow]"));
        painelFormulario.setBackground(new Color(240, 242, 245));

        JPanel pBusca = new JPanel(new MigLayout("fillx, insets 12", "[][grow][20px!][][200px!]"));
        pBusca.setBackground(Color.WHITE);
        pBusca.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(220,224,230)), "Busca Rápida de Imunizantes", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        JLabel lblBusca = new JLabel("Selecionar Imunizante:");
        lblBusca.setFont(fonteTexto);
        cbBuscaRapida = new JComboBox<>(new String[]{"Selecione um imunizante..."});
        cbBuscaRapida.setFont(fonteTexto);
        
        JLabel lblDigitar = new JLabel("Ou Digite (Nome/Lote):");
        lblDigitar.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDigitar.setForeground(new Color(44, 62, 80));
        
        txtFiltroDigitado = new JTextField();
        txtFiltroDigitado.setFont(fonteTexto);
        txtFiltroDigitado.setToolTipText("Digite parte do nome comercial ou número do lote para filtrar as opções...");
        
        pBusca.add(lblBusca);
        pBusca.add(cbBuscaRapida, "growx");
        pBusca.add(new JLabel("")); // Espaçador invisível na coluna 3
        pBusca.add(lblDigitar);
        pBusca.add(txtFiltroDigitado, "growx, h 30!");
        
        painelFormulario.add(pBusca, "growx, wrap, gapbottom 10");
        JPanel pSuperior = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        
        pSuperior.setOpaque(false);

        // 1. Identificação Técnica
        JPanel p1 = new JPanel(new MigLayout("wrap 2, fillx, insets 15", "[130px!]10[grow]"));
        p1.setBackground(Color.WHITE);
        p1.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(220,224,230)), "1. Identificação Técnica", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        txtNomeComercial = adicionarLinhaFormulario(p1, "Nome Comercial:");
        txtAntigeno = adicionarLinhaFormulario(p1, "Antígeno Alvo:");
        txtFabricante = adicionarLinhaFormulario(p1, "Laboratório:");

        JLabel lblPlataforma = new JLabel("Plataforma:"); lblPlataforma.setFont(fonteTexto);
        cbPlataforma = new JComboBox<>(new String[]{"Selecione...", "Bacteria Atenuada", "Polissacarídeo Conjugado", "Proteína Recombinante (Subunidade)", "Proteína Recombinante com Adjuvante AS01B", "Toxoides + Células Inteiras Inativadas", "VLP (Virus-Like Particle)", "Vetor Viral Não Replicante", "Vírus Atenuado", "Vírus Atenuado Oral", "Vírus Atenuado Quimérico", "Vírus Inativado", "Vírus Inativado Fragmentado", "mRNA Nanopartícula Lipídica"});
        cbPlataforma.setFont(fonteTexto);
        p1.add(lblPlataforma); p1.add(cbPlataforma, "growx, h 32!");
        
        JLabel lblApresentacao = new JLabel("Apresentação:"); lblApresentacao.setFont(fonteTexto);
        cbApresentacao = new JComboBox<>(new String[]{"Selecione...", "Aplicador Oral Monodose", "Frasco-ampola Monodose", "Frasco-ampola Monodose (Kit 2 frascos)", "Frasco-ampola Multidose (10 doses)", "Frasco-ampola Multidose (5 doses)", "Frasco-ampola Multidose (6 doses)", "Seringa Preenchida Monodose"});
        cbApresentacao.setFont(fonteTexto);
        p1.add(lblApresentacao); p1.add(cbApresentacao, "growx, h 32!");

        // 2. Controle e Rastreabilidade (Correção das linhas triplicadas)
        JPanel p2 = new JPanel(new MigLayout("wrap 2, fillx, insets 15", "[130px!]10[grow]"));
        p2.setBackground(Color.WHITE);
        p2.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(220,224,230)), "2. Controle e Rastreabilidade", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        txtLote = adicionarLinhaFormulario(p2, "Número do Lote:");

        JLabel lblValidade = new JLabel("Validade:"); lblValidade.setFont(fonteTexto);
        txtValidade = new com.toedter.calendar.JDateChooser();
        txtValidade.setDateFormatString("dd/MM/yyyy");
        txtValidade.setFont(fonteTexto);
        p2.add(lblValidade); p2.add(txtValidade, "growx, h 32!");

        txtPaisOrigem = adicionarLinhaFormulario(p2, "País de Origem:");
        txtAnvisa = adicionarLinhaFormulario(p2, "Registro ANVISA:");
        txtFornecedor = adicionarLinhaFormulario(p2, "Fornecedor:");

        

        pSuperior.add(p1, "grow, pushy");
        pSuperior.add(p2, "grow, pushy");
        painelFormulario.add(pSuperior, "growx, wrap, gapbottom 10");

        JPanel pInferior = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        pInferior.setOpaque(false);

        // 3. Gestão de Estocagem
        JPanel p3 = new JPanel(new MigLayout("wrap 2, fillx, insets 15", "[130px!]10[grow]"));
        p3.setBackground(Color.WHITE);
        p3.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(220,224,230)), "3. Gestão de Estoque", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        txtQuantidade = adicionarLinhaFormulario(p3, "Qtd. Frascos:");
        txtTotalDoses = adicionarLinhaFormulario(p3, "Total de Doses:");
        txtTotalDoses.setEditable(false);
        txtTotalDoses.setBackground(new Color(235, 237, 240));
        txtEstoqueMinimo = adicionarLinhaFormulario(p3, "Estoque Mínimo:");

        // 4. Logística Sanitária
        JPanel p4 = new JPanel(new MigLayout("wrap 2, fillx, insets 15", "[130px!]10[grow]"));
        p4.setBackground(Color.WHITE);
        p4.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(220,224,230)), "4. Logística e Estabilidade", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        JLabel lblTemp = new JLabel("Cadeia de Frio:"); lblTemp.setFont(fonteTexto);
        cbTemperatura = new JComboBox<>(new String[]{"Selecione...", "-50°C a -15°C", "-90°C a -60°C", "2°C a 8°C"});
        cbTemperatura.setFont(fonteTexto);
        p4.add(lblTemp); p4.add(cbTemperatura, "growx, h 32!");

        JLabel lblEstabilidade = new JLabel("Estabilidade:"); lblEstabilidade.setFont(fonteTexto);
        cbEstabilidade = new JComboBox<>(new String[]{"Selecione...", "12 horas a 2-8°C", "2 horas a 2-8°C", "28 dias a 2-8°C", "30 minutos", "4 semanas a 2-8°C", "6 horas a 2-8°C", "6 horas em temperatura ambiente", "Uso imediato"});
        cbEstabilidade.setFont(fonteTexto);
        p4.add(lblEstabilidade); p4.add(cbEstabilidade, "growx, h 32!");

        JLabel lblStatus = new JLabel("Status Liberação:"); lblStatus.setFont(fonteTexto);
        cbStatusLiberacao = new JComboBox<>(new String[]{"Selecione...", "Liberado pelo INCQS/CQ", "Quarentena/CQ"});
        cbStatusLiberacao.setFont(fonteTexto);
        p4.add(lblStatus); p4.add(cbStatusLiberacao, "growx, h 32!");

        pInferior.add(p3, "grow, pushy");
        pInferior.add(p4, "grow, pushy");
        painelFormulario.add(pInferior, "growx, wrap, gapbottom 10");
        
        // =====================================================================
        // INTEGRAÇÃO LOGÍSTICA v12.4: PAINEL DE VINCULAÇÃO DE COMPRAS
        // =====================================================================
        JPanel p5 = new JPanel(new MigLayout("fillx, insets 12", "[][grow]"));
        p5.setBackground(Color.WHITE);
        p5.setBorder(BorderFactory.createLineBorder(new Color(220,224,230)));
        
        JLabel lblVinculo = new JLabel("Atende a alguma Solicitação de Reposição Pendente?");
        lblVinculo.setFont(fonteTexto);
        lblVinculo.setForeground(new Color(230, 126, 34)); // Destaque em Laranja Logístico
        
        cbVinculoSolicitacao = new JComboBox<>(new String[]{"Nenhuma / Entrada Avulsa"});
        cbVinculoSolicitacao.setFont(fonteTexto);
        
        p5.add(lblVinculo);
        p5.add(cbVinculoSolicitacao, "growx");
        painelFormulario.add(p5, "growx, wrap, gapbottom 10");
        // =====================================================================

        JPanel pBotoes = new JPanel(new MigLayout("insets 15 0 0 0"));
        pBotoes.setOpaque(false);

        btnLimpar = new JButton(" LIMPAR CAMPOS");
        btnLimpar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnLimpar.setBackground(new Color(149, 165, 166));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBorderPainted(false);

        btnSalvar = new JButton(" SALVAR NOVO");
        btnSalvar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);

        pBotoes.add(btnLimpar, "width 180!, height 40!");
        pBotoes.add(btnSalvar, "width 180!, height 40!");
        painelFormulario.add(pBotoes, "right");
    }
    
    private void configurarPainelAppQRCode() {
        // Layout vertical ajustado para distribuir o formulário e a tabela histórica em baixo
        painelAppQRCode = new JPanel(new MigLayout("wrap, insets 30, fillx, filly", "[grow]", "[]10[]15[]15[]15[grow]"));
        painelAppQRCode.setBackground(new Color(244, 246, 249));

        JLabel lblTitulo = new JLabel("Terminal de Consumo Diário - APP QRCODE");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(44, 62, 80));
        painelAppQRCode.add(lblTitulo, "wrap");

        JLabel lblDesc = new JLabel("Simule o aplicativo móvel. Selecione o imunizante ou use a leitura direta do QR Code.");
        lblDesc.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDesc.setForeground(Color.GRAY);
        painelAppQRCode.add(lblDesc, "wrap");

        // Card Branco Centralizado - Configurado para as 4 linhas de campos ordenados
        JPanel cardLeitura = new JPanel(new MigLayout("wrap 2, insets 25, fillx", "[160px!]15[grow]", "[]15[]15[]15[]"));
        cardLeitura.setBackground(Color.WHITE);
        cardLeitura.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true));

        // 1. Campo de Seleção por Nome
        JLabel lblNomeApp = new JLabel("Buscar por Nome:");
        lblNomeApp.setFont(new Font("Tahoma", Font.BOLD, 14));
        cbQRCodeNome = new JComboBox<>(new String[]{"Selecione um imunizante..."});
        cbQRCodeNome.setFont(fonteTexto);

        // 2. Campo de Leitura do Lote
        JLabel lblLote = new JLabel("Leitura Lote / QR:"); 
        lblLote.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtQRCodeLote = new JTextField(); 
        txtQRCodeLote.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtQRCodeLote.setHorizontalAlignment(JTextField.CENTER);
        
        // 3. Campo de Quantidade Consumida
        JLabel lblQtd = new JLabel("Quantidade:"); 
        lblQtd.setFont(new Font("Tahoma", Font.BOLD, 14));
        spinQtdConsumo = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinQtdConsumo.setFont(new Font("Tahoma", Font.BOLD, 16));
        
        // 4. Campo do Local (Posto de Saúde Centralizado)
        JLabel lblLocal = new JLabel("Local / Posto:");
        lblLocal.setFont(new Font("Tahoma", Font.BOLD, 14));
        cbQRCodeLocal = new JComboBox<>(new String[]{"Selecione um posto..."});
        cbQRCodeLocal.setFont(fonteTexto);
        
        chkRegistrarPerda = new JCheckBox("Registrar como Perda / Descarte Sanitário");
        chkRegistrarPerda.setFont(new Font("Tahoma", Font.BOLD, 12));
        chkRegistrarPerda.setForeground(new Color(192, 57, 43)); 
        chkRegistrarPerda.setOpaque(false);

        JLabel lblMotivo = new JLabel("Motivo do Descarte:");
        lblMotivo.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        String[] motivos = {
            "Não há perda (Consumo Normal)",
            "Quebra física / Avaria de frasco",
            "Vencimento de Validade",
            "Exposição à Temperatura Inadequada (Cadeia de Frio)",
            "Fim do Prazo de Estabilidade pós-abertura",
            "Falha de Aspiração / Volume Insuficiente",
            "Descarte Técnico Acidental"
        };
        
        cbMotivoPerda = new JComboBox<>(motivos);
        cbMotivoPerda.setFont(fonteTexto);
        cbMotivoPerda.setEnabled(false); 
        // =====================================================================

        // Injeção organizada e sequencial de todos os elementos no Grid
        cardLeitura.add(lblNomeApp); cardLeitura.add(cbQRCodeNome, "growx, h 35!");
        cardLeitura.add(lblLote);    cardLeitura.add(txtQRCodeLote, "growx, h 35!");
        cardLeitura.add(lblQtd);     cardLeitura.add(spinQtdConsumo, "width 120px!, h 35!");
        cardLeitura.add(lblLocal);   cardLeitura.add(cbQRCodeLocal, "growx, h 35!");
        
        cardLeitura.add(new JLabel("Tipo Operação:")); 
        cardLeitura.add(chkRegistrarPerda, "growx");
        
        cardLeitura.add(lblMotivo);   
        cardLeitura.add(cbMotivoPerda, "growx, h 35!");
        
        painelAppQRCode.add(cardLeitura, "growx, wrap");

        // Grande Botão Verde de Registro de Baixas
        btnRegistrarConsumo = new JButton("REGISTRAR CONSUMO MÓVEL");
        btnRegistrarConsumo.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnRegistrarConsumo.setBackground(new Color(39, 174, 96)); 
        btnRegistrarConsumo.setForeground(Color.WHITE); 
        btnRegistrarConsumo.setOpaque(true);
        btnRegistrarConsumo.setContentAreaFilled(true);
        btnRegistrarConsumo.setBorderPainted(false);
        btnRegistrarConsumo.setFocusPainted(false);
        btnRegistrarConsumo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelAppQRCode.add(btnRegistrarConsumo, "growx, h 45!, wrap, gapbottom 15");

        // Tabela Inferior de Histórico Exclusivo do Operador Logado
        String[] colunas = {"Data/Hora", "Imunizante", "Lote", "Qtd Frascos", "Local", "Operação"};
        DefaultTableModel modeloPessoal = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Trava a edição direta das células
            }
        };

        tabelaConsumoPessoal = new JTable(modeloPessoal);
        tabelaConsumoPessoal.setFont(fonteTexto);
        tabelaConsumoPessoal.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        tabelaConsumoPessoal.setRowHeight(28);
        
        JScrollPane scrollPessoal = new JScrollPane(tabelaConsumoPessoal);
        scrollPessoal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)), "Seus Registros de Hoje", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        
        // Adiciona o contêiner de rolagem da tabela ao painel geral
        painelAppQRCode.add(scrollPessoal, "grow, push"); 
        painelAppQRCode.revalidate();
        painelAppQRCode.repaint();
    }
    
    private void configurarPainelDashboard() {
        // CORREÇÃO LAYOUT: 1 linha de título + 1 linha fixa de KPIs + 1 linha grow para os 3 gráficos horizontais
        painelDashboard = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[][110px!]20[grow]20[150px!]"));
        painelDashboard.setBackground(new Color(244, 246, 249));

        // ---------------------------------------------------------------------
        // LINHA 1: TÍTULO GERAL
        // ---------------------------------------------------------------------
        JLabel lblTitulo = new JLabel("DASHBOARD GERENCIAL DE IMUNIZAÇÃO");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(44, 62, 80));
        painelDashboard.add(lblTitulo, "wrap, gapbottom 10");

        // ---------------------------------------------------------------------
        // LINHA 2: BLOCO DE CARTÕES KPIs (4 colunas perfeitamente distribuídas)
        // ---------------------------------------------------------------------
        JPanel pKpis = new JPanel(new MigLayout("fill, insets 0", "[grow][grow][grow][grow]", "[grow]"));
        pKpis.setOpaque(false);

        // Cartão A: Total de Frascos Consumidos (Azul)
        JPanel c1 = new JPanel(new MigLayout("wrap 1, center, insets 15"));
        c1.setBackground(Color.WHITE);
        c1.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(52, 152, 219)));
        JLabel t1 = new JLabel("TOTAL DE FRASCOS CONSUMIDOS"); t1.setFont(new Font("Tahoma", Font.BOLD, 11)); t1.setForeground(Color.GRAY);
        lblKpiFrascos = new JLabel("0"); lblKpiFrascos.setFont(new Font("Tahoma", Font.BOLD, 28)); lblKpiFrascos.setForeground(new Color(41, 128, 185));
        c1.add(t1, "center"); c1.add(lblKpiFrascos, "center");

        // Cartão B: Postos de Saúde Atendidos (Verde)
        JPanel c2 = new JPanel(new MigLayout("wrap 1, center, insets 15"));
        c2.setBackground(Color.WHITE);
        c2.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(46, 204, 113)));
        JLabel t2 = new JLabel("POSTOS DE SAÚDE ATENDIDOS"); t2.setFont(new Font("Tahoma", Font.BOLD, 11)); t2.setForeground(Color.GRAY);
        lblKpiPostos = new JLabel("0"); lblKpiPostos.setFont(new Font("Tahoma", Font.BOLD, 28)); lblKpiPostos.setForeground(new Color(39, 174, 96));
        c2.add(t2, "center"); c2.add(lblKpiPostos, "center");
        
        // Cartão C: Total de Frascos Descartados (Vinho/Alerta Sanitário)
        JPanel cDescartes = new JPanel(new MigLayout("wrap 1, center, insets 15"));
        cDescartes.setBackground(Color.WHITE);
        cDescartes.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(192, 57, 43)));
        JLabel tDescartes = new JLabel("TOTAL DE FRASCOS DESCARTADOS"); tDescartes.setFont(new Font("Tahoma", Font.BOLD, 11)); tDescartes.setForeground(Color.GRAY);
        lblKpiDescartes = new JLabel("0"); lblKpiDescartes.setFont(new Font("Tahoma", Font.BOLD, 28)); lblKpiDescartes.setForeground(new Color(192, 57, 43));
        cDescartes.add(tDescartes, "center"); cDescartes.add(lblKpiDescartes, "center");

        // Cartão D: Lotes em Alerta Mínimo (Vermelho)
        JPanel c3 = new JPanel(new MigLayout("wrap 1, center, insets 15"));
        c3.setBackground(Color.WHITE);
        c3.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(231, 76, 60)));
        JLabel t3 = new JLabel("LOTES EM ALERTA CRÍTICO"); t3.setFont(new Font("Tahoma", Font.BOLD, 11)); t3.setForeground(Color.GRAY);
        lblKpiAlertas = new JLabel("0"); lblKpiAlertas.setFont(new Font("Tahoma", Font.BOLD, 28)); lblKpiAlertas.setForeground(new Color(192, 41, 43));
        c3.add(t3, "center"); c3.add(lblKpiAlertas, "center");

        pKpis.add(c1, "grow"); 
        pKpis.add(c2, "grow"); 
        pKpis.add(cDescartes, "grow"); 
        pKpis.add(c3, "grow");
        painelDashboard.add(pKpis, "growx, wrap, gapbottom 10");
        
        // ---------------------------------------------------------------------
        // LINHA 3: BLOCO DE GRÁFICOS UNIFICADO (3 Colunas horizontais lado a lado)
        // ---------------------------------------------------------------------
        JPanel pGraficosUnificado = new JPanel(new MigLayout("fill, insets 0", "[33%::][33%::][33%::]", "[grow]"));
        pGraficosUnificado.setOpaque(false);

        // 1. Gráfico de Barras - Demanda por Posto
        JPanel pEsquerdo = new JPanel(new BorderLayout());
        pEsquerdo.setBackground(Color.WHITE);
        pEsquerdo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)), "Demanda de Consumo por Unidade de Saúde", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        graficoBarrasPosto = new PainelGraficoBarras();
        pEsquerdo.add(graficoBarrasPosto, BorderLayout.CENTER);

        // 2. Gráfico de Pizza A - Distribuição por Imunizante
        JPanel pCentro = new JPanel(new BorderLayout());
        pCentro.setBackground(Color.WHITE);
        pCentro.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)), "Distribuição Proporcional por Imunizante", TitledBorder.LEADING, TitledBorder.TOP, fonteTitulo));
        graficoPizzaVacina = new PainelGraficoPizza();
        pCentro.add(graficoPizzaVacina, BorderLayout.CENTER);

        // 3. Gráfico de Pizza B - Análise de Perdas Técnicas
        JPanel pDireito = new JPanel(new BorderLayout());
        pDireito.setBackground(Color.WHITE);
        TitledBorder bordaPerdas = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)), 
            "Análise Epidemiológica de Perdas", 
            TitledBorder.LEADING, 
            TitledBorder.TOP, 
            fonteTitulo
        );
        bordaPerdas.setTitleColor(new Color(192, 57, 43));
        pDireito.setBorder(bordaPerdas);
        
        graficoPizzaMotivoDescarte = new PainelGraficoPizza();
        pDireito.add(graficoPizzaMotivoDescarte, BorderLayout.CENTER);

        pGraficosUnificado.add(pEsquerdo, "grow");
        pGraficosUnificado.add(pCentro, "grow");
        pGraficosUnificado.add(pDireito, "grow");
        
        painelDashboard.add(pGraficosUnificado, "grow, wrap, gapbottom 10");
        
        // ---------------------------------------------------------------------
        // LINHA 4 (NOVA!): CARD HORIZONTAL DE ENGENHARIA PRESCRITIVA (LOGÍSTICA DE COMPRAS)
        // ---------------------------------------------------------------------
        JPanel pLinhaAcoes = new JPanel(new BorderLayout());
        pLinhaAcoes.setBackground(Color.WHITE);
        
        TitledBorder bordaAcoes = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)), 
            "Plano de Ação Inteligente - Sugestão Automática de Aquisição e Reposição", 
            TitledBorder.LEADING, 
            TitledBorder.TOP, 
            fonteTitulo
        );
        bordaAcoes.setTitleColor(new Color(230, 126, 34)); // Cor Laranja de Alerta Logístico
        pLinhaAcoes.setBorder(bordaAcoes);

        // Estruturação da tabela interna de sugestões
        String[] colunasAcoes = {"#", "Prioridade Logística", "Imunizante", "Estoque Atual (Frascos)", "Estoque Mínimo", "Diretriz Operacional Recomendada", "Status Operacional"};
        modelSugestoesCompra = new DefaultTableModel(colunasAcoes, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Bloqueia edição
        };
        
        tabelaSugestoesCompra = new JTable(modelSugestoesCompra);
        tabelaSugestoesCompra.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tabelaSugestoesCompra.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        tabelaSugestoesCompra.getTableHeader().setPreferredSize(new Dimension(0, 28));
        tabelaSugestoesCompra.setRowHeight(26);
        
     // Alinha o índice numeral e a prioridade no centro da célula
        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
     // =====================================================================
        // CORREÇÃO v11.7: RENDERIZADORES RECALIBRADOS COM SUPORTE A SELEÇÃO AZUL
        // =====================================================================
        
        // 1. Renderizador Inteligente para a Coluna do Índice (#)
        tabelaSugestoesCompra.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Tahoma", Font.PLAIN, 12));
                
                // Se a linha estiver selecionada, força a cor azul padrão do sistema
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        
        // 2. Renderizador de Cores Críticas para a Coluna de Prioridade Logística
        tabelaSugestoesCompra.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    setFont(new Font("Tahoma", Font.BOLD, 12));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    
                    // Se estiver selecionado, preserva o azul comercial de foco
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    } else {
                        // Se não estiver selecionado, aplica as cores de alerta sanitário
                        if ("CRÍTICO".equals(status)) {
                            c.setBackground(new Color(255, 205, 205)); // Vermelho Suave
                            c.setForeground(new Color(192, 57, 43));
                        } else if ("URGENTE".equals(status)) {
                            c.setBackground(new Color(254, 237, 222)); // Laranja Suave
                            c.setForeground(new Color(211, 84, 0));
                        } else {
                            c.setBackground(new Color(255, 243, 205)); // Amarelo Suave
                            c.setForeground(new Color(156, 124, 18));
                        }
                    }
                }
                return c;
            }
        });
        // =====================================================================

        // Configuração proporcional das larguras das colunas
        tabelaSugestoesCompra.getColumnModel().getColumn(0).setPreferredWidth(40);   // Índice #
        tabelaSugestoesCompra.getColumnModel().getColumn(1).setPreferredWidth(130);  // Prioridade
        tabelaSugestoesCompra.getColumnModel().getColumn(2).setPreferredWidth(180);  // Nome
        tabelaSugestoesCompra.getColumnModel().getColumn(3).setPreferredWidth(130);  // Qtd
        tabelaSugestoesCompra.getColumnModel().getColumn(4).setPreferredWidth(110);  // Min
        tabelaSugestoesCompra.getColumnModel().getColumn(5).setPreferredWidth(350);  // Recomendação
        tabelaSugestoesCompra.getColumnModel().getColumn(6).setPreferredWidth(160);  // Status Operacional

        pLinhaAcoes.add(new JScrollPane(tabelaSugestoesCompra), BorderLayout.CENTER);
        painelDashboard.add(pLinhaAcoes, "grow");
    }

    private JTextField adicionarLinhaFormulario(JPanel painel, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(fonteTexto);
        JTextField txt = new JTextField();
        txt.setFont(fonteTexto);
        painel.add(lbl);
        painel.add(txt, "growx, h 32!");
        return txt;
    }
    
    private void configurarPainelSolicitacoes() {
        painelSolicitacoes = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[][grow]"));
        painelSolicitacoes.setBackground(Color.WHITE);

        JLabel lblTituloTela = new JLabel("HISTÓRICO DE SOLICITAÇÕES DE REPOSIÇÃO ENVIADAS");
        lblTituloTela.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTituloTela.setForeground(new Color(44, 62, 80));
        painelSolicitacoes.add(lblTituloTela, "wrap, gapbottom 15");

        String[] colunas = {"ID", "Data/Hora Solicitação", "Responsável", "Nível Acesso", "Imunizante Solicitado", "Qtd Solicitada (Fr.)", "Status"};
        modelViewSolicitacoes = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        tabelaViewSolicitacoes = new JTable(modelViewSolicitacoes);
        tabelaViewSolicitacoes.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tabelaViewSolicitacoes.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tabelaViewSolicitacoes.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tabelaViewSolicitacoes.setRowHeight(30);
        
        // Alinhamento centralizado para colunas numéricas e IDs
        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaViewSolicitacoes.getColumnModel().getColumn(0).setCellRenderer(renderCentro);
        tabelaViewSolicitacoes.getColumnModel().getColumn(1).setCellRenderer(renderCentro);
        tabelaViewSolicitacoes.getColumnModel().getColumn(5).setCellRenderer(renderCentro);
        tabelaViewSolicitacoes.getColumnModel().getColumn(6).setCellRenderer(renderCentro);

        JScrollPane scroll = new JScrollPane(tabelaViewSolicitacoes);
        painelSolicitacoes.add(scroll, "grow");
    }

    private void configurarPainelEstoque() {
        painelEstoque = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        painelEstoque.setBackground(Color.WHITE);

        JLabel lblT = new JLabel("INVENTÁRIO DE IMUNIZANTES");
        lblT.setFont(new Font("Tahoma", Font.BOLD, 18));
        painelEstoque.add(lblT, "center, wrap, gapbottom 15");
        
        JPanel pControles = new JPanel(new MigLayout("insets 0, fillx", "[][180px!]push[]", "[]"));
        pControles.setOpaque(false);

        JLabel lblFiltro = new JLabel("Status Sanitário:");
        lblFiltro.setFont(new Font("Tahoma", Font.BOLD, 13));
        
        cbFiltroStatusEstoque = new JComboBox<>(new String[]{"Todos", "Crítico (Vermelho)", "Atenção (Amarelo)", "Normal (Branco)", "Vencidos", "Descartes"});
        cbFiltroStatusEstoque.setFont(fonteTexto);
        
        btnImprimirEstoque = new JButton("Imprimir Lista");
        btnImprimirEstoque.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnImprimirEstoque.setBackground(new Color(52, 73, 94));
        btnImprimirEstoque.setForeground(Color.WHITE);
        btnImprimirEstoque.setFocusPainted(false);
        btnImprimirEstoque.setBorderPainted(false);
        btnImprimirEstoque.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pControles.add(lblFiltro);
        pControles.add(cbFiltroStatusEstoque, "h 30!");
        pControles.add(btnImprimirEstoque, "h 30!, width 130!");
        
        painelEstoque.add(pControles, "growx, wrap, gapbottom 10");

        modelTabela = new DefaultTableModel(new String[]{"Vacina", "Lote", "Qtd", "Descartes (Fr.)", "Mínimo", "Validade", "Status", "Colaborador", "Ações"}, 0);
        tabelaEstoque = new JTable(modelTabela);
        tabelaEstoque.setFont(fonteTexto);
        tabelaEstoque.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tabelaEstoque.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tabelaEstoque.setRowHeight(35);
        
        tabelaEstoque.setDefaultEditor(Object.class, null);
        painelEstoque.add(new JScrollPane(tabelaEstoque), "grow");
    }
    
    private void configurarPainelRelatorio() {
        painelRelatorio = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][][grow]"));
        painelRelatorio.setBackground(Color.WHITE);
	
        JLabel lblTitulo = new JLabel("RELATÓRIO HISTÓRICO DE CONSUMO DIÁRIO");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(44, 62, 80));
        painelRelatorio.add(lblTitulo, "split 2, growx");
	
        btnAtualizarRelatorio = new JButton(" Limpar Filtros / Atualizar");
        btnAtualizarRelatorio.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnAtualizarRelatorio.setBackground(new Color(52, 152, 219));
        btnAtualizarRelatorio.setForeground(Color.WHITE);
        btnAtualizarRelatorio.setFocusPainted(false);
        btnAtualizarRelatorio.setBorderPainted(false);
        btnAtualizarRelatorio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            ImageIcon iconUpdate = new ImageIcon(getClass().getResource("/br/edu/ufcspa/estoque/images/update.png"));
            Image imgRedim = iconUpdate.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            btnAtualizarRelatorio.setIcon(new ImageIcon(imgRedim));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem update: " + e.getMessage());
        }
        painelRelatorio.add(btnAtualizarRelatorio, "right, h 32!, wrap, gapbottom 15");
	
        JPanel pFiltros = new JPanel(new MigLayout("insets 10, fillx", "[][120px!][][grow][][grow][][grow][]", "[]"));
        pFiltros.setBackground(new Color(245, 246, 248));
        pFiltros.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
	
        JLabel lblFData = new JLabel("Data:"); 
        lblFData.setFont(new Font("Tahoma", Font.BOLD, 12));
	
        txtFiltroData = new com.toedter.calendar.JDateChooser();
        txtFiltroData.setDateFormatString("dd/MM/yyyy");
	
        JLabel lblFOperador = new JLabel("Operador:"); 
        lblFOperador.setFont(new Font("Tahoma", Font.BOLD, 12));
        cbFiltroOperador = new JComboBox<>(new String[]{"Todos"});
	
        JLabel lblFImunizante = new JLabel("Imunizante:"); 
        lblFImunizante.setFont(new Font("Tahoma", Font.BOLD, 12));
        cbFiltroImunizante = new JComboBox<>(new String[]{"Todos"});
	
        JLabel lblFLocal = new JLabel("Local:"); 
        lblFLocal.setFont(new Font("Tahoma", Font.BOLD, 12));
        cbFiltroLocal = new JComboBox<>(new String[]{"Todos"});
	
        btnFiltrarRelatorio = new JButton("Filtrar");
        btnFiltrarRelatorio.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnFiltrarRelatorio.setBackground(new Color(39, 174, 96)); 
        btnFiltrarRelatorio.setForeground(Color.WHITE);
        btnFiltrarRelatorio.setFocusPainted(false);
        btnFiltrarRelatorio.setBorderPainted(false);
	
        pFiltros.add(lblFData);       pFiltros.add(txtFiltroData, "width 120px!, h 28!");
        pFiltros.add(lblFOperador);   pFiltros.add(cbFiltroOperador, "growx, h 28!");
        pFiltros.add(lblFImunizante); pFiltros.add(cbFiltroImunizante, "growx, h 28!");
        pFiltros.add(lblFLocal);      pFiltros.add(cbFiltroLocal, "growx, h 28!");
        pFiltros.add(btnFiltrarRelatorio, "h 28!, width 90!");
	
        painelRelatorio.add(pFiltros, "growx, wrap, gapbottom 15");
	
        String[] colunasRelatorio = {"Data/Hora", "Responsável/Operador", "Imunizante", "Lote", "Qtd Frascos", "Local"};
        DefaultTableModel modelRelatorio = new DefaultTableModel(colunasRelatorio, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaRelatorioGeral = new JTable(modelRelatorio);
        tabelaRelatorioGeral.setFont(fonteTexto);
        tabelaRelatorioGeral.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tabelaRelatorioGeral.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tabelaRelatorioGeral.setRowHeight(32);
	
        painelRelatorio.add(new JScrollPane(tabelaRelatorioGeral), "grow");
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    public void inicializarRodape(String nomeColaborador, String nivelAcesso) {
        JPanel painelRodape = new JPanel(new MigLayout("fillx, insets 6 15 6 15", "[grow][right]"));
        painelRodape.setBackground(new Color(245, 246, 248));
        painelRodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        lblStatusUsuario = new JLabel();
        lblStatusUsuario.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblStatusUsuario.setForeground(new Color(127, 140, 141));
        painelRodape.add(lblStatusUsuario, "right");
        this.add(painelRodape, BorderLayout.SOUTH);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        Timer timer = new Timer(15000, e -> {
            lblStatusUsuario.setText("Sessão ativa: " + nomeColaborador + " | " + nivelAcesso + " | " + LocalDateTime.now().format(formatador));
        });
        lblStatusUsuario.setText("Sessão ativa: " + nomeColaborador + " | " + nivelAcesso + " | " + LocalDateTime.now().format(formatador));
        timer.start();
        revalidate(); repaint();
    }

    // --- Getters e Setters Completos ---
    public JTextField getTxtNomeComercial() { return txtNomeComercial; }
    public JTextField getTxtAntigeno() { return txtAntigeno; }
    public JTextField getTxtFabricante() { return txtFabricante; }
    public JTextField getTxtLote() { return txtLote; }
    public com.toedter.calendar.JDateChooser getTxtValidade() { return txtValidade; }
    public JTextField getTxtPaisOrigem() { return txtPaisOrigem; }
    public JTextField getTxtAnvisa() { return txtAnvisa; }
    public JTextField getTxtFornecedor() { return txtFornecedor; }
    public JTextField getTxtQuantidade() { return txtQuantidade; }
    public JTextField getTxtTotalDoses() { return txtTotalDoses; }
    public JTextField getTxtEstoqueMinimo() { return txtEstoqueMinimo; }
    public JComboBox<String> getCbPlataforma() { return cbPlataforma; }
    public JComboBox<String> getCbApresentacao() { return cbApresentacao; }
    public JComboBox<String> getCbTemperatura() { return cbTemperatura; }
    public JComboBox<String> getCbEstabilidade() { return cbEstabilidade; }
    public JComboBox<String> getCbStatusLiberacao() { return cbStatusLiberacao; }
    public JComboBox<String> getCbBuscaRapida() { return cbBuscaRapida; }
    public JTextField getTxtFiltroDigitado() { return txtFiltroDigitado; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnLimpar() { return btnLimpar; }
    public JButton getBtnMenuCadastro() { return btnMenuCadastro; }
    public JButton getBtnMenuPesquisa() { return btnMenuPesquisa; }
    public JButton getBtnMenuEdicao() { return btnMenuEdicao; }
    public JButton getBtnMenuEstoque() { return btnMenuEstoque; }
    public JButton getBtnMenuDashboard() { return btnMenuDashboard; }
    public JButton getBtnMenuRelatorio() { return btnMenuRelatorio; }
    public JButton getBtnMenuAppQR() { return btnMenuAppQR; }
    public JPanel getPainelFormulario() { return painelFormulario; }
    public JPanel getPainelEstoque() { return painelEstoque; }
    public JTable getTabelaEstoque() { return tabelaEstoque; }
    public JButton getBtnLogout() { return btnLogout; }
    public JPanel getPainelCentro() { return painelCentro; }
    public JTextField getTxtQRCodeLote() { return txtQRCodeLote; }
    public JSpinner getSpinQtdConsumo() { return spinQtdConsumo; }
    public JButton getBtnRegistrarConsumo() { return btnRegistrarConsumo; }
    public JPanel getPainelAppQRCode() { return painelAppQRCode; }
    public JComboBox<String> getCbQRCodeNome() { return cbQRCodeNome; }
    public JTable getTabelaConsumoPessoal() { return this.tabelaConsumoPessoal; }
    public JComboBox<String> getCbQRCodeLocal() { return this.cbQRCodeLocal; }
    public JTable getTabelaRelatorioGeral() { return this.tabelaRelatorioGeral; }
    public JComboBox<String> getCbFiltroOperador() { return cbFiltroOperador; }
    public JComboBox<String> getCbFiltroImunizante() { return cbFiltroImunizante; }
    public JComboBox<String> getCbFiltroLocal() { return cbFiltroLocal; }
    public JButton getBtnFiltrarRelatorio() { return btnFiltrarRelatorio; }
    public com.toedter.calendar.JDateChooser getTxtFiltroData() { return this.txtFiltroData; }
    public javax.swing.JButton getBtnAtualizarRelatorio() { return this.btnAtualizarRelatorio; }
    
    public JPanel getPainelDashboard() { return painelDashboard; }
    public JLabel getLblKpiFrascos() { return lblKpiFrascos; }
    public JLabel getLblKpiPostos() { return lblKpiPostos; }
    public JLabel getLblKpiAlertas() { return lblKpiAlertas; }
    public PainelGraficoBarras getGraficoBarrasPosto() { return graficoBarrasPosto; }
    public PainelGraficoPizza getGraficoPizzaVacina() { return graficoPizzaVacina; }
    
    public JComboBox<String> getCbFiltroStatusEstoque() { return cbFiltroStatusEstoque; }
    public JButton getBtnImprimirEstoque() { return btnImprimirEstoque; }
    
    public javax.swing.JCheckBox getChkRegistrarPerda() { return this.chkRegistrarPerda; }
    public javax.swing.JComboBox<String> getCbMotivoPerda() { return this.cbMotivoPerda; }
    
    public JLabel getLblKpiDescartes() { return lblKpiDescartes; }
    public PainelGraficoPizza getGraficoPizzaMotivoDescarte() { return graficoPizzaMotivoDescarte; }
    public JTable getTabelaSugestoesCompra() { return tabelaSugestoesCompra; }
    public DefaultTableModel getModelSugestoesCompra() { return modelSugestoesCompra; }
    
    public javax.swing.border.TitledBorder getBordaAcoesDashboard() {
        if (painelDashboard != null) {
            // Varre todos os componentes inseridos no Dashboard procurando quem tem a tabela de sugestões
            for (Component comp : painelDashboard.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel p = (JPanel) comp;
                    if (p.getBorder() instanceof TitledBorder) {
                        TitledBorder border = (TitledBorder) p.getBorder();
                        // Identifica a borda correta pelo texto base dela
                        if (border.getTitle().contains("Plano de Ação") || border.getTitle().contains("Sugestão Automa")) {
                            return border;
                        }
                    }
                }
            }
        }
        return null;
    }
    public JButton getBtnMenuSolicitacoes() { return btnMenuSolicitacoes; }
    public JPanel getPainelSolicitacoes() { return painelSolicitacoes; }
    public JTable getTabelaViewSolicitacoes() { return tabelaViewSolicitacoes; }
    public DefaultTableModel getModelViewSolicitacoes() { return modelViewSolicitacoes; }
    
    public javax.swing.JComboBox<String> getCbVinculoSolicitacao() { return cbVinculoSolicitacao; }
    

    // --- Classes Auxiliares de Renderização de Linhas ---
    @SuppressWarnings("serial")
	public class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(ImageIcon edit, ImageIcon delete) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
            setOpaque(true);
            add(new JLabel(edit)); add(new JLabel(delete));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            if (isSelected) {
                setBackground(new Color(125, 197, 220)); // Mesma cor cinza-escuro/azul do menu
            } else {
                // Se não estiver selecionada, mantém o fundo original da linha (que pode ser vermelho, amarelo ou branco)
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    @SuppressWarnings("serial")
	public class ButtonEditor extends DefaultCellEditor {
        private JPanel panel; private JTable table; private VacinaController controller;
        public ButtonEditor(JCheckBox checkBox, ImageIcon edit, ImageIcon delete, VacinaView view) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            panel.add(new JLabel(edit)); panel.add(new JLabel(delete));
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    int colWidth = panel.getWidth() / 2;
                    int row = table.getSelectedRow();
                    fireEditingStopped();
                    if (controller != null && row != -1) {
                        if (e.getX() < colWidth) controller.editarVacinaPelaTabela(row);
                        else controller.excluirVacinaPelaTabela(row);
                    }
                }
            });
        }
        public void setController(VacinaController controller) { this.controller = controller; }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table; panel.setBackground(table.getSelectionBackground()); return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }
    
 // =========================================================================
    // 1. COMPONENTE GRÁFICO DE BARRAS (RESTAURADO E HOMOLOGADO)
    // =========================================================================
    public static class PainelGraficoBarras extends JPanel {
        private java.util.Map<String, Integer> dados = new java.util.LinkedHashMap<>();
        
        private static final Color[][] PALETA_CORES = {
            {new Color(52, 152, 219), new Color(41, 128, 185)},  // Azul
            {new Color(155, 89, 182), new Color(142, 68, 173)},  // Roxo
            {new Color(26, 188, 156), new Color(22, 160, 133)},  // Turquesa
            {new Color(241, 196, 15), new Color(243, 156, 18)},  // Amarelo/Laranja
            {new Color(231, 76, 60),  new Color(192, 41, 43)}    // Vermelho
        };

        public void setDados(java.util.Map<String, Integer> dadosOriginal) {
            if (dadosOriginal == null || dadosOriginal.isEmpty()) {
                this.dados = new java.util.LinkedHashMap<>();
                repaint();
                return;
            }

            java.util.Map<String, Integer> dadosLimitados = new java.util.LinkedHashMap<>();
            int contador = 0;
            for (java.util.Map.Entry<String, Integer> entry : dadosOriginal.entrySet()) {
                if (contador < 5) dadosLimitados.put(entry.getKey(), entry.getValue());
                else break;
                contador++;
            }
            this.dados = dadosLimitados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados.isEmpty()) {
                g.setFont(new Font("Tahoma", Font.PLAIN, 14));
                g.drawString("Nenhum dado de consumo registrado.", 20, 30);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();

            int maxValor = 0;
            for (int v : dados.values()) {
                if (v > maxValor) maxValor = v;
            }
            if (maxValor == 0) maxValor = 1;

            int margemEsquerda = 40;
            int margemDireita = 20;
            int margemTopo = 30;
            int margemBase = 40;

            int largGrafico = largura - margemEsquerda - margemDireita;
            int altGrafico = altura - margemTopo - margemBase;

            int qtdBarras = dados.size();
            int largEspaco = largGrafico / qtdBarras;
            int largBarra = (int) (largEspaco * 0.6);

            int x = margemEsquerda + (largEspaco - largBarra) / 2;
            int index = 0;

            for (java.util.Map.Entry<String, Integer> entry : dados.entrySet()) {
                String local = entry.getKey();
                int valor = entry.getValue();

                int altBarra = (int) (((double) valor / maxValor) * altGrafico);
                int y = altura - margemBase - altBarra;

                Color[] cores = PALETA_CORES[index % PALETA_CORES.length];
                GradientPaint gradiente = new GradientPaint(x, y, cores[0], x, y + altBarra, cores[1]);
                g2d.setPaint(gradiente);
                g2d.fillRect(x, y, largBarra, altBarra);

                g2d.setColor(new Color(44, 62, 80));
                g2d.drawRect(x, y, largBarra, altBarra);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
                String strValor = String.valueOf(valor);
                int largTextoValor = g2d.getFontMetrics().stringWidth(strValor);
                g2d.drawString(strValor, x + (largBarra - largTextoValor) / 2, y - 5);

                g2d.setFont(new Font("Tahoma", Font.PLAIN, 10));
                String labelExibida = local.length() > 12 ? local.substring(0, 10) + ".." : local;
                int largLabel = g2d.getFontMetrics().stringWidth(labelExibida);
                g2d.drawString(labelExibida, x + (largBarra - largLabel) / 2, altura - margemBase + 15);

                x += largEspaco;
                index++;
            }
        }
    }

    // =========================================================================
    // 2. COMPONENTE GRÁFICO DE PIZZA (COM LEGENDAS INFERIORES E TAMANHO MAXIMIZADO)
    // =========================================================================
    public static class PainelGraficoPizza extends JPanel {
        private java.util.Map<String, Integer> dados = new java.util.LinkedHashMap<>();
        
        private static final Color[] CORES_SETORES = {
            new Color(46, 204, 113),  // Verde
            new Color(230, 126, 34), // Laranja
            new Color(155, 89, 182), // Roxo
            new Color(241, 196, 15),  // Amarelo
            new Color(52, 152, 219),  // Azul Claro
            new Color(149, 165, 166)  // Cinza ("Outros")
        };

        public void setDados(java.util.Map<String, Integer> dadosOriginal) {
            if (dadosOriginal == null || dadosOriginal.isEmpty()) {
                this.dados = new java.util.LinkedHashMap<>();
                repaint();
                return;
            }

            java.util.Map<String, Integer> dadosLimitados = new java.util.LinkedHashMap<>();
            int contador = 0;
            int somaOutros = 0;

            for (java.util.Map.Entry<String, Integer> entry : dadosOriginal.entrySet()) {
                if (contador < 5) {
                    dadosLimitados.put(entry.getKey(), entry.getValue());
                } else {
                    somaOutros += entry.getValue();
                }
                contador++;
            }

            if (somaOutros > 0) {
                dadosLimitados.put("Outros Imunizantes", somaOutros);
            }

            this.dados = dadosLimitados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados.isEmpty()) {
                g.setFont(new Font("Tahoma", Font.PLAIN, 14));
                g.drawString("Nenhum dado de consumo registrado.", 20, 30);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int totalGeral = 0;
            for (int v : dados.values()) totalGeral += v;
            if (totalGeral == 0) totalGeral = 1;

            int largura = getWidth();
            int altura = getHeight();
            
            // Diâmetro maximizado graças às legendas inferiores!
            int tamanhoPizza = 280; 
            
            // Centraliza horizontalmente no card de 33%
            int xPizza = (largura / 2) - (tamanhoPizza / 2); 
            int yPizza = 15; 

            int xLegendaInicial = 20;
            int yLegendaInicial = yPizza + tamanhoPizza + 20;
            
            int xLegenda = xLegendaInicial;
            int yLegenda = yLegendaInicial;
            int larguraDisponivel = largura - 30;

            int anguloInicial = 0;
            int i = 0;

            int raio = tamanhoPizza / 2;
            int centroX = xPizza + raio;
            int centroY = yPizza + raio;

            for (java.util.Map.Entry<String, Integer> entry : dados.entrySet()) {
                String vacina = entry.getKey();
                int valor = entry.getValue();

                int anguloFatia = (int) Math.round((double) valor / totalGeral * 360);
                Color corAtual = CORES_SETORES[i % CORES_SETORES.length];

                if (vacina.contains("Outros")) {
                    corAtual = CORES_SETORES[5];
                }

                // 1. Desenha a fatia da Pizza
                g2d.setColor(corAtual);
                g2d.fillArc(xPizza, yPizza, tamanhoPizza, tamanhoPizza, anguloInicial, anguloFatia);
                
                g2d.setColor(Color.WHITE);
                g2d.drawArc(xPizza, yPizza, tamanhoPizza, tamanhoPizza, anguloInicial, anguloFatia);

                double porcentagem = ((double) valor / totalGeral) * 100;

                // 2. Renderiza as porcentagens internas
                if (anguloFatia > 15) {
                    double anguloMedio = anguloInicial + (anguloFatia / 2.0);
                    double radianos = Math.toRadians(anguloMedio);

                    int distanciaCentro = (int) (raio * 0.6);
                    int textoX = (int) (centroX + distanciaCentro * Math.cos(radianos));
                    int textoY = (int) (centroY - distanciaCentro * Math.sin(radianos));

                    String strPorcentagem = String.format("%.1f%%", porcentagem);

                    g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
                    g2d.setColor(Color.WHITE);

                    FontMetrics fm = g2d.getFontMetrics();
                    int largTexto = fm.stringWidth(strPorcentagem);
                    int altTexto = fm.getAscent();

                    g2d.drawString(strPorcentagem, textoX - (largTexto / 2), textoY + (altTexto / 2));
                }

                // 3. Renderiza as Legendas inferiores dispostas lado a lado com quebra automática
                g2d.setColor(corAtual);
                g2d.fillRect(xLegenda, yLegenda, 10, 10);
                g2d.setColor(new Color(44, 62, 80));
                g2d.drawRect(xLegenda, yLegenda, 10, 10);

                g2d.setFont(new Font("Tahoma", Font.PLAIN, 10));
                String textoLegenda = String.format("%s (%d fr. - %.1f%%)", vacina, valor, porcentagem);
                g2d.drawString(textoLegenda, xLegenda + 15, yLegenda + 9);
                
                yLegenda += 16; 
                
                anguloInicial += anguloFatia;
                i++;
            }
        }
    }
    
    // =========================================================================
    // 3. SINALIZADOR VISUAL DE ESTOQUE ATUALIZADO
    // =========================================================================
    public static class SinalizadorEstoqueRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(new Color(125, 197, 220)); 
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Tahoma", Font.BOLD, 14));
                return c;
            }
            
            if (Boolean.TRUE.equals(table.getClientProperty("imprimindo"))) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
                c.setFont(new Font("Tahoma", Font.PLAIN, 12));
                return c;
            }

            try {
                int qtdFrascos = Integer.parseInt(table.getValueAt(row, 2).toString());
                int estoqueMinimo = Integer.parseInt(table.getValueAt(row, 4).toString());
                String stringValidade = table.getValueAt(row, 5).toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dataValidade = sdf.parse(stringValidade);
                LocalDate validade = dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate hoje = LocalDate.now();
                LocalDate trintaDiasDepois = hoje.plusDays(30);

                if (validade.isBefore(hoje)) {
                    c.setBackground(new Color(232, 218, 239)); 
                    c.setForeground(Color.BLACK); 
                    c.setFont(new Font("Tahoma", Font.BOLD, 14));
                } 
                else if (qtdFrascos <= estoqueMinimo) {
                    c.setBackground(new Color(255, 205, 205)); 
                    c.setForeground(Color.BLACK); 
                    c.setFont(new Font("Tahoma", Font.BOLD, 14));
                } 
                else if (validade.isBefore(trintaDiasDepois) || qtdFrascos <= (estoqueMinimo * 2)) {
                    c.setBackground(new Color(255, 243, 205)); 
                    c.setForeground(Color.BLACK); 
                    c.setFont(new Font("Tahoma", Font.BOLD, 14));
                } 
                else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    c.setFont(new Font("Tahoma", Font.PLAIN, 14));
                }
            } catch (Exception ex) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
    
 // =====================================================================
    // INTERFACE v12.7: RENDERIZADOR CUSTOMIZADO COM ÍCONE DE ATENÇÃO
    // =====================================================================
    public static class ComboAlertaRenderer extends DefaultListCellRenderer {
        private ImageIcon iconeAlerta;

        public ComboAlertaRenderer() {
            try {
                // Carrega o ícone de atenção e redimensiona para 16x16 pixels para caber na linha
                java.net.URL url = getClass().getResource("/br/edu/ufcspa/estoque/images/IconAtencion.png");
                if (url != null) {
                    ImageIcon original = new ImageIcon(url);
                    java.awt.Image img = original.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
                    this.iconeAlerta = new ImageIcon(img);
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar IconAtencion.png: " + e.getMessage());
            }
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Deixa o Swing desenhar o comportamento padrão de seleção e texto
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value != null) {
                String texto = value.toString();
                
                // Se a linha contiver o marcador de vencido
                if (texto.contains("⚠️ [VENCIDO]")) {
                    // Removemos a tag de texto feia para dar lugar ao ícone limpo
                    String textoLimpo = texto.replace("⚠️ [VENCIDO]", "").trim();
                    setText(textoLimpo);
                    
                    // Injeta graficamente o triângulo vermelho à esquerda do texto!
                    setIcon(iconeAlerta);
                    
                    // Coloca o texto em itálico ou vermelho sutil se quiser dar mais destaque
                    setForeground(new Color(192, 57, 43)); 
                } else {
                    // Itens normais não ganham ícone e usam a cor preta padrão
                    setIcon(null);
                    if (!isSelected) {
                        setForeground(Color.BLACK);
                    }
                }
            }
            return this;
        }
    }
    
}