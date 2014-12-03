package br.com.split.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.split.model.Pessoa;

/**
 * Classe que armazena informacoes temporarias sobre um produto.
 * Serve de suporte para o cadastro de um novo produto e para o recadastro de um produto.
 */
public class ProdutoInfo implements Serializable
{
	// [start] DECLARACOES
	private static final long serialVersionUID = 5L;

	private String nome;
	private double preco;
	private int quantidade;
	private String textoPreco;
	private List<Pessoa> pessoasVinculadas;

	// [end] DECLARACOES


	// [start] CONSTRUTORES
	/**
	 * Cria um novo <code>ProdutoInfo</code>, com todas as suas propriedades setadas para os seus valores padrao.
	 */
	public ProdutoInfo()
	{
		resetar();
	}

	public ProdutoInfo(String nome, double preco, int quantidade)
	{
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
	}

	// [end] CONSTRUTORES


	// [start] METODOS
	public void incrementarQuantidade()
	{
		++quantidade;
	}

	public void decrementarQuantidade()
	{
		--quantidade;
	}

	public boolean compararNomePreco(String nome, double preco)
	{
		return (this.nome.equals(nome) && this.preco == preco);
	}

	/**
	 * Reseta todas as propriedades do <code>ProdutoInfo</code> para os seus valores default.
	 */
	public void resetar()
	{
		nome = "";
		preco = 0.0d;
		quantidade = 1;
		textoPreco = "";
		pessoasVinculadas = new ArrayList<Pessoa>();
	}

	// [end] METODOS


	// [start] GET_SET
	/**
	 * Retorna o nome do produto.
	 * @return O nome do produto.
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Altera o nome do produto.
	 * @param nome O novo nome do produto.
	 */
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	/**
	 * Retorna o preco do produto.
	 * @return O preco do produto.
	 */
	public double getPreco()
	{
		return preco;
	}

	/**
	 * Altera o preco do produto.
	 * @param preco O novo preco do produto.
	 */
	public void setPreco(double preco)
	{
		this.preco = preco;
	}

	/**
	 * Retorna a quantidade do produto.
	 * @return A quantidade do produto.
	 */
	public int getQuantidade()
	{
		return quantidade;
	}

	public double getTotalProduto()
	{
		return preco * quantidade;
	}

	public String getTextoQuantidade()
	{
		return String.valueOf(quantidade);
	}

	public String getTextoTotalProdutoFormatado()
	{
		return String.format(Locale.getDefault(), "R$ %.2f", getTotalProduto());
	}

	public String getTextoPrecoFormatado()
	{
		return String.format(Locale.getDefault(), "R$ %.2f", preco);
	}

	/**
	 * Retorna o preco do produto em formato de <code>String</code>.
	 * @return O preco do produto em formato de <code>String</code>.
	 */
	public String getTextoPreco()
	{
		return textoPreco;
	}

	/**
	 * Altera o preco do produto em formato de <code>String</code>.
	 * @param textoPreco O novo preco do produto em formato de <code>String</code>.
	 */
	public void setTextoPreco(String textoPreco)
	{
		this.textoPreco = textoPreco;
	}

	/**
	 * Retorna uma <code>List</code> contendo as pessoas vinculadas ao produto.
	 * @return Uma <code>List</code> contendo as pessoas vinculadas ao produto.
	 */
	public List<Pessoa> getPessoasVinculadas()
	{
		return pessoasVinculadas;
	}

	/**
	 * Altera a <code>List</code> contendo as pessoas vinculadas ao produto.
	 * @param pessoasVinculadas A nova <code>List</code> contendo as pessoas vinculadas ao produto.
	 */
	public void setPessoasVinculadas(List<Pessoa> pessoasVinculadas)
	{
		this.pessoasVinculadas = pessoasVinculadas;
	}

	// [end] GET_SET
}
