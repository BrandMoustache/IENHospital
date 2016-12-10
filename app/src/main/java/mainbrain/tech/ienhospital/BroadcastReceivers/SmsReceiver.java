package mainbrain.tech.ienhospital.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by iammike on 18/07/16.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static String full_text_message=null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("receiver" , "A Message Received");
        /*Log.i("cs.fsu", "smsReceiver: SMS Received");

		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Log.i("cs.fsu", "smsReceiver : Reading Bundle");

			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

			//if (sms.getMessageBody().contains("FLAG")) {
				Intent myIntent = new Intent(context, Registration.class);
				myIntent.putExtra("mySMS", bundle);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(myIntent);
			//}
		}*/
        try {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    //str += "SMS from" + msgs[i].getOriginatingAddress();
                    //str += ":";
                    str += msgs[i].getMessageBody().toString();
                    //str += "\n";
                }

                //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

                if (str.contains("IEN")) {
                    full_text_message = str;
                }
            }
        }
        catch(Exception e)
        {
            Log.e("test" , e.toString()+"cgh");
        }
    }

    public String getMessage(){
        return full_text_message;
    }



}