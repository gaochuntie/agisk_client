package atms.app.agiskclient.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

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
import java.util.List;

import atms.app.agiskclient.R;
import atms.app.agiskclient.adapter.xmlListAdapter;
import atms.app.agiskclient.databinding.FragmentXmlManagerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link xmlManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class xmlManagerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_XML_FILE = 3;
    FragmentXmlManagerBinding fragmentInstallBinding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public xmlManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment xmlManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static xmlManagerFragment newInstance(String param1, String param2) {
        xmlManagerFragment fragment = new xmlManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadXmlItem();
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

        View view = inflater.inflate(R.layout.fragment_xml_manager, container, false);
        fragmentInstallBinding = FragmentXmlManagerBinding.bind(view);
        initComponents();
        setupXmlEncrypt();
        //show xml list
        reloadXmlItem();
        return view;
    }


    private ImageButton addnewbt;
    private ImageButton importbt;
    private ImageButton helpbt;

    private RecyclerView xmlListManage;
    private EditText enXml_path;
    private EditText enXml_key;
    private EditText enXml_arg;
    private RadioGroup enXml_rg;
    private Button enXml_en;
    private TextView enXml_log;
    private void initComponents() {
        addnewbt = fragmentInstallBinding.installAddIbt;
        addnewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAction();
            }
        });
        importbt = fragmentInstallBinding.installGetIbt;
        importbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDowloadDialog();
            }
        });
        helpbt = fragmentInstallBinding.installHelpIbt;
        helpbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpPopWin();
            }
        });
        xmlListManage=fragmentInstallBinding.xmlManageList;

        enXml_en=fragmentInstallBinding.xmlEncryptBtDoencrypt;
        enXml_arg=fragmentInstallBinding.xmlEncryptEtArgs;
        enXml_rg=fragmentInstallBinding.xmlEncryptRg;
        enXml_log=fragmentInstallBinding.xmlEncryptTvLog;
        enXml_key=fragmentInstallBinding.xmlEncryptEtKey;
        enXml_path=fragmentInstallBinding.xmlEncryptEtPath;
        enXml_arg.setVisibility(View.GONE);


    }
    private void setupXmlEncrypt(){
        enXml_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.xml_encrypt_rb_need_sn:
                        enXml_arg.setVisibility(View.VISIBLE);
                        break;
                    case R.id.xml_encrypt_rb_noneed_sn:
                        enXml_arg.setVisibility(View.GONE);
                        break;
                }
            }
        });
        enXml_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void reloadXmlItem(){

        Context context=this.getContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<xmlListAdapter.xmlItem> data=getxmllist();
                if (data == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        xmlListAdapter adapter = new xmlListAdapter(data);
                        xmlListManage.setAdapter(adapter);
                        xmlListManage.setLayoutManager(new LinearLayoutManager(context));
                        xmlListManage.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

                    }
                });
            }
        }).start();



    }

    private void showAddAction() {
        com.kongzue.dialogx.dialogs.PopMenu.show(new String[]{"Create", "Import"})
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PopMenu>() {
                    @Override
                    public boolean onClick(PopMenu dialog, CharSequence text, int index) {
                        switch (index) {
                            case 0:

                                createXml();
                                break;
                            case 1:
                                importXml();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .setOnIconChangeCallBack(new OnIconChangeCallBack<PopMenu>() {
                    @Override
                    public int getIcon(PopMenu dialog, int index, String menuText) {
                        switch (index) {
                            case 0:
                                return R.mipmap.add;
                            case 1:
                                return R.mipmap.download_circled;
                        }
                        return 0;
                    }
                })
                .setAlignGravity(Gravity.CENTER);

    }


    private List<xmlListAdapter.xmlItem> getxmllist() {
        File xmldir = getActivity().getExternalFilesDir("home");
        List<xmlListAdapter.xmlItem> xmlItemList = new ArrayList<>();
        String[] filelist = xmldir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                String finename = s.toLowerCase();
                return finename.endsWith(".xml") | finename.endsWith(".enxml");
            }
        });

        for (int i = 0; i < filelist.length; i++) {
            xmlListAdapter.xmlItem item=new xmlListAdapter.xmlItem(i,filelist[i]);
            item.setPath(xmldir+"/"+filelist[i]);
            xmlItemList.add(item);

        }
        return xmlItemList;

    }

    private void showDowloadDialog() {
        final Uri uri = Uri.parse("https://github.com/gaochuntie/agisk_client/tree/dev/xmls/release");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        MessageDialog.show("Download", "Follow my release on github.");
    }

    private void showHelpPopWin() {
        BottomMenu.show(new String[]{"Brief", "Home", "Install", "Map", "About"})
                .setTitle("Help Mannual")
                .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        switch (index) {
                            case 0:
                                MessageDialog.show(text.toString(), getContext().getString(R.string.Brief));
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

    }

    private void createXml() {
        //TODO create xml
    }

    private void importXml() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");//筛选器
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{DOC,DOCX, PPT, PPTX,PDF});
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Choose a xml"), PICK_XML_FILE);

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

            FileOutputStream os = new FileOutputStream(getContext().getExternalFilesDir("home")
                    .getAbsoluteFile() + "/" + uri.getLastPathSegment());
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
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
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
                    copyXmlTOPrivateStorage(data.getData());
                    reloadXmlItem();
                }
                break;
            default:
                break;
        }
    }

    private void editXml(String relative_path) {
        //TODO editxml
        PopTip.show("Edit " + relative_path + " on your own");
    }

    private void downloadXml() {
        //TODO download xml from repo

    }
}