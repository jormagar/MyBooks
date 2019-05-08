package es.jormagar.myBooks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class GrayScaleTransformation implements Transformation {

    private final Picasso picasso;

    public GrayScaleTransformation(Picasso picasso) {
        this.picasso = picasso;
    }

    /**
     * Transform the source bitmap into a new bitmap. If you create a new bitmap instance, you must
     * call {@link Bitmap#recycle()} on {@code source}. You may return the original
     * if no transformation is required.
     *
     * @param source
     */
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap grayScaled = applyGrayscale(source);

        source.recycle();

        return grayScaled;
    }

    /**
     * Returns a unique key for the transformation, used for caching purposes. If the transformation
     * has parameters (e.g. size, scale factor, etc) then these should be part of the key.
     */
    @Override
    public String key() {
        return "grayscale";
    }

    private Bitmap applyGrayscale(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap dest = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);

        return dest;
    }
}
