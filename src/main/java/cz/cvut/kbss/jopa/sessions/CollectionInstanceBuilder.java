/**
 * Copyright (C) 2016 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.jopa.sessions;

import cz.cvut.kbss.jopa.adapters.IndirectCollection;
import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.model.annotations.Types;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.PluralAttribute;
import cz.cvut.kbss.jopa.utils.CollectionFactory;
import cz.cvut.kbss.jopa.utils.EntityPropertiesUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

/**
 * Special class for cloning collections. Introduced because some Java collection have no no-argument constructor and
 * thus they must be cloned specially. NOTE: This class may be removed in case a better cloning mechanisms (namely
 * database mappings and copy policies) is introduced.
 */
class CollectionInstanceBuilder extends AbstractInstanceBuilder {

    private static final Class<?> singletonListClass = Collections.singletonList(null).getClass();
    private static final Class<?> singletonSetClass = Collections.singleton(null).getClass();
    private static final Class<?> arrayAsListClass = Arrays.asList(new Object()).getClass();

    CollectionInstanceBuilder(CloneBuilderImpl builder, UnitOfWork uow) {
        super(builder, uow);
        this.populates = true;
    }

    /**
     * This method is the entry point for cloning the Java collections. It clones standard collections as well as
     * immutable collections and singleton collections. </p>
     * <p>
     * Currently supported are List and Set.
     *
     * @param collection The collection to clone
     * @return A deep clone of the specified collection
     */
    @Override
    Object buildClone(Object cloneOwner, Field field, Object collection, Descriptor repository)
            throws OWLPersistenceException {
        assert (collection instanceof Collection);
        Collection<?> container = (Collection<?>) collection;
        if (container instanceof IndirectCollection<?>) {
            container = (Collection<?>) ((IndirectCollection<?>) container)
                    .getReferencedCollection();
        }
        if (container instanceof Set) {
            container = new HashSet<>(container);
        } else if (container instanceof List) {
            container = new ArrayList<>(container);
        }

        Collection<?> clone = cloneUsingDefaultConstructor(cloneOwner, field, container, repository);
        if (clone == null) {
            if (Collections.EMPTY_LIST == container) {
                return Collections.EMPTY_LIST;
            }
            if (Collections.EMPTY_SET == container) {
                return Collections.EMPTY_SET;
            }
            Constructor<?> c;
            Object element = container.iterator().next();
            Object[] params = new Object[1];
            if (!CloneBuilderImpl.isImmutable(element.getClass())) {
                element = builder.buildClone(element, repository);
                if (element instanceof Collection || element instanceof Map) {
                    element = builder.createIndirectCollection(element, cloneOwner, field);
                }
            }
            params[0] = element;
            if (singletonListClass.isInstance(container)) {
                c = getFirstDeclaredConstructorFor(singletonListClass);
            } else if (singletonSetClass.isInstance(container)) {
                c = getFirstDeclaredConstructorFor(singletonSetClass);
            } else if (arrayAsListClass.isInstance(container)) {
                final List arrayList = new ArrayList<>(container.size());
                cloneCollectionContent(cloneOwner, field, container, arrayList, repository);
                c = getFirstDeclaredConstructorFor(ArrayList.class);
                params[0] = arrayList;
            } else {
                throw new OWLPersistenceException("Encountered unsupported type of collection: "
                        + container.getClass());
            }
            try {
                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                clone = (Collection<?>) c.newInstance(params);
            } catch (InstantiationException | IllegalArgumentException | InvocationTargetException e) {
                throw new OWLPersistenceException(e);
            } catch (IllegalAccessException e) {
                logConstructorAccessException(c, e);
                try {
                    clone = (Collection<?>) AccessController
                            .doPrivileged(new PrivilegedInstanceCreator(c));
                } catch (PrivilegedActionException ex) {
                    throw new OWLPersistenceException(ex);
                }
            }
        }
        clone = (Collection<?>) builder.createIndirectCollection(clone, cloneOwner, field);
        return clone;
    }

