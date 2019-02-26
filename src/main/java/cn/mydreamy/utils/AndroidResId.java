package cn.mydreamy.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AndroidResId {
    private static final String TAG = "GetResId";
    private Map<String, Integer> ids = new HashMap<>();
    private Class mStyleableClass = null;
    private int[] imageView;
    private int imageSrcIndex;
    private int[] view;
    private int viewBackgroundIndex;

    public static int[] getImageView() {
        return getInstance().imageView;
    }

    public static int getImageSrcIndex() {
        return getInstance().imageSrcIndex;
    }

    public static int[] getView() {
        return getInstance().view;
    }

    public static int getViewBackgroundIndex() {
        return getInstance().viewBackgroundIndex;
    }

    public static AndroidResId getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static AndroidResId INSTANCE = new AndroidResId();
    }

    private AndroidResId() {
        try {
            initStyleable();
            Field field = mStyleableClass.getDeclaredField("ImageView");
            field.setAccessible(true);
            imageView = (int[]) field.get(mStyleableClass);


            Field fieldSrc = mStyleableClass.getDeclaredField("ImageView_src");
            fieldSrc.setAccessible(true);
            imageSrcIndex = (int) fieldSrc.get(mStyleableClass);

            Field fieldView = mStyleableClass.getDeclaredField("View");
            fieldView.setAccessible(true);
            view = (int[]) fieldView.get(mStyleableClass);

            Field fieldBg = mStyleableClass.getDeclaredField("View_background");
            fieldBg.setAccessible(true);
            viewBackgroundIndex = (int) fieldBg.get(mStyleableClass);

            Log.d(TAG, "imageSrcIndex:" + imageSrcIndex + ",viewBackgroundIndex:" + viewBackgroundIndex);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private void initStyleable() {
        if (mStyleableClass == null) {
            try {
                mStyleableClass = Class.forName("com.android.internal.R$styleable");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] getViewAttributeResId(Context context, AttributeSet attrs, int[] view, int[] attributeIndexs) {
        if(view == null || attributeIndexs == null) {
            return null;
        }
        int length = attributeIndexs.length;
        int[] result = new int[length];
        final TypedArray a = context.obtainStyledAttributes(attrs, view);
        for (int i = 0; i< length; i++) {
            result[i] = a.getResourceId(attributeIndexs[i], 0);
        }
        a.recycle();
        return result;
    }

    private String checkAndSetWidgetName(String[] attrNames) {
        String widgetName = null;
        for(String attr:attrNames) {
            String[] widget = attr.split("_");
            if(widget.length > 0) {
                if(widgetName == null) {
                    widgetName = widget[0];
                } else {
                    if(!widgetName.equals(widget[0])) {
                        throw new IllegalArgumentException("attrNames must have same prefix!");
                    }
                }
            } else {
                throw new IllegalArgumentException("attrNames must like \"View_background\"");
            }
        }
        return widgetName;
    }

    public int[] getAttributeResId(Context context, AttributeSet attrs, String... attrNames) {
        if(attrNames == null) {
            return null;
        }
        String widgetName = checkAndSetWidgetName(attrNames);

        if(widgetName != null) {
            initStyleable();
            try {
                Field field = mStyleableClass.getDeclaredField(widgetName);
                field.setAccessible(true);
                int[] view = (int[]) field.get(mStyleableClass);
                int length = attrNames.length;
                int[] indexs = new int[length];
                for (int i = 0; i < length; i++) {
                    if(!ids.containsKey(attrNames[i])) {
                        Field fieldAttr = mStyleableClass.getDeclaredField(attrNames[i]);
                        fieldAttr.setAccessible(true);
                        indexs[i] = (int) fieldAttr.get(mStyleableClass);
                        ids.put(attrNames[i], indexs[i]);
                    } else {
                        indexs[i] = ids.get(attrNames[i]);
                    }
                }
                return getViewAttributeResId(context, attrs, view, indexs);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return new int[attrNames.length];
    }
}
