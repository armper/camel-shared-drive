package com.PereaTechnologies.CamelExchange.CamelExchangeMain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.ToString;

@Entity
@Table
@Data
@ToString
public class SearchFile {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	protected UUID id;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected SearchUser createdBy;
	
	protected LocalDateTime createdDateTime;
	
	protected String fileName, extension;
}
