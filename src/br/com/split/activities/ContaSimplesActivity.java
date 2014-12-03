package br.com.split.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.utilidades.MonetaryMask;

public class ContaSimplesActivity extends Activity
{
	private EditText editValorTotal;
	private EditText editNumeroPessoas;
	private TextView totalPagar;
	private CheckBox checkGorjeta;
	private Button btnCalcular;
	private MonetaryMask mask;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conta_simples);
		editValorTotal = (EditText) findViewById(R.id.edit_valor_total);
		editNumeroPessoas = (EditText) findViewById(R.id.edit_numero_pessoas);
		totalPagar = (TextView) findViewById(R.id.label_resultado_pagar);
		checkGorjeta = (CheckBox) findViewById(R.id.checkBox_com_txa_servico);
		btnCalcular = (Button) findViewById(R.id.button_calcular);
		btnCalcular.setEnabled(true);
		mask = new MonetaryMask(editValorTotal);
		editValorTotal.addTextChangedListener(mask);

		totalPagar.setText("R$ 0.00");
	}

	public void calcular(View view)
	{
		String str = editValorTotal.getText().toString();
		boolean hasMask = (str.indexOf("R$") >= 0 && str.indexOf(".") >= 0 && str
				.indexOf(",") >= 0)
				|| (str.indexOf("R$") >= 0 && str.indexOf(",") >= 0);

		if (hasMask) {
			str = str.replaceAll("[R$]", "").replaceAll("[.]", "")
					.replaceAll("[,]", "");
		}

		Double billAmount = Double.parseDouble(str);
		Double totalPeople = Double.parseDouble(editNumeroPessoas.getText()
				.toString());
		billAmount = billAmount / 100;
		Log.d("calculo", String.valueOf(totalPeople));
		Log.d("calculo", String.valueOf(billAmount));
		Double percentage = null;
		boolean isError = false;
		if (billAmount < 1.0) {
			showErrorAlert("Enter a valid Total Amount.",
					editValorTotal.getId());
			isError = true;
		}
		if (totalPeople < 1.0) {
			showErrorAlert("Enter a valid value for No. of People.",
					editNumeroPessoas.getId());
			isError = true;
		}
		/*
		 * Se o usuário nunca modificar sua seleção de opção, significa que a
		 * seleção padrão de 10% está em efeito. Mas é mais seguro verificar
		 */
		if (checkGorjeta.isChecked()) {
			percentage = 10.00;
		} else {
			percentage = 1.00;
		}
		/*
		 * Se todos os campos estiverem preenchidos com valores válidos,
		 * prossiga para o cálculo das gorjetas
		 */
		if (!isError) {
			Double totalToPay = 0.0;
			Double tipAmount = ((billAmount * percentage) / 100);
			if (checkGorjeta.isChecked()) {
				totalToPay = billAmount + tipAmount;
			} else {
				totalToPay = billAmount;
			}
			Double perPersonPays = totalToPay / totalPeople;
			totalPagar.setText("R$" + String.valueOf(round(perPersonPays, 2)));
		}
	}

	public static double round(double value, int places)
	{
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	private void showErrorAlert(String errorMessage, final int fieldId)
	{
		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(errorMessage)
				.setNeutralButton("Close",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								findViewById(fieldId).requestFocus();
							}
						}).show();
	}
}
