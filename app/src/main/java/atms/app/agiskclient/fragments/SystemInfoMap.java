package atms.app.agiskclient.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.SimpleTableViewListener;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kongzue.dialogx.dialogs.FullScreenDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopMenu;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.BaseDialog;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.topjohnwu.superuser.Shell;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atms.app.agiskclient.ConfigBox.OrigConfig;
import atms.app.agiskclient.ConfigBox.XMLmod;
import atms.app.agiskclient.GPTfdisk.DiskChunk;
import atms.app.agiskclient.GPTfdisk.DiskUsageView;
import atms.app.agiskclient.GPTfdisk.GPTDriver;
import atms.app.agiskclient.GPTfdisk.GPTPart;
import atms.app.agiskclient.GPTfdisk.PartType;
import atms.app.agiskclient.MainActivity;
import atms.app.agiskclient.R;
import atms.app.agiskclient.Settings;
import atms.app.agiskclient.TableViewUI.DataModels.Cell;
import atms.app.agiskclient.TableViewUI.DataModels.ColumnHeader;
import atms.app.agiskclient.TableViewUI.DataModels.RowHeader;
import atms.app.agiskclient.Tools.ClipboardUtil;
import atms.app.agiskclient.Tools.CompressUtils;
import atms.app.agiskclient.Tools.DateUtils;
import atms.app.agiskclient.Tools.DirectFunctionUtils;
import atms.app.agiskclient.Tools.FileForceWriteListener;
import atms.app.agiskclient.Tools.FileUtils;
import atms.app.agiskclient.Tools.TAG;
import atms.app.agiskclient.Tools.Worker;
import atms.app.agiskclient.adapter.MyTableViewAdapter;
import atms.app.agiskclient.aidl.workClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SystemInfoMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInfoMap extends Fragment implements OnChartValueSelectedListener, View.OnClickListener {

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


    /**
     * file picker
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::onFilePicked);
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
        if (Settings.getRootAccess() == false) {
            Toast.makeText(view.getContext(), "Root Required", Toast.LENGTH_LONG).show();
            return view;
        }
        setupBasicUi(view);
        setInfo(view);
        setupFirmwareBackupBt(view);
        setupDiskSpinner(view);
        setupPieChart(view);
        setupPartActionButtons(view);
        TipDialog.overrideCancelable = BaseDialog.BOOLEAN.TRUE;
        if (!selectedDisk.isEmpty()) {
            //setData(selectedDisk);
        }

        return view;
    }


    /**
     * set up generate firmware flashable
     */


    Button fwFlashable_gen_bt;

    private void setupFirmwareBackupBt(View view) {
        fwFlashable_gen_bt = (Button) view.findViewById(R.id.generate_firmware_updater_zip);
        fwFlashable_gen_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageDialog.show("Introduce", "Create backup of firmware partitions" +
                        ",flash the zip to restore if necessary" +
                        ". This can be a good way to prevent 基带(IMEI related) lost" +
                        " caused by 格机(Wipe the whole flash) ." +
                        "Notice I recommand not to select big partitions such as super" +
                        " because they have nothing to do with IMEI. Last Big Big Thanks to GJZS's script and my ATMS project(ported from here) ", "Learned").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog baseDialog, View v) {
                        loadFirmwareList();
                        return false;
                    }
                });
            }
        });
    }


    /**
     * async function,a dialog will shown after finishing init list
     */
    Map<String, Long> block_dev = new HashMap<>();

    private void loadFirmwareList() {
        WaitDialog.overrideCancelable= BaseDialog.BOOLEAN.FALSE;
        WaitDialog.show("Loading Block Device");

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream block_dev_stream = FileUtils.getAssetsInputStream(getContext(), "Han.GJZS/Block_Device_Name.sh");
                if (block_dev_stream == null) {
                    TipDialog.show("Unable to load firmware list", -1);
                    return;
                }
                Shell.Result result;
                result = Shell.cmd(block_dev_stream).exec();

                //close inputdtream
                try {
                    block_dev_stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> out = result.getOut();  // stdout
                int code = result.getCode();         // return code of the last command
                boolean ok = result.isSuccess();     // return code == 0?
                block_dev.clear();
                for (String item : out) {
                    if (!showMapperDevice && item.contains("/mapper/")){
                        continue;
                    }
                    Log.d(TAG.SystemInforMap_TAG, "Block Dev : " + item);
                    String[] parts = item.split("=");
                    if (parts.length == 2) {
                        String filename = parts[0].trim();
                        try {
                            long size = Long.parseLong(parts[1].trim());
                            block_dev.put(filename, size);
                        } catch (NumberFormatException e) {
                            // Handle the case when the size is not a valid long value
                            // You can show an error message or handle it as needed
                        }
                    }
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WaitDialog.dismiss();
                        showFirmwareFlashableGenDialog();
                    }
                });

            }
        }).start();

    }

    //Firmware
    private List<String> selectedItems = new ArrayList<>();
    private boolean showMapperDevice=false;
    AlertDialog alertDialog=null;
    boolean isBackupGptTable = false;
    private void showFirmwareFlashableGenDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Items");

        // Create a list to display filename (size) format
        List<String> fileListWithSize = new ArrayList<>();
        for (Map.Entry<String, Long> entry : block_dev.entrySet()) {
            String filename = entry.getKey();
            Long size = entry.getValue();
            fileListWithSize.add(filename + " (" + size + ")");
        }

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.firmware_list_dialog, null);
        builder.setView(dialogView);

        // Initialize the ListView
        ListView listViewItems = dialogView.findViewById(R.id.listViewItems);
        Switch selectAll = dialogView.findViewById(R.id.fw_dialog_list_items_switch);
        Switch showMapper = dialogView.findViewById(R.id.fw_dialog_list_items_mapper_switch);
        Switch partition_table = dialogView.findViewById(R.id.fw_dialog_list_items_partition_table_switch);
        Button export_bt = dialogView.findViewById(R.id.fw_dialog_list_items_export);

        
        showMapper.setChecked(showMapperDevice);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_multiple_choice, fileListWithSize);
        listViewItems.setAdapter(adapter);

        // Set the default checked items to false (unselected)
        listViewItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        int i=0;
        int j=0;
        for (; i < fileListWithSize.size(); i++) {
            String filename = block_dev.keySet().toArray(new String[0])[i];
            boolean b = selectedItems.contains(filename);
            listViewItems.setItemChecked(i, b);
            if (b) {
                j++;
            }
        }
        if (i == j) {
            selectAll.setChecked(true);
        }
        // Handle item selection in the ListView
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = block_dev.keySet().toArray(new String[0])[position];
                if (selectedItems.contains(filename)) {
                    selectedItems.remove(filename);
                } else {
                    selectedItems.add(filename);
                }
            }
        });
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectedItems.clear();
                    listViewItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for (int i = 0; i < fileListWithSize.size(); i++) {
                        String filename = block_dev.keySet().toArray(new String[0])[i];
                        listViewItems.setItemChecked(i, true);
                        selectedItems.add(filename);
                    }
                }else{
                    selectedItems.clear();
                    listViewItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for (int i = 0; i < fileListWithSize.size(); i++) {
                        String filename = block_dev.keySet().toArray(new String[0])[i];
                        listViewItems.setItemChecked(i, false);
                    }
                }
            }
        });
        export_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder builder1 = new StringBuilder();
                for (String item : selectedItems) {
                    builder1.append(item);
                }
                if (builder1.toString().isEmpty()) {
                    //empty
                    return;
                }
                alertDialog.dismiss();
                builder1.append("\n");
                String filename = "/sdcard/agisk_fw_list_" + DateUtils.getCurrentDateTimeString() + ".txt";
                WaitDialog.overrideCancelable= BaseDialog.BOOLEAN.FALSE;
                WaitDialog.show("Exporting");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        FileUtils.forceWriteToFileWithRoot(getContext(), builder1.toString(), filename, new FileForceWriteListener() {
                            @Override
                            public void onWriteFailed(String reson) {
                                WaitDialog.dismiss();
                                MessageDialog.show("Failed", "Unable to export list,this action need root at present");

                            }

                            @Override
                            public void onWriteSuccess() {
                                WaitDialog.dismiss();
                                MessageDialog.show("Export",
                                        "See "  + filename,
                                        "Copy Path").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                                    @Override
                                    public boolean onClick(MessageDialog baseDialog, View v) {
                                        ClipboardUtil.copyToClipboard(getContext(), filename);

                                        return false;
                                    }
                                }).setDialogLifecycleCallback(new DialogLifecycleCallback<MessageDialog>() {
                                    @Override
                                    public void onDismiss(MessageDialog dialog) {
                                        super.onDismiss(dialog);
                                        alertDialog.show();
                                    }
                                })

                                ;
                            }
                        });
                    }
                }).start();
                return;
            }
        });


        showMapper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedItems.clear();
                showMapperDevice = b;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    loadFirmwareList();
                }
            }
        });
        partition_table.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBackupGptTable = isChecked;
            }
        });

        // Handle the Load button click
        builder.setNegativeButton("Load", null);

        // Create the custom "Action" button
        builder.setPositiveButton("Action", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create a new map for selected items in the same format (filename -> size)
                Map<String, Long> selectedMap = new HashMap<>();
                for (String filename : selectedItems) {
                    if (block_dev.containsKey(filename)) {
                        selectedMap.put(filename, block_dev.get(filename));
                    }
                }

                // Call the function with the selected items map
                generateFirmwareFlashable(selectedMap,partition_table.isChecked());
            }
        });
        //select all
        // Show the dialog
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        readFilenamesFromFile();
                        alertDialog.dismiss();
                    }
                });
            }
        });

        // Show the dialog
        alertDialog.show();
    }

    /**
     * load firmware list
     */
    private void readFilenamesFromFile() {
        filePickerLauncher.launch("text/plain");
    }

    private void onFilePicked(Uri uri) {
        if (uri != null) {
            /**
             * update selectedItem and redisplay dialog
             */
            selectedItems.clear();
            try {
                // Open the file using the file path
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        selectedItems.add(line.trim());
                    }
                    reader.close();
                } else {
                    Log.d(TAG.SystemInforMap_TAG, "inputstream is null");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
                // Handle the IOException if needed
                // You can show an error message or handle it as needed
            }
        } else {
            Log.d(TAG.SystemInforMap_TAG, "Url is null");
            return;
        }
        showFirmwareFlashableGenDialog();

    }


    /**
     * async function
     *
     * @param fw_list
     */
    private void generateFirmwareFlashable(Map<String, Long> fw_list,boolean isBackupPt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                for (String item : fw_list.keySet()) {
                    Log.d(TAG.SystemInforMap_TAG, item);
                }
                WaitDialog.overrideCancelable= BaseDialog.BOOLEAN.FALSE;
                WaitDialog.show("Backuping");
                String root_dir = getContext().getExternalFilesDir("firmware").getAbsolutePath();
                String fw_root = getContext().getExternalFilesDir("firmware/fw").getAbsolutePath();
                String script_dir = fw_root + "/META-INF/com/google/android/";

                //update-binary
                if (!FileUtils.copyAssetFileToStorage(getContext(), "Firmware/update-binary"
                        , script_dir + "/update-binary")
                        && FileUtils.copyAssetFileToStorage(getContext(), "Firmware/update-script"
                        , script_dir + "/update-script")
                ) {
                    //unable to copy file
                    TipDialog.show("Unable to copy files", -1);
                    return;
                }
                //partition table
                if (isBackupPt) {
                    Log.d(TAG.SystemInforMap_TAG, "GPT BACKUP ENABLED");
                    if (!FileUtils.copyAssetFileToStorage(getContext(), "StaticBinary/sgdisk"
                            , fw_root + "/sgdisk")) {
                        TipDialog.show("Unable to copy sgdisk", WaitDialog.TYPE.ERROR, -1);
                        return;
                    }
                }
                //write script
                File backup = new File(fw_root + "/backupList.list");
                File restore = new File(fw_root + "/restoreList.list");
                //File meta = new File(fw_root + "/META-INF");
                File script = new File(script_dir + "/update-binary");
                BufferedWriter backup_writer = null;
                BufferedWriter restore_writer = null;
                BufferedWriter scriptwriter = null;
                List<String> cmd = new ArrayList<String>();
                try {
                    if (!backup.exists()) {
                        backup.createNewFile();
                    }
                    if (!restore.exists()) {
                        restore.createNewFile();
                    }
                    if (!script.exists()) {
                        //error
                        Log.d(TAG.SystemInforMap_TAG, "Script not found");
                        return;
                    }
                    scriptwriter = new BufferedWriter(new FileWriter(script, true));


                    //Log.d(log,"checked partitions 51315 : "+checkpartitions[0]);
                    backup_writer = new BufferedWriter(new FileWriter(backup,false));
                    restore_writer = new BufferedWriter(new FileWriter(restore,false));
                    int total=fw_list.keySet().size();
                    int current=0;
                    for (String dev_path : fw_list.keySet()) {
                        current++;
                        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //Log.d(TAG.SystemInforMap_TAG, "writer 5585: " + dev_path);

                        String filename_zip = dev_path.substring(dev_path.indexOf("/") + 1);
                        Log.d(TAG.SystemInforMap_TAG, filename_zip);
                        String filedir_zip = filename_zip.substring(0, filename_zip.lastIndexOf("/") + 1);
                        Log.d(TAG.SystemInforMap_TAG, filedir_zip);

                        //new function
                        scriptwriter.write("ui_print \"Processing "+current+"/"+total+" "+dev_path+"\"\n");
                        scriptwriter.write("unzip $3 " + filename_zip + " -p | cat >" +dev_path+ "\n");

                        scriptwriter.flush();

                        //backup process
                        /**
                         * add cmd
                         */
                        cmd.add("mkdir -p " + fw_root + "/" + filedir_zip);
                        cmd.add("dd if=" + dev_path + " of=" + fw_root + "/" + filename_zip);
                        backup_writer.write(dev_path + "\n");
                        backup_writer.flush();
                        restore_writer.write(dev_path + "\n");
                        restore_writer.flush();
                    }
                    if (isBackupPt) {
                        //add partition table backup
                        cmd.add("mkdir -p " + fw_root + "/GPT_BACKUP");
                        cmd.add("chmod 777 " + fw_root + "/sgdisk");

                        scriptwriter.write("ui_print \"Restoring GPT Table ... "+"\"\n");
                        scriptwriter.write("unzip $3 sgdisk -d /agisk_tmp/"+"\n");
                        scriptwriter.write("chmod 777 /agisk_tmp/sgdisk"+"\n");
                        scriptwriter.write("unzip $3 GPT_BACKUP -d /agisk_tmp/" + "\n");
                        List<String> result = new ArrayList<>();

                        if (Settings.isUFS()) {
                            //ufs
                            for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
                                Shell.cmd("[ -e /dev/block/sd"+alphabet+" ] && echo Y || echo N").to(result).exec();
                                String result_s = result.get(result.size() - 1);
                                if (result_s.equals("Y")) {
                                    cmd.add( fw_root + "/sgdisk /dev/block/sd" + alphabet + " --backup=" + fw_root + "/GPT_BACKUP/sd" + alphabet + ".bin");
                                    scriptwriter.write("ui_print \"Processing sd"+alphabet+" "+"\"\n");
                                    scriptwriter.write("/agisk_tmp/sgdisk /dev/block/sd" + alphabet + " --load-backup=/agisk_tmp/GPT_BACKUP/sd"+alphabet+".bin\n");
                                }
                            }
                        }else{
                            //emmc
                            cmd.add(fw_root + "/sgdisk /dev/block/mmcblk0 --backup=" + fw_root + "/GPT_BACKUP/mmcblk0.bin");
                            scriptwriter.write("/agisk_tmp/sgdisk /dev/block/mmcblk0 --load-backup=/agisk_tmp/GPT_BACKUP/mmcblk0.bin\n");
                        }
                        scriptwriter.write("ui_print \"GPT Restore finished\"\n");
                        scriptwriter.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (backup_writer != null) {
                        try {
                            backup_writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (scriptwriter != null) {
                        try {
                            scriptwriter.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (restore_writer != null) {
                        try {
                            restore_writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                //run cmd
                for (String execmd : cmd) {
                    //Log.d(TAG.SystemInforMap_TAG, execmd);
                    Shell.cmd(execmd).exec();
                }
                //compress
                String date = DateUtils.getCurrentDateTimeString();
                try {
                    CompressUtils.compressWithoutBaseDir(fw_root, root_dir + "/firmware_flashable_" + date + ".zip");

                    //clean up
                    Shell.cmd("rm -rf " + fw_root).exec();
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG.SystemInforMap_TAG, "Unable to compress file");
                    success = false;
                }
                if (success) {
                    //mv to /sdcard
                    String output_file = root_dir + "/firmware_flashable_" + date + ".zip";
                    String final_file = output_file;

                    if (Settings.needMvToSdcard) {
                        String move_to="/sdcard/firmware_flashable_" + date + ".zip";
                        final_file = move_to;
                        Shell.cmd("mv " + output_file +" "+ move_to).exec();
                    }
                    WaitDialog.dismiss();
                    MessageDialog.overrideCancelable = BaseDialog.BOOLEAN.TRUE;
                    MessageDialog.show("Success",
                            "See " + final_file,
                            "Copy Path").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog baseDialog, View v) {
                            if (Settings.needMvToSdcard) {
                                //move to sdcard
                                ClipboardUtil.copyToClipboard(getContext(), "/sdcard/firmware_flashable_" + date + ".zip");
                                return false;
                            }
                            ClipboardUtil.copyToClipboard(getContext(), output_file);
                            return false;
                        }
                    });
                    return;
                }
                WaitDialog.dismiss();
                TipDialog.show("Generate Failed", WaitDialog.TYPE.ERROR, -1);

            }
        }).start();


    }
    //
