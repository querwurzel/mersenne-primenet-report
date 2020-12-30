package org.mersenne.primenet.meta;

import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetcher;
import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.meta.MetaService.ImportMeta;
import org.mersenne.primenet.meta.MetaService.Meta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaGraphQl {

    @Bean
    public GraphQLQueryResolver metaResolver(MetaService metaService) {
        return new MetaQueryResolver(metaService);
    }

    @Bean
    public DataFetcher<String> userNameFetcher(PrimeNetProperties primeNetProperties) {
        return env -> primeNetProperties.getIdentity();
    }

    @Bean
    public DataFetcher<Long> totalUserResultsFetcher(MetaService metaService, PrimeNetProperties primeNetProperties) {
        return env -> metaService.countResultsByUserName(primeNetProperties.getIdentity());
    }

    @Bean
    public DataFetcher<Long> totalResultsFetcher(MetaService metaService) {
        return env -> metaService.countResults();
    }

    @Bean
    public DataFetcher<ImportMeta> importStatesFetcher(MetaService metaService) {
        final ImportMeta importMeta = new ImportMeta(metaService.countImportsPerState());
        return env -> importMeta;
    }

    /*
    @Bean
    public GraphQLSchema schema(DataFetcher<String> userNameFetcher, DataFetcher<Long> totalUserResultsFetcher, DataFetcher<Long> totalResultsFetcher) {

        @Value("meta.graphqls") ClassPathResource typeRegistry
        this.typeRegistry = new SchemaParser().parse(typeRegistry.getInputStream());
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("meta", env -> LocalDateTime.now().toString())
                )
                .type(TypeRuntimeWiring.newTypeWiring("Meta")
                        .dataFetcher("lastUpdated", env -> LocalDateTime.now().toString())
                )
                .type(TypeRuntimeWiring.newTypeWiring("User")
                        .dataFetcher("name", userNameFetcher)
                        .dataFetcher("total", totalUserResultsFetcher)
                )
                .type(TypeRuntimeWiring.newTypeWiring("Results")
                        .dataFetcher("total", totalResultsFetcher)
                )
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }
    */

    public class MetaQueryResolver implements GraphQLQueryResolver {

        private final MetaService metaService;

        public MetaQueryResolver(MetaService metaService) {
            this.metaService = metaService;
        }

        public Meta getMeta() {
            return this.metaService.getMeta();
        }
    };

}
