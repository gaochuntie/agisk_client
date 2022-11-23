package atms.app.my_application_c.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

import atms.app.my_application_c.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SystemInfoMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInfoMap extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SystemInfoMap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SystemInfoMap.
     */
    // TODO: Rename and change types and number of parameters
    public static SystemInfoMap newInstance(String param1, String param2) {
        SystemInfoMap fragment = new SystemInfoMap();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_system_info_map, container, false);
        setInfo(view);
        return view;
    }

    void setInfo(View view) {
        List<String> result = new ArrayList<>();
        TextView storage_chip = view.findViewById(R.id.storage_chip);
        TextView userdata_size = view.findViewById(R.id.data_size);
        TextView userdata_location = view.findViewById(R.id.data_location);
        TextView partition_layout = view.findViewById(R.id.partition_layout);


        Shell.cmd("[ -e /dev/block/mmcblk0 ] && echo EMMC || echo UFS").to(result).exec();
        storage_chip.setText(result.get(result.size()-1));

        Shell.cmd("blockdev /dev/block/by-name/userdata --getsize64").to(result).exec();

        int size_gib = (int) (Long.valueOf(result.get(result.size() - 1)) / (1024 * 1024 * 1024));
        userdata_size.setText(String.valueOf(size_gib) + "Gib");

        Shell.cmd("ls -l /dev/block/by-name/userdata").to(result).exec();
        String data_location = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ")+3);
        userdata_location.setText(data_location);


        Shell.cmd("[ -e /dev/block/by-name/super ] && echo DSU || echo Ramdisk").to(result).exec();
        partition_layout.setText(result.get(result.size()-1));
    }
}