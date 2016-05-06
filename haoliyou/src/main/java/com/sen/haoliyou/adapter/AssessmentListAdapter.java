package com.sen.haoliyou.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.haoliyou.R;
import com.sen.haoliyou.mode.AssessmentItemBean;
import com.sen.haoliyou.tools.ResourcesUtils;

import java.util.List;

/**
 * Created by Sen on 2016/2/22.
 */
public class AssessmentListAdapter extends RecyclerView.Adapter<AssessmentListAdapter.ViewHolder> {
    private List<AssessmentItemBean> mData;
    private Context mContext;

    public AssessmentListAdapter(Context context, List<AssessmentItemBean> data) {
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
        void onItemClick(View view, int position, AssessmentItemBean childItemBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assessment_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        AssessmentItemBean itemBean = mData.get(position);

        holder.tv_asseess_name.setText(itemBean.getDemand_name());
        String time ="时间："+ itemBean.getBegin_date()+"至"+itemBean.getEnd_date();
        holder.tv_assess_time.setText(time);

        int examType =Integer.parseInt(itemBean.getTime_flag());
        String examTypeStr = "";
        if (examType==0){
            examTypeStr ="未开始";
            holder.tv_assess_type.setBackgroundDrawable(ResourcesUtils.getResDrawable(mContext,R.drawable.bg_exam_unenter));
        }else if (examType==1){
            examTypeStr ="进行中";
            holder.tv_assess_type.setBackgroundDrawable(ResourcesUtils.getResDrawable(mContext,R.drawable.bg_exam_type));
        }else if (examType ==2){
            holder.tv_assess_type.setBackgroundDrawable(ResourcesUtils.getResDrawable(mContext,R.drawable.bg_exam_unenter));
            examTypeStr ="已结束";
        }
        holder.tv_assess_type.setText(examTypeStr);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onItemClick(holder.itemView, position,mData.get(position));
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
        public AppCompatTextView tv_asseess_name,tv_assess_time,tv_assess_type;

        public ViewHolder(View view) {
            super(view);
            tv_asseess_name = (AppCompatTextView) view.findViewById(R.id.tv_asseess_name);
            tv_assess_time = (AppCompatTextView) view.findViewById(R.id.tv_assess_time);
            tv_assess_type = (AppCompatTextView) view.findViewById(R.id.tv_assess_type);


        }


    }
}
