package cn.mydreamy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import cn.mydreamy.utils.AndroidResId;
import me.everything.webp.WebPDecoder;


/**
 * A {@link android.widget.ImageView} with WebP support </p>
 * Use to load WebP resources to ImageView from View decleration in layout xmls </p>
 * <me.everything.webp.WebPImageView
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     webp:webp_src="@drawable/your_webp_image" />
 */
public class ImageView extends android.widget.ImageView {

    /**
     * Native WEBP support with lossless and transparency is officially supported in Android 4.2.1+.
     * Before that it was without transparency. Since we can only safely check for SDK version we
     * and 4.2 and 4.2.x are SDK version 17 will use SDK 18 (Android 4.3 and above).
     */
    public static final boolean NATIVE_WEB_P_SUPPORT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    private static final String TAG = "WebpImageView";

    public ImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        int webpSourceResourceID = getViewAttributeId(context, attrs, GetResId.getImageView(), GetResId.getImageSrcIndex());
//
//        int webpSourceBgResourceID = getViewAttributeId(context, attrs, GetResId.getView(), GetResId.getViewBackgroundIndex());

        int[] imageView_srcs = AndroidResId.getInstance().getAttributeResId(context, attrs, "ImageView_src");
        int webpSourceResourceID = imageView_srcs[0];
        int[] view_backgrounds = AndroidResId.getInstance().getAttributeResId(context, attrs, "View_background");
        int webpSourceBgResourceID = view_backgrounds[0];

        if(webpSourceResourceID != 0) {

            // Prefer the BitmapFactory decoder on post JB_MR2 OSs
            if (!NATIVE_WEB_P_SUPPORT) {
                InputStream inputStream = getResources().openRawResource(webpSourceResourceID);
                byte[] bytes = streamToBytes(inputStream);
                Bitmap bitmap = WebPDecoder.getInstance().decodeWebP(bytes);
                if (bitmap != null) {
                    this.setImageBitmap(bitmap);
                }
            } else {
                try {
                    this.setImageResource(webpSourceResourceID);
                } catch (Exception e) {
                    Log.e(TAG, "setImageResource", e);
                    InputStream inputStream = getResources().openRawResource(webpSourceResourceID);
                    byte[] bytes = streamToBytes(inputStream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap != null) {
                        this.setImageBitmap(bitmap);
                    }
                }
            }
        }

        if(webpSourceBgResourceID != 0) {

            // Prefer the BitmapFactory decoder on post JB_MR2 OSs
            if (!NATIVE_WEB_P_SUPPORT) {
                InputStream inputStream = getResources().openRawResource(webpSourceBgResourceID);
                byte[] bytes = streamToBytes(inputStream);
                Bitmap bitmap = WebPDecoder.getInstance().decodeWebP(bytes);
                if (bitmap != null) {
                    this.setBackground(new BitmapDrawable(getResources(), bitmap));
                }
            } else {
                try {
                    this.setBackgroundResource(webpSourceBgResourceID);
                } catch (Exception e) {
                    Log.e(TAG, "setBackgroundResource", e);
                    InputStream inputStream = getResources().openRawResource(webpSourceBgResourceID);
                    byte[] bytes = streamToBytes(inputStream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap != null) {
                        this.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }
                }
            }
        }
    }

    private int getViewAttributeId(Context context, AttributeSet attrs, int[] view, int viewBackgroundIndex) {
        final TypedArray a1 = context.obtainStyledAttributes(attrs, view);
        int webpSourceBgResourceID = a1.getResourceId(viewBackgroundIndex, 0);
        a1.recycle();
        return webpSourceBgResourceID;
    }

    private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        try {
            byte[] bytes = new byte[1024];
            while ((i = is.read(bytes)) != -1) {
                baos.write(bytes, 0, i);
                bytes = new byte[1024];
            }
            return baos.toByteArray();
        } catch (Exception e) {
        }
        return null;
    }
}