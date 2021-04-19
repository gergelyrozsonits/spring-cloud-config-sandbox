package org.rozsonitsg.cloudconfigserver;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Configuration
@EnableJpaAuditing
public class SpringDataConfiguration {
	@Component
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {
		@Override
		public Optional<String> getCurrentAuditor() {
			return Optional
				.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.map(auth -> auth.getName());
		}
    }
}
