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

public class MyAdapter extends BaseAdapter {

	Context mContext;
	private List<GetListInfo> mList = null;

	public MyAdapter(Context context, List<GetListInfo> list) {
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
					R.layout.list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (mList.get(position).getBill_type().toString().equals("1")) {
			viewHolder.apply_code.setText(mList.get(position).getApply_code()
					.toString()
					+ "(请示)");
		}
		if (mList.get(position).getBill_type().toString().equals("2")) {
			viewHolder.apply_code.setText(mList.get(position).getApply_code()
					.toString()
					+ "(申请)");
		}
		viewHolder.flower_name.setText(mList.get(position).getFlower_name()
				.toString());
		viewHolder.flower_type.setText(mList.get(position).getFlower_type()
				.toString());
		viewHolder.Apply_man_name.setText(mList.get(position)
				.getApply_man_name().toString());
		viewHolder.Apply_amt.setText(mList.get(position).getApply_amt()
				.toString());
		viewHolder.apply_text.setText(mList.get(position).getApply_text()
				.toString());
		viewHolder.start_time.setText(mList.get(position).getStart_time()
				.toString());
		viewHolder.audit_order.setText(mList.get(position).getAudit_order()
				.toString());
		viewHolder.contract_code.setText(mList.get(position).getContract_code()
				.toString());

		if (mList.get(position).getArea_small().toString().equals("")) {
			viewHolder.area.setText(mList.get(position).getArea_big()
					.toString());
		} else {
			viewHolder.area.setText(mList.get(position).getArea_big()
					.toString()
					+ "("
					+ mList.get(position).getArea_small().toString()
					+ ")");
		}

		return convertView;

	}

	class ViewHolder {
		// 申请单号
		TextView apply_code = null;
		// 申请名称
		TextView flower_name = null;
		// 申请类型
		TextView flower_type = null;
		// 申请人
		TextView Apply_man_name = null;
		// 申请金额
		TextView Apply_amt = null;
		// 申请地区
		TextView area = null;
		// 申请时间
		TextView start_time = null;
		// 申请content
		TextView apply_text = null;
		// 审批顺序
		TextView audit_order = null;
		// 合同号
		TextView contract_code = null;

		public ViewHolder(View view) {
			apply_code = (TextView) view.findViewById(R.id.apply_num);
			flower_name = (TextView) view.findViewById(R.id.flower_name);
			flower_type = (TextView) view.findViewById(R.id.flower_type);
			Apply_man_name = (TextView) view.findViewById(R.id.apply_man);
			start_time = (TextView) view.findViewById(R.id.apply_date);
			Apply_amt = (TextView) view.findViewById(R.id.apply_money);
			area = (TextView) view.findViewById(R.id.apply_area);
			apply_text = (TextView) view.findViewById(R.id.apply_text);
			audit_order = (TextView) view.findViewById(R.id.audit_order);
			contract_code = (TextView) view.findViewById(R.id.contract_code);
		}
	}
}
