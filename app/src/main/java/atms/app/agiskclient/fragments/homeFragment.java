package atms.app.agiskclient.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.kongzue.dialogx.util.TextInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atms.app.agiskclient.ConfigBox.ActionBase;
import atms.app.agiskclient.ConfigBox.DiskAction;
import atms.app.agiskclient.ConfigBox.OrigConfig;
import atms.app.agiskclient.Data.romListData;
import atms.app.agiskclient.MainActivity;
import atms.app.agiskclient.R;
import atms.app.agiskclient.ReservedAreaKits.ReservedAreaTools;
import atms.app.agiskclient.Settings;
import atms.app.agiskclient.Tools.GlobalMsg;
import atms.app.agiskclient.Tools.TAG;
import atms.app.agiskclient.Tools.Worker;
import atms.app.agiskclient.Tools.clientNumListener;
import atms.app.agiskclient.adapter.noticeListAdapter;
import atms.app.agiskclient.adapter.romItemOnClickListener;
import atms.app.agiskclient.adapter.romListAdapter;
import atms.app.agiskclient.aidl.workClient;

public class homeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView selectedCategoryRomList;
    private RecyclerView noticeListRC;
    private TabLayout romCategoryTab;
    private View myview;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);
        myview = view;
        bindView(view);
        ReservedAreaTools.initRepository();
        setUpRomCategoryList(view);

        setupTaskManager(view);

        showNotice();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSelectedCategoryRomList(0);
    }

    private void setupTaskManager(View view) {
        ((MainActivity) getActivity()).setupLogPopWindows(view);
        view.findViewById(R.id.fab_tasksmanager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showLogViewer();
            }
        });
    }

    private void showNotice() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        noticeListRC.setLayoutManager(linearLayoutManager);

        List<noticeListAdapter.NoticeItem> notice_list = new ArrayList<>();

        notice_list.add(new noticeListAdapter.NoticeItem("Update notes", "Provide basic function:\n" + "DiskAction all\n" + "PartitionAction new123 delete12 \n" + "Provide some xmls :\n" + "System Partition Expansion\n" + "Virtual SD Card Maker\n"));

        notice_list.add(new noticeListAdapter.NoticeItem("Coffee", "paypal.me/gaochuntie\n"));
        notice_list.add(new noticeListAdapter.NoticeItem("Warning", "Partition Number start from 0 , not 1 as sgdisk"));

        notice_list.add(new noticeListAdapter.NoticeItem("Join Us", "------So join us to show off your talent.\n" + "jackmaxpale@gmail.com\n" + "2041469901@qq.com\n" + "https://t.me/+0Ouds6DUyhgwMDk1\n" + "https://github.com/gaochuntie/agisk_client"));
        noticeListRC.setAdapter(new noticeListAdapter(notice_list));
    }

    /**
     * show category management dialog
     */
    private void manageCategory() {
        //clean list
        List<String> list = new ArrayList<>();


        String[] items = null;
        String[] cat = getRomCategoryList();
        if (cat != null) {
            //dirty array
            items = new String[cat.length + 1];
            for (int i = 0; i < cat.length; i++) {
                list.add(cat[i]);
                items[i] = cat[i];
            }

            items[items.length - 1] = "Add";
        } else {
            items = new String[1];
            items[0] = "Add";
        }


        Log.d(TAG.HomeFragTag, "Items length : " + items.length);

        BottomMenu bottomMenu = BottomMenu.show(items).setMessage("Manage Category item ").setDialogLifecycleCallback(new DialogLifecycleCallback<BottomDialog>() {
            @Override
            public void onDismiss(BottomDialog dialog) {
                super.onDismiss(dialog);
                setUpRomCategoryList(myview);
                romCategoryTab.selectTab(romCategoryTab.getTabAt(0));

            }
        });
        bottomMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
            @Override
            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                if (text.equals("Add")) {
                    InputDialog inputDialog = new InputDialog("Add one filter", "add a filter for rom,which " + "will be used to sort out your prefer rom s" + "uch as pixel,evolutionX...", "Confirm", "Cancel", "");
                    inputDialog.setCancelable(false);
                    inputDialog.setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                        @Override
                        public boolean onClick(InputDialog baseDialog, View v, String inputStr) {
                            if (inputStr.length() == 0) {
                                return true;
                            }
                            if (inputStr.contains(" ")) {
                                Snackbar.make(v, "NO space ", Snackbar.LENGTH_SHORT).show();
                                return true;
                            }
                            list.add(inputStr);
                            setRomCategoryList(list);
                            bottomMenu.dismiss();
                            return false;
                        }
                    });
                    inputDialog.show();
                    return true;
                }
                MessageDialog messageDialog = new MessageDialog("Manage", "Delete this filter?", "Delete", "Cancel").setOkTextInfo(new TextInfo().setFontColor(Color.RED)).setButtonOrientation(LinearLayout.VERTICAL).setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog baseDialog, View v) {
                        list.remove(text);
                        setRomCategoryList(list);
                        bottomMenu.dismiss();
                        return false;
                    }
                });
                messageDialog.show();

                return true;
            }
        });
    }

    private void bindView(View view) {
        selectedCategoryRomList = view.findViewById(R.id.selectedCategoryList);
        noticeListRC = view.findViewById(R.id.homeNoticeBoardList);
        romCategoryTab = view.findViewById(R.id.romCategoryList);


        //Capture the touch event to prevent the parent from moving uncorrectly
        selectedCategoryRomList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        selectedCategoryRomList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        romCategoryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equalsIgnoreCase("manage")) {
                    manageCategory();
                    return;
                    //
                }
                refreshSelectedCategoryRomList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        romCategoryTab.setTabGravity(TabLayout.GRAVITY_FILL);
        registClientNumListener();
    }


    private void setUpRomCategoryList(View view) {

        String[] list = getRomCategoryList();
        romCategoryTab.removeAllTabs();
        romCategoryTab.addTab(romCategoryTab.newTab().setText("All"));

        if (list != null) {
            for (String item : list) {
                romCategoryTab.addTab(romCategoryTab.newTab().setText(item));
            }
        }

        romCategoryTab.addTab(romCategoryTab.newTab().setText("Manage"));


    }


    /**
     * get category list,clean list
     *
     * @return
     */
    private String[] getRomCategoryList() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Set<String> content = sharedPref.getStringSet(getString(R.string.romcategory_list_key), null);
        if (content == null) {
            return null;
        }
        String[] ret = new String[content.size()];
        Log.d(TAG.HomeFragTag, "Set size " + content.size());
        int count = 0;
        for (String item : content) {
            ret[count] = item;
            count++;
        }
        return ret;
    }

    private void setRomCategoryList(List<String> list) {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.romcategory_list_key), new HashSet<String>(list));
        editor.apply();
    }


    //  get romlist async

    private String[] getRomList(String category) {
        //init reserved repository first
        refreshReservedRepository();
        //
        List<String> list = new ArrayList<>();
        romListData.clearRomList();

        File xmldir = getActivity().getExternalFilesDir("home");

        File[] filelist = xmldir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                String finename = s.toLowerCase();
                return finename.endsWith(".xml") | finename.endsWith(".enxml");
            }
        });

        int id = 0;
        for (File item : filelist) {
            OrigConfig origConfig = new OrigConfig(item.getAbsolutePath());
            if (!origConfig.isParseSuccess()) {
                //parse xml failed
                continue;
            }
            //get
            String category_min = category.toLowerCase();
            if (romListData.isMatchFilter(origConfig.getAttributions().get("filter"), category_min)
                    | category_min.equals("all")) {
                romListData rom = romListData.addRom(id);
                rom.setOrigConfig(origConfig);
                rom.initRomFromOrigConfig();
                list.add(rom.getRomname());
                id++;
                //set reserved state
                //same id will ignore protection
                rom.setIsReservedProtected();

            }

        }

        //
        String[] strings = new String[id + 1];
        list.add("command::add");

        return list.toArray(strings);
    }

    /**
     * check all xmls to get reserved area
     */
    private void refreshReservedRepository() {

        ReservedAreaTools.blankRepository();

        File xmldir = getActivity().getExternalFilesDir("home");

        File[] filelist = xmldir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                String finename = s.toLowerCase();
                return finename.endsWith(".xml") | finename.endsWith(".enxml");
            }
        });

        for (File item : filelist) {
            OrigConfig origConfig = new OrigConfig(item.getAbsolutePath());
            if (!origConfig.isParseSuccess()) {
                //parse xml failed
                Log.d(TAG.HomeFragTag, "Parse " + item.getAbsolutePath() + " failed");
                continue;
            }
            //get reserved
            origConfig.setActionList();
            List<ActionBase> actionBaseList = origConfig.getActionList();
            for (ActionBase actionBase : actionBaseList) {
                if (actionBase.actiontype == ActionBase.ActionType.ACTION_TYPE_DISK) {
                    DiskAction diskAction = (DiskAction) actionBase;
                    if (diskAction.type == DiskAction.Disk_Action_Type.DISK_ACTION_TYPE_RESERVE) {

                        //put reserved area to Repository
                        DiskAction.ReservedChunks reservedChunks = diskAction.getReservedChunks();
                        if (reservedChunks == null) {
                            continue;
                        }

                        //reserved found
                        String id = origConfig.getAttributions().get("id");
                        Log.d(TAG.HomeFragTag, "Reserved : " + reservedChunks.driver + "|" + reservedChunks.start + "|" + reservedChunks.length + " ID " + id);
                        GlobalMsg.appendLog("Put Reserved : " + reservedChunks.driver + "|" + reservedChunks.start + "|" + reservedChunks.length + " ID " + id, GlobalMsg.GLOBAL_LOG);

                        ReservedAreaTools.putArea(reservedChunks.driver, reservedChunks.start, reservedChunks.length, id);
                    }
                }
            }


        }
        ReservedAreaTools.dPrintAllReserved();
    }

    private void refreshSelectedCategoryRomList(int category) {
        //Snackbar.make(myview, "Select " + romCategoryTab.getTabAt(category).getText(), Snackbar.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String[] romlist = getRomList(romCategoryTab.getTabAt(category).getText().toString());
                //get rom list

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        selectedCategoryRomList.setLayoutManager(linearLayoutManager);
                        Log.d(TAG.HomeFragTag, "Romlist size " + romlist.length);
                        selectedCategoryRomList.setAdapter(new romListAdapter(romlist, new romItemOnClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                showSelectedRomDetails(position);
                            }
                        }));

                    }
                });
                //update ui
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        Log.d("HOME", "view clicked");
        switch (view.getId()) {
            default:
                break;

        }
    }


    // Request code for selecting a PDF document.
    private static final int PICK_XML_FILE = 2;
    private static final int PICK_ENXML_FILE = 3;

    private void openFile(String filter) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");//筛选器
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{DOC,DOCX, PPT, PPTX,PDF});
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Choose a xml"), PICK_XML_FILE);
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * .
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_XML_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG.HomeFragTag, "Choose a extra xml");
                    copyXmlTOPrivateStorage(data.getData());
                }
                break;
            default:
                break;
        }
    }

    private void copyXmlTOPrivateStorage(Uri uri) {
        try {
            //保存读取到的内容
            StringBuilder result = new StringBuilder();
            //获取URI

            //获取输入流
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            //创建用于字符输入流中读取文本的bufferReader对象
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                //将读取到的内容放入结果字符串
                result.append(line);
            }
            //文件中的内容
            String content = result.toString();

            FileOutputStream os = new FileOutputStream(getContext().getExternalFilesDir("home").getAbsoluteFile() + "/" + uri.getLastPathSegment());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(content.toCharArray());
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        refreshSelectedCategoryRomList(romCategoryTab.getSelectedTabPosition());
    }

    private void showSelectedRomDetails(int id) {
        romListData rom = romListData.getRom(id);
        if (rom == null) {
            //add rom
            //Snackbar.make(myview, "Null rom", Snackbar.LENGTH_SHORT).show();
            openFile("xml");
            return;
        }

        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.rom_detail_dialog) {
            @Override
            public void onBind(final CustomDialog dialog, View view) {
                TextView name = view.findViewById(R.id.dialog_romname);
                Button action = view.findViewById(R.id.rom_detail_Action);
                action.setEnabled(true);
                name.setText(rom.getRomname());

                TextView author = view.findViewById(R.id.rom_detail_author);
                TextView uuid = view.findViewById(R.id.rom_detail_uuid);
                TextView id = view.findViewById(R.id.rom_detail_id);
                TextView mark = view.findViewById(R.id.rom_detail_mark);
                TextView description = view.findViewById(R.id.rom_detail_description);
                TextView xml_path = view.findViewById(R.id.rom_detail_xml_location);
                TextView state = view.findViewById(R.id.rom_detail_state);
                //set value
                author.setText(author.getText() + rom.getAuthor());
                uuid.setText(uuid.getText() + rom.getUuid());
                description.setText(description.getText() + rom.getDescription());
                xml_path.setText(xml_path.getText() + rom.getXml_path());
                mark.setText(mark.getText() + rom.getMark());
                id.setText(id.getText() + rom.getId());

                Button cancel = view.findViewById(R.id.rom_detail_cancelBT);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                if (!Settings.getRootAccess()) {
                    action.setEnabled(false);
                    action.setTextColor(Color.BLACK);
                    action.setBackgroundColor(Color.LTGRAY);
                    action.setHint("No root");
                    action.setText("NO ROOT");
                    state.setTextColor(Color.RED);
                    state.setText(state.getText() + "Permission denied.");
                    return;
                }

                //check reserved
                if (rom.isReservedProtected()) {
                    action.setEnabled(false);
                    action.setTextColor(Color.BLACK);
                    action.setBackgroundColor(Color.LTGRAY);
                    action.setHint("INVALID");
                    action.setText("INVALID");
                    state.setTextColor(Color.RED);
                    state.setText(state.getText() + "Broke reserved-area protection , forbidden. ");
                    return;
                }

                action.setEnabled(true);
                state.setTextColor(Color.GREEN);
                state.setText(state.getText() + "VALID");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        workClient client = null;

                        //Do action
                        OrigConfig origConfig = rom.getOrigConfig();

                        client = Worker.putTaskToRootService(origConfig, getActivity());

                        if (client == null) {
                            MessageDialog.show("Error", "Permission denied.This perfermance requires root permission", "Cancel");
                            return;
                        }
                        ((MainActivity) getActivity()).getWorning_box().setBackgroundColor(Color.RED);
                        ((MainActivity) getActivity()).showWorningMsg("Processing in background.Touch to see.");
                        if (client == null) {
                            //Client poll full
                            Toast.makeText(getContext(), "Offer client failed.Up to max.", Toast.LENGTH_LONG).show();
                        }

                        //Mainly, worningbox listener only open the log viewer windows

                        ((MainActivity) getActivity()).getWorning_box().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((MainActivity) getActivity()).showLogViewer();

                            }
                        });
                    }
                });
            }
        }).setMaskColor(Color.parseColor("#4D000000"));
    }


    clientNumListener myClientNumListener = null;

    public void registClientNumListener() {
        myClientNumListener = new clientNumListener() {
            @Override
            public void onNumChanged(int clientnum) {
                Log.d(TAG.HomeFragTag, "Recieve client num " + clientnum);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).updateBadge(0, String.valueOf(clientnum));
                    }
                });
            }
        };
        Worker.registClientNumListener(myClientNumListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Worker.unregistClientNumListener(myClientNumListener);
    }
}
