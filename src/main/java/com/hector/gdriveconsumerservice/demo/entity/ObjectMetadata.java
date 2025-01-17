package com.hector.gdriveconsumerservice.demo.entity;

import com.google.api.client.util.DateTime;
import lombok.Builder;


@Builder
public record ObjectMetadata(String id, String name, String type, DateTime lastModifiedDate, String uri) { }
