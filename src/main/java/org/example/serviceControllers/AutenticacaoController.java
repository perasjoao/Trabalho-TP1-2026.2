package org.example.serviceControllers;

import org.example.models.Usuario;
import org.example.exceptions.UsuarioInativoException;
import org.example.exceptions.CredenciaisInvalidasException;

public class AutenticacaoController {
    private UsuarioController usuarioController;
    private Usuario usuarioLogado;

    public AutenticacaoController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public Usuario autenticar(String email, String senha) throws CredenciaisInvalidasException, UsuarioInativoException {
        Usuario u = usuarioController.buscarPorEmail(email);

        if (u == null || !u.getSenha().equals(senha)) {
            throw new CredenciaisInvalidasException("E-mail ou senha incorretos.");
        }

        if (!u.isStatus()) {
            throw new UsuarioInativoException("Acesso negado. Este usuário está desativado.");
        }

        this.usuarioLogado = u;
        return u;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}