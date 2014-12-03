/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.split.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import br.com.split.R;
import br.com.split.controller.SplitApplication;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.FriendPickerFragment.FriendPickerType;
import com.facebook.widget.PickerFragment;

// This class provides an example of an Activity that uses FriendPickerFragment to display a list of
// the user's friends. It takes a programmatic approach to creating the FriendPickerFragment with the
// desired parameters -- see PickPlaceActivity in the PlacePickerSample project for an example of an
// Activity creating a fragment (in this case a PlacePickerFragment) via XML layout rather than
// programmatically.
public class PickFriendsActivity extends FragmentActivity
{
	public static final String TAG = "PickFriendsActivity";
	FriendPickerFragment friendPickerFragment;

	// A helper to simplify life for callers who want to populate a Bundle with the necessary
	// parameters. A more sophisticated Activity might define its own set of parameters; our needs
	// are simple, so we just populate what we want to pass to the FriendPickerFragment.
	public static void populateParameters(Intent intent, String userId, boolean multiSelect, boolean showTitleBar,
			boolean initialLoad)
	{
		intent.putExtra(FriendPickerFragment.USER_ID_BUNDLE_KEY, userId);
		intent.putExtra(FriendPickerFragment.MULTI_SELECT_BUNDLE_KEY, multiSelect);
		intent.putExtra(FriendPickerFragment.SHOW_TITLE_BAR_BUNDLE_KEY, showTitleBar);
		intent.putExtra(FriendPickerFragment.INITIAL_LOAD_KEY, initialLoad);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escolher_amigos_facebook);

		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {
			// First time through, we create our fragment programmatically.
			final Bundle args = getIntent().getExtras();
			friendPickerFragment = new FriendPickerFragment(args);
			friendPickerFragment.setFriendPickerType(FriendPickerType.TAGGABLE_FRIENDS);
			friendPickerFragment.setDoneButtonText(getResources().getString(R.string.acao_feito));
			friendPickerFragment.setTitleText(getResources().getString(R.string.title_activity_facebook_amigos));
			fm.beginTransaction()
					.add(R.id.friend_picker_fragment, friendPickerFragment)
					.commit();
		} else {
			// Subsequent times, our fragment is recreated by the framework and already has saved and
			// restored its state, so we don't need to specify args again. (In fact, this might be
			// incorrect if the fragment was modified programmatically since it was created.)
			friendPickerFragment = (FriendPickerFragment) fm.findFragmentById(R.id.friend_picker_fragment);
		}

		friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			@Override
			public void onError(PickerFragment<?> fragment, FacebookException error)
			{
				PickFriendsActivity.this.onError(error);
			}
		});

		friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> fragment)
			{
				if (friendPickerFragment.getSelection().size() != 0) {
					SplitApplication application = (SplitApplication) getApplication();
					// TODO: Aqui nao devemos desmarcar os amigos ja adicionados ao evento.
					application.setSelectedUsers(friendPickerFragment.getSelection());
					setResult(RESULT_OK);
					finish();
				} else {
					setResult(RESULT_CANCELED);
					finish();
				}

			}
		});
	}

	private void onError(Exception error)
	{
		//TODO: Arrumar mensagem de excecao
		String text = getString(R.string.com_facebook_requesterror_web_login, error.getMessage());
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		try {
			SplitApplication application = (SplitApplication) getApplication();
			List<GraphUser> selectedUsers = application.getSelectedUsers();
			if (selectedUsers != null && !selectedUsers.isEmpty()) {
				friendPickerFragment.setSelection(selectedUsers);
			}
			// Load data, unless a query has already taken place.
			friendPickerFragment.loadData(false);
		} catch (Exception ex) {
			onError(ex);
		}
	}
}
