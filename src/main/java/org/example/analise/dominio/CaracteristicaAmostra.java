package org.example.analise.dominio;

import org.example.coleta.dominio.Participante;

// Caracteristicas da amostra usadas para agrupar respostas.
// Cada constante sabe ler o proprio valor no participante (polimorfismo dentro do enum).
public enum CaracteristicaAmostra {
    REGIAO("Região") {
        @Override
        public String valorDe(Participante p) {
            return p.getRegiao();
        }
    },
    FAIXA_ETARIA("Faixa etária") {
        @Override
        public String valorDe(Participante p) {
            return p.getFaixaEtaria();
        }
    },
    ESCOLARIDADE("Escolaridade") {
        @Override
        public String valorDe(Participante p) {
            return p.getEscolaridade();
        }
    },
    GENERO("Gênero") {
        @Override
        public String valorDe(Participante p) {
            return p.getGenero();
        }
    },
    FAIXA_RENDA("Faixa de renda") {
        @Override
        public String valorDe(Participante p) {
            return p.getFaixaRenda();
        }
    };

    private final String descricao;

    CaracteristicaAmostra(String descricao) {
        this.descricao = descricao;
    }

    public abstract String valorDe(Participante participante);

    public String getDescricao() {
        return descricao;
    }
}
