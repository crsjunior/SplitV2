package br.com.split.controles;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;


public class NumberDialog extends DialogFragment
{
	// [start] DECALRACAO_DE_VARIAVEIS
	private static final String ARG_QUANTIDADE_NUMEROS = "QuantidadeNumeros";
	private static final String ARG_VALOR_INICIAL = "ValorInicial";
	private static final String VALOR_ATUAL = "ValorAtual";

	private NumberPicker[] numberPickers;
	private int quantidadeNumeros;
	private int valorAtual;

	private OnNumberDialogListener mListener;

	// [end] DECALRACAO_DE_VARIAVEIS


	// [start] CONSTRUTORES
	public NumberDialog()
	{
	}

	public static NumberDialog newInstance(int numeroDials, int valorInicial)
	{
		NumberDialog numberDialog = new NumberDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_QUANTIDADE_NUMEROS, numeroDials);
		args.putInt(ARG_VALOR_INICIAL, valorInicial);
		numberDialog.setArguments(args);
		return numberDialog;
	}

	// [end] CONSTRUTORES


	// [start] METODOS_HERDADOS
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			this.quantidadeNumeros = getArguments().getInt(ARG_QUANTIDADE_NUMEROS);
			this.valorAtual = getArguments().getInt(ARG_VALOR_INICIAL);
			this.numberPickers = new NumberPicker[this.quantidadeNumeros];
		}

		if (savedInstanceState != null) {
			this.valorAtual = savedInstanceState.getInt(VALOR_ATUAL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState)
	{
		//		getDialog().setTitle("Quantidade do produto:");
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setCancelable(false);

		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		int width = (int) (metrics.widthPixels * 0.8);
		//		getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout.LayoutParams params;

		TextView textView_titulo = new TextView(getActivity());
		params = novoLayoutParams();
		params.width = width;
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = 10;
		params.bottomMargin = 10;
		textView_titulo.setLayoutParams(params);
		textView_titulo.setPadding(0, 10, 0, 10);
		textView_titulo.setTextSize(16);
		textView_titulo.setGravity(Gravity.CENTER_HORIZONTAL);
		textView_titulo.setText("Quantidade do produto");

		// criando o LinearLayout (horizontal) que contera os NumberPickers:
		LinearLayout linearLayout_numberPickers = new LinearLayout(getActivity());
		params = novoLayoutParams();
		params.gravity = Gravity.CENTER_HORIZONTAL;
		linearLayout_numberPickers.setLayoutParams(params);
		linearLayout_numberPickers.setOrientation(LinearLayout.HORIZONTAL);

		// criando os NumberPickers:
		int posicao;
		for (int i = 0; i < this.quantidadeNumeros; i++) {
			posicao = this.quantidadeNumeros - i - 1;
			this.numberPickers[posicao] = new NumberPicker(getActivity());
			this.numberPickers[posicao].setMinValue(0);
			this.numberPickers[posicao].setMaxValue(9);
			// previne a exibicao do teclado quando o NumberPicker for clicado:
			this.numberPickers[posicao].setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			this.numberPickers[posicao].setValue(this.getDigito(this.valorAtual, posicao));
			linearLayout_numberPickers.addView(this.numberPickers[posicao]);
		}

		// criando o LinearLayout (horizontal) que contera os Buttons:
		LinearLayout linearLayout_buttons = new LinearLayout(getActivity());
		params = novoLayoutParams();
		params.width = LinearLayout.LayoutParams.MATCH_PARENT;
		linearLayout_buttons.setLayoutParams(params);
		linearLayout_buttons.setOrientation(LinearLayout.HORIZONTAL);

		// criando o LinearLayout (vertical) principal, que contera todo o Dialog:
		LinearLayout linearLayout_principal = new LinearLayout(getActivity());
		params = novoLayoutParams();
		linearLayout_principal.setLayoutParams(params);
		linearLayout_principal.setOrientation(LinearLayout.VERTICAL);
		linearLayout_principal.addView(textView_titulo);
		linearLayout_principal.addView(linearLayout_numberPickers);
		linearLayout_principal.addView(linearLayout_buttons);

		// criando o Button Cancel:
		Button button_cancel = new Button(getActivity());
		params = novoLayoutParams();
		params.width = 0;
		params.weight = 0.5f;
		button_cancel.setLayoutParams(params);
		button_cancel.setText("Cancelar");
		button_cancel.setSingleLine(true);
		button_cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (mListener != null) {
					mListener.onNumberDialogButtonCancelClick();
				}
				dismiss();
			}
		});
		linearLayout_buttons.addView(button_cancel);

		// criando o Button Ok:
		Button button_ok = new Button(getActivity());
		button_ok.setLayoutParams(params);
		button_ok.setText("Concluído");
		button_ok.setSingleLine(true);
		button_ok.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				valorAtual = getValor();
				if (mListener != null) {
					mListener.onNumberDialogButtonDoneClick(valorAtual);
				}
				dismiss();
			}
		});
		linearLayout_buttons.addView(button_ok);

		return linearLayout_principal;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try {
			this.mListener = (OnNumberDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNumberDialogListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		this.mListener = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putInt(VALOR_ATUAL, getValor());
	}

	// [end] METODOS_HERDADOS


	// [start] METODOS_PRIVADOS
	private LinearLayout.LayoutParams novoLayoutParams()
	{
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	private int getValor()
	{
		int valor = 0;
		int mult = 1;

		for (int i = 0; i < this.quantidadeNumeros; i++) {
			valor += this.numberPickers[i].getValue() * mult;
			mult *= 10;
		}

		return valor;
	}

	private int getDigito(int valor, int posicao)
	{
		String temp = Integer.toString(valor);

		if (temp.length() <= posicao) {
			return 0;
		}

		return Character.getNumericValue(temp.charAt(temp.length() - posicao - 1));
	}

	// [end] METODOS_PRIVADOS


	// [start] INTERFACES
	public interface OnNumberDialogListener
	{
		/**
		 * Este metodo eh chamado quando o Button Done for clicado.
		 * @param valor O valor selecionado no NumberDialog.
		 */
		abstract public void onNumberDialogButtonDoneClick(int valor);

		/**
		 * Este metodo eh chamado quando o Button Cancel for clicado.
		 */
		abstract public void onNumberDialogButtonCancelClick();
	}

	// [end] INTERFACES
}
