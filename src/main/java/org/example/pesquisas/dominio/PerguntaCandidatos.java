package org.example.pesquisas.dominio;

import org.example.candidatos.dominio.Candidato;

// Pergunta cujas opcoes sao os candidatos da pesquisa
// (intencao de voto, rejeicao, segundo turno...).
public class PerguntaCandidatos extends Pergunta {

    public PerguntaCandidatos(String texto, boolean obrigatoria) {
        super(texto, obrigatoria);
    }

    public void adicionarCandidato(Candidato candidato) {
        adicionarOpcao(new OpcaoResposta(candidato.getNome(), candidato));
    }

    // sobrescrita: aqui so entram opcoes ligadas a um candidato
    @Override
    public void adicionarOpcao(OpcaoResposta opcao) {
        if (!opcao.isCandidato()) {
            throw new IllegalArgumentException("As opções desta pergunta devem ser candidatos.");
        }
        super.adicionarOpcao(opcao);
    }

    @Override
    public String getTipo() {
        return "Candidatos";
    }
}
