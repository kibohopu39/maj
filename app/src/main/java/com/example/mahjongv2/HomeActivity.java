package com.example.mahjongv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TextView name , email;
    private RecyclerView mainButtons;
    private LinearLayoutManager linearLayoutManager;
    private CustomizeLayout customizeLayout;
    private MainButtonsAdapter mainButtonsAdapter;
    private Button test;
    SessionManager sessionManager;
    String uri = "@drawable/"+"mb1";
    //背景做事
    private MyService myService;
    private boolean isBind;
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyService.LocalBinder binder=(MyService.LocalBinder)iBinder;
            myService=binder.getService();
            isBind=true;
            Log.v("wei","hi");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind=false;
        }
    };

    private String[] buttonNames={"房間列表","單機小遊戲","登出"};
    private int itemcount=buttonNames.length;
    //調變器按鈕事件
    private EatList.OnItemListener onItemListener=new EatList.OnItemListener() {
        @Override
        public void onItemClick(int position) {
            //當item被點擊的時候要做的事情
            //0-->房間列表
            //1-->單機遊戲
            //2-->登出
            if (position==0){
                Intent intent = new Intent(HomeActivity.this,RoomsActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();
            }else if (position==1){
                Intent intent = new Intent(HomeActivity.this,SmallGameActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();
            }else if (position==2){
                sessionManager.logout();
            }
        }
    };

//    private CustomizScroll customizScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SessionManager.java
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        name = findViewById(R.id.name);
        email= findViewById(R.id.email);
        mainButtons=findViewById(R.id.mainButtons);
        test=findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.playeffect(1);
            }
        });
        //佈局格式
        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        customizeLayout=new CustomizeLayout();
        mainButtons.setLayoutManager(customizeLayout);
        //調變器
        mainButtonsAdapter=new MainButtonsAdapter(onItemListener);
        mainButtons.setAdapter(mainButtonsAdapter);
        //滑動監聽

//        customizScroll=new CustomizScroll(528);
//        mainButtons.setOnScrollListener(customizScroll);


        HashMap<String,String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.EMAIL);

        name.setText(mName);
        email.setText(mEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        跟myService繫結
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
//        startService(intent);

    }

    @Override
    protected void onStop() {
        if (isBind){
            unbindService(mConnection);
        }
        Intent intent=new Intent(this,MyService.class);
//        stopService(intent);
        super.onStop();
    }




    public int MBimgURI(int i){
        int getButtonsImg = getResources().getIdentifier(uri+i,null,getPackageName());
        return getButtonsImg;
    }

    private class MainButtonsAdapter extends RecyclerView.Adapter<HomeActivity.MainButtonsAdapter.viewHolder>{
        private EatList.OnItemListener onItemListener;

        public MainButtonsAdapter(EatList.OnItemListener onItemListener){
            this.onItemListener=onItemListener;
        }
        @NonNull
        @Override
        public HomeActivity.MainButtonsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_button, parent, false);
            MainButtonsAdapter.viewHolder vh=new MainButtonsAdapter.viewHolder(view,onItemListener);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull HomeActivity.MainButtonsAdapter.viewHolder holder, int position) {
            ImageView iv=holder.iv;
            TextView tv=holder.tv;

            iv.setImageResource(MBimgURI(position%3));
            tv.setText(buttonNames[position%3]);
        }

        @Override
        public int getItemCount() {
            return 3;
        }


        private class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView iv;
            TextView tv;
            EatList.OnItemListener onItemListener;

            public viewHolder(@NonNull View itemView, EatList.OnItemListener onItemListener) {
                super(itemView);
                iv=itemView.findViewById(R.id.showPicture);
                tv=itemView.findViewById(R.id.showText);
                this.onItemListener=onItemListener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onItemListener.onItemClick(getAdapterPosition());
            }
        }
    }

    //自訂佈局
    private class CustomizeLayout extends RecyclerView.LayoutManager{
        private View view;
        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            return null;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            super.onLayoutChildren(recycler, state);
            /**利用 position 挖出需要的 View
            *把 View 塞進 RecyclerView 裏頭
            *測量(measure) View 的佈局(layout)資訊
            *取得測量後的 layout 資訊
            *layout View 本身
            **/
            int itemCount = getItemCount();//取得有多少個item

                for(int position=0;position<itemCount;position++){
                    view=recycler.getViewForPosition(position);//取得第幾個 view?
                    addView(view);//把 view 放入recycler裡
                    measureChildWithMargins(view, 0, 0);// 測量 View 的佈局(layout)資訊
                    // getDecoratedMeasuredWidth(view) 可以得到 Item View 的宽度
                    // 這裡 width height space 指的,是螢幕寬高減去item寬高後的值
                    int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                    int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                    //layout View 本身
                    //把child顯示在RecyclerView上面，left，top，right，bottom為顯示的區域
                    int left=widthSpace/2;
                    int top=heightSpace/2;

                    int right=widthSpace/2+getDecoratedMeasuredWidth(view);
                    int bottom=heightSpace/2+getDecoratedMeasuredHeight(view);



                    //這樣位置就在正中間,這是第一個item的佈局位置
                    layoutDecorated(view, left, top, right, bottom);
                    //想要做出三個item,且可以繞圈圈的效果
                    //1.定義item位置0,1,2要畫在哪---v
                    //2.定義滑動時item怎麼移動
                    //3.定義滑動時itme裡的東西要放大放小
                    //4.定義滑動後,矛點位置
                    //5.哪些item是可以被點擊的,在矛點位置才可以被點
                    if(position%3==ItemSwipeConfig.DEFAULT_SHOW_ITEM){//item是2時,在左邊後方位置
                        // 按照一定的規則縮放0.75
                        view.setScaleX((1+ItemSwipeConfig.DEFAULT_SCALE)*ItemSwipeConfig.DEFAULT_SCALE);
                        view.setScaleY((1+ItemSwipeConfig.DEFAULT_SCALE)*ItemSwipeConfig.DEFAULT_SCALE);
                        view.setTranslationY(view.getMeasuredHeight()/ItemSwipeConfig.DEFAULT_TRANSLATE_Y);
                        view.setOnTouchListener(mOnTouchListener);
                    }else if (position%3==ItemSwipeConfig.DEFAULT_SHOW_ITEM-1){//在右邊後方位置
                        view.setScaleX(ItemSwipeConfig.DEFAULT_SCALE);
                        view.setScaleY(ItemSwipeConfig.DEFAULT_SCALE);
                        view.setTranslationY((-1)*view.getMeasuredHeight()/ItemSwipeConfig.DEFAULT_TRANSLATE_Y);
                        view.setTranslationX(view.getMeasuredWidth()/ItemSwipeConfig.DEFAULT_TRANSLATE_X);
                    }else if(position%3==0){
                        view.setScaleX(ItemSwipeConfig.DEFAULT_SCALE);
                        view.setScaleY(ItemSwipeConfig.DEFAULT_SCALE);
                        view.setTranslationY((-1)*view.getMeasuredHeight()/ItemSwipeConfig.DEFAULT_TRANSLATE_Y);
                        view.setTranslationX((-1)*view.getMeasuredWidth()/ItemSwipeConfig.DEFAULT_TRANSLATE_X);

                    }
                }


        }

        @Override
        public boolean canScrollHorizontally() {//能否水平滑動
            return true;
        }


        private boolean item_x=false;
        private boolean item_y=false;
        private boolean item1_x=false;
        private boolean item1_y=false;

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            //手指向右滑--->dx為負
//            int position=0;
//            getChildAt(position);//獲得item,目前只有三個

            float temp0_x=getChildAt(0).getTranslationX();
