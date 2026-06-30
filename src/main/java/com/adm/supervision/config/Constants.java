package com.adm.supervision.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    // Default password assigned to accounts created by an administrator, a locataire (manager boutique) or a manager boutique (vendeur).
    // The user must change it on first login (see User.mustChangePassword).
    public static final String MOT_DE_PASSE_PAR_DEFAUT = "Adm@2026";

    private Constants() {}
}
