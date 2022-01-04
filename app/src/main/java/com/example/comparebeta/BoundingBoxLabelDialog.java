package com.example.comparebeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.comparebeta.Utils.Constants;

/**
 * This class defines the properties if the dialog, which receives the label name of the bounding
 * boxes.
 *
 * @author Nisal Hemadasa
 */
public class BoundingBoxLabelDialog extends AppCompatDialogFragment {
    private EditText editTextBoundingBoxLabelName;
    private BoundingBoxLabelDialogListener boundingBoxLabelDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bounding_box_label, null);

        builder.setView(view)
                .setTitle(Constants.ENTER_OBJECT_CLASS_NAME)
                .setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String boundingBoxLabel = editTextBoundingBoxLabelName.getText().toString();
                        boundingBoxLabelDialogListener.applyTexts(boundingBoxLabel);
                    }
                });

        editTextBoundingBoxLabelName = view.findViewById(R.id.bounding_box_label_name);
        if(getArguments() != null && getArguments().containsKey(Constants.CURRENT_LABEL)){
            // Display the label if the box already has been labelled before.
            editTextBoundingBoxLabelName.setText(getArguments().getString(Constants.CURRENT_LABEL));
        }

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        editTextBoundingBoxLabelName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Disable OK button for empty strings
                if (s.toString().length() == 0){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            boundingBoxLabelDialogListener = (BoundingBoxLabelDialogListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context,
                    context.toString() + Constants.LABEL_DIALOG_LISTENER_NOT_IMPLEMENTED,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public interface BoundingBoxLabelDialogListener{
        void applyTexts(String boundingBoxLabel);
    }
}
