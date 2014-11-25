package org.literacybridge.acm.mobile;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://literacybridge.iriscouch.com/acra-myapp/_design/acra-storage/_update/report",        
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="reporter",
        formUriBasicAuthPassword="reporter",
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.crash_toast_text
    )
public class MTBLApplication extends Application {

	 @Override
     public void onCreate() {
         super.onCreate();

         // The following line triggers the initialization of ACRA
         ACRA.init(this);
     }
	 
}
