package atms.app.agiskclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import atms.app.agiskclient.R;

public class noticeListAdapter extends RecyclerView.Adapter{
    List<NoticeItem> notice_list;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);

        return new MyHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHoler myHoler = (MyHoler) holder;
        myHoler.getContentTv().setText(notice_list.get(position).getMain_content());
        myHoler.getTitle().setText(notice_list.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return notice_list.size();
    }

    public noticeListAdapter(List<NoticeItem> mdata) {
        notice_list = mdata;
    }
    class MyHoler extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final FrameLayout extra_container;

        public MyHoler(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notice_title);
            content = itemView.findViewById(R.id.notice_content);
            extra_container = itemView.findViewById(R.id.notice_extra_container);
        }

        public FrameLayout getExtra_container() {
            return extra_container;
        }

        public TextView getContentTv() {
            return content;
        }

        public TextView getTitle() {
            return title;
        }
    }
    public static class NoticeItem{
        private String title;
        private String main_content;
        //TODO expand notice


        public NoticeItem(String title, String main_content) {
            this.main_content = main_content;
            this.title = title;
        }

        public String getMain_content() {
            return main_content;
        }

        public String getTitle() {
            return title;
        }

    }

}
