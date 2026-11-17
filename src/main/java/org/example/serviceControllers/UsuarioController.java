package org.example.serviceControllers;

import org.example.exceptions.EmailJaCadastradoException;
import org.example.exceptions.SenhaInvalidaException;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.utils.GerenciadorArquivo;

import java.util.ArrayList;
import java.util.List;

public class UsuarioController {
    private List<Usuario> usuarios;
    private GerenciadorArquivo<Usuario> gerenciador;
    private final String ARQUIVO_USUARIOS = "usuarios.dat";

    public UsuarioController() {
        this.gerenciador = new GerenciadorArquivo<>();
        this.usuarios = gerenciador.ler(ARQUIVO_USUARIOS);

        if (this.usuarios.isEmpty()) {
            this.usuarios.add(new Usuario("Administrador Geral", "admin@email.com", "admin123", Perfil.ADMINISTRADOR, true));
            gerenciador.salvar(ARQUIVO_USUARIOS, this.usuarios);
        }
    }

    public void cadastrarUsuario(Usuario usuario) throws EmailJaCadastradoException, SenhaInvalidaException {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (!validarEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("O formato do e-mail informado é inválido.");
        }
        if (buscarPorEmail(usuario.getEmail()) != null) {
            throw new EmailJaCadastradoException("O e-mail " + usuario.getEmail() + " já está cadastrado.");
        }
        if (!validarSenha(usuario.getSenha())) {
            throw new SenhaInvalidaException("A senha deve ter no mínimo 8 caracteres, contendo letras e números.");
        }
        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Selecione um perfil de acesso para o usuário.");
        }

        usuarios.add(usuario);
        gerenciador.salvar(ARQUIVO_USUARIOS, usuarios);
    }

    public void editarUsuario(String emailOriginal, Usuario novosDados) throws EmailJaCadastradoException, SenhaInvalidaException {
        Usuario usuario = buscarPorEmail(emailOriginal);
        if (usuario != null) {
            if (novosDados.getNome() == null || novosDados.getNome().trim().isEmpty()) {
                throw new IllegalArgumentException("O nome do usuário é obrigatório.");
            }

            if (novosDados.getEmail() != null && !novosDados.getEmail().equalsIgnoreCase(emailOriginal)) {
                if (!validarEmail(novosDados.getEmail())) {
                    throw new IllegalArgumentException("O formato do novo e-mail é inválido.");
                }
                if (buscarPorEmail(novosDados.getEmail()) != null) {
                    throw new EmailJaCadastradoException("O e-mail " + novosDados.getEmail() + " já está em uso.");
                }
                usuario.setEmail(novosDados.getEmail());
            }

            usuario.setNome(novosDados.getNome());
            usuario.setPerfil(novosDados.getPerfil());
            usuario.setStatus(novosDados.isStatus());

            if (novosDados.getSenha() != null && !novosDados.getSenha().trim().isEmpty()) {
                if (!validarSenha(novosDados.getSenha())) {
                    throw new SenhaInvalidaException("A nova senha deve ter no mínimo 8 caracteres, contendo letras e números.");
                }
                usuario.setSenha(novosDados.getSenha());
            }

            gerenciador.salvar(ARQUIVO_USUARIOS, usuarios);
        }
    }

    public void alterarStatus(String email, boolean status) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            usuario.setStatus(status);
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
        if (email == null) {
            return null;
        }
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> filtrarUsuarios(String nome, Perfil perfil, Boolean status) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
            boolean correspondeNome = (nome == null || nome.trim().isEmpty())
                    || u.getNome().toLowerCase().contains(nome.trim().toLowerCase())
                    || u.getEmail().toLowerCase().contains(nome.trim().toLowerCase());
            boolean correspondePerfil = (perfil == null) || u.getPerfil() == perfil;
            boolean correspondeStatus = (status == null) || u.isStatus() == status;

            if (correspondeNome && correspondePerfil && correspondeStatus) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public int contarTotal() {
        return usuarios.size();
    }

    public int contarAtivos() {
        int total = 0;
        for (Usuario u : usuarios) {
            if (u.isStatus()) {
                total++;
            }
        }
        return total;
    }

    public int contarInativos() {
        return contarTotal() - contarAtivos();
    }

    public int contarPorPerfil(Perfil perfil) {
        int total = 0;
        for (Usuario u : usuarios) {
            if (u.getPerfil() == perfil) {
                total++;
            }
        }
        return total;
    }

    public boolean validarEmail(String email) {
        return email != null && email.trim().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }
        boolean temLetra = senha.matches(".*[a-zA-Z].*");
        boolean temNumero = senha.matches(".*\\d.*");
        return temLetra && temNumero;
    }
}