package org.example.serviceControllers;

import org.example.models.Perfil;
import org.example.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioController {
    private List<Usuario> usuarios;

    public UsuarioController() {
        this.usuarios = new ArrayList<>();
        this.usuarios.add(new Usuario("Administrador Geral", "admin@email.com", "admin123", Perfil.ADMINISTRADOR, true));
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        if (!validarEmail(usuario.getEmail()) || !validarSenha(usuario.getSenha())) {
            return false;
        }
        if (buscarPorEmail(usuario.getEmail()) != null) {
            return false;
        }
        return usuarios.add(usuario);
    }

    public boolean editarUsuario(String emailOriginal, Usuario novosDados) {
        Usuario usuario = buscarPorEmail(emailOriginal);
        if (usuario == null) {
            return false;
        }
        usuario.setNome(novosDados.getNome());
        usuario.setPerfil(novosDados.getPerfil());
        usuario.setStatus(novosDados.isStatus());
        if (novosDados.getSenha() != null && !novosDados.getSenha().trim().isEmpty() && validarSenha(novosDados.getSenha())) {
            usuario.setSenha(novosDados.getSenha());
        }
        return true;
    }

    public boolean excluirUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            return usuarios.remove(usuario);
        }
        return false;
    }

    public Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public boolean validarEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean validarSenha(String senha) {
        return senha != null && senha.length() >= 8;
    }
}