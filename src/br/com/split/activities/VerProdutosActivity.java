package br.com.split.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.adapters.VerProdutosAdapter;
import br.com.split.contracts.IVerProdutosAdapterListener;
import br.com.split.controller.ProdutoInfo;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Consumo;
import br.com.split.model.Evento;
import br.com.split.model.Pessoa;
import br.com.split.model.Produto;
import br.com.split.model.ProdutoVerInfo;
import br.com.split.utilidades.Utilidades;

public class VerProdutosActivity extends Activity implements IVerProdutosAdapterListener
{
	// [start] DECLARACOES
	public static final String TAG = "VerProdutosActivity";

	private SplitApplication app;
	private VerProdutosAdapter adapter;
	private TextView textView_linhaDescricao;
	private Animation animZoom;
	private List<ProdutoVerInfo> produtos;
	private ProdutoVerInfo produto;
	private List<ProdutoInfo> produtosInfo;

	// [end] DECLARACOES


	// [start] METODOS_HERDADOS
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_produtos);

		this.app = (SplitApplication) getApplicationContext();

		this.produto = null;
		this.app.getProdutoInfo().setPessoasVinculadas(new ArrayList<Pessoa>());

		this.textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		this.textView_linhaDescricao.setText("Produtos consumidos no evento");

		//		TextView textView_totalProdutosNumero = (TextView) findViewById(R.id.textview_total_produtos_numero);
		//		TextView textView_totalProdutosTexto = (TextView) findViewById(R.id.textview_total_produtos_texto);
		//
		//		gerarListaInformacoesProdutos();
		//
		//		textView_totalProdutosNumero.setText(String.valueOf(this.produtos.size()));
		//		if (this.produtos.size() < 1) {
		//			textView_totalProdutosNumero.setVisibility(TextView.GONE);
		//			textView_totalProdutosTexto.setText("Nenhum produto consumido no evento");
		//		} else if (this.produtos.size() > 1) {
		//			textView_totalProdutosTexto.setText("produtos consumidos no evento");
		//		} else {
		//			textView_totalProdutosTexto.setText("produto consumido no evento");
		//		}

		this.adapter = new VerProdutosAdapter(this, R.layout.listview_ver_produtos_2, this.produtos, this);
		((ListView) findViewById(R.id.listview_ver_produtos)).setAdapter(this.adapter);

		this.animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		textView_linhaDescricao.startAnimation(this.animZoom);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		boolean atualizar = false;
		if (this.app.getProdutoInfo().getPessoasVinculadas().size() > 0) {
			atualizar = true;
			this.iniciarCadastroProdutoConsumo();
		}

		TextView textView_totalProdutosNumero = (TextView) findViewById(R.id.textview_total_produtos_numero);
		TextView textView_totalProdutosTexto = (TextView) findViewById(R.id.textview_total_produtos_texto);

		gerarListaInformacoesProdutos();

		textView_totalProdutosNumero.setText(String.valueOf(this.produtos.size()));
		if (this.produtos.size() < 1) {
			textView_totalProdutosNumero.setVisibility(TextView.GONE);
			textView_totalProdutosTexto.setText("Nenhum produto consumido no evento");
		} else if (this.produtos.size() > 1) {
			textView_totalProdutosTexto.setText("produtos consumidos no evento");
		} else {
			textView_totalProdutosTexto.setText("produto consumido no evento");
		}

		if (atualizar)
		{
			this.adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed()
	{
		this.voltarEvento();
	}

	@Override
	public void onProdutoClicked(ProdutoVerInfo produto)
	{
		this.produto = produto;
		this.app.getProdutoInfo().setNome(produto.getNome());
		this.app.getProdutoInfo().setTextoPreco(String.valueOf(produto.getPreco()));
		this.app.getProdutoInfo().setPessoasVinculadas(new ArrayList<Pessoa>());

		Intent intent = new Intent(VerProdutosActivity.this, VincularProdutoPessoasActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.deslizar_abre_filho, R.anim.deslizar_fecha_pai);
	}

	// [end] METODOS_HERDADOS


	// [start] METODOS
	public void onButtonClick(View view)
	{
		this.voltarEvento();
	}

	private void voltarEvento()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	private void gerarListaInformacoesProdutos()
	{
		Evento evento = this.app.getEvento();
		// compila uma lista contendo informacoes sobre cada produto do evento:
		// a classe ProdutoVerInfo armazena informacoes relevantes sobre um produto, que somente sao necessarias
		// durante a criacao e exibicao desta lista em especifico.
		this.produtos = new ArrayList<ProdutoVerInfo>();

		for (Consumo consumo : evento.getConsumos()) {
			// pesquisando pelo produto na lista atual:
			ProdutoVerInfo novoProduto = pesquisaListaProduto(consumo.getProduto().getNome(), consumo.getPrecoProduto());
			if (novoProduto != null) {
				// produto ja esta na lista, apenas incrementando sua quantidade para mais um:
				novoProduto.incrementarQuantidade();
			} else {
				// produto ainda nao esta na lista, adicionando-o:
				novoProduto = new ProdutoVerInfo(consumo.getProduto().getNome(), consumo.getPrecoProduto(), 1);
				this.produtos.add(novoProduto);
			}
		}
	}

	/**
	 * Pesquisa por um produto na lista de produtos desta classe por nome e preco.
	 * @param nome O nome pelo qual pesquisar o produto.
	 * @param preco O preco pelo qual pesquisar o produto.
	 * @return Se encontrado, um ProdutoVerInfo com as informacoes do produto, caso contrario, null.
	 */
	private ProdutoVerInfo pesquisaListaProduto(String nome, double preco)
	{
		for (ProdutoVerInfo p : this.produtos) {
			if (p.compararNomePreco(nome, preco)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Inicia o processo de cadastro do produto, validando o preenchimento dos campos do produto pelo usuario.
	 * Se tudo estiver ok, automaticamente chama o metodo que finaliza o cadastro,
	 * caso contrario, informa o usuario.
	 */
	private void iniciarCadastroProdutoConsumo()
	{
		// produto valido, cadastrando:
		Produto novoProduto = new Produto(this.produto.getNome(), this.produto.getPreco());
		// TODO: Aqui, no construtor do Consumo, vai a quantidade consumida do produto (ainda nao implementado).
		Consumo novoConsumo = new Consumo(novoProduto, 1);
		// vinculando as pessoas ao consumo:
		for (Pessoa pessoa : this.app.getProdutoInfo().getPessoasVinculadas()) {
			novoConsumo.vincularPessoa(pessoa);
		}
		// adicionando o produto e o consumo ao evento:
		this.app.getEvento().adicionarConsumo(novoConsumo);
		this.app.getEvento().adicionarProduto(novoProduto);

		this.produto = null;
		this.app.getProdutoInfo().setPessoasVinculadas(new ArrayList<Pessoa>());

		Utilidades.Toasts.enviarToastSemTeclado(this, "Produto adicionado.", 600);
	}

	// [end] METODOS
}
