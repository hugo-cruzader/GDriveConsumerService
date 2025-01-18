package com.hector.gdriveconsumerservice.demo.entity;

import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record DownloadableResource(Resource content, String fileName) { }
