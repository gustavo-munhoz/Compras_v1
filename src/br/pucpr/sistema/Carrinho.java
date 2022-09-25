package br.pucpr.sistema;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Carrinho {
    private Map<String, List<Object>> produtos;
    private double precoTotal;

    public Carrinho() {
        this.produtos = new HashMap<>();
        this.precoTotal = 0;
    }

    /**
     * Adiciona um novo produto ao carrinho do usuário.
     * Recebe nome e quantidade de um produto a ser inserido, e calcula o preço adicional sobre o preço total
     * no carrinho atual.
     * Verifica se o produto existe no banco de dados, procura o preço e adiciona-o no HashMap produtos como
     * "{Nome, [Quantidade, Preço total]}"
     * @param nome Nome do produto a ser inserido.
     * @param qtd Quantidade a ser inserida.
     */
    public void adicionarProduto(String nome, int qtd) {
        File db = new File("src/br/pucpr/sistema/produtos.txt");
        Scanner in = null;

        try {
            in = new Scanner(db);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (in.hasNextLine()) {
            String line = in.nextLine();

            if (line.substring(0, line.indexOf(",")).equals(nome)) {
                double preco = Double.parseDouble(line.substring(line.indexOf(",") + 1, line.lastIndexOf(",")));
                List<Object> dados = new ArrayList<>();

                if (qtd > 0) {
                    if (produtos.containsKey(nome)) {
                        dados.add(qtd + (Integer) produtos.get(nome).get(0));
                        dados.add(preco * ((Integer) produtos.get(nome).get(0) + qtd));
                        precoTotal += preco * ((Integer) produtos.get(nome).get(0) + qtd);
                    } else {
                        dados.add(qtd);
                        dados.add(qtd * preco);
                        precoTotal += preco * qtd;
                    }

                    produtos.put(nome, dados);
                } else {
                    throw new IllegalArgumentException("Quantidade inválida.");
                }
            }
        }
    }

    /**
     * Converte os objetos do tipo Object do atributo produtos para Integer e Double,
     * mostrando assim a quantidade, nome e preço de cada produto contido no carrinho.
     * Mostra também o preço total.
     */
    public void exibirCarrinho() {
        System.out.println("\n---MEU CARRINHO---");
        for (String produto : produtos.keySet()) {
            System.out.printf("%d %s, $%.2f\n", (Integer) produtos.get(produto).get(0),
                    produto, (Double) produtos.get(produto).get(1));
        }
        System.out.printf("Preço total: $%.2f\n", precoTotal);
    }

    /**
     * Permite a edição do estoque de um produto, buscando e alterando-o no banco de dados.
     * @param nome Nome do produto a ser editado.
     * @param nova_qtd Quantidade a ser inserida no novo estoque.
     */
    public void editarProduto(String nome, int nova_qtd) {
        // há um bug na edição de produto, o carrinho reseta para 0.0

        File db = new File("src/br/pucpr/sistema/produtos.txt");
        try {
            Scanner sc = new Scanner(db);

            List<Object> novos_dados = new ArrayList<>();

            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                String str_preco = linha.substring(linha.indexOf(",") + 1,linha.lastIndexOf(","));
                if (nome.equals(linha.substring(0, linha.indexOf(",")))) {
                    precoTotal -= (Double) produtos.get(nome).get(1);

                    double preco = Double.parseDouble(str_preco);
                    novos_dados.add(nova_qtd);
                    novos_dados.add(preco * nova_qtd);
                    precoTotal += preco * nova_qtd;
                }
            }
            produtos.put(nome, novos_dados);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void removerProduto(String nome) {
        precoTotal -= (Double) produtos.get(nome).get(1);
        produtos.remove(nome);
    }


    public double getPrecoTotal() {
        return precoTotal;
    }

    public Map<String, List<Object>> getProdutos() {
        return produtos;
    }

    public String produtosToString() {
        String str = "";
        for (String produto : produtos.keySet()) {
            str = str.concat("," + produto);
        }
        return str;
    }

    @Override
    public String toString() {
        String str = "";
        for (String produto : produtos.keySet()) {
            str = str.concat("%d %s $%.2f;".formatted( (Integer) produtos.get(produto).get(0), produto,
                    (Double) produtos.get(produto).get(1)));
        }
        return str;
    }
}

