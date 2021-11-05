package com.fongmi.android.tv.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.AppDatabase;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.bean.Channel;
import com.fongmi.android.tv.bean.Type;
import com.fongmi.android.tv.dao.ChannelDao;
import com.fongmi.android.tv.databinding.AdapterChannelBinding;
import com.fongmi.android.tv.ui.adapter.holder.ChannelHolder;
import com.fongmi.android.tv.utils.Notify;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Channel> mItems;
	private final ChannelDao mDao;
	private Channel mCurrent;
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

	public Channel getCurrent() {
		return mCurrent;
	}

	public void setCurrent(Channel current) {
		this.mCurrent = current;
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

	private Channel getItem() {
		return mItems.get(position);
	}

	public Type getType() {
		return mType;
	}

	private void setType(Type type) {
		this.mType = type;
	}

	public void addAll(Type type) {
		setType(type);
		mItems.clear();
		mItems.addAll(type.getChannel());
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
		setFocus(true);
	}

	public void clearSelect() {
		setFocus(false);
		if (mItems.isEmpty()) return;
		getItem().setSelect(false);
		notifyItemChanged(position);
	}

	public void setChannel() {
		if (position < 0 || position > mItems.size() - 1) return;
		if (!getType().isHidden()) getItem().putKeep();
		mItemClickListener.onItemClick(getItem());
		getType().setPosition(position);
		getItem().setType(getType());
		setCurrent(getItem());
		setSelected();
	}

	public boolean onKeep() {
		if (mItems.isEmpty() || position < 0 || getType().isHidden()) return false;
		boolean exist = mDao.getCount(getItem().getNumber()) > 0;
		Notify.show(exist ? R.string.channel_keep_delete : R.string.channel_keep_insert);
		if (exist) delete(getItem());
		else mDao.insert(getItem());
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
