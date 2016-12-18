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
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;


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

    TextView txtsettings, txtMaxNotification, txtMaxNotificationDescription, txtNotificationsPerDay, txtDownloadArticleImages, txtArticleReaderMode, txtArticleReaderModeDescription,
            txtAccountInformation, txtNetworkStatus, txtNetworkStatusGreen, txtAbout, txtVersion, txtVersionNumber;
    Button btnSendTestNotification, btnPrivacy, btnTerms, btnRateOnAppStore, btnSendFeedback;
    RadioButton RadioBtnNever, RadioBtnWifiOnly, RadioBtnAlways;
    Typeface openSansRegular, openSansLight;
    SeekBar seekBarMaxNotificationsPerDay;
    TextView txtSeekBarValue;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

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

        seekBarMaxNotificationsPerDay.setProgress(0);
        seekBarMaxNotificationsPerDay.setMax(20);
        txtSeekBarValue.setText(seekBarMaxNotificationsPerDay.getProgress() + " per day");
        seekBarMaxNotificationsPerDay.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
