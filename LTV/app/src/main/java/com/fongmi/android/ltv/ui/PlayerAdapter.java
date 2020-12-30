package com.fongmi.android.ltv.ui;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private OnItemClickListener mItemClickListener;
	private final List<Channel> mHides;
	private final List<Object> mItems;
	private final ChannelDao mDao;
	private boolean visible;
	private int position;
	private int count;

	PlayerAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mDao = AppDatabase.getInstance().getDao();
	}

	public interface OnItemClickListener {

		void onItemClick(Channel item);
	}

	void setOnItemListener(OnItemClickListener itemClickListener) {
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

	private boolean isVisible() {
		return visible;
	}

	void setVisible(boolean visible) {
		this.visible = visible;
	}

	void setPosition(int position) {
		this.position = position;
	}

	private void setCount() {
		this.count = 0;
	}

	class TypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private final AdapterTypeBinding binding;

		TypeHolder(AdapterTypeBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
			onSelect();
			addCount();
		}
	}

	class ChannelHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		private final AdapterChannelBinding binding;

		ChannelHolder(AdapterChannelBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
			itemView.setOnLongClickListener(this);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
			setChannel();
		}

		@Override
		public boolean onLongClick(View view) {
			setPosition(getLayoutPosition());
			return onKeep();
		}
	}

	void addAll(List<Channel> items) {
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

	void addCount() {
		if (mHides.isEmpty() || ++count < 5) return;
		mItems.addAll(mHides);
		notifyDataSetChanged();
		Notify.show(R.string.app_unlock);
		mHides.clear();
		setCount();
	}

	int onMoveUp() {
		if (mItems.isEmpty()) return 0;
		this.position = position > 0 ? --position : mItems.size() - 1;
		if (Prefers.isOk() || isType(position)) onSelect(); else setChannel();
		return position;
	}

	int onMoveDown() {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (Prefers.isOk() || isType(position)) onSelect(); else setChannel();
		return position;
	}

	private void onSelect() {
		for (int i = 0; i < mItems.size(); i++) getBean(i).setSelect(i == position);
		notifyDataSetChanged();
	}

	void setChannel() {
		if (position < 0 || position > mItems.size() - 1 || isType(position)) return;
		for (int i = 0; i < mItems.size(); i++) getBean(i).setSelect(i == position);
		if (isVisible()) mItemClickListener.onItemClick(getChannel(position));
		Prefers.putKeep(getChannel(position).getNumber());
		notifyDataSetChanged();
	}

	boolean onKeep() {
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

	int getIndex(String number) {
		return mItems.indexOf(Channel.create(number));
	}

	String getInfo(String number) {
		return getIndex(number) == -1 ? number : number.concat("  ").concat(getChannel(getIndex(number)).getName());
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
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == 1) {
			return new TypeHolder(AdapterTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		} else {
			return new ChannelHolder(AdapterChannelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == 1) {
			TypeHolder type = (TypeHolder) viewHolder;
			Type item = getType(position);
			type.itemView.setSelected(item.isSelect());
			type.binding.name.setText(item.getName());
			type.binding.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
		} else {
			ChannelHolder holder = (ChannelHolder) viewHolder;
			Channel item = getChannel(position);
			item.loadImage(holder.binding.logo);
			holder.itemView.setSelected(item.isSelect());
			holder.binding.name.setText(item.getName());
			holder.binding.number.setText(item.getNumber());
			holder.binding.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
			holder.binding.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
		}
	}
}
