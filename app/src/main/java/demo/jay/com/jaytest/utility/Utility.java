package demo.jay.com.jaytest.utility;

import android.widget.EditText;
/**
 * Created by jay on 03-Jan-18.
 */

public class Utility
{
    public static boolean isBlankField(EditText etPersonData)
    {
        return etPersonData.getText().toString().trim().equals("");
    }
}
