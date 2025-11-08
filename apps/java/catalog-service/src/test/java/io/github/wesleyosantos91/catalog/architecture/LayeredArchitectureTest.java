package io.github.wesleyosantos91.catalog.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("🏗️ Testes de Arquitetura em Camadas")
class LayeredArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("io.github.wesleyosantos91.catalog");

    @Test
    @DisplayName("Arquitetura em camadas deve ser respeitada (API → Domain → Infrastructure)")
    void layered_architecture_should_be_respected() {
        ArchRule rule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            
            .layer("API").definedBy("..api.v1.controller..", "..api.v1.request..", "..api.v1.response..", "..api.exception..")
            .layer("Domain").definedBy("..domain.service..", "..domain.model..", "..domain.exception..", "..domain.port..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Core").definedBy("..core.mapper..")

            .whereLayer("API").mayOnlyAccessLayers("Domain", "Core")
            .whereLayer("Domain").mayOnlyAccessLayers("Infrastructure", "Core", "API")
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Core", "Domain")
            .whereLayer("Core").mayOnlyAccessLayers("Domain", "API");
            
        rule.check(importedClasses);
    }
}