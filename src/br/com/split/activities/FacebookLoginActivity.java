package br.com.split.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.com.split.R;
import br.com.split.controller.SplitApplication;
import br.com.split.utilidades.Utilidades;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class FacebookLoginActivity extends FragmentActivity
{
	public static final String TAG = "FacebookLoginActivity";
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	public static final String LOCALIZACAO_KEY = "localizacao";
	public static final String LISTA_DE_PESSOAS_KEY = "listaDePessoas";
	private static final List<String> PERMISSIONS = new ArrayList<String>() {
		{
			add("user_friends");
			add("public_profile");
		}
	};
	private MenuItem settings;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callBack = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception)
		{
			onSessionStateChange(session, state, exception);

		}
	};

	public void escolhaConcluida(View view)
	{
		List<GraphUser> selectedUsers = ((SplitApplication) getApplication()).getSelectedUsers();
		GraphPlace place = ((SplitApplication) getApplication()).getSelectedPlace();

		Intent intent = new Intent(FacebookLoginActivity.this, CriarNovoEventoActivity.class);
		intent.putStringArrayListExtra(LISTA_DE_PESSOAS_KEY, (ArrayList<String>) Utilidades.FB.getUsersNamesAsString(selectedUsers));
		if (place != null) {
			intent.putExtra(LOCALIZACAO_KEY, place.getName());
		} else {
			intent.putExtra(LOCALIZACAO_KEY, "");
		}
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callBack);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_login);
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(PERMISSIONS);
		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack)
	{
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}

		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	protected void onResumeFragments()
	{
		super.onResumeFragments();
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// sessao já aberta, mostrar o fragmento de selecao
			showFragment(SELECTION, false);

		} else {
			// senao, mostrar login
			showFragment(SPLASH, false);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception)
	{
		// Somente validar a sessão se a atividade estiver ativa
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			int backStackSize = manager.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			if (state.isOpened()) {
				// Se a sessão já estiver aberta, mostrar o fragmento de selecao
				showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				// Senão, mostrar o login
				showFragment(SPLASH, false);
			}

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.equals(settings)) {
			showFragment(SETTINGS, true);
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (fragments[SELECTION].isVisible()) {
			if (menu.size() == 0) {
				settings = menu.add(R.string.action_settings);
			}
			return true;
		} else {
			menu.clear();
			settings = null;
		}
		return false;
	}
}
