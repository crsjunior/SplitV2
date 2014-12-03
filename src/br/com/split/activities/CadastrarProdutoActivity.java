package br.com.split.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.controles.NumberDialog;
import br.com.split.controles.NumberDialog.OnNumberDialogListener;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Consumo;
import br.com.split.model.Pessoa;
import br.com.split.model.Produto;
import br.com.split.utilidades.Utilidades;

public class CadastrarProdutoActivity extends Activity implements DialogInterface.OnClickListener, OnNumberDialogListener
{
	// [start] STRING_RESOURCES
	private static final int R_LINHA_DESCRICAO = R.string.act_cadastrar_produto_linha_descricao;
	private static final int R_PRODUTO_CADASTRADO_COM_SUCESSO = R.string.act_cadastrar_produto_cadastrado_com_sucesso;
	private static final int R_INFORME_NOME_PRODUTO = R.string.act_cadastrar_produto_preenchimento_nome_produto;
	private static final int R_INFORME_PRECO_PRODUTO = R.string.act_cadastrar_produto_preenchimento_preco_produto;
	private static final int R_DIALOG_NINGUEM_VINCULADO_TITULO = R.string.act_cadastrar_produto_dialog_ninguem_vinculado_titulo;
	private static final int R_DIALOG_NINGUEM_VINCULADO_MENSAGEM = R.string.act_cadastrar_produto_dialog_ninguem_vinculado_mensagem;
	private static final int R_DIALOG_NINGUEM_VINCULADO_BOTAO_POSITIVO = R.string.act_cadastrar_produto_dialog_ninguem_vinculado_botao_positivo;
	private static final int R_DIALOG_NINGUEM_VINCULADO_BOTAO_NEGATIVO = R.string.act_cadastrar_produto_dialog_ninguem_vinculado_botao_negativo;

	// [end] STRING_RESOURCES


	// [start] DECLARACOES
	private SplitApplication app;
	private EditText editText_nomeProduto;
	private EditText editText_precoProduto;

	// array dos controles (views) da activity que serao bloqueadas quando necessario,
	// de forma a impedir o usuario de interagir com elas:
	private View[] arrViewsBloqueaveis;

	private int quantidadeProduto;
	private boolean umProdutoParaCadaPessoa;

	private Animation animZoom;

	// [end] DECLARACOES


