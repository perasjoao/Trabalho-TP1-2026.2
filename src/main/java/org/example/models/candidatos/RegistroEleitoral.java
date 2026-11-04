package org.example.models.candidatos;

import org.example.exceptions.DadoInvalidoException;

import java.io.Serializable;

//Serializable permite gravar o objeto em arquivo
//Candidatura e Partido herdam isso
public abstract class RegistroEleitoral implements Serializable {
    private static final long serialVersionUID = 1L;
    private int numero;

    protected RegistroEleitoral(int numero) throws DadoInvalidoException {
        setNumero(numero);
    }

    public int getNumero() {
        return numero;
    }

    //valida o objeto para nunca ficar invalido: numero precisa ser positivo
    public void setNumero(int numero) throws DadoInvalidoException {
        if (numero <= 0) throw new DadoInvalidoException("O número deve ser positivo.");
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
