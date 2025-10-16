package io.github.wesleyosantos91.catalog.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Testes de arquitetura para validar uso correto de anotações Spring.
 */
@DisplayName("🏗️ Testes de Anotações Spring")
public class SpringAnnotationTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("io.github.wesleyosantos91.catalog");

    @Test
    @DisplayName("Services devem ser anotados com @Service")
    void services_should_be_annotated_with_service() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain.service..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class)
            .as("Service classes should be annotated with @Service");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers devem ser anotados com @RestController")
    void controllers_should_be_annotated_with_rest_controller() {
        ArchRule rule = classes()
            .that().resideInAPackage("..api.v1.controller..")
            .and().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(RestController.class)
            .as("Controller classes should be annotated with @RestController");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories devem ser anotados com @Repository")
    void repositories_should_be_annotated_with_repository() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain.repository..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .as("Repository classes should be annotated with @Repository");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Evitar uso genérico de @Component (usar @Service, @Repository ou @Controller)")
    void no_classes_should_use_generic_component_annotation() {
        ArchRule rule = classes()
            .that().resideInAPackage("io.github.wesleyosantos91.catalog..")
            .and().haveSimpleNameNotEndingWith("Filter") // Excluir filtros
            .and().haveSimpleNameNotEndingWith("Config") // Excluir configs
            .should().notBeAnnotatedWith(Component.class)
            .as("Classes should use specific Spring stereotypes (@Service, @Repository, @Controller) instead of generic @Component, except for filters and configurations");
            
        rule.check(importedClasses);
    }
}