package com.fongmi.android.ltv.ui.adapter.holder;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.databinding.AdapterChannelBinding;
import com.fongmi.android.ltv.ui.adapter.ChannelAdapter;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Utils;

public class ChannelHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

	private final AdapterChannelBinding binding;
	private final ChannelAdapter adapter;

	public ChannelHolder(ChannelAdapter adapter, @NonNull AdapterChannelBinding binding) {
		super(binding.getRoot());
		this.binding = binding;
		this.adapter = adapter;
		itemView.setOnClickListener(this);
		itemView.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View view) {
		adapter.setPosition(getLayoutPosition());
		adapter.setChannel();
	}

	@Override
	public boolean onLongClick(View view) {
		adapter.setPosition(getLayoutPosition());
		return adapter.onKeep();
	}

	public void setView(Channel item) {
		itemView.setSelected(item.isSelect());
		binding.number.setText(item.getNumber());
		binding.name.setText(item.getName());
		item.loadLogo(binding.logo);
		setSize(Prefers.getSize());
	}

	private void setSize(int size) {
		binding.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, size * 2 + 14);
		binding.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size * 2 + 14);
		ViewGroup.LayoutParams params = binding.logo.getLayoutParams();
		params.height = Utils.dp2px(size * 3 + 36);
		params.width = Utils.dp2px(size * 4 + 48);
		binding.logo.setLayoutParams(params);
	}
}
