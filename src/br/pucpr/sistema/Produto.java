package br.pucpr.sistema;

public class Produto {
    private String nome;
    private int estoque;
    private double preco;

    public Produto(String nome, double preco, int estoque) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome invalido.");
        }
        if (estoque <= 0) {
            throw new IllegalArgumentException("Estoque inicial deve ser positivo.");
        }

        if (preco <= 0) {
            throw new IllegalArgumentException("Preco invalido.");
        }
        this.nome = nome;
        this.estoque = estoque;
        this.preco = preco;
    }


    @Override
    public String toString() {
        return "%s,%d,%.2f".formatted(nome,estoque,preco);
    }

    public String getNome() {
        return nome;
    }

    public int getEstoque() {
        return estoque;
    }

    public double getPreco() {
        return preco;
    }
}

