package br.com.split.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Pessoa implements Serializable
{
	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private boolean fromFacebook;
	private List<Consumo> consumos;
	private double valorConta;

	public static enum Order implements Comparator<Pessoa>
	{
		byNome()
		{
			@Override
			public int compare(Pessoa p1, Pessoa p2)
			{
				return p1.nome.compareTo(p2.nome);
			}
		},
		byValorConta()
		{
			@Override
			public int compare(Pessoa p1, Pessoa p2)
			{
				return ((Double) p1.valorConta).compareTo((Double) p2.valorConta);
			}
		},
		byQuantidadeConsumos()
		{
			@Override
			public int compare(Pessoa p1, Pessoa p2)
			{
				return ((Integer) p1.consumos.size()).compareTo((Integer) p2.consumos.size());
			}
		};

		@Override
		public abstract int compare(Pessoa p1, Pessoa p2);

		public Comparator<Pessoa> ascending()
		{
			return this;
		}

		public Comparator<Pessoa> descending()
		{
			return Collections.reverseOrder(this);
		}

	}

	public Pessoa()
	{
		this("", false);
	}

	//	public Pessoa(String nome)
	//	{
	//		this.nome = nome;
	//		this.consumos = new ArrayList<Consumo>();
	//		this.consumos = new ArrayList<Consumo>();
	//		this.valorConta = 0d;
	//	}

	public Pessoa(String nome, boolean fromFacebook)
	{
		this.nome = nome;
		this.fromFacebook = fromFacebook;
		this.consumos = new ArrayList<Consumo>();
		this.consumos = new ArrayList<Consumo>();
		this.valorConta = 0d;
	}

	protected void vincularConsumo(Consumo consumo)
	{
		consumos.add(consumo);
	}

	public void calcularContaPessoa()
	{
		valorConta = 0d;
		for (Consumo consumo : consumos) {
			valorConta += consumo.getValorPorPessoa();
		}
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
	 * Retorna o nome desta pessoa.
	 * @return O nome desta pessoa.
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Seta o nome desta pessoa.
	 * @param nome O novo nome desta pessoa.
	 */
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	/**
	 * Retorna um boolean indicando se a pessoa eh proveniente do Facebook.
	 * @return True se a pessoa eh proveniente do Facebook, caso contrario, false.
	 */
	public boolean isFromFacebook()
	{
		return fromFacebook;
	}

	/**
	 * Seta se a pessoa eh proveniente do Facebook.
	 * @param fromFacebook Um boolean indicando se a pessoa eh proveniente do Facebook.
	 */
	public void setFromFacebook(boolean fromFacebook)
	{
		this.fromFacebook = fromFacebook;
	}

	/**
	 * Retorna uma lista dos consumos vinculados a esta pessoa.
	 * @return Uma lista dos consumos vinculados a esta pessoa.
	 */
	public List<Consumo> getConsumos()
	{
		return consumos;
	}

	/**
	 * Retorna o valor da conta desta pessoa.
	 * @return O valor da conta desta pessoa.
	 */
	public double getValorConta()
	{
		return valorConta;
	}

	/**
	 * Seta o valor da conta desta pessoa.
	 * @param valorConta O valor da conta desta pessoa.
	 */
	public void setValorConta(float valorConta)
	{
		this.valorConta = valorConta;
	}

	/**
	 * Retorna uma <code>String</code> com o valor a ser pago por esta pessoa.
	 * @return Uma <code>String</code> com o valor a ser pago por esta pessoa
	 */
	public String getTextoValorConta()
	{
		return String.format("R$ %.2f", this.valorConta);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Pessoa) {
			Pessoa oPessoa = (Pessoa) o;
			return this.nome.equals(oPessoa.getNome());
		}
		return false;
	}

	@Override
	public String toString()
	{
		return nome;
	}
}
