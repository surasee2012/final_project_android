package kmitl.project.surasee2012.eatrightnow.model;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Gun on 11/5/2017.
 */

public class Message {

    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void alert(Context context, String warning) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(warning);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ตกลง",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
