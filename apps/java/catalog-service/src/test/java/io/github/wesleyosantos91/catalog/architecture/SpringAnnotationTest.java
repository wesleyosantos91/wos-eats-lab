package io.github.wesleyosantos91.catalog.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@DisplayName("🏗️ Testes de Anotações Spring")
class SpringAnnotationTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("io.github.wesleyosantos91.catalog");

    @Test
    @DisplayName("Services devem ser anotados com @Adapter")
    void services_should_be_annotated_with_service() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain.service..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Adapter.class)
            .as("Service classes should be annotated with @Adapter");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Adapters devem ser anotados com @Adapter")
    void adapters_should_be_annotated_with_service() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure..adapter..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().beAnnotatedWith(Adapter.class)
                .as("Service classes should be annotated with @Adapter");

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
            .that().resideInAPackage("..infrastructure.database.repository..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().beAnnotatedWith(Repository.class)
            .as("Repository classes should be annotated with @Repository");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Evitar uso genérico de @Component (usar @Adapter, @Repository ou @Controller)")
    void no_classes_should_use_generic_component_annotation() {
        ArchRule rule = classes()
            .that().resideInAPackage("io.github.wesleyosantos91.catalog..")
            .and().haveSimpleNameNotEndingWith("Filter")
            .and().haveSimpleNameNotEndingWith("Config")
            .and().resideOutsideOfPackage("..core.annotation..")
            .should().notBeAnnotatedWith(Component.class)
            .as("Classes should use specific Spring stereotypes (@Adapter, @Repository, @Controller) instead of generic @Component, except for filters and configurations");
            
        rule.check(importedClasses);
    }
}