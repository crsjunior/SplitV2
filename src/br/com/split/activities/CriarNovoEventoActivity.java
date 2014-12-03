package br.com.split.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.controller.SplitApplication;
import br.com.split.utilidades.Utilidades;

public class CriarNovoEventoActivity extends Activity
{
	public static final String TAG = "CriarNovoEventoActivity";
	private SplitApplication app;
	//	private List<String> listaDeParticipantes;

	private EditText editText_nomeEvento;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_criar_novo_evento);

		this.app = (SplitApplication) getApplicationContext();

		TextView textView_linhaDescricao = (TextView) findViewById(R.id.textview_linha_descricao);
		textView_linhaDescricao.setText("Crie um novo evento");

		this.editText_nomeEvento = (EditText) findViewById(R.id.edittext_nome_evento);

		this.app.suprimirTeclado(getWindow());

		Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_linha_descricao);
		textView_linhaDescricao.startAnimation(animZoom);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (this.app.getSelectedPlace() != null) {
			// somente seta o campo de nomo do evento com o nome do local obtido do fb:
			this.editText_nomeEvento.setText(this.app.getSelectedPlace().getName());
		}

		//		SplitApplication splitApp = (SplitApplication) getApplicationContext();
		//		if (splitApp.getSelectedPlace() != null) {
		//			splitApp.criarEvento(splitApp.getSelectedPlace().getName());
		//			Intent intentEvent = new Intent(this, EventoActivity.class);
		//			intentEvent.setFlags(intentEvent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//			startActivity(intentEvent);
		//		}
	}

	public void onClick(View view)
	{
		switch (view.getId()) {
			case R.id.button_criar_evento_facebook:
				abrirLocaisFacebook();
				break;
			case R.id.button_criar_evento:
				criarNovoEventoManual();
				break;
		}
	}

	private void abrirLocaisFacebook()
	{
		Intent intent = new Intent(this, FacebookEscolherLocalActivity.class);
		startActivityForResult(intent, 100);
	}

	private void criarNovoEventoManual()
	{
		String sNomeEvento = editText_nomeEvento.getText().toString().trim();

		if (sNomeEvento.equals("")) {
			Utilidades.Toasts.enviarToast(getApplicationContext(), "Informe o nome do evento.", 500);
			//			Utilidades.enviarToast(getApplicationContext(), "Informe o nome do evento.", 500);
			return;
		}

		this.app.criarEvento(sNomeEvento);

		Intent intent = new Intent(this, EventoActivity.class);
		// setando os flags para o intent:
		// - FLAG_ACTIVITY_NEW_TASK: Indica que a nova activity sera a nova raiz.
		// - FLAG_ACTIVITY_CLEAR_TASK: Limpa a lista de back stack.
		intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);

	}
}
