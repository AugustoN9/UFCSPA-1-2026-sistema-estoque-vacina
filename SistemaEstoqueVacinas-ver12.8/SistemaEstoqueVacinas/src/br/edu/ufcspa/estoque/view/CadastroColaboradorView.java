package br.edu.ufcspa.estoque.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

/**
 * Interface gráfica da Tela de Cadastro de Colaboradores.
 * Design assimétrico moderno: Identidade à esquerda e Formulário com campo e-mail à direita.
 */
public class CadastroColaboradorView extends JFrame {

    private JTextField txtNome;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JComboBox<String> cbCargo;
    private JComboBox<String> cbGenero;
    private JComboBox<String> cbNivelAcesso;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public CadastroColaboradorView() {
        setTitle("VaccineManager - Cadastro de Colaborador");
        try {
            Image iconeSistema = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/edu/ufcspa/estoque/images/icon_vaccine.png"));
            setIconImage(iconeSistema);
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da janela: " + e.getMessage());
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não o sistema todo
        setSize(900, 550); // Ligeiramente maior para acomodar confortavelmente todos os campos
        setLocationRelativeTo(null);
        setResizable(false);

        // Layout de duas colunas com divisão perfeita (50% / 50%)
        setLayout(new MigLayout("fill, insets 0", "[grow, fill][grow, fill]", "[grow, fill]"));

        configurarPainelEsquerdo(); // Painel Azul (Identidade Visual)
        configurarPainelDireito();  // Painel Branco (Formulário Expandido)
    }

    /**
     * Painel Esquerdo: Identidade visual da UFCSPA / VaccineManager
     */
    private void configurarPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new MigLayout("fill, insets 0, wrap 1", "[center]", "[center]"));
        painelEsquerdo.setBackground(new Color(0, 168, 255)); // Azul padrão do Login

        try {
            // Reaproveita o mesmo logo de 300x300px que você redimensionou
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/br/edu/ufcspa/estoque/images/logo_vaccine.png"));
            JLabel lblLogo = new JLabel(logoIcon);
            painelEsquerdo.add(lblLogo);
        } catch (Exception e) {
            JLabel lblFallback = new JLabel("VaccineManager");
            lblFallback.setFont(new Font("Tahoma", Font.BOLD, 32));
            lblFallback.setForeground(Color.WHITE);
            painelEsquerdo.add(lblFallback);
        }
        // --- ADIÇÃO: Texto institucional idêntico para manter o padrão ---
        JLabel lblRodapeInstitucional = new JLabel("Developed by @AugustoN9 - 2026");
        lblRodapeInstitucional.setFont(new Font("Tahoma", Font.ITALIC, 12));
        lblRodapeInstitucional.setForeground(Color.WHITE);
        
        // Mantém o alinhamento no canto inferior direito interno do painel azul
        painelEsquerdo.add(lblRodapeInstitucional, "center, gapY 40");

