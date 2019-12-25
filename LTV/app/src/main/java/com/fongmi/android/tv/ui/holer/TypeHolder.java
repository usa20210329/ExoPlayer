package com.fongmi.android.tv.ui.holer;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Type;
import com.fongmi.android.tv.ui.adapter.ChannelAdapter;
import com.fongmi.android.tv.ui.adapter.TypeAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.name) TextView name;
	@BindView(R.id.recyclerView) RecyclerView recyclerView;

	private ChannelAdapter mAdapter;
	private TypeAdapter adapter;

	public TypeHolder(View view, TypeAdapter adapter) {
		super(view);
		ButterKnife.bind(this, view);
		setAdapter(adapter);
		setRecyclerView();
	}

	@OnClick(R.id.name)
	public void onName() {
		adapter.mItemClickListener.onTypeClick();
	}

	private void setAdapter(TypeAdapter adapter) {
		this.adapter = adapter;
	}

	private void setRecyclerView() {
		mAdapter = new ChannelAdapter();
		mAdapter.setOnItemClickListener(this::onItemClick);
		recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(mAdapter);
	}

	public void setType(Type item) {
		name.setText(item.getName());
		name.setTextSize(item.getTextSize());
		mAdapter.addAll(item.getChannel());
	}

	private void onItemClick(Channel item) {
		adapter.onItemClick(item);
	}
}
