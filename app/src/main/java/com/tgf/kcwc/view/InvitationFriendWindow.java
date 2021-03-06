package com.tgf.kcwc.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tgf.kcwc.R;
import com.tgf.kcwc.adapter.WrapAdapter;
import com.tgf.kcwc.entity.DataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:Jenny
 * Date:16/9/11 14:16
 * E-mail:fishloveqin@gmail.com
 * 邀请、添加好友
 */
public class InvitationFriendWindow extends BottomPushPopupWindow<Void> {

    protected TextView title;
    protected GridView grid;
    protected TextView cancelBtn;



    public InvitationFriendWindow(Context context) {
        super(context, null);
    }

    @Override
    protected View generateCustomView(Void data) {
        View root = View.inflate(context, R.layout.invitation_firend_bottom_window, null);
        title = (TextView) root.findViewById(R.id.title);
        grid = (GridView) root.findViewById(R.id.grid);

        cancelBtn = (TextView) root.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        List<DataItem> items = new ArrayList<DataItem>();

        String[] arrays = context.getResources().getStringArray(R.array.add_friend_type);
      int[]      icons = { R.drawable.btn_contacts, R.drawable.btn_weixin,
                R.drawable.btn_qq_icon, R.drawable.btn_weibo };
        int index = 0;
        for (String s : arrays) {
            DataItem dataItem = new DataItem();
            dataItem.title = s;
            dataItem.icon = icons[index];
            items.add(dataItem);
            index++;
        }
        grid.setAdapter(new WrapAdapter<DataItem>(context, R.layout.invitation_item2, items) {
            @Override
            public void convert(ViewHolder helper, DataItem item) {

                SimpleDraweeView img = helper.getView(R.id.img);
                TextView nameTv = helper.getView(R.id.name);
                nameTv.setText(item.title);
                img.setImageResource(item.icon);
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mListener != null) {

                    mListener.onItemClick(parent, view, position, id);
                }
            }
        });

        return root;

    }

    private AdapterView.OnItemClickListener mListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {

        this.mListener = listener;
    }

}