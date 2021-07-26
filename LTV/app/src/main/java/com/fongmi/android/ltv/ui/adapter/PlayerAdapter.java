package com.fongmi.android.ltv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.AppDatabase;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Bean;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.dao.ChannelDao;
import com.fongmi.android.ltv.databinding.AdapterChannelBinding;
import com.fongmi.android.ltv.databinding.AdapterTypeBinding;
import com.fongmi.android.ltv.ui.adapter.holder.BaseHolder;
import com.fongmi.android.ltv.ui.adapter.holder.ChannelHolder;
import com.fongmi.android.ltv.ui.adapter.holder.TypeHolder;
import com.fongmi.android.ltv.utils.Notify;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<BaseHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Channel> mHides;
	private final List<Object> mItems;
	private final ChannelDao mDao;
	private int position;
	private int count;

	public PlayerAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mDao = AppDatabase.getInstance().getDao();
	}

	public interface OnItemClickListener {

		void onItemClick(Channel item);
	}

	public void setOnItemListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private Bean getBean(int position) {
		return (Bean) mItems.get(position);
	}

	private Type getType(int position) {
		return (Type) mItems.get(position);
	}

	private Channel getChannel(int position) {
		return (Channel) mItems.get(position);
	}

	private boolean isType(int position) {
		return mItems.get(position) instanceof Type;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private void setCount() {
		this.count = 0;
	}

	public void addAll(List<Channel> items) {
		mHides.clear();
		mItems.clear();
		addChannel(items);
		notifyDataSetChanged();
	}

	private void addChannel(List<Channel> items) {
		mItems.add(Type.create(R.string.channel_type_keep));
		mItems.addAll(mDao.getKeep());
		mItems.add(Type.create(R.string.channel_type_all));
		for (Channel item : items) {
			if (item.isHidden()) mHides.add(item);
			else mItems.add(item);
		}
	}

	public void addCount() {
		if (mHides.isEmpty() || ++count < 5) return;
		mItems.addAll(mHides);
		notifyDataSetChanged();
		Notify.show(R.string.app_unlock);
		mHides.clear();
		setCount();
	}

	public int onMoveUp(boolean play) {
		if (mItems.isEmpty()) return 0;
		this.position = position > 0 ? --position : mItems.size() - 1;
		if (isType(position)) onMoveUp(play);
		if (play) setChannel();
		else setSelected();
		return position;
	}

	public int onMoveDown(boolean play) {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (isType(position)) onMoveDown(play);
		if (play) setChannel();
		else setSelected();
		return position;
	}

	public void setSelected() {
		for (int i = 0; i < mItems.size(); i++) getBean(i).setSelect(i == position);
		notifyDataSetChanged();
	}

	public void setChannel() {
		if (position < 0 || position > mItems.size() - 1 || isType(position)) return;
		mItemClickListener.onItemClick(getChannel(position));
		getChannel(position).putKeep();
		setSelected();
	}

	public boolean onKeep() {
		if (mItems.isEmpty() || position < 0 || isType(position)) return false;
		Channel item = getChannel(position);
		boolean exist = mDao.getCount(item.getNumber()) > 0;
		Notify.show(exist ? R.string.channel_keep_delete : R.string.channel_keep_insert);
		if (exist) delete(item);
		else insert(item);
		return true;
	}

	private void delete(Channel item) {
		int index = mItems.indexOf(item);
		mDao.delete(item);
		mItems.remove(item);
		notifyItemRemoved(index);
		--position;
	}

	private void insert(Channel item) {
		int index = mItems.indexOf(Type.create(R.string.channel_type_all));
		mItems.add(index, item.get());
		notifyItemInserted(index);
		mDao.insert(item);
		++position;
	}

	public int getIndex(String number) {
		return mItems.lastIndexOf(Channel.create(number));
	}

	public Channel getCurrent() {
		if (position < 0 || position > mItems.size() - 1 || isType(position)) return null;
		return getChannel(position);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		return isType(position) ? 1 : 2;
	}

	@NonNull
	@Override
	public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == 1) {
			return new TypeHolder(this, AdapterTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		} else {
			return new ChannelHolder(this, AdapterChannelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
		if (getItemViewType(position) == 1) {
			TypeHolder type = (TypeHolder) holder;
			type.setView(getType(position));
		} else {
			ChannelHolder channel = (ChannelHolder) holder;
			channel.setView(getChannel(position));
		}
	}
}
