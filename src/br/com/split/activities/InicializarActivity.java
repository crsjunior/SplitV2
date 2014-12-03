package br.com.split.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.controller.DatabaseHelper;
import br.com.split.utilidades.Utilidades;

public class InicializarActivity extends Activity
{
	public static final String TAG = "InicializarActivity";

	// array dos controles (views) da activity que serao bloqueadas quando necessario,
	// de forma a impedir o usuario de interagir com elas:
	private View[] arrViewsBloqueaveis;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicializar);

		((TextView) findViewById(R.id.split_logo_title))
				.setText(Html.fromHtml(getResources().getString(R.string.formatted_splash_text)));

		this.arrViewsBloqueaveis = new View[] {
				(Button) findViewById(R.id.button_novo_evento),
				(Button) findViewById(R.id.button_conta_simples)
		};

		initializeSplashScreenCycle();

		// testes de banco de dados:
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		Log.e(TAG, dbHelper.getDatabaseName());
	}

	@Override
	public void onBackPressed()
	{
		// minimizando o aplicativo programaticamente (evitando que a splash screen seja reexibida quando
		// restaurada, ou que o aplicativo volte para a parent intent):
		Utilidades.Aplicativo.minizarAplicativo(this);
		
		
		
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
//		getApplicationContext().startActivity(intent);
	}

	public void onButtonClick(View view)
	{
		Intent intent;
		switch (view.getId()) {
			case R.id.button_novo_evento:
				intent = new Intent(InicializarActivity.this, MainSplitActivity.class);
				startActivity(intent);
				break;
			case R.id.button_conta_simples:
				intent = new Intent(InicializarActivity.this, ContaSimplesActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	/**
	 * Inicia o ciclo de exibicao temporaria da splash screen, seguindo com a animacao
	 * de transicao para exibir o conteudo principal da activity.
	 */
	private void initializeSplashScreenCycle()
	{
		// desabilitando os botoes antes de iniciar a animacao da splash screen:
		Utilidades.Views.disableViews(this.arrViewsBloqueaveis);

		Animation animLayoutSplitLogo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_move);
		Animation animLayoutContent = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_fade_in);

		animLayoutContent.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// rehabilitando os botoes depois de finalizar a animacao da splash screen:
				Utilidades.Views.enableViews(arrViewsBloqueaveis);
			}
		});

		((RelativeLayout) findViewById(R.id.layout_split_logo)).startAnimation(animLayoutSplitLogo);
		((RelativeLayout) findViewById(R.id.layout_content)).startAnimation(animLayoutContent);
	}
}
