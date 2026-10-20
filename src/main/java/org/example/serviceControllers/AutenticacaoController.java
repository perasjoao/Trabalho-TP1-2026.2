package org.example.serviceControllers;

import org.example.models.Usuario;

public class AutenticacaoController {
    private UsuarioController usuarioController;
    private Usuario usuarioLogado;

    public AutenticacaoController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public Usuario autenticar(String email, String senha) {
        Usuario u = usuarioController.buscarPorEmail(email);
        if (u != null && u.getSenha().equals(senha) && u.isStatus()) {
            this.usuarioLogado = u;
            return u;
        }
        return null;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}