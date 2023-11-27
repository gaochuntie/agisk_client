package atms.app.agiskclient.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

import atms.app.agiskclient.R;
import atms.app.agiskclient.SettingsActivity;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link aboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class aboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public aboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment aboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static aboutFragment newInstance(String param1, String param2) {
        aboutFragment fragment = new aboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    Button setting;

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
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initView(view);
        initAboutPage(view);
        return view;
    }

    private void initAboutPage(View view) {
        View aboutPage = new AboutPage(getContext(),false)
                .isRTL(false)
                .setImage(R.drawable.icons_android_studio_144)
                .setDescription("Agisk Client is a free open source project that " +
                        "aims to provide a simple and easy " +
                        "to use interface for Agisk.Before using any function," +
                        "please make sure you have backuped your data " +
                        "and keep them in a safe place (Go to Map->GENERATE FIRMWARE FLASHABLE to backup " +
                        "firmware,system and GPT Table). \nNote : If you point at me for you broken device," +
                        "I will only laugh loudly at you.")
                 // or Typeface
                .addGroup("Contruct with us")
                .addEmail("2041469901@qq.com")
                .addGitHub("gaochuntie")
                .addItem(getTelegramElement())
                .addItem(getSettingsElement())
                .addItem(getCopyRightsElement())
                .create();
        about_page_container.addView(aboutPage);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format("Copy Rights", Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.copyrights_ic_foreground);
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    Element getSettingsElement() {
        Element settingsElement = new Element();
        settingsElement.setTitle("Settings");
        settingsElement.setIconDrawable(R.drawable.settings_ic_foreground);
        settingsElement.setAutoApplyIconTint(true);
        settingsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        settingsElement.setIconNightTint(android.R.color.white);
        settingsElement.setGravity(Gravity.LEFT);
        settingsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

            }
        });
        return settingsElement;
    }
    Element getTelegramElement() {
        Element settingsElement = new Element();
        settingsElement.setTitle("Telegram");
        settingsElement.setIconDrawable(R.drawable.icons_telegram_dark50);
        settingsElement.setAutoApplyIconTint(true);
        settingsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        settingsElement.setIconNightTint(android.R.color.white);
        settingsElement.setGravity(Gravity.LEFT);
        settingsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupId = "0Ouds6DUyhgwMDk1"; // Replace with your actual group ID

                try {
                    // Use the Telegram deep link to open the group
                    Uri uri = Uri.parse("https://t.me/+" + groupId);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exceptions, e.g., if Telegram is not installed
                    // You can show a message to the user to install Telegram
                }

            }
        });
        return settingsElement;
    }
    LinearLayout about_page_container;
    private void initView(View view) {
        about_page_container=view.findViewById(R.id.about_page_container);
    }
}