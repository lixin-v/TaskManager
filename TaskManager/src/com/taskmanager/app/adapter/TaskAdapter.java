package com.taskmanager.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.taskmanager.app.bean.Task;
import com.taskmanager.app.dao.TaskDao;
import com.taskmanager.app.dao.impl.TaskDaoImpl;
import com.taskmanager.app.ui.TaskDesc;
import com.taskmanager.app.widget.ItemsViewPager;
import com.vegeta.taskmanager.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskAdapter extends BaseAdapter {
	private Context context;
	private List<Task> taskItems;
	private LayoutInflater inflater;
	private int itemResourceId;

	private View view1;
	private View view2;
	private View view3;
	private List<View> views;
	protected static String TAG = "TaskAdapter";

	static class ListItemView { // 自定义控件集合
		public TextView txtName;
		public TextView txtTime;
		public TextView txtIsFinish;
	}

	public TaskAdapter(Context context, List<Task> data, int itemResourceId) {
		this.context = context;
		this.taskItems = data;
		this.itemResourceId = itemResourceId;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		return taskItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Task task = taskItems.get(position);
		final int taskId=task.getId();
		views = new ArrayList<View>();

		view1 = LayoutInflater.from(context).inflate(R.layout.fragment1, null);
		view2 = LayoutInflater.from(context).inflate(R.layout.task_list_item,
				null);
		view3 = LayoutInflater.from(context).inflate(R.layout.fragment3, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		view2.setClickable(true);
		view2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(TAG, "item  Onclick");
				Intent intent=new Intent(context,TaskDesc.class);
				
				Bundle extras=new Bundle();
				extras.putInt("taskId", taskId);
				intent.putExtras(extras);
				context.startActivity(intent);
			}
		});
		ListItemView listItemView = null;
		listItemView = new ListItemView();
		listItemView.txtName = (TextView) view2
				.findViewById(R.id.frame_task_name);
		listItemView.txtTime = (TextView) view2
				.findViewById(R.id.frame_task_time);
		listItemView.txtIsFinish = (TextView) view2
				.findViewById(R.id.frame_task_isfinish);
		if (convertView == null) {
			convertView = inflater.inflate(itemResourceId, null);

			//convertView.setTag(listItemView);
		} else {
			//listItemView = (ListItemView) convertView.getTag();
		}

		ItemsViewPager vp = (ItemsViewPager) convertView
				.findViewById(R.id.tabcontent_vp);
		// 给viewpager设置适配器
		TabAdapter adapter = new TabAdapter();
		vp.setAdapter(adapter);
		// 第一次加载都是设置的显示第2个页面
		vp.setCurrentItem(1);
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				if (pos == 0)// 完成
				{
					// 震动一下
					Vibrator vib = (Vibrator) TaskAdapter.this.context
							.getSystemService(Service.VIBRATOR_SERVICE);
					vib.vibrate(50);
					finishTask(position);

				} else if (pos == 2)// 删除
				{
					Vibrator vib = (Vibrator) TaskAdapter.this.context
							.getSystemService(Service.VIBRATOR_SERVICE);
					vib.vibrate(50);
					deleteItem(position);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		Log.v(TAG, taskItems.get(position) + "---position" + position);
		

		listItemView.txtName.setText(task.getName());
		listItemView.txtTime.setText(task.getStartTime() + " 至 "
				+ task.getEndTime());
		listItemView.txtIsFinish.setText(task.isFinish() ? "已完成" : "未完成");
		return convertView;
	}

	protected void finishTask(int position) {
		TaskDao dao=new TaskDaoImpl(context);
		Task task=(Task)taskItems.get(position);
		task.setFinish(true);
		dao.update(task);
		notifyDataSetChanged();
	}

	protected void deleteItem(int position) {
		Log.v(TAG, "position:" + position);
		TaskDao dao=new TaskDaoImpl(context);
		Log.v(TAG, "taskItems.toString():" + taskItems.toString());
		Task task=(Task)taskItems.get(position);
		taskItems.remove(position);
		if(dao.delete(task.getId())){
			Toast.makeText(context, R.string.message_task_delete_success, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(context, R.string.message_task_delete_error, Toast.LENGTH_SHORT).show();
		}
		this.notifyDataSetChanged();
	}

	private class TabAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewGroup p = (ViewGroup) views.get(arg1).getParent();
			if (p != null) {
				p.removeView(views.get(arg1));
			}

			((ViewPager) arg0).addView(views.get(arg1));

			return views.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {

			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

}
