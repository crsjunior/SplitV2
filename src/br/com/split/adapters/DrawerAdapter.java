package br.com.split.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.com.split.R;
import br.com.split.model.DrawerItem;

public class DrawerAdapter extends ArrayAdapter<DrawerItem>
{
	private Context context;
	private int layoutResourceId;
	private List<DrawerItem> listItems;

	public DrawerAdapter(Context context, int layoutResourceId, List<DrawerItem> listItems)
	{
		super(context, layoutResourceId, listItems);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.listItems = listItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		DrawerItemHolder holder;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			holder = new DrawerItemHolder();

			view = inflater.inflate(layoutResourceId, parent, false);
			holder.textview_listItem = (TextView) view.findViewById(R.id.textview_list_item);

			view.setTag(holder);
		} else {
			holder = (DrawerItemHolder) view.getTag();
		}

		DrawerItem item = (DrawerItem) this.listItems.get(position);

		holder.textview_listItem.setText(item.getNome());

		return view;
	}

	private static class DrawerItemHolder
	{
		TextView textview_listItem;
	}
}
