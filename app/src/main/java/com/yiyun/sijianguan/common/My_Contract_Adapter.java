package com.yiyun.sijianguan.common;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.activity.GetListInfo;

import java.util.List;

public class My_Contract_Adapter extends BaseAdapter {

	Context mContext;
	private List<GetListInfo> mList = null;

	public My_Contract_Adapter(Context context, List<GetListInfo> list) {
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
					R.layout.contract_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.contract_name.setText(mList.get(position).getContract_name()
				.toString());
		viewHolder.contract_type.setText(mList.get(position).getContract_type()
				.toString());
		viewHolder.contract_man_name.setText(mList.get(position)
				.getContract_man_name().toString());
		viewHolder.contract_money.setText(mList.get(position)
				.getContract_money().toString());
		viewHolder.contract_text.setText(mList.get(position).getContract_text()
				.toString());
		viewHolder.contract_date.setText(mList.get(position).getContract_date()
				.toString());
		viewHolder.audit_order.setText(mList.get(position).getAudit_order()
				.toString());
		viewHolder.contract_code.setText(mList.get(position).getContract_code()
				.toString());
		viewHolder.contract_A.setText(mList.get(position).getContract_A()
				.toString());
		viewHolder.contract_B.setText(mList.get(position).getContract_B()
				.toString());

		if (mList.get(position).getArea_small().toString().equals("")) {
			viewHolder.contract_area.setText(mList.get(position).getArea_big()
					.toString());
		} else {
			viewHolder.contract_area.setText(mList.get(position).getArea_big()
					.toString()
					+ "("
					+ mList.get(position).getArea_small().toString()
					+ ")");
		}

		return convertView;

	}

	class ViewHolder {
		// 合同号
		TextView contract_code = null;
		// 申请人
		TextView contract_man_name = null;
		// 合同名称
		TextView contract_name = null;
		// 合同类型
		TextView contract_type = null;

		// 合同金额
		TextView contract_money = null;
		// 合同地区
		TextView contract_area = null;
		// 合同时间
		TextView contract_date = null;
		// 合同content
		TextView contract_text = null;
		// 审批顺序
		TextView audit_order = null;
		// 甲方
		TextView contract_A = null;
		// 乙方
		TextView contract_B = null;

		public ViewHolder(View view) {
			contract_man_name = (TextView) view.findViewById(R.id.contract_man);
			contract_code = (TextView) view.findViewById(R.id.contract_code);
			contract_name = (TextView) view.findViewById(R.id.contract_name);
			contract_type = (TextView) view.findViewById(R.id.contract_type);
			contract_date = (TextView) view.findViewById(R.id.contract_date);
			contract_money = (TextView) view.findViewById(R.id.contract_money);
			contract_area = (TextView) view.findViewById(R.id.contract_area);
			contract_text = (TextView) view.findViewById(R.id.contract_text);
			audit_order = (TextView) view.findViewById(R.id.contract_order);
			contract_A = (TextView) view.findViewById(R.id.contract_A);
			contract_B = (TextView) view.findViewById(R.id.contract_B);
		}
	}
}
