package org.theider.plugin.templates;

/**
 *
 * @author Tim
 */
public class TemplateParseException extends Exception {

    public TemplateParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of
     * <code>TemplateParseException</code> without detail message.
     */
    public TemplateParseException() {
    }

    /**
     * Constructs an instance of
     * <code>TemplateParseException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TemplateParseException(String msg) {
        super(msg);
    }
}
