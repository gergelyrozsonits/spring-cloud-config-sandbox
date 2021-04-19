package org.rozsonitsg.cloudconfigserver.web;

import javax.validation.Valid;

import org.rozsonitsg.cloudconfigserver.ConfigurationEntity;
import org.rozsonitsg.cloudconfigserver.ConfigurationRepository;
import org.springframework.cloud.bus.endpoint.RefreshBusEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

// Please note: the request mapping for this controller must be aligned with Cloud Config Server's mapping
// therefore there is no proper way to have more then just one path fragments otherwise Cloud Config
// handlers would process the request
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
public final class ConfigurationRestController {
	private final ConfigurationRepository configurationRepository;
	private final RefreshBusEndpoint refreshBusEndpoint;

	@PostMapping()
	public Mono<ResponseEntity<ConfigurationEntity>> post(@Valid @RequestBody ConfigurationEntity configurationEntity) {
		return Mono.just(configurationEntity)
			// TODO: wrap blocking IO properly...
			.map(configurationRepository::save)
			.doOnNext(configuration -> {
				log.info("Dispatching notification on Cloud Bus");
		        // Produces "Caused by: java.lang.IllegalArgumentException: Unknown type for contentType header value: class [B"
		        // Fixed by Horsham.SR2
		        // Reference https://github.com/spring-cloud/spring-cloud-stream-binder-kafka/issues/863
		        refreshBusEndpoint.busRefresh();
			})
			.map(ResponseEntity::ok);
	}
	
	@GetMapping()
	public Mono<Page<ConfigurationEntity>> get(@ModelAttribute ConfigurationQuery query) {
		return Mono.just(query)
			.map(q -> configurationRepository.findByValueLike(
					q.getPattern(),
					PageRequest.of(q.getPageNumber(), q.getPageSize())));
	}
}