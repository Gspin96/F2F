package today.challengerproject.face2face.f2f;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

class HelperMethods {

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
