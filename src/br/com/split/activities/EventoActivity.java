package br.com.split.activities;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.controller.SplitApplication;
import br.com.split.utilidades.Utilidades;

public class EventoActivity extends Activity implements AnimationListener
{
	// indica se o sentido da rotacao da animacao dos botoes sera sorteada aleatoriamente
	// toda vez que um botao for clicado, ou sera sempre uma rotacao no sentido horario.
	private static final boolean SENTIDO_ROTACAO_ALEATORIO = true;

	private SplitApplication app;
	private TextView textView_nomeEvento;
	private TextView textView_qtdPessoas;
	private TextView textView_qtdProdutos;
	private TextView textView_totalParcial;

	private ImageView imageView_buttonCadastroPessoa;
	private ImageView imageView_buttonCadastroProduto;
	private ImageView imageView_buttonExibirPessoas;
	private ImageView imageView_buttonExibirProdutos;

	private OpcaoBotao opcao;
	private Animation[] animRotacao = new Animation[2];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evento);

		app = (SplitApplication) getApplicationContext();

		textView_nomeEvento = (TextView) findViewById(R.id.textview_nome_evento);
		textView_qtdPessoas = (TextView) findViewById(R.id.textview_qtd_pessoas);
		textView_qtdProdutos = (TextView) findViewById(R.id.textview_qtd_produtos);
		textView_totalParcial = (TextView) findViewById(R.id.textview_total_parcial);

		animRotacao[0] = AnimationUtils.loadAnimation(EventoActivity.this, R.anim.rotacao_sentido_horario);
		animRotacao[0].setAnimationListener(this);
		if (SENTIDO_ROTACAO_ALEATORIO) {
			animRotacao[1] = AnimationUtils.loadAnimation(EventoActivity.this, R.anim.rotacao_sentido_antihorario);
			animRotacao[1].setAnimationListener(this);
		}

		imageView_buttonCadastroPessoa = (ImageView) findViewById(R.id.button_cadastro_pessoa_imageview);
		imageView_buttonCadastroProduto = (ImageView) findViewById(R.id.button_cadastro_produto_imageview);
		imageView_buttonExibirPessoas = (ImageView) findViewById(R.id.button_exibir_pessoas_imageview);
		imageView_buttonExibirProdutos = (ImageView) findViewById(R.id.button_exibir_produtos_imageview);

		//		atualizarInformacoes();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		app.getProdutoInfo().resetar();
		//		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textview_slide);
		//		textView_nomeEvento.startAnimation(anim);

		atualizarInformacoes();
	}

	@Override
	public void onBackPressed()
	{
		// minimizando o aplicativo programaticamente (evitando que a splash screen seja reexibida quando
		// restaurada, ou que o aplicativo volte para a parent intent):
		Utilidades.Aplicativo.minizarAplicativo(this);
		//		Intent intent = new Intent(Intent.ACTION_MAIN);
		//		intent.addCategory(Intent.CATEGORY_HOME);
		//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.evento, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_cadastro_pessoa:
				opcao = OpcaoBotao.CADASTRO_PESSOA;
				imageView_buttonCadastroPessoa.startAnimation(obterAnimacaoRotacao());
				break;
			case R.id.button_cadastro_produto:
				opcao = OpcaoBotao.CADASTRO_PRODUTO;
				imageView_buttonCadastroProduto.startAnimation(obterAnimacaoRotacao());
				break;
			case R.id.button_exibir_pessoas:
				opcao = OpcaoBotao.EXIBIR_PESSOAS;
				imageView_buttonExibirPessoas.startAnimation(obterAnimacaoRotacao());
				break;
			case R.id.button_exibir_produtos:
				opcao = OpcaoBotao.EXIBIR_PRODUTOS;
				imageView_buttonExibirProdutos.startAnimation(obterAnimacaoRotacao());
				break;
			case R.id.button_ver_conta:
				abrirActivity(VerContaActivity.class);
				break;
		}
	}

	/**
	 * Cria uma <code>Intent</code> para uma <code>Activity</code> e a inicia.
	 * @param cls A <code>Class</code> da <code>Activity</code> que sera iniciada.
	 */
	private void abrirActivity(Class<?> cls)
	{
		startActivity(new Intent(EventoActivity.this, cls));
		overridePendingTransition(R.anim.esmaecer_abre_filho, R.anim.esmaecer_fecha_pai);
	}

	private void atualizarInformacoes()
	{
		textView_nomeEvento.setText(app.getEvento().getNome());
		textView_qtdPessoas.setText(app.getEvento().getTextoQtdPessoas());
		textView_qtdProdutos.setText(app.getEvento().getTextoQtdProdutos());
		textView_totalParcial.setText(app.getEvento().getTextoValorConta());
	}

	private Animation obterAnimacaoRotacao()
	{
		return (SENTIDO_ROTACAO_ALEATORIO ? animRotacao[new Random().nextInt(2)] : animRotacao[0]);
	}

	@Override
	public void onAnimationEnd(Animation animation)
	{
		switch (opcao) {
			case CADASTRO_PESSOA:
				abrirActivity(CadastrarPessoaActivity.class);
				break;
			case CADASTRO_PRODUTO:
				abrirActivity(CadastrarProdutoActivity.class);
				break;
			case EXIBIR_PESSOAS:
				abrirActivity(VerPessoasActivity.class);
				break;
			case EXIBIR_PRODUTOS:
				abrirActivity(VerProdutos2Activity.class);
				break;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation)
	{

	}

	@Override
	public void onAnimationStart(Animation animation)
	{

	}

	private enum OpcaoBotao
	{
		CADASTRO_PESSOA,
		CADASTRO_PRODUTO,
		EXIBIR_PESSOAS,
		EXIBIR_PRODUTOS;
	}
}
