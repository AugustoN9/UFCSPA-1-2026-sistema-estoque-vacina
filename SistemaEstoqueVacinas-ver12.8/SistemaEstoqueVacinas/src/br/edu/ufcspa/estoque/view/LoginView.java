package br.edu.ufcspa.estoque.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

/**
 * Interface gráfica da Tela de Login.
 * Implementa um design moderno de duas colunas (Formulário à esquerda, Imagem à direita).
 */
public class LoginView extends JFrame {

    // Componentes que precisam ser acessados pelo Controller
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblEsqueceuSenha;
    private JLabel lblInscreverSe;

    public LoginView() {
        setTitle("Sistema de Gestão de Imunizantes v12.8 - Login");
        try {
            Image iconeSistema = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/br/edu/ufcspa/estoque/images/icon_vaccine.png"));
            setIconImage(iconeSistema);
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da janela: " + e.getMessage());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamanho retangular moderno ideal para este layout
        setSize(850, 500); 
        setLocationRelativeTo(null); 
        setResizable(false);

        // Grid principal: Duas colunas com divisões iguais (50% / 50%) e sem insets externos
        setLayout(new MigLayout("fill, insets 0", "[grow, fill][grow, fill]", "[grow, fill]"));

        configurarPainelEsquerdo(); // Painel do Formulário (Fundo Branco)
        configurarPainelDireito();  // Painel da Identidade (Fundo Azul com Logo)
    }

    /**
     * Monta o painel esquerdo com o título e os campos de input.
     */
    private void configurarPainelEsquerdo() {
        // Painel Branco para o Formulário de Login (com MigLayout interno)
        JPanel painelEsquerdo = new JPanel(new MigLayout("fillx, insets 45", "[grow, fill]", "[]35[]5[]20[]5[]15[]25[]"));
        painelEsquerdo.setBackground(Color.WHITE);

        // Título Principal
        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblLogin.setForeground(new Color(44, 62, 80)); // Cor grafite escura elegante

        // Campo Usuário
        JLabel lblUser = new JLabel("Username / Utilizador");
        lblUser.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblUser.setForeground(new Color(127, 140, 141));

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Tahoma", Font.PLAIN, 14));
        // Truque de UI: Borda fina cinza clara com espaçamento interno generoso (padding)
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Campo Senha
        JLabel lblPass = new JLabel("Password / Senha");
        lblPass.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblPass.setForeground(new Color(127, 140, 141));

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Linha Auxiliar (Remember Me e Links textuais)
        JCheckBox chkRememberMe = new JCheckBox("Remember me");
        chkRememberMe.setBackground(Color.WHITE);
        chkRememberMe.setFont(new Font("Tahoma", Font.PLAIN, 12));
        chkRememberMe.setForeground(new Color(127, 140, 141));

        lblEsqueceuSenha = new JLabel("Esqueceu a Senha?");
        lblEsqueceuSenha.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblEsqueceuSenha.setForeground(new Color(0, 168, 255));
        lblEsqueceuSenha.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Mãozinha de link

        JPanel painelAuxiliar = new JPanel(new MigLayout("fillx, insets 0", "[grow][right]", "[]"));
        painelAuxiliar.setBackground(Color.WHITE);
        painelAuxiliar.add(chkRememberMe);
        painelAuxiliar.add(lblEsqueceuSenha);

        // Botão Entrar Plano (Modern Flat)
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(0, 151, 230)); // Azul vibrante da marca
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setMargin(new Insets(12, 0, 12, 0)); // Aumenta a altura interna do botão

        // Rodapé de Inscrição
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        painelRodape.setBackground(Color.WHITE);
        JLabel lblNaoTemConta = new JLabel("Não Tem Uma Conta?");
        lblNaoTemConta.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblNaoTemConta.setForeground(new Color(127, 140, 141));
        
        lblInscreverSe = new JLabel("Inscrever-se");
        lblInscreverSe.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblInscreverSe.setForeground(new Color(0, 168, 255));
        lblInscreverSe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelRodape.add(lblNaoTemConta);
        painelRodape.add(lblInscreverSe);

        // Adicionando elementos à esquerda, respeitando o grid vertical
        painelEsquerdo.add(lblLogin, "wrap");
        painelEsquerdo.add(lblUser, "wrap");
        painelEsquerdo.add(txtUsuario, "growx, wrap");
        painelEsquerdo.add(lblPass, "wrap");
        painelEsquerdo.add(txtSenha, "growx, wrap");
        painelEsquerdo.add(painelAuxiliar, "growx, wrap");
        painelEsquerdo.add(btnEntrar, "growx, wrap");
        painelEsquerdo.add(painelRodape, "growx, gaptop 15"); // 'lgap' dá espaçamento superior

        add(painelEsquerdo, "width 50%");
    }

    /**
     * Monta o painel direito com o fundo azul e a logo centralizada.
     */
    private void configurarPainelDireito() {
        // Painel Azul de Destaque da Marca (Lado Direito)
        JPanel painelDireito = new JPanel(new MigLayout("fill, insets 0, wrap 1", "[center]", "[center]"));
        painelDireito.setBackground(new Color(0, 168, 255)); // Mesmo azul vibrante da logo

        try {
            // Carrega a imagem da pasta 'images' (relativo à raiz do pacote view)
            // Certifique-se de que o arquivo 'logo_vaccine.png' está na pasta images
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/br/edu/ufcspa/estoque/images/logo_vaccine.png"));
            JLabel lblLogo = new JLabel(logoIcon);
            painelDireito.add(lblLogo);
        } catch (Exception e) {
            // Fallback elegante caso a imagem não seja carregada
            JLabel lblFallback = new JLabel("VaccineManager");
            lblFallback.setFont(new Font("Tahoma", Font.BOLD, 32));
            lblFallback.setForeground(Color.WHITE);
            painelDireito.add(lblFallback);
        }
        // --- ADIÇÃO: Texto institucional alinhado à direita no rodapé azul ---
        JLabel lblRodapeDev = new JLabel("Developed by @AugustoN9 - 2026");
        lblRodapeDev.setFont(new Font("Tahoma", Font.ITALIC, 12));
        lblRodapeDev.setForeground(Color.WHITE);
        
        // A restrição "south, align right" cola o texto na base direita interna do painel azul,
        // adicionando um pequeno espaçamento (gap) de segurança nas bordas inferior e direita
        painelDireito.add(lblRodapeDev, "center, gaptop 50");

        add(painelDireito, "width 50%");
    }

    // --- GETTERS ATUALIZADOS PARA O LOGINCONTROLLER ---
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtSenha() { return txtSenha; }
    public JButton getBtnEntrar() { return btnEntrar; }
    // Getters para os novos links textuais
    public JLabel getLblEsqueceuSenha() { return lblEsqueceuSenha; }
    public JLabel getLblInscreverSe() { return lblInscreverSe; }
}