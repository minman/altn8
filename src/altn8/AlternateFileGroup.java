package altn8;

import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateFileGroup implements Comparable<AlternateFileGroup> {
    private String baseFilename;
    private List<PsiFile> files;

    /**
     *
     */
    public AlternateFileGroup(String baseFilename) {
        this.baseFilename = baseFilename;
        this.files = new ArrayList<PsiFile>();
    }

    /**
     * BiseFilename of this Group
     */
    public String getBaseFilename() {
        return baseFilename;
    }

    /**
     * @return List with all files of this group
     */
    public List<PsiFile> getFiles() {
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlternateFileGroup that = (AlternateFileGroup) o;

        return baseFilename.equals(that.baseFilename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return baseFilename.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(AlternateFileGroup o) {
        return baseFilename.compareTo(o.baseFilename);
    }
}
