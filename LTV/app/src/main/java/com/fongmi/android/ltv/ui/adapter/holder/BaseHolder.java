package com.fongmi.android.ltv.ui.adapter.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.ui.adapter.PlayerAdapter;

public abstract class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

	protected PlayerAdapter adapter;

	public BaseHolder(@NonNull View itemView, PlayerAdapter adapter) {
		super(itemView);
		this.adapter = adapter;
		itemView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		adapter.setPosition(getLayoutPosition());
	}
}
