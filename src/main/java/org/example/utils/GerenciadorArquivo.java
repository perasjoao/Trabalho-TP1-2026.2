package org.example.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivo<T> {

    public void salvar(String nomeArquivo, List<T> lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Erro ao gravar arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> ler(String nomeArquivo) {
        File arquivo = new File(nomeArquivo);

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao ler arquivo " + nomeArquivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
