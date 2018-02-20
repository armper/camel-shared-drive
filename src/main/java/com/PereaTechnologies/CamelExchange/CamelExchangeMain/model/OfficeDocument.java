package com.PereaTechnologies.CamelExchange.CamelExchangeMain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class OfficeDocument extends SearchFile {

	protected String title, author, keywords, comments, createDateTime, lastSaveDateTime;

}
