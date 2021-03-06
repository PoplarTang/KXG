package com.cqts.kxg.classify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.base.BaseFragment;
import com.base.BaseValue;
import com.base.utils.MyGridDecoration;
import com.cqts.kxg.R;
import com.cqts.kxg.adapter.ClassifyListAdapter;
import com.cqts.kxg.adapter.ClassifyRVAdapter;
import com.cqts.kxg.bean.ClassifyListInfo;
import com.cqts.kxg.bean.ClassifyListInfo.ClassifyChildInfo;
import com.cqts.kxg.home.SearchActivity;
import com.cqts.kxg.hot.HotFragment;
import com.cqts.kxg.main.MyFragment;
import com.cqts.kxg.utils.MyHttp;
import com.cqts.kxg.utils.SPutils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ClassifyFragment extends MyFragment implements View.OnClickListener {
    private ArrayList<ClassifyListInfo> classifyListInfos = new ArrayList<ClassifyListInfo>();
    private ArrayList<ClassifyChildInfo> classifyChildInfos = new ArrayList<ClassifyChildInfo>();
    private RecyclerView classify_rv;
    private ListView classify_list;
    private ClassifyListAdapter listAdapter;
    private ClassifyRVAdapter adapter2;

    public static ClassifyFragment fragment;

    public static ClassifyFragment getInstance() {
        if (fragment == null) {
            fragment = new ClassifyFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_classify, null);
            InitView();
            getData();
        }
        return view;
    }

    private void InitView() {
        classify_rv = (RecyclerView) view.findViewById(R.id.classify_rv);
        classify_list = (ListView) view.findViewById(R.id.classify_list);
        ImageView search_img = (ImageView) view.findViewById(R.id.search_img);
        search_img.setOnClickListener(this);
        InitRecyclerView();
        InitListView();
    }

    /**
     * 在SP总获取分类的缓存数据(大于24小时则重新请求)
     */
    private void getData() {
        long nowTime = System.currentTimeMillis();//获取系统当前时间
        Long classifyTime = SPutils.getClassifyTime();
        String classifyData = SPutils.getClassifyData();

        //数据不为空并且时间小于24小时(8640000L毫秒)的情况下提取以前的数据,否则从网络获取
        if (classifyTime != 0 && nowTime < classifyTime + 86400000L && !classifyData.isEmpty()) {
            setData(classifyData);
        }else {
            toHttp();
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    private void toHttp() {
        MyHttp.category(http, null, new MyHttp.MyHttpResult() {
            @Override
            public void httpResult(Integer which, int code, String msg, Object bean) {
                if (code != 0) {
                    showToast(msg);
                    return;
                }

                classifyListInfos.addAll((ArrayList<ClassifyListInfo>) bean);
                ClassifyListInfo classifyListInfoHot = new ClassifyListInfo();
                for (int i = 0; i < classifyListInfos.size(); i++) {
                    if (classifyListInfos.get(i).cat_id == -1) {
                        classifyListInfoHot = classifyListInfos.get(i);
                        classifyListInfoHot.ischecked = true;
                        classifyListInfos.remove(i);
                        classifyListInfos.add(0, classifyListInfoHot);
                        break;
                    }
                }
                String beanStr = BaseValue.gson.toJson(classifyListInfos);
                SPutils.setClassifyData(beanStr);
                SPutils.setClassifyTime(System.currentTimeMillis());
                setData(beanStr);
            }
        });
    }

    private void setData(String data) {
        ArrayList<ClassifyListInfo> classifyListInfo = BaseValue.gson.fromJson(data, new TypeToken<ArrayList<ClassifyListInfo>>() {
        }.getType());
        classifyListInfos.clear();
        classifyListInfos.addAll(classifyListInfo);
        listAdapter.notifyDataSetChanged();
        classifyChildInfos.addAll(classifyListInfos.get(0).son);
        adapter2.notifyDataSetChanged();
    }


    private void InitListView() {
        listAdapter = new ClassifyListAdapter(getActivity(), classifyListInfos);
        classify_list.setAdapter(listAdapter);
        classify_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listAdapter.setChildAdapter(new ClassifyListAdapter.ChildAdapterChange() {
            @Override
            public void ChildAdapterChange(ArrayList<ClassifyChildInfo> classifyChildInfo) {
                classifyChildInfos.clear();
                classifyChildInfos.addAll(classifyChildInfo);
                adapter2.notifyDataSetChanged();
            }
        });
    }

    private void InitRecyclerView() {
        classify_rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        classify_rv.setLayoutManager(manager);
        MyGridDecoration myGridDecoration = new MyGridDecoration(BaseValue.dp2px(24), 0,
                getResources().getColor(R.color.white), false);
        classify_rv.addItemDecoration(myGridDecoration);
        adapter2 = new
                ClassifyRVAdapter(getActivity(), classifyChildInfos);
        classify_rv.setAdapter(adapter2);
    }
}