    /**
     * Clones the specified collection using its default zero argument constructor. If the specified collection has none
     * (e. g. like SingletonList), this method returns null.
     *
     * @param container The collection to clone.
     * @return cloned collection
     */
    private Collection<?> cloneUsingDefaultConstructor(Object cloneOwner, Field field,
                                                       Collection<?> container, Descriptor repository) {
        Class<?> javaClass = container.getClass();
        Collection<?> result = createNewInstance(javaClass, container.size());
        if (result != null) {
            // Makes shallow copy
            cloneCollectionContent(cloneOwner, field, container, result, repository);
        }
        return result;
    }

    private Collection<?> createNewInstance(Class<?> type, int size) {
        Object[] params = null;
        Class<?>[] types = {int.class};
        // Look for constructor taking initial size as parameter
        Constructor<?> ctor = getDeclaredConstructorFor(type, types);
        if (ctor != null) {
            params = new Object[1];
            params[0] = size;
        } else {
            ctor = DefaultInstanceBuilder.getDeclaredConstructorFor(type, null);
        }
        if (ctor == null) {
            return null;
        }
        Collection<?> result = null;
        try {
            result = (Collection<?>) ctor.newInstance(params);
        } catch (InstantiationException | InvocationTargetException | IllegalArgumentException e) {
            throw new OWLPersistenceException(e);
        } catch (IllegalAccessException e) {
            logConstructorAccessException(ctor, e);
            try {
                result = (Collection<?>) AccessController
                        .doPrivileged(new PrivilegedInstanceCreator(ctor));
            } catch (PrivilegedActionException ex) {
                logPrivilegedConstructorAccessException(ctor, ex);
                // Do nothing
            }
        }
        return result;
    }

    /**
     * Clone all the elements in the collection. This will make sure that the cloning process creates a deep copy.
     *
     * @param source The collection to clone.
     */
    @SuppressWarnings(value = "unchecked")
    private void cloneCollectionContent(Object cloneOwner, Field field, Collection<?> source,
                                        Collection<?> target, Descriptor descriptor) {
        if (source.isEmpty()) {
            return;
        }
        Collection<Object> tg = (Collection<Object>) target;
        for (Object obj : source) {
            if (obj == null) {
                tg.add(null);
                continue;
            }
            if (CloneBuilderImpl.isImmutable(obj.getClass())) {
                tg.addAll(source);
                break;
            }
            Object clone;
            if (builder.isTypeManaged(obj.getClass())) {
                clone = uow.registerExistingObject(obj, descriptor);
            } else {
                clone = builder.buildClone(cloneOwner, field, obj, descriptor);
            }
            tg.add(clone);
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    void mergeChanges(Field field, Object target, Object originalValue, Object cloneValue) {
        assert (originalValue == null || originalValue instanceof Collection);
        assert cloneValue instanceof Collection;

        Collection<Object> clone = (Collection<Object>) cloneValue;
        if (clone instanceof IndirectCollection) {
            clone = ((IndirectCollection<Collection<Object>>) clone).getReferencedCollection();
        }
        Collection<Object> orig = (Collection<Object>) createNewInstance(clone.getClass(), clone.size());
        if (orig == null) {
            orig = createDefaultCollection(clone.getClass());
        }
        EntityPropertiesUtils.setFieldValue(field, target, orig);

        if (clone.isEmpty()) {
            return;
        }
        for (Object cl : clone) {
            orig.add(uow.contains(cl) ? builder.getOriginal(cl) : cl);
        }
        final Types types = field.getAnnotation(Types.class);
        if (types != null) {
            checkForNewTypes(orig);
        }
    }

    private static Collection<Object> createDefaultCollection(Class<?> cls) {
        return CollectionFactory.createDefaultCollection(PluralAttribute.CollectionType.fromClass(cls));
    }

    /**
     * Checks if new types were added to the specified collection. </p>
     * <p>
     * If so, they are added to the module extraction signature managed by Metamodel.
     *
     * @param collection The collection to check
     * @see Types
     */
    private void checkForNewTypes(Collection<?> collection) {
        assert collection != null;
        if (collection.isEmpty()) {
            return;
        }
        final Set<URI> signature = builder.getMetamodel().getModuleExtractionExtraSignature();
        for (Object elem : collection) {
            final URI u = EntityPropertiesUtils.getValueAsURI(elem);
            if (!signature.contains(u)) {
                builder.getMetamodel().addUriToModuleExtractionSignature(u);
            }
        }
    }
}
