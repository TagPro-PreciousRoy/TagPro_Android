package com.koalabeast.tagpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class NetworkErrorDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		return builder.setTitle(R.string.network_error_title)
				.setMessage(R.string.network_error_description)
				.setPositiveButton(R.string.network_error_button, null).create();

	}

}
