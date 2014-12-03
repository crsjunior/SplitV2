package br.com.split.model;

import java.io.Serializable;
import java.util.Locale;

public class ProdutoVerInfo implements Serializable
{
	private static final long serialVersionUID = 4L;

	private String nome;
	private double preco;
	private int quantidade;

	public ProdutoVerInfo(String nome, double preco, int quantidade)
	{
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
	}

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

	public String getNome()
	{
		return nome;
	}

	public double getPreco()
	{
		return preco;
	}

	public int getQuantidade()
	{
		return quantidade;
	}

	public double getTotalProduto()
	{
		return preco * quantidade;
	}

	public String getTextoPreco()
	{
		return String.format(Locale.getDefault(), "R$ %.2f", preco);
	}

	public String getTextoQuantidade()
	{
		return String.valueOf(quantidade);
	}

	public String getTextoTotalProduto()
	{
		return String.format(Locale.getDefault(), "R$ %.2f", getTotalProduto());
	}
}
