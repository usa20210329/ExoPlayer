package com.fongmi.android.ltv.ui;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fongmi.android.ltv.App;
import com.fongmi.android.ltv.AppDatabase;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Bean;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.dao.ChannelDao;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mHides;
	private List<Object> mItems;
	private ChannelDao mDao;
	private boolean visible;
	private int position;
	private int count;

	MainAdapter() {
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

	private void setCount() {
		this.count = 0;
	}

	class TypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.name) TextView name;

		TypeHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
			addCount();
			setType();
		}
	}

	class ChannelHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		@BindView(R.id.number) TextView number;
		@BindView(R.id.logo) ImageView logo;
		@BindView(R.id.name) TextView name;

		ChannelHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			view.setOnLongClickListener(this);
			view.setOnClickListener(this);
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
		if (isType(position)) setType(); else setChannel();
		return position;
	}

	int onMoveDown() {
		if (mItems.isEmpty()) return 0;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		if (isType(position)) setType(); else setChannel();
		return position;
	}

	private void setType() {
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

	int getIndex(Channel item) {
		return mItems.indexOf(item);
	}

	boolean onKeep() {
		if (mItems.isEmpty() || isType(position)) return false;
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

	void setPosition(int position) {
		this.position = position;
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
			return new TypeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_type, parent, false));
		} else {
			return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_channel, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == 1) {
			TypeHolder type = (TypeHolder) viewHolder;
			Type item = getType(position);
			type.name.setText(item.getName());
			type.itemView.setSelected(item.isSelect());
			type.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
		} else {
			ChannelHolder holder = (ChannelHolder) viewHolder;
			Channel item = getChannel(position);
			holder.name.setText(item.getName());
			holder.number.setText(item.getNumber());
			holder.itemView.setSelected(item.isSelect());
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
			holder.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
			Glide.with(App.get()).load(item.getLogoUrl()).into(holder.logo);
		}
	}
}
