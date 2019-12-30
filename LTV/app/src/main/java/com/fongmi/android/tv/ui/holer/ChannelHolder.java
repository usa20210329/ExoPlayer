package com.fongmi.android.tv.ui.holer;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.AppDatabase;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.ui.adapter.ChannelAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

	@BindView(R.id.number) TextView number;
	@BindView(R.id.name) TextView name;

	private ChannelAdapter adapter;

	public ChannelHolder(View view, ChannelAdapter adapter) {
		super(view);
		ButterKnife.bind(this, view);
		view.setOnLongClickListener(this);
		view.setOnClickListener(this);
		setAdapter(adapter);
	}

	private void setAdapter(ChannelAdapter adapter) {
		this.adapter = adapter;
	}

	public void setChannel(Channel item) {
		itemView.setSelected(item.isSelect());
		number.setTextSize(item.getTextSize());
		name.setTextSize(item.getTextSize());
		number.setText(item.getNumber());
		name.setText(item.getName());
	}

	@Override
	public void onClick(View v) {
		adapter.getListener().onItemClick(adapter.getItem(getLayoutPosition()));
	}

	@Override
	public boolean onLongClick(View v) {
		if (adapter.getType().getId() == 0) AppDatabase.getInstance().getDao().delete(adapter.getItem(getLayoutPosition()));
		else AppDatabase.getInstance().getDao().insert(adapter.getItem(getLayoutPosition()));
		return adapter.getListener().onLongClick();
	}
}
