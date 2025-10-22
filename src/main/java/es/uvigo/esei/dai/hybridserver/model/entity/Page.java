package es.uvigo.esei.dai.hybridserver.model.entity;

import java.io.Serializable;
import java.util.Objects;

/** 
 * Class representing a web page with a unique identifier (UUID) and content.
 */
public class Page implements Serializable {
    
    private static final long serialVersionUID = 1L; // For serialization compatibility

    private String uuid;    // Unique identifier for the page
    private String content; // Content of the page
    private int version;    // Version of the page

    /**
     * Default constructor for Page.
     */
    public Page() {}

    /**
     * Constructs a Page with the specified UUID and content.
     * @param uuid the UUID of the page
     * @param content the content of the page
     */
    public Page(String uuid, String content) {
        this.uuid = uuid;
        this.content = content;
        this.version = 1;
    }

    public Page(String uuid, String content, int version) {
        this.uuid = uuid;
        this.content = content;
        this.version = version;
    }

    /**
     * Returns the UUID of the page.
     * @return the UUID of the page
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Sets the UUID of the page.
     * @param uuid the new UUID of the page
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the content of the page.
     * @return the content of the page
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Sets the content of the page.
     * @param content the new content of the page
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the version of the page
     * @return the version of the page
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * Sets the versio of the page
     * @param version the new version of the page
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid, content);
    }

    /**
     * Compares this object to the specified object. 
     * The result is true if and only if the argument is not null and 
     * is a Page object that has the same uuid and content as this object.
     * @param obj the object to compare this Page against
     * @return true if the given object represents a Page equivalent to this Page, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Page other = (Page) obj;
        return Objects.equals(uuid, other.uuid) && Objects.equals(content, other.content) && Objects.equals(version, other.version);
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Pages [uuid=" + uuid + ", version= " + version + ", content=" + content + "]";
    }
}
