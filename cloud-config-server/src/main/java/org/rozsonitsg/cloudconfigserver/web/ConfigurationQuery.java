package org.rozsonitsg.cloudconfigserver.web;

import lombok.Data;

@Data
public class ConfigurationQuery {
	private String pattern = "%%";
	private Integer pageNumber = 0;
	private Integer pageSize = 10;
}