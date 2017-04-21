package cz.cvut.kbss.spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.cvut.kbss.spipes.model.AbstractEntity;
import cz.cvut.kbss.spipes.model.Vocabulary;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 12.01.17.
 */
@OWLClass(iri = Vocabulary.s_c_Modules)
public class Module extends AbstractEntity {

    @OWLDataProperty(iri = Vocabulary.s_p_label)
    private String label;
    @OWLObjectProperty(iri = Vocabulary.s_p_next, fetch = FetchType.EAGER)
    private Set<Module> next;

    public Module() {
    }

    public Module(String label, Set<Module> next) {
        this.id = UUID.randomUUID().toString();
        this.uri = URI.create(Vocabulary.s_c_Modules + "/" + id);
        this.label = label;
        this.next = next;
    }

    public Module(URI uri, String id, String label, Set<Module> next) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.next = next;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<Module> getNext() {
        return next;
    }

    public void setNext(Set<Module> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Module{" +
                "uri=" + uri +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", next=" + next +
                '}';
    }
}
