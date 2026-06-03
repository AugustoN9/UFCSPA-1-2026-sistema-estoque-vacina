package br.edu.ufcspa.estoque.controller;

import br.edu.ufcspa.estoque.dao.ColaboradorDAO;
import br.edu.ufcspa.estoque.model.Colaborador;
import br.edu.ufcspa.estoque.view.CadastroColaboradorView;

import javax.swing.JOptionPane;

/**
 * Controller responsável por gerenciar o fluxo de cadastro de novos colaboradores.
 */
public class CadastroColaboradorController {

    private CadastroColaboradorView view;
    private ColaboradorDAO dao;

    public CadastroColaboradorController(CadastroColaboradorView view) {
        this.view = view;
        this.dao = new ColaboradorDAO();
        initListeners();
    }

    /**
     * Inicializa os ouvintes dos botões Salvar e Cancelar.
     */
    private void initListeners() {
        // Ação do botão Cadastrar Conta
        view.getBtnSalvar().addActionListener(e -> salvarCadastro());

        // Ação do botão Voltar ao Login
        view.getBtnCancelar().addActionListener(e -> {
            view.dispose(); // Fecha a tela de cadastro e retorna à tela que a chamou
        });
    }

    /**
     * Captura, valida e envia os dados do formulário para o banco de dados.
     */
    private void salvarCadastro() {
        // 1. Captura os dados da View
        String nome = view.getTxtNome().getText().trim();
        String usuario = view.getTxtUsuario().getText().trim();
        String email = view.getTxtEmail().getText().trim();
        String senha = new String(view.getTxtSenha().getPassword()).trim();
        
        // Capturando os JComboBox da View
        String genero = (String) view.getCbGenero().getSelectedItem();
        String cargo = (String) view.getCbCargo().getSelectedItem();
        String nivelAcesso = (String) view.getCbNivelAcesso().getSelectedItem();

        // 2. Validação de Campos Obrigatórios
        if (nome.isEmpty() || usuario.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Todos os campos obrigatórios devem ser preenchidos.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Monta o Objeto Model com todos os dados incluindo Gênero
        Colaborador novoColaborador = new Colaborador();
        novoColaborador.setNome(nome);
        novoColaborador.setGenero(genero); // Setando o Gênero capturado
        novoColaborador.setCargo(cargo);   // Setando o Cargo padrão/selecionado
        novoColaborador.setUsuario(usuario);
        novoColaborador.setEmail(email);
        novoColaborador.setSenha(senha);
        novoColaborador.setNivelAcesso(nivelAcesso);

        // 4. Persistência através do DAO
        boolean sucesso = dao.salvarNovoColaborador(novoColaborador);

        if (sucesso) {
            JOptionPane.showMessageDialog(view, 
                "Colaborador cadastrado com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
            view.dispose(); 
        } else {
            JOptionPane.showMessageDialog(view, 
                "Erro ao salvar o colaborador. Verifique a conexão com o banco ou duplicidade de usuário.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}