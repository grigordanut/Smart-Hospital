package com.example.smarthospitals.record;

import android.nfc.NdefRecord;

import com.google.android.gms.common.internal.Preconditions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

 //NFC Text Record

public class TextRecord implements ParsedNdefRecord {

    private final String mLanguageCode;

    private final String mText;

    public TextRecord(String languageCode, String text) {
        mLanguageCode = Preconditions.checkNotNull(languageCode);
        mText = Preconditions.checkNotNull(text);
    }

    public String str() {
        return mText;
    }

    public String getText() {
        return mText;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }

    // TODO: deal with text fields which span multiple NdefRecords
    public static TextRecord parse(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        try {
            byte[] payload = record.getPayload();

            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            String languageCode = new String(payload, 1, languageCodeLength, StandardCharsets.US_ASCII);
            String text =
                    new String(payload, languageCodeLength + 1,
                            payload.length - languageCodeLength - 1, textEncoding);
            return new TextRecord(languageCode, text);
        } catch (UnsupportedEncodingException e) {
            // should never happen unless we get a malformed tag.
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
