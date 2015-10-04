package com.wz.mobilesafetest2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wz.mobilesafetest2.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author goudan
 * @公司 黑马程序员 可以上拉下拉刷新的listview
 * @time 下午4:16:12
 * @date 2015-9-1
 * 
 * @Author $Author: goudan $
 * @Date $Date: 2015-09-06 11:32:44 +0800 (Sun, 06 Sep 2015) $
 * @Id $Id: RefreshListView.java 46 2015-09-06 03:32:44Z goudan $
 * @Rev $Rev: 46 $
 * @URL $URL:
 *      https://188.188.1.100/svn/smartbj/trunk/SmartBJ/src/com/itheima11/smartbj
 *      /view/RefreshListView.java $
 */
public class RefreshListView extends ListView {

	private LinearLayout mHeadContainer;
	private ImageView iv_arrow;
	private ProgressBar pb_loading;
	private TextView tv_desc;
	private TextView tv_time;
	private View mFootView;
	private View mLunboView;
	private LinearLayout ll_head_view;
	private int mMeasuredHeadViewHeight;
	private int mMeasuredFootViewHeight;
	private float downY = -1; // 没有值

	private static final int STATE_PULLDOWN = 1;// 下拉刷新的状态
	private static final int STATE_RELEASE = 2;// 松开刷新的状态
	private static final int STATE_REFRESHING = 3;// 正在刷新状态

