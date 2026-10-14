package org.example.administracao.aplicacao;

import org.example.administracao.dominio.Sessao;
import org.example.administracao.dominio.Usuario;
import org.example.comum.Perfil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

// Casos de uso de usuarios: cadastro, busca, ativacao, login e relatorio.
// Por enquanto os usuarios ficam so em memoria (a persistencia entra na etapa 3).
public class UsuarioControlador {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final List<Usuario> usuarios = new ArrayList<>();

    // RF-ADM-1
    public Usuario cadastrar(String nome, String email, String senha, Set<Perfil> perfis) {
        validarEmail(email, null);
        validarSenha(senha);
        Usuario usuario = new Usuario(nome, email, senha, perfis);
        usuarios.add(usuario);
        return usuario;
    }

    public void editar(Usuario usuario, String nome, String email, Set<Perfil> perfis) {
        validarEmail(email, usuario);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setPerfis(perfis);
    }

    public void excluir(Usuario usuario) {
        usuarios.remove(usuario);
    }

    public List<Usuario> listar() {
        return new ArrayList<>(usuarios);
    }

    // RF-ADM-3: criterios nulos sao ignorados, os demais se combinam
    public List<Usuario> buscar(String nome, Perfil perfil, Boolean ativo) {
        List<Usuario> encontrados = new ArrayList<>();
        for (Usuario u : usuarios) {
            boolean nomeOk = nome == null || u.getNome().toLowerCase().contains(nome.toLowerCase());
            boolean perfilOk = perfil == null || u.possuiPerfil(perfil);
            boolean ativoOk = ativo == null || u.isAtivo() == ativo;
            if (nomeOk && perfilOk && ativoOk) {
                encontrados.add(u);
            }
        }
        return encontrados;
    }

    // RF-ADM-5
    public void ativar(Usuario usuario) {
        usuario.ativar();
    }

    public void desativar(Usuario usuario) {
        usuario.desativar();
    }

    // RNF-12 e RN-ADM-9: login com e-mail e senha; usuario inativo nao entra
    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.verificarSenha(senha)) {
                if (!u.isAtivo()) {
                    throw new IllegalStateException("Usuário inativo não pode acessar o sistema.");
                }
                Sessao.getInstancia().iniciar(u);
                return u;
            }
        }
        throw new IllegalArgumentException("E-mail ou senha inválidos.");
    }

    // RF-ADM-7: quantidade de usuarios por perfil
    public Map<Perfil, Integer> contarPorPerfil() {
        Map<Perfil, Integer> contagem = new EnumMap<>(Perfil.class);
        for (Perfil perfil : Perfil.values()) {
            contagem.put(perfil, buscar(null, perfil, null).size());
        }
        return contagem;
    }

    public int contarAtivos() {
        return buscar(null, null, true).size();
    }

    public int contarInativos() {
        return buscar(null, null, false).size();
    }

    // RN-ADM-7: formato valido e e-mail unico (o proprio usuario pode manter o seu)
    private void validarEmail(String email, Usuario usuarioAtual) {
        if (email == null || !FORMATO_EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("E-mail em formato inválido.");
        }
        for (Usuario u : usuarios) {
            if (u != usuarioAtual && u.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Já existe um usuário com o e-mail " + email + ".");
            }
        }
    }

    // RN-ADM-8: pelo menos 8 caracteres, com letras e numeros
    private void validarSenha(String senha) {
        boolean valida = senha != null && senha.length() >= 8
                && senha.chars().anyMatch(Character::isLetter)
                && senha.chars().anyMatch(Character::isDigit);
        if (!valida) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, com letras e números.");
        }
    }
}
