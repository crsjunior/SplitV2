package br.com.split.utilidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class Utilidades
{
	private static final String TAG = "Utilidades";

	public static class Views
	{
		/**
		 * Desabilita uma <code>View</code>.
		 * @param view A <code>View</code> que a ser desabilitada.
		 */
		public static void disableView(View view)
		{
			view.setEnabled(false);
		}

		/**
		 * Habilita uma <code>View</code>.
		 * @param view A <code>View</code> que a ser habilitada.
		 */
		public static void enableView(View view)
		{
			view.setEnabled(true);
		}

		/**
		 * Desabilita todas as <code>View</code> contidas na array.
		 * @param views A array das <code>View</code> que serao desabilitatas.
		 */
		public static void disableViews(View[] views)
		{
			for (View view : views) {
				view.setEnabled(false);
			}
		}

		/**
		 * Habilita todas as <code>View</code> contidas na array.
		 * @param views A array das <code>View</code> que serao habilitadas.
		 */
		public static void enableViews(View[] views)
		{
			for (View view : views) {
				view.setEnabled(true);
			}
		}
	}

	public static class Teclado
	{
		/**
		 * Oculta o teclado, caso este esteja sendo exibido.
		 * @param activity A activity da qual ocultar o teclado.
		 */
		public static void ocultarTeclado(Activity activity)
		{
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			View view = activity.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}

		/**
		 * Exibe o teclado, caso este nao esteja sendo exibido.
		 * @param activity A activity na qual exibir o teclado.
		 */
		public static void exibirTeclado(Activity activity)
		{
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			View view = activity.getCurrentFocus();
			if (view != null) {
				imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
			}
		}

		/**
		 * Exibe o teclado, caso este nao esteja sendo exibido, depois de um intervalo de tempo estabelecido.
		 * @param activity A activity na qual exibir o teclado.
		 * @param tempoAtraso O total de tempo antes de exibir o teclado.
		 */
		public static void exibirTecladoComAtraso(final Activity activity, int tempoAtraso)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					exibirTeclado(activity);
				}
			}, tempoAtraso);
		}
	}

	public static class Toasts
	{
		private static final int TEMPO_OCULTACAO_TECLADO = 200;
		private static final int TEMPO_OCULTACAO_TOAST = 600;

		/**
		 * Exibe um <code>Toast</code> contendo uma mensagem que fica visivel durante um intervalo de tempo estabelecido.
		 * @param context O contexto do qual sera enviada a mensagem.
		 * @param mensagem O texto da mensagem do <code>Toast</code>.
		 * @param tempoExibicao O total de tempo de exibicao do <code>Toast</code>.
		 */
		public static void enviarToast(Context context, String mensagem, int tempoExibicao)
		{
			final Toast toast = Toast.makeText(context, mensagem, Toast.LENGTH_LONG);
			toast.show();

			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					toast.cancel();
				}
			}, tempoExibicao);
		}

		/**
		 * Exibe um <code>Toast</code> contendo uma mensagem, depois de um intervalo de tempo estabelecido, e que fica
		 * visivel durante um intervalo de tempo estabelecido.
		 * @param context O contexto do qual sera enviada a mensagem.
		 * @param mensagem O texto da mensagem do <code>Toast</code>.
		 * @param tempoExibicao O total de tempo de exibicao do <code>Toast</code>.
		 * @param tempoAtraso O total de tempo antes de exibir o <code>Toast</code>.
		 */
		public static void enviarToast(final Context context, final String mensagem, final int tempoExibicao, int tempoAtraso)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					enviarToast(context, mensagem, tempoExibicao);
				}
			}, tempoAtraso);
		}

		/**
		 * Exibe um <code>Toast</code> contendo uma mensagem que fica visivel durante um intervalo de tempo estabelecido,
		 * ocultando o teclado durante a sua exibicao.
		 * @param context O contexto do qual sera enviada a mensagem.
		 * @param mensagem O texto da mensagem do <code>Toast</code>.
		 * @param tempoExibicao O total de tempo de exibicao do <code>Toast</code>.
		 */
		public static void enviarToastSemTeclado(Context context, String mensagem, int tempoExibicao)
		{
			Activity activity = (Activity) context;

			Teclado.ocultarTeclado(activity);
			enviarToast(context, mensagem, tempoExibicao, TEMPO_OCULTACAO_TECLADO);
			Teclado.exibirTecladoComAtraso(activity, TEMPO_OCULTACAO_TECLADO + TEMPO_OCULTACAO_TOAST + tempoExibicao);
		}

		/**
		 * Exibe um <code>Toast</code> contendo uma mensagem que fica visivel durante um intervalo de tempo estabelecido,
		 * ocultando o teclado e bloqueando a <code>View</code> durante a sua exibicao.
		 * @param context O contexto do qual sera enviada a mensagem.
		 * @param view A <code>View</code> que sera bloqueada durante a exibicao do <code>Toast</code>;
		 * @param mensagem O texto da mensagem do <code>Toast</code>.
		 * @param tempoExibicao O total de tempo de exibicao do <code>Toast</code>.
		 */
		public static void enviarToastSemTeclado(Context context, View view, String mensagem, int tempoExibicao)
		{
			enviarToastSemTeclado(context, new View[] { view }, mensagem, tempoExibicao);
		}

		/**
		 * Exibe um <code>Toast</code> contendo uma mensagem que fica visivel durante um intervalo de tempo estabelecido,
		 * ocultando o teclado e bloqueando todas as <code>View</code> da array durante a sua exibicao.
		 * @param context O contexto do qual sera enviada a mensagem.
		 * @param views A array de <code>View</code> que serao bloqueadas durante a exibicao do <code>Toast</code>;
		 * @param mensagem O texto da mensagem do <code>Toast</code>.
		 * @param tempoExibicao O total de tempo de exibicao do <code>Toast</code>.
		 */
		public static void enviarToastSemTeclado(Context context, final View[] views, String mensagem, int tempoExibicao)
		{
			Views.disableViews(views);

			enviarToastSemTeclado(context, mensagem, tempoExibicao);

			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					Views.enableViews(views);
				}
			}, TEMPO_OCULTACAO_TECLADO + TEMPO_OCULTACAO_TOAST + tempoExibicao);
		}
	}

	public static class Navegacao
	{
		public static final int TEMPO_PADRAO_ATRASO = 100;

		/**
		 * Volta para a <code>Activity</code> pai, depois de um intervalo de tempo estabelecido, podendo exibir uma
		 * animacao de transicao personalizada.
		 * @param activity A activity atual.
		 * @param tempoAtraso O intervalo de tempo antes de voltar para a activity pai.
		 * @param enterAnim A animacao de transicao de entrada (0 para nenhuma).
		 * @param exitAnim A animacao de transicao de saida (0 para nenhuma).
		 */
		public static void voltarComAtraso(final Activity activity, int tempoAtraso, final int enterAnim, final int exitAnim)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					NavUtils.navigateUpTo(activity, activity.getIntent());
					if (enterAnim > 0 && exitAnim > 0) {
						activity.overridePendingTransition(enterAnim, exitAnim);
					}
				}
			}, tempoAtraso);
		}

		/**
		 * Volta para a <code>Activity</code> pai, depois de um intervalo de tempo estabelecido.
		 * @param activity A activity atual.
		 * @param tempoAtraso O intervalo de tempo antes de voltar para a activity pai.
		 */
		public static void voltarComAtraso(Activity activity, int tempoAtraso)
		{
			voltarComAtraso(activity, tempoAtraso, 0, 0);
		}

		/**
		 * Abre uma activity a partir de uma intent, depois de um intervalo de tempo de 100ms, podendo exibir uma animacao
		 * de transicao personalizada.
		 * @param activity A activity atual.
		 * @param intent A intent para a activity que sera aberta.
		 * @param tempoAtraso O intervalo de tempo antes de abrir a activity.
		 * @param enterAnim A animacao de transicao de entrada (0 para nenhuma).
		 * @param exitAnim A animacao de transicao de saida (0 para nenhuma).
		 */
		public static void abrirComAtraso(final Activity activity, final Intent intent, int tempoAtraso, final int enterAnim,
				final int exitAnim)
		{
			Teclado.ocultarTeclado(activity);

			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					activity.startActivity(intent);
					if (enterAnim > 0 && exitAnim > 0) {
						activity.overridePendingTransition(enterAnim, exitAnim);
					}
				}
			}, tempoAtraso);
		}

		/**
		 * Abre uma activity a partir de uma intent, depois de um intervalo de tempo de 100ms.
		 * @param activity A activity atual.
		 * @param intent A intent para a activity que sera aberta.
		 * @param tempoAtraso O intervalo de tempo antes de abrir a activity.
		 */
		public static void abrirComAtraso(Activity activity, Intent intent, int tempoAtraso)
		{
			abrirComAtraso(activity, intent, tempoAtraso, 0, 0);
		}
	}

	public static class Aplicativo
	{
		/**
		 * Minimiza o aplicativo programaticamente.
		 * @param context O <code>Context</code> atual a partir do qual o aplicativo sera minimizado.
		 */
		public static void minizarAplicativo(Context context)
		{
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static class FB
	{
		private static final String TAG = Utilidades.TAG + ".FB";

		public static byte[] getByteArray(List<GraphUser> users)
		{

			// Converte a lista de GraphUsers em Strings para serem salvos como um array de bytes.
			List<String> usersAsString = new ArrayList<String>(users.size());

			for (GraphUser user : users) {
				usersAsString.add(user.getInnerJSONObject().toString());
			}

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				new ObjectOutputStream(outputStream).writeObject(usersAsString);
				return outputStream.toByteArray();
			} catch (IOException e) {
				Log.e(TAG, "Unable to serialize users.", e);
			}
			return null;
		}

		public static List<GraphUser> restoreByteArray(byte[] bytes)
		{
			try {
				@SuppressWarnings("unchecked")
				List<String> usersAsString = (List<String>) (new ObjectInputStream(
						new ByteArrayInputStream(bytes))).readObject();
				if (usersAsString != null) {
					List<GraphUser> users = new ArrayList<GraphUser>(
							usersAsString.size());
					for (String user : usersAsString) {
						GraphUser graphUser = GraphObject.Factory.create(
								new JSONObject(user), GraphUser.class);
						users.add(graphUser);
					}
					return users;
				}
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Unable to deserialize users.", e);
			} catch (IOException e) {
				Log.e(TAG, "Unable to deserialize users.", e);
			} catch (JSONException e) {
				Log.e(TAG, "Unable to deserialize users.", e);
			}
			return null;
		}

		public static List<String> getUsersNamesAsString(List<GraphUser> userList)
		{
			List<String> usersAsString = new ArrayList<String>(userList.size());

			for (GraphUser user : userList) {
				user.getId();
				usersAsString.add(user.getName());
			}
			return usersAsString;
		}
	}

	//	public static List<UsuarioFacebook> getFacebookUsers(List<GraphUser> userList)
	//	{
	//		List<UsuarioFacebook> users = new ArrayList<UsuarioFacebook>(userList.size());
	//
	//		for (GraphUser user : userList) {
	//			users.add(new UsuarioFacebook(user.getId(), user.getName()));
	//		}
	//		return users;
	//	}
}
