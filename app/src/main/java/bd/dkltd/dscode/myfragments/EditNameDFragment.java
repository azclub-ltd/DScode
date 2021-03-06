package bd.dkltd.dscode.myfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import bd.dkltd.dscode.R;
import java.io.File;
import java.io.IOException;

public class EditNameDFragment extends DialogFragment {

    private EditText edt;
    private String dirPath;
    private boolean createFile,createFolder;
    private OnSuccessListener onSuccessListener;

    public static final boolean CREATE_SUCCESS = true;
    public static final boolean CREATE_FAILED = false;

    //empty constructor
    public EditNameDFragment() {}

    public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
    }

    public void setForFileCreation() {
        this.createFolder = false;
        this.createFile = true;
    }

    public void setForFolderCreation() {
        this.createFile = false;
        this.createFolder = true;
    }

    public void checkExistance(String dirPath) {
        this.dirPath = dirPath;
    }
    //empty instance
    public static EditNameDFragment newInstance() {
        return new EditNameDFragment();
    }
    //newInstance with title
    public static EditNameDFragment newInstance(String title, String positiveBtnText, String negativeBtnText, String editTextValue) {
        EditNameDFragment endf = new EditNameDFragment();
        Bundle args = new Bundle();
        args.putString("dialog-title", title);
        args.putString("dialog-positive-text", positiveBtnText);
        args.putString("dialog-negative-text", negativeBtnText);
        args.putString("dialog-editext-value", editTextValue);
        endf.setArguments(args);
        return endf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String dialogTitle = getArguments().getString("dialog-title", "Choose Name");
        String dialogPositiveText = getArguments().getString("dialog-positive-text", "ok");
        String dialogNegativeText = getArguments().getString("dialog-negative-text", "cancel");
        String inputEditTextValue = getArguments().getString("dialog-editext-value", "");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_one_et_dialog, null);
        edt = view.findViewById(R.id.createFileNameId);
        edt.setText(inputEditTextValue);
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        adBuilder.setTitle(dialogTitle);
        adBuilder.setView(view);
        adBuilder.setPositiveButton(dialogPositiveText, null);
        adBuilder.setNegativeButton(dialogNegativeText, null);
        return adBuilder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog ad = (AlertDialog) getDialog();
        String logs1 = "";
        if (ad != null) {
            //get button and editText
            Button positiveBtn = ad.getButton(Dialog.BUTTON_POSITIVE);
            final EditText localEdt = edt;
            positiveBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        String strValue = localEdt.getText().toString();
                        String logs = "";
                        //start validation
                        if (strValue.isEmpty()) {
                            localEdt.setError("Empty Field");
                            localEdt.requestFocus();
                        } else if (dirPath != null) {
                            File newFile = new File(dirPath + "/" + strValue);
                            boolean isExist = newFile.exists();
                            if (!isExist) {
                                //we can create file or folder
                                if (createFile) {
                                    //we have to create file
                                    try {
                                        boolean created = newFile.createNewFile();
                                        if (created) {
                                            logs += "File " + strValue + " has been created\n";
                                            ad.dismiss();
                                        } else {
                                            logs += "File " + strValue + " has not been created\n";
                                        }
                                        try {
                                            onSuccessListener.onSuccess(created, newFile);
                                        } catch (NullPointerException e) {
                                            logs += "Listner is not set\n";
                                        }
                                    } catch (IOException e) {
                                        logs += "Error " + strValue + "\n";
                                    }
                                } else if (createFolder) {
                                    //we have to create folder
                                    boolean created = newFile.mkdir();
                                    if (created) {
                                        logs += "Folder " + strValue + " has been created \n";
                                        ad.dismiss();
                                    } else {
                                        logs += "Some error occured\n";
                                    }
                                    try {
                                        onSuccessListener.onSuccess(created, newFile);
                                    } catch (NullPointerException e) {
                                        logs += "Listner is not set for folder\n";
                                    }
                                }
                            } else {
                                //we can't create file or folder cz already exists
                                localEdt.setError("A File or Folder with the same name already exists");
                                localEdt.requestFocus();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Can't create anything here", Toast.LENGTH_SHORT).show();
                            ad.dismiss();
                        }
                        //Toast.makeText(getActivity(), logs, Toast.LENGTH_LONG).show();
                    }
                });

        } else {
            logs1 += "Dialog is null";
        }
    }
    public interface OnSuccessListener {
        void onSuccess(boolean result, File createdFile);
    }
}
