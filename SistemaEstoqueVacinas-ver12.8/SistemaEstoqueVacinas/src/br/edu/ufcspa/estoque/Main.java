package br.edu.ufcspa.estoque;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import br.edu.ufcspa.estoque.controller.LoginController;
import br.edu.ufcspa.estoque.view.LoginView;

/**
 * Ponto de entrada do Sistema de Gestão de Imunizantes.
 * Atualizado para iniciar pelo fluxo de autenticação.
 */
public class Main {
    public static void main(String[] args) {
        
        // Define o visual do sistema para o padrão do Windows/Sistema Operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Instancia a View de Login
                LoginView loginView = new LoginView();
                
                // 2. Instancia o Controller de Login (que gerenciará a transição para a VacinaView)
                new LoginController(loginView);
                
                // 3. Torna a janela de login visível
                loginView.setVisible(true);
                
                System.out.println(">>> Sistema de Imunizantes: Aguardando autenticação...");
                
            } catch (Exception e) {
                System.err.println("Erro crítico ao iniciar o sistema: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}