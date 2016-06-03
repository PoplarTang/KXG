package com.cqts.kxg.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqts.kxg.R;
import com.cqts.kxg.bean.GoodsInfo;
import com.cqts.kxg.main.WebActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.MyViewHolder> {
    Context context;
    List<GoodsInfo> goods_list;
    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).showImageOnLoading(R.color.white)
            .build();
    public GoodsAdapter(Context context, List<GoodsInfo> goods_list) {
        this.context = context;
        this.goods_list = goods_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        ImageLoader.getInstance().displayImage(goods_list.get(i).goods_thumb, myViewHolder
                .item_nine_img,defaultOptions);
        myViewHolder.item_info_tv.setText(goods_list.get(i).goods_name);
        myViewHolder.item_money_tv.setText("¥" + goods_list.get(i).shop_price);
        myViewHolder.item_love_tv.setText(goods_list.get(i).volume + "");
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("title", goods_list.get(i).goods_name);
                intent.putExtra("url", goods_list.get(i).url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goods_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_nine_img;
        private TextView item_info_tv;
        private TextView item_money_tv;
        private TextView item_love_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_nine_img = (ImageView) itemView.findViewById(R.id.item_nine_img);
            item_info_tv = (TextView) itemView.findViewById(R.id.item_info_tv);
            item_money_tv = (TextView) itemView.findViewById(R.id.item_money_tv);
            item_love_tv = (TextView) itemView.findViewById(R.id.item_love_tv);
        }
    }
}
