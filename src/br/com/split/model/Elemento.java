package br.com.split.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

//Elemento da lista de amigos do facebook
public abstract class Elemento {
	private Drawable icon;
	private String text1;
	private String text2;
	private int requestCode;
	private BaseAdapter adapter;
	
	public Elemento(Drawable icon, String text1, String text2, int requestCode) {
		super();
		this.icon = icon;
		this.text1 = text1;
		this.text2 = text2;
		this.requestCode = requestCode;
	}
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}

	public Drawable getIcon() {
		return icon;
	}
	public abstract View.OnClickListener getOnClickListener();
	public void onActivityResult(Intent data){};
	public void onSaveInstanceState(Bundle bundle){};
	public boolean restoreState(Bundle savedState) {
		return false;
	}
	protected void notifyDataChanged() {
		adapter.notifyDataSetChanged();
	}
}
