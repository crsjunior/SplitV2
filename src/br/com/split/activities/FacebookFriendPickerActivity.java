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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import br.com.split.R;
import br.com.split.controller.SplitApplication;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class FacebookFriendPickerActivity extends FragmentActivity
{
	public static final String TAG = "FacebookFriendPicker";
	private static final List<String> PERMISSIONS = new ArrayList<String>() {
		private static final long serialVersionUID = -5261292506188830678L;

		{
			add("user_friends");
			add("public_profile");
		}
	};
	private static final int PICK_FRIENDS_ACTIVITY = 1;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		lifecycleHelper = new UiLifecycleHelper(this,
				new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception)
					{
						onSessionStateChanged(session, state, exception);
					}
				});
		lifecycleHelper.onCreate(savedInstanceState);

		ensureOpenSession();
		startPickFriendsActivity();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		// Call the 'deactivateApp' method to log an app event for use in
		// analytics and advertising
		// reporting. Do so in the onPause methods of the primary Activities
		// that an app may be launched into.
		AppEventsLogger.deactivateApp(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PICK_FRIENDS_ACTIVITY:
					setResult(RESULT_OK);
					finish();
					break;
				default:
					Session.getActiveSession().onActivityResult(this, requestCode,
							resultCode, data);
					break;
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}

	}

	private boolean ensureOpenSession()
	{
		if (Session.getActiveSession() == null
				|| !Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, PERMISSIONS,
					new Session.StatusCallback() {
						@Override
						public void call(Session session, SessionState state,
								Exception exception)
						{
							onSessionStateChanged(session, state, exception);
						}
					});
			return false;
		}
		return true;
	}

	private boolean sessionHasNecessaryPerms(Session session)
	{
		if (session != null && session.getPermissions() != null) {
			for (String requestedPerm : PERMISSIONS) {
				if (!session.getPermissions().contains(requestedPerm)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private List<String> getMissingPermissions(Session session)
	{
		List<String> missingPerms = new ArrayList<String>(PERMISSIONS);
		if (session != null && session.getPermissions() != null) {
			for (String requestedPerm : PERMISSIONS) {
				if (session.getPermissions().contains(requestedPerm)) {
					missingPerms.remove(requestedPerm);
				}
			}
		}
		return missingPerms;
	}

	private void onSessionStateChanged(final Session session, SessionState state, Exception exception)
	{
		if (state.isOpened() && !sessionHasNecessaryPerms(session)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.need_perms_alert_text);
			builder.setPositiveButton(R.string.need_perms_alert_button_ok,
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							session.requestNewReadPermissions(new NewPermissionsRequest(
									FacebookFriendPickerActivity.this, getMissingPermissions(session)));
						}
					});
			builder.setNegativeButton(R.string.need_perms_alert_button_quit,
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							finish();
						}
					});
			builder.show();
			startPickFriendsActivity();
		} else if (pickFriendsWhenSessionOpened && state.isOpened()) {
			pickFriendsWhenSessionOpened = false;
			startPickFriendsActivity();
		}
	}

	private boolean friendsAlreadySelected()
	{
		SplitApplication application = (SplitApplication) getApplication();
		return !(application.getSelectedUsers() != null && application.getSelectedUsers().size() != 0);

	}

	private void startPickFriendsActivity()
	{
		if (ensureOpenSession()) {
			Intent intent = new Intent(this, PickFriendsActivity.class);
			// Note: The following line is optional, as multi-select behavior is  the default for
			// FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
			// friend picker if single-select functionality was desired, or if a different user ID was
			// desired (for instance, to see friends of a friend).
			PickFriendsActivity.populateParameters(intent, null, true, true, friendsAlreadySelected());
			startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
		} else {
			pickFriendsWhenSessionOpened = true;
		}
	}
}
