package com.fongmi.android.ltv.ui.adapter.holder;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.databinding.AdapterTypeBinding;
import com.fongmi.android.ltv.ui.adapter.TypeAdapter;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Utils;

public class TypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

	private final AdapterTypeBinding binding;
	private final TypeAdapter adapter;

	public TypeHolder(TypeAdapter adapter, AdapterTypeBinding binding) {
		super(binding.getRoot());
		this.binding = binding;
		this.adapter = adapter;
		itemView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		adapter.setPosition(getLayoutPosition());
		adapter.setSelected();
		adapter.addCount();
		adapter.setType();
	}

	public void setView(Type item) {
		itemView.setSelected(item.isSelect());
		binding.name.setText(item.getName());
		item.loadLogo(binding.logo);
		setSize(Prefers.getSize());
	}

	private void setSize(int size) {
		binding.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size * 2 + 14);
		ViewGroup.LayoutParams params = binding.logo.getLayoutParams();
		params.height = Utils.dp2px(size * 3 + 36);
		params.width = Utils.dp2px(size * 3 + 36);
		binding.logo.setLayoutParams(params);
	}
}
