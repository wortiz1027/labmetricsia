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
                .because("El Dominio debe ser el núcleo puro del negocio, 100% agnóstico a frameworks, " +
                        "especificaciones de bases de datos o tecnologías de infraestructura externa " +
                        "para garantizar la máxima portabilidad, testabilidad y evolución del laboratorio.");

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
                .because("Los componentes de la capa de aplicación que implementan la lógica de orquestación " +
                        "de los Casos de Uso (Puertos de Entrada) deben poseer el sufijo 'Service' para " +
                        "garantizar una consistencia semántica absoluta y una rápida identificación en el monorrepo.");

        ArchRule restRule = classes()
                .that()
                .resideInAPackage(String.format("%s%s", BASE_PACKAGE, ".infrastructure.adapter.input.http"))
                .and()
                .haveSimpleNameContaining("RestController")
                .should()
                .haveSimpleNameEndingWith("RestController")
                .because("Los adaptadores de entrada encargados de exponer los endpoints HTTP/REST del sistema " +
                        "deben seguir la convención tipográfica estricta terminando en 'RestController' " +
                        "para diferenciarse claramente de las fronteras GraphQL, gRPC o CLI del monorrepo.");

        ArchRule graphqlRule = classes()
                .that()
                .resideInAPackage(BASE_PACKAGE + ".infrastructure.adapter.input.graphql..")
                .and()
                .haveSimpleNameContaining("Controller")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .because("Los adaptadores de entrada de la infraestructura GraphQL encargados de resolver queries y mutations "+
                        "deben seguir la convención tipográfica terminando en 'Controller' para asegurar la consistencia " +
                        "con el framework Spring GraphQL y distinguirse claramente de los adaptadores RESTful HTTP.");

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
                .because("Los adaptadores de entrada (Fronteras de Red REST/GraphQL) tienen estrictamente " +
                        "prohibido omitir la capa de aplicación y comunicarse de forma directa con los almacenes " +
                        "de persistencia del dominio. Toda interacción exterior debe ser orquestada obligatoriamente " +
                        "a través de un Caso de Uso (Puerto de Entrada) para salvaguardar la gobernanza, la transaccionalidad " +
                        "y las auditorías cognitivas de seguridad de la solución.");

        flowRule.check(classes);
    }
}
