import br.pucpr.sistema.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Requisitos do programa:
 * Sistema de login com opção de cadastrar usuário e usuário administrador OK
 * Menu de compras com:
 * Busca de produtos; OK
 * Listagem de produtos; OK
 * Adicionar produto ao carrinho; OK
 * Exibir carrinho / finalizar compra e salvar no histórico do cliente. OK
 * Relatório de clientes (função disponível só para admin) com:
 * Listagem de clientes ordenados por gasto, número de compras, total comprado e valor médio de compra; OK
 * Total geral vendido. OK
 * Listagens paginadas, com opção de avançar e voltar OK
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int escolha_menu, escolha_compras, escolha_carrinho, escolha_editar, escolha_admin;
        boolean sair = false;
        Scanner in = new Scanner(System.in);

        System.out.println("Bem vindo ao supermercado TXT.\nPor favor, faça login abaixo:");
        Usuario user = fazerLogin();

        do {
            Thread.sleep(300);
            if (user.existeUsuario() && !user.getLogin().equals("admin")) {

                // Evita que o programa cadastre dois usuarios iguais com senhas diferentes
                while (!user.senhaCorreta()) {
                    System.out.println("Senha incorreta! Tente novamente:");
                    user = fazerLogin();
                }
                Carrinho carrinho = new Carrinho();

                mostrarMenuPrincipal();
                escolha_menu = Integer.parseInt(in.nextLine());

                if (escolha_menu == 1) {
                    // Leva ao menu de compras
                    do {
                        Thread.sleep(300);
                        mostrarMenuCompras();
                        escolha_compras = Integer.parseInt(in.nextLine());

                        if (escolha_compras == 1) {
                            // Buscar produto
                            System.out.print("Insira o nome do produto: ");
                            String busca = in.nextLine().toUpperCase();

                            Thread.sleep(300);
                            if (!buscarProduto(busca).isEmpty()) {
                                System.out.println("Resultados encontrados:\n");
                                for (Produto produto : buscarProduto(busca)) {
                                    System.out.printf("%s..........$%.2f\n\n", produto.getNome(), produto.getPreco());
                                }
                            } else {
                                System.out.println("Nenhum produto encontrado.");
                            }
                            standby();

                        } else if (escolha_compras == 2) {
                            // Listar produtos
                            System.out.println("Lista de produtos:\n");

                            Pagina pag = new Pagina();

                            for (Produto produto : listarProdutos()) {
                                pag.adicionarTexto("%s..........R$%.2f\n".formatted(produto.getNome(),
                                        produto.getPreco()));
                            }
                            pag.setLinha_inicial(0);
                            pag.setLinha_final(8);
                            Scanner sc = new Scanner(System.in);
                            int continuar;
                            int indice = 1;

                            do {

                                System.out.printf("--PÁGINA %d--\n", indice);
                                for (String l : pag.mostrarPagina()) {
                                    System.out.println(l);
                                }
                                System.out.println("[1] Para página anterior");
                                System.out.println("[2] Para próxima página");
                                System.out.println("[3] Para voltar ao menu");
                                System.out.print("Insira a opção desejada: ");
                                continuar = Integer.parseInt(in.nextLine());

                                if (continuar == 1) {
                                    if (pag.getLinha_inicial() > 0) {
                                        pag.voltarPagina();
                                        indice -= 1;
                                    }
                                    else {
                                        System.out.println("Ação inválida.");
                                        standby();
                                    }
                                }
                                else if (continuar == 2) {
                                    if (pag.getLinha_final() <= pag.getTexto().size()) {
                                        pag.avancarPagina();
                                        indice += 1;
                                    }
                                    else {
                                        System.out.println("Ação inválida.");
                                        standby();
                                    }
                                }
                                else if (continuar == 3) {
                                    System.out.println("Retornando...");
                                    Thread.sleep(300);
                                }
                                else {
                                    System.out.println("Insira uma opção válida!");
                                }
                            } while (continuar != 3);


                        } else if (escolha_compras == 3) {
                            // Adicionar ao carrinho
                            System.out.print("Insira o nome do produto: ");
                            String busca = in.nextLine().toUpperCase();
                            System.out.print("Insira a quantidade desejada: ");
                            int qtd = Integer.parseInt(in.nextLine());

                            carrinho.adicionarProduto(busca, qtd);
                            System.out.println("Produto adicionado com sucesso!\n");
                            standby();

                        } else if (escolha_compras == 4) {
                            // Exibir carrinho e opções de ação
                            carrinho.exibirCarrinho();
                            mostrarMenuCarrinho();
                            escolha_carrinho = Integer.parseInt(in.nextLine());

                            while (escolha_carrinho != 1 && escolha_carrinho != 2 && escolha_carrinho != 3) {
                                System.out.print("Opção inválida! Insira a opção: ");
                                escolha_carrinho = Integer.parseInt(in.nextLine());
                            }

                            if (escolha_carrinho == 1) {
                                // Continuar comprando
                                continue;

                            } else if (escolha_carrinho == 2) {
                                // Editar carrinho
                                System.out.println("Ações disponíveis:\n[1] Editar produto\n[2] Remover produto");
                                escolha_editar = Integer.parseInt(in.nextLine());

                                while (escolha_editar != 1 && escolha_editar != 2) {
                                    System.out.print("Opção inválida! Insira a opção: ");
                                    escolha_editar = Integer.parseInt(in.nextLine());
                                }

                                boolean sucesso = false;
                                if (escolha_editar == 1) {
                                    do {
                                        System.out.print("Qual produto deseja editar? Insira o nome: ");
                                        String edit = in.nextLine().toUpperCase();
                                        System.out.print("Insira a nova quantidade: ");
                                        int nova_qtd = Integer.parseInt(in.nextLine());
                                        if (carrinho.produtosToString().contains(edit)) {
                                            carrinho.editarProduto(edit, nova_qtd);
                                            sucesso = true;
                                            System.out.println("\nProduto editado com sucesso!");
                                        } else {
                                            System.out.println("Produto não encontrado.");
                                        }
                                    } while (!sucesso);

                                } else {
                                    do {
                                        System.out.print("Qual produto deseja remover? Insira o nome: ");
                                        String remove = in.nextLine().toUpperCase();
                                        if (carrinho.produtosToString().contains(remove)) {
                                            carrinho.removerProduto(remove);
                                            sucesso = true;
                                            System.out.println("\nProduto removido com sucesso!");
                                        } else {
                                            System.out.println("Produto não encontrado.");
                                        }
                                    } while (!sucesso);
                                }

                            } else {
                                try {
                                    finalizarCompra(user, carrinho);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Não há estoque suficiente.");
                                    standby();
                                }
                            }

                        } else if (escolha_compras == 5) {
                            System.out.println("Voltando para o menu");

                        } else {
                            System.out.println("Digite uma opção válida!");
                        }

                    } while (escolha_compras != 5);
                } else if (escolha_menu == 2) {
                    System.out.println("Usuário desconectado.\nPor favor, faça login abaixo:");
                    user = fazerLogin();
                    carrinho = new Carrinho();

                } else if (escolha_menu == 3) {
                    System.out.println("SUPERMERCADO TXT, V1.");
                    System.out.println("Criado por Gustavo Munhoz Corrêa.");
                    
                } else if (escolha_menu == 4) {
                    sair = true;
                    System.out.println("Finalizando...\nAté a próxima!");
                } else {
                    System.out.println("Digite uma opção válida!");
                }


            } else if (user.existeUsuario() && user.getLogin().equals("admin")) {
                while (!user.senhaCorreta()) {
                    System.out.println("Senha incorreta! Tente novamente:");
                    user = fazerLogin();
                }
                mostrarMenuAdmin();
                escolha_admin = Integer.parseInt(in.nextLine());

                if (escolha_admin == 1) {
                    String salvar;
                    System.out.println("Deseja salvar o relatório? [S/N]");
                    do {
                        salvar = in.nextLine().toUpperCase();
                        if (!salvar.equals("S") && !salvar.equals("N")) {
                            System.out.println("Insira uma opção válida!");
                        }
                    } while (!salvar.equals("S") && !salvar.equals("N"));

                    if (salvar.equals("S")) {
                        System.out.println("---RELATÓRIO---");
                        imprimirRelatorio(true);
                        standby();
                    } else {
                        System.out.println("---RELATÓRIO---");
                        imprimirRelatorio(false);
                        standby();
                    }
                } else if (escolha_admin == 2) {
                    String nome;
                    double preco;
                    int estoque;

                    System.out.println("---CADASTRAR PRODUTO---");
                    System.out.print("Digite o nome do novo produto: ");
                    nome = in.nextLine().toUpperCase();
                    System.out.print("Digite o preço do novo produto: ");
                    preco = Double.parseDouble(in.nextLine());
                    System.out.print("Digite o estoque inicial do novo produto: ");
                    estoque = Integer.parseInt(in.nextLine());

                    adminCadastrarProduto(nome, preco, estoque);

                } else if (escolha_admin == 3) {
                    String nome;
                    int qtd;

                    System.out.println("---ADICIONAR ESTOQUE---");
                    System.out.print("Digite o nome do produto: ");
                    nome = in.nextLine().toUpperCase();
                    System.out.print("Digite a quantidade a se adicionar no estoque produto: ");
                    qtd = Integer.parseInt(in.nextLine());

                    adminAdicionarEstoque(nome, qtd);
                } else {
                    sair = true;
                    System.out.println("Finalizando...");
                }

            } else {
                System.out.println("Usuário não cadastrado. Deseja se registrar? [S/N]");
                String register = in.nextLine();

                if (Objects.equals(register, "S") || Objects.equals(register, "s")) {
                    user.cadastrarUsuario();
                    System.out.println("Usuário cadastrado com sucesso.");
                    System.out.println("Por favor, faça login novamente.");
                    user = fazerLogin();

                } else if (Objects.equals(register, "N") || Objects.equals(register, "n")) {
                    System.out.println("Operação cancelada.");
                    sair = true;

                } else System.out.println("Digite uma opção válida.");
            }
        } while (!sair);
    }

    /**
     * Funçao para logar um usuário, emite um scanner que recebe o login e senha
     * @return um objeto Usuario para ser utilizado no programa
     */
    public static Usuario fazerLogin() {
        Scanner in = new Scanner(System.in);
        String login, senha;

        do {
            System.out.print("CPF (somente números): ");
            login = in.nextLine();
            System.out.print("Senha: ");
            senha = in.nextLine();

            if (!validarCPF(login) && !login.equals("admin")) {
                System.out.println("CPF inválido.");
            }
        } while (!validarCPF(login) && !login.equals("admin"));

        return new Usuario(login, senha);
    }

    static void mostrarMenuPrincipal() {
        System.out.println("\n---MENU PRINCIPAL---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Fazer compras");
        System.out.println("[2] Trocar usuário");
        System.out.println("[3] Sobre");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCompras() {
        System.out.println("\n---MENU DE COMPRAS---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Buscar produto");
        System.out.println("[2] Listar produtos");
        System.out.println("[3] Adicionar produto ao carrinho");
        System.out.println("[4] Exibir carrinho");
        System.out.println("[5] Voltar ao menu principal");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCarrinho() {
        System.out.println("------------------");
        System.out.println("[1] Continuar comprando");
        System.out.println("[2] Editar carrinho");
        System.out.println("[3] Finalizar compra");
        System.out.print("Insira a opção desejada: ");
    }

    /**
     * Imprime um menu acessível somente ao usuário administrador.
     */
    static void mostrarMenuAdmin() {
        System.out.println("---ADMINISTRADOR---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Relatório de clientes");
        System.out.println("[2] Cadastrar produto");
        System.out.println("[3] Adicionar estoque");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }

    /**
     * Recebe uma string e a procura no banco de dados produtos.txt;
     * Adiciona todos os produtos que contenham o parametro busca no nome,
     * podendo resultar em mais de um produto.
     * @param busca nome do produto a ser procurado
     * @return uma lista que contém todos os produtos encontrados, permitindo uma facil impressao de todos
     * os produtos encontrados.
     */
    public static List<Produto> buscarProduto(String busca) {
        List<Produto> result = new ArrayList<>();

        try {
            File db = new File("src/br/pucpr/sistema/produtos.txt");
            Scanner in = new Scanner(db);

            while (in.hasNextLine()) {
                String produto = in.nextLine();

                if (produto.contains(busca)) {
                    String[] atributos = produto.split(",");
                    List<String> lista = Arrays.asList(atributos);
                    Produto p = new Produto(lista.get(0), Double.parseDouble(lista.get(1)),
                                Integer.parseInt(lista.get(2)));
                    result.add(p);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Possui funcionamento similar à funçao de busca
     * @see #buscarProduto(String)
     * mas @return uma lista contendo todos os produtos na base de dados
     */
    public static List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();

        try {
            File db = new File("src/br/pucpr/sistema/produtos.txt");
            Scanner in = new Scanner(db);
            while (in.hasNextLine()) {
                String[] produto = in.nextLine().split(",");
                List<String> lista = Arrays.asList(produto);
                Produto p = new Produto(lista.get(0), Double.parseDouble(lista.get(1)),
                            Integer.parseInt(lista.get(2)));
                produtos.add(p);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return produtos;
    }

    /**
     * Recebe como parametros usuario e carrinho para finalizar a compra;
     * Caso haja estoque suficiente, a compra será concluida e o estoque será reduzido,
     * adicionando as compras e o total gasto no banco de dados de usuarios. FINALIZA O PROGRAMA.
     * A formatacao do banco é "cpf,senha,{qtd, produto, preco|total}", o que permite acessar as compras anteriores
     * e somar ao total já salvo quando novas compras forem feitas no cpf do usuario.
     * Utiliza a biblioteca Apache para manipular o arquivo txt, substituindo os textos no meio das linhas.
     * Não é muito eficiente para grandes bancos de dados, visto que reescreve o arquivo inteiro toda vez que
     * faz alguma adição de produto. Se o usuário comprou 5 produtos, o arquivo .txt será reescrito completamente
     * 5 vezes, tornando o processo extremamente lento e ineficiente.
     * @param user usuario para finalizar a compra.
     * @param carrinho carrinho do usuario.
     */
    public static void finalizarCompra(Usuario user, Carrinho carrinho) {
        File usuarios = new File("src/br/pucpr/sistema/usuarios.txt");
        File produtos = new File("src/br/pucpr/sistema/produtos.txt");

        try {
            Scanner scu = new Scanner(usuarios);
            Scanner scp = new Scanner(produtos);

            try {
                String db_produtos = FileUtils.readFileToString(produtos, "utf-8");
                String db_usuarios = FileUtils.readFileToString(usuarios, "utf-8");

                while (scp.hasNextLine()) {
                    String linha_produto = scp.nextLine();

                    for (String nome : carrinho.getProdutos().keySet()) {
                        // Sabe-se que a formatação do banco de dados de produtos é
                        // "nome,preco,estoque", portanto utiliza-se manipulação de String para obter os valores
                        // de nome, preço e estoque de cada produto

                        if (linha_produto.substring(0, linha_produto.indexOf(",")).equals(nome)) {
                            int estoque = Integer.parseInt(linha_produto.substring(
                                 linha_produto.lastIndexOf(",") + 1));
                            int qtd = (Integer) carrinho.getProdutos().get(nome).get(0);

                            if (estoque - qtd >= 0) {
                                String novo_estoque = String.format("%d", estoque - qtd);
                                db_produtos = db_produtos.replace(Integer.toString(estoque), novo_estoque);
                                FileUtils.writeStringToFile(produtos, db_produtos, "utf-8");

                                while (scu.hasNextLine()) {
                                    String linha_usuario = scu.nextLine();
                                    String nova_linha = linha_usuario.substring(0, linha_usuario.indexOf("|"));

                                    if (linha_usuario.substring(0,linha_usuario.indexOf(",")).equals(user.getLogin())) {
                                        String novas_compras = carrinho.toString();

                                        for (String p : carrinho.getProdutos().keySet()) {
                                            Double preco = (Double) carrinho.getProdutos().get(p).get(1);
                                            user.somarGasto(preco);
                                        }

                                        db_usuarios = db_usuarios.replace(linha_usuario,
                                               nova_linha + novas_compras + "|" + user.getGasto() + "}");

                                        FileUtils.writeStringToFile(usuarios, db_usuarios, "utf-8");
                                        System.out.println("Compra finalizada com sucesso!");
                                        System.exit(0);
                                    }
                                }

                            } else {
                                throw new IllegalArgumentException("Não há estoque suficiente.");
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Imprime um relatorio ordenado do cliente que mais gastou para o que menos gastou,
     * mostrando individualmente a quantidade obtida de cada produto e o preço de cada compra;
     * Cria a lista bidimensional "organizador", que recebe varias listas contendo "[cpf do usuario, gasto total]",
     * e organiza-a de acordo com o gasto total de cada cpf.
     * Utiliza a interface Comparator para organizar os clientes com a classe ComparadorUsuarios.
     */
    public static void imprimirRelatorio(boolean salvar) {
        File usuarios = new File("src/br/pucpr/sistema/usuarios.txt");
        double totalGeral = 0;
        int contador = 0;
        String arquivo = "";

        Date data = Calendar.getInstance().getTime();
        SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy");
        String strData = formatador.format(data);


        try {
            Scanner sc1 = new Scanner(usuarios);
            List<List<String>> organizador = new ArrayList<>();

            while (sc1.hasNextLine()) {
                String linha = sc1.nextLine();
                List<String> dados_cliente = new ArrayList<>();
                if (!linha.substring(0, linha.indexOf(",")).equals("admin")) {
                    String gasto_ind = linha.substring(linha.indexOf("|") + 1, linha.length() - 1);
                    dados_cliente.add(linha.substring(0, linha.indexOf(",")));
                    dados_cliente.add(gasto_ind);
                    organizador.add(dados_cliente);
                    totalGeral += Double.parseDouble(gasto_ind);
                    contador += 1;
                }

            }
            ComparadorUsuarios comp = new ComparadorUsuarios();
            organizador.sort(comp.reversed());

            for (List<String> user : organizador) {
                System.out.printf("\n%s, $%s\n", user.get(0), user.get(1));
                arquivo = arquivo.concat("%s, $%s\n".formatted(user.get(0), user.get(1)));
                Scanner sc2 = new Scanner(usuarios);

                while (sc2.hasNextLine()) {
                    String linha = sc2.nextLine();
                    List<String> produtos;

                    if (linha.substring(0, linha.indexOf(",")).equals(user.get(0))) {
                        produtos = List.of(linha.substring(linha.indexOf("{") + 1,
                                           linha.indexOf("|")).split(";"));
                        for (String produto : produtos) {
                            System.out.printf("\t%s\n", produto);
                            arquivo = arquivo.concat("\t%s\n".formatted(produto));
                        }
                        arquivo = arquivo.concat("\n");

                    }
                }
            }
            arquivo = arquivo.concat("------------------\n");
            arquivo = arquivo.concat("TOTAL GERAL: $%.2f\n".formatted(totalGeral));
            arquivo = arquivo.concat("VALOR MÉDIO: $%.2f\n".formatted(totalGeral / contador));


            System.out.println("------------------");
            System.out.printf("TOTAL GERAL: $%.2f\n", totalGeral);
            System.out.printf("VALOR MÉDIO: $%.2f\n", totalGeral / contador);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (salvar) {
            try {
                PrintWriter pw = new PrintWriter("RELATORIO_%s.txt".formatted(strData));
                pw.println(arquivo);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Faz a conta de um cpf real utilizando os 9 e, se necessario,
     * os 10 primeiros digitos do cpf informados na funcao de login.
     * @see #fazerLogin()
     * @return true, caso contrario retorna false (CPF invalido).
     * @see <a href="https://bit.ly/3RYpbRN"></a>
     * Para informações sobre validação de cpf.
     * @param cpf String somente de numero contendo CPF recebido para análise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean validarCPF(String cpf) {
        int soma = 0, fator = 10;

        if (cpf.length() == 11) {
            for (char c : cpf.substring(0, 9).toCharArray()) {
                String s = String.valueOf(c);
                soma += fator * Integer.parseInt(s);
                fator -= 1;
            }
            if (soma % 11 < 2) {
                if (!String.valueOf(cpf.charAt(9)).equals("0") && !String.valueOf(cpf.charAt(9)).equals("1")) {
                    return false;
                    }

            } else {
                if (!String.valueOf(cpf.charAt(9)).equals("%d".formatted(11 - soma % 11))) {
                    return false;
                }
            }
            soma = 0;
            fator = 11;

            for (char c : cpf.substring(0, 10).toCharArray()) {
                String s = String.valueOf(c);
                soma += fator * Integer.parseInt(s);
                fator -= 1;
            }

            if (soma % 11 < 2) {
                if (!String.valueOf(cpf.charAt(10)).equals("0") && !String.valueOf(cpf.charAt(10)).equals("1")) {
                    return false;
                    }

            } else {
                if (!String.valueOf(cpf.charAt(10)).equals("%d".formatted(11 - soma % 11))) {
                    return false;
                }
            }
        return true;
        } else {
            return false;
        }
    }

    /**
     * Funçãoo para esperar o usuario decidir quando continuar a interação com os menus.
     */
    public static void standby() {
        Scanner in = new Scanner(System.in);
        String continuar = null;
        System.out.print("Pressione ENTER para continuar...");
        do {
          continuar = in.nextLine();
        } while (!continuar.isEmpty());
    }

    /**
     * Cadastra um produto no banco de dados, colocando-o no final do arquivo de texto.
     * @param nome do produto a ser cadastrado.
     * @param preco do produto a ser cadastrado.
     * @param estoque do produto a ser cadastrado.
     */
    public static void adminCadastrarProduto(String nome, Double preco, int estoque) {
        File produtos = new File("src/br/pucpr/sistema/produtos.txt");

        try (FileWriter fw = new FileWriter(produtos, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw);) {

            pw.print("\n" + nome + "," + preco + "," + estoque);
            bw.flush();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Adiciona uma certa quantidade ao estoque atual do produto.
     * @param nome do produto a receber o estoque.
     * @param qtd a ser inserida no estoque.
     */
    public static void adminAdicionarEstoque(String nome, int qtd) {
        File produtos = new File("src/br/pucpr/sistema/produtos.txt");
        try {
            Scanner sc = new Scanner(produtos);
            String db_produtos = FileUtils.readFileToString(produtos, "utf-8");

            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (linha.substring(0, linha.indexOf(",")).equals(nome)) {
                    String nova_linha = linha.substring(0, linha.lastIndexOf(","));
                    int estoque = Integer.parseInt(linha.substring(linha.lastIndexOf(",") + 1));

                    db_produtos = db_produtos.replace(linha, nova_linha + "," + Integer.toString(qtd + estoque));
                    FileUtils.writeStringToFile(produtos, db_produtos, "utf-8");
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
