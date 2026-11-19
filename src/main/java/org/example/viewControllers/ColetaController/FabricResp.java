package org.example.viewControllers.ColetaController;

import org.example.models.researchModels.Pergunta;

//escolhe qual campo montar de acordo com o tipo da pergunta
//se aparecer um tipo novo, e so criar outra subclasse de CampoResposta e incluir aqui
public final class FabricResp {

    private FabricResp() {
    }

    public static CampoResposta criar(Pergunta pergunta) {
        if ("Resposta Livre".equalsIgnoreCase(pergunta.getTipo())) {
            return new CampoTextoLivre(pergunta);
        }
        return new CampoEscolha(pergunta);
    }
}
