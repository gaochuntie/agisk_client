package atms.app.agiskclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

public class WelcomScreenActivity extends AppCompatActivity {


    private CheckBox checkBoxAccept;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom_screen);

        checkBoxAccept = findViewById(R.id.checkBoxAccept);
        btnNext = findViewById(R.id.btnNext);

        // Set a listener to enable/disable the "Next" button based on checkbox state
        checkBoxAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnNext.setEnabled(isChecked);
        });

        // Set a listener for the "Next" button click
        btnNext.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("agisk_base",Context.MODE_PRIVATE);

            // Check if the "firstTime" flag is set
            boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
            Log.d("firstTime", String.valueOf(firstTime));
            // If it's the first time, set the flag to false
            if (firstTime) {
                if (Settings.getRootAccess()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("firstTime", false);
                    editor.apply();
                }

            }

            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}