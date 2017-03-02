package com.example.dell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.adapter.MyRecyclerAdapter;
import com.example.dell.listener.HttpCallBackListener;
import com.example.dell.utils.HttpUtils;
import com.example.dell.vo.ShowVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private List<ShowVO> mList = new ArrayList<ShowVO>();
    private SwipeRefreshLayout mRefresh;
    //时间值
    private String mTime = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindID();
        //设置toolbar
        setSupportActionBar(mToolbar);
        getData();
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 2);
        mAdapter = new MyRecyclerAdapter(mList);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        //一开始刷新数据
        if (mRefresh != null) {
            mRefresh.setRefreshing(true);
        }
        //下拉刷新
        refreshData();
    }

    private void refreshData() {
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }).start();
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData() {
        HttpUtils.getData("http://www.grwork.cn/jump/code/file.html", new HttpCallBackListener() {
            @Override
            public void onSuccess(String data) {
                //成功
                //联网获取
                Document document = Jsoup.parse(data);
                //先获取更新时间
                Elements strong = document.select("strong");
                for (Element element : strong) {
                    String ownText = element.ownText();
                    if (ownText.indexOf("更新Shadowsocks账号") >= 0) {
                        Log.e("4399", ownText);
                        if (mTime.equals(ownText)) {
                            //相等，新的更新
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "没有新节点更新", Toast.LENGTH_SHORT).show();
                                    //隐藏进度
                                    mRefresh.setRefreshing(false);
                                }
                            });
                            return;
                        }
                        //有更新
                        mTime = ownText;
                    }
                }
                mList.clear();
                //获取数据
                Elements table = document.select("table").select("tr");
                for (Element element : table) {
                    Elements td = element.select("td");
                    String temp = "";
                    for (int i = 0; i < td.size(); i++) {
                        String da = "";
                        if (i == 6) {
                            final Element ttt = td.get(i);
                            Elements a = ttt.select("a");
                            for (Element as : a) {
                                String href = as.attr("href");
                                if (href.startsWith("http://pan.baidu.com/")) {
//                                    Log.e("链接", href);
                                    //删除http://pan.baidu.com/share/qrcode?w=300&h=300&url=
                                    int length = "http://pan.baidu.com/share/qrcode?w=300&h=300&url=".length();
                                    String substring = href.substring(length);
                                    da = substring;
                                }
                            }
                        } else {
                            //文档,前五个有样式
                            final Element ttt = td.get(i);
                            //判断是服务器名称否有样式
                            Elements fuqname = ttt.select("span").select("strong");
                            if (fuqname.size() != 0) {
                                //有样式
                                Element sss = fuqname.get(0);
//                                Log.e("Doc_style", sss.ownText());
                                da = sss.ownText();
                            } else {
                                //判断提供者名字是否有样式
                                Elements name = ttt.select("strong").select("span");
                                if (name.size() != 0) {
                                    //有样式
                                    Element sss = name.get(0);
//                                    Log.e("Doc_style", sss.ownText());
                                    da = sss.ownText();
                                } else {
//                                    Log.e("Doc", ttt.ownText());
                                    da = ttt.ownText();
                                }
                            }

                        }
                        temp = temp + da + "!!";
                    }
                    String[] split = temp.split("!!");
                    if (split.length == 7) {
                        Log.e("AAA", split.length + "");
                        //判断国旗
                        int country = 0;
                        if (split[0].startsWith("美国")) {
                            Log.e("g", "美国");
                            country = R.drawable.flag_usa;
                        } else if (split[0].startsWith("新加坡")) {
                            Log.e("g", "新加坡");
                            country = R.drawable.flag_singapore;
                        } else if (split[0].startsWith("俄罗斯")) {
                            Log.e("g", "俄罗斯");
                            country = R.drawable.flag_russia;
                        } else if (split[0].startsWith("加拿大")) {
                            Log.e("g", "加拿大");
                            country = R.drawable.flag_canada;
                        } else if (split[0].startsWith("香港") || split[0].startsWith("北京") || split[0].startsWith("台湾")) {
                            Log.e("g", "中华人民共和国");
                            country = R.drawable.flag_china;
                        } else if (split[0].startsWith("日本")) {
                            Log.e("g", "日本");
                            country = R.drawable.flag_japan;
                        }
                        ShowVO showVO = new ShowVO(split[0], split[1], split[2], split[3], split[4], split[5], split[6], country);
                        mList.add(showVO);
                        showVO = null;
                    }

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        if (mRefresh != null) {
                            mRefresh.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                //错误
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        if (mRefresh != null) {
                            mRefresh.setRefreshing(false);
                        }
                        Toast.makeText(MainActivity.this, "链接超时请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 菜单监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_1:
                Intent intent = new Intent(MainActivity.this, ExplainActivity.class);
                intent.putExtra(ExplainActivity.EXPLAIN_CONTENT,
                        "\n1.首页面进行加载各个节点的数据，如有更新可以下拉刷新。" +
                                "\n\n2.点击各个节点可以看到详情页面，详情包括二维码" +
                                "、IP地址、密码、加密方式。\n\n3.可以通过复制账号等" +
                                "信息上网，也可以通过扫描二维码来上网。\n\n4.长按二维码可" +
                                "触发下载该节点的二维码\n\n5.本项目" +
                                "只是个人学习练习的项目。\n\n6.个人项目，请勿用于商业" +
                                "用途。\n\n7.请在下载24小时之内删除该软件。\n");
                intent.putExtra(ExplainActivity.EXPLAIN_TITLE, "使用说明");
                startActivity(intent);
                break;
            case R.id.menu_2:
                Intent intent2 = new Intent(MainActivity.this, ExplainActivity.class);
                intent2.putExtra(ExplainActivity.EXPLAIN_CONTENT, "\n1.本软件是个人在学习android的过程中，" +
                        "自己突发奇想做出来的，来检验自己的开发水平，节点采用的第三方分享的免费节点，软件" +
                        "有许多的不足，自己将会进行不断地完善，来改进本项目。\n\n2.软件会进行不断更新，如启动" +
                        "时发现更新，请及时更新。\n");
                intent2.putExtra(ExplainActivity.EXPLAIN_TITLE, "关于我们");
                startActivity(intent2);
                break;
        }
        return true;
    }

    private void bindID() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_show);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.main_refresh_load);
    }

}
