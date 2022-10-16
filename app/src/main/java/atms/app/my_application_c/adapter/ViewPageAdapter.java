package atms.app.my_application_c.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewPageAdapter extends FragmentStateAdapter {
    private final List<Fragment> mdata;

    public ViewPageAdapter(@NonNull @NotNull FragmentActivity fragmentActivity, List<Fragment> data) {
        super(fragmentActivity);
        mdata = data;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return mdata.get(position);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }
}