//            float temp1_x=getChildAt(1).getTranslationX();
            float temp2_x=getChildAt(2).getTranslationX();
            float temp0_y=getChildAt(0).getTranslationY();
            float temp1_y=getChildAt(1).getTranslationY();
//            float temp2_y=getChildAt(2).getTranslationY();
            //item0的X軸
            if (temp0_x>=197){
                item_x=true;
            }else if (temp0_x<=-197){
                item_x=false;
            }
            if(!item_x) {//是否要開始回走,否
                if (temp0_x < 197 || temp0_x > -197) {
                    getChildAt(0).setTranslationX(temp0_x - dx / 2);
                }
            } else if (item_x) {//是否開始回走,是
                if (temp0_x - dx / 2 < 197 || temp0_x - dx / 2 > -197) {
                    getChildAt(0).setTranslationX(temp0_x + dx / 2);
                }
            }
//            getChildAt(1).setTranslationX(temp1_x+1.5f*dx/2);
//
            getChildAt(0).setTranslationY(temp0_y-dx/2);
//            getChildAt(1).setTranslationY(temp1_y+dx/2);
//            getChildAt(2).setTranslationY(temp2_y+dx/2);
            return super.scrollHorizontallyBy(dx, recycler, state);
        }
    }

    private  View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };

    //定義滑動時的item表現
    private class CustomizScroll extends RecyclerView.OnScrollListener{
        private int movementX=0;
        private int movementY=0;
        private int itemWidth;
        private float animationValue = 0.2f;
        public CustomizScroll(int itemwidth){
            this.itemWidth=itemwidth;
        }
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


        }
        private void setItemAnim(RecyclerView recyclerView, int dx, int dy){
            float fmovementX=(float)movementX;
            float offset=fmovementX/itemWidth;//滑動了多少個item單位寬
            Log.v("wei","滑動距離:"+movementX);
            int position=(int) offset;
            float percent=offset-position;
            // 左
            View mLeftView = recyclerView.getChildAt(position%3+1);
            // 中
            View mCurView = recyclerView.getChildAt(position%3);
            //
            View mRightView = recyclerView.getChildAt(position%3-1);
//            View mRRView = recyclerView.layoutManager!!.findViewByPosition(position + 2)
            float sideTransferValue = 1 - animationValue + percent * animationValue;
            float currentItemsScaleValue = 1 - percent * animationValue;

            if (mLeftView != null) {
                mLeftView.setScaleX(sideTransferValue);
                mLeftView.setScaleY(sideTransferValue);
            }
            if (mCurView != null) {
                mCurView.setScaleX(currentItemsScaleValue);
                mCurView.setScaleY(currentItemsScaleValue);
            }
            if (mRightView != null) {
                mRightView.setScaleX(sideTransferValue);
                mRightView.setScaleY(sideTransferValue);
            }

        }


    }
}
