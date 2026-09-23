package co.com.devsoft.devopsmind;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.DisplayName;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTags;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;;

@ArchTags({
        @ArchTag("archTest")
})
@AnalyzeClasses(packages = "co.com.devsoft.devopsmind", importOptions = { ImportOption.DoNotIncludeTests.class })
@DisplayName("📐 Pruebas de Arquitectura :: Control de Fronteras Hexagonales")
class HexagonalArchitectureATest {

    private static final String BASE_PACKAGE = "co.com.devsoft.devopsmind";

    @ArchTest
    @DisplayName("📌 Regla de Oro: El Dominio debe ser 100% independiente de capas externas")
    public static final void domainShouldBeIndependent(JavaClasses classes) {
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

    @ArchTest
    @DisplayName("🛡️ Regla de Pureza: El Dominio no debe estar contaminado por Spring o JPA")
    void domainShouldBeFrameworkFree(JavaClasses classes) {
        ArchRule rule = classes().that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".domain.."))
                .should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("java..",
                        String.format("%s%s", BASE_PACKAGE, ".domain.."),
                        "org.slf4j..")
                .because(""); // TODO: llenar descripcion

        rule.check(classes);
    }

    @ArchTest
    @DisplayName("📌 Regla de Nomenclatura: Los servicios y controladores deben seguir la convención")
    void interfacesAndServicesShouldHaveCorrectSuffixes(JavaClasses classes) {
        ArchRule serviceRule = classes()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".application.service.."))
                .should()
                .haveSimpleNameEndingWith("Service")
                .because(""); // TODO: llenar descripcion

        ArchRule restRule = classes()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".infrastructure.adapter.input.http"))
                .and()
                .haveSimpleNameContaining("RestController")
                .should()
                .haveSimpleNameEndingWith("RestController")
                .because(""); // TODO: llenar descripcion

        ArchRule graphqlRule = classes()
                .that()
                .resideInAPackage(BASE_PACKAGE + ".infrastructure.adapter.input.graphql..")
                .and()
                .haveSimpleNameContaining("Controller")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .because(""); // TODO: llenar descripcion

        serviceRule.check(classes);
        restRule.check(classes);
        graphqlRule.check(classes);
    }

    @ArchTest
    @DisplayName("🛡️ Regla de Control de Flujo: Los controladores no deben puentear a los UseCases")
    void controllersShouldOnlyDependOnUseCasesNotOnStorage(JavaClasses classes) {
        ArchRule flowRule = noClasses()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".infrastructure.adapter.input.."))
                .should()
                .dependOnClassesThat()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".domain.repository.."))
                .because(""); // TODO: llenar descripcion

        flowRule.check(classes);
    }
}
