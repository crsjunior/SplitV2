package br.com.split.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.activities.FriendPickerActivity;
import br.com.split.controller.SplitApplication;
import br.com.split.model.Elemento;
import br.com.split.utilidades.Utilidades;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SelectionFragment extends Fragment
{
	private static final String TAG = "SelectionFragment";
	private ProfilePictureView profilePictureView;
	private TextView userNameTextView;
	private UiLifecycleHelper uiHelper;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private ListView listView;
	private List<Elemento> listElements;
	private GraphUser currentUser;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception)
		{
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_facebook_selection,
				container, false);

		// Pega a foto do usuário
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		// Seta o username
		userNameTextView = (TextView) view
				.findViewById(R.id.selection_user_name);

		// Find the list view
		listView = (ListView) view.findViewById(R.id.selection_list);

		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<Elemento>();

		// Add an item for the place picker
		listElements.add(new LocationListElement(0));
		// Add an item for the friend picker
		listElements.add(new PessoaElemento(1));

		if (savedInstanceState != null) {
			// Restore the state for each list element
			for (Elemento listElement : listElements) {
				listElement.restoreState(savedInstanceState);
			}
		}
		// Set the list view adapter
		listView.setAdapter(new ActionListAdapter(getActivity(),
				R.id.selection_list, listElements));

		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Pega os dados
			makeMeRequest(session);
		}
		return view;

	}

	private void makeMeRequest(final Session session)
	{
		// Faz uma call para a api e define um callback para processar a
		// resposta
		Request request = Request.newMeRequest(session,
				new GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response)
					{
						// Se a resposta for ok
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// Seta a id para a foto do perfil
								// que a transforma em uma foto
								profilePictureView.setProfileId(user.getId());
								// Seta o texto para ser o nome de usuário
								userNameTextView.setText(user.getName());
								currentUser = user;
							}
						}
						if (response.getError() != null) {
							currentUser = null;
						}

					}
				});
		request.executeAsync();
	}

	// Classe privada para criar a lista de amigos
	private class ActionListAdapter extends ArrayAdapter<Elemento>
	{
		private List<Elemento> listElements;

		public ActionListAdapter(Context context, int resourceId,
				List<Elemento> listElements)
		{
			super(context, resourceId, listElements);
			this.listElements = listElements;
			// Levanta um observador para alterar a lista
			// caso ela mude
			for (int i = 0; i < listElements.size(); i++) {
				listElements.get(i).setAdapter(this);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.faceboob_list_view, null);
			}

			Elemento listElement = listElements.get(position);
			if (listElement != null) {
				view.setOnClickListener(listElement.getOnClickListener());
				ImageView icon = (ImageView) view.findViewById(R.id.icon);
				TextView text1 = (TextView) view.findViewById(R.id.text1);
				TextView text2 = (TextView) view.findViewById(R.id.text2);
				if (icon != null) {
					icon.setImageDrawable(listElement.getIcon());
				}
				if (text1 != null) {
					text1.setText(listElement.getText1());
				}
				if (text2 != null) {
					text2.setText(listElement.getText2());
				}
			}
			return view;
		}

	}

	private void startPickerActivity(Uri data, int requestCode)
	{
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(getActivity(), FriendPickerActivity.class);
		startActivityForResult(intent, requestCode);
	}

	private void onSessionStateChange(final Session session,
			SessionState state, Exception exception)
	{
		if (session != null && session.isOpened()) {
			// pega os dados do usuário
			makeMeRequest(session);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK && requestCode >= 0
				&& requestCode < listElements.size()) {
			listElements.get(requestCode).onActivityResult(data);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		for (Elemento listElement : listElements) {
			listElement.onSaveInstanceState(outState);
		}
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private class PessoaElemento extends Elemento
	{
		private List<GraphUser> selectedUsers;
		private static final String FRIENDS_KEY = "friends";

		public PessoaElemento(int requestCode)
		{
			super(getActivity().getResources().getDrawable(
					R.drawable.add_friends), getActivity().getResources()
					.getString(R.string.acao_pessoas), getActivity()
					.getResources().getString(R.string.acao_pessoas_padrao),
					requestCode);
		}

		@Override
		public boolean restoreState(Bundle savedState)
		{
			byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
			if (bytes != null) {
				selectedUsers = Utilidades.FB.restoreByteArray(bytes);
				setUsersText();
				return true;
			}
			return false;
		}

		@Override
		public void onSaveInstanceState(Bundle bundle)
		{
			if (selectedUsers != null) {
				bundle.putByteArray(FRIENDS_KEY, Utilidades.FB.getByteArray(selectedUsers));
			}
		}

		private void setUsersText()
		{
			String text = null;
			if (selectedUsers != null) {
				// Um amigo
				if (selectedUsers.size() == 1) {

					text = String.format(Locale.getDefault(),
							getResources().getString(
									R.string.um_usuario_selecionado),
							selectedUsers.get(0).getName().trim());
				} else if (selectedUsers.size() == 2) {
					// Dois amigos
					text = String.format(
							getResources().getString(
									R.string.dois_usuarios_selecionados), selectedUsers
									.get(0).getName().trim(), selectedUsers.get(1)
									.getName().trim());
				} else if (selectedUsers.size() > 2) {
					// Mais de dois amigos
					text = String.format(
							getResources().getString(
									R.string.varios_usuarios_selecionados),
							selectedUsers.get(0).getName().trim(),
							(selectedUsers.size() - 1));
				}
			}
			if (text == null) {
				// Se não houver texto, usar o placeholder
				text = getResources().getString(R.string.acao_pessoas_padrao);
			}
			// Set the text in list element. This will notify the
			// adapter that the data has changed to
			// refresh the list view.
			setText2(text);
		}

		@Override
		public void onActivityResult(Intent data)
		{
			selectedUsers = ((SplitApplication) getActivity()
					.getApplication()).getSelectedUsers();
			if (currentUser != null) {
				selectedUsers.add(currentUser);
			}

			setUsersText();
			notifyDataChanged();
		}

		@Override
		public View.OnClickListener getOnClickListener()
		{
			return new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					startPickerActivity(FriendPickerActivity.FRIEND_PICKER,
							getRequestCode());
				}
			};
		}
	}

	private class LocationListElement extends Elemento
	{
		private GraphPlace selectedPlace = null;
		private static final String PLACE_KEY = "place";

		private void setPlaceText()
		{
			String text = null;
			if (selectedPlace != null) {
				text = selectedPlace.getName();
			}
			if (text == null) {
				text = getResources().getString(
						R.string.acao_localizacao_padrao);
			}
			setText2(text);
		}

		public LocationListElement(int requestCode)
		{
			super(getActivity().getResources().getDrawable(
					R.drawable.add_friends), getActivity()
					.getResources().getString(R.string.acao_localizacao),
					getActivity().getResources().getString(
							R.string.acao_localizacao_padrao), requestCode);
		}

		@Override
		public View.OnClickListener getOnClickListener()
		{
			return new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					startPickerActivity(FriendPickerActivity.PLACE_PICKER,
							getRequestCode());
				}
			};
		}

		@Override
		public void onActivityResult(Intent data)
		{
			super.onActivityResult(data);
			selectedPlace = ((SplitApplication) getActivity()
					.getApplication()).getSelectedPlace();
			setPlaceText();
			notifyDataChanged();
		}

		@Override
		public void onSaveInstanceState(Bundle bundle)
		{
			if (selectedPlace != null) {
				bundle.putString(PLACE_KEY,
						selectedPlace.getInnerJSONObject().toString());
			}
		}

		@Override
		public boolean restoreState(Bundle savedState)
		{
			String place = savedState.getString(PLACE_KEY);
			if (place != null) {
				try {
					selectedPlace = GraphObject.Factory.create(
							new JSONObject(place),
							GraphPlace.class);
					setPlaceText();
					return true;
				} catch (JSONException e) {
					Log.e(TAG, "Unable to deserialize place.", e);
				}
			}
			return false;
		}

	}

}
