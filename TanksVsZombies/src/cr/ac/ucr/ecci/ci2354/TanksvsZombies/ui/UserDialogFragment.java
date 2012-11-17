package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class UserDialogFragment extends DialogFragment {

	private WeakReference<GameOverActivity> weakReference;

	public static UserDialogFragment newInstance(GameOverActivity activity) {
		UserDialogFragment f = new UserDialogFragment();
		f.attach(activity);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCancelable(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.dialog_fragment_user, container);
		Button b = (Button) v.findViewById(R.id.dialog_fragment_button);
		EditText e = (EditText) v.findViewById(R.id.dialog_fragment_username);
		b.setTag(e);
		
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				EditText e = (EditText) v.getTag();
				if (!e.getEditableText().toString().isEmpty()) {
					weakReference.get().userNameChoosed(e.getEditableText().toString());
					dismiss();
				}
			}
		});
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.getDialog().setTitle("Digite su nombre");
	}
	
	public void attach(GameOverActivity activity) {
		weakReference = new WeakReference<GameOverActivity>(activity);
	}

	public void dettach() {
		weakReference = null;
	}
}
