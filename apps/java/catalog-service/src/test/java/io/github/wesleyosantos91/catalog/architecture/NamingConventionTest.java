package io.github.wesleyosantos91.catalog.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Testes de arquitetura para validar convenções de nomenclatura e estrutura.
 */
@DisplayName("🏗️ Testes de Convenções de Nomenclatura")
public class NamingConventionTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("io.github.wesleyosantos91.catalog");

    @Test
    @DisplayName("Controllers devem estar no pacote 'api.v1.controller'")
    void controllers_should_be_in_controller_package() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(RestController.class)
            .or().areAnnotatedWith(Controller.class)
            .should().resideInAPackage("..api.v1.controller..")
            .as("Controllers should reside in api.v1.controller package");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers devem ter sufixo 'Controller'")
    void controllers_should_have_controller_suffix() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(RestController.class)
            .or().areAnnotatedWith(Controller.class)
            .should().haveSimpleNameEndingWith("Controller")
            .as("Controllers should have 'Controller' suffix");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Services devem estar no pacote 'domain.service'")
    void services_should_be_in_service_package() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Service.class)
            .should().resideInAPackage("..domain.service..")
            .as("Services should reside in domain.service package");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Services devem ter sufixo 'Service'")
    void services_should_have_service_suffix() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Service.class)
            .should().haveSimpleNameEndingWith("Service")
            .as("Services should have 'Service' suffix");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories devem estar no pacote 'domain.repository'")
    void repositories_should_be_in_repository_package() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Repository.class)
            .or().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..domain.repository..")
            .as("Repositories should reside in domain.repository package");
            
        rule.check(importedClasses);
    }
}