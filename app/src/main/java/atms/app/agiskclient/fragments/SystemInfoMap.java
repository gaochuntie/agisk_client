package atms.app.agiskclient.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.topjohnwu.superuser.Shell;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import atms.app.agiskclient.ConfigBox.OrigConfig;
import atms.app.agiskclient.ConfigBox.XMLmod;
import atms.app.agiskclient.GPTfdisk.DiskChunk;
import atms.app.agiskclient.GPTfdisk.GPTDriver;
import atms.app.agiskclient.GPTfdisk.GPTPart;
import atms.app.agiskclient.GPTfdisk.PartType;
import atms.app.agiskclient.MainActivity;
import atms.app.agiskclient.R;
import atms.app.agiskclient.Settings;
import atms.app.agiskclient.Tools.GlobalMsg;
import atms.app.agiskclient.Tools.TAG;
import atms.app.agiskclient.Tools.Worker;
import atms.app.agiskclient.aidl.workClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SystemInfoMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInfoMap extends Fragment implements OnChartValueSelectedListener, View.OnClickListener {

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
        View view = inflater.inflate(R.layout.fragment_system_info_map, container, false);
        setInfo(view);
        setupDiskSpinner(view);
        setupPieChart(view);
        setupPartActionButtons(view);
        return view;
    }


    /**
     * set up part actions
     */
    Button partMount_bt;
    Button partUmount_bt;
    Button partNew_bt;
    Button partDelete_bt;
    Button partSettings_bt;

    private void setupPartActionButtons(View view) {
        partMount_bt = (Button) view.findViewById(R.id.part_mount_bt);
        partUmount_bt = (Button) view.findViewById(R.id.part_umount_bt);
        partNew_bt = (Button) view.findViewById(R.id.part_new_bt);
        partDelete_bt = (Button) view.findViewById(R.id.part_delete_bt);
        partSettings_bt = (Button) view.findViewById(R.id.part_settings_bt);

        partMount_bt.setOnClickListener(this);
        partUmount_bt.setOnClickListener(this);
        partNew_bt.setOnClickListener(this);
        partDelete_bt.setOnClickListener(this);
        partSettings_bt.setOnClickListener(this);


    }

    /**
     * used in part functions
     */
    GPTDriver selectedDriver = null;
    DiskChunk selectedChunk = null;

    /**
     * check chunk type in button listener
     *
     * @return just judge return by the mount state of part
     * not the mount process
     */
    private boolean partMount() {
        if (selectedChunk == null) {
            return false;
        }
        GPTPart part = (GPTPart) selectedChunk;
        if (part.isMounted()) {
            //already mounted
            return true;
        }
        Shell.cmd("[ -e " + part.getMountPointString() + " ] " +
                "&& echo dirExisted || mkdir -p " + part.getMountPointString());
        String readNum = String.valueOf(part.getNumber() + 1);
        Shell.cmd("mount " + part.getDriver() + readNum + " " + part.getMountPointString());
        if (part.isMounted()) {
            //already mounted
            return true;
        }
        return false;
    }

    /**
     * @return just judge return by the mount state of part
     * * not the umount process
     */
    private boolean partUmount() {
        if (selectedChunk == null) {
            return false;
        }
        GPTPart part = (GPTPart) selectedChunk;
        if (part.isMounted()) {
            //already mounted
            Shell.cmd("umount " + part.getMountPointString());
            if (part.isMounted()) {
                return false;
            }
            return true;
        }
        return true;
    }


    /**
     * async task
     */
    private void partNew() {
        //TODO
        //need DirectFunction
        //check in aide file

        return;
    }


    /**
     * async task
     */
    private void partDelete() {
        //Need DirectFunction

        WaitDialog.show("Deleting");
        //disable all related ui
        disablePartUIs();
        new Thread(new Runnable() {
            @Override
            public void run() {
                workClient client = new workClient(getContext(), selectedDriver.getPath());
                client.setDirect2_PART_DELETE(((GPTPart) selectedChunk).getNumber());
                boolean result = (Boolean) client.submitWork();
                //resume ui s
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enablePartUIs();
                        if (result) {
                            TipDialog.show("Success", WaitDialog.TYPE.SUCCESS);
                            //reload partition table
                            setData(selectedDriver.getPath());
                        }
                        TipDialog.show("Failed", WaitDialog.TYPE.ERROR);
                    }
                });
            }
        }).start();
        return;
    }

    private void partSettings(View view) {
        //Give dialog
    }


    ///////////////////////////////////

    /**
     * setup system info
     *
     * @param view
     */
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
        String result_s = result.get(result.size() - 1);
        storage_chip.setText(result_s);
        if (result_s.equals("UFS")) {
            Settings.setUFS();
        }

        Shell.cmd("blockdev /dev/block/by-name/userdata --getsize64").to(result).exec();

        try {
            int size_gib = (int) (Long.valueOf(result.get(result.size() - 1)) / (1024 * 1024 * 1024));
            userdata_size.setText(String.valueOf(size_gib) + "Gib");
        } catch (Exception e) {
            userdata_size.setText("Permission denied");
        }


        Shell.cmd("ls -l /dev/block/by-name/userdata").to(result).exec();
        String data_location = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ") + 3);
        userdata_location.setText(data_location);


        Shell.cmd("[ -e /dev/block/by-name/super ] && echo DSU || echo Ramdisk").to(result).exec();
        partition_layout.setText(result.get(result.size() - 1));

        Shell.cmd("ls -l /dev/block/by-name/system").to(result).exec();
        String system_local = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ") + 3);
        system_location.setText(system_local);

        Shell.cmd("ls -l /dev/block/by-name/super").to(result).exec();
        String super_s = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ") + 3);
        super_location.setText(super_s);


        //check virtual sd
        Shell.cmd("[ -e /dev/block/by-name/virtual_sd ] && echo true || echo false").to(result).exec();
        String if_virtualsd_s = result.get(result.size() - 1).trim();
        if (if_virtualsd_s.equals("true")) {
            Shell.cmd("ls -l /dev/block/by-name/virtual_sd").to(result).exec();
            String sd_location = result.get(result.size() - 1).substring(result.get(result.size() - 1).lastIndexOf("-> ") + 3);
            virtualsd_location.setText(sd_location);

            //get virtual sd size

            Shell.cmd("blockdev /dev/block/by-name/virtual_sd --getsize64").to(result).exec();

            try {
                int vsd_size = (int) (Long.valueOf(result.get(result.size() - 1)) / (1024 * 1024 * 1024));
                virtualsd_size.setText(String.valueOf(vsd_size) + "Gib");
            } catch (Exception e) {
                virtualsd_size.setText("Permission denied");
            }

        } else {
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
                                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                                    String str;
                                    while ((str = br.readLine()) != null) {
                                        if (str.contains(XMLmod.PARTDUMP_DRIVER_REPLACE_KEY)) {
                                            str = str.replace(XMLmod.PARTDUMP_DRIVER_REPLACE_KEY, inputStr);
                                        }
                                        sb.append(str);
                                    }
                                    br.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                Log.d("SYSTEM_MAP", sb.toString());
                                workClient client = null;

                                //Do action
                                OrigConfig origConfig = new OrigConfig(sb.toString(), "UTF-8");

                                client = Worker.putTaskToRootService(origConfig
                                        , getActivity());

                                if (client == null) {
                                    MessageDialog.show("Error", "Permission denied.This perfermance requires root permission", "Cancel");
                                    return false;
                                }
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

    /**
     * Setup gpt table manager
     *
     * @param view
     */

    private PieChart chart;
    private Typeface tf;

    private void setupPieChart(View view) {

        chartNoticer = view.findViewById(R.id.chartNotiverTv);

        chart = view.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(view.getContext().getAssets(), "OpenSans-Regular.ttf");

        chart.setCenterTextTypeface(Typeface.createFromAsset(view.getContext().getAssets(), "OpenSans-Light.ttf"));
        chart.setCenterText(generateCenterSpannableText("Unknown"));

        chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    protected final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected final String[] parties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };


    /**
     * Set gpt part list data to chart
     *
     * @param count
     * @param range
     * @param driver
     */

    TextView chartNoticer;


    /**
     * update parts to chart
     * all parts smaller than 10% of the disk size will be mixed into "other" extry
     *
     * @param driver
     */
    private void setData(String driver) {

        /**
         * Need async get gpt list data
         */
        //Block spinner and chart
        diskSpinner.setEnabled(false);
        chart.setEnabled(false);
        chartNoticer.setText("Loading");
        chartNoticer.setVisibility(View.VISIBLE);
        //get data in thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                GPTDriver driver_data = getPartList(driver);
                //save for global usage
                selectedDriver = driver_data;

                //upddate ui
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        //set chart
                        ////////////////////////////////////////////////////////////
                        ArrayList<PieEntry> entries = new ArrayList<>();

                        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                        // the chart.
                        int partCount = driver_data.getPartList().size();
                        List<DiskChunk> partList = driver_data.getPartList();
                        long disk_total = driver_data.getTotal_size_sector();
                        long other_size = 0;
                        long threshold = (long) (0.1 * disk_total);


                        /**
                         * Add part
                         * Note : size <10 and is a part will be mixed into other
                         * if size < 10 and is free space , will get a blank label
                         * if size > 10 and is free space , will get a "Unused" label
                         */
                        for (int i = 0; i < partCount; i++) {

                            if (partList.get(i).getPart_type().getPartType() == PartType.Part_Type.TYPE_FREESPACE) {
                                //free chunk
                                DiskChunk freeChunk = partList.get(i);
                                long size = partList.get(i).getSize_sector();
                                String label;
                                if (size < threshold) {
                                    //avoid label overlapped
                                    label = "";
                                } else {
                                    label = "Unused";
                                }
                                PieEntry entry = new PieEntry(size, label);
                                entry.setData(freeChunk);
                                entries.add(entry);

                                Log.d(TAG.SystemInforMap_TAG, "Add Chart Entry : "
                                        + "Unused "
                                        + ":" + String.valueOf(freeChunk.getStartSector()));
                            } else {
                                //part
                                GPTPart part = (GPTPart) partList.get(i);
                                long size = partList.get(i).getSize_sector();
                                if (size < threshold) {
                                    other_size += size;
                                    continue;
                                }
                                String label = part.getNumber() + ":" + part.getName();
                                PieEntry entry = new PieEntry(size, label);
                                entry.setData(part);
                                entries.add(entry);

                                Log.d(TAG.SystemInforMap_TAG, "Add Chart Entry : "
                                        + part.getNumber()
                                        + ":" + part.getName());
                            }
                        }
                        if (other_size > 0) {
                            String label_other = "Other";
                            entries.add(new PieEntry(other_size, label_other));
                            Log.d(TAG.SystemInforMap_TAG, "Add Other Entry : "
                                    + other_size);
                        }

                        PieDataSet dataSet = new PieDataSet(entries, "Disk Layout");
                        dataSet.setSliceSpace(3f);
                        dataSet.setSelectionShift(5f);

                        // add a lot of colors

                        ArrayList<Integer> colors = new ArrayList<>();

//                        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//                            colors.add(c);

//                        for (int c : ColorTemplate.JOYFUL_COLORS)
//                            colors.add(c);

                        for (int c : ColorTemplate.COLORFUL_COLORS)
                            colors.add(c);
//
//                        for (int c : ColorTemplate.LIBERTY_COLORS)
//                            colors.add(c);
//
//                        for (int c : ColorTemplate.PASTEL_COLORS)
//                            colors.add(c);

                        colors.add(ColorTemplate.getHoloBlue());

                        dataSet.setColors(colors);
                        //dataSet.setSelectionShift(0f);


                        dataSet.setValueLinePart1OffsetPercentage(80.f);
                        dataSet.setValueLinePart1Length(0.2f);
                        dataSet.setValueLinePart2Length(0.4f);
                        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                        PieData data = new PieData(dataSet);
                        data.setValueFormatter(new PercentFormatter());
                        data.setValueTextSize(11f);
                        data.setValueTextColor(Color.BLACK);
                        data.setValueTypeface(tf);
                        chart.setData(data);

                        // undo all highlights
                        chart.highlightValues(null);
                        chart.setCenterText(generateCenterSpannableText(driver));


                        chart.getLegend().setTextColor(Color.BLACK);
                        chart.invalidate();

                        setUpPartListRecyclerView(driver_data);
                        //unblock spinner and chart
                        diskSpinner.setEnabled(true);
                        chart.setEnabled(true);
                        chartNoticer.setVisibility(View.INVISIBLE);

                        selectOtherEntry();
                    }
                });
            }
        }).start();

    }


    /**
     * set up recyclerview for part list
     * @param driver
     */
    private void setUpPartListRecyclerView(GPTDriver driver) {

    }

    private GPTDriver getPartList(String driver) {
        workClient client = new workClient(getActivity(), driver);
        client.setDirect1_PART_DUMPER();
        String result = (String) client.submitWork();
        Log.d(TAG.SystemInforMap_TAG, "GPart list string : " + result);
        /**
         * result format
         * private List<GPTPart> partList;
         *     private long sector_size;
         *     private long block_size;
         *     private String path;
         *     private int partLimit;
         *     private String GUID;
         *     private long total_size_sector=0;
         *     private long free_size_sector=0;
         *
         *      private int number;
         *     private long startSector;
         *     private long endSector;
         *     private long size_sector;
         *     private String code;
         *     private String name="";
         *     private PartType part_type;
         *     private String driver;
         *
         * {block_size:sector_size:GUID:partLimit:total:free}
         * {number:name:start:end:code:typeGUID}{number:name:start:end:code:typeGUID}
         *
         * {0:ALIGN_TO_128K_1:6:31:65535:Unknown}
         */

        //parse result
        String[] r1 = result.substring(1, result.length() - 1).split("\\}\\{");
        String[] driver_r1 = r1[0].split(":");

        GPTDriver m_driver = new GPTDriver(
                driver
                , Long.valueOf(driver_r1[1])
                , Long.valueOf(driver_r1[0])
                , Integer.valueOf(driver_r1[3])
                , driver_r1[2]);

        m_driver.setFree_size_sector(Long.parseLong(driver_r1[5]));
        m_driver.setTotal_size_sector(Long.valueOf(driver_r1[4]));


        for (int i = 1; i < r1.length; i++) {
            String[] partData = r1[i].split(":");

            if (partData[5].equals("*FREESPACE")) {
                DiskChunk freeChunk = new DiskChunk(
                        driver
                        , Long.valueOf(partData[2])
                        , Long.valueOf(partData[3]));
                freeChunk.setPart_type(new PartType("*FREESPACE"));
                m_driver.addPart(freeChunk);
            } else {

                //{0:ALIGN_TO_128K_1:6:31:65535:Unknown}
                GPTPart part = new GPTPart(
                        driver
                        , Integer.valueOf(partData[0])
                        , Long.valueOf(partData[2])
                        , Long.valueOf(partData[3]));
                part.setName(partData[1]);
                part.setCode(partData[4]);
                part.setPart_type(new PartType(partData[4]));

                m_driver.addPart(part);
            }
        }


        return m_driver;
    }

    private SpannableString generateCenterSpannableText(String driver) {

        SpannableString s;
        if (Settings.isUFS()) {
            s = new SpannableString("UFS\n" + driver);
            s.setSpan(new RelativeSizeSpan(1.5f), 0, 3, 0);
            s.setSpan(new StyleSpan(Typeface.ITALIC), 3, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 3, s.length(), 0);
        } else {
            s = new SpannableString("EMMC\n" + driver);
        }
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 4, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 4, s.length(), 0);

        return s;
    }



    private void selectOtherEntry() {
        int last_index = chart.getData().getDataSet().getEntryCount()-1;
        chart.highlightValue(last_index,0);
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        DiskChunk chunk = (DiskChunk) e.getData();
        if (chunk == null) {
            //Other entry
            Log.i("VAL SELECTED",
                    "Value: " + e.getY() + ", xIndex: " + e.getX()
                            + ", Other entry  ");
            disableExistedPartActions();
            disableFreeChunkActions();
        } else {
            Log.i("VAL SELECTED",
                    "Value: " + e.getY() + ", xIndex: " + e.getX()
                            + ", |  " + chunk.getPart_type().getPartType().toString());
            if (chunk.getPart_type().getPartType() == PartType.Part_Type.TYPE_FREESPACE) {
                disableExistedPartActions();
                enableFreeChunkActions();
            }else{
                disableFreeChunkActions();
                enableExistedPartActions();
            }
        }
        selectedChunk = chunk;
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
        selectedChunk = null;
        disableExistedPartActions();
        disableFreeChunkActions();
    }


    /**
     * setup disk driver spinner
     */
    private NiceSpinner diskSpinner;

    private void setupDiskSpinner(final View view) {
        diskSpinner = (NiceSpinner) view.findViewById(R.id.disk_driver_spinner);
        List<String> dlist = getBlockDriverList();

        /**
         * M* Fuck
         * My logd died
         * M* fuck
         */
        //Toast.makeText(view.getContext(),dlist.get(0),Toast.LENGTH_SHORT).show();
        if (dlist.size() < 2) {
            dlist.clear();
            dlist.add("E:Unable to load driver list");
            dlist.add("E:Size 0 list");

        }

        diskSpinner.attachDataSource(dlist);
        diskSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                //set list
                setData(dlist.get(position));
            }
        });

    }


    /**
     * Get block driver list
     */

    private List<String> getBlockDriverList() {

        if (!Settings.isUFS()) {

            /**
             * Important
             * at least 2 element for linkedlist
             * Or the spinner won't work as expected
             */
            List<String> dlist;
            dlist = new LinkedList<>();
            dlist.add("/dev/block/mmcblk0");
            dlist.add("/dev/block/mmcblk0");
            return dlist;
        } else {
            List<String> results = new LinkedList<>();
            Shell.cmd("ls /dev/block/sd?").to(results).exec();
            return results;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.part_mount_bt:
        }
    }

    private void disablePartUIs() {
        diskSpinner.setEnabled(false);
        chart.setEnabled(false);
        partMount_bt.setEnabled(false);
        partUmount_bt.setEnabled(false);
        partNew_bt.setEnabled(false);
        partDelete_bt.setEnabled(false);
        partSettings_bt.setEnabled(false);

    }

    private void enablePartUIs() {
        diskSpinner.setEnabled(true);
        chart.setEnabled(true);
        partMount_bt.setEnabled(true);
        partUmount_bt.setEnabled(true);
        partNew_bt.setEnabled(true);
        partDelete_bt.setEnabled(true);
        partSettings_bt.setEnabled(true);
    }

    private void disableExistedPartActions() {
        partMount_bt.setEnabled(false);
        partUmount_bt.setEnabled(false);
        partDelete_bt.setEnabled(false);
        partSettings_bt.setEnabled(false);
    }

    private void enableExistedPartActions() {
        partMount_bt.setEnabled(true);
        partUmount_bt.setEnabled(true);
        partDelete_bt.setEnabled(true);
        partSettings_bt.setEnabled(true);
    }

    private void disableFreeChunkActions() {
        partNew_bt.setEnabled(false);
    }

    private void enableFreeChunkActions() {
        partNew_bt.setEnabled(true);
    }
}

