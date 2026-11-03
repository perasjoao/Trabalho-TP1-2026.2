package org.example.serviceControllers;

import org.example.models.Usuario;
import org.example.exceptions.EmailJaCadastradoException;
import org.example.exceptions.SenhaInvalidaException;
import org.example.utils.GerenciadorArquivo;

import java.util.List;

public class UsuarioController {
    private List<Usuario> usuarios;
    private GerenciadorArquivo<Usuario> gerenciador;
    private final String ARQUIVO_USUARIOS = "usuarios.dat";

    public UsuarioController() {
        this.gerenciador = new GerenciadorArquivo<>();
        this.usuarios = gerenciador.ler(ARQUIVO_USUARIOS);
    }

    public void cadastrarUsuario(Usuario usuario) throws EmailJaCadastradoException, SenhaInvalidaException {
        if (buscarPorEmail(usuario.getEmail()) != null) {
            throw new EmailJaCadastradoException("O e-mail " + usuario.getEmail() + " já está cadastrado.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().length() < 8) {
            throw new SenhaInvalidaException("A senha informada não atende ao requisito mínimo de 8 caracteres.");
        }

        usuarios.add(usuario);

        gerenciador.salvar(ARQUIVO_USUARIOS, usuarios);
    }

    public void editarUsuario(String emailOriginal, Usuario novosDados) throws SenhaInvalidaException {
        Usuario usuario = buscarPorEmail(emailOriginal);
        if (usuario != null) {
            usuario.setNome(novosDados.getNome());
            usuario.setPerfil(novosDados.getPerfil());
            usuario.setStatus(novosDados.isStatus());

            if (novosDados.getSenha() != null && !novosDados.getSenha().trim().isEmpty()) {
                if (novosDados.getSenha().length() < 8) {
                    throw new SenhaInvalidaException("A nova senha deve ter no mínimo 8 caracteres.");
                }
                usuario.setSenha(novosDados.getSenha());
            }

            gerenciador.salvar(ARQUIVO_USUARIOS, usuarios);
        }
    }

    public boolean excluirUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            boolean removido = usuarios.remove(usuario);
            if (removido) {
                gerenciador.salvar(ARQUIVO_USUARIOS, usuarios);
            }
            return removido;
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
}