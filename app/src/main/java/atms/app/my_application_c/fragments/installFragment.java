package atms.app.my_application_c.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import atms.app.my_application_c.R;
import atms.app.my_application_c.databinding.FragmentInstallBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link installFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class installFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_XML_FILE = 3;
    FragmentInstallBinding fragmentInstallBinding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public installFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment installFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static installFragment newInstance(String param1, String param2) {
        installFragment fragment = new installFragment();
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

        View view = inflater.inflate(R.layout.fragment_install, container, false);
        fragmentInstallBinding = FragmentInstallBinding.bind(view);
        initComponents();
        return view;
    }


    private ImageButton addnewbt;
    private ImageButton editbt;
    private ImageButton importbt;
    private ImageButton helpbt;

    private void initComponents() {
        addnewbt = fragmentInstallBinding.installAddIbt;
        addnewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAction();
            }
        });
        editbt = fragmentInstallBinding.installEditIbt;
        editbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditWindows();
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

    private void showEditWindows() {
        String[] xmllist = getxmllist();
        if (xmllist == null) {
            MessageDialog.show("Edit xml", "(Null)No xml found and you cant edit" +
                    " .enxml file. Try import first");
            return;
        }
        if (xmllist.length == 0) {
            MessageDialog.show("Edit xml", "(Length 0)No xml found and you cant edit" +
                    " .enxml file. Try import first");
            return;
        }
        BottomMenu.show(xmllist)
                .setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>() {
                    @Override
                    public int getIcon(BottomMenu dialog, int index, String menuText) {
                        return R.mipmap.file_icon;
                    }
                })
                .setTitle("Choose a existed xml to edit")
                .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        editXml(text.toString());
                        return false;
                    }
                });
    }

    private String[] getxmllist() {
        File xmldir = getActivity().getExternalFilesDir("home");

        String[] filelist = xmldir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                String finename = s.toLowerCase();
                return finename.endsWith(".xml");
            }
        });
        return filelist;

    }

    private void showDowloadDialog() {
        MessageDialog.show("Download", "Follow my repo on github.");
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
                    PopTip.show("Please reload rom list on your own").showLong();
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