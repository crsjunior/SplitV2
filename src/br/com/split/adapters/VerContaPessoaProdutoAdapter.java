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
import br.com.split.model.Pessoa;

public class VerContaPessoaProdutoAdapter extends ArrayAdapter<Pessoa>
{
	private Context context;
	private int layoutResourceId;
	private List<Pessoa> listPessoas;

	public VerContaPessoaProdutoAdapter(Context context, int layoutResourceId, List<Pessoa> pessoas)
	{
		super(context, layoutResourceId, pessoas);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.listPessoas = pessoas;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final RowHolder holder;
		final Pessoa pessoa;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.textView_nomePessoa = (TextView) row.findViewById(R.id.textview_nome_pessoa);

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

		pessoa = listPessoas.get(position);

		holder.textView_nomePessoa.setText(pessoa.getNome());

		row.setBackgroundResource(R.drawable.shape_list_ver_conta_normal);

		return row;
	}

	private static class RowHolder
	{
		TextView textView_nomePessoa;
	}
}
