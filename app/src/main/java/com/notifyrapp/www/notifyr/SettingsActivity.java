package com.notifyrapp.www.notifyr;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    TextView txtsettings, txtMaxNotification, txtMaxNotificationDescription, txtNotificationsPerDay, txtDownloadArticleImages, txtArticleReaderMode, txtArticleReaderModeDescription,
            txtAccountInformation, txtNetworkStatus, txtAbout, txtVersion;
    Button btnSendTestNotification, btnPrivacy, btnTerms, btnRateOnAppStore, btnSendFeedback;
    Typeface openSansRegular, openSansLight;
    SeekBar seekBarMaxNotificationsPerDay;
    TextView txtSeekBarValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        seekBar();

        txtsettings = (TextView) findViewById(R.id.txtSettings);
        txtMaxNotification = (TextView) findViewById(R.id.txtMaxNotification);
        txtMaxNotificationDescription = (TextView) findViewById(R.id.txtMaxNotificationDescription);
        txtNotificationsPerDay = (TextView) findViewById(R.id.txtSeekBarValue);
        txtDownloadArticleImages = (TextView) findViewById(R.id.txtDownloadArticleImages);
        txtArticleReaderMode = (TextView) findViewById(R.id.txtArticleReaderMode);
        txtArticleReaderModeDescription = (TextView) findViewById(R.id.txtArticleReaderModeDescription);
        txtAccountInformation = (TextView) findViewById(R.id.txtAccountInformation);
        txtNetworkStatus = (TextView) findViewById(R.id.txtNetworkStatus);
        txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtVersion = (TextView) findViewById(R.id.txtVersion);

        btnSendTestNotification = (Button) findViewById(R.id.btnSendTestNotification);
        btnPrivacy = (Button) findViewById(R.id.btnPrivacy);
        btnTerms = (Button) findViewById(R.id.btnTerms);
        btnRateOnAppStore = (Button) findViewById(R.id.btnRateOnAppStore);
        btnSendFeedback = (Button) findViewById(R.id.btnSendFeedback);

        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        txtsettings.setTypeface(openSansRegular);
        txtMaxNotification.setTypeface(openSansRegular);
        txtMaxNotificationDescription.setTypeface(openSansLight);
        txtNotificationsPerDay.setTypeface(openSansRegular);
        txtDownloadArticleImages.setTypeface(openSansRegular);
        txtArticleReaderMode.setTypeface(openSansRegular);
        txtArticleReaderModeDescription.setTypeface(openSansLight);
        txtAccountInformation.setTypeface(openSansRegular);
        txtNetworkStatus.setTypeface(openSansRegular);
        txtAbout.setTypeface(openSansRegular);
        txtVersion.setTypeface(openSansRegular);
        btnSendTestNotification.setTypeface(openSansRegular);
        btnPrivacy.setTypeface(openSansRegular);
        btnTerms.setTypeface(openSansRegular);
        btnRateOnAppStore.setTypeface(openSansRegular);
        btnSendFeedback.setTypeface(openSansRegular);

    }
    protected void seekBar() {
        seekBarMaxNotificationsPerDay = (SeekBar) findViewById(R.id.seekBarMaxNotificationsPerDay);
        txtSeekBarValue = (TextView) findViewById(R.id.txtSeekBarValue);

        seekBarMaxNotificationsPerDay.setProgress(0);
        seekBarMaxNotificationsPerDay.setMax(19);
        txtSeekBarValue.setText(seekBarMaxNotificationsPerDay.getProgress() + " per day");
        seekBarMaxNotificationsPerDay.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if (progress != 19) {
                            txtSeekBarValue.setText(progress + " per day");
                        } else
                            txtSeekBarValue.setText("No Limit");

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }
}
