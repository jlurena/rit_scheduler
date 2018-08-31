package me.jlurena.ritscheduler.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import me.jlurena.ritscheduler.R;

public class Utils {

    public static final LocalDateTime now = LocalDateTime.now();
    public static final DateTimeFormatter STANDARD_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma");

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
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
    }

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

    public static int dpToPixel(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void genericAlertDialogError(Context context, Exception exc) {
        String body = "Phone: " + Build.MODEL +
                "\nBuild Version: " + Build.VERSION.SDK_INT;
        if (exc != null) {
            body += "\nError: " + exc.getMessage() +
                    " \nError Log:\n\n\n\t\t" + TextUtils.join("\n\t\t", exc.getStackTrace());
        }
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"eljean@live.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "RIT Scheduler Error Report");
        intent.putExtra(Intent.EXTRA_TEXT, body);

        new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(R.string.generic_error)
                .setPositiveButton(R.string.close, (d, which) -> d.dismiss())
                .setNeutralButton("Report", (d, which) -> {
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        try {
                            intent.setAction(Intent.ACTION_SENDTO);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            context.startActivity(Intent.createChooser(intent, "Send Report Using:"));
                        }
                    }
                })
                .show();
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int pressedColor) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{pressedColor}), new ColorDrawable(pressedColor), null);
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int pressedColor, Drawable drawable) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{pressedColor}), drawable, null);
    }

}
