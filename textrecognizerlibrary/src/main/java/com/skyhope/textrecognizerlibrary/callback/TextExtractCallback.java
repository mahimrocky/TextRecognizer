package com.skyhope.textrecognizerlibrary.callback;

import java.util.List;
/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 12/10/2018 at 11:47 AM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 12/10/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public interface TextExtractCallback {
    void onGetExtractText(List<String> textList);
}
