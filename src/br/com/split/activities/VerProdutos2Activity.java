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
import br.com.split.adapters.VerProdutosAdapter2;
import br.com.split.contracts.IVerProdutosAdapterListener2;
import br.com.split.controller.ProdutoInfo;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Consumo;
import br.com.split.model.Evento;
import br.com.split.model.Pessoa;
import br.com.split.model.Produto;
import br.com.split.utilidades.Utilidades;

public class VerProdutos2Activity extends Activity implements IVerProdutosAdapterListener2
{
	// [start] DECLARACAO_VARIAVEIS
	public static final String TAG = "VerProdutos2Activity";

	private SplitApplication app;
	private VerProdutosAdapter2 adapter;
	private List<ProdutoInfo> produtos;
	private ProdutoInfo produtoInfo;

	// [end] DECLARACAO_VARIAVEIS


	// [start] METODOS_HERDADOS
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_produtos2);

		this.app = (SplitApplication) getApplicationContext();

		TextView textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		textView_linhaDescricao.setText("Produtos consumidos no evento");

		atualizarListaInformacoesProdutos();

		this.adapter = new VerProdutosAdapter2(this, R.layout.listview_ver_produtos_2, this.produtos, this);
		((ListView) findViewById(R.id.listview_ver_produtos)).setAdapter(this.adapter);

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);
		textView_linhaDescricao.startAnimation(animZoom);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if (this.app.getProdutoInfo().getPessoasVinculadas().size() > 0) {
			iniciarCadastroProdutoConsumo();
			atualizarListaInformacoesProdutos();
			this.adapter.atualizarListaProdutos(this.produtos);
		}
	}

	@Override
	public void onBackPressed()
	{
		voltarEvento();
	}

	// [end] METODOS_HERDADOS


	// [start] LISTENERS
	@Override
	public void onProdutoClicked(ProdutoInfo produto)
	{
		this.produtoInfo = produto;

		this.app.getProdutoInfo().setNome(produto.getNome());
		this.app.getProdutoInfo().setPreco(produto.getPreco());
		this.app.getProdutoInfo().setPessoasVinculadas(new ArrayList<Pessoa>());

		Intent intent = new Intent(VerProdutos2Activity.this, VincularProdutoPessoasActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.deslizar_abre_filho, R.anim.deslizar_fecha_pai);
	}

	public void onButtonClick(View view)
	{
		voltarEvento();
	}

	// [end] LISTENERS


	// [start] METODOS
	private void iniciarCadastroProdutoConsumo()
	{
		// produto valido, cadastrando:
		Produto novoProduto = new Produto(this.produtoInfo.getNome(), this.produtoInfo.getPreco());
		// TODO: Aqui, no construtor do Consumo, vai a quantidade consumida do produto (ainda nao implementado).
		Consumo novoConsumo = new Consumo(novoProduto, 1);
		// vinculando as pessoas ao consumo:
		for (Pessoa pessoa : this.app.getProdutoInfo().getPessoasVinculadas()) {
			novoConsumo.vincularPessoa(pessoa);
		}
		// adicionando o produto e o consumo ao evento:
		this.app.getEvento().adicionarConsumo(novoConsumo);
		this.app.getEvento().adicionarProduto(novoProduto);

		this.produtoInfo = null;
		this.app.getProdutoInfo().setPessoasVinculadas(new ArrayList<Pessoa>());

		Utilidades.Toasts.enviarToastSemTeclado(this, "Produto adicionado.", 600);
	}

	private void atualizarListaInformacoesProdutos()
	{
		Evento evento = this.app.getEvento();

		this.produtos = new ArrayList<ProdutoInfo>();

		for (Consumo consumo : evento.getConsumos()) {
			ProdutoInfo novoProduto = pesquisarListaProdutos(consumo.getProduto().getNome(), consumo.getPrecoProduto());
			if (novoProduto != null) {
				novoProduto.incrementarQuantidade();
			} else {
				novoProduto = new ProdutoInfo(consumo.getProduto().getNome(), consumo.getPrecoProduto(), 1);
				this.produtos.add(novoProduto);
			}
		}

		TextView textView_totalProdutosNumero = (TextView) findViewById(R.id.textview_total_produtos_numero);
		TextView textView_totalProdutosTexto = (TextView) findViewById(R.id.textview_total_produtos_texto);

		textView_totalProdutosNumero.setText(String.valueOf(this.produtos.size()));
		if (this.produtos.size() < 1) {
			textView_totalProdutosNumero.setVisibility(TextView.GONE);
			textView_totalProdutosTexto.setText("Nenhum produto consumido no evento");
		} else if (this.produtos.size() > 1) {
			textView_totalProdutosTexto.setText("produtos consumidos no evento");
		} else {
			textView_totalProdutosTexto.setText("produto consumido no evento");
		}
	}

	private ProdutoInfo pesquisarListaProdutos(String nome, double preco)
	{
		for (ProdutoInfo produtoInfo : this.produtos) {
			if (produtoInfo.compararNomePreco(nome, preco)) {
				return produtoInfo;
			}
		}
		return null;
	}

	private void voltarEvento()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	// [end] METODOS
}
