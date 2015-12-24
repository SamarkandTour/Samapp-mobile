package uz.samtuit.samapp.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.samtuit.samapp.main.R;

public class AddToMyItineraryDayPicker extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DialogListener mListener;

    public AddToMyItineraryDayPicker() {
        // Required empty public constructor
    }

    public static AddToMyItineraryDayPicker newInstance(String param1, String param2) {
        AddToMyItineraryDayPicker fragment = new AddToMyItineraryDayPicker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please choose day")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



        return builder.create();
    }


    public interface DialogListener {
        // TODO: Update argument type and name
        public void OnPositiveButtonClicker(DialogFragment dialog);
        public void OnNegativeButtonClicker(DialogFragment dialog);
    }
}
