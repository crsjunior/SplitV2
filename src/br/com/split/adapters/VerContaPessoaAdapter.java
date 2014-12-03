package br.com.split.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.model.Consumo;

public class VerContaPessoaAdapter extends ArrayAdapter<Consumo>
{
	private Context context;
	private int layoutResourceId;
	private List<Consumo> listConsumos;

	public VerContaPessoaAdapter(Context context, int layoutResourceId, List<Consumo> consumos)
	{
		super(context, layoutResourceId, consumos);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.listConsumos = consumos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final RowHolder holder;
		final Consumo consumo;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.textView_nomeProduto = (TextView) row.findViewById(R.id.textview_nome_produto);
			holder.textView_precoProduto = (TextView) row.findViewById(R.id.textview_preco_produto);
			holder.textView_qtdPessoasVinculadas = (TextView) row.findViewById(R.id.textview_qtd_pessoas_vinculadas);
			holder.textView_precoPorPessoa = (TextView) row.findViewById(R.id.textview_preco_por_pessoa);

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

//		produto = listProdutos.get(position);
		consumo = listConsumos.get(position);
		

		holder.textView_nomeProduto.setText(consumo.getProduto().getNome());
		holder.textView_qtdPessoasVinculadas.setText(String.valueOf(consumo.getPessoas().size()));
		holder.textView_precoPorPessoa.setText(consumo.getTextoValorPorPessoa());
		
		//		holder.textView_precoProduto.setText(produto.getTextoPreco());
		//		holder.textView_qtdPessoasVinculadas.setText(String.valueOf(produto.getQtdPessoas()));
		//		holder.textView_precoPorPessoa.setText(produto.getTextoMediaPreco());

		row.setBackgroundResource(R.drawable.selector_list_ver_conta);

		return row;
	}

	private static class RowHolder
	{
		TextView textView_nomeProduto;
		TextView textView_precoProduto;
		TextView textView_qtdPessoasVinculadas;
		TextView textView_precoPorPessoa;
	}
}
