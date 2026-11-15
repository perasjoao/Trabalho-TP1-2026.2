package org.example.utils;

import org.example.exceptions.EmailJaCadastradoException;
import org.example.exceptions.SenhaInvalidaException;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.serviceControllers.AutenticacaoController;
import org.example.serviceControllers.UsuarioController;

// guarda os controllers de usuario/autenticacao compartilhados por todas as telas,
// para que o usuario logado fique acessivel em qualquer controller
public final class Sessao {

    public static final String EMAIL_ADMIN_PADRAO = "admin@sistema.com";
    public static final String SENHA_ADMIN_PADRAO = "admin123";

    private static final UsuarioController usuarioController = new UsuarioController();
    private static final AutenticacaoController autenticacaoController = new AutenticacaoController(usuarioController);

    static {
        // sem nenhum usuario cadastrado ninguem conseguiria entrar, entao cria um administrador padrao
        if (usuarioController.listarUsuarios().isEmpty()) {
            try {
                usuarioController.cadastrarUsuario(new Usuario("Administrador", EMAIL_ADMIN_PADRAO,
                        SENHA_ADMIN_PADRAO, Perfil.ADMINISTRADOR, true));
            } catch (EmailJaCadastradoException | SenhaInvalidaException e) {
                System.err.println("Não foi possível criar o administrador padrão: " + e.getMessage());
            }
        }
    }

    private Sessao() {
    }

    public static UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public static AutenticacaoController getAutenticacao() {
        return autenticacaoController;
    }

    public static Usuario getUsuarioLogado() {
        return autenticacaoController.getUsuarioLogado();
    }
}
