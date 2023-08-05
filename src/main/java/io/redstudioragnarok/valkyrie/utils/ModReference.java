package io.redstudioragnarok.valkyrie.utils;

import io.redstudioragnarok.redcore.logging.RedLogger;
import io.redstudioragnarok.valkyrie.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class defines constants for Valkyrie.
 * <p>
 * They are automatically updated by RFG on compile time, except for the name as Gradle would remove spaces.
 */
public final class ModReference {

    private static final String[] VALKYRIE_RECOMFORT_MESSAGES = new String[]{
            "Exception encountered! Fear not, for Valkyries fear no code.",
            "Forge ahead! A Valkyrie's mettle is tested by challenges.",
            "Patience, as Valkyries know, leads to triumph in the end.",
            "A glitch in the matrix? Valkyries are skilled in rewriting fate.",
            "Hold your ground! Valkyries embrace challenges as opportunities.",
            "Remember, even Valkyries stumble before they learn to fly gracefully."
    };

    public static final String ID = Tags.ID;
    public static final String NAME = "Valkyrie";
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOG = LogManager.getLogger(NAME);
    public static final RedLogger RED_LOG = new RedLogger(NAME, "https://linkify.cz/ValkyrieBugReport", LOG, VALKYRIE_RECOMFORT_MESSAGES);
}
