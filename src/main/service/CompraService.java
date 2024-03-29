package main.service;

import main.domain.Compra;
import main.domain.Produto;
import main.repository.CompraRepository;
import main.repository.ControleEstoqueRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompraService {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Scanner sc = new Scanner(System.in);

    ControleEstoqueRepository controleEstoqueRepository = new ControleEstoqueRepository();
    CompraRepository compraRepostory = new CompraRepository();
    PessoaService pessoaService = new PessoaService();

    public void realizarCompra() throws SQLException, IOException {
        ProdutoService produtoService = new ProdutoService();
        Compra compra = new Compra();

        pessoaService.printIdENome(pessoaService.carregaPessoaPorTipo("F"));
        System.out.print("Informe o id do fornecedor: ");
        compra.setIdFornecedor(sc.nextInt());

        compra.setDtCompra(new Timestamp(System.currentTimeMillis()));
        /* TODO fix idCompra error */
        compra.setId(compraRepostory.registaCompra(compra));

        List<Produto> produtoComprados = new ArrayList<>();

        while(true) {
            Produto produto = new Produto();

            produtoService.printIdEDescricao(produtoService.carregaProdutos());
            System.out.print("informe o id do produto: ");
            produto.setId(sc.nextInt());

            System.out.print("informe a quantidade comprada: ");
            produto.setQtdEstoque(sc.nextInt());

            produtoComprados.add(produto);
            controleEstoqueRepository.entradaEstoque(produto.getId(), produto.getQtdEstoque());

            System.out.println("Deseja inserir outro produto a lista de compras? [SIM/NÃO]");
            String resposta = in.readLine();

            if (resposta.equalsIgnoreCase("sim")) {
                System.out.println("Insira o próximo produto\n");
            } else {
                compraRepostory.inserirCompraItens(compra.getId(), produtoComprados);
                break;
            }

        }
    }

    public Compra findCompraById() {
        System.out.println("Informe o id: ");
        int id = sc.nextInt();

        return compraRepostory.findCompraById(id);
    }

    public void deleteCompraById() {
        System.out.println("Informe o id: ");
        int id = sc.nextInt();

        compraRepostory.deleteCompraById(id);
    }
}
