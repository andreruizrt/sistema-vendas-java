package main.repository;

import main.domain.Compra;
import main.domain.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraRepository {
    DatabaseRepository dbRepository = new DatabaseRepository();

    public int registaCompra(Compra compra) {
        String insertSql = "INSERT INTO COMPRA_TR01(IDFORNECEDOR, DTCOMPRA) " +
                "VALUES (?, ?)";

        try (Connection conn = dbRepository.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, compra.getIdFornecedor());
            pstmt.setString(2, compra.getDtCompra().toString());

            int i = pstmt.executeUpdate();

            System.out.println("Compra cadastrada!");
            return i;
        } catch (SQLException ex) {
            System.out.println("Erro inserindo compra no banco: " + ex.getMessage());
        }
        return -1;
    }

    public void inserirCompraItens(int idCompra, List<Produto> produtoComprados) {
        Connection conn = dbRepository.connect();

        String insertSql = "INSERT INTO ITEM_COMPRA_TR01(IDCOMPRA, IDPRODUTO, QTDPRODUTO) " +
                "VALUES (?, ?, ?)";

        for (Produto produto : produtoComprados) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, idCompra);
                pstmt.setInt(2, produto.getId());
                pstmt.setInt(3, produto.getQtdEstoque());

                pstmt.executeUpdate();
                System.out.println("Lista de produtos comprados salva com sucesso!");

            } catch (SQLException e) {
                System.out.println("Erro ao inserir lista de produtos: " + e.getMessage());
                return;
            }
        }
    }

    public Compra findCompraById(int id) {
        String sql = "SELECT * FROM COMPRA_TR01";

        try (Connection conn = dbRepository.connect();
             Statement stmt = conn.createStatement();
             ResultSet pstmt = stmt.executeQuery(sql)) {

            while (pstmt.next()) {
                if (pstmt.getInt("ID") == id) {
                    Compra compra = new Compra();
                    compra.setId(pstmt.getInt("ID"));
                    compra.setIdFornecedor(pstmt.getInt("IDFORNECEDOR"));
//                    compra.setDtCompra(pstmt.getString("DTCOMPRA"));
                   compra.setProdutos(this.retornaProdutoCompra(compra.getId()));

                    return compra;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private List<Produto> retornaProdutoCompra(int id) {
        String sql = "SELECT IDCOMPRA, IDPRODUTO FROM ITEM_COMPRA_TR01";
        ProdutoRepository produtoService = new ProdutoRepository();
        List<Produto> produtoList = new ArrayList<>();

        try (Connection conn = dbRepository.connect();
             Statement stmt = conn.createStatement();
             ResultSet pstmt = stmt.executeQuery(sql)) {

            while (pstmt.next()) {
                if (pstmt.getInt("IDCOMPRA") == id) {
                    int idProduto = pstmt.getInt("IDPRODUTO");
                    produtoList.add(produtoService.findProdutoById(idProduto));
                }
            }

            return (produtoList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteCompraById(int id) {
        String sql = "DELETE FROM COMPRA_TR01 WHERE ID = ?";

        try (Connection conn = dbRepository.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            pstmt.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
