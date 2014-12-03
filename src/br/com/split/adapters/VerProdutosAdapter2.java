package br.com.split.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.contracts.IVerProdutosAdapterListener2;
import br.com.split.controller.ProdutoInfo;

public class VerProdutosAdapter2 extends ArrayAdapter<ProdutoInfo>
{
	// [start] DECLARACAO_VARIAVEIS
	private Context context;
	private int layoutResourceId;
	private List<ProdutoInfo> listProdutos;
	private IVerProdutosAdapterListener2 listener;

	// [end] DECLARACAO_VARIAVEIS


	// [start] CONSTRUTORES
	public VerProdutosAdapter2(Context context, int layoutResourceId, List<ProdutoInfo> produtos, IVerProdutosAdapterListener2 listener)
	{
		super(context, layoutResourceId, produtos);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.listProdutos = new ArrayList<ProdutoInfo>(produtos);
		this.listener = listener;
	}

	// [end] CONSTRUTORES


	public void atualizarListaProdutos(List<ProdutoInfo> produtos)
	{
		this.listProdutos = new ArrayList<ProdutoInfo>(produtos);
		this.notifyDataSetChanged();
	}


	// [start] METODOS_HERDADOS
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final RowHolder holder;
		final ProdutoInfo produto;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.textView_nomeProduto = (TextView) row.findViewById(R.id.textview_nome_produto);
			holder.textView_precoProduto = (TextView) row.findViewById(R.id.textview_preco_produto);
			holder.textView_qtdConsumidaProduto = (TextView) row.findViewById(R.id.textview_qtd_consumida_produto);
			holder.textView_valorTotalProduto = (TextView) row.findViewById(R.id.textview_valor_total_produto);
			holder.button_adicionar_produto = (ImageView) row.findViewById(R.id.button_adicionar_produto);

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

		produto = listProdutos.get(position);

		holder.textView_nomeProduto.setText(produto.getNome());
		holder.textView_precoProduto.setText(produto.getTextoPrecoFormatado());
		holder.textView_qtdConsumidaProduto.setText(produto.getTextoQuantidade());
		holder.textView_valorTotalProduto.setText(produto.getTextoTotalProdutoFormatado());
		holder.button_adicionar_produto.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				listener.onProdutoClicked(produto);
			}
		});

		row.setBackgroundResource(R.drawable.shape_list_ver_conta_normal);

		return row;
	}

	// [end] METODOS_HERDADOS


	// [start] INNER_CLASSES
	private static class RowHolder
	{
		TextView textView_nomeProduto;
		TextView textView_precoProduto;
		TextView textView_qtdConsumidaProduto;
		TextView textView_valorTotalProduto;
		ImageView button_adicionar_produto;
	}

	// [end] INNER_CLASSES
}
