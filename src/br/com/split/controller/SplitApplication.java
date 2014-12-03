package br.com.split.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.view.Window;
import android.view.WindowManager;
import br.com.split.activities.VerContaActivity;
import br.com.split.model.Evento;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;

public class SplitApplication extends Application
{
	private Evento evento = null;
	private SplitConfig config = null;
	private ProdutoInfo produtoInfo = null;
	private Integer actVerContaOrdenacao = null;
	private List<GraphUser> selectedUsers = null;
	private GraphPlace selectedPlace = null;

	/**
	 * Cria um novo evento global.
	 * @param nome O nome do novo evento.
	 * @return O evento global que foi criado.
	 */
	public Evento criarEvento(String nome)
	{
		evento = new Evento(nome);
		return evento;
	}

	/**
	 * Verifica se o evento global existe.
	 * @return True se o evento global existe, caso contrario, false.
	 */
	public boolean existeEvento()
	{
		return (evento != null);
	}

	public void suprimirTeclado(Window window)
	{
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	//////////////////////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	//////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retorna o evento global.
	 * @return O evento atual global.
	 */
	public synchronized Evento getEvento()
	{
		return evento;
	}

	/**
	 * Retorna as configuracoes do aplicativo Split.
	 * @return As configuracoes do aplicativo Split.
	 */
	public synchronized SplitConfig getConfig()
	{
		if (config == null) {
			config = new SplitConfig(getApplicationContext());
		}
		return config;
	}

	/**
	 * Retorna as informacoes armazenadas temporariamente de um produto.
	 * @return As informacoes armazenadas temporariamente de um produto.
	 */
	public synchronized ProdutoInfo getProdutoInfo()
	{
		if (produtoInfo == null) {
			produtoInfo = new ProdutoInfo();
		}
		return produtoInfo;
	}

	public synchronized Integer getActVerContaOrdenacao()
	{
		if (actVerContaOrdenacao == null) {
			actVerContaOrdenacao = VerContaActivity.ORDENACAO_NOME;
		}
		return actVerContaOrdenacao;
	}

	public void setActVerContaOrdenacao(Integer actVerContaOrdenacao)
	{
		this.actVerContaOrdenacao = actVerContaOrdenacao;
	}

	/**
	 * Retorna uma <code>List</code> contendo os usuarios do Facebook selecionados.
	 * @return Uma <code>List</code> contendo os usuarios do Facebook selecionados.
	 */
	public synchronized List<GraphUser> getSelectedUsers()
	{
		if (selectedUsers == null) {
			selectedUsers = new ArrayList<GraphUser>();
		}
		return selectedUsers;
	}

	/**
	 * Altera a <code>List</code> contendo os usuarios do Facebook selecionados.
	 * @param selectedUsers A nova <code>List</code> contendo os usuarios do Facebook selecionados.
	 */
	public void setSelectedUsers(List<GraphUser> selectedUsers)
	{
		this.selectedUsers = selectedUsers;
	}

	public synchronized GraphPlace getSelectedPlace()
	{
		if (selectedPlace == null) {
			selectedPlace = null;
		}
		return selectedPlace;
	}

	public void setSelectedPlace(GraphPlace selectedPlace)
	{
		this.selectedPlace = selectedPlace;
	}
}
