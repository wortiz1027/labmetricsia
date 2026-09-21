package co.com.devsoft.devopsmind;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;;

@Tags({
        @Tag("archTest")
})
@DisplayName("📐 Pruebas de Arquitectura :: Control de Fronteras Hexagonales")
class HexagonalArchitectureATest {

    private static final String BASE_PACKAGE = "co.com.devsoft.devopsmind";

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

    @Test
    @DisplayName("📌 Regla de Oro: El Dominio debe ser 100% independiente de capas externas")
    void domainShouldBeIndependent() {
        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringOnlyDependenciesInAnyPackage(BASE_PACKAGE)
                .layer("Domain").definedBy(String.format("%s%s", BASE_PACKAGE, ".domain.."))
                .layer("Application").definedBy(String.format("%s%s", BASE_PACKAGE, ".application.."))
                .layer("Infrastructure").definedBy(String.format("%s%s", BASE_PACKAGE, ".infrastructure.."))
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

        architecture.check(classes);
    }

    @Test
    @DisplayName("🛡️ Regla de Pureza: El Dominio no debe estar contaminado por Spring o JPA")
    void domainShouldBeFrameworkFree() {
        ArchRule rule = classes().that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".domain.."))
                .should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("java..",
                        String.format("%s%s", BASE_PACKAGE, ".domain.."),
                        "org.slf4j..");

        rule.check(classes);
    }

    @Test
    @DisplayName("📌 Regla de Nomenclatura: Los servicios y controladores deben seguir la convención")
    void interfacesAndServicesShouldHaveCorrectSuffixes() {
        ArchRule serviceRule = classes()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".application.service.."))
                .should()
                .haveSimpleNameEndingWith("Service");

        ArchRule restRule = classes()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".infrastructure.adapter.input.http"))
                .and()
                .haveSimpleNameContaining("RestController")
                .should()
                .haveSimpleNameEndingWith("RestController");

        ArchRule graphqlRule = classes()
                .that()
                .resideInAPackage(BASE_PACKAGE + ".infrastructure.adapter.input.graphql..")
                .and()
                .haveSimpleNameContaining("Controller")
                .should()
                .haveSimpleNameEndingWith("Controller");

        serviceRule.check(classes);
        restRule.check(classes);
        graphqlRule.check(classes);
    }

    @Test
    @DisplayName("🛡️ Regla de Control de Flujo: Los controladores no deben puentear a los UseCases")
    void controllersShouldOnlyDependOnUseCasesNotOnStorage() {
        ArchRule flowRule = noClasses()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".infrastructure.adapter.input.."))
                .should()
                .dependOnClassesThat()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".domain.repository.."));

        flowRule.check(classes);
    }
}
