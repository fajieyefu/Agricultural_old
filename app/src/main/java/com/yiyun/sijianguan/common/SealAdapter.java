package com.yiyun.sijianguan.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yiyun.sijianguan.R;

import java.util.List;


/**
 * Created by qiancheng on 2016/12/22.
 */
public class SealAdapter extends BaseAdapter {
	private Context context;
	private List<SealApplyInfo> mData;

	public SealAdapter(Context context, List<SealApplyInfo> data) {
		this.context = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SealApplyInfo sealApplyInfo = mData.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.seal_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.applyMan = (TextView) convertView.findViewById(R.id.apply_man);
			viewHolder.useMan = (TextView) convertView.findViewById(R.id.use_man);
			viewHolder.useAddress = (TextView) convertView.findViewById(R.id.use_address);
			viewHolder.applyDate = (TextView) convertView.findViewById(R.id.apply_date);
			viewHolder.applyArea = (TextView) convertView.findViewById(R.id.apply_area);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.applyMan.setText(sealApplyInfo.getApply_man_name());
		viewHolder.useMan.setText(sealApplyInfo.getUse_man());
		viewHolder.useAddress.setText(sealApplyInfo.getUse_address());
		viewHolder.applyDate.setText(sealApplyInfo.getApply_date());
		viewHolder.applyArea.setText(sealApplyInfo.getArea_big() + "(" + sealApplyInfo.getArea_small() + ")");
		return convertView;
	}

	class ViewHolder {
		TextView applyMan;
		TextView useMan;
		TextView useAddress;
		TextView applyDate;
		TextView applyArea;
	}
}
