package br.com.split.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Evento
{
	private long id;
	private String nome;
	private Date dataInicio;
	private Date dataFim;
	private List<Consumo> consumos;
	private List<Pessoa> pessoas;
	private List<Produto> produtos;
	private double valorConta;

	/**
	 * Cria um novo evento.
	 */
	public Evento()
	{
		this("");
	}

	/**
	 * Cria um novo evento.
	 * @param nome O nome do novo evento.
	 */
	public Evento(String nome)
	{
		this.nome = nome;
		this.dataInicio = new Date();
		this.dataFim = null;
		this.consumos = new ArrayList<Consumo>();
		this.pessoas = new ArrayList<Pessoa>();
		this.produtos = new ArrayList<Produto>();
		this.valorConta = 0d;
	}

	public void calcularEvento()
	{
		valorConta = 0d;
		for (Consumo consumo : consumos) {
			consumo.calcularConsumo();
			valorConta += consumo.getValorTotalConsumo();
		}
		for (Pessoa pessoa : pessoas) {
			pessoa.calcularContaPessoa();
		}
		for (Produto produto : produtos) {
			produto.calcularProduto();
		}
	}

	/**
	 * Retorna uma media com o valor a ser pago por cada pessoa, conforme a quantidade de
	 * pessoas e de produtos deste evento (valor da conta / quantidade de pessoas).
	 * @return Uma media com o valor a ser pago por cada pessoa deste evento.
	 */
	public double calcularMediaGeralPreco()
	{
		return this.valorConta / this.pessoas.size();
	}

	public void adicionarConsumo(Consumo consumo)
	{
		consumos.add(consumo);
		valorConta += consumo.getPrecoProduto() * consumo.getQuantidade();
	}

	public void adicionarPessoa(Pessoa pessoa)
	{
		pessoas.add(pessoa);
	}

	public void adicionarProduto(Produto produto)
	{
		produtos.add(produto);
	}

	/**
	 * Retorna o Id deste evento.
	 * @return O Id deste evento.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Seta o Id deste evento.
	 * @param id O novo Id deste evento.
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Retorna o nome deste evento.
	 * @return O nome deste evento.
	 */
	public String getNome()
	{
		return nome;
	}

	/**
	 * Seta o nome deste evento.
	 * @param nome O novo nome deste evento.
	 */
	public void setNome(String nome)
	{
		this.nome = nome;
	}

	/**
	 * Retorna a data de inicializacao deste evento.
	 * @return A data de inicializacao deste evento.
	 */
	public Date getDataInicio()
	{
		return dataInicio;
	}

	/**
	 * Retorna a data de encerramento deste evento.
	 * @return A a data de encerramento deste evento.
	 */
	public Date getDataFim()
	{
		return dataFim;
	}

	/**
	 * A data de encerramento deste evento.
	 * @param dataFim A nova data de encerramento deste evento.
	 */
	public void setDataFim(Date dataFim)
	{
		this.dataFim = dataFim;
	}

	public List<Consumo> getConsumos()
	{
		return consumos;
	}

	/**
	 * Retorna a lista de pessoas vinculadas a este evento.
	 * @return A lista de pessoas vinculadas a este evento.
	 */
	public List<Pessoa> getPessoas()
	{
		return pessoas;
	}

	/**
	 * Retorn a lista de produtos vinculados a este evento.
	 * @return A lista de produtos vinculados a este evento.
	 */
	public List<Produto> getProdutos()
	{
		return produtos;
	}

	/**
	 * Retorn o valor da conta deste evento.
	 * @return O valor da conta deste evento.
	 */
	public double getValorConta()
	{
		return valorConta;
	}

	/**
	 * Retorna a quantidade de pessoas vinculadas a este evento.
	 * @return A quantidade de pessoas vinculadas a este evento.
	 */
	public int getQtdPessoas()
	{
		return this.pessoas.size();
	}

	/**
	 * Retorna a quantidade de produtos vinculadas a este evento.
	 * @return A quantidade de produtos vinculadas a este evento.
	 */
	public int getQtdProdutos()
	{
		return this.produtos.size();
	}

	/**
	 * Retorna uma <code>String</code> com a quantidade de pessoas vinculadas a este evento.
	 * @return Uma <code>String</code> com a quantidade de pessoas vinculadas a este evento.
	 */
	public String getTextoQtdPessoas()
	{
		return String.valueOf(this.pessoas.size());
	}

	/**
	 * Retorna uma <code>String</code> com a quantidade de produtos vinculados a este evento.
	 * @return Uma <code>String</code> com a quantidade de produtos vinculados a este evento.
	 */
	public String getTextoQtdProdutos()
	{
		return String.valueOf(this.produtos.size());
	}

	/**
	 * Retorna uma <code>String</code> formatada com o valor da conta deste evento.
	 * @return Uma <code>String</code> formatada com o valor da conta deste evento.
	 */
	public String getTextoValorConta()
	{
		return String.format("R$ %.2f", this.valorConta);
	}

	/**
	 * Procura e, se encontrada, retorna uma pessoa vinculada a este evento pelo seu nome.
	 * @param nome O nome da pessoa vinculada a este evento pela qual procurar.
	 * @return Uma <code>Pessoa</code> se esta for encontrada, caso contrario, <code>null</code>;
	 */
	public Pessoa getPessoaByNome(String nome)
	{
		for (Pessoa pessoa : this.pessoas) {
			if (pessoa.getNome().equals(nome)) {
				return pessoa;
			}
		}
		return null;
	}

	/**
	 * Procura e, se encontrada, retorna um produto vinculado a este evento pelo seu nome.
	 * @param nome O nome do produto vinculada a este evento pelao qual procurar.
	 * @return Um <code>Produto</code> se este for encontrada, caso contrario, <code>null</code>;
	 */
	public Produto getProdutoByNome(String nome)
	{
		for (Produto produto : this.produtos) {
			if (produto.getNome().equals(nome)) {
				return produto;
			}
		}
		return null;
	}
}
