package cz.cvut.kbss.spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.spipes.model.AbstractEntity;

import java.net.URI;
import java.util.UUID;

import static cz.cvut.kbss.spipes.model.Vocabulary.*;

/**
 * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 18.01.17.
 */
@OWLClass(iri = s_c_context)
public class Context extends AbstractEntity {

    @OWLDataProperty(iri = s_p_label)
    private String label;
    @OWLDataProperty(iri = s_p_comment)
    private String comment;
    @OWLDataProperty(iri = s_p_has_content_hash)
    private String contentHash;

    public Context() {
    }

    public Context(String label, String comment) {
        this.id = UUID.randomUUID().toString();
        this.uri = URI.create(s_c_context + "/" + id);
        this.label = label;
        this.comment = comment;
    }

    public Context(URI uri, String id, String label, String comment) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.comment = comment;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
