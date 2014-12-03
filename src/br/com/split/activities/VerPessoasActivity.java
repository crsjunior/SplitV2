package br.com.split.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.adapters.VerPessoasAdapter;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Evento;

public class VerPessoasActivity extends Activity
{
	private SplitApplication app;
	private VerPessoasAdapter adapter;
	private TextView textView_linhaDescricao;
	private Animation animZoom;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_pessoas);

		this.app = (SplitApplication) getApplicationContext();
		Evento evento = this.app.getEvento();

		this.textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		this.textView_linhaDescricao.setText("Pessoas participando do evento");

		TextView textView_totalPessoasNumero = (TextView) findViewById(R.id.textview_total_pessoas_numero);
		TextView textView_totalPessoasTexto = (TextView) findViewById(R.id.textview_total_pessoas_texto);

		textView_totalPessoasNumero.setText(String.valueOf(evento.getQtdPessoas()));
		if (evento.getQtdPessoas() < 1) {
			textView_totalPessoasNumero.setVisibility(TextView.GONE);
			textView_totalPessoasTexto.setText("Nenhuma pessoa participando do evento");
		} else if (evento.getQtdPessoas() > 1) {
			textView_totalPessoasTexto.setText("pessoas participando do evento");
		} else {
			textView_totalPessoasTexto.setText("pessoa participando do evento");
		}

		this.adapter = new VerPessoasAdapter(this, R.layout.listview_ver_pessoas, evento.getPessoas());
		((ListView) findViewById(R.id.listview_ver_pessoas)).setAdapter(adapter);

		this.animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);

		textView_linhaDescricao.startAnimation(this.animZoom);
	}

	@Override
	public void onBackPressed()
	{
		this.voltarEvento();
	}

	public void onButtonClick(View view)
	{
		this.voltarEvento();
	}

	private void voltarEvento()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}
}
