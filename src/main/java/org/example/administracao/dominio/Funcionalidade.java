package org.example.administracao.dominio;

import org.example.comum.Perfil;

import java.util.EnumSet;
import java.util.Set;

// Funcionalidades do sistema e os perfis que podem usa-las (RN-ADM-2 e RN-ADM-5).
// O administrador tem acesso total, por isso nao precisa ser listado.
public enum Funcionalidade {
    GERENCIAR_USUARIOS(),
    CONFIGURAR_PARAMETROS(),
    GERENCIAR_PESQUISAS(Perfil.PESQUISADOR),
    REGISTRAR_COLETA(Perfil.PESQUISADOR),
    CONSULTAR_COLETA(Perfil.PESQUISADOR, Perfil.ANALISTA),
    PROCESSAR_ANALISE(Perfil.ANALISTA),
    GERAR_RELATORIOS(Perfil.ANALISTA),
    GERENCIAR_CANDIDATOS(Perfil.PESQUISADOR);

    private final Set<Perfil> perfisPermitidos = EnumSet.of(Perfil.ADMINISTRADOR);

    Funcionalidade(Perfil... perfis) {
        for (Perfil perfil : perfis) {
            perfisPermitidos.add(perfil);
        }
    }

    public boolean permitidaPara(Perfil perfil) {
        return perfisPermitidos.contains(perfil);
    }
}
