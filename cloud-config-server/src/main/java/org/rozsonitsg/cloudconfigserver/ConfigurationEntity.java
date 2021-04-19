package org.rozsonitsg.cloudconfigserver;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@Table(name = "PROPERTIES")
@EntityListeners(AuditingEntityListener.class)
public class ConfigurationEntity {
	@Data
	@Embeddable
	@SuppressWarnings("serial")
	public static class ConfigurationId implements Serializable {
		@NotBlank
		@Column(name = "APPLICATION")
		private String application;
		@NotBlank
		@Column(name = "PROFILE")
		private String profile;
		@NotBlank
		@Column(name = "LABEL")
		private String label;
		@NotBlank
		@Column(name = "KEY")
		private String key;
	}
	@Valid
	@EmbeddedId
	private ConfigurationId id;
	@NotBlank
	@Column(name = "VALUE")
	private String value;
	@CreatedBy
	@Column(name = "CREATE_BY")
	private String createBy;
	@CreatedDate
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	@LastModifiedBy
	@Column(name = "LAST_MODIFIED_BY")
	private String lastModifiedBy;
	@LastModifiedDate
	@Column(name = "LAST_MODIFIED_DATE")
	private LocalDateTime lastModifiedDate;
}
