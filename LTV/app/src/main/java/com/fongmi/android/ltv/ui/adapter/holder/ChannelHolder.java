package com.fongmi.android.ltv.ui.adapter.holder;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.databinding.AdapterChannelBinding;
import com.fongmi.android.ltv.ui.adapter.PlayerAdapter;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Utils;

public class ChannelHolder extends BaseHolder implements View.OnLongClickListener {

	private final AdapterChannelBinding binding;

	public ChannelHolder(PlayerAdapter adapter, AdapterChannelBinding binding) {
		super(binding.getRoot(), adapter);
		this.binding = binding;
		itemView.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
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
