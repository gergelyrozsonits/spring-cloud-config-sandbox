package org.rozsonitsg.cloudconfigserver;

import org.rozsonitsg.cloudconfigserver.ConfigurationEntity.ConfigurationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ConfigurationRepository extends Repository<ConfigurationEntity, ConfigurationId> {
	ConfigurationEntity save(ConfigurationEntity entity);
	Page<ConfigurationEntity> findByValueLike(String value, Pageable pageable);
}
