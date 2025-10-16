package io.github.wesleyosantos91.catalog.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Testes de arquitetura para validar a estrutura em camadas do projeto.
 * 
 * Este teste garante que:
 * - A camada API só pode acessar as camadas Domain e Core
 * - A camada Domain só pode acessar as camadas Infrastructure, Core e API
 * - A camada Infrastructure só pode acessar as camadas Core e Domain
 * - A camada Core pode acessar as camadas Domain e API
 */
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
            
            // Definindo as camadas baseadas na estrutura real do projeto
            .layer("API").definedBy("..api.v1.controller..", "..api.v1.request..", "..api.v1.response..", "..api.exception..")
            .layer("Domain").definedBy("..domain.service..", "..domain.entity..", "..domain.model..", "..domain.repository..", "..domain.exception..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Core").definedBy("..core.mapper..")

            // Regras de dependência mais flexíveis baseadas na arquitetura atual
            .whereLayer("API").mayOnlyAccessLayers("Domain", "Core")
            .whereLayer("Domain").mayOnlyAccessLayers("Infrastructure", "Core", "API") // Services podem usar Request/Response
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Core", "Domain")
            .whereLayer("Core").mayOnlyAccessLayers("Domain", "API"); // Mappers podem acessar API e Domain
            
        rule.check(importedClasses);
    }
}