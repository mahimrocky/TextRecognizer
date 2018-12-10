package com.skyhope.textrecognizerlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.skyhope.textrecognizerlibrary.callback.TextExtractCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 12/10/2018 at 11:19 AM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 12/10/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class TextScanner {

    private static TextScanner sTextScanner;
    private Context mContext;
    private TextRecognizer mTextRecognizer;
    private Bitmap mBitmap;
    private TextExtractCallback mCallback;

    public static TextScanner getInstance(Context context) {
        if (sTextScanner == null) {
            sTextScanner = new TextScanner(context);
        }
        return sTextScanner;
    }

    //Constructor

    private TextScanner(Context context) {
        this.mContext = context;
    }

    /**
     * Initialize the text recognizer
     */

    public TextScanner init() {
        mTextRecognizer = new TextRecognizer.Builder(mContext).build();
        return sTextScanner;
    }

    public TextScanner load(Bitmap bitmap) {
        mBitmap = bitmap;
        read(mBitmap);
        return sTextScanner;
    }

    public TextScanner load(Uri uri) {
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            read(mBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sTextScanner;
    }

    public void getCallback(TextExtractCallback callback) {
        this.mCallback = callback;
    }

    private void read(Bitmap bitmap) {
        if (isInitialized()) {
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmap)
                    .build();

            final SparseArray<TextBlock> textBlocks = mTextRecognizer.detect(imageFrame);

            sortTextBlock(textBlocks);
        }
    }

    private void sortTextBlock(SparseArray<TextBlock> textBlocks) {
        List<TextBlock> myTextBlock = new ArrayList<>();
        for (int i = 0; i < textBlocks.size(); i++) {
            myTextBlock.add(textBlocks.valueAt(i));
        }

        Collections.sort(myTextBlock, new Comparator<TextBlock>() {
            @Override
            public int compare(TextBlock textBlock1, TextBlock textBlock2) {
                return textBlock1.getBoundingBox().top - textBlock2.getBoundingBox().top;
            }
        });

        parseText(myTextBlock);
    }

    private void parseText(List<TextBlock> myTextBlock) {
        List<String> textList = new ArrayList<>();
        for (int i = 0; i < myTextBlock.size(); i++) {
            TextBlock textBlock = myTextBlock.get(i);
            List<Line> lines = (List<Line>) textBlock.getComponents();

            for (Line line : lines) {
                textList.add(line.getValue());
            }
        }
        if (mCallback != null) {
            mCallback.onGetExtractText(textList);
        }
    }


    private boolean isInitialized() {
        if (!mTextRecognizer.isOperational()) {

            Log.d("ScannerTest", "Detector dependencies are not yet available.");

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = mContext.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(mContext, "Low Storage", Toast.LENGTH_LONG).show();
                // Log.w(TAG, "Low Storage");
            }
        }

        return mTextRecognizer.isOperational();
    }
}
