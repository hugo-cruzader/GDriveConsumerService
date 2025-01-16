package com.hector.gdriveconsumerservice.demo.entity;

import lombok.Builder;

import java.util.Date;

@Builder
public record ObjectMetadata(String name, String type, Date lastModifiedDate) { }