///////////////////////////////////////////////////////////////
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
            MessageDialog.show("Null", "No part selected", "Cancel");
            return false;
        }
        GPTPart part = (GPTPart) selectedChunk;

        //this check is unnecessary
        if (part.isMountedInner()) {
            //already mounted
            autoHandleActions();
            MessageDialog.show("Mounted", "Already mounted.See " + part.getMountPointString());
            return true;
        }
        Shell.cmd("[ -e " + part.getMountPointString() + " ] " +
                "&& echo dirExisted || mkdir -p " + part.getMountPointString()).exec();
        String readNum = String.valueOf(part.getNumber() + 1);
        Shell.cmd("mount " + part.getDriver() + readNum + " " + part.getMountPointString()).exec();
        part.checIsMountedInner();
        if (part.isMountedInner()) {
            // mounted
            autoHandleActions();
            MessageDialog.show("Mounted", "See " + part.getMountPointString(), "Copy Path").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                @Override
                public boolean onClick(MessageDialog baseDialog, View v) {
                    ClipboardUtil.copyToClipboard(v.getContext(), part.getMountPointString() + "/");
                    return false;
                }
            });
            return true;
        }
        MessageDialog.show("Failed", "Unable to mount " + part.getName() + "@" + part.getDriver());
        return false;
    }

    /**
     * @return just judge return by the mount state of part
     * * not the umount process
     */
    private boolean partUmount() {
        if (selectedChunk == null) {
            MessageDialog.show("Null", "No part selected");
            return false;
        }
        GPTPart part = (GPTPart) selectedChunk;
        if (part.isMountedInner()) {
            //already mounted
            String cmd = "umount " + part.getMountPointString();
            Shell.cmd(cmd).exec();
            Log.d(TAG.SystemInforMap_TAG, cmd);
            part.checIsMountedInner();
            if (part.isMountedInner()) {
                MessageDialog.show("Failed", "Unable to umount");
                return false;
            }
            autoHandleActions();
            return true;
        }
        autoHandleActions();
        return true;
    }


    private void partNew(View mview) {
        //TODO
        //need DirectFunction
        //check in aide file
        FullScreenDialog.show(new OnBindView<FullScreenDialog>(R.layout.part_new_dialog) {
                                  @Override
                                  public void onBind(FullScreenDialog dialog, View view) {
                                      //View childView = v.findViewById(resId)...
                                      TextView rangeStart = view.findViewById(R.id.newDialog_start_tv);
                                      TextView driver_tv = view.findViewById(R.id.newDialog_driver_tv);
                                      TextView sectorSize_tv = view.findViewById(R.id.newDialog_sector_size_tv);
                                      TextView rangeEnd = view.findViewById(R.id.newDialog_end_tv);
                                      EditText setStart = view.findViewById(R.id.newDialog_start_et);
                                      Button cancel = view.findViewById(R.id.newDialog_cancel_bt);
                                      Button confirm = view.findViewById(R.id.newDialog_confirm_bt);
                                      EditText setEnd = view.findViewById(R.id.newDialog_end_et);
                                      TextView totalTv = view.findViewById(R.id.newDialog_total_tv);
                                      EditText name_et = view.findViewById(R.id.newDialog_name_et);
                                      TextView percentage_tv = view.findViewById(R.id.newDialog_new_percentage_tv);
                                      TextView total_byte_tv = view.findViewById(R.id.newDialog_total_byte_tv);
                                      TextView new_byte_tv = view.findViewById(R.id.newDialog_new_byte_tv);
                                      TextView available_tv = view.findViewById(R.id.newDialog_available_tv);
                                      EditText sizeEt = view.findViewById(R.id.newDialog_size_et);
                                      NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.newDialog_size_type_sp);
                                      NiceSpinner filesystemSp = view.findViewById(R.id.newDialog_filesystem_type_sp);
                                      SeekBar seekBar = view.findViewById(R.id.newDialog_setsize_sb);
                                      DiskUsageView space_usage = view.findViewById(R.id.newDialog_space_usage);

                                      List<String> dataset = new LinkedList<>(Arrays.asList("byte", "sector", "kib", "gib"));
                                      niceSpinner.attachDataSource(dataset);
                                      //set filesystem selector
                                      List<String> filesystems = new LinkedList<>();
                                      for (PartType partType : PartType.getSupportedPartType()) {
                                          if (partType.getName().contains("Linux")) {
                                              filesystems.add(partType.getName() +
                                                      " (0x" + Integer.toHexString(partType.getMbrType()).toUpperCase()
                                                      + ")");
                                          }
                                      }
                                      filesystemSp.attachDataSource(filesystems);

                                      //set data
                                      driver_tv.setText(selectedDriver.getPath());
                                      sectorSize_tv.setText(String.valueOf(selectedDriver.getBlock_size()));
                                      rangeStart.setText(String.valueOf(selectedChunk.getStartSector() * selectedDriver.getBlock_size()));
                                      rangeEnd.setText(String.valueOf(((selectedChunk.getEndSector() + 1) * selectedDriver.getBlock_size()) - 1));
                                      setStart.setText(String.valueOf(selectedChunk.getStartSector() * selectedDriver.getBlock_size()));
                                      setEnd.setText(String.valueOf(((selectedChunk.getEndSector() + 1) * selectedDriver.getBlock_size()) - 1));
                                      totalTv.setText(String.valueOf(selectedChunk.getSize_sector() * selectedDriver.getBlock_size()));
                                      total_byte_tv.setText(String.valueOf(selectedChunk.getSize_sector() * selectedDriver.getBlock_size()));
                                      new_byte_tv.setText(String.valueOf(selectedChunk.getSize_sector() * selectedDriver.getBlock_size()));
                                      sizeEt.setText(String.valueOf(selectedChunk.getSize_sector() * selectedDriver.getBlock_size()));
                                      available_tv.setText(total_byte_tv.getText());


                                      //
                                      seekBar.setMax(100);
                                      seekBar.setProgress(100);

                                      //
                                      long minStart = selectedChunk.getStartSector() * selectedDriver.getBlock_size();
                                      long maxEnd = ((selectedChunk.getEndSector() + 1) * selectedDriver.getBlock_size()) - 1;

                                      setStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                          @Override
                                          public void onFocusChange(View view, boolean b) {

                                              String s = setStart.getText().toString();
                                              if (s != null && !s.equals("")) {
                                                  if (minStart != -1 && maxEnd != -1) {//最大值和最小值自设
                                                      long a = 0;
                                                      try {
                                                          a = Long.parseLong(s.toString());
                                                      } catch (NumberFormatException e) {
                                                          a = 0;
                                                      }
                                                      if (a < minStart) {
                                                          setStart.setText(String.valueOf(minStart));
                                                      }
                                                      if (a > maxEnd) {
                                                          setStart.setText(String.valueOf(maxEnd));
                                                      }

                                                  }
                                              } else {
                                                  setStart.setText(String.valueOf(selectedChunk.getStartSector() * selectedDriver.getBlock_size()));
                                              }
                                              //update sizeEt
                                              long size_set = Long.parseLong(setEnd.getText().toString()) - Long.parseLong(setStart.getText().toString()) + 1;
                                              long available_total = Long.valueOf(rangeEnd.getText().toString()) - Long.valueOf(setStart.getText().toString()) + 1;
                                              sizeEt.setText(String.valueOf(size_set));
                                              //update available
                                              available_tv.setText(String.valueOf(Long.valueOf(rangeEnd.getText().toString())
                                                      -
                                                      Long.valueOf(setStart.getText().toString()) + 1));
                                              //update seekbar
                                              int seekbar_value = (int) (size_set / available_total) * 100;
                                              seekBar.setProgress(seekbar_value);
                                              //update diskusage
                                              space_usage.setData(Long.valueOf(total_byte_tv.getText().toString())
                                                      , Long.valueOf(rangeStart.getText().toString())
                                                      , Long.valueOf(rangeStart.getText().toString())
                                                      , Long.valueOf(setStart.getText().toString())
                                                      , Long.valueOf(setEnd.getText().toString()));
                                              space_usage.invalidate();
                                          }


                                      });
                                      setEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                          @Override
                                          public void onFocusChange(View view, boolean b) {
                                              String s = setEnd.getText().toString();
                                              if (s != null && !s.equals("")) {
                                                  if (minStart != -1 && maxEnd != -1) {//最大值和最小值自设
                                                      long a = 0;
                                                      try {
                                                          a = Long.parseLong(s.toString());
                                                      } catch (NumberFormatException e) {
                                                          a = 0;
                                                      }

                                                      long start = 0;
                                                      try {
                                                          start = Long.parseLong(setStart.getText().toString());
                                                      } catch (NumberFormatException e) {
                                                          start = maxEnd;
                                                      }
                                                      if (a < start) {
                                                          setEnd.setText(String.valueOf(start));
                                                      }
                                                      if (a > maxEnd) {
                                                          setEnd.setText(String.valueOf(maxEnd));

                                                      }

                                                  }
                                              } else {
                                                  setEnd.setText(rangeEnd.getText());
                                              }

                                              //update sizeEt
                                              long size_set = Long.parseLong(setEnd.getText().toString()) - Long.parseLong(setStart.getText().toString()) + 1;
                                              long available_total = Long.valueOf(rangeEnd.getText().toString()) - Long.valueOf(setStart.getText().toString()) + 1;
                                              sizeEt.setText(String.valueOf(size_set));
                                              //update available
                                              available_tv.setText(String.valueOf(Long.valueOf(rangeEnd.getText().toString())
                                                      -
                                                      Long.valueOf(setStart.getText().toString()) + 1));
                                              //update seekbar
                                              int seekbar_value = (int) (size_set / available_total) * 100;
                                              seekBar.setProgress(seekbar_value);
                                              //update diskusage
                                              space_usage.setData(Long.valueOf(total_byte_tv.getText().toString())
                                                      , Long.valueOf(rangeStart.getText().toString())
                                                      , Long.valueOf(rangeStart.getText().toString())
                                                      , Long.valueOf(setStart.getText().toString())
                                                      , Long.valueOf(setEnd.getText().toString()));
                                              space_usage.invalidate();
                                          }
                                      });
                                      sizeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                          @Override
                                          public void onFocusChange(View view, boolean b) {
                                              String s = sizeEt.getText().toString();
                                              long available_total = 0;
                                              try {
                                                  available_total = Long.parseLong(rangeEnd.getText().toString()) - Long.parseLong(setStart.getText().toString()) + 1;
                                              } catch (NumberFormatException e) {
                                                  available_total = 0;
                                              }
                                              if (s != null && !s.equals("")) {
                                                  if (available_total > 0) {
                                                      long a = 0;
                                                      try {
                                                          a = Long.parseLong(s.toString());
                                                      } catch (NumberFormatException e) {
                                                          a = 0;
                                                      }

                                                      if (a > available_total) {
                                                          sizeEt.setText(String.valueOf(available_total));
                                                      } else if (a > 0) {
                                                          setEnd.setText(String.valueOf(a + Long.valueOf(setStart.getText().toString()) - 1));
                                                      }

                                                  }
                                              } else {
                                                  sizeEt.setText("0");
                                              }
                                          }
                                      });
                                      seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                          @Override
                                          public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                                              percentage_tv.setText(String.valueOf(i));
                                              if (fromUser) {
                                                  long available_total = Long.valueOf(rangeEnd.getText().toString()) - Long.valueOf(setStart.getText().toString()) + 1;
                                                  long start = Long.valueOf(setStart.getText().toString());
                                                  long size = (available_total * i / 100);
                                                  long end = start + size - 1;
                                                  if (i == 0) {
                                                      end++;
                                                  }
                                                  setEnd.setText(String.valueOf(end));
                                                  sizeEt.setText(String.valueOf(size));
                                                  //update diskusage
                                                  space_usage.setData(Long.valueOf(total_byte_tv.getText().toString())
                                                          , Long.valueOf(rangeStart.getText().toString())
                                                          , Long.valueOf(rangeStart.getText().toString())
                                                          , Long.valueOf(setStart.getText().toString())
                                                          , Long.valueOf(setEnd.getText().toString()));
                                                  space_usage.invalidate();
                                              }
                                          }

                                          @Override
                                          public void onStartTrackingTouch(SeekBar seekBar) {

                                          }

                                          @Override
                                          public void onStopTrackingTouch(SeekBar seekBar) {

                                          }
                                      });
                                      cancel.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              dialog.dismiss();
                                          }
                                      });
                                      confirm.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view_i) {
                                              //async block
                                              dialog.dismiss();
                                              WaitDialog.show("Creating partition on " + selectedDriver.getPath());
                                              disablePartUIs();
                                              new Thread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      boolean result = DirectFunctionUtils.Direct3_PART_NEW(getActivity(), selectedDriver.getPath(),
                                                              Long.valueOf(setStart.getText().toString()),
                                                              Long.valueOf(setEnd.getText().toString()),
                                                              (String) filesystemSp.getSelectedItem(),
                                                              name_et.getText().toString());
                                                      WaitDialog.dismiss();
                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              //failed
                                                              if (!result) {
                                                                  MessageDialog.show("Create Part", "Failed to create partition on " + selectedDriver.getPath());
                                                                  enablePartUIs();
                                                                  disableExistedPartActions();
                                                                  return;
                                                              }
                                                              TipDialog.show("Success", WaitDialog.TYPE.SUCCESS, -1);
                                                              enablePartUIs();
                                                              disableExistedPartActions();
                                                              disableFreeChunkActions();
                                                              setData(selectedDriver.getPath());
                                                          }
                                                      });


                                                  }
                                              }).start();
                                          }
                                      });

                                  }
                              }
        );
        return;
    }


    /**
     * async task
     */
    private void partDelete() {

        //Need DirectFunction
        WaitDialog.overrideCancelable= BaseDialog.BOOLEAN.FALSE;
        WaitDialog.show("Deleting");
        GPTPart part = (GPTPart) selectedChunk;
        part.checIsMountedInner();
        if (part.isMountedInner()) {
            MessageDialog.show("Invalid", "Part is already mounted");
            return;
        }
        MessageDialog.show("Delete Part", "Are you sure to delete part " + part.getName() + ":"
                + part.getNumber() + " on " + selectedDriver.getPath(), "Delete").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
            @Override
            public boolean onClick(MessageDialog baseDialog, View v) {
                //disable all related ui
                disablePartUIs();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = DirectFunctionUtils.Direct2_PART_DELETE(getContext(), selectedDriver.getPath(), part.getNumber());
                        //resume ui s
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TipDialog.overrideCancelable = BaseDialog.BOOLEAN.TRUE;
                                WaitDialog.dismiss();
                                if (result) {
                                    TipDialog.show("Success", WaitDialog.TYPE.SUCCESS, -1);
                                    //reload partition table
                                    enablePartUIs();
                                    disableFreeChunkActions();
                                    disableExistedPartActions();
                                    setData(selectedDriver.getPath());
                                    return;
                                }
                                enablePartUIs();
                                disableFreeChunkActions();
                                enableExistedPartActions();
                                TipDialog.show("Failed", WaitDialog.TYPE.ERROR, -1);
                            }
                        });
                    }
                }).start();
                return false;
            }
        });
    }

    private void partSettings(View view) {
        //Give dialog
        PopMenu.show(new String[]{"Flash", "Backup", "Check", "Fix"})
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PopMenu>() {
                    @Override
                    public boolean onClick(PopMenu dialog, CharSequence text, int index) {
                        switch (index) {
                            case 0:
                                //flash
                                new InputDialog("Flash", "flash image to " + ((GPTPart) selectedChunk).getDriver() + ":" + ((GPTPart) selectedChunk).getName(), "Go", "Cancel", "/sdcard/?.img")
                                        .setCancelable(false)
                                        .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                                            @Override
                                            public boolean onClick(InputDialog baseDialog, View v, String inputStr) {
                                                if (inputStr.length() == 0) {
                                                    return false;
                                                }
                                                StringBuilder sb = new StringBuilder();
                                                InputStream is = null;
                                                try {
                                                    is = getActivity().getAssets().open("innerConfigFile/DiskWriteMod.xml");
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                                                    String str;
                                                    GPTPart part = (GPTPart) selectedChunk;
                                                    while ((str = br.readLine()) != null) {
                                                        if (str.contains(XMLmod.REPLACE_DRIVER_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_DRIVER_KEY, part.getDriver());
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_START_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_START_KEY, String.valueOf(part.getStartBytes()));
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_LENGTH_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_LENGTH_KEY, String.valueOf(part.getLengthBytes()));
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_RAW_FILE_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_RAW_FILE_KEY, inputStr);
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_OFFSET_RAW_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_OFFSET_RAW_KEY, "0");
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
                                                        ((MainActivity) getActivity()).showLogViewer(1);

                                                    }
                                                });
                                                return false;
                                            }
                                        })
                                        .show();
                                break;
                            case 1:
                                //backup
                                new InputDialog("Backup", "Backup " + ((GPTPart) selectedChunk).getDriver() + ":" + ((GPTPart) selectedChunk).getName()+" to ?", "Go", "Cancel", "/sdcard/"+((GPTPart) selectedChunk).getName()+".img")
                                        .setCancelable(false)
                                        .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                                            @Override
                                            public boolean onClick(InputDialog baseDialog, View v, String inputStr) {
                                                if (inputStr.length() == 0) {
                                                    return false;
                                                }
                                                StringBuilder sb = new StringBuilder();
                                                InputStream is = null;
                                                try {
                                                    is = getActivity().getAssets().open("innerConfigFile/DiskBackupMod.xml");
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                                                    String str;
                                                    GPTPart part = (GPTPart) selectedChunk;
                                                    while ((str = br.readLine()) != null) {
                                                        if (str.contains(XMLmod.REPLACE_DRIVER_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_DRIVER_KEY, part.getDriver());
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_START_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_START_KEY, String.valueOf(part.getStartBytes()));
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_LENGTH_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_LENGTH_KEY, String.valueOf(part.getLengthBytes()));
                                                        }
                                                        if (str.contains(XMLmod.REPLACE_DESTFILE_KEY)) {
                                                            str = str.replace(XMLmod.REPLACE_DESTFILE_KEY, inputStr);
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
                                                        ((MainActivity) getActivity()).showLogViewer(1);

                                                    }
                                                });
                                                return false;
                                            }
                                        })
                                        .show();
                                break;
                            case 2:
                                //check

                                break;
                            case 3:
                                //fix

                                break;
                        }
                        return false;
                    }
                });
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
        TextView serial_number = view.findViewById(R.id.serial_number);

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

        Shell.cmd("cat /sys/devices/soc0/serial_number").to(result).exec();
        String serial_num_s = result.get(result.size() - 1);
        serial_number.setText(serial_num_s);
        serial_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardUtil.copyToClipboard(getContext(),serial_num_s);
                Toast.makeText(getContext(), "Serial Num copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });


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
        partListTableView = view.findViewById(R.id.part_list_tableview);
        partListTableView.setTableViewListener(new SimpleTableViewListener() {
            @Override
            public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                //super.onCellClicked(cellView, column, row);
                Log.d(TAG.SystemInforMap_TAG, "Click Table View ROW : " + row);
                tableViewChooseDiskChunk(row);
            }
        });
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

                            if (partList.get(i).getPart_type().getPartType() == PartType.PartEnum.TYPE_FREESPACE) {
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

                        setUpPartListTableView(driver_data);
                        //unblock spinner and chart
                        diskSpinner.setEnabled(true);
                        chart.setEnabled(true);
                        chartNoticer.setVisibility(View.INVISIBLE);


                    }
                });
            }
        }).start();

    }


    /**
     * set up recyclerview for part list
     *
     * @param driver
     */

    private List<RowHeader> mRowHeaderList = new ArrayList<>();
    private List<ColumnHeader> mColumnHeaderList = new ArrayList<>();
    private List<List<Cell>> mCellList = new ArrayList<>();
    TableView partListTableView = null; //fuck on in piechart setup

    private void setUpPartListTableView(GPTDriver driver) {
        setCellList(driver);
        // Create our custom TableView Adapter
        MyTableViewAdapter adapter = new MyTableViewAdapter(getContext());

        // Set this adapter to the our TableView
        partListTableView.setAdapter(adapter);

        // Let's set datas of the TableView on the Adapter
        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
    }

    private void setCellList(GPTDriver driver) {
        mRowHeaderList.clear();
        mColumnHeaderList.clear();
        mCellList.clear();

        /**
         * supported column number:name:start:end:code:typeGUID
         */
        mColumnHeaderList.add(new ColumnHeader("Number"));
        mColumnHeaderList.add(new ColumnHeader("Name"));
        mColumnHeaderList.add(new ColumnHeader("StartLBA"));
        mColumnHeaderList.add(new ColumnHeader("EndLBA"));
        mColumnHeaderList.add(new ColumnHeader("CODE"));
        mColumnHeaderList.add(new ColumnHeader("Filesystem"));

        for (int counter = 0; counter < driver.getPartList().size(); counter++) {
            DiskChunk chunk = driver.getPartList().get(counter);
            List<Cell> subcell_list = new ArrayList<>();
            switch (chunk.getPart_type().getPartType()) {
                case TYPE_FREESPACE:
                    //freespace
                    //number:name:start:end:code:typeGUID
                    subcell_list.add(new Cell("-1"));
                    subcell_list.add(new Cell("*Unused"));
                    subcell_list.add(new Cell(String.valueOf(chunk.getStartSector())));
                    subcell_list.add(new Cell(String.valueOf(chunk.getEndSector())));
                    subcell_list.add(new Cell(""));
                    subcell_list.add(new Cell(""));
                    break;
                case TYPE_TODOPART:
                    //other
                    //number:name:start:end:code:typeGUID
                    GPTPart part = (GPTPart) chunk;
                    subcell_list.add(new Cell(String.valueOf(part.getNumber())));
                    subcell_list.add(new Cell(part.getName()));
                    subcell_list.add(new Cell(String.valueOf(chunk.getStartSector())));
                    subcell_list.add(new Cell(String.valueOf(chunk.getEndSector())));
                    String code = "0x" + Integer.toHexString(Integer.parseInt(part.getCode())).toUpperCase();
                    subcell_list.add(new Cell(code));
                    subcell_list.add(new Cell(part.getPart_type().getName()));
                    break;
                default:
                    break;
            }
            mRowHeaderList.add(new RowHeader(String.valueOf(counter)));
            mCellList.add(subcell_list);
        }
    }

    /**
     * The origin_index is the index of item in list
     * but in piechart I grouped small part into [other] entry
     * so the origin_index won't fit piechart
     *
     * @param origin_index
     */
    private void tableViewChooseDiskChunk(int origin_index) {
        if (origin_index >= selectedDriver.getPartList().size() | origin_index < 0) {
            Log.d(TAG.SystemInforMap_TAG, "tableViewChoose set selectedChunk null 1");
            selectedChunk = null;
            disableExistedPartActions();
            disableFreeChunkActions();
            return;
        }
        selectedChunk = selectedDriver.getPartList().get(origin_index);
        if (selectedChunk == null) {
            Log.d(TAG.SystemInforMap_TAG, "tableViewChoose set selectedChunk null 2");
        }
        /**
         * calcuate piechart index
         */
        int piechart_index = -1;
        long threshold = (long) (0.1 * selectedDriver.getTotal_size_sector());
        for (int i = 0; i <= origin_index; i++) {
            DiskChunk chunk = selectedDriver.getPartList().get(i);
            if (chunk.getSize_sector() < threshold) {
                if (chunk.getPart_type().getPartType() == PartType.PartEnum.TYPE_FREESPACE) {
                    piechart_index++;
                }
                continue;
            }
            piechart_index++;
        }

        //set pie chart select
        switch (selectedChunk.getPart_type().getPartType()) {
            case TYPE_TODOPART:
                GPTPart part = (GPTPart) selectedChunk;
                if (part.getSize_sector() < (long) (0.1 * selectedDriver.getTotal_size_sector())) {
                    // smaller part
                    selectOtherEntry();
                } else {
                    selectEntry(piechart_index);
                }
                disableFreeChunkActions();
                enableExistedPartActions();
                break;
            case TYPE_FREESPACE:
                selectEntry(piechart_index);
                disableExistedPartActions();
                enableFreeChunkActions();
        }
    }

    ///////////////////////////////////////

    private GPTDriver getPartList(String driver) {
        String result = DirectFunctionUtils.Direct1_PART_DUMPER(getActivity(), driver);
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
                freeChunk.setPart_type(PartType.getPartType(0));
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
                part.setPart_type(PartType.getPartType(Integer.parseInt(partData[4])));

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


    boolean preventPieReselect = false;

    private void selectEntry(int index) {
        if (index > chart.getData().getEntryCount() - 1) {
            //out of range
            TipDialog.show("Out of range");
            return;
        }
        preventPieReselect = true;
        chart.highlightValue(index, 0);
    }

    private void selectOtherEntry() {
        int last_index = chart.getData().getDataSet().getEntryCount() - 1;
        preventPieReselect = true;
        chart.highlightValue(last_index, 0);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //prevent reselect tableview cell
        if (preventPieReselect) {
            Log.d(TAG.SystemInforMap_TAG, "Prevented onvalueselected");
            preventPieReselect = false;
            return;
        }
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
            partListTableView.getSelectionHandler().clearSelection();
        } else {
            selectedChunk = chunk;
            if (selectedChunk == null) {
                Log.d(TAG.SystemInforMap_TAG, "tableViewChoose set selectedChunk null 3");
            }
            Log.i("VAL SELECTED",
                    "Value: " + e.getY() + ", xIndex: " + e.getX()
                            + ", |  " + chunk.getPart_type().getPartType().toString());
            int pie_index = chart.getData().getDataSet().getEntryIndex((PieEntry) e);
            Log.d(TAG.SystemInforMap_TAG, "Piechart index " + pie_index);
            /**
             * calcuate original index and select in tableview
             */
            int origin_index = 0;
            long threshold = (long) (0.1 * selectedDriver.getTotal_size_sector());
            int counter = 0;
            origin_index = selectedDriver.getPartList().indexOf(chunk);
            Log.d(TAG.SystemInforMap_TAG, "Piechart origin index " + origin_index);
//            for (; origin_index < selectedDriver.getPartList().size(); origin_index++) {
//                DiskChunk ctmp=selectedDriver.getPartList().get(origin_index);
//            }


            partListTableView.setSelectedCell(1, origin_index);

            //

            if (chunk.getPart_type().getPartType() == PartType.PartEnum.TYPE_FREESPACE) {
                Log.d(TAG.SystemInforMap_TAG, "FREESPACE SELECTED");
                disableExistedPartActions();
                enableFreeChunkActions();
            } else {
                Log.d(TAG.SystemInforMap_TAG, "PART SELECTED");
                disableFreeChunkActions();
                enableExistedPartActions();
            }
        }

    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
        Log.d(TAG.SystemInforMap_TAG, "NothingSelected set selectedChunk null");
        selectedChunk = null;
        partListTableView.getSelectionHandler().clearSelection();
        disableExistedPartActions();
        disableFreeChunkActions();
    }


    /**
     * setup disk driver spinner
     */
    private NiceSpinner diskSpinner;
    private String selectedDisk = "";

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
                if (position == 0) {
                    chart.clear();
                    partListTableView.setVisibility(View.GONE);
                    //hint
                    return;
                }
                partListTableView.setVisibility(View.VISIBLE);
                selectedDisk = dlist.get(position);
                setData(dlist.get(position));

            }
        });
        selectedDisk = dlist.get(0);
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
            results.add("Please choose a block device");
            Shell.cmd("ls /dev/block/sd?").to(results).exec();
            return results;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.part_mount_bt:
                Log.d(TAG.SystemInforMap_TAG, "Mount Clicked");
                partMount();
                break;
            case R.id.part_umount_bt:
                Log.d(TAG.SystemInforMap_TAG, "Umount Clicked");
                partUmount();
                break;
            case R.id.part_delete_bt:
                Log.d(TAG.SystemInforMap_TAG, "Delete Clicked");
                partDelete();
                break;
            case R.id.part_new_bt:
                Log.d(TAG.SystemInforMap_TAG, "New Clicked");
                partNew(view);
                break;
            case R.id.part_settings_bt:
                Log.d(TAG.SystemInforMap_TAG, "Settings Clicked");
                partSettings(view);
                break;
            default:
                break;
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

    /**
     * enable existed parts actions
     * auto enable/disable mount/umount button
     */
    private void enableExistedPartActions() {
        partDelete_bt.setEnabled(true);
        partSettings_bt.setEnabled(true);
        if (selectedChunk != null) {
            GPTPart part = (GPTPart) selectedChunk;
            if (part.isMountedInner()) {
                partMount_bt.setEnabled(false);
                partUmount_bt.setEnabled(true);
            } else {
                partMount_bt.setEnabled(true);
                partUmount_bt.setEnabled(false);
            }
        } else {
            Log.d(TAG.SystemInforMap_TAG, "ENABLE EXISTED : NULL");
        }

    }

    private void disableFreeChunkActions() {
        partNew_bt.setEnabled(false);
    }

    private void enableFreeChunkActions() {
        partNew_bt.setEnabled(true);
    }

    private void autoHandleActions() {
        if (selectedChunk == null) {
            disableFreeChunkActions();
            disableExistedPartActions();
            return;
        }
        if (selectedChunk.getPart_type().getPartType() == PartType.PartEnum.TYPE_FREESPACE) {
            //free space
            enableFreeChunkActions();
            disableExistedPartActions();
            return;
        }
        GPTPart part = (GPTPart) selectedChunk;
        disableFreeChunkActions();
        enableExistedPartActions();
        part.checIsMountedInner();
        if (part.isMountedInner()) {
            partMount_bt.setEnabled(false);
            return;
        }
        partUmount_bt.setEnabled(false);

    }

    /////////////////////////////////////////////////////
    NestedScrollView nestedScrollView;

    private void setupBasicUi(View view) {
        nestedScrollView = view.findViewById(R.id.map_container_ns);
    }

    private void disableContainerScroll() {
        nestedScrollView.setNestedScrollingEnabled(false);
    }

    private void enableContainerScroll() {
        nestedScrollView.setNestedScrollingEnabled(true);
    }
}

