package com.fongmi.android.tv.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.bean.Type;
import com.fongmi.android.tv.databinding.AdapterTypeBinding;
import com.fongmi.android.tv.ui.adapter.holder.TypeHolder;
import com.fongmi.android.tv.utils.Notify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class TypeAdapter extends RecyclerView.Adapter<TypeHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Type> mItems;
	private final List<Type> mHides;
	private boolean focus;
	private int position;

	public TypeAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
	}

	public interface OnItemClickListener {

		void onItemClick(Type item, boolean tv);
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private Type getItem() {
		return mItems.get(position);
	}

	public void addAll(List<Type> items) {
		mItems.clear();
		addType(items);
		notifyDataSetChanged();
	}

	private void addType(List<Type> items) {
		mItems.add(Type.create(R.string.type_keep));
		for (Type item : items) if (item.isHidden()) mHides.add(item); else mItems.add(item);
		mItems.add(Type.create(R.string.type_setting));
	}

	public void setType() {
		mItemClickListener.onItemClick(getItem(), false);
	}

	public int onMoveUp() {
		if (mItems.isEmpty()) return 0;
		this.position = position > 0 ? --position : mItems.size() - 1;
		if (!getItem().isSetting()) setType(); setSelected();
		return position;
	}

	public int onMoveDown() {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (!getItem().isSetting()) setType(); setSelected();
		return position;
	}

	public void setSelected() {
		for (int i = 0; i < mItems.size(); i++) mItems.get(i).setSelect(i == position);
		notifyDataSetChanged();
		setFocus(true);
	}

	public void clearSelect() {
		setFocus(false);
		if (mItems.isEmpty()) return;
		getItem().setSelect(false);
		notifyItemChanged(position);
	}

	public void addHides(String pass) {
		if (pass.isEmpty()) return;
		int position = mItems.size() - 1;
		Iterator<Type> iterator = mHides.iterator();
		while (iterator.hasNext()) {
			Type item = iterator.next();
			if (!item.getPass().equals(pass)) continue;
			mItems.add(position, item);
			notifyItemRangeInserted(position, 1);
			Notify.show(R.string.app_unlock);
			iterator.remove();
		}
	}

	public void onKeyCenter() {
		mItemClickListener.onItemClick(getItem(), true);
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
