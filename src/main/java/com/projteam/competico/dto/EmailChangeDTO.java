package com.projteam.competico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeDTO
{
	private String email;
	private CharSequence password;
}