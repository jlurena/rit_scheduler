package me.jlurena.ritscheduler.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class Utils {

    public static final LocalDateTime now = LocalDateTime.now();
    public static final DateTimeFormatter STANDARD_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma");

    public static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    /**
     * Creates an AlertDialog.Builder with generic "Error" title and dismissable "OK" button.
     *
     * @param context Application context.
     * @param title Title of dialog.
     * @param errorMsg The error message.
     * @return The AlertDialog.Builder.
     */
    public static AlertDialog.Builder alertDialogFactory(@NonNull Context context, @StringRes int title, @Nullable String errorMsg) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(errorMsg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int pressedColor) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{pressedColor}), new ColorDrawable(pressedColor), null);
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int pressedColor, Drawable drawable) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{pressedColor}), drawable, null);
    }

    public static Intent emailIntent(String[] recipients, String subject) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("plain/text");
        return emailIntent;
    }

}
