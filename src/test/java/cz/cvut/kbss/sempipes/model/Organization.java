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
package cz.cvut.kbss.sempipes.model;

import cz.cvut.kbss.jopa.CommonVocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.sempipes.TestVocabulary;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = TestVocabulary.ORGANIZATION)
public class Organization {

    private static final String DEFAULT_COUNTRY = "Czech Republic";

    @Id
    private URI uri;

    @OWLAnnotationProperty(iri = TestVocabulary.DATE_CREATED)
    private Date dateCreated;

    @OWLAnnotationProperty(iri = CommonVocabulary.RDFS_LABEL)
    private String name;

    @OWLDataProperty(iri = TestVocabulary.BRAND)
    private Set<String> brands;

    private Long age;

    @OWLObjectProperty(iri = TestVocabulary.HAS_ADMIN)
    private Set<Employee> admins;

    @OWLObjectProperty(iri = TestVocabulary.HAS_MEMBER)
    private Set<Employee> employees;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getBrands() {
        return brands;
    }

    public void setBrands(Set<String> brands) {
        this.brands = brands;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Set<Employee> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<Employee> admins) {
        this.admins = admins;
    }

    public void addAdmin(Employee admin) {
        Objects.requireNonNull(admin);
        if (admins == null) {
            this.admins = new HashSet<>();
        }
        admins.add(admin);
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee);
        if (employees == null) {
            this.employees = new HashSet<>();
        }
        employees.add(employee);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "uri=" + uri +
                ", dateCreated=" + dateCreated +
                ", name='" + name + '\'' +
                ", brands=" + brands +
                ", age=" + age +
                ", employees=" + employees +
                '}';
    }
}
