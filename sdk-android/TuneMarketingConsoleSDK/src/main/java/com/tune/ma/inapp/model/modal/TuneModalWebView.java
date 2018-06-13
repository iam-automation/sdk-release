package com.tune.ma.inapp.model.modal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by johng on 4/10/17.
 * @deprecated IAM functionality. This method will be removed in Tune Android SDK v6.0.0
 */
@Deprecated
public class TuneModalWebView extends WebView {
    protected Context context;
    protected TuneModal modal;

    @SuppressLint("SetJavaScriptEnabled")
    protected TuneModalWebView(Context context, TuneModal parentModal) {
        super(context);
        this.context = context;
        this.modal = parentModal;
        this.setFocusable(true);

        this.setBackgroundColor(Color.TRANSPARENT);
        // Turn off hardware acceleration when possible, it causes WebView loading issues
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
        this.setVisibility(View.INVISIBLE);
        this.setBackgroundColor(Color.TRANSPARENT);
        // Not default before API level 11
        this.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        WebSettings settings = this.getSettings();

        /**
         * setJavaScriptEnabled(true) allows JavaScript to be run in the WebView. (This is necessary for IMv2.)
         *
         * While this technically can allow cross-site scripting and would be a vulnerability risk in the wild,
         * this WebView will only be used by TUNE's customers for HTML/JS they serve to their app via TUNE's dashboard.
         * It is a relatively low-risk usage in practice as it's customer-controlled content and part of a protected constructor, so suppressing warn.
         */

        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Send a message dismissed event in background thread
                    modal.processDismiss();
                }
            }).run();

            // Dismiss the modal if back button is pressed
            modal.dismiss();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
