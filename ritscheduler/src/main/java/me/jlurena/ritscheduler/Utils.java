package me.jlurena.ritscheduler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

public class Utils {

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
     * @param errorMsg The error message.
     * @return The AlertDialog.Builder.
     */
    public static AlertDialog.Builder errorDialogFactory(@NonNull Context context, @Nullable String errorMsg) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(errorMsg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int normalColor, @ColorInt int pressedColor) {
        return new RippleDrawable(getPressedColorSelector(pressedColor), getColorDrawableFromColor(normalColor), null);
    }

    public static RippleDrawable getPressedColorRippleDrawable(@ColorInt int normalColor, @ColorInt int pressedColor, Drawable drawable) {
        return new RippleDrawable(getPressedColorSelector(pressedColor), drawable, null);
    }


    public static ColorStateList getPressedColorSelector(int pressedColor) {
        return new ColorStateList(new int[][]{new int[]{}}, new int[]{pressedColor});
    }

    public static ColorDrawable getColorDrawableFromColor(int color) {
        return new ColorDrawable(color);
    }
}
