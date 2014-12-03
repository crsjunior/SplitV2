/*
 * O Conusmo vai ter:
 * - Um produto.
 * - A quantidade consumida deste produto.
 * - Uma lista das pessoas que consumiram este produto.
 */

package br.com.split.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Consumo implements Serializable
{
	private static final long serialVersionUID = 3L;
	private long id;
	private Produto produto;
	private double precoProduto;
	private int quantidade;
	private double valorTotalConsumo;
	private double valorPorPessoa;
	private List<Pessoa> pessoas;

	public Consumo(Produto produto, int quantidade)
	{
		this(produto, produto.getPrecoSugerido(), quantidade);
	}

	public Consumo(Produto produto, double precoProduto, int quantidade)
	{
		this.produto = produto;
		this.precoProduto = precoProduto;
		this.quantidade = quantidade;
		this.pessoas = new ArrayList<Pessoa>();
		produto.vincularConsumo(this);
	}

	public void vincularPessoa(Pessoa pessoa)
	{
		pessoas.add(pessoa);
		pessoa.vincularConsumo(this);
	}

	public void calcularConsumo()
	{
		valorTotalConsumo = precoProduto * quantidade;
		valorPorPessoa = valorTotalConsumo / pessoas.size();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Produto getProduto()
	{
		return produto;
	}

	public int getQuantidade()
	{
		return quantidade;
	}

	public double getPrecoProduto()
	{
		return precoProduto;
	}

	public double getValorTotalConsumo()
	{
		return valorTotalConsumo;
	}

	public double getValorPorPessoa()
	{
		return valorPorPessoa;
	}

	public List<Pessoa> getPessoas()
	{
		return pessoas;
	}

	public String getTextoPrecoProduto()
	{
		return String.format("R$ %.2f", this.precoProduto);
	}

	public String getTextoValorTotalConsumo()
	{
		return String.format("R$ %.2f", this.valorTotalConsumo);
	}

	public String getTextoValorPorPessoa()
	{
		return String.format("R$ %.2f", this.valorPorPessoa);
	}
}
