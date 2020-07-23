package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class BitmapManager {

    private static String TAG = BitmapManager.class.getSimpleName();

    public enum STAMP_POSITION {LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM, TOP_CENTER, RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM, BOTTOM_CENTER, CENTER}

    public enum TEXT_POSITION {CENTER, TOP, BOTTOM}

    public static Bitmap cloneBitmap(Bitmap bitmap) {
        return bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
    }

    public static <T> Bitmap createBitmap(Context context, T imageSource) {
        Bitmap bitmap = null;
        if (imageSource instanceof String) {
            if (!TextUtils.isEmpty((String) imageSource)) {
                File file = new File(imageSource.toString());
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } else if (imageSource instanceof Integer) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), (Integer) imageSource);
        } else if (imageSource instanceof View) {
            View view = (View) imageSource;
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            view.draw(c);
        }
        return bitmap;
    }

    public static Bitmap setWaterMark(Context context, Bitmap bitmap, String text) {
        WatermarkText watermarkText = new WatermarkText(text)
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setTextColor(Color.WHITE)
                .setTextFont(R.font.champagne)
                .setTextShadow(0.1f, 5, 5, Color.BLUE)
                .setTextAlpha(90)
                .setRotation(50)
                .setTextSize(20);

        return WatermarkBuilder
                .create(context, bitmap)
                .loadWatermarkText(watermarkText)
                .setTileMode(true) // select different drawing mode.
                .getWatermark()
                .getOutputImage();
    }

    public static void saveBitmap(Context context, Bitmap bitmap) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy, EEEE, hh:mm:ss a");
        String dateTime = simpleDateFormat.format(now);

        // Create folder if not exist
        String rootPath = Environment.getExternalStorageDirectory() + File.separator + "PostCreator";
        File folder = new File(rootPath);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // image naming and path  to include sd card  appending name you choose for file
        String filePath = rootPath + File.separator + "PostCreator_" + dateTime + ".jpg";

        File imagePath = new File(filePath);
        FileOutputStream fos;
        if (bitmap != null) {
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap addStamp(Context gContext, Bitmap bitmap, String gText, STAMP_POSITION stampPosition) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(resources.getColor(android.R.color.white));
        paint.setTypeface(Typeface.createFromAsset(gContext.getAssets(), "fonts/mathilde.otf"));
        paint.setTextSize((30 * scale));
        paint.setShadowLayer(2f, 0f, 2f, resources.getColor(android.R.color.black));
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 0, y = 0;
        switch (stampPosition) {
            case CENTER:
                x = (bitmap.getWidth() - bounds.width()) / 2;
                y = (bitmap.getHeight() + bounds.height()) / 2;
                break;
            case RIGHT_BOTTOM:
                x = (bitmap.getWidth() - bounds.width() - 10);
                y = (bitmap.getHeight() - (bounds.height() / 2) - 10);
                break;
            case LEFT_BOTTOM:
                x = 10;
                y = (bitmap.getHeight() - (bounds.height() / 2) - 10);
                break;
            case BOTTOM_CENTER:
                x = (bitmap.getWidth() - bounds.width()) / 2;
                y = (bitmap.getHeight() - (bounds.height() / 2) - 10);
                break;
            case TOP_CENTER:
                x = (bitmap.getWidth() - bounds.width()) / 2;
                y = 30;
                break;
            case LEFT_TOP:
                x = 10;
                y = 30;
                break;
            case LEFT_CENTER:
                x = 10;
                y = (bitmap.getHeight() + bounds.height()) / 2;
                break;
            case RIGHT_TOP:
                x = (bitmap.getWidth() - bounds.width() - 10);
                y = 30;
                break;
            case RIGHT_CENTER:
                x = (bitmap.getWidth() - bounds.width() - 10);
                y = (bitmap.getHeight() + bounds.height()) / 2;
                break;
        }
        canvas.drawText(gText, x, y, paint);
        return bitmap;
    }

    public static Bitmap addText(Context context, Bitmap bitmap, String text, TEXT_POSITION textPosition) {
        // prepare canvas
        float scale = context.getResources().getDisplayMetrics().density;
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(255, 255, 255));
        // text size in pixels
        paint.setTextSize((40 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (15 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(text, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.5f, 1.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        int x = 0, y = 0;
        switch (textPosition) {
            case CENTER:
                x = (bitmap.getWidth() - textWidth) / 2;
                y = (bitmap.getHeight() - textHeight) / 2;
                break;
            case TOP:
                break;
            case BOTTOM:
                break;
        }

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public static Bitmap addFrame(Bitmap originalBitmap) {
        Canvas canvas = new Canvas(originalBitmap);
        Paint framePaint = new Paint();
        for (int i = 1; i < 5; i++) {
            setFramePaint(framePaint, i, originalBitmap.getWidth(), originalBitmap.getHeight());
            canvas.drawPaint(framePaint);
        }
        return originalBitmap;
    }

    private static void setFramePaint(Paint p, int side, float imageWidth, float imageHeight) {
        // paint, side of rect, image width, image height

        p.setShader(null);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        float borderSize = 0.1f; //relative size of border
        //use the smaller image size to calculate the actual border size
        float bSize = (imageWidth > imageHeight) ? imageHeight * borderSize : imageHeight * borderSize;
        float g1x = 0;
        float g1y = 0;
        float g2x = 0;
        float g2y = 0;
        int c1 = 0, c2 = 0;

        if (side == 1) {
            //left
            g1x = 0;
            g1y = imageHeight / 2;
            g2x = bSize;
            g2y = imageHeight / 2;
            c1 = Color.TRANSPARENT;
            c2 = Color.BLACK;

        } else if (side == 2) {
            //top
            g1x = imageWidth / 2;
            g1y = 0;
            g2x = imageWidth / 2;
            g2y = bSize;
            c1 = Color.TRANSPARENT;
            c2 = Color.BLACK;


        } else if (side == 3) {
            //right
            g1x = imageWidth;
            g1y = imageHeight / 2;
            g2x = imageWidth - bSize;
            g2y = imageHeight / 2;
            c1 = Color.TRANSPARENT;
            c2 = Color.BLACK;


        } else if (side == 4) {
            //bottom
            g1x = imageWidth / 2;
            g1y = imageHeight;
            g2x = imageWidth / 2;
            g2y = imageHeight - bSize;
            c1 = Color.TRANSPARENT;
            c2 = Color.BLACK;
        }

        p.setShader(new LinearGradient(g1x, g1y, g2x, g2y, c1, c2, Shader.TileMode.CLAMP));
    }

    public static Bitmap addShadow(final Bitmap bm, final int dstHeight, final int dstWidth, int color, int size, float dx, float dy) {
        final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);

        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0, 0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();
        return ret;
    }

    public static Bitmap addLinearGradient(Bitmap src, int gradientHeight) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap overlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, h - gradientHeight, 0, h, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, h - gradientHeight, w, h, paint);

        return overlay;
    }

    public static Bitmap addShade(Bitmap src, int alphaColor) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap overlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(alphaColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, 0, 0, paint);

        return overlay;
    }

    /**
     * A one color image.
     * @param width
     * @param height
     * @param color
     * @return A one color image with the given width and height.
     */
    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }
}