package com.projteam.competico.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListWordFillDTO implements TaskDTO
{
	private String instruction;
	private List<String> tags;
	private double difficulty;
	
	private List<WordFillElementDTO> rows;
}
