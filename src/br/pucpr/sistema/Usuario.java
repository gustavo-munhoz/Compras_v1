package br.pucpr.sistema;

import java.io.*;
import java.util.Scanner;

public class Usuario {
    private String login;
    private String senha;
    public Carrinho compras;
    private Double gasto;

    public Usuario(String login, String senha) {
        File db = new File ("src/br/pucpr/sistema/usuarios.txt");
        try {
            Scanner sc = new Scanner(db);
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (linha.substring(0,linha.indexOf(",")).equals(login)) {
                    this.gasto = Double.parseDouble(linha.substring(linha.lastIndexOf("|") + 1,
                            linha.length() - 1));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Campo <usuario> vazio.");
        }
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Campo <senha> vazio.");
        }
        this.login = login;
        this.senha = senha;
        this.compras = new Carrinho();
    }

    public void setCompras(Carrinho carrinho) {
        compras = carrinho;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public Double getGasto() {
        return gasto;
    }

    public void somarGasto(Double valor) {
        gasto += valor;
    }

    /**
     * Procura no usuario no banco de dados usuarios.txt, mas nao verifica se a senha está correta.
     * @return true caso o usuário exista, false caso contrário.
     */
    public boolean existeUsuario() {
        File usuarios = new File("src/br/pucpr/sistema/usuarios.txt");
        Scanner in = null;

        try {
            in = new Scanner(usuarios);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.substring(0,line.indexOf(",")).equals(login)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se a senha inserida está correta. Procura o valor inserido dentro do banco de dados,
     * e confere se corresponde à senha do usuario inserido.
     * @return true caso senha esteja correta, false caso contrário.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean senhaCorreta() {
        File usuarios = new File("src/br/pucpr/sistema/usuarios.txt");
        Scanner in = null;

        try {
            in = new Scanner(usuarios);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.substring(0,line.indexOf(",")).equals(login)) {
                return line.substring(line.indexOf(",") + 1,
                        line.indexOf("{")).equals(senha); // retorna true se forem iguais
            }
        }
        return false;
    }

    /**
     * Insere o usuário no banco de dados, com login, senha e total de gastos com valor inicial 0.
     */
    public void cadastrarUsuario() {
        File usuarios = new File("src/br/pucpr/sistema/usuarios.txt");

        try (FileWriter fw = new FileWriter(usuarios, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);) {

            pw.print("\n" + login + "," + senha + "{|0.0}");
            bw.flush();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}