        add(painelEsquerdo, "width 50%");
    }

    /**
     * Painel Direito: Formulário com os dados do novo colaborador, incluindo o e-mail.
     */
    private void configurarPainelDireito() {
        JPanel painelDireito = new JPanel(new MigLayout("fillx, insets 35", "[grow, fill]", "[]20[]5[]15[]5[]15[]5[]15[]5[]25[]"));
        painelDireito.setBackground(Color.WHITE);

        // Título da Tela
        JLabel lblTitulo = new JLabel("Criar Nova Conta");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));

        // Estilo comum para as Labels
        Font fonteLabel = new Font("Tahoma", Font.BOLD, 11);
        Color corLabel = new Color(127, 140, 141);

        // Campo Nome Completo
        JLabel lblNome = new JLabel("Nome Completo");
        lblNome.setFont(fonteLabel); lblNome.setForeground(corLabel);
        txtNome = criarTextFieldEstilizado();

        // Linha Dupla: Usuário e Género (MigLayout divide o espaço interno)
        JLabel lblUser = new JLabel("Username / Utilizador");
        lblUser.setFont(fonteLabel); lblUser.setForeground(corLabel);
        txtUsuario = criarTextFieldEstilizado();

        JLabel lblGenero = new JLabel("Género");
        lblGenero.setFont(fonteLabel); lblGenero.setForeground(corLabel);
        cbGenero = new JComboBox<>(new String[]{"Masculino", "Feminino", "Não-binário", "Outro","Prefiro não informar"});
        estilizarCombo(cbGenero);

        // Campo E-mail (O novo campo identificado na imagem!)
        JLabel lblEmail = new JLabel("E-mail Institucional");
        lblEmail.setFont(fonteLabel); lblEmail.setForeground(corLabel);
        txtEmail = criarTextFieldEstilizado();

        // Linha Dupla: Senha e Cargo
        JLabel lblPass = new JLabel("Password / Senha");
        lblPass.setFont(fonteLabel); lblPass.setForeground(corLabel);
        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblCargo = new JLabel("Cargo / Função");
        lblCargo.setFont(fonteLabel); lblCargo.setForeground(corLabel);
        cbCargo = new JComboBox<>(new String[]{"Técnico de Laboratório", "Pesquisador", "Enfermeiro", "Gestor", "Administrador"});
        estilizarCombo(cbCargo);

        // Nível de Acesso (Invisível por padrão ou definido como OPERADOR, a menos que o admin mude)
        JLabel lblNivel = new JLabel("Nível de Permissão no Sistema");
        lblNivel.setFont(fonteLabel); lblNivel.setForeground(corLabel);
        cbNivelAcesso = new JComboBox<>(new String[]{"OPERADOR", "ADMIN", "APP_MOBILE"});
        estilizarCombo(cbNivelAcesso);

        // Painel para os Botões (Alinhados lado a lado no rodapé)
        JPanel painelBotoes = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        painelBotoes.setBackground(Color.WHITE);

        btnSalvar = new JButton("Cadastrar Conta");
        btnSalvar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnSalvar.setBackground(new Color(39, 174, 96)); // Verde Sucesso
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.setMargin(new Insets(10, 0, 10, 0));

        btnCancelar = new JButton("Voltar ao Login");
        btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(189, 195, 199)); // Cinza Neutro
        btnCancelar.setForeground(new Color(44, 62, 80));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setMargin(new Insets(10, 0, 10, 0));

        painelBotoes.add(btnCancelar, "growx");
        painelBotoes.add(btnSalvar, "growx");

        // Adicionando os componentes estruturados no Grid vertical do MigLayout
        painelDireito.add(lblTitulo, "wrap");
        
        painelDireito.add(lblNome, "wrap");
        painelDireito.add(txtNome, "growx, wrap");

        // Grid dividido: Utilizador toma 65% da largura, Género toma 35%
        painelDireito.add(lblUser, "split 2, width 65%");
        painelDireito.add(lblGenero, "width 35%, wrap");
        painelDireito.add(txtUsuario, "split 2, width 65%");
        painelDireito.add(cbGenero, "width 35%, wrap");

        painelDireito.add(lblEmail, "wrap");
        painelDireito.add(txtEmail, "growx, wrap");

        // Grid dividido: Senha toma 50%, Cargo toma 50%
        painelDireito.add(lblPass, "split 2, width 50%");
        painelDireito.add(lblCargo, "width 50%, wrap");
        painelDireito.add(txtSenha, "split 2, width 50%");
        painelDireito.add(cbCargo, "width 50%, wrap");

        painelDireito.add(lblNivel, "wrap");
        painelDireito.add(cbNivelAcesso, "growx, wrap");

        painelDireito.add(painelBotoes, "growx, gaptop 10");

        add(painelDireito, "width 50%");
    }

    // Métodos Auxiliares para evitar repetição de código de estilização (Clean Code)
    private JTextField criarTextFieldEstilizado() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }

    // --- GETTERS COMPLETOS PARA O CONTROLLER ACESSAR OS DADOS ---
    public JTextField getTxtNome() { return txtNome; }
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JPasswordField getTxtSenha() { return txtSenha; }
    public JComboBox<String> getCbCargo() { return cbCargo; }
    public JComboBox<String> getCbGenero() { return cbGenero; }
    public JComboBox<String> getCbNivelAcesso() { return cbNivelAcesso; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}