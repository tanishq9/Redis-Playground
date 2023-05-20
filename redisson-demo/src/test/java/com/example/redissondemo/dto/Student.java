package com.example.redissondemo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
// Default Codec is org.redisson.codec.MarshallingCodec	which expects Class to be Serializable
// Serializable is not required for JsonJacksonCodec
public class Student {

	private String name;
	private int age;
	private String city;
	private List<Integer> marks;
}
