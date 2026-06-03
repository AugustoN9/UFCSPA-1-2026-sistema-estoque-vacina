package br.edu.ufcspa.estoque.controller;

import br.edu.ufcspa.estoque.dao.ColaboradorDAO;
import br.edu.ufcspa.estoque.model.Colaborador;
import br.edu.ufcspa.estoque.view.CadastroColaboradorView;
import br.edu.ufcspa.estoque.view.LoginView;
import br.edu.ufcspa.estoque.view.VacinaView;

import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Controller do Login - Adaptado para o design moderno de duas colunas.
 */
public class LoginController {

    private LoginView view;
    private ColaboradorDAO dao;

    public LoginController(LoginView view) {
        this.view = view;
        this.dao = new ColaboradorDAO();
        initListeners();
    }

    private void initListeners() {
        // Ação do botão Entrar
        view.getBtnEntrar().addActionListener(e -> autenticar());

        // Ação para o link "Esqueceu a Senha?"
        view.getLblEsqueceuSenha().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(view, 
                    "Para recuperar sua senha, entre em contato com o administrador do sistema ou TI da UFCSPA.", 
                    "Recuperação de Senha", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

     // Ação real para o link "Inscrever-se" -> Abre a tela de cadastro
        view.getLblInscreverSe().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Instancia a nova View de cadastro
                CadastroColaboradorView cadastroView = new CadastroColaboradorView();
                
                // Instancia o Controller passando a View (Injetando a dependência)
                new CadastroColaboradorController(cadastroView);
                
                // Exibe a tela de cadastro de forma independente
                cadastroView.setVisible(true);
            }
        });
    }

    private void autenticar() {
        String usuario = view.getTxtUsuario().getText();
        String senha = new String(view.getTxtSenha().getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor, preencha todos os campos.");
            return;
        }

        Colaborador colaboradorLogado = dao.validarLogin(usuario, senha);

        if (colaboradorLogado != null) {
            // Sucesso! Abrir a aplicação principal passando o colaborador logado
            abrirSistemaPrincipal(colaboradorLogado);
            view.dispose(); // Fecha a tela de login
        } else {
            JOptionPane.showMessageDialog(view, "Usuário ou senha inválidos.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirSistemaPrincipal(Colaborador colaborador) {
        // 1. Instancia a View usando o construtor correto (sem parâmetros)
        VacinaView mainView = new VacinaView();
        
        // 2. O VacinaController agora recebe o colaborador e se encarrega 
        // de chamar o inicializarRodape e aplicar os níveis de acesso
        new VacinaController(mainView, colaborador);
        
        // 3. Torna a tela visível
        mainView.setVisible(true);
    }
}