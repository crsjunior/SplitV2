package br.com.split.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Evento;
import br.com.split.model.Pessoa;
import br.com.split.utilidades.Utilidades;

public class CadastrarPessoaActivity extends Activity
{
	public static final String TAG = "CadastrarPessoaActivity";

	private SplitApplication app;
	private EditText editText_nomePessoa;

	// array dos controles (views) da activity que serao bloqueadas quando necessario,
	// de forma a impedir o usuario de interagir com elas:
	private View[] arrViewsBloqueaveis;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastrar_pessoa);

		this.app = (SplitApplication) getApplicationContext();
		this.editText_nomePessoa = (EditText) findViewById(R.id.edittext_nome_pessoa);

		TextView textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		textView_linhaDescricao.setText(getResources().getString(R.string.act_cadastrar_pessoa_linha_descricao));

		this.arrViewsBloqueaveis = new View[] {
				this.editText_nomePessoa,
				(Button) findViewById(R.id.button_adicionar_pessoa_facebook),
				(Button) findViewById(R.id.button_enviar_cadastro_pessoa)
		};

		this.app.suprimirTeclado(getWindow());

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);
		textView_linhaDescricao.startAnimation(animZoom);
	}

	@Override
	public void onBackPressed()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		Log.e(TAG, "entrou onActivityResult");

		if (resultCode == RESULT_OK) {
			Evento evento = this.app.getEvento();
			List<String> listaEscolhidos = Utilidades.FB.getUsersNamesAsString(this.app.getSelectedUsers());
			for (String nome : listaEscolhidos) {
				if (evento.getPessoaByNome(nome) == null) {
					evento.adicionarPessoa(new Pessoa(nome, true));
				}
			}

			this.voltarActivityPai(getResources().getString(R.string.act_cadastrar_pessoa_cadastrado_com_sucesso_facebook));
		} else {
			// 
			Utilidades.Views.enableViews(this.arrViewsBloqueaveis);
		}
	}

	/**
	 * Escuta os cliques do usuario nos botoes da activity.
	 * @param view A view do botao clicado pelo usuario.
	 */
	public void onButtonClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_adicionar_pessoa_facebook:
				mostrarAmigosDoFacebook();
				break;
			case R.id.button_enviar_cadastro_pessoa:
				iniciarCadastroPessoa();
				break;
		}
	}

	/**
	 * Abre a activity para escolher os amigos do facebook.
	 */
	private void mostrarAmigosDoFacebook()
	{
		// o atraso corrige um bug do android.
		Utilidades.Views.disableViews(this.arrViewsBloqueaveis);
		Utilidades.Teclado.ocultarTeclado(this);
		final Intent intent = new Intent(getApplication(), FacebookFriendPickerActivity.class);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				startActivityForResult(intent, 100);
			}
		}, 100);
	}

	/**
	 * Inicia o processo de cadastro da pessoa, validando o preenchimento dos campos da pessoa pelo usuario.
	 * Se tudo estiver ok, automaticamente chama o metodo que finaliza o cadastro,
	 * caso contrario, informa o usuario.
	 */
	private void iniciarCadastroPessoa()
	{
		String nomePessoa = ((EditText) findViewById(R.id.edittext_nome_pessoa)).getText().toString().trim();

		/*
		 * VALIDANDO O PREENCHIMENTO DOS CAMPOS PELO USUARIO:
		 * - nome da pessoa: obrigatorio
		 */

		// validando o nome da pessoa:
		if (nomePessoa.equals("")) {
			Utilidades.Toasts.enviarToastSemTeclado(this, this.arrViewsBloqueaveis,
					getResources().getString(R.string.act_cadastrar_pessoa_preenchimento_nome_pessoa), 1000);
			return;
		}

		// pessoa valida, cadastrando:
		Pessoa pessoa = new Pessoa(nomePessoa, false);
		this.app.getEvento().adicionarPessoa(pessoa);
		this.voltarActivityPai(getResources().getString(R.string.act_cadastrar_pessoa_cadastrado_com_sucesso));
	}

	private void voltarActivityPai(String mensagem)
	{
		Utilidades.Toasts.enviarToastSemTeclado(this, this.arrViewsBloqueaveis, mensagem, 600);
		Utilidades.Navegacao.voltarComAtraso(this, 1400, R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}
}
