package br.com.split.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.adapters.VincularProdutoPessoasAdapter;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Pessoa;
import br.com.split.model.ProdutoVerInfo;

public class VincularProdutoPessoasActivity extends Activity implements DialogInterface.OnClickListener
{
	public static final String PRODUTO_VER_INFO = "ProdutoVerInfo";

	private SplitApplication app;
	private VincularProdutoPessoasAdapter adapter;

	private ProdutoVerInfo produtoInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vincular_produto_pessoas);

		this.app = (SplitApplication) getApplicationContext();

		this.produtoInfo = null;
		// caso exista uma intent, significa que um produto esta sendo recadastrado:
		try {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			this.produtoInfo = (ProdutoVerInfo) bundle.getSerializable(PRODUTO_VER_INFO);
		} catch (Exception e) {
		}

		RelativeLayout layout_infoProduto = (RelativeLayout) findViewById(R.id.layout_info_produto);
		if (this.produtoInfo == null) {
			// nao eh um recadastro de produto, escondendo a linha de informacoes do produto:
			layout_infoProduto.setVisibility(RelativeLayout.GONE);
		} else {
			// eh um recadastro de produto, exibindo e preenchendo a linha de informacoes do produto:
			layout_infoProduto.setVisibility(RelativeLayout.VISIBLE);
			((TextView) findViewById(R.id.textview_nome_produto)).setText(this.produtoInfo.getNome());
			((TextView) findViewById(R.id.textview_preco_produto)).setText(this.produtoInfo.getTextoPreco());
		}

		TextView textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		textView_linhaDescricao.setText("Selecione as pessoas que consumirão este produto");

		// criando e setando o adapter para a listview:
		this.adapter = new VincularProdutoPessoasAdapter(this,
				R.layout.listview_vincular_produto_pessoas,
				(ArrayList<Pessoa>) this.app.getEvento().getPessoas());
		((ListView) findViewById(R.id.listview_vincular_produto_pessoas)).setAdapter(this.adapter);

		Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		textView_linhaDescricao.startAnimation(animZoom);
	}

	@Override
	public void onBackPressed()
	{
		// verificando se eh um cadastro de novo produto, ou um recadastro de produto:
		if (this.produtoInfo == null) {
			this.cadastrandoNovoProduto();
		} else {
			this.recadastrandoProduto();
		}
	}

	private void cadastrandoNovoProduto()
	{
		boolean iguais = true;
		// verificando alteracoes na lista de pessoas vinculadas:
		for (Pessoa pessoa : app.getProdutoInfo().getPessoasVinculadas()) {
			if (!adapter.getPessoasSelecionadas().contains(pessoa)) {
				iguais = false;
				break;
			}
		}
		if (iguais) {
			for (Pessoa pessoa : adapter.getPessoasSelecionadas()) {
				if (!app.getProdutoInfo().getPessoasVinculadas().contains(pessoa)) {
					iguais = false;
					break;
				}
			}
		}

		if (iguais) {
			voltarParaPai();
		} else { // houve alteracao na lista de pessoas vinculadas.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.vincluar_produto_pessoas_dialog_titulo));
			builder.setMessage("\n" + getResources().getString(R.string.vincluar_produto_pessoas_dialog_mensagem) + "\n");
			builder.setPositiveButton(getResources().getString(R.string.vincluar_produto_pessoas_dialog_botao_positivo), this);
			builder.setNegativeButton(getResources().getString(R.string.vincluar_produto_pessoas_dialog_botao_negativo), this);
			builder.setCancelable(false);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void recadastrandoProduto()
	{
		voltarParaPai();
	}

	public void onButtonClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_vincular_produto_pessoas_concluido:
				app.getProdutoInfo().setPessoasVinculadas(adapter.getPessoasSelecionadas());
				voltarParaPai();
				break;
			case R.id.button_vincular_produto_pessoas_selecionar_todos:
				this.adapter.vincularTodos();
				break;
			case R.id.button_vincular_produto_pessoas_deselecionar_todos:
				this.adapter.desvincularTodos();
				break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				app.getProdutoInfo().setPessoasVinculadas(adapter.getPessoasSelecionadas());
				break;
			case DialogInterface.BUTTON_NEGATIVE:

				break;
		}
		voltarParaPai();
	}

	private void voltarParaPai()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.deslizar_abre_pai, R.anim.deslizar_fecha_filho);
	}
}
