package me.bonoj.moviebug.utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseUtils {

    private static final String DELIMITER = "<#delim-com.hk47.popularmoviesstageone>";

    public static String packArrayList(ArrayList<String[]> arrayList) {

        if (arrayList == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        Object[] objectsArray = arrayList.toArray();

        String [] stringArray;
        for(int i = 0; i < objectsArray.length; i++) {
            stringArray = (String[]) objectsArray[i];

            builder.append(Arrays.toString(stringArray));
            if (i != objectsArray.length - 1) {
                builder.append(DELIMITER);
            }
        }
        return builder.toString();
    }

    public static ArrayList<String[]> unpackArrayList(String pack) {

        ArrayList<String[]> unpackedArrayList = new ArrayList<String[]>();

        String[] stringArray = pack.split(DELIMITER);

        for (String stringArrayString : stringArray) {
            String trimmedStringArrayString = stringArrayString.substring(1,stringArrayString.length() - 1);
            String[] urlNamePair = trimmedStringArrayString.split(",");
            unpackedArrayList.add(urlNamePair);
        }
        return unpackedArrayList;
    }
}
