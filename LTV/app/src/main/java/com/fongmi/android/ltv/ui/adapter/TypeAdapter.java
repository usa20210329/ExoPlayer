package com.fongmi.android.ltv.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.databinding.AdapterTypeBinding;
import com.fongmi.android.ltv.ui.adapter.holder.TypeHolder;
import com.fongmi.android.ltv.utils.Notify;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class TypeAdapter extends RecyclerView.Adapter<TypeHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Type> mItems;
	private final List<Type> mHides;
	private boolean focus;
	private int position;
	private int count;

	public TypeAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
	}

	public interface OnItemClickListener {

		void onItemClick(Type item);
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void addAll(List<Type> items) {
		mItems.clear();
		addType(items);
		notifyDataSetChanged();
	}

	private void addType(List<Type> items) {
		mItems.add(Type.create(R.string.channel_type_keep));
		for (Type item : items) if (item.isHidden()) mHides.add(item); else mItems.add(item);
		mItems.add(Type.create(R.string.channel_type_setting));
	}

	public void setType() {
		Type item = mItems.get(position);
		mItemClickListener.onItemClick(item);
		if (item.isKeep()) addCount();
		setSelected();
	}

	public int onMoveUp() {
		if (mItems.isEmpty()) return 0;
		this.position = position > 0 ? --position : mItems.size() - 1;
		if (mItems.get(position).isSetting()) setSelected(); else setType();
		return position;
	}

	public int onMoveDown() {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (mItems.get(position).isSetting()) setSelected(); else setType();
		return position;
	}

	public void setSelected() {
		for (int i = 0; i < mItems.size(); i++) mItems.get(i).setSelect(i == position);
		notifyDataSetChanged();
	}

	private void addCount() {
		if (mHides.isEmpty() || ++count < 10) return;
		mItems.addAll(mItems.size() - 1, mHides);
		Notify.show(R.string.app_unlock);
		notifyDataSetChanged();
		mHides.clear();
	}

	public int[] find(String number) {
		for (int i = 1; i < mItems.size() - 1; i++) {
			int j = mItems.get(i).find(number);
			if (j != -1) return new int[]{i, j};
		}
		return new int[]{-1, -1};
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new TypeHolder(this, AdapterTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TypeHolder holder, int position) {
		holder.setView(mItems.get(position));
	}
}