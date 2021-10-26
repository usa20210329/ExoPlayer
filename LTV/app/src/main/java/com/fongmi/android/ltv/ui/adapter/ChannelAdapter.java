package com.fongmi.android.ltv.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.AppDatabase;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.dao.ChannelDao;
import com.fongmi.android.ltv.databinding.AdapterChannelBinding;
import com.fongmi.android.ltv.ui.adapter.holder.ChannelHolder;
import com.fongmi.android.ltv.utils.Notify;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Channel> mItems;
	private final ChannelDao mDao;
	private boolean focus;
	private int position;
	private Type mType;

	public ChannelAdapter() {
		this.mItems = new ArrayList<>();
		this.mDao = AppDatabase.get().getDao();
	}

	public interface OnItemClickListener {

		void onItemClick(Channel item);
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

	public Type getType() {
		return mType;
	}

	private void setType(Type type) {
		this.mType = type;
	}

	public void addAll(Type type, List<Channel> items) {
		setType(type);
		mItems.clear();
		mItems.addAll(items);
		setPosition(type.getPosition());
		notifyDataSetChanged();
	}

	public int onMoveUp(boolean play) {
		if (mItems.isEmpty()) return 0;
		this.position = position > 0 ? --position : mItems.size() - 1;
		if (play) setChannel();
		else setSelected();
		return position;
	}

	public int onMoveDown(boolean play) {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (play) setChannel();
		else setSelected();
		return position;
	}

	public void setSelected() {
		for (int i = 0; i < mItems.size(); i++) mItems.get(i).setSelect(i == position);
		notifyDataSetChanged();
	}

	public void clearSelect() {
		for (int i = 0; i < mItems.size(); i++) mItems.get(i).setSelect(false);
		notifyDataSetChanged();
	}

	public void setChannel() {
		if (position < 0 || position > mItems.size() - 1) return;
		if (!getType().isHidden()) mItems.get(position).putKeep();
		mItemClickListener.onItemClick(mItems.get(position));
		getType().setPosition(position);
		setSelected();
	}

	public boolean onKeep() {
		if (getType().isHidden() || mItems.isEmpty() || position < 0) return false;
		Channel item = mItems.get(position);
		boolean exist = mDao.getCount(item.getNumber()) > 0;
		Notify.show(exist ? R.string.channel_keep_delete : R.string.channel_keep_insert);
		if (exist) delete(item);
		else mDao.insert(item);
		return true;
	}

	private void delete(Channel item) {
		mDao.delete(item);
		if (!getType().isKeep()) return;
		int index = mItems.indexOf(item);
		mItems.remove(item);
		notifyItemRemoved(index);
		--position;
	}

	public Channel getCurrent() {
		if (position < 0 || position > mItems.size() - 1) return null;
		return mItems.get(position);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public ChannelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ChannelHolder(this, AdapterChannelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ChannelHolder holder, int position) {
		holder.setView(mItems.get(position));
	}
}
