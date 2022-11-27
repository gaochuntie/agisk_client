package atms.app.my_application_c.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.topjohnwu.superuser.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import atms.app.my_application_c.ConfigBox.OrigConfig;
import atms.app.my_application_c.ConfigBox.XMLmod;
import atms.app.my_application_c.MainActivity;
import atms.app.my_application_c.R;
import atms.app.my_application_c.Tools.Worker;
import atms.app.my_application_c.aidl.workClient;

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
        TextView system_location = view.findViewById(R.id.system_location);
        TextView super_location = view.findViewById(R.id.super_location);

        //advanced
        TextView virtualsd_location = view.findViewById(R.id.virtual_sd_location);
        TextView virtualsd_size = view.findViewById(R.id.virtual_sd_size);
        TextView metadata_location = view.findViewById(R.id.metadata_location);
        TextView metadata_area = view.findViewById(R.id.metadata_area);

        Button read_partition_table = view.findViewById(R.id.read_partition_table);


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

        Shell.cmd("ls -l /dev/block/by-name/system").to(result).exec();
        String system_local = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ")+3);
        system_location.setText(system_local);

        Shell.cmd("ls -l /dev/block/by-name/super").to(result).exec();
        String super_s = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ")+3);
        super_location.setText(super_s);


        //check virtual sd
        Shell.cmd("[ -e /dev/block/by-name/virtual_sd ] && echo true || echo false").to(result).exec();
        String if_virtualsd_s = result.get(result.size() - 1).trim();
        if (if_virtualsd_s.equals("true")) {
            Shell.cmd("ls -l /dev/block/by-name/virtual_sd").to(result).exec();
            String sd_location = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ")+3);
            virtualsd_location.setText(sd_location);

            //get virtual sd size

            Shell.cmd("blockdev /dev/block/by-name/virtual_sd --getsize64").to(result).exec();

            int vsd_size = (int) (Long.valueOf(result.get(result.size() - 1)) / (1024 * 1024 * 1024));
            virtualsd_size.setText(String.valueOf(vsd_size) + "Gib");
        }else {
            virtualsd_location.setText("No virtual sdcard found");
            virtualsd_size.setText("0");

        }


        //set read partition table button listener

        read_partition_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InputDialog("Driver", "Which driver you are going to read", "Read", "Cancel", "")
                        .setCancelable(false)
                        .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                            @Override
                            public boolean onClick(InputDialog baseDialog, View v, String inputStr) {

                                StringBuilder sb = new StringBuilder();
                                InputStream is = null;
                                try {
                                    is = getActivity().getAssets().open("innerConfigFile/PartDumpMod.xml");
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8 ));
                                    String str;
                                    while ((str = br.readLine()) != null) {
                                        if (str.contains(XMLmod.PARTDUMP_DRIVER_REPLACE_KEY)) {
                                            str=str.replace(XMLmod.PARTDUMP_DRIVER_REPLACE_KEY, inputStr);
                                        }
                                        sb.append(str);
                                    }
                                    br.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                                Log.d("SYSTEM_MAP", sb.toString());
                                workClient client = null;

                                //Do action
                                OrigConfig origConfig = new OrigConfig(sb.toString(), "UTF-8");

                                client = Worker.putTaskToRootService(origConfig
                                        , getActivity());
                                ((MainActivity) getActivity()).getWorning_box().setBackgroundColor(Color.RED);
                                ((MainActivity) getActivity()).showWorningMsg("Processing in background.Touch to see.");
                                if (client == null) {
                                    Toast.makeText(getContext(), "Offer client failed.Up to max.", Toast.LENGTH_LONG).show();
                                }

                                //Mainly, worningbox listener only open the log viewer windows

                                ((MainActivity) getActivity()).getWorning_box().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((MainActivity) getActivity()).showLogViewer(3);

                                    }
                                });
                                return false;
                            }
                        })
                        .show();
            }
        });

    }
}