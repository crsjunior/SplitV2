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
import br.com.split.controller.SplitApplication;
import br.com.split.model.Pessoa;

public class VincularProdutoPessoasAdapter extends ArrayAdapter<Pessoa>
{
	private Context context;
	private int layoutResourceId;
	private ArrayList<Pessoa> pessoas;
	private List<Pessoa> pessoasSelecionadas;

	public VincularProdutoPessoasAdapter(Context context, int layoutResourceId, ArrayList<Pessoa> itens)
	{
		super(context, layoutResourceId, itens);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.pessoas = itens;
		// a lista de pessoas selecionadas local (desta classe) eh uma copia da lista que foi passada pelo parametro,
		// e nao uma referencia, de forma que a lista que foi passada pelo parametro nao seja modificada aqui.
		SplitApplication app = (SplitApplication) context.getApplicationContext();
		this.pessoasSelecionadas = new ArrayList<Pessoa>(app.getProdutoInfo().getPessoasVinculadas());
	}

	/**
	 * Retorna a lista dos pessoas que foram selecionadas pelo usuario.
	 * @return A lista dos pessoas que foram selecionadas pelo usuario.
	 */
	public List<Pessoa> getPessoasSelecionadas()
	{
		return pessoasSelecionadas;
	}

	/**
	 * Vincula, ao produto, todas as pessoas do evento.
	 */
	public void vincularTodos()
	{
		this.pessoasSelecionadas = new ArrayList<Pessoa>(this.pessoas);
		notifyDataSetChanged();
	}

	/**
	 * Desvincula, do produto, todas as pessoas do evento.
	 */
	public void desvincularTodos()
	{
		this.pessoasSelecionadas = new ArrayList<Pessoa>();
		notifyDataSetChanged();
	}

	/**
	 * Modifica o layout da linha para selecionada.
	 * @param row A <code>View</code> da linha a ser modificada.
	 */
	private void selecionarRow(View row)
	{
		row.setBackgroundResource(R.drawable.selector_listview_vincular_selected);
		((RowHolder) row.getTag()).imageView_buttonVincularPessoa.setImageResource(R.drawable.ic_sinal_menos_2);
		//		RowHolder holder = (RowHolder) row.getTag();
		//		holder.imageView_buttonVincularPessoa.setImageResource(R.drawable.ic_sinal_menos_2);
	}

	/**
	 * Modifica o layout da linha para nao selecionada.
	 * @param row A <code>View</code> da linha a ser modificada.
	 */
	private void deselecionarRow(View row)
	{
		row.setBackgroundResource(R.drawable.selector_listview_vincular_unselected);
		((RowHolder) row.getTag()).imageView_buttonVincularPessoa.setImageResource(R.drawable.ic_sinal_mais_2);
		//		RowHolder holder = (RowHolder) row.getTag();
		//		holder.imageView_buttonVincularPessoa.setImageResource(R.drawable.ic_sinal_mais_2);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final RowHolder holder;
		final Pessoa pessoa;

		if (row == null) {
			// inflando o layout da linha:
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			// obtendo os widgets da linha:
			holder = new RowHolder();
			holder.textView_nomePessoa = (TextView) row.findViewById(R.id.textview_nome_pessoa);
			holder.imageView_buttonVincularPessoa = (ImageView) row.findViewById(R.id.imageview_button_vincular_pessoa);

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

		// obtendo a pessoa pertencente a linha:
		pessoa = pessoas.get(position);

		// verificando se a pessoa pertencente a linha eh uma pessoa selecionada:
		holder.linhaSelecionada = pessoasSelecionadas.contains(pessoa);

		// setando os dados da linha:
		holder.textView_nomePessoa.setText(pessoa.getNome());
		// verificando se a linha ja estava selecionada, ou nao, entao aplica o background correto a linha:
		if (holder.linhaSelecionada) {
			selecionarRow(row);
		} else {
			deselecionarRow(row);
		}

		// setando o listener do Button de selecao da linha:
		holder.imageView_buttonVincularPessoa.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				View parent = (View) view.getParent().getParent();
				if (holder.linhaSelecionada) {
					pessoasSelecionadas.remove(pessoa);
					deselecionarRow(parent);
				} else {
					pessoasSelecionadas.add(pessoa);
					selecionarRow(parent);
				}
				holder.linhaSelecionada = !holder.linhaSelecionada;
			}
		});

		return row;
	}

	private static class RowHolder
	{
		TextView textView_nomePessoa;
		ImageView imageView_buttonVincularPessoa;
		boolean linhaSelecionada;
	}
}
