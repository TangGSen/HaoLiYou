package com.sen.haoliyou.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.haoliyou.R;
import com.sen.haoliyou.mode.ActEmployeeHome;
import com.sen.haoliyou.tools.ResourcesUtils;

import java.util.List;

/**
 * Created by Sen on 2016/2/22.
 */
public class ActEmployeeAdapter extends RecyclerView.Adapter<ActEmployeeAdapter.ViewHolder> {
    private List<ActEmployeeHome.EmployeeItemBean> mData;
    private Context mContext;

    public ActEmployeeAdapter(Context context, List<ActEmployeeHome.EmployeeItemBean> data) {
        mContext = context;
        mData = data;
    }

//    public void addLessonBeanData(List<LessonItemBean> data){
//        mData.addAll(data);
//        notifyDataSetChanged();
//    }

    private OnItemClickListener onItemClickListener = null;


    //Item click thing
    public interface OnItemClickListener {
        void onItemClick(View view, int position, ActEmployeeHome.EmployeeItemBean childItemBean,int enterType);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_examlist_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ActEmployeeHome.EmployeeItemBean itemBean = mData.get(position);

        holder.tv_employee_name.setText(itemBean.getUser_name());

        final int examType =Integer.parseInt(itemBean.getCheck_flag());
        String examTypeStr = "";
        if (examType==0){
            examTypeStr ="评估";
            holder.tv_state_type.setBackgroundDrawable(ResourcesUtils.getResDrawable(mContext,R.drawable.bg_exam_type));
        }else {
            holder.tv_state_type.setBackgroundDrawable(ResourcesUtils.getResDrawable(mContext,R.drawable.bg_exam_unenter));
            examTypeStr ="查看";
        }
        holder.tv_state_type.setText(examTypeStr);


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        onItemClickListener.onItemClick(holder.itemView, position, mData.get(position),examType);
                }

            });
        }




    }







    @Override
    public int getItemCount() {
        return mData.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView tv_employee_name,tv_organization,tv_group_name,tv_station_name,tv_state_type;
        public View test_view_bar;

        public ViewHolder(View view) {
            super(view);
            tv_employee_name = (AppCompatTextView) view.findViewById(R.id.tv_employee_name);
            tv_organization = (AppCompatTextView) view.findViewById(R.id.tv_organization);
            tv_group_name = (AppCompatTextView) view.findViewById(R.id.tv_group_name);
            tv_station_name = (AppCompatTextView) view.findViewById(R.id.tv_station_name);
            tv_state_type = (AppCompatTextView) view.findViewById(R.id.tv_state_type);
            test_view_bar = (View) view.findViewById(R.id.test_view_bar);


        }


    }
}
