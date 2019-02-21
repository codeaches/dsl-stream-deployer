package com.codeaches.dsl.stream.deployer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.core.ApplicationType;
import org.springframework.cloud.dataflow.rest.client.DataFlowOperations;
import org.springframework.cloud.dataflow.rest.client.dsl.Stream;
import org.springframework.cloud.dataflow.rest.resource.AppRegistrationResource;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedResources;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {

		return args -> {

			registerHttpSource();
			registerLogSink();
			createAndDeployStream();
		};
	}

	@Autowired
	DataFlowOperations dataFlowOperations;

	void registerHttpSource() {

		dataFlowOperations.appRegistryOperations().register("myHttpSource", ApplicationType.source,
				"maven://org.springframework.cloud.stream.app:http-source-rabbit:2.1.0.RELEASE", null, true);
	}

	void registerLogSink() {

		dataFlowOperations.appRegistryOperations().register("myLogSink", ApplicationType.sink,
				"maven://org.springframework.cloud.stream.app:log-sink-rabbit:2.1.0.RELEASE", null, true);
	}

	void createAndDeployStream() {

		Stream.builder(dataFlowOperations).name("myStreamApp").definition("myHttpSource | myLogSink").create().deploy();		
	}

	void unregisterAllApps() {

		PagedResources<AppRegistrationResource> registeredResources = dataFlowOperations.appRegistryOperations().list();
		registeredResources.forEach(k -> {
			dataFlowOperations.appRegistryOperations().unregister(k.getName(), ApplicationType.valueOf(k.getType()));
		});
	}
}
