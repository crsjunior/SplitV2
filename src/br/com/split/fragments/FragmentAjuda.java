package br.com.split.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import br.com.split.R;
import br.com.split.contracts.OnFragmentSelectedListener;


public class FragmentAjuda extends Fragment
{
	private static final int POSITION = 2;

	private OnFragmentSelectedListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_ajuda, container, false);

		WebView wvAjuda = (WebView) view.findViewById(R.id.webview_ajuda);
		wvAjuda.loadDataWithBaseURL(null,
				getActivity().getResources().getString(R.string.texto_ajuda),
				"text/html", "UTF-8", null);

		return view;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try {
			mListener = (OnFragmentSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " deve implementar OnFragmentSelectedListener");
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mListener.onFragmentSelected(POSITION);
	}
}
