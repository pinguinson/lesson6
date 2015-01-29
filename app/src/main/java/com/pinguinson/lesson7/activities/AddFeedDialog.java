package com.pinguinson.lesson7.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pinguinson.lesson7.R;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class AddFeedDialog extends DialogFragment {
    private OnCompleteListener onCompleteListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onCompleteListener = (OnCompleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View modifiedView = inflater.inflate(R.layout.add_feed_dialog, null);
        final EditText feedName = (EditText) modifiedView.findViewById(R.id.add_name);
        final EditText feedURL = (EditText) modifiedView.findViewById(R.id.add_url);


        builder.setView(modifiedView)
                .setTitle("Add new RSS feed")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = feedName.getText().toString();
                        String url = feedURL.getText().toString();
                        if (name.trim().equals("")) {
                            Toast.makeText(getActivity().getBaseContext(), "Please enter feed title", Toast.LENGTH_SHORT).show();
                        } else if (url.trim().equals("")) {
                            Toast.makeText(getActivity().getBaseContext(), "Please enter feed URL", Toast.LENGTH_SHORT).show();
                        } else {
                            onCompleteListener.onComplete(name, url);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }

                );
        return builder.create();
    }


    public static interface OnCompleteListener {
        public abstract void onComplete(String name, String url);
    }
}