package br.com.split.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Produto implements Serializable
{
	private static final long serialVersionUID = 2L;
	private long id;
	private String nome;
	private double precoSugerido;
	private int quantidadeConsumida;
	private List<Consumo> consumos;
	private double valorTotalGastoComEsteProduto;

	public Produto()
	{
		this("", 0l);
	}

	public Produto(String nome, double precoSugerido)
	{
		this.nome = nome;
		this.precoSugerido = precoSugerido;
		this.consumos = new ArrayList<Consumo>();
		this.quantidadeConsumida = 0;
		this.valorTotalGastoComEsteProduto = 0d;
	}

	public void calcularProduto()
	{
		quantidadeConsumida = consumos.size();
		valorTotalGastoComEsteProduto = 0d;
		for (Consumo consumo : consumos) {
			valorTotalGastoComEsteProduto += consumo.getPrecoProduto();
		}
	}

	protected void vincularConsumo(Consumo consumo)
	{
		consumos.add(consumo);
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Retorna o nome deste produto.
	 * @return O nome deste produto.
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Seta o nome deste produto.
	 * @param nome O novo nome deste produto.
	 */
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public double getPrecoSugerido()
	{
		return precoSugerido;
	}

	public void setPrecoSugerido(double precoSugerido)
	{
		this.precoSugerido = precoSugerido;
	}

	public int getQuantidadeConsumida()
	{
		return quantidadeConsumida;
	}

	public List<Consumo> getConsumos()
	{
		return consumos;
	}

	public double getValorTotalGastoComEsteProduto()
	{
		return valorTotalGastoComEsteProduto;
	}

	/**
	 * Retorna uma <code>String</code> com o preco deste produto.
	 * @return Uma <code>String</code> com o preco deste produto.
	 */
	public String getTextoPrecoSugerido()
	{
		return String.format("R$ %.2f", this.precoSugerido);
	}

	public String getTextoValorTotalGastoComEsteProduto()
	{
		return String.format("R$ %.2f", this.valorTotalGastoComEsteProduto);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Produto) {
			Produto oProduto = (Produto) o;
			return this.nome.equals(oProduto.getNome());
			//			return (this.nome.equals(oProduto.getNome()) && this.precoSugerido == oProduto.getPrecoSugerido());
		}
		return false;
	}
}
