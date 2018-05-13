package com.codespurt.chatusingfirebase.chatModule.security;

import se.simbio.encryption.Encryption;

public class AES {

    private String key = "YourKey";
    private String salt = "YourSalt";
    private byte[] iv = {-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};
    private Encryption encryption = Encryption.getDefault(key, salt, iv);

    private boolean isEncryptionEnabled = true;

    public String encrypt(String unencryptedText, boolean overrideDefault) {
        if (overrideDefault) {
            return encryption.encryptOrNull(unencryptedText);
        }
        if (isEncryptionEnabled) {
            return encryption.encryptOrNull(unencryptedText);
        } else {
            return unencryptedText;
        }
    }

    public String decrypt(String encryptedText, boolean overrideDefault) {
        if (overrideDefault) {
            return encryption.decryptOrNull(encryptedText);
        }
        if (isEncryptionEnabled) {
            return encryption.decryptOrNull(encryptedText);
        } else {
            return encryptedText;
        }
    }

    public String isMessageEncrypted() {
        if (isEncryptionEnabled) {
            return "true";
        } else {
            return "false";
        }
    }
}
