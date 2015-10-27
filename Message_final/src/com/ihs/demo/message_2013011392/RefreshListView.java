package com.ihs.demo.message_2013011392;

import com.ihs.message_2013011392.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

// 为ListView增加下拉刷新功能
// OnRefreshListener

public class RefreshListView extends ListView {

    private final static int RATIO = 3;

    private final static int RELEASE_TO_REFRESH = 0;
    private final static int PULL_TO_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private int headState;
    private boolean refreshEnableState = false;

    public boolean getRefreshEnableState() {
        return refreshEnableState;
    }

    public void setRefreshEnableState(boolean refreshEnableState) {
        this.refreshEnableState = refreshEnableState;
    }

    private LayoutInflater inflater;
    private LinearLayout headView;
    private TextView updatedTimeTextView;
    private ProgressBar progressBar;

    /** 用于保证 startY 的�?�在�?个完整的touch事件中只被记录一�? */
    private boolean isRecored;
    private int headViewHeight;
    private int startY;
    private boolean isBack;

    private OnRefreshListener refreshListener;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //初始�?
        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        inflater = LayoutInflater.from(context);
        addHeadView();
    }

    @SuppressLint("InflateParams")
    private void addHeadView() {
        //添加下拉刷新的HeadView
        headView = (LinearLayout) inflater.inflate(R.layout.refresh_listview_head, null);
        progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        updatedTimeTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);
        measureView(headView);
        headViewHeight = headView.getMeasuredHeight();

        headView.setPadding(0, -1 * headViewHeight, 0, 0);
        headView.invalidate();

        addHeaderView(headView, null, false);
        headState = DONE;
    }

    private void measureView(View child) {
        //测量HeadView宽高
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (getFirstVisiblePosition() > 0) {
            return super.onTouchEvent(event); // 滑动位置未在列表顶端时，不处理直接返�?
        }
        // �?测触屏移动状�?
        if (refreshEnableState) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isRecored) {
                        isRecored = true;
                        startY = (int) event.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (headState != REFRESHING && headState != LOADING) {
                        if (headState == DONE) {

                        }
                        if (headState == PULL_TO_REFRESH) {
                            headState = DONE;
                            changeHeaderViewByState();
                        }
                        if (headState == RELEASE_TO_REFRESH) {
                            headState = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                        }
                    }
                    isRecored = false;
                    isBack = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    if (!isRecored) {
                        isRecored = true;
                        startY = tempY;
                    }
                    if (headState != REFRESHING && isRecored && headState != LOADING) {

                        if (headState == RELEASE_TO_REFRESH) {
                            // �?上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - startY) / RATIO < headViewHeight) && (tempY - startY) > 0) {
                                headState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                            // �?下子推到顶了
                            else if (tempY - startY <= 0) {
                                headState = DONE;
                                changeHeaderViewByState();
                            }
                            // �?下拉了，或�?�还没有上推到屏幕顶部掩盖head的地�?
                        }
                        // 还没有到达显示松�?刷新的时�?,DONE或�?�是PULL_To_REFRESH状�??
                        if (headState == PULL_TO_REFRESH) {
                            // 下拉到可以进入RELEASE_TO_REFRESH的状�?
                            if ((tempY - startY) / RATIO >= headViewHeight) {
                                headState = RELEASE_TO_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                            } else if (tempY - startY <= 0) {
                                headState = DONE;
                                changeHeaderViewByState();
                            }
                        }

                        if (headState == DONE) {
                            if (tempY - startY > 0) {
                                headState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                        }

                        if (headState == PULL_TO_REFRESH) {
                            headView.setPadding(0, -1 * headViewHeight + (tempY - startY) / RATIO, 0, 0);

                        }

                        if (headState == RELEASE_TO_REFRESH) {
                            headView.setPadding(0, (tempY - startY) / RATIO - headViewHeight, 0, 0);
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    private void changeHeaderViewByState() {
        // 更新当前 HeadView
        switch (headState) {
            case RELEASE_TO_REFRESH:
                progressBar.setVisibility(View.VISIBLE);
                updatedTimeTextView.setVisibility(View.VISIBLE);
                break;
            case PULL_TO_REFRESH:
                progressBar.setVisibility(View.VISIBLE);
                updatedTimeTextView.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状�?�转变来�?
                if (isBack) {
                    isBack = false;
                }
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);

                progressBar.setVisibility(View.VISIBLE);
                updatedTimeTextView.setVisibility(View.VISIBLE);

                break;
            case DONE:
                headView.setPadding(0, -1 * headViewHeight, 0, 0);
                progressBar.setVisibility(View.GONE);
                updatedTimeTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public interface OnRefreshListener {
        // 下拉加载更多监听接口
        public void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        if (refreshListener != null) {
            this.refreshListener = refreshListener;
            refreshEnableState = true;
        }
    }

    private void onRefresh() {
        // 正在下拉加载更多
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    public void onRefreshComplete(int numLoaded) {
        // 下拉加载更多完成
        setSelection(numLoaded);
        headState = DONE;
        updatedTimeTextView.setText(getResources().getString(R.string.refreshListView_head_load_more));
        changeHeaderViewByState();
    }

    public void setAdapter(BaseAdapter adapter) {
        updatedTimeTextView.setText(getResources().getString(R.string.refreshListView_head_load_more));
        super.setAdapter(adapter);
    }

}
