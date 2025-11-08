package io.github.wesleyosantos91.catalog.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("🏗️ Testes de Dependências da Arquitetura")
class DependencyTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("io.github.wesleyosantos91.catalog");

    @Test
    @DisplayName("Camada de domínio não deve depender da camada de API")
    void domain_should_not_depend_on_api() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..api..")
            .as("Domain layer should not depend on API layer");
            
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Entidades de banco não devem depender do domínio")
    void persistence_entities_should_not_depend_on_domain() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..infrastructure.database.entity..")
            .should().dependOnClassesThat().resideInAPackage("..domain..")
            .as("Database entities should remain isolated from domain layer");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Infraestrutura não deve depender de controllers da API")
    void no_cycles_should_exist() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..infrastructure..")
            .should().dependOnClassesThat().resideInAPackage("..api.v1.controller..")
            .as("Infrastructure should not depend on API controllers");
            
        rule.check(importedClasses);
    }
}