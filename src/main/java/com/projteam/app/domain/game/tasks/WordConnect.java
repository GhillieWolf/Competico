package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.domain.game.tasks.answers.WordConnectAnswer;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import com.projteam.app.dto.game.tasks.WordConnectDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class WordConnect implements Task
{
	private @Id UUID id;
	private @ElementCollection @OrderColumn List<String> leftWords;
	private @ElementCollection @OrderColumn List<String> rightWords;
	private @ElementCollection Map<Integer, Integer> correctMapping;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof WordConnectAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		Map<Integer, Integer> answerMapping = ((WordConnectAnswer) answer).getAnswerMapping();
		
		if (answerMapping == null)
			return 0;
		
		long score = 0;
		
		for (Entry<Integer, Integer> e: correctMapping.entrySet())
		{
			if (answerMapping.get(e.getKey()) == e.getValue())
				score++;
		}
		
		return ((double) score) / correctMapping.size();
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return WordConnectAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("WordConnect", taskNumber,
				new WordConnectDTO(this));
	}
}