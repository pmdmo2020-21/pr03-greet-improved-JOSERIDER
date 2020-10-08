package es.iessaladillo.pedrojoya.pr02_greetimproved.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class SoftInputUtils {

    private SoftInputUtils() {
    }

    public static boolean showSoftInput(@NonNull View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                    (Context.INPUT_METHOD_SERVICE));
            if (imm != null) {
                return imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
        return false;
    }

    public static boolean hideSoftKeyboard(@NonNull View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }

}