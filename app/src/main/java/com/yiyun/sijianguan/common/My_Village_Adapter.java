package com.yiyun.sijianguan.common;

import java.util.List;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.activity.GetListInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class My_Village_Adapter extends BaseAdapter {

	Context mContext;
	private List<GetListInfo> mList = null;

	public My_Village_Adapter(Context context, List<GetListInfo> list) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mList != null) {
			return mList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.village_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.apply_village.setText(mList.get(position).getArea_small()
				.toString());
		viewHolder.work_type.setText(mList.get(position).getWORK_TYPE()
				.toString());
		viewHolder.count_num.setText(mList.get(position).getCount_num()
				.toString());
		

		return convertView;

	}

	class ViewHolder {
		// 村庄
		TextView apply_village = null;
		// 数量
		TextView count_num = null;
		// 类型
		TextView work_type = null;

		public ViewHolder(View view) {
			apply_village = (TextView) view.findViewById(R.id.apply_village);
			count_num = (TextView) view.findViewById(R.id.count_num);
			work_type = (TextView) view.findViewById(R.id.work_type);
		}
	}
}
