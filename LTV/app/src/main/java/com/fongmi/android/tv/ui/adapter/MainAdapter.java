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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mItems;
	private List<Channel> mHides;
	private Handler mHandler;
	private boolean visible;
	private boolean waiting;
	private int position;
	private int count;

	public MainAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mHandler = new Handler();
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (visible) mItemClickListener.onItemClick(mItems.get(position));
		}
	};

	public interface OnItemClickListener {

		void onItemClick(Channel item);
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private void setCount() {
		this.count = 0;
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.number) TextView number;
		@BindView(R.id.name) TextView name;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
		}
	}

	public void addAll(List<Channel> items) {
		mHides.clear();
		mItems.clear();
		addChannel(items);
		notifyDataSetChanged();
	}

	private void addChannel(List<Channel> items) {
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
		for (Channel item : mItems) item.deselect();
		mItems.get(position).select();
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

	public void onKeep() {
		Channel item = mItems.get(position);
		ChannelDao dao = AppDatabase.getInstance().getDao();
		boolean exist = dao.getCount(item.getNumber()) > 0;
		Notify.show(exist ? R.string.channel_keep_delete : R.string.channel_keep_insert);
		if (exist) dao.delete(item);
		else dao.insert(item);
	}

	public void resetUrl() {
		for (Channel item : mItems) item.setReal("");
		notifyDataSetChanged();
		setChannel(0);
	}

	public void setPosition(int position) {
		this.position = position;
		this.setChannel(0);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Channel item = mItems.get(position);
		holder.name.setText(item.getName());
		holder.number.setText(item.getNumber());
		holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
		holder.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
		holder.itemView.setSelected(item.isSelect());
	}
}
