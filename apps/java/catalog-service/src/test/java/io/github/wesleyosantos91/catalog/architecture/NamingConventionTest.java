package io.github.wesleyosantos91.catalog.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

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
    @DisplayName("INBOUND: classes em '..domain.service..' DEVEM usar @Adapter(type=INBOUND)")
    void inbound_must_be_in_service_and_use_inbound_adapter() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.service..")                // <-- escopo correto (somente INBOUND)
                .should().beAnnotatedWith(Adapter.class)
                .andShould(haveAdapterType(Adapter.AdapterType.INBOUND))
                .as("INBOUND residem em '..domain.service..' e usam @Adapter(type=INBOUND)");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("OUTBOUND: classes em '..infrastructure..adapter..' DEVEM usar @Adapter(type=OUTBOUND)")
    void outbound_must_be_in_infra_adapter_and_use_outbound_adapter() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure..adapter..")       // <-- escopo correto (somente OUTBOUND)
                .should().beAnnotatedWith(Adapter.class)
                .andShould(haveAdapterType(Adapter.AdapterType.OUTBOUND))
                .as("OUTBOUND residem em '..infrastructure..adapter..' e usam @Adapter(type=OUTBOUND)");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("INBOUND: Services em '..domain.service..' devem ter sufixo 'Service' e @Adapter(INBOUND)")
    void inbound_services_should_be_in_service_package_and_have_service_suffix() {
        var rule = classes()
                .that().resideInAPackage("..domain.service..")
                .and().areAnnotatedWith(Adapter.class)
                .should().haveSimpleNameEndingWith("Service")
                .andShould(haveAdapterType(Adapter.AdapterType.INBOUND))
                .as("INBOUND devem ficar em '..domain.service..', terminar com 'Service' e usar @Adapter(INBOUND)");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("OUTBOUND: Adapters em '..infrastructure..adapter..' devem ter sufixo 'Adapter' e @Adapter(OUTBOUND)")
    void outbound_adapters_should_be_in_adapter_package_and_have_adapter_suffix() {
        var rule = classes()
                .that().resideInAPackage("..infrastructure..adapter..")
                .and().areAnnotatedWith(Adapter.class)
                .should().haveSimpleNameEndingWith("Adapter")
                .andShould(haveAdapterType(Adapter.AdapterType.OUTBOUND))
                .as("OUTBOUND devem ficar em '..infrastructure..adapter..', terminar com 'Adapter' e usar @Adapter(OUTBOUND)");

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

    private static ArchCondition<JavaClass> haveAdapterType(Adapter.AdapterType expected) {
        return new ArchCondition<>("ter @Adapter(type=" + expected + ")") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Adapter ann = item.reflect().getAnnotation(Adapter.class);
                boolean ok = ann != null && ann.type() == expected;
                if (!ok) {
                    events.add(SimpleConditionEvent.violated(
                            item, "Esperado @Adapter(type=" + expected + ") em " + item.getName()
                    ));
                }
            }
        };
    }

}