	private int current_State = STATE_PULLDOWN;// 默认设置成下拉刷新的状态
	private RotateAnimation downArrow;
	private RotateAnimation upArrow;
	private int mListViewY = -1;

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
		initAnimation();
		initEvent();
	}
	
	//设置是否启用下拉刷新头
	public void isEnableRefreshHead(boolean isEnable){
		this.mIsEnableRereshHead = isEnable;
	}
	
	//设置是否启用下拉刷新尾
	public void isEnableRefreshFoot(boolean isEnable) {
		mIsEnableRefreshFoot = isEnable;
	}

	private boolean loadMore = false;

	private void initEvent() {
		// 给listview添加滑动的事件
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				if (!mIsEnableRefreshFoot){
					return;
				}
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//不是静止
					// 滑动到最后一条数据显示
					if (getLastVisiblePosition() >= getAdapter().getCount() - 1) {
System.out.println("加载更多界面显示");						
						// 最后一条数据显示
						// 显示尾部加载更多数据的View
						mFootView.setPadding(0, 0, 0, 0);
						loadMore = true;
						// 加载更多数据
						if (mOnRefreshingDataListener != null) {
							// 回调加载更多数据的业务
							mOnRefreshingDataListener.onFooterFreshing();
						}
						//显示加载更多
						smoothScrollToPosition(getAdapter().getCount());
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				

			}
		});

	}

	private void initAnimation() {
		// 初始化箭头的动画

		// 逆时针旋转180度 ，锚点是中心点
		upArrow = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		upArrow.setFillAfter(true);// 停留动画结束的状态(阴影)
		upArrow.setDuration(500);// 动画的时长

		// 逆时针旋转180度 ，锚点是中心点
		downArrow = new RotateAnimation(-180, -360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		downArrow.setFillAfter(true);// 停留动画结束的状态(阴影)
		downArrow.setDuration(500);// 动画的时长

	}

	private OnRefreshingDataListener mOnRefreshingDataListener;// 声明接口回调的对象
	private boolean mIsEnableRereshHead;//是否启用下拉刷新头
	private boolean mIsEnableRefreshFoot;

	public void setOnRefreshingDataListener(OnRefreshingDataListener listener) {
		this.mOnRefreshingDataListener = listener;
	}

	// 完成刷新
	public void finishRefreshing() {
System.out.println(loadMore);		
		// 是下拉刷新 还是加载更多
		if (loadMore) {
			// 尾部刷新
			loadMore = false;
			
			//隐藏尾部
			mFootView.setPadding(0, 0, 0, 0);
			
		} else {
			// 更新刷新时间
			tv_time.setText(getCurrentDatetime());
			iv_arrow.setVisibility(View.VISIBLE);// 隐藏箭头
			pb_loading.setVisibility(View.GONE);// 显示正在刷新的进度条
			tv_desc.setText("下拉刷新");
			current_State = STATE_PULLDOWN;// 初始化状态为下拉刷新状态
			// 隐藏刷新头
			ll_head_view.setPadding(0, -mMeasuredHeadViewHeight, 0, 0);
		}
	}

	private String getCurrentDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 刷新数据的监听回调
	 */
	public interface OnRefreshingDataListener {
		// listview head刷新数据的回调
		void onHeadRefreshing();

		void onFooterFreshing();// listview尾部刷新数据
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//是否启用下拉刷新头
		if (!mIsEnableRereshHead) {
			return super.onTouchEvent(ev);
		}
		// 如果处于正在刷新状态，不让触摸滑动事件生效
		if (current_State == STATE_REFRESHING) {
			// 不让触摸滑动事件生效
			return true;
		}

		// 轮播图没有完全显示 ，不能拖动,走的是listview的原生事件
		if (!isLunboFullVisible()) {
			// 没有完全显示，走的是listview的原生事件
			return super.onTouchEvent(ev);
		}
		// 覆盖touch方法 处理不同的事件
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 按下
			downY = ev.getY();

			break;
		case MotionEvent.ACTION_MOVE:

			if (downY == -1) { // 防止按下的动作没有获取到y的坐标
				downY = ev.getY();
			}

			float moveY = ev.getY();

			int dy = Math.round(moveY - downY);
			// 滑动
			// 如果listview显示了第一个数据 并且从上往下滑动,才控制下拉刷新头的显示
			if (getFirstVisiblePosition() == 0 && dy > 0) {
				// 设置下拉刷新头的显示
				// 自己处理事件
				// 不断的设置下拉刷新头的padingTop的值
				int tempHeight = dy + (-mMeasuredHeadViewHeight);

				if (tempHeight >= 0) {
					if (current_State != STATE_RELEASE) {// 处理不同状态的切换
						// 松开刷新状态
						current_State = STATE_RELEASE;// 状态改变
						// 处理状态
						processState();
					}
				} else {
					if (current_State != STATE_PULLDOWN) {// 处理不同状态的切换
						// 下拉刷新状态
						current_State = STATE_PULLDOWN;// 下拉刷新
						// 处理状态
						processState();
					}
				}

				ll_head_view.setPadding(0, tempHeight, 0, 0);

				// 用自己事件，listview原生的onTouchEvent事件无效
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			// 松开

			// 判断状态，如果是下拉刷新的状态：隐藏下拉刷新的View
			if (current_State == STATE_PULLDOWN) {
				// 隐藏刷新头的view
				ll_head_view.setPadding(0, -mMeasuredHeadViewHeight, 0, 0);
			} else if (current_State == STATE_RELEASE) {
				// 如果是松开刷新的状态，切换到正在刷新状态
				current_State = STATE_REFRESHING;// 状态改为正在刷新
				// 刷新头View完全显示
				ll_head_view.setPadding(0, 0, 0, 0);
				// 处理状态
				processState();

				// 干活
				if (mOnRefreshingDataListener != null) {
					// 调用刷新数据的业务
					mOnRefreshingDataListener.onHeadRefreshing();
				}
			}
			break;

		default:
			break;
		}

		// 用listview原生的滑动事件
		return super.onTouchEvent(ev);
	}

	// 判断轮播图是否完全显示
	private boolean isLunboFullVisible() {
		// 判断轮播图是否完全显示
		if (mLunboView == null) {
			return true;//相当于完全显示
		}
		int[] location = new int[2];// x,y坐标

		if (mListViewY == -1) {
			// 获取ListView 在屏幕中的y坐标，永远不变,只获取一次
			this.getLocationOnScreen(location);
			mListViewY = location[1];
		}

		// 获取轮播图在屏幕中的位置
		mLunboView.getLocationOnScreen(location);

		// location[1] 存放的是轮播图在屏幕中的y坐标

		// 判断
		if (location[1] < mListViewY) {
			// 轮播图没有完全显示
			return false;
		}

		return true;// 完全显示
	}

	private void processState() {
		switch (current_State) {
		case STATE_PULLDOWN:
			// 下拉刷新状态
			// 增加动画
			iv_arrow.startAnimation(downArrow);
			tv_desc.setText("下拉刷新");
			break;
		case STATE_RELEASE:
			// 松开刷新状态
			// 增加动画
			iv_arrow.startAnimation(upArrow);
			tv_desc.setText("松开刷新");
			break;
		case STATE_REFRESHING:
			// 正在刷新的状态
			iv_arrow.clearAnimation();// 清除动画
			iv_arrow.setVisibility(View.GONE);// 隐藏箭头
			pb_loading.setVisibility(View.VISIBLE);// 显示正在刷新的进度条
			tv_desc.setText("正在刷新");
			// System.out.println("正在刷新");
			break;
		default:
			break;
		}
	}

	private void initView() {
		// 添加下拉刷新头
		initHead();
		// 添加下拉刷新尾
		initFoot();

	}

	public void addLunboView(View lunboView) {
		mLunboView = lunboView;
		// listview头部分又添加了轮播图
		mHeadContainer.addView(lunboView);
	}

	private void initFoot() {
		mFootView = View.inflate(getContext(), R.layout.foot_listview_refresh,
				null);

		mFootView.measure(0, 0);
		mMeasuredFootViewHeight = mFootView.getMeasuredHeight();
		addFooterView(mFootView);

		// 隐藏脚布局
		mFootView.setPadding(0, -mMeasuredFootViewHeight, 0, 0);
		//mFootView.setPadding(0, 0, 0, 0);
	}

	private void initHead() {
		mHeadContainer = (LinearLayout) View.inflate(getContext(),
				R.layout.head_container_listview_refresh, null);

		// 下拉刷新头根布局
		ll_head_view = (LinearLayout) mHeadContainer
				.findViewById(R.id.ll_head_root);

		// 获取子组件

		iv_arrow = (ImageView) mHeadContainer.findViewById(R.id.iv_head_arrow);
		pb_loading = (ProgressBar) mHeadContainer
				.findViewById(R.id.pb_head_loading);

		tv_desc = (TextView) mHeadContainer.findViewById(R.id.tv_head_desc);
		tv_time = (TextView) mHeadContainer.findViewById(R.id.tv_head_time);

		// 添加的listview的头
		addHeaderView(mHeadContainer);

		// ll_head_view 隐藏 top -height
		ll_head_view.measure(0, 0);

		mMeasuredHeadViewHeight = ll_head_view.getMeasuredHeight();

		ll_head_view.setPadding(0, -mMeasuredHeadViewHeight, 0, 0);

	}

	public RefreshListView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

}
