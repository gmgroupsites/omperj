package br.com.omperj.model;

/**
 * Created by renan on 05/02/18.
 */

public class Remedio {

    private String nome;
    private String quantidade;
    private String imagem;

    public Remedio() {}

    public Remedio(String nome, String quantidade, String imagem) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return "Remedio{" +
                "nome='" + nome + '\'' +
                ", quantidade='" + quantidade + '\'' +
                ", imagem='" + imagem + '\'' +
                '}';
    }
}
