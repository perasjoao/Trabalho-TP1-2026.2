package org.example.administracao.dominio;

import org.example.comum.Perfil;
import org.example.comum.Pessoa;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Usuario do sistema. Herda o nome de Pessoa e pode ter um ou mais perfis (RN-ADM-3).
public class Usuario extends Pessoa {

    private String email;
    private String senha;
    private final Set<Perfil> perfis = EnumSet.noneOf(Perfil.class);
    private boolean ativo = true;

    public Usuario(String nome, String email, String senha, Set<Perfil> perfis) {
        super(nome);
        this.email = email;
        this.senha = senha;
        setPerfis(perfis);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // a senha nao tem getter: so pode ser conferida
    public boolean verificarSenha(String senhaInformada) {
        return senha.equals(senhaInformada);
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Perfil> getPerfis() {
        return Collections.unmodifiableSet(perfis);
    }

    public void setPerfis(Set<Perfil> novosPerfis) {
        if (novosPerfis == null || novosPerfis.isEmpty()) {
            throw new IllegalArgumentException("O usuário precisa de pelo menos um perfil.");
        }
        perfis.clear();
        perfis.addAll(novosPerfis);
    }

    public boolean possuiPerfil(Perfil perfil) {
        return perfis.contains(perfil);
    }

    // RN-ADM-1 e RN-ADM-9: inativo nao acessa nada; ativo acessa o que algum perfil permite
    public boolean podeAcessar(Funcionalidade funcionalidade) {
        if (!ativo) {
            return false;
        }
        for (Perfil perfil : perfis) {
            if (funcionalidade.permitidaPara(perfil)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void ativar() {
        ativo = true;
    }

    public void desativar() {
        ativo = false;
    }

    @Override
    public String getDescricao() {
        return getNome() + " <" + email + "> " + perfis;
    }
}
