package br.com.split.controller;

import android.content.Context;
import android.util.DisplayMetrics;

public class SplitConfig
{
	// Quantidade percentual da largura do display que o drawer ira ocupar quando exibido.
	private static final int LARGURA_PERCENTUAL_DRAWER = 90;

	private int displayWidth;
	private int displayHeight;

	/**
	 * Cria um novo <code>SplitConfig</code>.
	 * @param applicationContext O contexto da aplicacao.
	 */
	public SplitConfig(Context applicationContext)
	{
		DisplayMetrics displayMetrics = applicationContext.getResources().getDisplayMetrics();
		this.displayWidth = displayMetrics.widthPixels;
		this.displayHeight = displayMetrics.heightPixels;
	}

	/**
	 * Retorna a largura do display (em pixels).
	 * @return A largura do display (em pixels).
	 */
	public int getDisplayWidth()
	{
		return displayWidth;
	}

	/**
	 * Retorna a altura do display (em pixels).
	 * @return A altura do display (em pixels).
	 */
	public int getDisplayHeight()
	{
		return displayHeight;
	}

	/**
	 * Retorna a quantidade percentual da largura do display para o drawer ocupar.
	 * @return A quantidade percentual da largura do display para o drawer ocupar.
	 */
	public int getLarguraPercentualDrawer()
	{
		return LARGURA_PERCENTUAL_DRAWER;
	}
}
