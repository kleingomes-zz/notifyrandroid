package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.RadioGroup;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Model.UserSetting;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isDirtyServer;
    private boolean isDirtyLocal;
    private boolean truth;
    private int getMaxNotifications;
    private int swtchCounter;
    private int seekbarValueFromUser;
    private int serverRadioButtonValue;
    private int userRadioButtonValue;
    private int articleReaderMode;

    TextView txtsettings, txtMaxNotification, txtMaxNotificationDescription, txtNotificationsPerDay, txtDownloadArticleImages, txtArticleReaderMode, txtArticleReaderModeDescription,
            txtAccountInformation, txtNetworkStatus, txtNetworkStatusGreen, txtAbout, txtVersion, txtVersionNumber;
    Button btnSendTestNotification, btnPrivacy, btnTerms, btnRateOnAppStore, btnSendFeedback;
    RadioButton RadioBtnNever, RadioBtnWifiOnly, RadioBtnAlways;
    RadioGroup radioGroup;
    Typeface openSansRegular, openSansLight;
    SeekBar seekBarMaxNotificationsPerDay;
    TextView txtSeekBarValue;
    Switch swtchArticleReaderMode;
    Business business;
    UserSetting settings;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        //hardcode all the settings on the initial create
        //make the usersettings class global and instantiate it inside oncreate and set it to defaults
        //so when a person makes a change you should be able to test it against an initial flag
        //onleave if isdirty == true then save to the database or local
        //usersettings = new usersettings
        //save to database
        //load to database
        //UserSetting usersetting = new UserSetting();


        //set buttons equal to search
        //make a save funciton that is fake klein will take care of the actual saving




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        //get settings info from the local database
        business = new Business(getActivity());
        settings = business.getUserSettings();

        txtsettings = (TextView) view.findViewById(R.id.txtSettings);
        txtMaxNotification = (TextView) view.findViewById(R.id.txtMaxNotification);
        txtMaxNotificationDescription = (TextView) view.findViewById(R.id.txtMaxNotificationDescription);
        txtNotificationsPerDay = (TextView) view.findViewById(R.id.txtSeekBarValue);
        txtDownloadArticleImages = (TextView) view.findViewById(R.id.txtDownloadArticleImages);
        txtArticleReaderMode = (TextView) view.findViewById(R.id.txtArticleReaderMode);
        txtArticleReaderModeDescription = (TextView) view.findViewById(R.id.txtArticleReaderModeDescription);
        txtAccountInformation = (TextView) view.findViewById(R.id.txtAccountInformation);
        txtNetworkStatus = (TextView) view.findViewById(R.id.txtNetworkStatus);
        txtNetworkStatusGreen = (TextView) view.findViewById(R.id.txtNetworkStatusGreen);
        txtAbout = (TextView) view.findViewById(R.id.txtAbout);
        txtVersion = (TextView) view.findViewById(R.id.txtVersion);
        txtVersionNumber = (TextView) view.findViewById(R.id.txtVersionNumber);
        btnSendTestNotification = (Button) view.findViewById(R.id.btnSendTestNotification);
        btnPrivacy = (Button) view.findViewById(R.id.btnPrivacy);
        btnTerms = (Button) view.findViewById(R.id.btnTerms);
        btnRateOnAppStore = (Button) view.findViewById(R.id.btnRateOnAppStore);
        btnSendFeedback = (Button) view.findViewById(R.id.btnSendFeedback);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        RadioBtnNever = (RadioButton) view.findViewById(R.id.radioButtonNever);
        RadioBtnWifiOnly = (RadioButton) view.findViewById(R.id.radioButtonWifiOnly);
        RadioBtnAlways = (RadioButton) view.findViewById(R.id.radioButtonAlways);

        openSansRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        txtsettings.setTypeface(openSansRegular);
        txtMaxNotification.setTypeface(openSansRegular);
        txtMaxNotificationDescription.setTypeface(openSansLight);
        txtNotificationsPerDay.setTypeface(openSansRegular);
        txtDownloadArticleImages.setTypeface(openSansRegular);
        txtArticleReaderMode.setTypeface(openSansRegular);
        txtArticleReaderModeDescription.setTypeface(openSansLight);
        txtAccountInformation.setTypeface(openSansRegular);
        txtNetworkStatus.setTypeface(openSansRegular);
        txtNetworkStatusGreen.setTypeface(openSansRegular);
        txtAbout.setTypeface(openSansRegular);
        txtVersion.setTypeface(openSansRegular);
        txtVersionNumber.setTypeface(openSansRegular);
        btnSendTestNotification.setTypeface(openSansRegular);
        btnPrivacy.setTypeface(openSansRegular);
        btnTerms.setTypeface(openSansRegular);
        btnRateOnAppStore.setTypeface(openSansRegular);
        btnSendFeedback.setTypeface(openSansRegular);
        RadioBtnNever.setTypeface(openSansRegular);
        RadioBtnWifiOnly.setTypeface(openSansRegular);
        RadioBtnAlways.setTypeface(openSansRegular);
        seekBarMaxNotificationsPerDay = (SeekBar) view.findViewById(R.id.seekBarMaxNotificationsPerDay);
        txtSeekBarValue = (TextView) view.findViewById(R.id.txtSeekBarValue);
        swtchArticleReaderMode = (Switch) view.findViewById(R.id.btnArticleReaderMode);


        //RADIO BUTTONS
        //need to change server value from 1-3 to 0-2 instead in order for this to work
        serverRadioButtonValue = settings.getArticleDisplayType();

        if (serverRadioButtonValue == 0)
        {
            RadioBtnNever.setChecked(true);
        }
        else if (serverRadioButtonValue == 1)
        {
            RadioBtnWifiOnly.setChecked(true);
        }
        else if(serverRadioButtonValue == 2)
        {
            RadioBtnAlways.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (RadioBtnNever.isChecked())
                {
                    userRadioButtonValue = 0;
                }
                else if (RadioBtnWifiOnly.isChecked())
                {
                    userRadioButtonValue = 1;
                }
                else if (RadioBtnAlways.isChecked())
                {
                    userRadioButtonValue = 2;
                }
            }
        });


        // ARTICLE READER MODE SWITCH
        truth = settings.isArticleReaderMode();
        swtchArticleReaderMode.setChecked(truth);
        swtchArticleReaderMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {

                swtchCounter++;

            }
        });

        //NETWORK STATUS AND VERSION NUMBER
        txtNetworkStatusGreen.setText(settings.getNetworkStatus()); //get networkstatus from the db
        txtVersionNumber.setText(settings.getVersion()); //get version number from the db
        //seekBarMaxNotificationsPerDay.setProgress(0);

        //SEEKBAR FOR MAX NOTIFICATIONS
        seekBarMaxNotificationsPerDay.setMax(20);
        getMaxNotifications = settings.getMaxNotificaitons();
        seekBarMaxNotificationsPerDay.setProgress(getMaxNotifications); //set the progress on the maxnotifications to match db
        txtSeekBarValue.setText(seekBarMaxNotificationsPerDay.getProgress() + " per day"); //set the text on the seekbar to match db
        seekBarMaxNotificationsPerDay.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekbarValueFromUser = progress;
                        if (progress != 20) {
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


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //business.saveusersetting()
        //UserSetting newUserSetting;
        //business.saveusersetting
        // ARTICLE READER MODE SWITCH
        if (swtchCounter%2 != 0) //checks if switch state is the same as when created
        {
            isDirtyLocal = true;
            articleReaderMode = (truth) ? 0:1;
            settings.setArticleReaderMode(!(truth));
            business.saveUserSettings(settings);
            //saveLocal(); (save the opposite of the original boolean value in the server which is = articleReaderMode)
        }
        //SEEKBAR FOR MAX NOTIFICATIONS
        settings.setMaxNotificaitons(seekBarMaxNotificationsPerDay.getProgress());
        if (seekbarValueFromUser != getMaxNotifications) //if original value from server is different from userseekbarvalue
        {
            isDirtyLocal = true;
            isDirtyServer = true;
            business.saveUserSettings(settings);
            //saveLocal(); (save the seekbarValueFromUser to the local and server)
            //saveServer(); make this later
        }
        //ARTICLE DISPLAY TYPE (RADIOGROUP)
        settings.setArticleDisplayType(radioGroup.getCheckedRadioButtonId());
        if (userRadioButtonValue != serverRadioButtonValue)
        {
            isDirtyLocal = true;
            business.saveUserSettings(settings);
            //saveLocal()  (save the userRadioButtonValue in the local db)
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}