package org.example.administracao.dominio;

// Usuario logado no momento. Padrao Singleton: existe uma unica sessao
// no sistema inteiro, acessada por Sessao.getInstancia().
public class Sessao {

    private static Sessao instancia;

    private Usuario usuarioLogado;

    // construtor privado: ninguem fora da classe consegue criar outra sessao
    private Sessao() {
    }

    public static Sessao getInstancia() {
        if (instancia == null) {
            instancia = new Sessao();
        }
        return instancia;
    }

    public void iniciar(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void encerrar() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean estaLogado() {
        return usuarioLogado != null;
    }
}
