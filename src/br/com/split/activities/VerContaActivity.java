package br.com.split.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.adapters.VerContaAdapter;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Evento;
import br.com.split.model.Pessoa;
import br.com.split.utilidades.Globais.IntentExtras;

public class VerContaActivity extends Activity implements OnItemClickListener, OnItemSelectedListener
{
	public static final String TAG = "VerContaActivity";

	public static final int ORDENACAO_NOME = 0;
	public static final int ORDENACAO_QTD_PRODUTOS = 1;
	public static final int ORDENACAO_VALOR_CONTA = 2;

	private SplitApplication app;
	private VerContaAdapter adapter;
	private Spinner spinner_ordenacao;
	private ListView listView_verConta;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver_conta);

		this.app = (SplitApplication) getApplicationContext();
		Evento evento = app.getEvento();

		// calcula toda a conta (valor total, valor por pessoa, etc...):
		evento.calcularEvento();

		this.spinner_ordenacao = (Spinner) findViewById(R.id.spinner_ordenacao);
		this.listView_verConta = (ListView) findViewById(R.id.listview_ver_conta);

		// adapter do spinner_ordenacao:
		ArrayAdapter<CharSequence> adapterSpinnerOrdenacao = ArrayAdapter.createFromResource(this, R.array.ordenacao_ver_conta,
				android.R.layout.simple_spinner_item);
		adapterSpinnerOrdenacao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.spinner_ordenacao.setAdapter(adapterSpinnerOrdenacao);
		this.spinner_ordenacao.setOnItemSelectedListener(this);
		this.spinner_ordenacao.setSelection(this.app.getActVerContaOrdenacao());

		// adapter da listView_verConta:
		this.adapter = new VerContaAdapter(this, R.layout.listview_ver_conta, evento.getPessoas());
		this.listView_verConta.setAdapter(adapter);
		this.listView_verConta.setOnItemClickListener(this);

		// informando o nome do evento e o valor total da conta:
		((TextView) findViewById(R.id.textview_nome_evento)).setText(evento.getNome());
		((TextView) findViewById(R.id.textview_valor_total_conta)).setText(evento.getTextoValorConta());

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.ver_conta, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpTo(this, getIntent());
				return true;
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed()
	{
		NavUtils.navigateUpTo(this, getIntent());
		overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
	}

	/**
	 * Escuta e recebe os cliques ocorridos nos itens da ListView.
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(AdapterView, View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// recuperando a pessoa correspondente ao item da ListView que foi clicado:
		Pessoa pessoa = (Pessoa) this.listView_verConta.getItemAtPosition(position);

		// avancando para a activity VerContaPessoaActivity:
		Intent intent = new Intent(this, VerContaPessoaActivity.class);
		intent.putExtra(IntentExtras.PESSOA, pessoa);
		startActivity(intent);
		overridePendingTransition(R.anim.deslizar_abre_filho, R.anim.deslizar_fecha_pai);
	}

	/**
	 * Escuta e recebe as selecoes ocorridas no Spinner de ordenacao.
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(AdapterView, View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		this.app.setActVerContaOrdenacao(pos);
		this.adapter.reordenarLista(pos);
	}

	/**
	 * Escuta e recebe as selecoes ocorridas no Spinner de ordenacao.
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
	}

	/*
	 * Escuta e recebe os cliques nos botoes.
	 */
	public void onClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_info_conta:
				NavUtils.navigateUpTo(this, getIntent());
				overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
				break;
			case R.id.button_ver_conta_voltar:
				NavUtils.navigateUpTo(this, getIntent());
				overridePendingTransition(R.anim.esmaecer_abre_pai, R.anim.esmaecer_fecha_filho);
				break;
			case R.id.button_ver_conta_encerrar:

				break;
		}
	}
}
