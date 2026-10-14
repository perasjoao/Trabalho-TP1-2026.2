package org.example.analise.dominio;

// Padrao Factory: concentra em um so lugar a decisao de qual exportador criar.
// Para um formato novo (ex.: XLSX), basta uma classe nova e um caso aqui.
public final class ExportadorFactory {

    private ExportadorFactory() {
    }

    public static Exportador criar(FormatoExportacao formato) {
        return switch (formato) {
            case PDF -> new ExportadorPDF();
            case CSV -> new ExportadorCSV();
        };
    }
}
