package me.grudzien.patryk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@ToString
public class EmailDto {

	private String from;
	private String to;
	private String subject;
	private String content;
	private Map<String, Object> templatePlaceholders;
}
