package com.mccarl.es.plugin;

import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.features.NodeFeature;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.SearchPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.repositories.RepositoriesService;
import org.elasticsearch.rest.*;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;
import org.elasticsearch.xcontent.NamedXContentRegistry;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class PersonalizationPlugin extends Plugin implements ActionPlugin, AnalysisPlugin, SearchPlugin {

    private static UserSignalService signalService;

    public static UserSignalService getSignalService() {
        return signalService;
    }

    public String name() {
        return "personalization-plugin";
    }

    public String description() {
        return "Personalization Plugin for Elasticsearch";
    }

    @Override
    public List<QuerySpec<?>> getQueries() {
        return List.of(new QuerySpec<>(
                new ParseField(PersonalizationQueryBuilder.NAME),
                PersonalizationQueryBuilder::new,
                PersonalizationQueryBuilder::fromXContent
        ));
    }

    @Override
    public Collection<?> createComponents(PluginServices services) {
        signalService = new UserSignalService();

        services.threadPool().schedule(
                () -> signalService.updateSignals(services.client()),
                TimeValue.timeValueSeconds(10),
                services.threadPool().executor(ThreadPool.Names.GENERIC)
        );

        return List.of();
    }

    @Override
    public Collection<RestHandler> getRestHandlers(Settings settings,
                                                   NamedWriteableRegistry namedWriteableRegistry,
                                                   RestController restController,
                                                   ClusterSettings clusterSettings,
                                                   IndexScopedSettings indexScopedSettings,
                                                   SettingsFilter settingsFilter,
                                                   IndexNameExpressionResolver indexNameExpressionResolver,
                                                   Supplier<DiscoveryNodes> nodesInCluster,
                                                   Predicate<NodeFeature> clusterSupportsFeature) {

        return List.of(new BaseRestHandler() {
            @Override
            public List<Route> routes() {
                return List.of(new Route(RestRequest.Method.GET, "/_personalization/status"));
            }

            @Override
            public String getName() {
                return "personalization_status";
            }

            @Override
            protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
                return channel -> {
                    try {
                        XContentBuilder builder = channel.newBuilder();
                        builder.startObject();
                        builder.field("plugin", "personalization-plugin");
                        builder.field("status", "active");
                        builder.field("query_name", PersonalizationQueryBuilder.NAME);
                        builder.field("queries_registered", getQueries().size());
                        builder.endObject();

                        channel.sendResponse(new RestResponse(RestStatus.OK, builder));
                    } catch (Exception e) {
                        channel.sendResponse(new RestResponse(channel, e));
                    }
                };
            }
        });
    }

}