package br.com.split.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.split.R;

public class FacebookSplashFrgmnt extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(
				R.layout.fragment_facebook_splash, container, false);
		TextView splashTextLogin = (TextView) view.findViewById(R.id.titulo_login);
		splashTextLogin.setText(Html.fromHtml(getString(R.string.formatted_splash_text)));
		return view;
	}
}