	// [start] METODOS_HERDADOS
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastrar_produto);

		this.app = (SplitApplication) getApplicationContext();

		this.editText_nomeProduto = (EditText) findViewById(R.id.edittext_nome_produto);
		this.editText_precoProduto = (EditText) findViewById(R.id.edittext_preco_produto);

		TextView textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		textView_linhaDescricao.setText(getResources().getString(R_LINHA_DESCRICAO));

		this.arrViewsBloqueaveis = new View[] {
				this.editText_nomeProduto,
				this.editText_precoProduto,
				(LinearLayout) findViewById(R.id.button_vincular_pessoas),
				(Button) findViewById(R.id.button_enviar_cadastro_produto)
		};

		this.quantidadeProduto = 1;
		this.umProdutoParaCadaPessoa = false;

		this.app.suprimirTeclado(getWindow());

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		this.animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_texto);

		Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);
		textView_linhaDescricao.startAnimation(animZoom);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		this.atualizarInformacoes();
	}

	@Override
	public void onBackPressed()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	// [end] METODOS_HERDADOS


	// [start] LISTENERS
	/**
	 * Escuta os cliques do usuario no DialogInterface.
	 * @see android.content.DialogInterface.OnClickListener#onClick(DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				this.abrirVincularProdutoPessoasActivity();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				this.app.getProdutoInfo().setPessoasVinculadas(app.getEvento().getPessoas());
				this.app.getProdutoInfo().setNome(this.editText_nomeProduto.getText().toString());
				this.app.getProdutoInfo().setTextoPreco(this.editText_precoProduto.getText().toString());
				this.atualizarInformacoes();
				break;
		}
		Utilidades.Views.enableViews(this.arrViewsBloqueaveis);
	}

	@Override
	public void onNumberDialogButtonDoneClick(int valor)
	{
		if (this.umProdutoParaCadaPessoa) {
			this.umProdutoParaCadaPessoa = false;
		}
		this.setQuantidadeProdutos(valor);
	}

	@Override
	public void onNumberDialogButtonCancelClick()
	{
	}

	/**
	 * Escuta os cliques do usuario nos botoes da activity.
	 * @param view A view do botao clicado pelo usuario.
	 */
	public void onButtonClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_quantidade_produto:
				this.abrirQuantidadeProdutoDialog();
				break;
			case R.id.button_um_produto_para_cada_pessoa:
				this.setUmProdutoParaCadaPessoa(!this.umProdutoParaCadaPessoa);
				break;
			case R.id.button_vincular_pessoas:
				this.abrirVincularProdutoPessoasActivity();
				break;
			case R.id.button_enviar_cadastro_produto:
				this.iniciarCadastroProdutoConsumo();
				break;
		}
	}

	// [end] LISTENERS


	// [start] METODOS
	/**
	 * Abre o dialog que permite alterar a quantidade do produto.
	 */
	private void abrirQuantidadeProdutoDialog()
	{
		this.salvarProdutoInfo();

		// abrindo o dialog para a escolha da quantidade do produto:
		NumberDialog numberDialog = NumberDialog.newInstance(3, this.quantidadeProduto);
		numberDialog.show(getFragmentManager(), "NumDialog");
	}

	/**
	 * Abre a activity que vincula as pessoas ao produto.
	 */
	private void abrirVincularProdutoPessoasActivity()
	{
		this.salvarProdutoInfo();

		// abrindo a activity para o usuario vincular as pessoas ao produto:
		Intent intent = new Intent(CadastrarProdutoActivity.this, VincularProdutoPessoasActivity.class);
		Utilidades.Navegacao.abrirComAtraso(this, intent, Utilidades.Navegacao.TEMPO_PADRAO_ATRASO,
				R.anim.deslizar_abre_filho, R.anim.deslizar_fecha_pai);
	}

	private void setQuantidadeProdutos(int quantidade)
	{
		if (quantidade > 0) {
			this.quantidadeProduto = quantidade;
		} else {
			this.quantidadeProduto = 1;
		}

		this.atualizarInformacoes();
	}

	private void setUmProdutoParaCadaPessoa(boolean ativar)
	{
		this.umProdutoParaCadaPessoa = ativar;

		if (ativar) {
			this.abrirVincularProdutoPessoasActivity();
		} else {
			this.setQuantidadeProdutos(1);
		}
	}

	/**
	 * Inicia o processo de cadastro do produto, validando o preenchimento dos campos do produto pelo usuario.
	 * Se tudo estiver ok, automaticamente chama o metodo que finaliza o cadastro,
	 * caso contrario, informa o usuario.
	 */
	private void iniciarCadastroProdutoConsumo()
	{
		String nomeProduto = this.editText_nomeProduto.getText().toString().trim();
		String strPrecoProduto = this.editText_precoProduto.getText().toString().trim();
		double precoProduto;

		/*
		 * VALIDANDO O PREENCHIMENTO DOS CAMPOS PELO USUARIO:
		 * - nome do produto: obrigatorio
		 * - preco do produto: obrigatorio
		 * - pessoas vinculadas ao produto: no minimo uma pessoa
		 */

		// validando nome do produto:
		if (nomeProduto.equals("")) {
			Utilidades.Toasts.enviarToastSemTeclado(this, this.arrViewsBloqueaveis, getResources().getString(R_INFORME_NOME_PRODUTO), 1000);
			return;
		}

		// validando preco do produto:
		try {
			precoProduto = Double.parseDouble(strPrecoProduto);
		} catch (Exception e) {
			Utilidades.Toasts.enviarToastSemTeclado(this, this.arrViewsBloqueaveis, getResources().getString(R_INFORME_PRECO_PRODUTO), 1000);
			return;
		}

		// validando pessoas vinculadas ao produto:
		if (app.getProdutoInfo().getPessoasVinculadas().size() == 0) {
			Utilidades.Teclado.ocultarTeclado(this);
			// exibe um dialog questionando ao usuario se ele deseja vincular todas as pessoas do evento ao produto,
			// ou se deseja escolher as pessoas vinculadas manualmente.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R_DIALOG_NINGUEM_VINCULADO_TITULO));
			builder.setMessage("\n" + getResources().getString(R_DIALOG_NINGUEM_VINCULADO_MENSAGEM) + "\n");
			builder.setPositiveButton(getResources().getString(R_DIALOG_NINGUEM_VINCULADO_BOTAO_POSITIVO), this);
			builder.setNegativeButton(getResources().getString(R_DIALOG_NINGUEM_VINCULADO_BOTAO_NEGATIVO), this);
			builder.setCancelable(false);
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}

		// produto valido, cadastrando:
		this.finalizarCadastroProdutoConsumo(nomeProduto, precoProduto, this.quantidadeProduto, this.app.getProdutoInfo()
				.getPessoasVinculadas());
	}

	/**
	 * Efetua os cadastros do produto e do consumo no evento, exibe ao usuario uma mensagem informando que
	 * o produto foi cadastrado e retorna para a activity principal do evento.
	 * @param nomeProduto O nome do produto que sera cadastrado com o consumo.
	 * @param precoProduto O preco do produto que sera cadastrado com o consumo.
	 * @param quantidade A quantidade do produto que sera cadastrado com o consumo.
	 * @param pessoasVinculadasConsumo A lista de pessoas vinculadas ao consumo do produto.
	 */
	private void finalizarCadastroProdutoConsumo(String nomeProduto, double precoProduto, int quantidade,
			List<Pessoa> pessoasVinculadasConsumo)
	{
		Produto produto = new Produto(nomeProduto, precoProduto);

		if (this.umProdutoParaCadaPessoa) {
			for (Pessoa pessoa : pessoasVinculadasConsumo) {
				Consumo consumo = new Consumo(produto, 1);
				consumo.vincularPessoa(pessoa);
				// adicionando o produto e o consumo ao evento:
				this.app.getEvento().adicionarConsumo(consumo);
				this.app.getEvento().adicionarProduto(produto);
			}
		} else {
			for (int i = 0; i < quantidade; i++) {
				// TODO: Aqui, no construtor do Consumo, vai a quantidade consumida do produto (ainda nao implementado).
				Consumo consumo = new Consumo(produto, 1);
				// vinculando as pessoas ao consumo:
				for (Pessoa pessoa : pessoasVinculadasConsumo) {
					consumo.vincularPessoa(pessoa);
				}
				// adicionando o produto e o consumo ao evento:
				this.app.getEvento().adicionarConsumo(consumo);
				this.app.getEvento().adicionarProduto(produto);
			}
		}

		Utilidades.Toasts.enviarToastSemTeclado(this, this.arrViewsBloqueaveis,
				getResources().getString(R_PRODUTO_CADASTRADO_COM_SUCESSO), 600);
		Utilidades.Navegacao.voltarComAtraso(this, 1400, R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	/**
	 * Atualiza as informacoes dos campos com os dados do produto que esta sendo cadastrado.
	 */
	private void atualizarInformacoes()
	{
		this.editText_nomeProduto.setText(this.app.getProdutoInfo().getNome());
		this.editText_precoProduto.setText(this.app.getProdutoInfo().getTextoPreco());

		TextView textView_quantidadeProduto = (TextView) findViewById(R.id.button_quantidade_produto_textview);
		TextView textView_umProdutoParaCadaPessoa = (TextView) findViewById(R.id.button_um_produto_para_cada_pessoa_textview);
		TextView textView_buttonVincularPessoas = (TextView) findViewById(R.id.button_vincular_pessoas_textview);
		LinearLayout linearLayout_umProdutoParaCadaPessoa = (LinearLayout) findViewById(R.id.button_um_produto_para_cada_pessoa);

		if (this.umProdutoParaCadaPessoa) {
			this.quantidadeProduto = this.app.getProdutoInfo().getPessoasVinculadas().size();
			textView_umProdutoParaCadaPessoa.setText(getResources().getString(
					R.string.act_cadastrar_produto_button_um_produto_para_cada_pessoa_sim));
			linearLayout_umProdutoParaCadaPessoa.setBackgroundResource(R.drawable.selector_listview_vincular_selected);
//			textView_quantidadeProduto.startAnimation(this.animZoom);
//			textView_buttonVincularPessoas.startAnimation(this.animZoom);
		} else {
			textView_umProdutoParaCadaPessoa.setText(getResources().getString(
					R.string.act_cadastrar_produto_button_um_produto_para_cada_pessoa_nao));
			linearLayout_umProdutoParaCadaPessoa.setBackgroundResource(R.drawable.selector_listview_vincular_unselected);
		}

		textView_quantidadeProduto.setText(String.valueOf(this.quantidadeProduto));

		textView_buttonVincularPessoas.setText(String.valueOf(this.app.getProdutoInfo().getPessoasVinculadas().size()));
	}

	/**
	 * Salva as informacoes atuais do produto no contexto da aplicacao.
	 */
	private void salvarProdutoInfo()
	{
		// armazenando as informacoes do produto no contexto da aplicacao:
		this.app.getProdutoInfo().setNome(this.editText_nomeProduto.getText().toString());
		this.app.getProdutoInfo().setTextoPreco(this.editText_precoProduto.getText().toString());
	}

	// [end] METODOS
}
