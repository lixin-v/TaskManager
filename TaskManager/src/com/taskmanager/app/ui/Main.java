package com.taskmanager.app.ui;

import java.security.Guard;
import java.util.List;






import com.taskmanager.app.adapter.TaskAdapter;
import com.taskmanager.app.bean.Task;
import com.taskmanager.app.dao.TaskDao;
import com.taskmanager.app.dao.impl.TaskDaoImpl;
import com.taskmanager.app.widget.RefreshableView;
import com.taskmanager.app.widget.RefreshableView.PullToRefreshListener;
import com.taskmanager.app.widget.ScrollLayout;
import com.taskmanager.app.widget.ScrollLayout.OnViewChangeListener;
import com.taskmanager.app.widget.SlideListView;
import com.vegeta.taskmanager.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private final String TAG = "Main";
	private ImageButton ibtnAddTask;
	private ListView lvTask;
	private ListView lvHistoryTask;
	private TaskAdapter taskAdapter;
	private TaskAdapter historyTaskAdapter;
	private RadioGroup radioGroup;
	private ScrollLayout scrollLayout;
	private TextView txtTitle;
	RefreshableView refreshableView;

	private final char FLING_CLICK = 0;
	private final char FLING_LEFT = 1;
	private final char FLING_RIGHT = 2;
	private char flingState = FLING_CLICK;
	private int currentViewIndex = 0;

	
	List<Task> list;
	List<Task> finishedList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
		Log.d(TAG, "-->>onCreate");
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "-->>onStart");
		super.onStart();
		initData();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "-->>onResume");
		super.onResume();
	}

	private void initView() {
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view_current);
		ibtnAddTask = (ImageButton) findViewById(R.id.main_head_add_task);
		lvTask = (SlideListView) findViewById(R.id.main_listview_today_task);
		lvHistoryTask=(SlideListView)findViewById(R.id.main_listview_history_task);
		radioGroup = (RadioGroup) findViewById(R.id.main_footbar_btn_group);
		scrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
		txtTitle = (TextView) findViewById(R.id.main_head_title);
		scrollLayout.setIsScroll(false);
		//refreshableView.findViewById(R.id.pull_to_refresh_head).setVisibility(View.GONE);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 0);
		scrollLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		// 添加任务
		ibtnAddTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, AddTask.class);
				startActivity(intent);
			}
		});
		
		lvTask.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("ontouch", "-->>");
				return false;
			}
		});
		
		lvTask.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Log.v(TAG, "item click");
				switch (flingState) {
				// 处理左滑事件
				case FLING_LEFT:
					Toast.makeText(Main.this, "Fling Left:" + pos,
							Toast.LENGTH_SHORT).show();
					flingState = FLING_CLICK;
					break;
				// 处理右滑事件
				case FLING_RIGHT:
					Toast.makeText(Main.this, "Fling Right:" + pos,
							Toast.LENGTH_SHORT).show();
					flingState = FLING_CLICK;
					break;
				// 处理点击事件
				case FLING_CLICK:
					switch (pos) {
					case 0:
						break;
					case 1:
						break;
					}
					Toast.makeText(Main.this, "Click Item:" + pos,
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		});
		
		RadioButton btnDefault=(RadioButton)findViewById(R.id.main_footbar_home);
		btnDefault.setChecked(true);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (group.getCheckedRadioButtonId()) {
				case R.id.main_footbar_home:
					if (currentViewIndex != 0) {
						scrollLayout.scrollToScreen(0);
						txtTitle.setText(R.string.title_today_task);
						ibtnAddTask.setVisibility(View.VISIBLE);
					}
					Log.d(TAG, "-->>home check");
					break;
				case R.id.main_footbar_history:
					if (currentViewIndex != 1) {
						scrollLayout.scrollToScreen(1);
						txtTitle.setText(R.string.title_history_task);
						ibtnAddTask.setVisibility(View.GONE);
					}
					break;
				case R.id.main_footbar_setting:
					if (currentViewIndex != 2) {
						scrollLayout.scrollToScreen(2);
						txtTitle.setText(R.string.title_setting);
						ibtnAddTask.setVisibility(View.GONE);
					}
					break;
				default:
					break;
				}
			}
		});

		// 左右滑动监听
		scrollLayout.SetOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int viewIndex) {
				if (currentViewIndex == viewIndex)
					return;
				currentViewIndex = viewIndex;
				Log.d(TAG, "-->>OnViewChange viewIndex:" + viewIndex);
				switch (viewIndex) {
				case 0:
					txtTitle.setText(R.string.title_today_task);
					ibtnAddTask.setVisibility(View.VISIBLE);
					break;
				case 1:
					txtTitle.setText(R.string.title_history_task);
					ibtnAddTask.setVisibility(View.GONE);
					loadHistoryData();
					break;
				case 2:
					txtTitle.setText(R.string.title_setting);
					ibtnAddTask.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});
	}

	protected void loadHistoryData() {
		finishedList.clear();
		finishedList.addAll(getFinishedTasks());
		historyTaskAdapter.notifyDataSetChanged();
	}

	private void initData() {
		list = getTaskList();
		finishedList=getFinishedTasks();
		
		Log.v(TAG, "finishedList-->>"+finishedList.toString());
		taskAdapter = new TaskAdapter(this, list, R.layout.vp_item);
		lvTask.setAdapter(taskAdapter);
		historyTaskAdapter=new TaskAdapter(this, finishedList, R.layout.vp_item);
		lvHistoryTask.setAdapter(historyTaskAdapter);
	}

	private List<Task> getFinishedTasks() {
		TaskDao dao = new TaskDaoImpl(this);
		return dao.selectFinishedByPage(0,50);
	}

	private List<Task> getTaskList() {
		TaskDao dao = new TaskDaoImpl(this);
		return dao.selectByPage(0, 100);
	}

	

}
