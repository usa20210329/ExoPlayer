package com.fongmi.android.tv.ui.adapter;

import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.AppDatabase;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.dao.ChannelDao;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mHides;
	private List<Object> mItems;
	private ChannelDao mDao;
	private Handler mHandler;
	private boolean visible;
	private boolean waiting;
	private int position;
	private int count;

	public MainAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mHandler = new Handler();
		this.mDao = AppDatabase.getInstance().getDao();
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (visible) mItemClickListener.onItemClick(getItem(position));
		}
	};

	public interface OnItemClickListener {

		void onItemClick(Channel item);
	}

	public void setOnItemListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private Channel getItem(int position) {
		return (Channel) mItems.get(position);
	}

	private void setCount() {
		this.count = 0;
	}

	class TypeHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.name) TextView name;

		TypeHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		@BindView(R.id.number) TextView number;
		@BindView(R.id.name) TextView name;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			view.setOnLongClickListener(this);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
			setChannel(0);
		}

		@Override
		public boolean onLongClick(View v) {
			setPosition(getLayoutPosition());
			return onKeep();
		}
	}

	public void addAll(List<Channel> items) {
		mHides.clear();
		mItems.clear();
		addChannel(items);
		notifyDataSetChanged();
	}

	private void addChannel(List<Channel> items) {
		mItems.add(Utils.getString(R.string.channel_type_keep));
		mItems.addAll(mDao.getKeep());
		mItems.add(Utils.getString(R.string.channel_type_all));
		for (Channel item : items) {
			if (item.isHidden()) mHides.add(item);
			else mItems.add(item);
		}
	}

	public void addCount() {
		if (mHides.isEmpty() || ++count < 5) return;
		mItems.addAll(mHides);
		notifyDataSetChanged();
		Notify.show(R.string.channel_unlock);
		mHides.clear();
		setCount();
	}

	public int onMoveUp(boolean wait) {
		this.waiting = wait;
		this.position = position > 0 ? --position : mItems.size() - 1;
		this.setChannel(wait ? 10000 : 500);
		return position;
	}

	public int onMoveDown(boolean wait) {
		this.waiting = wait;
		this.position = position < mItems.size() - 1 ? ++position : 0;
		this.setChannel(wait ? 10000 : 500);
		return position;
	}

	public void setChannel(int delay) {
		if (position < 0 || position > mItems.size() - 1) return;
		for (int i = 0; i < mItems.size(); i++) if (mItems.get(i) instanceof Channel) ((Channel) mItems.get(i)).deselect();
		getItem(position).select();
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, delay);
		notifyDataSetChanged();
	}

	public int getIndex(Channel item) {
		return mItems.indexOf(item);
	}

	public void onCenter() {
		if (waiting) setChannel(0);
		this.waiting = false;
	}

	public boolean onKeep() {
		Channel item = getItem(position);
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
	}

	private void insert(Channel item) {
		int index = mItems.indexOf(Utils.getString(R.string.channel_type_all));
		mItems.add(index, item.get());
		notifyItemInserted(index);
		mDao.insert(item);
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}


	@Override
	public int getItemViewType(int position) {
		return mItems.get(position) instanceof String ? 1 : 2;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == 1) {
			return new TypeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_type, parent, false));
		} else {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == 1) {
			TypeHolder type = (TypeHolder) viewHolder;
			type.name.setText(mItems.get(position).toString());
		} else {
			ViewHolder holder = (ViewHolder) viewHolder;
			Channel item = getItem(position);
			holder.name.setText(item.getName());
			holder.number.setText(item.getNumber());
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
			holder.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
			holder.itemView.setSelected(item.isSelect());
		}
	}
}
