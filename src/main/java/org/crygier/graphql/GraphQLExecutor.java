package org.crygier.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.function.BiConsumer;

public class GraphQLExecutor {

    @Resource
    private EntityManager entityManager;
    private GraphQL graphQL;
    private BiConsumer<GraphQLSchema.Builder, EntityManager> schemaEnhancer;

    protected GraphQLExecutor() {}
    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        createGraphQL();
    }

    public GraphQLExecutor(EntityManager entityManager, BiConsumer<GraphQLSchema.Builder, EntityManager> schemaEnhancer) {
        this.entityManager = entityManager;
        this.schemaEnhancer = schemaEnhancer;

        createGraphQL();
    }

    @PostConstruct
    protected void createGraphQL() {
        if (entityManager != null)
            this.graphQL = new GraphQL(new GraphQLSchemaBuilder(entityManager, schemaEnhancer).getGraphQLSchema());
    }

    @Transactional
    public ExecutionResult execute(String query) {
        final ExecutionResult execute = graphQL.execute(query);
        return execute;
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        if (arguments == null)
            return graphQL.execute(query);
        else
            return graphQL.execute(query, (Object) null, arguments);
    }

}
