package br.com.split.model;

/*
 * EM TESTA, AINDA NAO UTILIZADA!
 */
public class UsuarioFacebook
{
	private String id;
	private String nome;

	public UsuarioFacebook(String id, String nome)
	{
		this.id = id;
		this.nome = nome;
	}

	public String getId()
	{
		return id;
	}

	public String getNome()
	{
		return nome;
	}
}
