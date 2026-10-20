package org.example.models.candidatos;

public abstract class RegistroEleitoral {
    private int numero;

    protected RegistroEleitoral(int numero) {
        setNumero(numero);
    }

    public int getNumero() {
        return numero;
    }

    //antecipa a etapa 3, valida o objeto para nunca ficar invalido
    public void setNumero(int numero) {
        //o IllegalArgumentException vai ser trocado na entrega 3
        if (numero <= 0) throw new IllegalArgumentException("O número deve ser positivo.");
        this.numero = numero;
    }

    //cada subclasse decide o que significa estar ativo/como se apresenta o texto
    public abstract boolean isAtivo();
    public abstract String getDescricao();

    @Override
    public String toString() {
        return getDescricao();
    }
}
